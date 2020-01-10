/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.web.controller.youzan;

import com.ruoyi.common.config.Global;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.domain.YouzanMessage;
import com.ruoyi.yz.service.YouzanConfigService;
import com.ruoyi.yz.service.YouzanKdtService;
import com.ruoyi.yz.service.YouzanMessageService;
import com.ruoyi.yz.service.YouzanOrderService;
import static com.ruoyi.yz.utils.YouZanUtil.call;
import static com.ruoyi.yz.utils.YouZanUtil.parseMessage;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.enums.Customer;
import static com.ruoyi.common.enums.Customer.YOUZAN;
import static com.ruoyi.common.enums.Customer.getCustomerByDeptName;
import static com.ruoyi.common.utils.AesUtil.decrypt;
import static com.ruoyi.common.utils.DateUtil.addDays;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysDept;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.domain.YouzanUserToken;
import static com.ruoyi.yz.utils.YouZanUtil.parseToken;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import static org.apache.commons.collections.MapUtils.isNotEmpty;
import static org.apache.commons.collections.MapUtils.toProperties;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import static com.ruoyi.common.utils.DateUtil.parse;
import com.ruoyi.framework.web.http.Invoke;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.service.ISysMenuService;
import static com.ruoyi.yz.cnst.Const.YOUZAN_USER_CN_PREFIX;
import static com.ruoyi.yz.cnst.Const.YOUZAN_USER_CN_SUFFIX;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_APPLYING;
import static com.ruoyi.yz.enums.WuliuComp.YUNDA;
import com.ruoyi.yz.service.YundaKjOrderService;
import static com.ruoyi.yz.utils.PasswdUtil.getCalcName;
import static com.ruoyi.yz.utils.PasswdUtil.getLoginName;
import static com.ruoyi.yz.utils.PasswdUtil.getPasswdByName;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponse;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponses;
import java.text.ParseException;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import static com.ruoyi.yz.utils.YundaUtil.assembleYdCreateReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import static com.ruoyi.yz.utils.YundaUtil.assembleYdQueryReq;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateRequest;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryRequest;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryResponse;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryResponses;
import java.util.Arrays;
import static java.util.Calendar.getInstance;
import java.util.HashMap;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import org.apache.commons.collections4.CollectionUtils;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang.time.DateUtils.addSeconds;
import static org.apache.commons.lang3.StringUtils.endsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.time.DateUtils.addHours;
import org.springframework.ui.ModelMap;

/**
 *
 * @author wmao
 */
@Controller
@RequestMapping("/rest/yz")
public class YzCallbackController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(YzCallbackController.class);

    @Autowired
    private YouzanConfigService youzanConfigService;

    @Autowired
    private YouzanKdtService youzanKdtService;

    @Autowired
    private YouzanOrderService youzanOrderService;

    @Autowired
    private YouzanMessageService youzanMessageService;

    @Autowired
    private YundaKjOrderService yundaKjOrderService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysMenuService menuService;

    @RequestMapping(value = "/callback", produces = "text/html;charset=GBK", method = GET)
    @ResponseBody
    public String callback(@RequestParam Map<String, String> params) {
        LOG.info("params:" + params);
        String ret = "failed";
        if (isNotEmpty(params)) {
            YouzanConfig config = youzanConfigService.getOne();
            if (config != null) {
                if (params.containsKey("message")) {
                    try {
                        String msgCtnt = decrypt(params.get("message"), config.getClientSecret());
                        YouzanMessage message = parseMessage(msgCtnt);
                        int insertRet = youzanMessageService.insert(message);
                        ret = insertRet < 0 ? "failed" : "success";
                    } catch (Exception ex) {
                        LOG.error("failed to decrypt message in callback:" + ex.getMessage());
                    }
                } else if (params.containsKey("code")) {
                    String code = params.get("code");
                    OAuthToken token = call(config, code);
                    if (nonNull(token)) {
                        int insertUpdateRet = -1;
                        YouzanKdt oldKdt = youzanKdtService.getOneByAuthId(token.getAuthorityId());
                        if (isNull(oldKdt)) {
                            YouzanKdt newKdt = youzanKdtService.fill(new YouzanKdt(code, config.getName(), config.getCode()), token);
                            insertUpdateRet = nonNull(newKdt) ? youzanKdtService.insert(newKdt) : -1;
                            if (insertUpdateRet >= 0) {
                                SysUser user = assembleUser(newKdt);
                                if (nonNull(user)) {
                                    userService.insertUser(user);
                                }
                            }
                        } else {
                            oldKdt.setCode(code);
                            YouzanKdt newKdt = youzanKdtService.fill(oldKdt, token);
                            insertUpdateRet = nonNull(newKdt) ? youzanKdtService.update(newKdt) : -1;
                        }
                        ret = insertUpdateRet < 0 ? "failed" : "success";
                    } else {
                        LOG.error("failed to get token from code");
                    }
                } else {
                    LOG.warn("it's very weired to get {}in callback interface", toProperties(params).toString());
                }
            } else {
                LOG.warn("Youzan config is null, it's weired!");
            }
        }
        return ret;
    }

    private SysUser assembleUser(YouzanKdt kdt) {
        SysUser user = null;
        if (nonNull(kdt)) {
            Date currentTime = Calendar.getInstance().getTime();
            final String salt = ShiroUtils.randomSalt();
            final String kdtName = trimToEmpty(kdt.getAuthorityName());
            final String kdtId = trimToEmpty(kdt.getAuthorityId());
            user = new SysUser();
            user.setLoginName(getLoginName(kdtId));
            user.setUserName(isNotBlank(kdtName) ? kdtName : getCalcName(kdt.getName()));
            user.setSalt(salt);
            user.setPassword(getPasswdByName(kdtId, kdtName, salt));
            user.setRemark(kdt.getRegCode());
            user.setKdtId(kdt.getId());
            SysPost post = postService.getPostByCode(YOUZAN.getPostCode());
            user.setPostIds(nonNull(post) ? new Long[]{post.getPostId()} : null);
            SysRole role = roleService.getRoleByKey(YOUZAN.getRoleKey());
            user.setRoleIds(nonNull(role) ? new Long[]{role.getRoleId()} : null);
            SysDept dept = deptService.checkDeptNameUnique(YOUZAN.getDeptName(), YOUZAN.getParentDeptId());
            user.setDeptId(nonNull(dept) ? dept.getDeptId() : null);
            user.setCreateBy(CREATE_BY_PROGRAM);
            user.setCreateTime(currentTime);
            user.setUpdateBy(CREATE_BY_PROGRAM);
            user.setUpdateTime(currentTime);
        } else {
            LOG.error("kdt is empty, failed to generate user from kdt:{}", kdt);
        }
        return user;
    }

    private boolean checkIfFstLogin(String userName) {
        String un = trimToEmpty(userName);
        return isBlank(un) || (startsWithIgnoreCase(un, YOUZAN_USER_CN_PREFIX) && endsWithIgnoreCase(un, YOUZAN_USER_CN_SUFFIX));
    }

    @GetMapping(value = "/goto")
    public String gto(@RequestParam Map<String, String> params, ModelMap mmap) {
        String ret = "error/404";
        if (isNotEmpty(params) && params.containsKey("userToken")) {
            YouzanConfig config = youzanConfigService.getOne();
            try {
                YouzanUserToken userToken = parseToken(decrypt(params.get("userToken"), config.getClientSecret()));
                if (nonNull(userToken)) {
                    String kdtId = userToken.getKdtId();
                    if (isNotBlank(kdtId)) {
                        String loginName = getLoginName(userToken.getKdtId());
                        SysUser sysUser = userService.selectUserByLoginName(loginName);
                        if (nonNull(sysUser)) {
                            if (checkIfFstLogin(sysUser.getUserName())) {
                                int udRet = youzanKdtService.updateYouzanKdtByUserToken(userToken, sysUser);
                                LOG.info("success to login:{}, update kdt result:{}", userToken, udRet);
                            }
                            UsernamePasswordToken token = new UsernamePasswordToken(loginName, getPasswdByName(kdtId, sysUser.getUserName(), sysUser.getSalt()), false);
                            Subject subject = SecurityUtils.getSubject();
                            subject.login(token);

                            // 根据用户id取出菜单
                            List<SysMenu> menus = menuService.selectMenusByUser(sysUser);
                            mmap.put("menus", menus);
                            mmap.put("user", sysUser);
                            mmap.put("copyrightYear", Global.getCopyrightYear());
                            mmap.put("demoEnabled", Global.isDemoEnabled());
                            return "/index";
                        } else {
                            LOG.error("can't find kdt by kdtId:{}", kdtId);
                        }
                    } else {
                        LOG.error("kdt of user token is empty:{}", kdtId);
                    }
                } else {
                    LOG.error("user token is empty:{}", userToken);
                }
            } catch (Exception ex) {
                LOG.error("failed to login:{}", Arrays.toString(ex.getStackTrace()));
            }
        } else {
            LOG.warn("invalid params:{}", params);
        }
        return ret;
    }

    @PostMapping(value = "/queryKdtOfPlat")
    @ResponseBody
    @Invoke(token = "******", drift = 10L * 1000)
    public List<YouzanKdt> queryKdt(@RequestBody Map<String, String> params) {
        List<YouzanKdt> kdts = null;
        if (isNotEmpty(params)) {
            String dept = trim(params.get("dept"));
            if (isNotBlank(dept)) {
                Customer cust = getCustomerByDeptName(dept);
                if (nonNull(cust) && cust == YOUZAN) {
                    kdts = youzanKdtService.getAvailKdts();
                } else {
                    LOG.error("cust is empty or is not youza plat:{}", cust);
                }
            } else {
                LOG.error("dept in params is empty:{}", dept);
            }
        } else {
            LOG.error("params in request is empty:{}", params);
        }
        return kdts;
    }

    @PostMapping(value = "/latestOrders")
    @ResponseBody
    @Invoke(token = "******", drift = 10L * 1000)
    public List<YouzanOrder> queryOrderByTime(@RequestBody Map<String, String> params) {
        List<YouzanOrder> orders = null;
        if (isNotEmpty(params)) {
            try {
                Date lastPulledDate = parse(trim(params.get("lastPulledDate")), "yyyy-MM-dd HH:mm:ss");
                if (nonNull(lastPulledDate)) {
                    orders = youzanOrderService.wmsPullOrders(addSeconds(lastPulledDate, -10));
                } else {
                    LOG.warn("last pulled date is empty:{}", lastPulledDate);
                }
            } catch (ParseException ex) {
                LOG.error("failed to get orders by last pulled date:{}", ex.getMessage());
            }
        } else {
            LOG.error("params in request is empty:{}", params);
        }
        return orders;
    }

    @PostMapping(value = "/callWuliu")
    @ResponseBody
    @Invoke(token = "******", drift = 10L * 1000)
    public Map<String, String> callWuliu(@RequestBody Map<String, String> params) {
        Map<String, String> wuliu = new HashMap<>();
        if (isNotEmpty(params)) {
            String orderNo = trim(params.get("orderNo"));
            if (isNotBlank(orderNo)) {
                YouzanOrder order = youzanOrderService.getOneByOrderNo(orderNo);
                if (nonNull(order)) {
                    YouzanKdt kdt = youzanKdtService.getOneByAuthId(order.getKdtId());
                    if (nonNull(kdt)) {
                        YdCreateRequest createRequest = assembleYdCreateReq(order, kdt.getSender());
                        YdCreateResponses response = yundaKjOrderService.sendRequest(createRequest);
                        if (nonNull(response)) {
                            List<YdCreateResponse> resps = response.getResponse();
                            if (CollectionUtils.isNotEmpty(resps)) {
                                YdCreateResponse resp = resps.get(0);
                                if (nonNull(resp) && isNotBlank(resp.getMailNo())) {
                                    order.setWayBillEnt(YUNDA.getValue());
                                    order.setWayBillNo(resp.getMailNo());
                                    order.setStatus(STATUS_APPLYING.name());
                                    order.setStatusMessage(STATUS_APPLYING.getValue());
                                    order.setUpdateBy(CREATE_BY_PROGRAM);
                                    order.setUpdateTime(Calendar.getInstance().getTime());
                                    youzanOrderService.update(order);
                                    wuliu.put("entName", YUNDA.getValue());
                                    wuliu.put("entNo", resp.getMailNo());
                                    wuliu.put("success", "true");
                                } else if (nonNull(resp)) {
                                    wuliu.put("entName", YUNDA.getValue());
                                    wuliu.put("success", "false");
                                    wuliu.put("errorMsg", resp.getMsg());
                                    LOG.error("fst element of list is empty:{}", resp);
                                }
                            } else {
                                LOG.error("response list is empty:{}", response);
                            }
                        } else {
                            LOG.error("create response is empty:{}", createRequest);
                        }
                    } else {
                        LOG.error("failed to find kdt by kdt from order:{}", kdt);
                    }
                } else {
                    LOG.error("can't find right order by order no:{}", orderNo);
                }
            } else {
                LOG.error("no orderno in the request params{}", params);
            }
        } else {
            LOG.error("params in request is empty:{}", params);
        }
        return wuliu;
    }

    @PostMapping(value = "/queryWuliu")
    @ResponseBody
    @Invoke(token = "******", drift = 10L * 1000)
    public Map<String, String> queryWuliu(@RequestBody Map<String, String> params) {
        Map<String, String> trace = new HashMap<>();
        if (isNotEmpty(params)) {
            String mailNo = trim(params.get("mailNo"));
            if (isNotBlank(mailNo)) {
                YdQueryRequest queryRequest = assembleYdQueryReq(mailNo);
                YdQueryResponses response = yundaKjOrderService.findTrail(queryRequest);
                if (nonNull(response)) {
                    List<YdQueryResponse> resps = response.getResponse();
                    if (CollectionUtils.isNotEmpty(resps)) {
                        YdQueryResponse resp = resps.get(0);
                        if (nonNull(resp)) {
                            trace.put("status", resp.getOrderStatus());
                            trace.put("code", resp.getCode());
                            trace.put("msg", resp.getMsg());
                        } else {
                            LOG.error("fst element of list is empty:{}", resp);
                        }
                    } else {
                        LOG.error("response list is empty:{}", response);
                    }
                } else {
                    LOG.error("query response is empty:{}", queryRequest);
                }
            } else {
                LOG.error("mailno in params is empty:{}", mailNo);
            }
        } else {
            LOG.error("params in request is empty:{}", params);
        }
        return trace;
    }

    @Scheduled(fixedDelay = 12 * 60 * 60 * 1000, initialDelay = 1000)
    public void refreshToken() {
        youzanKdtService.refreshToken();
    }

    /**
     * 定时拉取订单
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 1000)
    public void pullOrders() {
        youzanOrderService.pullOrders();
    }

    /**
     * 定时自动发送清关信息到wms平台
     */
    @Scheduled(fixedDelay = 6 * 60 * 1000, initialDelay = 1000)
    public void placeWmsOrders() {
        youzanOrderService.placeWmsOrders();
    }

    /**
     * 定时清关(发送物流、支付、订单推单信息到海关)
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000, initialDelay = 1000)
    public void autoExecute() {
        Date curretTime = getInstance().getTime();
        List<YouzanKdt> kdts = youzanKdtService.getAvailKdts();
        if (CollectionUtils.isNotEmpty(kdts)) {
            kdts.forEach((kdt) -> {
                if (nonNull(kdt)) {
                    LOG.info("开始对商户[{}]物流、支付、订单清关推送", kdt.getAuthorityName());
                    youzanOrderService.autoExecute(kdt, addDays(curretTime, -1));
                }
            });
        }
    }

    /**
     * 定时清关(发送清单推单信息到海关)
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 1000)
    public void autoDetailsExecute() {
        Date curretTime = getInstance().getTime();
        List<YouzanKdt> kdts = youzanKdtService.getAvailKdts();
        if (CollectionUtils.isNotEmpty(kdts)) {
            kdts.forEach((kdt) -> {
                if (nonNull(kdt)) {
                    LOG.info("开始对商户[{}]清单清关推送", kdt.getAuthorityName());
                    youzanOrderService.autoDetailsExecute(kdt, addDays(curretTime, -1));
                }
            });
        }
    }

    /**
     * 定时查找有赞支付单推送结果
     */
    @Scheduled(fixedDelay = 10 * 60 * 1000, initialDelay = 1000)
    public void autoQueryPayResult() {
        Date currentTime = getInstance().getTime();
        List<YouzanKdt> kdts = youzanKdtService.getAvailKdts();
        if (CollectionUtils.isNotEmpty(kdts)) {
            kdts.forEach((kdt) -> {
                if (nonNull(kdt)) {
                    LOG.info("开始查找有赞支付单[{}]推送结果", kdt.getAuthorityName());
                    youzanOrderService.autoQueryPayClearanceResult(kdt, addHours(currentTime, -12));
                }
            });
        }
    }

    /**
     * 定时向有赞同步清关成功状态
     */
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 1000)
    public void autoComplete() {
        final Date curretTime = getInstance().getTime();
        List<YouzanKdt> kdts = youzanKdtService.getAvailKdts();
        if (isNotEmpty(kdts)) {
            kdts.forEach((kdt) -> {
                if (nonNull(kdt)) {
                    LOG.info("开始对商户[{}]清关完成订单进行结案处理", kdt.getAuthorityName());
                    youzanOrderService.autoComplete(kdt, addDays(curretTime, -2));
                }
            });
        }
    }
}
