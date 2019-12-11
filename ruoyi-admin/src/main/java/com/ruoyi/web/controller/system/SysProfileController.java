package com.ruoyi.web.controller.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import java.io.IOException;
import com.ruoyi.yz.service.YouzanKdtService;
import com.ruoyi.yz.service.intf.Enterprise;
import com.ruoyi.yz.wuliu.Sender;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.size;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static com.ruoyi.web.core.factory.EnterpriseFactory.getServiceByPlatName;
import com.ruoyi.yz.domain.YouzanKdt;
import java.util.Calendar;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(SysProfileController.class);

    private final String prefix = "system/user/profile";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private YouzanKdtService youzanKdtService;

    /**
     * 个人信息
     *
     * @param mmap
     * @return
     */
    @GetMapping()
    public String profile(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();
        mmap.put("user", user);
        
        Sender sender = null;
        YouzanKdt setting = null;
        if(nonNull(user)){
            sender = ((Enterprise)youzanKdtService).getSenderByKdtId(user.getKdtId());
            YouzanKdt kdt = youzanKdtService.getKdtById(user.getKdtId());
            if(nonNull(kdt)){
                setting = new YouzanKdt();
                setting.setId(kdt.getId());
                setting.setAutoPulling(kdt.isAutoPulling());
                setting.setAutoClearing(kdt.isAutoClearing());
            }
        }
        mmap.put("sender", nonNull(sender) ? sender : new Sender());
        mmap.put("setting", nonNull(setting) ? setting : new YouzanKdt());
        mmap.put("roleGroup", userService.selectUserRoleGroup(user.getUserId()));
        mmap.put("postGroup", userService.selectUserPostGroup(user.getUserId()));
        return prefix + "/profile";
    }

    @GetMapping("/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        SysUser user = ShiroUtils.getSysUser();
        return passwordService.matches(user, password);
    }

    @GetMapping("/resetPwd")
    public String resetPwd(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/resetPwd";
    }

    @Log(title = "更新店铺设置", businessType = BusinessType.UPDATE)
    @PostMapping("/updateKdtSetting")
    @ResponseBody
    public AjaxResult updateKdtSetting(YouzanKdt youzanKdt) {
        SysUser user = ShiroUtils.getSysUser();
        if(nonNull(user) && nonNull(youzanKdt) && equalsIgnoreCase(user.getKdtId(), youzanKdt.getId())){
            youzanKdt.setUpdateBy(user.getLoginName());
            youzanKdt.setUpdateTime(Calendar.getInstance().getTime());
            int retValue = youzanKdtService.update(youzanKdt);
            LOG.info("retValue:{}", retValue);
            return retValue > 0 ? success("更新店铺设置成功！") : error("更新店铺设置失败！");
        }
        return error("非法用户，不能更新店铺设置!");
    }

    @Log(title = "更新发货地址", businessType = BusinessType.UPDATE)
    @PostMapping("/updateSender")
    @ResponseBody
    public AjaxResult updateSender(Sender sender) {
        SysUser user = ShiroUtils.getSysUser();
        LOG.info("senderAddr:{}", sender);
        if (nonNull(user)) {
            String loginName = user.getLoginName();
            if (isNotBlank(loginName)) {
                String[] lnArray = split(loginName, "_");
                if (isNotEmpty(lnArray) && size(lnArray) > 1) {
                    Enterprise ent = getServiceByPlatName(lnArray[0]);
                    if (nonNull(ent)) {
                        int retValue = ent.updateAddress(user.getKdtId(), sender);
                        LOG.info("retValue:{}", retValue);
                        return retValue > 0 ? success("更新发货/退货地址成功！") : error("更新发货/退货地址失败！");
                    } else {
                        return error("无法找到对应平台");
                    }
                } else {
                    return error("无法找到合适的平台");
                }
            } else {
                return error("非法登录名, 不能更新发货地址!");
            }
        }
        return error("非法用户，不能更新发货地址!");
    }

    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwd(String oldPassword, String newPassword) {
        SysUser user = ShiroUtils.getSysUser();
        if (StringUtils.isNotEmpty(newPassword) && passwordService.matches(user, oldPassword)) {
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
            if (userService.resetUserPwd(user) > 0) {
                ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
                return success();
            }
            return error();
        } else {
            return error("修改密码失败，旧密码错误");
        }
    }

    /**
     * 修改用户
     *
     * @param mmap
     * @return
     */
    @GetMapping("/edit")
    public String edit(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/edit";
    }

    /**
     * 修改头像
     *
     * @param mmap
     * @return
     */
    @GetMapping("/avatar")
    public String avatar(ModelMap mmap) {
        SysUser user = ShiroUtils.getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/avatar";
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(SysUser user) {
        SysUser currentUser = ShiroUtils.getSysUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (userService.updateUserInfo(currentUser) > 0) {
            ShiroUtils.setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }

    /**
     * 保存头像
     *
     * @param file
     * @return
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvatar")
    @ResponseBody
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        SysUser currentUser = ShiroUtils.getSysUser();
        try {
            if (!file.isEmpty()) {
                String avatar = FileUploadUtils.upload(Global.getAvatarPath(), file);
                currentUser.setAvatar(avatar);
                if (userService.updateUserInfo(currentUser) > 0) {
                    ShiroUtils.setSysUser(userService.selectUserById(currentUser.getUserId()));
                    return success();
                }
            }
            return error();
        } catch (IOException e) {
            LOG.error("修改头像失败！", e);
            return error(e.getMessage());
        }
    }
}
