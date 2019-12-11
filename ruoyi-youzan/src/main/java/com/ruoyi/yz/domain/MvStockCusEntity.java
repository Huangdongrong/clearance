package com.ruoyi.yz.domain;

import com.ruoyi.common.core.domain.BaseQingmaEntity;
import org.apache.ibatis.type.Alias;

/**
 * @Title: Entity
 * @Description: 客户库存
 * @author erzhongxmu
 * @date 2017-09-17 21:45:28
 * @version V1.0
 *
 */
@Alias("MvStockCusEntity")
public class MvStockCusEntity extends BaseQingmaEntity {

    /**
     * 库存类型
     */
    private String kucType;
    /**
     * 数量
     */
//	@Excel(name="数量")
    private Integer goodsQua;
    /**
     * 单位
     */
//	@Excel(name="单位")
    private String goodsUnit;
    /**
     * 库存数量
     */
    private Integer baseGoodscount;

    /**
     * 重量
     */
    private Integer zhongLiang;
    /**
     * 库存单位
     */
    private String baseUnit;
    /**
     * 储位编码
     */
//	@Excel(name="储位编码")
    private String kuWeiBianMa;
    /**
     * 托盘
     */
//	@Excel(name="托盘")
    private String binId;
    /**
     * 客户
     */
    private String cusCode;
    /**
     * 客户名称
     */
    private String zhongWenQch;
    /**
     * 商品编码
     */
    private String goodsId;
    /**
     * 商品编码kh
     */
    private String shpBianmakh;
    /**
     * 商品名称
     */
    private String shpMingCheng;
    /**
     * 单位
     */
    private String shlDanWei;
    /**
     * 生产日期
     */
    private String goodsProData;
    /**
     * 保质期
     */
    private String bzhiQi;
    /**
     * 保质期
     */
    private String dqr;
    private String hiti;
    private String kuWeiLeiXing;
    /**
     * 取货次序
     */

    private String quHuoCiXu;
    /**
     * 上架次序
     */

    private String shangJiaCiXu;

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 库存类型
     */
    public String getKucType() {
        return this.kucType;
    }

    /**
     * 方法: 设置String
     *
     * @param kucType
     * @param: String 库存类型
     */
    public void setKucType(String kucType) {
        this.kucType = kucType;
    }

    /**
     * 方法: 取得Integer
     *
     * @return
     * @return: Integer 数量
     */
    public Integer getGoodsQua() {
        return this.goodsQua;
    }

    /**
     * 方法: 设置Integer
     *
     * @param goodsQua
     * @param: Integer 数量
     */
    public void setGoodsQua(Integer goodsQua) {
        this.goodsQua = goodsQua;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 单位
     */
    public String getGoodsUnit() {
        return this.goodsUnit;
    }

    /**
     * 方法: 设置String
     *
     * @param goodsUnit
     * @param: String 单位
     */
    public void setGoodsUnit(String goodsUnit) {
        this.goodsUnit = goodsUnit;
    }

    /**
     * 方法: 取得Integer
     *
     * @return
     * @return: Integer 库存数量
     */
    public Integer getBaseGoodscount() {
        return this.baseGoodscount;
    }

    /**
     * 方法: 设置Integer
     *
     * @param baseGoodscount
     * @param: Integer 库存数量
     */
    public void setBaseGoodscount(Integer baseGoodscount) {
        this.baseGoodscount = baseGoodscount;
    }

    /**
     * 方法: 取得Integer
     *
     * @return
     * @return: Integer 库存数量
     */
    public Integer getZhongLiang() {
        return this.zhongLiang;
    }

    /**
     * 方法: 设置Integer
     *
     * @param zhongLiang
     * @param: Integer 库存数量
     */
    public void setZhongLiang(Integer zhongLiang) {
        this.zhongLiang = zhongLiang;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 库存单位
     */
    public String getBaseUnit() {
        return this.baseUnit;
    }

    /**
     * 方法: 设置String
     *
     * @param baseUnit
     * @param: String 库存单位
     */
    public void setBaseUnit(String baseUnit) {
        this.baseUnit = baseUnit;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 储位编码
     */
    public String getKuWeiBianMa() {
        return this.kuWeiBianMa;
    }

    /**
     * 方法: 设置String
     *
     * @param kuWeiBianMa
     * @param: String 储位编码
     */
    public void setKuWeiBianMa(String kuWeiBianMa) {
        this.kuWeiBianMa = kuWeiBianMa;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 托盘
     */
    public String getBinId() {
        return this.binId;
    }

    /**
     * 方法: 设置String
     *
     * @param binId
     * @param: String 托盘
     */
    public void setBinId(String binId) {
        this.binId = binId;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 客户
     */
    public String getCusCode() {
        return this.cusCode;
    }

    /**
     * 方法: 设置String
     *
     * @param cusCode
     * @param: String 客户
     */
    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 客户名称
     */
    public String getZhongWenQch() {
        return this.zhongWenQch;
    }

    /**
     * 方法: 设置String
     *
     * @param zhongWenQch
     * @param: String 客户名称
     */
    public void setZhongWenQch(String zhongWenQch) {
        this.zhongWenQch = zhongWenQch;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 商品编码
     */
    public String getGoodsId() {
        return this.goodsId;
    }

    /**
     * 方法: 设置String
     *
     * @param goodsId
     * @param: String 商品编码
     */
    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getShpBianmakh() {
        return this.shpBianmakh;
    }

    /**
     * 方法: 设置String
     *
     * @param shpBianmakh
     * @param: String 商品编码
     */
    public void setShpBianmakh(String shpBianmakh) {
        this.shpBianmakh = shpBianmakh;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 商品名称
     */
    public String getShpMingCheng() {
        return this.shpMingCheng;
    }

    /**
     * 方法: 设置String
     *
     * @param shpMingCheng
     * @param: String 商品名称
     */
    public void setShpMingCheng(String shpMingCheng) {
        this.shpMingCheng = shpMingCheng;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 单位
     */
    public String getShlDanWei() {
        return this.shlDanWei;
    }

    /**
     * 方法: 设置String
     *
     * @param shlDanWei
     * @param: String 单位
     */
    public void setShlDanWei(String shlDanWei) {
        this.shlDanWei = shlDanWei;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 生产日期
     */
    public String getGoodsProData() {
        return this.goodsProData;
    }

    /**
     * 方法: 设置String
     *
     * @param goodsProData
     * @param: String 生产日期
     */
    public void setGoodsProData(String goodsProData) {
        this.goodsProData = goodsProData;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 保质期
     */
    public String getBzhiQi() {
        return this.bzhiQi;
    }

    /**
     * 方法: 设置String
     *
     * @param bzhiQi
     * @param: String 保质期
     */
    public void setBzhiQi(String bzhiQi) {
        this.bzhiQi = bzhiQi;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 取货次序
     */
    public String getQuHuoCiXu() {
        return this.quHuoCiXu;
    }

    /**
     * 方法: 设置String
     *
     * @param quHuoCiXu
     * @param: String 取货次序
     */
    public void setQuHuoCiXu(String quHuoCiXu) {
        this.quHuoCiXu = quHuoCiXu;
    }

    /**
     * 方法: 取得String
     *
     * @return
     * @return: String 上架次序
     */
    public String getShangJiaCiXu() {
        return this.shangJiaCiXu;
    }

    /**
     * 方法: 设置String
     *
     * @param shangJiaCiXu
     * @param: String 上架次序
     */
    public void setShangJiaCiXu(String shangJiaCiXu) {
        this.shangJiaCiXu = shangJiaCiXu;
    }

    public String getDqr() {
        return dqr;
    }

    public void setDqr(String dqr) {
        this.dqr = dqr;
    }

    public String getHiti() {
        return hiti;
    }

    public void setHiti(String hiti) {
        this.hiti = hiti;
    }

    public String getKuWeiLeiXing() {
        return kuWeiLeiXing;
    }

    public void setKuWeiLeiXing(String kuWeiLeiXing) {
        this.kuWeiLeiXing = kuWeiLeiXing;
    }

}
