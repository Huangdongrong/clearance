/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.controller.customs;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.BusinessException;
import static com.ruoyi.common.utils.DateUtil.addDays;
import static com.ruoyi.common.utils.DateUtils.getMonthFirstDay;
import com.ruoyi.common.utils.JsonUtil;
import static com.ruoyi.common.utils.ServletUtils.compAbsoluteUrl;
import static com.ruoyi.framework.util.ShiroUtils.getSysUser;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import static com.ruoyi.yz.cnst.Const.DEFAULT_INVALID_KDT_ID;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.service.YouzanOrderService;
import com.ruoyi.yz.service.YouzanKdtService;
import java.util.HashSet;
import java.util.List;
import static java.util.Objects.nonNull;
import java.util.Set;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import static org.thymeleaf.util.SetUtils.contains;
import static com.ruoyi.system.enums.DataScope.ALL;
import static com.ruoyi.system.enums.DataScope.DEFINED;
import static com.ruoyi.system.enums.YouzanMenu.YOUZAN_MENU_LIST;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.domain.MvStockCusEntity;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_APPLYING;
import com.ruoyi.yz.service.MvGoodsService;
import com.ruoyi.yz.service.MvStockCusService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MapUtils;
import static org.apache.commons.collections4.MapUtils.getString;
import org.apache.commons.lang.time.DateUtils;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author wmao
 */
@Controller
@RequestMapping("/customs/youzan")
public class CustomsYouzanController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomsYouzanController.class);

    public static final String YOUZAN_PREFIX = "customs/youzan";

    @Autowired
    private YouzanOrderService youzanOrderService;

    @Autowired
    private MvStockCusService mvStockCusService;

    @Autowired
    private MvGoodsService mvGoodsService;

    @Autowired
    private YouzanKdtService youzanKdtService;

    @Autowired
    private ISysMenuService sysMenuService;

    /**
     * 跳转人工推单页
     *
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @GetMapping("/manual")
    public String manual(ModelMap mmap, @RequestParam(value = "orderNo", required = false) String orderNo) {
        orderNo = trimToEmpty(orderNo);
        if (isNotBlank(orderNo)) {
            mmap.put("searchValue", orderNo);
        }
        return YOUZAN_PREFIX + "/manual";
    }

    /**
     * 重新推送订单清关
     *
     * @param order
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping(value = "/retry")
    @ResponseBody
    public AjaxResult retry(@RequestParam Map<String, String> order) {
        AjaxResult ret = error();
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && MapUtils.isNotEmpty(order)) {
            YouzanOrder oldOrder = youzanOrderService.getOne(getString(order, "id"));
            if (nonNull(oldOrder)) {
                String body = getString(order, "body");
                if (isNotBlank(body)) {
                    oldOrder.setBody((CEB311Message) JsonUtil.fromJson(body, CEB311Message.class));
                }
                oldOrder.setWayBillNo(getString(order, "wayBillNo"));
                oldOrder.setWayBillEnt(getString(order, "wayBillEnt"));
                oldOrder.setAlreadySyncDetails(false);
                oldOrder.setAlreadySyncOrder(false);
                oldOrder.setAlreadySyncPay(false);
                oldOrder.setAlreadySyncWuliu(false);
                oldOrder.setSyncDetailsStatus(null);
                oldOrder.setSyncOrderStatus(null);
                oldOrder.setSyncPayStatus(null);
                oldOrder.setSyncWuliuStatus(null);
                oldOrder.setStatus(STATUS_APPLYING.name());
                oldOrder.setStatusMessage(STATUS_APPLYING.getValue());
                oldOrder.setUpdateTime(Calendar.getInstance().getTime());
                oldOrder.setUpdateBy(sysUser.getLoginName());
                int retCode = youzanOrderService.update(oldOrder);
                if (retCode > 0) {
                    ret = success("更新完成，请耐心等待重新发起清关");
                } else {
                    ret = error("无法获取信息");
                }
            } else {
                ret = error("无法获取信息");
            }
        }
        return ret;
    }

    /**
     * 查询某个订单推单情况
     *
     * @param searchValue
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping("/query")
    @ResponseBody
    public AjaxResult query(String searchValue) {
        AjaxResult ret = error();
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(searchValue)) {
            YouzanOrder cr = youzanOrderService.getOneByOrderNo(searchValue);
            if (nonNull(cr)) {
                ret = success(cr);
            } else {
                ret = error("无法获取信息");
            }
        }
        return ret;
    }

    /**
     * 跳转订单列表页
     *
     * @param mmap
     * @return
     */
    @RequiresPermissions("customs:youzan:view")
    @GetMapping("/order")
    public String view(ModelMap mmap) {
        YouzanOrder order = new YouzanOrder();
        Date currentTime = Calendar.getInstance().getTime();
        order.setCreateTime(DateUtils.addDays(currentTime, -1));
        order.setUpdateTime(currentTime);
        mmap.put("order", order);
        return YOUZAN_PREFIX + "/order";
    }

    /**
     * 跳转仓储列表页
     *
     * @return
     */
    @RequiresPermissions("customs:youzan:view")
    @GetMapping("/stock")
    public String stock() {
        return YOUZAN_PREFIX + "/stock";
    }

    /**
     * 跳转拉取订单页
     *
     * @param mmap
     * @return
     */
    @RequiresPermissions("customs:youzan:execute")
    @GetMapping("/pull")
    public String pull(ModelMap mmap) {
        Date currentTime = Calendar.getInstance().getTime();
        YouzanOrder order = new YouzanOrder();
        order.setCreateTime(addDays(currentTime, -1));
        order.setUpdateTime(currentTime);
        mmap.put("order", order);
        return YOUZAN_PREFIX + "/pull";
    }

    /**
     * 查询用户库存
     *
     * @param entity
     * @return
     */
    @RequiresPermissions("customs:youzan:view")
    @PostMapping("/queryGoods")
    @ResponseBody
    public TableDataInfo queryGoods(MvStockCusEntity entity) {
        TableDataInfo info = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser)) {
            YouzanKdt kdt = youzanKdtService.getKdtById(sysUser.getKdtId());
            if (nonNull(kdt)) {
                String cusCode = mvGoodsService.getCusCodeByAuthId(kdt.getAuthorityId());
                if (isNotBlank(cusCode)) {
                    entity.setCusCode(cusCode);
                    startPage();
                    info = getDataTable(mvStockCusService.getList(entity));
                } else {
                    info = getDataTable(new ArrayList<>());
                    LOG.error("cus code is null, pls check it");
                }
            } else {
                info = getDataTable(new ArrayList<>());
                LOG.error("kdt is null, pls check it");
            }
        } else {
            LOG.error("sysUser is null, it's weired!");
        }
        return info;
    }

    /**
     * 更新店铺订单摘要
     *
     * @param kdts
     * @return
     */
    private List<YouzanKdt> updateOrderSummary(final List<YouzanKdt> kdts, final Date startTime, final Date endTime) {
        if (isNotEmpty(kdts) && nonNull(startTime) && nonNull(endTime)) {
            List<String> ids = (List<String>) CollectionUtils.collect(kdts, (Object obj) -> {
                YouzanKdt kt = (YouzanKdt) obj;
                return nonNull(kt) ? kt.getAuthorityId() : null;
            });
            ids.removeAll(Collections.singleton(null));
            List<Map<String, Object>> orders = youzanOrderService.getOrdersOfKdts(ids, null, startTime, endTime);
            if (isNotEmpty(orders)) {
                for (YouzanKdt cdt : kdts) {
                    if (nonNull(cdt)) {
                        Map<String, Object> order = IteratorUtils.find(orders.iterator(), (Map<String, Object> map) -> {
                            return MapUtils.isNotEmpty(map) ? equalsIgnoreCase(getString(map, "kdt_id"), cdt.getAuthorityId()) : false;
                        });
                        if (MapUtils.isNotEmpty(order)) {
                            order.remove("kdt_id");
                        }
                        cdt.setParams(order);
                    }
                }
            }
        }
        return kdts;
    }

    /**
     * 跳转查询所有店铺页面，
     *
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @GetMapping("/kdt")
    public String queryAllKdts() {
        return YOUZAN_PREFIX + "/kdt";
    }

    /**
     * 跳转查询店铺订单页面，
     *
     * @param mmap
     * @param authId
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @GetMapping("/kdt/orders")
    public String queryAllOrdersOfKdt(ModelMap mmap, @RequestParam(value = "authId", required = false) String authId) {
        YouzanOrder order = new YouzanOrder();
        String kdtId = trimToEmpty(authId);
        order.setKdtId(isNotBlank(kdtId) ? kdtId : DEFAULT_INVALID_KDT_ID);
        Date currentTime = Calendar.getInstance().getTime();
        order.setCreateTime(getMonthFirstDay(currentTime));
        order.setUpdateTime(currentTime);
        mmap.put("order", order);
        mmap.put("kdtList", youzanKdtService.getList(null));
        return YOUZAN_PREFIX + "/kdtods";
    }

    /**
     * 管理员查询所有平台店铺
     *
     * @param entity
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping("/kdt/query")
    @ResponseBody
    public TableDataInfo queryAllKdts(YouzanKdt entity) {
        TableDataInfo info = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && nonNull(entity)) {
            if (isBlank(sysUser.getKdtId())) {
                startPage();
                info = getDataTable(youzanKdtService.getList(entity));
                Date currentTime = Calendar.getInstance().getTime();
                List<YouzanKdt> kdts = updateOrderSummary((List<YouzanKdt>) info.getRows(), getMonthFirstDay(currentTime), currentTime);
                if (isNotEmpty(kdts)) {
                    info.setRows(kdts);
                }
            } else {
                LOG.error("this is not admin account, pls check it!");
            }
        }
        return info;
    }

    /**
     * 管理员查询所有平台店铺
     *
     * @param order
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping("/kdt/orders/query")
    @ResponseBody
    public TableDataInfo queryOrdersOfKdt(YouzanOrder order) {
        TableDataInfo info = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser)
                && nonNull(order)
                && isNotBlank(order.getKdtId())
                && !equalsIgnoreCase(order.getKdtId(), DEFAULT_INVALID_KDT_ID)) {
            startPage();
            info = list(order);
        }
        return info;
    }

    /**
     * 根据kdt id 查询 kdt信息
     *
     * @param authId
     * @return
     */
    @RequiresPermissions("customs:youzan:view")
    @PostMapping("/queryKdt/{id}")
    @ResponseBody
    public AjaxResult queryKdt(@PathVariable("id") String authId) {
        AjaxResult ret = error();
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser)
                && isNotBlank(authId)) {
            YouzanKdt kdt = youzanKdtService.getOneByAuthId(authId);
            if (nonNull(kdt) && equalsIgnoreCase(sysUser.getKdtId(), kdt.getId())) {
                ret = success((Object) (kdt.getAuthorityName() + "[" + kdt.getAuthorityId()) + "]");
            } else {
                LOG.error("can't find right kdt:{}", kdt);
            }
        } else {
            LOG.error("sys user is null or kdtId is empty, it's weired, kdt:{}", authId);
        }
        return ret;
    }

    /**
     * 根据订单信息查询订单
     *
     * @param order
     * @return
     */
    @RequiresPermissions("customs:youzan:view")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(YouzanOrder order) {
        TableDataInfo info = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser)) {
            startPage();
            /**
             * 根据用户查询订单列表
             */
            if (nonNull(order)) {
                if (isBlank(order.getKdtId())) {
                    YouzanKdt yk = youzanKdtService.getKdtById(sysUser.getKdtId());
                    order.setKdtId(nonNull(yk) ? yk.getAuthorityId() : "");
                } else {
                    List<SysRole> roles = sysUser.getRoles();
                    if (isNotEmpty(roles)) {
                        Set<String> dataScopes = new HashSet<>();
                        roles.stream().filter((role) -> (nonNull(role))).forEachOrdered((role) -> {
                            dataScopes.add(role.getDataScope());
                        });
                        if (!contains(dataScopes, ALL.getKey())
                                && !contains(dataScopes, DEFINED.getKey())) {
                            LOG.error("r u tried to hack our system with invalid roles? {}", dataScopes);
                            order.setKdtId(DEFAULT_INVALID_KDT_ID);
                        }
                    } else {
                        LOG.error("r u tried to hack our system with no roles?");
                        order.setKdtId(DEFAULT_INVALID_KDT_ID);
                    }
                }
                info = getDataTable(youzanOrderService.getList(order));
            } else {
                LOG.error("input param order is empty, pls check it!");
            }
        } else {
            LOG.error("can't find right user, pls ckeck it!");
        }
        return info;
    }

    /**
     * 订单清关
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:execute")
    @PostMapping("/execute/{ids}")
    @ResponseBody
    public AjaxResult execute(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanOrderService.execute(split(ids, ','), sysUser.getKdtId());
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to send execute signal:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }

    /**
     * 手工拉取订单
     *
     * @param order
     * @param request
     * @return
     */
    @RequiresPermissions("customs:youzan:execute")
    @PostMapping("/pull")
    @ResponseBody
    public AjaxResult pull(YouzanOrder order, HttpServletRequest request) {
        LOG.info("order:{}", order);
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && nonNull(order)) {
            YouzanKdt kdt = youzanKdtService.getKdtById(sysUser.getKdtId());
            if (nonNull(kdt)) {
                if (isBlank(order.getKdtId())) {
                    order.setKdtId(kdt.getAuthorityId());
                }
                int pulledNum = youzanOrderService.pullOrders(order, kdt);
                if (pulledNum > 0) {
                    Map<String, Object> data = new HashMap<>();
                    List<SysMenu> menus = sysMenuService.selectMenuList(new SysMenu(YOUZAN_MENU_LIST.getName(), YOUZAN_MENU_LIST.getVisiable()));
                    if (isNotEmpty(menus)) {
                        SysMenu sysMenu = menus.get(0);
                        if (nonNull(sysMenu)) {
                            data.put("title", sysMenu.getMenuName());
                            data.put("url", compAbsoluteUrl(request, sysMenu.getUrl()));
                        }
                    }
                    result = success(data);
                } else {
                    result = success("您在这个时间范围内没有新订单，请确认后再拉取");
                }
            } else {
                result = error("你不是有赞用户！");
            }
        } else {
            result = error("没有输入起止时间");
        }
        return result;
    }

    /**
     * 删除订单
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:remove")
    @PostMapping("/remove/{ids}")
    @ResponseBody
    public AjaxResult remove(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanOrderService.remove(split(ids, ','), sysUser.getKdtId());
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to remove orders:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }

    /**
     * 删除商铺
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping("/kdt/remove/{ids}")
    @ResponseBody
    public AjaxResult removeKdt(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanKdtService.remove(split(ids, ','));
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to remove orders:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }

    /**
     * 恢复商铺
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:admin")
    @PostMapping("/kdt/recovery/{ids}")
    @ResponseBody
    public AjaxResult recoveryKdt(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanKdtService.recovery(split(ids, ','));
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to remove orders:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }

    /**
     * 废弃订单但不删除
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:remove")
    @PostMapping("/descard/{ids}")
    @ResponseBody
    public AjaxResult descard(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanOrderService.discard(split(ids, ','), sysUser.getKdtId());
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to remove orders:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }

    /**
     * 恢复订单
     *
     * @param ids
     * @return
     */
    @RequiresPermissions("customs:youzan:recovery")
    @PostMapping("/recovery/{ids}")
    @ResponseBody
    public AjaxResult recovery(String ids) {
        AjaxResult result = null;
        SysUser sysUser = getSysUser();
        if (nonNull(sysUser) && isNotBlank(ids)) {
            try {
                youzanOrderService.recovery(split(ids, ','), sysUser.getKdtId());
                result = success();
            } catch (BusinessException ex) {
                LOG.error("failed to recover orders:{}", ex.getMessage());
                result = error(ex.getMessage());
            }
        } else {
            LOG.error("sys user is null or ids is empty, it's weired, ids:{}", ids);
        }
        return result;
    }
}
