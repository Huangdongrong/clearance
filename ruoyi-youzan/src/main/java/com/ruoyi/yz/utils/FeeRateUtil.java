/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import java.text.DecimalFormat;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public final class FeeRateUtil {
    private static final BigDecimal CUSTOM_FEE_DISCOUNT = new BigDecimal(0.70).setScale(2, ROUND_HALF_DOWN);

    public static BigDecimal strPer2Bd(String percent) {
        if (isNotBlank(percent) && contains(percent, "%")) {
            percent = percent.replace("%", "");
            Float f = Float.valueOf(percent) / 100;
            BigDecimal decimal = new BigDecimal(f);
            return decimal;
        }
        return null;
    }

    public static String bd2StrPer(BigDecimal decimal) {
        DecimalFormat df = new DecimalFormat("0.00%");
        return nonNull(decimal) ? df.format(decimal) : null;
    }
    
    public static BigDecimal realFeeRate(BigDecimal dutyFee, BigDecimal valueAddedFee){
        return dutyFee.add(valueAddedFee).multiply(CUSTOM_FEE_DISCOUNT).divide(BigDecimal.ONE.subtract(dutyFee),4, BigDecimal.ROUND_HALF_DOWN);
    }
}
