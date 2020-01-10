/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.enums;

import static com.ruoyi.common.utils.StringUtils.isNumeric;
import java.io.Serializable;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author wmao
 */
public enum Unit implements Serializable{
    First("001", "台"),
    Second("002", "座"),
    Third("003", "辆"),
    Four("004", "艘"),
    Five("005", "架"),
    Six("006", "套"),
    Seven("007", "个"),
    Eight("008", "只"),
    Nigh("009", "头"),
    Ten("010", "张"),
    Eleven("011", "件"),
    Twelve("012", "支"),
    Thirteen("013", "枝"),
    Fourteen("014", "根"),
    Fifteen("015", "条"),
    Sixteen("016", "把"),
    Seventeen("017", "块"),
    Eighteen("018", "卷"),
    Nineteen("019", "副"),
    Twenty("020", "片"),
    Twone("021", "组"),
    Twtwo("022", "份"),
    Twthree("023", "幅"),
    Twfive("025", "双"),
    Twsix("026", "对"),
    Twseven("027", "棵"),
    Tweight("028", "株"),
    Twnine("029", "井"),
    Thirty("030", "米"),
    Thone("031", "盘"),
    Thtwo("032", "平方米"),
    Ththree("033", "立方米"),
    Thfour("034", "筒"),
    Thfive("035", "千克"),
    Thsix("036", "克"),
    Thseven("037", "盆"),
    Theight("038", "万个"),
    Thnine("039", "具"),
    Forty("040", "百副"),
    Foone("041", "百支"),
    Fotwo("042", "百把"),
    Fothree("043", "百个"),
    Fofour("044", "百片"),
    Fofive("045", "刀"),
    Fosix("046", "疋"),
    Foseven("047", "公担"),
    Foeight("048", "扇"),
    Fonine("049", "百枝"),
    Fifty("050", "千只"),
    Fione("051", "千块"),
    Fitwo("052", "千盒"),
    Fithree("053", "千枝"),
    Fifour("054", "千个"),
    Fifive("055", "亿支"),
    Fisix("056", "亿个"),
    Fiseven("057", "万套"),
    Fieight("058", "千张"),
    Finine("059", "万张"),
    Sixty("060", "千伏安"),
    Sione("061", "千瓦"),
    Sitwo("062", "千瓦时"),
    Sithree("063", "千升"),
    Siseven("067", "英尺"),
    Seventy("070", "吨"),
    Seone("071", "长吨"),
    Setwo("072", "短吨"),
    Sethree("073", "司马担"),
    Sefour("074", "司马斤"),
    Sefive("075", "斤"),
    Sesix("076", "磅"),
    Seseven("077", "担"),
    Seeight("078", "英担"),
    Senine("079", "短担"),
    Eighty("080", "两"),
    Eione("081", "市担"),
    Eithree("083", "盎司"),
    Eifour("084", "克拉"),
    Eifive("085", "市尺"),
    Eisix("086", "码"),
    Eieight("088", "英寸"),
    Einine("089", "寸"),
    Nifive("095", "升"),
    Nisix("096", "毫升"),
    Niseven("097", "英加仑"),
    Nieight("098", "美加仑"),
    Ninine("099", "立方英尺"),
    Oone("101", "立方尺"),
    Oten("110", "平方码"),
    Oeleven("111", "平方英尺"),
    Otwelve("112", "平方尺"),
    Ofifteen("115", "英制马力"),
    Osixteen("116", "公制马力"),
    Oeighteen("118", "令"),
    Otwy("120", "箱"),
    Otwo("121", "批"),
    Otwt("122", "罐"),
    Otwh("123", "桶"),
    Otwr("124", "扎"),
    Otwf("125", "包"),
    Otws("126", "箩"),
    Otwv("127", "打"),
    Otwe("128", "筐"),
    Otwn("129", "罗"),
    Othirty("130", "匹"),
    Othio("131", "册"),
    Othirtyw("132", "本"),
    Othirtyr("133", "发"),
    Othirtyf("134", "枚"),
    Othirtyi("135", "捆"),
    Othirtys("136", "袋"),
    Othirtyn("139", "粒"),
    Ohunforty("140", "盒"),
    Ohfortyo("141", "合"),
    Ohuforty("142", "瓶"),
    Ohfortyh("143", "千支"),
    Ohfortyu("144", "万双"),
    Ohfortyfi("145", "万粒"),
    Ohfortysi("146", "千粒"),
    Ohfortyse("147", "千米"),
    Ofortyeig("148", "千英尺"),
    Osixtythr("163", "部");

    private String key;
    private String value;

    Unit(String key, String value) {
        this.key = key;
        this.value = value;
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
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    public static String getKeyByValue(String value) {
        String key = null;
        value = trimToNull(value);
        if (isNotBlank(value) && !isNumeric(value)) {
            Unit[] units = Unit.values();
            if (isNotEmpty(units)) {
                for (Unit unit : units) {
                    if (equalsIgnoreCase(unit.getValue(), value)) {
                        key = unit.getKey();
                        break;
                    }
                }
            }
        }
        return isBlank(key) ? value : key;
    }

    public static Unit getUnitByValue(String value) {
        Unit unit = null;
        if (isNotBlank(value) && !isNumeric(value)) {
            Unit[] units = Unit.values();
            if (isNotEmpty(units)) {
                for (Unit unt : units) {
                    if (equalsIgnoreCase(unt.getValue(), value)) {
                        unit = unt;
                        break;
                    }
                }
            }
        }
        return unit;
    }
    
    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
