/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static com.ruoyi.common.utils.StringUtils.isNumeric;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 *
 * @author wmao
 */
public enum Currency {

    AFG("101", "阿富汗", "Afghanistan"),
    BHR("102", "巴林", "Bahrian"),
    BGD("103", "孟加拉国", "Bangladesh"),
    BTN("104", "不丹", "Bhutan"),
    BRN("105", "文莱", "Brunei"),
    MMR("106", "缅甸", "Myanmar"),
    KHM("107", "柬埔寨", "Cambodia"),
    CYP("108", "塞浦路斯", "Cyprus"),
    PRK("109", "朝鲜", "Korea,DPR"),
    HKG("110", "中国香港", "Hong Kong"),
    IND("111", "印度", "India"),
    IDN("112", "印度尼西亚", "Indonesia"),
    IRN("113", "伊朗", "Iran"),
    IRQ("114", "伊拉克", "Iraq"),
    ISR("115", "以色列", "Israel"),
    JPN("116", "日本", "Japan"),
    JOR("117", "约旦", "Jordan"),
    KWT("118", "科威特", "Kuwait"),
    LAO("119", "老挝", "Laos,PDR"),
    LBN("120", "黎巴嫩", "Lebanon"),
    MAC("121", "中国澳门", "Macau"),
    MYS("122", "马来西亚", "Malaysia"),
    MDV("123", "马尔代夫", "Maldives"),
    MNG("124", "蒙古", "Mongolia"),
    NPL("125", "尼泊尔", "Nepal"),
    OMN("126", "阿曼", "Oman"),
    PAK("127", "巴基斯坦", "Pakistan"),
    PSE("128", "巴勒斯坦", "Palestine"),
    PHL("129", "菲律宾", "Philippines"),
    QAT("130", "卡塔尔", "Qatar"),
    SAU("131", "沙特阿拉伯", "Saudi Arabia"),
    SGP("132", "新加坡", "Singapore"),
    KOR("133", "韩国", "Korea Rep."),
    LKA("134", "斯里兰卡", "Sri Lanka"),
    SYR("135", "叙利亚", "Syrian"),
    THA("136", "泰国", "Thailand"),
    TUR("137", "土耳其", "Turkey"),
    ARE("138", "阿联酋", "United Arab Emirates"),
    YEM("139", "也门共和国", "Republic of Yemen"),
    NVM("141", "越南", "Vietnam"),
    CNY("142", "中国", "China"),
    TWN("143", "台澎金马关税区", "Taiwan prov."),
    TLS("144", "东帝汶", "East Timor"),
    KAZ("145", "哈萨克斯坦", "Kazakhstan"),
    KGZ("146", "吉尔吉斯斯坦", "Kirghizia"),
    TJK("147", "塔吉克斯坦", "Tadzhikistan"),
    TKM("148", "土库曼斯坦", "Turkmenistan"),
    UZB("149", "乌兹别克斯坦", "Uzbekstan"),
    YZQ("199", "亚洲其他国家(地区)", "Oth. Asia nes"),
    DZA("201", "阿尔及利亚", "Algeria"),
    AGO("202", "安哥拉", "Angora"),
    BEN("203", "贝宁", "Benin"),
    BWA("204", "博茨瓦那", "Botswana"),
    BDI("205", "布隆迪", "Burundi"),
    CMR("206", "喀麦隆", "Cameroon"),
    JNL("207", "加那利群岛", "Canary Is"),
    CPV("208", "佛得角", "Cape Vrde"),
    CAF("209", "中非共和国", "Central African Rep."),
    SBT("210", "塞卜泰", "Ceuta"),
    TCD("211", "乍得", "Chad"),
    COM("212", "科摩罗", "Comoros"),
    COG("213", "刚果", "Congo"),
    DJI("214", "吉布提", "Djibouti"),
    EGY("215", "埃及", "Egypt"),
    GNQ("216", "赤道几内亚", "Eq.Guinea"),
    ETH("217", "埃塞俄比亚", "Ethiopia"),
    GAB("218", "加蓬", "Gabon"),
    GMB("219", "冈比亚", "Gambia"),
    GHA("220", "加纳", "Ghana"),
    GIN("221", "几内亚", "Guinea"),
    GNB("222", "几内亚(比绍)", "Guinea Bissau"),
    CIV("223", "科特迪瓦", "Cote d’lvoir"),
    KEN("224", "肯尼亚", "Kenya"),
    LBR("225", "利比里亚", "Liberia"),
    LBY("226", "利比亚", "Libyan Arab Jm"),
    MDG("227", "马达加斯加", "Madagascar"),
    MWI("228", "马拉维", "Malawi"),
    MLI("229", "马里", "Mali"),
    MRT("230", "毛里塔尼亚", "Mauritania"),
    MUS("231", "毛里求斯", "Mauritius"),
    MAR("232", "摩洛哥", "Morocco"),
    MOZ("233", "莫桑比克", "Mozambique"),
    NAM("234", "纳米比亚", "Namibia"),
    NER("235", "尼日尔", "Niger"),
    NGA("236", "尼日利亚", "Nigeria"),
    REU("237", "留尼汪", "Reunion"),
    RWA("238", "卢旺达", "Rwanda"),
    STP("239", "圣多美和普林西比", "Sao Tome & Principe"),
    SEN("240", "塞内加尔", "Senegal"),
    SYC("241", "塞舌尔", "Seychelles"),
    SLE("242", "塞拉利昂", "Sierra Leone"),
    SOM("243", "索马里", "Somalia"),
    ZAF("244", "南非", "S.Africa"),
    ESH("245", "西撒哈拉", "Western Sahara"),
    SDN("246", "苏丹", "Sudan"),
    TZA("247", "坦桑尼亚", "Tanzania"),
    TGO("248", "多哥", "Togo"),
    TUN("249", "突尼斯", "Tunisia"),
    UGA("250", "乌干达", "Uganda"),
    BFA("251", "布基纳法索", "Burkina Faso"),
    COD("252", "民主刚果", "Congo,DR"),
    ZMB("253", "赞比亚", "Zambia"),
    ZWE("254", "津巴布韦", "Zimbabwe"),
    LSO("255", "莱索托", "Lesotho"),
    MLL("256", "梅利利亚", "Melilla"),
    SWZ("257", "斯威士兰", "Swaziland"),
    ERI("258", "厄立特里亚", "Eritrea"),
    MYT("259", "马约特岛", "Mayotte"),
    OTH("299", "非洲其他国家(地区)", "Oth. Afr. Nes"),
    BEL("301", "比利时", "Belgium"),
    DNK("302", "丹麦", "Denmark"),
    GBR("303", "英国", "United Kingdom"),
    DEU("304", "德国", "Germany"),
    FRA("305", "法国", "France"),
    IRL("306", "爱尔兰", "Ireland"),
    ITA("307", "意大利", "Italy"),
    LUX("308", "卢森堡", "Luxembourg"),
    NLD("309", "荷兰", "Netherlands"),
    GRC("310", "希腊", "Greece"),
    PRT("311", "葡萄牙", "Portugal"),
    ESP("312", "西班牙", "Spain"),
    ALB("313", "阿尔巴尼亚", "Albania"),
    AND("314", "安道尔", "Andorra"),
    AUT("315", "奥地利", "Austria"),
    BGR("316", "保加利亚", "Bulgaria"),
    FIN("318", "芬兰", "Finland"),
    CIB("320", "直布罗陀", "Gibraltar"),
    HUN("321", "匈牙利", "Hungary"),
    ISL("322", "冰岛", "Iceland"),
    LIE("323", "列支敦士登", "Liechtenstein"),
    MLT("324", "马耳他", "Malta"),
    MCO("325", "摩纳哥", "Monaco"),
    NOR("326", "挪威", "Norway"),
    POL("327", "波兰", "Poland"),
    ROM("328", "罗马尼亚", "Romania"),
    SMR("329", "圣马力诺", "San Marino"),
    SWE("330", "瑞典", "Sweden"),
    CHE("331", "瑞士", "Switzerland"),
    EST("334", "爱沙尼亚", "Estonia"),
    LVA("335", "拉脱维亚", "Latvia"),
    LTU("336", "立陶宛", "Lithuania"),
    GEO("337", "格鲁吉亚", "Georgia"),
    ARM("338", "亚美尼亚", "Armenia"),
    AZE("339", "阿塞拜疆", "Azerbaijan"),
    BLR("340", "白俄罗斯", "Byelorussia"),
    MDA("343", "摩尔多瓦", "Moldavia"),
    RUS("344", "俄罗斯联邦", "Russia"),
    UKR("347", "乌克兰", "Ukraine"),
    SEH("349", "塞尔维亚和黑山", "Serbia and Montenegro"),
    SVN("350", "斯洛文尼亚", "Slovenia Rep"),
    HRV("351", "克罗地亚", "Croatia Rep"),
    CZE("352", "捷克共和国", "Czech Rep"),
    SVK("353", "斯洛伐克", "Slovak Rep"),
    MKD("354", "马其顿", "Macedonia Rep"),
    BIH("355", "波斯尼亚-黑塞哥维那共和", "Bosnia&Hercegovina"),
    VAT("356", "梵蒂冈城国", "Vatican City State"),
    FRO("357", "法罗群岛", "the Faroe Islands"),
    SRB("358", "塞尔维亚", "Sefbia"),
    MNE("359", "黑山", "Montengro"),
    OEN("399", "欧洲其他国家(地区)", "Oth. Eur. Nes"),
    ATG("401", "安提瓜和巴布达", "Antigua & Barbuda"),
    ARG("402", "阿根廷", "Argentina"),
    ABW("403", "阿鲁巴岛", "Aruba"),
    BHS("404", "巴哈马", "Bahamas"),
    BRB("405", "巴巴多斯", "Barbados"),
    BLZ("406", "伯利兹", "Belize"),
    BOL("408", "玻利维亚", "Bolivia"),
    BIR("409", "博内尔", "Bonaire"),
    BRA("410", "巴西", "Brazil"),
    CYM("411", "开曼群岛", "Cayman Is"),
    CHL("412", "智利", "Chile"),
    COL("413", "哥伦比亚", "Colombia"),
    DMA("414", "多米尼亚共和国", "Dominica"),
    CRI("415", "哥斯达黎加", "Costa Rica"),
    CUB("416", "古巴", "Cuba"),
    CUR("417", "库腊索岛", "Curacao"),
    DOM("418", "多米尼加共和国", "Dominican Rep."),
    ECU("419", "厄瓜多尔", "Ecuador"),
    GUF("420", "法属圭亚那", "French Guyana"),
    GRD("421", "格林纳达", "Grenada"),
    GLP("422", "瓜德罗普", "Guadeloupe"),
    GTM("423", "危地马拉", "Guatemala"),
    GUY("424", "圭亚那", "Guyana"),
    HTI("425", "海地", "Haiti"),
    HND("426", "洪都拉斯", "Honduras"),
    JAM("427", "牙买加", "Jamaica"),
    MTQ("428", "马提尼克", "Martinique"),
    MEX("429", "墨西哥", "Mexico"),
    MTS("430", "蒙特塞拉特", "Montserrat"),
    NIC("431", "尼加拉瓜", "Nicaragua"),
    PAN("432", "巴拿马", "Panama"),
    PRY("433", "巴拉圭", "Paraguay"),
    PER("434", "秘鲁", "Peru"),
    PRI("435", "波多黎各", "Puerto Rico"),
    SBA("436", "萨巴", "Saba"),
    LCA("437", "圣卢西亚", "Saint Lucia"),
    SMI("438", "圣马丁岛", "Saint Martin Is"),
    VCT("439", "圣文森特和格林纳丁斯", "Saint Vincent & Grenadines"),
    SLV("440", "萨尔瓦多", "El Salvador"),
    SUR("441", "苏里南", "Suriname"),
    TTO("442", "特立尼达和多巴哥", "Trinidad & Tobago"),
    TCA("443", "特克斯和凯科斯群岛", "Turks & Caicos Is"),
    URY("444", "乌拉圭", "Uruguay"),
    VEN("445", "委内瑞拉", "Venezuela"),
    VGB("446", "英属维尔京群岛", "Br. Virgin Is"),
    KNA("447", "圣其茨-尼维斯", "St. Kitts-Nevis"),
    SPM("448", "圣皮埃尔和密克隆", "St.Pierre and Miquelon"),
    ANT("449", "荷属安地列斯群岛", "the Netherlands Antilles"),
    LAN("499", "拉丁美洲其他国家(地区)", "Oth. L.Amer. Nes"),
    CAN("501", "加拿大", "Canada"),
    USA("502", "美国", "United States"),
    GRL("503", "格陵兰", "Greenland"),
    BMU("504", "百慕大", "Bermuda"),
    NAN("599", "北美洲其他国家(地区)", "Oth. N.Amer. Nes"),
    AUS("601", "澳大利亚", "Australia"),
    COK("602", "库克群岛", "Cook Is"),
    FJI("603", "斐济", "Fiji"),
    GAI("604", "盖比群岛", "Gambier Is"),
    MKS("605", "马克萨斯群岛", "Marquesas Is"),
    NRU("606", "瑙鲁", "Nauru"),
    NCL("607", "新喀里多尼亚", "New Caledonia"),
    VUT("608", "瓦努阿图", "Vanuatu"),
    NZL("609", "新西兰", "New Zealand"),
    NFK("610", "诺福克岛", "Norfolk Is"),
    PNG("611", "巴布亚新几内亚", "Papua New Guinea"),
    SOC("612", "社会群岛", "Society Is"),
    SLB("613", "所罗门群岛", "Solomon Is"),
    TON("614", "汤加", "Tonga"),
    TUM("615", "土阿莫土群岛", "Tuamotu Is"),
    TUB("616", "土布艾群岛", "Tubai Is"),
    WSM("617", "萨摩亚", "Samoa"),
    KIR("618", "基里巴斯", "Kiribati"),
    TUV("619", "图瓦卢", "Tuvalu"),
    FSM("620", "密克罗尼西亚联邦", "Micronesia Fs"),
    MHL("621", "马绍尔群岛", "Marshall Is Rep"),
    PLW("622", "帕劳共和国", "Palau"),
    PYF("623", "法属波利尼西亚", "French Polynesia"),
    WLF("625", "瓦利斯和浮图纳", "Wallis and Futuna"),
    OON("699", "大洋洲其他国家(地区)", "Oth. Ocean. Nes"),
    CUN("701", "国(地)别不详的", "Countries(reg.) unknown"),
    UNA("702", "联合国及机构和国际组织", "UN and other interational"),
    CNP("999", "中性包装原产国别", "Conutries of Neutral Package");

    private String key;
    private String cnName;
    private String enName;

    Currency(String key, String cnName, String enName) {
        this.key = key;
        this.cnName = cnName;
        this.enName = enName;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the cnName
     */
    public String getCnName() {
        return cnName;
    }

    /**
     * @param cnName the cnName to set
     */
    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    /**
     * @return the enName
     */
    public String getEnName() {
        return enName;
    }

    /**
     * @param enName the enName to set
     */
    public void setEnName(String enName) {
        this.enName = enName;
    }

    public static String getKeyByCnName(String cnName) {
        String key = null;
        if (isNotBlank(cnName) && !isNumeric(cnName)) {
            Currency[] currencies = Currency.values();
            if (isNotEmpty(currencies)) {
                for (Currency currency : currencies) {
                    if (equalsIgnoreCase(currency.getCnName(), cnName)) {
                        key = currency.getKey();
                        break;
                    }
                }
            }
        }
        return isBlank(key) ? cnName : key;
    }

    public static String[] keys() {
        String[] keys = null;
        Currency[] currencies = Currency.values();
        if (isNotEmpty(currencies)) {
            keys = new String[currencies.length];
            int idx = 0;
            for (Currency currency : currencies) {
                keys[idx] = currency.getKey();
                idx++;
            }
        }
        return keys;
    }

    public static String[] cnNames() {
        String[] cnNames = null;
        Currency[] currencies = Currency.values();
        if (isNotEmpty(currencies)) {
            cnNames = new String[currencies.length];
            int idx = 0;
            for (Currency currency : currencies) {
                cnNames[idx] = currency.getCnName();
                idx++;
            }
        }
        return cnNames;
    }

}
