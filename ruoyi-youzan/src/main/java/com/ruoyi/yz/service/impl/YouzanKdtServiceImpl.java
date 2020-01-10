/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.service.impl;

import com.ruoyi.common.exception.BusinessException;
import static com.ruoyi.common.utils.AddressUtil.addressResolution;
import com.ruoyi.yz.domain.YouzanAuthMessage;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.domain.YouzanKdt;
import static com.ruoyi.yz.enums.KdtStatus.STATUS_VALID;
import static com.ruoyi.yz.enums.KdtStatus.STATUS_WARNING;
import com.ruoyi.yz.mapper.YouzanAuthMessageMapper;
import com.ruoyi.yz.mapper.YouzanConfigMapper;
import com.ruoyi.yz.mapper.YouzanKdtMapper;
import com.ruoyi.yz.service.YouzanKdtService;
import static com.ruoyi.yz.utils.YouZanUtil.call;
import static com.ruoyi.yz.utils.YouZanUtil.refresh;
import static com.ruoyi.common.utils.DateUtil.STANDARD_DATE_PATTERN;
import static com.ruoyi.common.utils.DateUtil.addDays;
import static com.ruoyi.common.utils.DateUtil.fromMillise;
import static com.ruoyi.common.utils.DateUtil.parse;
import static com.ruoyi.common.utils.DateUtils.getStartTimeOfDay;
import static com.ruoyi.common.utils.StringUtils.nullToEmpty;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.mapper.SysUserMapper;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.YouzanUserToken;
import static com.ruoyi.yz.enums.Port.CD;
import com.ruoyi.yz.mapper.CustomsPlatMapper;
import static com.ruoyi.yz.utils.PasswdUtil.getPasswdByName;
import com.ruoyi.yz.wuliu.Sender;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections.MapUtils.getString;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author wmao
 */
@Service("youzanKdtService")
@Scope("prototype")
@Transactional
public class YouzanKdtServiceImpl implements YouzanKdtService {

    private static final Logger LOG = LoggerFactory.getLogger(YouzanKdtServiceImpl.class);

    @Autowired
    private YouzanKdtMapper youzanKdtMapper;

    @Autowired
    private YouzanAuthMessageMapper youzanAuthMessageMapper;

    @Autowired
    private YouzanConfigMapper youzanConfigMapper;

    @Autowired
    private CustomsPlatMapper customsPlatMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional
    public int update(YouzanKdt kdt) {
        return nonNull(kdt) ? youzanKdtMapper.update(kdt) : -1;
    }

    @Override
    @Transactional
    public int insert(YouzanKdt kdt) {
        int ret = -1;
        if (nonNull(kdt)) {
            String authId = kdt.getAuthorityId();
            if (isNotBlank(authId)) {
                YouzanAuthMessage authMessage = authMessage(authId);
                kdt.setStatus(alreadyReceivedAuthMessage(authMessage));
                kdt.setAuthorityDisplayNo(nonNull(authMessage) ? authMessage.getShopDisPlayNo() : "");
                YouzanKdt oldKdt = getOneByAuthId(authId);
                if (nonNull(oldKdt)) {
                    kdt.setId(oldKdt.getId());
                    kdt.setCode(oldKdt.getCode());
                    kdt.setCreateTime(oldKdt.getCreateTime());
                    kdt.setLastPulledDate(oldKdt.getLastPulledDate());
                    kdt.setAuthorityName(oldKdt.getAuthorityName());
                    kdt.setSender(oldKdt.getSender());
                    try {
                        Date currentTime = parse(Calendar.getInstance().getTime(), STANDARD_DATE_PATTERN);
                        kdt.setUpdateTime(currentTime);
                    } catch (ParseException ex) {
                        LOG.error("failed to parse time:" + ex.getMessage());
                    }
                    ret = update(kdt);
                } else {
                    try {
                        CustomsPlat plat = customsPlatMapper.getOneByDistrict(CD.name());
                        if (nonNull(plat)) {
                            kdt.setSender(plat.getSender());
                        }
                        Date currentTime = parse(Calendar.getInstance().getTime(), STANDARD_DATE_PATTERN);
                        kdt.setCreateTime(currentTime);
                        kdt.setUpdateTime(currentTime);
                        kdt.setLastPulledDate(addDays(kdt.getCreateTime(), -1));
                    } catch (ParseException ex) {
                        LOG.error("failed to parse time:" + ex.getMessage());
                    }
                    ret = youzanKdtMapper.insert(kdt);
                }
            } else {
                LOG.warn("kdt id is empty:" + kdt);
            }
        } else {
            LOG.warn("kdt is empty");
        }
        return ret;
    }

    @Override
    public YouzanKdt fill(YouzanKdt kdt, OAuthToken token) {
        if (nonNull(kdt) && nonNull(token)) {
            try {
                String kdtId = token.getAuthorityId();
                Date currentTime = parse(Calendar.getInstance().getTime(), STANDARD_DATE_PATTERN);
                kdt.setAccessToken(token.getAccessToken());
                kdt.setAuthorityId(kdtId);
                YouzanAuthMessage authMessage = authMessage(kdtId);
                kdt.setAuthorityDisplayNo(nonNull(authMessage) ? authMessage.getShopDisPlayNo() : "");
                kdt.setScope(token.getScope());
                kdt.setRefreshToken(token.getRefreshToken());
                kdt.setExpires(fromMillise(token.getExpires()));
                kdt.setUpdateTime(currentTime);
                kdt.setCreateBy(CREATE_BY_PROGRAM);
                kdt.setUpdateBy(CREATE_BY_PROGRAM);
            } catch (ParseException ex) {
                LOG.error("failed to parse expires:" + ex.getMessage());
                kdt.setExpires(new Date());
            }
        } else {
            LOG.error("failed to update token, pls check it, kdt:{}, token:{}", kdt, token);
        }
        return kdt;
    }

    @Override
    public YouzanKdt getKdtById(String id) {
        return isNotBlank(id) ? youzanKdtMapper.getOne(id) : null;
    }

    private YouzanAuthMessage authMessage(String kdtId) {
        YouzanAuthMessage youzanMessage = null;
        if (isNotBlank(kdtId)) {
            youzanMessage = (YouzanAuthMessage) youzanAuthMessageMapper.getOne(kdtId);
        }
        return youzanMessage;
    }

    private String alreadyReceivedAuthMessage(String kdtId) {
        String status = STATUS_WARNING.name();
        if (isNotBlank(kdtId)) {
            YouzanAuthMessage authMessage = authMessage(kdtId);
            if (nonNull(authMessage)) {
                status = alreadyReceivedAuthMessage(authMessage);
            } else {
                LOG.error("auth message of kdt is empty:{}", kdtId);
            }
        } else {
            LOG.error("kdtid is emtpy, it's weired!");
        }
        return status;
    }

    private String alreadyReceivedAuthMessage(YouzanAuthMessage youzanMessage) {
        boolean received = false;
        if (nonNull(youzanMessage)) {
            Date currentTime = Calendar.getInstance().getTime();
            Date effectTime = youzanMessage.getEffectTime();
            Date expireTime = youzanMessage.getExpireTime();
            received = currentTime.after(effectTime) && currentTime.before(expireTime);
        } else {
            LOG.error("auth message of kdt is empty, it's weired!");
        }
        return received ? STATUS_VALID.name() : STATUS_WARNING.name();
    }

    @Override
    @Transactional
    public void refreshToken() {
        YouzanConfig config = youzanConfigMapper.getOne();
        if (nonNull(config)) {
            Date currentTime = Calendar.getInstance().getTime();
            List<YouzanKdt> kdts = youzanKdtMapper.getAvailKdts();
            if (isNotEmpty(kdts)) {
                kdts.forEach((kdt) -> {
                    if (nonNull(kdt)) {
                        Date expireTime = addDays(kdt.getExpires(), -1);
                        //判断当前时间是否到了刷新token时间
                        LOG.info("kdt:{}, expireTime is:{}", kdt.getAuthorityName(), expireTime);
                        if (currentTime.after(expireTime)) {
                            OAuthToken token = null;
                            if (isNotBlank(kdt.getAccessToken()) && isNotBlank(kdt.getRefreshToken())) {
                                //判断是否刷新token
                                LOG.info("refresh kdt:{}, token:{}", kdt.getAuthorityName(), kdt.getRefreshToken());
                                token = refresh(config, kdt.getRefreshToken());
                            } else {
                                //判断是否获取token
                                LOG.info("get new kdt:{}, token:{}", kdt.getAuthorityName(), kdt.getCode());
                                token = call(config, kdt.getCode());
                            }
                            kdt.setStatus(alreadyReceivedAuthMessage(kdt.getAuthorityId()));
                            LOG.info("begin to update kdt:{}, token:{}", kdt.getAuthorityName(), token);
                            update(fill(kdt, token));
                        } else {
                            LOG.info("time is far from expiration{}, no needs to update token, kdt:{}", expireTime, kdt.getAuthorityName());
                        }
                    } else {
                        LOG.warn("kdt is empty {}, it's weired!", kdt);
                    }
                });
            } else {
                LOG.warn("there is no kdt in system:{}", kdts);
            }
        } else {
            LOG.warn("there is no youzan config in system:{}", config);
        }
    }

    /**
     * 更新店铺发货/退货地址
     *
     * @param id
     * @param sender
     * @return
     */
    @Override
    @Transactional
    public int updateAddress(String id, Sender sender) {
        int ret = -1;
        if (nonNull(sender) && isNotBlank(id)) {
            Date currentTime = Calendar.getInstance().getTime();
            Map<String, String> addrs = addressResolution(sender.getAddress());
            if (MapUtils.isNotEmpty(addrs)) {
                sender.setCountry("中国");
                sender.setProvince(getString(addrs, "province"));
                sender.setCity(getString(addrs, "city"));
                sender.setArea(getString(addrs, "district"));
                sender.setAddress(getString(addrs, "town") + getString(addrs, "village"));
                YouzanKdt kdt = new YouzanKdt();
                kdt.setId(id);
                kdt.setSender(sender);
                kdt.setUpdateTime(currentTime);
                kdt.setUpdateBy(CREATE_BY_PROGRAM);
                LOG.info("update kdt:{}", kdt);
                ret = youzanKdtMapper.update(kdt);
            } else {
                LOG.warn("sender's address is empty, pls check it!");
            }
        }
        return ret;
    }

    @Override
    public Sender getSenderByKdtId(String id) {
        Sender sender = null;
        if (isNotBlank(id)) {
            YouzanKdt kdt = youzanKdtMapper.getOne(id);
            if (nonNull(kdt)) {
                sender = kdt.getSender();
                if (nonNull(sender)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(nullToEmpty(sender.getProvince())).append(nullToEmpty(sender.getCity())).append(nullToEmpty(sender.getArea())).append(nullToEmpty(sender.getAddress()));
                    sender.setAddress(sb.toString());
                }
            }
        }
        return sender;
    }

    @Override
    public List<YouzanKdt> getAvailKdts() {
        return youzanKdtMapper.getAvailKdts();
    }

    @Override
    @Transactional
    public int updateYouzanKdtByUserToken(YouzanUserToken token, SysUser sysUser) {
        int ret = -1;
        LOG.info("userToken:" + token);
        if (nonNull(token)) {
            YouzanKdt kdt = getOneByAuthId(token.getKdtId());
            if (nonNull(kdt) && !equalsIgnoreCase(kdt.getAuthorityName(), token.getShopName())) {
                Date currentTime = Calendar.getInstance().getTime();
                final String kdtName = trimToEmpty(token.getShopName());
                sysUser.setUserName(kdtName);
                sysUser.setPassword(getPasswdByName(token.getKdtId(), kdtName, sysUser.getSalt()));
                sysUser.setUpdateBy(CREATE_BY_PROGRAM);
                sysUser.setUpdateTime(currentTime);
                kdt.setAuthorityName(kdtName);
                kdt.setUpdateBy(CREATE_BY_PROGRAM);
                kdt.setUpdateTime(currentTime);
                userMapper.updateUser(sysUser);
                ret = youzanKdtMapper.update(kdt);
            } else {
                LOG.warn("kdt can't be found:{}, or no need to update kdt:{}", token, kdt);
            }
        } else {
            LOG.warn("youzan token is empty:{}", token);
        }
        return ret;
    }

    @Override
    public YouzanKdt getOneByAuthId(String authId) {
        return isNotBlank(authId) ? youzanKdtMapper.getOneByAuthId(authId) : null;
    }

    @Override
    public List<YouzanKdt> getList(YouzanKdt kdt) {
        if (nonNull(kdt) && nonNull(kdt.getUpdateTime())) {
            kdt.setUpdateTime(getStartTimeOfDay(addDays(kdt.getUpdateTime(), 1)));
        }
        return youzanKdtMapper.getPageList(kdt);
    }

    @Override
    @Transactional
    public int remove(String[] ids) throws BusinessException {
        int ret = 0;
        if (ArrayUtils.isNotEmpty(ids)) {
            ret = youzanKdtMapper.batchDelete(Arrays.asList(ids));
        }
        return ret;
    }

    @Override
    @Transactional
    public int recovery(String[] ids) throws BusinessException {
        int ret = 0;
        if (ArrayUtils.isNotEmpty(ids)) {
            ret = youzanKdtMapper.batchRecovery(Arrays.asList(ids));
        }
        return ret;
    }
}
