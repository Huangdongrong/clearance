/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.utils;

import static com.ruoyi.common.utils.StringUtils.nullToEmpty;
import static com.ruoyi.common.utils.xml.XmlUtil.convertXmlStrToObject;
import static com.ruoyi.common.utils.security.Base64Util.safeUrlBase64Encode;
import static com.ruoyi.common.utils.security.Md5Utils.hashYunda;
import static com.ruoyi.common.utils.xml.XmlUtil.unformat;
import static com.ruoyi.yz.cnst.Const.PACKAGE_WEIGHT;
import static com.ruoyi.yz.cnst.Const.YUNDA_GOOD_NAME_MAX_LENGTH;
import com.ruoyi.yz.config.YundaKjProperties;
import com.ruoyi.yz.customs.order.CEB311Message;
import com.ruoyi.yz.customs.order.Order;
import com.ruoyi.yz.customs.order.OrderHead;
import com.ruoyi.yz.customs.order.OrderList;
import com.ruoyi.yz.domain.CustomsPlat;
import com.ruoyi.yz.domain.WuliuKjPlat;
import com.ruoyi.yz.domain.YouzanOrder;
import static com.ruoyi.yz.enums.Currency.CNY;
import static com.ruoyi.yz.enums.YdWuliuType.GSS;
import static com.ruoyi.yz.enums.YundaApplyType.BEIHUO;
import static com.ruoyi.yz.enums.YundaReqType.CREATE;
import static com.ruoyi.yz.enums.YundaReqType.QUERY;
import static com.ruoyi.yz.utils.WuliuUtil.assembleUrl;
import static com.ruoyi.yz.utils.WuliuUtil.generateKjHeader;
import com.ruoyi.yz.wuliu.Receiver;
import com.ruoyi.yz.wuliu.Sender;
import com.ruoyi.yz.wuliu.ydkj.YdGoods;
import com.ruoyi.yz.wuliu.ydkj.YdGoodsList;
import com.ruoyi.yz.wuliu.ydkj.YdReceiver;
import com.ruoyi.yz.wuliu.ydkj.YdSender;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyBody;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyHead;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyItem;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyReceiver;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyRequest;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyResponses;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplySender;
import com.ruoyi.yz.wuliu.ydkj.apply.YdApplyShip;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateHawb;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateHawbs;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateRequest;
import com.ruoyi.yz.wuliu.ydkj.create.YdCreateResponses;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryHawb;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryHawbs;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryRequest;
import com.ruoyi.yz.wuliu.ydkj.query.YdQueryResponses;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateRequest;
import com.ruoyi.yz.wuliu.ydkj.update.YdUpdateResponses;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_DOWN;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.length;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import static com.ruoyi.common.utils.xml.XmlUtil.unescapeConvertToXml;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 *
 * @author wmao
 */
public final class YundaUtil {

    private static final Logger LOG = LoggerFactory.getLogger(YundaUtil.class);

    private static final String VER = "1.0";

    private static final String DATA_STYLE = "xml";

    private static final String BUZ_TYPE = "clientShip";

    public static YdQueryRequest assembleYdQueryReq(String mailNo) {
        YdQueryRequest request = null;
        if (isNotBlank(mailNo)) {
            request = new YdQueryRequest();
            request.setReqType(QUERY.getValue());
            YdQueryHawbs hawbs = new YdQueryHawbs();
            List<YdQueryHawb> hawbList = new ArrayList<>();
            YdQueryHawb hawb = new YdQueryHawb();
            hawb.setMailNo(mailNo);
            hawbList.add(hawb);
            hawbs.setHawb(hawbList);
            request.setHawbs(hawbs);
        }
        return request;
    }

    public static YdApplyRequest assembleYdApplyReq(YouzanOrder order, Sender sender, String accoutNo) {
        YdApplyRequest req = null;
        if (nonNull(order) && nonNull(sender) && isNotBlank(accoutNo)) {
            req = new YdApplyRequest();
            YdApplyHead head = new YdApplyHead();
            head.setBuztype(BUZ_TYPE);
            head.setVersion(VER);
            req.setHead(head);
            YdApplyBody body = new YdApplyBody();
            body.setLogisticsId("W00001");
            body.setCustomsId("");
            body.setApplyType(BEIHUO.getKey());
            List<YdApplyShip> shipList = new ArrayList<>();
            YdApplyShip ship = new YdApplyShip();
            ship.setClientShip(order.getWayBillNo());
            String goodName = "";
            CEB311Message message = (CEB311Message) order.getBody();

            if (nonNull(message)) {
                Order od = message.getOrder();
                if (nonNull(od)) {
                    List<OrderList> ols = od.getOrderList();
                    if (isNotEmpty(ols)) {
                        List<YdApplyItem> itemList = new ArrayList<>();
                        for (OrderList ol : ols) {
                            YdApplyItem itm = new YdApplyItem();
                            int goodNameLen = length(goodName);
                            int itemNameLen = length(ol.getItemName());
                            if ((goodNameLen + itemNameLen) < YUNDA_GOOD_NAME_MAX_LENGTH) {
                                goodName += ol.getItemName() + "|";
                            }
                            itm.setProductCode(ol.getItemNo());
                            itm.setProductName(ol.getItemName());
                            itm.setProductQutity(ol.getQty());
                            itemList.add(itm);
                        }
                        ship.setItem(itemList);
                    }

                    OrderHead header = od.getOrderHead();
                    if (nonNull(header)) {
                        ship.setTotalCount(header.getQty());
                        ship.setTotalWeight(PACKAGE_WEIGHT.add(header.getWeight()).setScale(4, ROUND_HALF_DOWN));
                        Receiver receiver = header.getReceiver();
                        if (nonNull(receiver)) {
                            YdApplyReceiver recv = new YdApplyReceiver();
                            recv.setReceiverName(receiver.getName());
                            recv.setReceiverAddress(header.getConsigneeAddress());
                            recv.setReCardType(header.getBuyerIdType());
                            recv.setReCardNo(header.getBuyerIdNumber());
                            recv.setReCountryCode(CNY.getKey());
                            recv.setReCityCode(CNY.getKey());
                            recv.setReceiverEamil(receiver.getEmail());
                            recv.setReceiverTel(receiver.getMobile());
                            ship.setReceiver(recv);
                        }
                    }
                }
            }
            ship.setGoodsName(goodName);
            ship.setOrderList(order.getOrderNo());
            ship.setPreExpress(order.getWayBillNo());
            ship.setTotalFees(BigDecimal.ONE);

            if (nonNull(sender)) {
                YdApplySender sdr = new YdApplySender();
                sdr.setSenderName(sender.getName());
                sdr.setSenderAddress(sender.getProvince() + sender.getCity() + sender.getArea() + sender.getAddress());
                sdr.setSdCityCode(CNY.getKey());
                sdr.setSdCountryCode(CNY.getKey());
                sdr.setSenderEamil(sender.getEmail());
                sdr.setSenderCity(sender.getCity());
                sdr.setSenderTel(sender.getMobile());
                ship.setSender(sdr);
            }
            shipList.add(ship);
            body.setShip(shipList);
            body.setSourceFlag(accoutNo);
            req.setBody(body);
        }
        return req;
    }

    public static YdCreateRequest assembleYdCreateReq(YouzanOrder order, Sender sender) {
        YdCreateRequest request = null;
        if (nonNull(order) && nonNull(sender)) {
            request = new YdCreateRequest();
            request.setReqType(CREATE.getValue());
            YdCreateHawbs hawbs = new YdCreateHawbs();
            List<YdCreateHawb> hawbList = new ArrayList<>();
            CEB311Message message = (CEB311Message) order.getBody();
            if (nonNull(message)) {
                YdCreateHawb hawb = new YdCreateHawb();
                hawb.setHawbno(order.getOrderNo());
                hawb.setPtype(GSS.name());
                hawb.setPiece(1);
                hawb.setWeight(new BigDecimal(0.6).setScale(2, ROUND_HALF_DOWN));
                hawb.setVolumeWeight(new BigDecimal(0.5).setScale(2, ROUND_HALF_DOWN));
                hawb.setFcountry("CHN");
                hawb.setTcountry("CHN");
                hawb.setGoodsMoney(isNotBlank(order.getAmount()) ? new BigDecimal(order.getAmount()).setScale(2, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                hawb.setRemark(order.getRemark());
                hawb.setCurrency(CNY.getKey());
                com.ruoyi.yz.customs.order.Order od = message.getOrder();
                if (nonNull(od)) {
                    OrderHead oh = od.getOrderHead();
                    if (nonNull(oh)) {
                        Receiver receiver = oh.getReceiver();
                        if (nonNull(receiver)) {
                            //receiver
                            YdReceiver rcv = new YdReceiver();
                            rcv.setAddress(nullToEmpty(receiver.getArea()) + nullToEmpty(receiver.getAddress()));
                            rcv.setCity(nullToEmpty(receiver.getProvince()) + nullToEmpty(receiver.getCity()));
                            rcv.setContacts(receiver.getName());
                            rcv.setEMail(receiver.getEmail());
                            rcv.setPostalCode(receiver.getPostCode());
                            rcv.setRecTele(nullToEmpty(receiver.getMobile()));
                            if (isBlank(rcv.getRecTele())) {
                                rcv.setRecTele(nullToEmpty(receiver.getTel()));
                            }
                            hawb.setReceiver(rcv);
                        }

                        hawb.setFreight(nonNull(oh.getFreight()) ? oh.getFreight().setScale(2, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                        hawb.setInsuranceFee(nonNull(oh.getTaxTotal()) ? oh.getTaxTotal().setScale(2, ROUND_HALF_DOWN) : BigDecimal.ZERO);
                    } else {
                        LOG.warn("order head is empty, it's weired!");
                    }

                    List<OrderList> ols = od.getOrderList();
                    if (isNotEmpty(ols)) {
                        YdGoodsList goodsList = new YdGoodsList();
                        List<YdGoods> goods = new ArrayList<>();
                        ols.forEach((ol) -> {
                            YdGoods gd = new YdGoods();
                            if (nonNull(ol)) {
                                gd.setName(ol.getItemName());
                                gd.setUnitPrice(ol.getTotalPrice());
                                gd.setActWeight(new BigDecimal(0.5).setScale(2, ROUND_HALF_DOWN));
                                gd.setDimWeight(new BigDecimal(0.4).setScale(2, ROUND_HALF_DOWN));
                                gd.setQuantity(nonNull(ol.getQty()) ? ol.getQty().intValue() : 1);
                            }
                            goods.add(gd);
                        });
                        goodsList.setGoods(goods);
                        hawb.setGoodsList(goodsList);
                    }
                } else {
                    LOG.warn("order in message is empty, it's weired!");
                }
                //sender
                YdSender sd = new YdSender();
                sd.setAddress(nullToEmpty(sender.getArea()) + nullToEmpty(sender.getAddress()));
                sd.setCity(nullToEmpty(sender.getProvince()) + nullToEmpty(sender.getCity()));
                sd.setContacts(sender.getName());
                sd.setEMail(sender.getEmail());
                sd.setPostalCode(sender.getPostCode());
                sd.setSenderTele(nullToEmpty(sender.getMobile()));
                if (isBlank(sd.getSenderTele())) {
                    sd.setSenderTele(nullToEmpty(sender.getTel()));
                }
                hawb.setSender(sd);
                hawbList.add(hawb);
            }
            hawbs.setHawb(hawbList);
            request.setHawbs(hawbs);
        }
        return request;
    }

    private static String genValidation(WuliuKjPlat plat) {
        String validation = null;
        if (nonNull(plat)) {
            String tradeId = trim(plat.getSiteCode());
            String passwd = trim(plat.getSecretKey());
            String buzType = trim(plat.getPartnerCode());
            StringBuilder sb = new StringBuilder();
            sb.append("buz_type").append(buzType);
            sb.append("data_style").append(DATA_STYLE);
            sb.append("traderId").append(tradeId);
            sb.append("version").append(VER);
            sb.append(passwd);
            validation = lowerCase(hashYunda(sb.toString()));
        }
        return validation;
    }

    private static String genValidation(CustomsPlat plat, String data) {
        String validation = null;
        if (nonNull(plat)) {
            String accountNo = trim(plat.getCopCode());
            String passwd = trim(plat.getDxpId());
            StringBuilder sb = new StringBuilder();
            sb.append(accountNo).append(passwd).append(data);
            validation = lowerCase(hashYunda(sb.toString()));
        }
        return validation;
    }

    private static String genUrl(WuliuKjPlat plat, YundaKjProperties props) {
        return genUrl(plat.getUrl(), props);
    }

    private static String genUrl(String url, YundaKjProperties props) {
        if (isNotBlank(url)) {
            StringBuilder urlSb = new StringBuilder(trim(url));
            urlSb.append(props.getPort() > 0 ? ":" + props.getPort() : "");
            url = assembleUrl(urlSb.toString(), (isNotBlank(props.getPath()) ? trim(props.getPath()) : ""));
        }
        return url;
    }

    public static YdApplyResponses applyReq(YdApplyRequest request, RestTemplate template, CustomsPlat plat, YundaKjProperties props) {
        YdApplyResponses resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            try {
                String data = trim(unescapeConvertToXml(request));
                String base64Data = safeUrlBase64Encode(unformat(data).getBytes("UTF-8"));
                String validation = genValidation(plat, base64Data);
                String url = genUrl(plat.getUrl(), props);
//                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                        .queryParam("accountNo", plat.getCopCode())
//                        .queryParam("passWord", plat.getDxpId())
//                        .queryParam("version", VER)
//                        .queryParam("buzType", "logic")
//                        .queryParam("dataType", DATA_STYLE)
//                        .queryParam("data", base64Data)
//                        .queryParam("validation", validation);
                
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("accountNo", plat.getCopCode());
                params.add("passWord", plat.getDxpId());
                params.add("version", VER);
                params.add("buzType", "logic");
                params.add("dataType", DATA_STYLE);
                params.add("data", base64Data);
                params.add("validation", validation);
                HttpHeaders headers = generateKjHeader();
                //headers.setContentType(new MediaType("text", DATA_STYLE, Charset.forName("UTF-8")));
                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity<>(params, headers);
                //ResponseEntity<String> response = template.exchange(url, POST, new HttpEntity<>(headers), String.class);
                ResponseEntity<String> response = template.exchange(url, POST, entity, String.class);
                LOG.info("url:{}, data:{}, validation:{}, response:{}", url, data, validation, response);
                if (nonNull(response)) {
                    String respBody = response.getBody();
                    LOG.info("response body is:{}", respBody);
                    if (isNotBlank(respBody)) {
                        resp = convertXmlStrToObject(YdApplyResponses.class, respBody);
                    } else {
                        LOG.error("failed to get create response from yunda platform:{}", request);
                    }
                } else {
                    LOG.error("response is null");
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.error("failed to base64 request:{}", ex.getMessage());
            }
        }
        return resp;
    }

    public static YdCreateResponses sendReq(YdCreateRequest request, RestTemplate template, WuliuKjPlat plat, YundaKjProperties props) {
        YdCreateResponses resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            String validation = genValidation(plat);
            String url = genUrl(plat, props);
            try {
                /*UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                        .queryParam("traderId", plat.getSiteCode())
                        .queryParam("buz_type", plat.getPartnerCode())
                        .queryParam("method", "global_order_create")
                        .queryParam("validation", validation)
                        .queryParam("version", VER)
                        .queryParam("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")))
                        .queryParam("data_style", DATA_STYLE);*/
                
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("traderId", plat.getSiteCode());
                params.add("buz_type", plat.getPartnerCode());
                params.add("method", "global_order_create");
                params.add("validation", validation);
                params.add("version", VER);
                params.add("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")));
                params.add("data_style", DATA_STYLE);
                
                HttpHeaders headers = generateKjHeader();
                //headers.setContentType(new MediaType("text", "xml", Charset.forName("UTF-8")));
                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity<>(params, headers);
                
                //ResponseEntity<String> response = template.exchange(url, POST, new HttpEntity<>(headers), String.class);
                ResponseEntity<String> response = template.exchange(url, POST, entity, String.class);
                LOG.info("response:{}", response);
                if (nonNull(response)) {
                    String respBody = response.getBody();
                    LOG.info("response body is:{}", respBody);
                    if (isNotBlank(respBody)) {
                        resp = convertXmlStrToObject(YdCreateResponses.class, respBody);
                    } else {
                        LOG.error("failed to get create response from yunda platform:{}", request);
                    }
                } else {
                    LOG.error("response is null");
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.error("failed to base64 request:{}", ex.getMessage());
            }
        }
        return resp;
    }

    public static YdUpdateResponses updateReq(YdUpdateRequest request, RestTemplate template, WuliuKjPlat plat, YundaKjProperties props) {
        YdUpdateResponses resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            String validation = genValidation(plat);
            String url = genUrl(plat, props);
            try {
//                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                        .queryParam("traderId", plat.getSiteCode())
//                        .queryParam("buz_type", plat.getPartnerCode())
//                        .queryParam("validation", validation)
//                        .queryParam("version", VER)
//                        .queryParam("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")))
//                        .queryParam("data_style", DATA_STYLE);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("traderId", plat.getSiteCode());
                params.add("buz_type", plat.getPartnerCode());
                params.add("validation", validation);
                params.add("version", VER);
                params.add("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")));
                params.add("data_style", DATA_STYLE);
                HttpHeaders headers = generateKjHeader();
                //headers.setContentType(new MediaType("text", "xml", Charset.forName("UTF-8")));                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity<>(params, headers);
                
                //ResponseEntity<String> response = template.exchange(url, POST, new HttpEntity<>(headers), String.class);
                ResponseEntity<String> response = template.exchange(url, POST, entity, String.class);
                LOG.info("response:{}", response);
                if (nonNull(response)) {
                    String respBody = response.getBody();
                    LOG.info("response body is:{}", respBody);
                    if (isNotBlank(respBody)) {
                        resp = convertXmlStrToObject(YdUpdateResponses.class, respBody);
                    } else {
                        LOG.error("failed to get update response from yunda platform:{}", request);
                    }
                } else {
                    LOG.error("response is null");
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.error("failed to base64 request:{}", ex.getMessage());
            }
        }
        return resp;
    }

    public static YdQueryResponses trace(YdQueryRequest request, RestTemplate template, WuliuKjPlat plat, YundaKjProperties props) {
        YdQueryResponses resp = null;
        if (nonNull(request) && nonNull(template) && nonNull(plat) && nonNull(props)) {
            LOG.info("trace request:{}", trim(unescapeConvertToXml(request)));
            String validation = genValidation(plat);
            String url = genUrl(plat, props);
            try {
//                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
//                        .queryParam("traderId", plat.getSiteCode())
//                        .queryParam("buz_type", plat.getPartnerCode())
//                        .queryParam("validation", validation)
//                        .queryParam("version", VER)
//                        .queryParam("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")))
//                        .queryParam("data_style", DATA_STYLE);
                MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
                params.add("traderId", plat.getSiteCode());
                params.add("buz_type", plat.getPartnerCode());
                params.add("validation", validation);
                params.add("version", VER);
                params.add("data", safeUrlBase64Encode(unformat(trim(unescapeConvertToXml(request))).getBytes("UTF-8")));
                params.add("data_style", DATA_STYLE);
                
                HttpHeaders headers = generateKjHeader();
                //headers.setContentType(new MediaType("text", "xml", Charset.forName("UTF-8")));                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                headers.setContentType(new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));
                HttpEntity entity = new HttpEntity<>(params, headers);
                //ResponseEntity<String> response = template.exchange(builder.build().encode().toUri(), POST, new HttpEntity<>(headers), String.class);
                ResponseEntity<String> response = template.exchange(url, POST, entity, String.class);
                LOG.info("response:{}", response);
                if (nonNull(response)) {
                    String respBody = response.getBody();
                    LOG.info("response body is:{}", respBody);
                    if (isNotBlank(respBody)) {
                        resp = convertXmlStrToObject(YdQueryResponses.class, respBody);
                    } else {
                        LOG.error("failed to get query response from yunda platform:{}", request);
                    }
                } else {
                    LOG.error("response is null");
                }
            } catch (UnsupportedEncodingException ex) {
                LOG.error("failed to base64 request:{}", ex.getMessage());
            }
        }
        return resp;
    }
}
