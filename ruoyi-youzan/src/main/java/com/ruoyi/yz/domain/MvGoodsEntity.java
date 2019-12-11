package com.ruoyi.yz.domain;

import java.math.BigDecimal;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import org.apache.ibatis.type.Alias;

/**
 * @Title: Entity
 * @Description: 商品信息
 * @author erzhongxmu
 * @date 2017-08-15 23:16:53
 * @version V1.0
 *
 */
@Alias("MdGoodsEntity")
public class MvGoodsEntity extends BaseQingmaEntity {

    /**
     * 所属客户
     */
    private java.lang.String cusCode;
    /**
     * 商品编码(拆零有影响)
     */
    private java.lang.String goodsCode;

    /**
     * 商品编码
     */
    private java.lang.String goodsId;
    /**
     * 商品名称（商品编码-商品名称-单位/拆零单位）
     */
    private java.lang.String goodsName;
    /**
     * 单位/拆零单位
     */
    private java.lang.String shlDanWei;
    /**
     * 存放温层
     */
    private java.lang.String cfWenCeng;
    /**
     * 码盘单层数量
     */
    private java.lang.String mpDanCeng;
    /**
     * 码盘层高
     */
    private java.lang.String mpCengGao;
    /**
     * 商品条码
     */
    private java.lang.String shpTiaoMa;
    /**
     * 保质期
     */
    private java.lang.String bzhiQi;
    /**
     * 拆零数量
     */
    private java.lang.String chlShl;
    /**
     * 体积
     */
    private java.lang.String tiJiCm;
    /**
     * 重量
     */
    private java.lang.String zhlKg;
    /**
     * 拆零单位
     */
    private java.lang.String baseunit;

    /**
     * 商品名称
     */
    private java.lang.String shpMingCheng;

    /**
     * 商品规格
     */
    private java.lang.String shpGuiGe;
    /**
     * 商品品牌
     */
    private java.lang.String shpPinPai;

    /**
     * 长整箱
     */
    private java.lang.String chZhXiang;

    /**
     * 宽整箱
     */
    private java.lang.String kuZhXiang;
    /**
     * 高整箱
     */
    private java.lang.String gaoZhXiang;

    /**
     * 商品海关编码(HScode)
     */
    private java.lang.String shpCustomsBianMa;

    /**
     * 商品海关计量单位
     */
    private java.lang.String shpCustomsUnit;

    /**
     * 商品海关原产国
     */
    private java.lang.String shpCustomsOriginCountry;

    /**
     * 商品海关跨境综合税率
     */
    private BigDecimal shpCustomsTaxRate;

    /**
     * 商品海关消费税
     */
    private BigDecimal shpCustomsConsumeTaxRate;

    /**
     * 商品海关增值税
     */
    private BigDecimal shpCustomsValueAddedTaxRate;

    /**
     * 海关账册备案料号
     */
    private String shpCustomsOrderNo;
    
    /**
     * 商品海关法定计量单位
     */
    private String shpCustomsOfficialDanWei;
    
    /**
     * 商品海关法定计量单位对应数量
     */
    private BigDecimal shpCustomsOfficialQty;
    
    /**
     * 商品海关第二计量单位
     */
    private String shpCustomsSecondDanWei;
    
    /**
     * 商品海关第二计量单位对应数量
     */
    private BigDecimal shpCustomsSecondQty;

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String cusCode
     */
    public java.lang.String getCusCode() {
        return this.cusCode;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param cusCode
     * @param: java.lang.String cusCode
     */
    public void setCusCode(java.lang.String cusCode) {
        this.cusCode = cusCode;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String goodsCode
     */
    public java.lang.String getGoodsId() {
        return this.goodsId;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param goodsId
     * @param: java.lang.String goodsCode
     */
    public void setGoodsId(java.lang.String goodsId) {
        this.goodsId = goodsId;
    }

    /**
     *
     * @return
     */
    public java.lang.String getGoodsCode() {
        return this.goodsCode;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param goodsCode
     * @param: java.lang.String goodsCode
     */
    public void setGoodsCode(java.lang.String goodsCode) {
        this.goodsCode = goodsCode;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String goodsName
     */
    public java.lang.String getGoodsName() {
        return this.goodsName;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param goodsName
     * @param: java.lang.String goodsName
     */
    public void setGoodsName(java.lang.String goodsName) {
        this.goodsName = goodsName;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String 商品名称
     */
    public java.lang.String getShpMingCheng() {
        return this.shpMingCheng;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param shpMingCheng
     * @param: java.lang.String 商品名称
     */
    public void setShpMingCheng(java.lang.String shpMingCheng) {
        this.shpMingCheng = shpMingCheng;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String shlDanWei
     */
    public java.lang.String getShlDanWei() {
        return this.shlDanWei;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param shlDanWei
     * @param: java.lang.String shlDanWei
     */
    public void setShlDanWei(java.lang.String shlDanWei) {
        this.shlDanWei = shlDanWei;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String cfWenCeng
     */
    public java.lang.String getCfWenCeng() {
        return this.cfWenCeng;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param cfWenCeng
     * @param: java.lang.String cfWenCeng
     */
    public void setCfWenCeng(java.lang.String cfWenCeng) {
        this.cfWenCeng = cfWenCeng;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String mpDanCeng
     */
    public java.lang.String getMpDanCeng() {
        return this.mpDanCeng;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param mpDanCeng
     * @param: java.lang.String mpDanCeng
     */
    public void setMpDanCeng(java.lang.String mpDanCeng) {
        this.mpDanCeng = mpDanCeng;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return 
     * @return: java.lang.String mpCengGao
     */
    public java.lang.String getMpCengGao() {
        return this.mpCengGao;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param mpCengGao
     * @param: java.lang.String mpCengGao
     */
    public void setMpCengGao(java.lang.String mpCengGao) {
        this.mpCengGao = mpCengGao;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String shpTiaoMa
     */
    public java.lang.String getShpTiaoMa() {
        return this.shpTiaoMa;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String shpTiaoMa
     */
    public void setShpTiaoMa(java.lang.String shpTiaoMa) {
        this.shpTiaoMa = shpTiaoMa;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String bzhiQi
     */
    public java.lang.String getBzhiQi() {
        return this.bzhiQi;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String bzhiQi
     */
    public void setBzhiQi(java.lang.String bzhiQi) {
        this.bzhiQi = bzhiQi;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String chlShl
     */
    public java.lang.String getChlShl() {
        return this.chlShl;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String chlShl
     */
    public void setChlShl(java.lang.String chlShl) {
        this.chlShl = chlShl;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String tiJiCm
     */
    public java.lang.String getTiJiCm() {
        return this.tiJiCm;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String tiJiCm
     */
    public void setTiJiCm(java.lang.String tiJiCm) {
        this.tiJiCm = tiJiCm;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String zhlKg
     */
    public java.lang.String getZhlKg() {
        return this.zhlKg;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String zhlKg
     */
    public void setZhlKg(java.lang.String zhlKg) {
        this.zhlKg = zhlKg;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String baseunit
     */
    public java.lang.String getBaseunit() {
        return this.baseunit;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String baseunit
     */
    public void setBaseunit(java.lang.String baseunit) {
        this.baseunit = baseunit;
    }

    /**
     *
     * @return
     */
    public java.lang.String getShpGuiGe() {
        return this.shpGuiGe;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String 商品规格
     */
    public void setShpGuiGe(java.lang.String shpGuiGe) {
        this.shpGuiGe = shpGuiGe;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String 长整箱
     */
    public java.lang.String getChZhXiang() {
        return this.chZhXiang;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String 长整箱
     */
    public void setChZhXiang(java.lang.String chZhXiang) {
        this.chZhXiang = chZhXiang;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String 宽整箱
     */
    public java.lang.String getKuZhXiang() {
        return this.kuZhXiang;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String 宽整箱
     */
    public void setKuZhXiang(java.lang.String kuZhXiang) {
        this.kuZhXiang = kuZhXiang;
    }

    /**
     * 方法: 取得java.lang.String
     *
     * @return: java.lang.String 高整箱
     */
    public java.lang.String getGaoZhXiang() {
        return this.gaoZhXiang;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String 高整箱
     */
    public void setGaoZhXiang(java.lang.String gaoZhXiang) {
        this.gaoZhXiang = gaoZhXiang;
    }

    /**
     *
     * @return
     */
    public java.lang.String getShpPinPai() {
        return this.shpPinPai;
    }

    /**
     * 方法: 设置java.lang.String
     *
     * @param: java.lang.String 商品品牌
     */
    public void setShpPinPai(java.lang.String shpPinPai) {
        this.shpPinPai = shpPinPai;
    }

    /**
     * @return the shpCustomsConsumeTaxRate
     */
    public BigDecimal getShpCustomsConsumeTaxRate() {
        return shpCustomsConsumeTaxRate;
    }

    /**
     * @param shpCustomsConsumeTaxRate the shpCustomsConsumeTaxRate to set
     */
    public void setShpCustomsConsumeTaxRate(BigDecimal shpCustomsConsumeTaxRate) {
        this.shpCustomsConsumeTaxRate = shpCustomsConsumeTaxRate;
    }

    /**
     * @return the shpCustomsValueAddedTaxRate
     */
    public BigDecimal getShpCustomsValueAddedTaxRate() {
        return shpCustomsValueAddedTaxRate;
    }

    /**
     * @param shpCustomsValueAddedTaxRate the shpCustomsValueAddedTaxRate to set
     */
    public void setShpCustomsValueAddedTaxRate(BigDecimal shpCustomsValueAddedTaxRate) {
        this.shpCustomsValueAddedTaxRate = shpCustomsValueAddedTaxRate;
    }

    /**
     * @return the shpCustomsBianMa
     */
    public java.lang.String getShpCustomsBianMa() {
        return shpCustomsBianMa;
    }

    /**
     * @param shpCustomsBianMa the shpCustomsBianMa to set
     */
    public void setShpCustomsBianMa(java.lang.String shpCustomsBianMa) {
        this.shpCustomsBianMa = shpCustomsBianMa;
    }

    /**
     * @return the shpCustomsUnit
     */
    public java.lang.String getShpCustomsUnit() {
        return shpCustomsUnit;
    }

    /**
     * @param shpCustomsUnit the shpCustomsUnit to set
     */
    public void setShpCustomsUnit(java.lang.String shpCustomsUnit) {
        this.shpCustomsUnit = shpCustomsUnit;
    }

    /**
     * @return the shpCustomsOriginCountry
     */
    public java.lang.String getShpCustomsOriginCountry() {
        return shpCustomsOriginCountry;
    }

    /**
     * @param shpCustomsOriginCountry the shpCustomsOriginCountry to set
     */
    public void setShpCustomsOriginCountry(java.lang.String shpCustomsOriginCountry) {
        this.shpCustomsOriginCountry = shpCustomsOriginCountry;
    }

    /**
     * @return the shpCustomsTaxRate
     */
    public BigDecimal getShpCustomsTaxRate() {
        return shpCustomsTaxRate;
    }

    /**
     * @param shpCustomsTaxRate the shpCustomsTaxRate to set
     */
    public void setShpCustomsTaxRate(BigDecimal shpCustomsTaxRate) {
        this.shpCustomsTaxRate = shpCustomsTaxRate;
    }

    /**
     * @return the shpCustomsOrderNo
     */
    public String getShpCustomsOrderNo() {
        return shpCustomsOrderNo;
    }

    /**
     * @param shpCustomsOrderNo the shpCustomsOrderNo to set
     */
    public void setShpCustomsOrderNo(String shpCustomsOrderNo) {
        this.shpCustomsOrderNo = shpCustomsOrderNo;
    }

    /**
     * @return the shpCustomsOfficialDanWei
     */
    public String getShpCustomsOfficialDanWei() {
        return shpCustomsOfficialDanWei;
    }

    /**
     * @param shpCustomsOfficialDanWei the shpCustomsOfficialDanWei to set
     */
    public void setShpCustomsOfficialDanWei(String shpCustomsOfficialDanWei) {
        this.shpCustomsOfficialDanWei = shpCustomsOfficialDanWei;
    }

    /**
     * @return the shpCustomsOfficialQty
     */
    public BigDecimal getShpCustomsOfficialQty() {
        return shpCustomsOfficialQty;
    }

    /**
     * @param shpCustomsOfficialQty the shpCustomsOfficialQty to set
     */
    public void setShpCustomsOfficialQty(BigDecimal shpCustomsOfficialQty) {
        this.shpCustomsOfficialQty = shpCustomsOfficialQty;
    }

    /**
     * @return the shpCustomsSecondDanWei
     */
    public String getShpCustomsSecondDanWei() {
        return shpCustomsSecondDanWei;
    }

    /**
     * @param shpCustomsSecondDanWei the shpCustomsSecondDanWei to set
     */
    public void setShpCustomsSecondDanWei(String shpCustomsSecondDanWei) {
        this.shpCustomsSecondDanWei = shpCustomsSecondDanWei;
    }

    /**
     * @return the shpCustomsSecondQty
     */
    public BigDecimal getShpCustomsSecondQty() {
        return shpCustomsSecondQty;
    }

    /**
     * @param shpCustomsSecondQty the shpCustomsSecondQty to set
     */
    public void setShpCustomsSecondQty(BigDecimal shpCustomsSecondQty) {
        this.shpCustomsSecondQty = shpCustomsSecondQty;
    }
}
