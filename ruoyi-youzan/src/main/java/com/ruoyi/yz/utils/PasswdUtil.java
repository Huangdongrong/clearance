/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import static com.ruoyi.common.utils.ChineseCharacterUtil.convertHanzi2Pinyin;
import static com.ruoyi.common.utils.ChineseCharacterUtil.merge;
import static com.ruoyi.yz.cnst.Const.YOUZAN_USER_CN_PREFIX;
import static com.ruoyi.yz.cnst.Const.YOUZAN_USER_CN_SUFFIX;
import static com.ruoyi.yz.cnst.Const.YOUZAN_USER_PREFIX;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.logging.log4j.util.Strings.isNotBlank;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public final class PasswdUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PasswdUtil.class);

    private static final String SALT = "Af2D!sd$2";

    public static String getLoginName(final String authId) {
        if (isNotBlank(authId)) {
            return YOUZAN_USER_PREFIX + authId;
        } else {
            LOG.error("failed to get right authid of kdt:{}", authId);
        }
        return "";
    }

    public static String getCalcName(final String kdtCnName) {
        String ret = null;
        if (isNotBlank(kdtCnName)) {
            ret = YOUZAN_USER_CN_PREFIX + trimToEmpty(kdtCnName) + YOUZAN_USER_CN_SUFFIX;
        }
        return ret;
    }

    public static String getPasswdByName(final String authId, final String kdtCnName, final String salt) {
        String ret = null;
        if (isNotBlank(kdtCnName)) {
            String saltStr = isNotBlank(salt) ? salt : SALT;
            String cnName = getCalcName(kdtCnName);
            String enName = convertHanzi2Pinyin(cnName, false);
            String passwd = merge(enName, saltStr);
            String loginName = getLoginName(authId);
            ret = new Md5Hash(loginName + passwd + saltStr).toHex();
        }
        return ret;
    }

}
