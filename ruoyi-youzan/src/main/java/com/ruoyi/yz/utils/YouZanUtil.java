/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruoyi.common.annotation.JsonIgore;
import static com.ruoyi.common.utils.JsonUtil.OBJECT_MAPPER;
import static com.ruoyi.common.utils.JsonUtil.stringify;
import com.ruoyi.yz.domain.YouzanAuthMessage;
import com.youzan.cloud.open.sdk.common.exception.SDKException;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.ruoyi.yz.domain.YouzanConfig;
import com.ruoyi.yz.domain.YouzanKdt;
import com.ruoyi.yz.domain.YouzanMessage;
import com.ruoyi.yz.domain.YouzanOrder;
import com.ruoyi.yz.domain.YouzanSubscribeMessage;
import com.ruoyi.yz.domain.YouzanUserToken;
import static com.ruoyi.yz.enums.ActionType.PUSH;
import static com.ruoyi.yz.enums.Currency.CNY;
import static com.ruoyi.yz.enums.Port.CD;
import com.ruoyi.yz.support.TimestampToDateMorpher;
import static com.ruoyi.common.utils.UuidUtil.get32UUID;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.enums.WuliuComp;
import com.youzan.cloud.open.sdk.gen.v1_0_1.api.YouzanPayCustomsDeclarationReportpaymentQuery;
import com.youzan.cloud.open.sdk.gen.v1_0_1.api.YouzanPayCustomsDeclarationReportpaymentReport;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentQueryParams;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentQueryResult;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportParams;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanLogisticsExpressGet;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanLogisticsOnlineConfirm;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult.YouzanLogisticsExpressGetResultAllexpress;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsExpressGetResult.YouzanLogisticsExpressGetResultData;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradesSoldGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetParams;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradesSoldGetResult;
import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import static java.net.HttpURLConnection.HTTP_OK;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;
import java.util.Set;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;
import org.apache.commons.collections4.CollectionUtils;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.size;
import static org.apache.commons.collections4.IteratorUtils.get;
import org.apache.commons.collections4.IteratorUtils;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.startsWithIgnoreCase;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author wmao
 */
public class YouZanUtil {

    private static final Logger LOG = LoggerFactory.getLogger(YouZanUtil.class);

    private static final DefaultYZClient YZ_CLIENT = new DefaultYZClient();

    public static final int RESPONSE_SATSUS_SUCCESS_CODE = 200;

    /**
     * 忽略部分fields转换
     */
    public static final JsonConfig jsonConfig;

    static {
        jsonConfig = new JsonConfig();
        jsonConfig.addIgnoreFieldAnnotation(JsonIgore.class);
    }

    public static void logYzSdkObj(String method, Object params) {
        try {
            if (isNotBlank(method) && nonNull(params)) {
                LOG.info("method[{}], params[{}]", method, OBJECT_MAPPER.writeValueAsString(params));
            }
        } catch (JsonProcessingException ex) {
            LOG.error("failed to log params:", ex.getMessage());
        }
    }

    public static OAuthToken call(YouzanConfig config, String code) {
        OAuthToken codeToken = null;
        try {
            TokenParameter tokenParameter = TokenParameter.code()
                    .clientId(config.getClientKey())
                    .clientSecret(config.getClientSecret())
                    .code(code)
                    .build();
            codeToken = YZ_CLIENT.getOAuthToken(tokenParameter);
        } catch (SDKException ex) {
            LOG.error("failed to get token:" + ex.getMessage());
        }
        return codeToken;
    }

    public static OAuthToken refresh(YouzanConfig config, String refreshToken) {
        OAuthToken codeToken = null;
        try {
            TokenParameter tokenParameter = TokenParameter.refresh()
                    .clientId(config.getClientKey())
                    .clientSecret(config.getClientSecret())
                    .refreshToken(refreshToken)
                    .build();
            codeToken = YZ_CLIENT.getOAuthToken(tokenParameter);
        } catch (SDKException ex) {
            LOG.error("failed to refresh token:" + ex.getMessage());
        }
        return codeToken;
    }

    public static YouzanTradesSoldGetResult listOrders(YouzanKdt kdt, Date startTime, Date endTime) {
        YouzanTradesSoldGetResult result = null;
        if (nonNull(kdt)) {
            Date currentTime = addSeconds(Calendar.getInstance().getTime(), -10);
            Token token = new Token(kdt.getAccessToken());
            YouzanTradesSoldGet youzanTradeGet = new YouzanTradesSoldGet();
            YouzanTradesSoldGetParams youzanTradesSoldGetParams = new YouzanTradesSoldGetParams();
            youzanTradesSoldGetParams.setStartUpdate(nonNull(startTime) ? startTime : kdt.getCreateTime());
            youzanTradesSoldGetParams.setEndUpdate(nonNull(endTime) ? endTime : currentTime);
            youzanTradeGet.setAPIParams(youzanTradesSoldGetParams);
            try {
                logYzSdkObj("listOrders", youzanTradesSoldGetParams);
                result = YZ_CLIENT.invoke(youzanTradeGet, token, YouzanTradesSoldGetResult.class);
            } catch (SDKException ex) {
                LOG.error("failed to list shop:" + ex.getMessage());
            }
        }
        return result;
    }

    public static YouzanTradesSoldGetResult queryOrder(YouzanKdt kdt, String tid) {
        YouzanTradesSoldGetResult result = null;
        if (nonNull(kdt)) {
            Token token = new Token(kdt.getAccessToken());
            YouzanTradesSoldGet youzanTradeGet = new YouzanTradesSoldGet();
            YouzanTradesSoldGetParams youzanTradesSoldGetParams = new YouzanTradesSoldGetParams();
            youzanTradesSoldGetParams.setTid(tid);
            youzanTradeGet.setAPIParams(youzanTradesSoldGetParams);
            try {
                logYzSdkObj("queryOrder", youzanTradesSoldGetParams);
                result = YZ_CLIENT.invoke(youzanTradeGet, token, YouzanTradesSoldGetResult.class);
            } catch (SDKException ex) {
                LOG.error("failed to list shop:" + ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 支付单报关
     *
     * @param kdt
     * @param order
     * @return
     */
    public static YouzanPayCustomsDeclarationReportpaymentReportResult reportPay(YouzanKdt kdt, YouzanOrder order) {
        YouzanPayCustomsDeclarationReportpaymentReportResult result = null;
        if (nonNull(kdt) && nonNull(order)) {
            Token token = new Token(kdt.getAccessToken());
            YouzanPayCustomsDeclarationReportpaymentReport reportReq = new YouzanPayCustomsDeclarationReportpaymentReport();
            YouzanPayCustomsDeclarationReportpaymentReportParams params = new YouzanPayCustomsDeclarationReportpaymentReportParams();
            params.setKdtId(Long.parseLong(kdt.getAuthorityId()));
            params.setAmount(new BigDecimal(Double.valueOf(order.getAmount()) * 100).longValue());
            if (order.isSeperated()) {
                params.setSubOrderNo(order.getOrderNo());
            }
            params.setCustomsCode(CD.name());
            params.setActionType(PUSH.getKey());
            params.setCurrency(CNY.name());
            params.setTransaction(order.getTransaction());
            params.setTid(order.getTid());
            reportReq.setAPIParams(params);
            try {
                logYzSdkObj("reportPay", params);
                result = YZ_CLIENT.invoke(reportReq, token, YouzanPayCustomsDeclarationReportpaymentReportResult.class);
            } catch (SDKException ex) {
                LOG.error("failed to report pay info for order:" + ex.getMessage());
            }
        }
        return result;
    }

    public static YouzanPayCustomsDeclarationReportpaymentQueryResult queryReportPayResult(YouzanKdt kdt, YouzanOrder order) {
        YouzanPayCustomsDeclarationReportpaymentQueryResult result = null;
        if (nonNull(kdt)) {
            Token token = new Token(kdt.getAccessToken());
            YouzanPayCustomsDeclarationReportpaymentQuery query = new YouzanPayCustomsDeclarationReportpaymentQuery();
            YouzanPayCustomsDeclarationReportpaymentQueryParams params = new YouzanPayCustomsDeclarationReportpaymentQueryParams();
            params.setKdtId(Long.parseLong(kdt.getAuthorityId()));
            params.setTid(order.getTid());
            params.setTransaction(order.getTransaction());
            if (order.isSeperated()) {
                params.setSubOrderNo(order.getOrderNo());
            }
            query.setAPIParams(params);
            try {
                logYzSdkObj("queryClearResult", params);
                result = YZ_CLIENT.invoke(query, token, YouzanPayCustomsDeclarationReportpaymentQueryResult.class);
            } catch (SDKException ex) {
                LOG.error("failed to query clear result:" + ex.getMessage());
            }
        }
        return result;
    }

    public static YouzanUserToken parseToken(String userToken) {
        YouzanUserToken token = null;
        if (isNotBlank(userToken)) {
            JSONObject jsonObject = JSONObject.fromObject(userToken, jsonConfig);
            token = (YouzanUserToken) JSONObject.toBean(jsonObject, YouzanUserToken.class);
            if (nonNull(token)) {
                token.setCreateTime(Calendar.getInstance().getTime());
            }
        }
        LOG.info("token:" + token);
        return token;
    }

    public static YouzanMessage parseMessage(String message) {
        YouzanMessage yzMsg = null;
        if (isNotBlank(message)) {
            JSONObject jsonObject = JSONObject.fromObject(message, jsonConfig);
            JSONUtils.getMorpherRegistry().registerMorpher(new TimestampToDateMorpher());
            if (containsIgnoreCase(message, "expireTime") || containsIgnoreCase(message, "effectTime")) {
                yzMsg = (YouzanMessage) JSONObject.toBean(jsonObject, YouzanAuthMessage.class);
            } else if (containsIgnoreCase(message, "payTime") && containsIgnoreCase(message, "skuIntervalText")) {
                yzMsg = (YouzanMessage) JSONObject.toBean(jsonObject, YouzanSubscribeMessage.class);
                if (nonNull(yzMsg)) {
                    String kdtID = ((YouzanSubscribeMessage) yzMsg).getKdtID();
                    String kdtId = yzMsg.getKdtId();
                    if (isBlank(kdtId) && isNotBlank(kdtID)) {
                        yzMsg.setKdtId(kdtID);
                    }
                }
            } else {
                LOG.warn("invalid message, can't parse it:{}", message);
            }
            if (nonNull(yzMsg)) {
                Date currentTime = Calendar.getInstance().getTime();
                yzMsg.setId(get32UUID());
                yzMsg.setCreateBy(CREATE_BY_PROGRAM);
                yzMsg.setCreateTime(currentTime);
                yzMsg.setUpdateBy(CREATE_BY_PROGRAM);
                yzMsg.setUpdateTime(currentTime);
            }
        }
        return yzMsg;
    }

    public static List<YouzanLogisticsExpressGetResultAllexpress> listLogistics(final YouzanKdt kdt) {
        List<YouzanLogisticsExpressGetResultAllexpress> logistics = null;
        if (nonNull(kdt)) {
            Token token = new Token(kdt.getAccessToken());
            YouzanLogisticsExpressGet get = new YouzanLogisticsExpressGet();
            YouzanLogisticsExpressGetParams params = new YouzanLogisticsExpressGetParams();
            get.setAPIParams(params);
            try {
                YouzanLogisticsExpressGetResult result = YZ_CLIENT.invoke(get, token, YouzanLogisticsExpressGetResult.class);
                if (nonNull(result) && result.getCode() == HTTP_OK && result.getSuccess()) {
                    YouzanLogisticsExpressGetResultData data = result.getData();
                    if (nonNull(data)) {
                        logistics = data.getAllexpress();
                    } else {
                        LOG.error("failed to get datas from result:{}", stringify(data));
                    }
                } else {
                    LOG.error("failed to get logistic list:{}", stringify(result));
                }
            } catch (SDKException ex) {
                LOG.error("failed to list logistic:" + ex.getMessage());
            }
        }
        return logistics;
    }

    public static String findWuliuCode(final List<YouzanLogisticsExpressGetResultAllexpress> expresses, final WuliuComp wuliuComp) {
        String wuliuEntCode = null;
        if (isNotEmpty(expresses) && nonNull(wuliuComp)) {
            final String keyName = wuliuComp.getValue();
            final String alias = wuliuComp.getAlias();
            Collection<YouzanLogisticsExpressGetResultAllexpress> subList = CollectionUtils.select(expresses, (YouzanLogisticsExpressGetResultAllexpress express) -> {
                return startsWithIgnoreCase(express.getName(), keyName) || (keyName.length() > 2 && startsWithIgnoreCase(express.getName(), substring(keyName, 0, 2)));
            });
            if (isNotEmpty(subList)) {
                if (size(subList) > 1) {
                    YouzanLogisticsExpressGetResultAllexpress expr = IteratorUtils.find(subList.iterator(), (YouzanLogisticsExpressGetResultAllexpress express) -> {
                        return equalsIgnoreCase(express.getName(), alias);
                    });
                    if (nonNull(expr)) {
                        wuliuEntCode = "" + expr.getId();
                    } else {
                        LOG.error("too much logistic ent match key name, pls check it:{}", keyName);
                    }
                } else {
                    wuliuEntCode = "" + get(subList.iterator(), 0).getId();
                }
            } else {
                LOG.error("failed to filter list by key name:{}", keyName);
            }
        }
        return wuliuEntCode;
    }

    public static YouzanLogisticsOnlineConfirmResult confirm(YouzanKdt kdt, YouzanOrder order) {
        YouzanLogisticsOnlineConfirmResult result = null;
        if (nonNull(order) && nonNull(kdt)) {
            Token token = new Token(kdt.getAccessToken());
            YouzanLogisticsOnlineConfirm confirm = new YouzanLogisticsOnlineConfirm();
            YouzanLogisticsOnlineConfirmParams params = new YouzanLogisticsOnlineConfirmParams();
            params.setTid(order.getTid());
            params.setOutStype(order.getSearchValue());
            params.setOutSid(order.getWayBillNo());
            confirm.setAPIParams(params);
            try {
                logYzSdkObj("confirm", params);
                result = YZ_CLIENT.invoke(confirm, token, YouzanLogisticsOnlineConfirmResult.class);
            } catch (SDKException ex) {
                LOG.error("failed to confirm clear result:" + ex.getMessage());
            }
        }
        return result;
    }

    /**
     * 按照折扣后价格计算税金；折扣后总价-税金后计算商品价格
     *
     * @param totalFeeBeforeDiscount
     * @param feePerOneAfterDiscount
     * @param qty
     * @param taxRate
     * @return
     */
    public static Map<String, BigDecimal> priceCaculateFst(final BigDecimal totalFeeBeforeDiscount, final BigDecimal feePerOneAfterDiscount, final BigDecimal qty, final BigDecimal taxRate) {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        if (nonNull(totalFeeBeforeDiscount) && nonNull(feePerOneAfterDiscount) && nonNull(qty) && nonNull(taxRate)) {
            //折扣后含税商品总价=折扣后含税商品单价*商品数量
            BigDecimal totalFeeAfterDiscount = feePerOneAfterDiscount.multiply(qty);
            priceMap.put("actualPaid", totalFeeAfterDiscount);
            //折扣=折扣前商品总价-折扣后商品总价
            priceMap.put("discount", totalFeeBeforeDiscount.subtract(totalFeeAfterDiscount));
            //折扣后不含税商品总价 * (1+税率) =折扣后含税商品总价
            BigDecimal factor = BigDecimal.ONE.add(taxRate);
            BigDecimal totalPriceNoTaxAfterDiscount = totalFeeAfterDiscount.divide(factor, 4, ROUND_HALF_DOWN);
            //税金=折扣后含税商品总价-折扣后不含税商品总价
            BigDecimal totalTax = totalFeeAfterDiscount.subtract(totalPriceNoTaxAfterDiscount);
            priceMap.put("tax", totalTax);
            //折扣前商品总价=折扣前总价-税金
            BigDecimal totalPriceNoTax = totalFeeBeforeDiscount.subtract(totalTax);
            priceMap.put("totalPrice", totalPriceNoTax);
            //每件商品单价=折扣前商品总价/商品数量
            BigDecimal perOnePriceNoTax = totalPriceNoTax.divide(qty, 4, ROUND_HALF_DOWN);
            priceMap.put("perOnePrice", perOnePriceNoTax);
        }
        return priceMap;
    }

    /**
     * 按照折扣前价格计算税金；折扣前总价-税金后计算商品价格
     *
     * @param totalFeeBeforeDiscount
     * @param feePerOneAfterDiscount
     * @param qty
     * @param taxRate
     * @return
     */
    public static Map<String, BigDecimal> priceCaculateScd(final BigDecimal totalFeeBeforeDiscount, final BigDecimal feePerOneAfterDiscount, final BigDecimal qty, final BigDecimal taxRate) {
        Map<String, BigDecimal> priceMap = new HashMap<>();
        if (nonNull(totalFeeBeforeDiscount) && nonNull(feePerOneAfterDiscount) && nonNull(qty) && nonNull(taxRate)) {
            //折扣后含税总价=折扣后含税商品单价*商品数量
            BigDecimal totalFeeAfterDiscount = feePerOneAfterDiscount.multiply(qty);
            priceMap.put("actualPaid", totalFeeAfterDiscount);
            //折扣=折扣前商品总价-折扣后商品总价
            priceMap.put("discount", totalFeeBeforeDiscount.subtract(totalFeeAfterDiscount));
            //折扣前不含税商品总价 * (1+税率) =折扣前含税商品总价
            BigDecimal factor = BigDecimal.ONE.add(taxRate);
            BigDecimal totalPriceNoTaxBeforDiscount = totalFeeBeforeDiscount.divide(factor, 4, ROUND_HALF_DOWN);
            priceMap.put("totalPrice", totalPriceNoTaxBeforDiscount);
            //税金=折扣前含税商品总价-折扣前商品总价
            BigDecimal totalTax = totalFeeBeforeDiscount.subtract(totalPriceNoTaxBeforDiscount);
            priceMap.put("tax", totalTax);
            //每件商品单价=折扣前商品总价/商品数量
            BigDecimal perOnePriceNoTaxBeforeDiscount = totalPriceNoTaxBeforDiscount.divide(qty, 4, ROUND_HALF_DOWN);
            priceMap.put("perOnePrice", perOnePriceNoTaxBeforeDiscount);
        }
        return priceMap;
    }

    public static boolean isSeperatedOrder(Collection<String> orderNos) {
        boolean isSeperated = false;
        if (isNotEmpty(orderNos)) {
            orderNos.removeAll(Collections.singleton(null));
            Set<String> orderNoSet = new HashSet<>(orderNos);
            if (size(orderNoSet) > 1) {
                LOG.info("the request is seperated order, may send to diffent port!");
                isSeperated = true;
            }
        } else {
            LOG.error("can't get order no list in request");
        }
        return isSeperated;
    }
}
