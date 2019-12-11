/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.domain;

import com.ruoyi.common.core.domain.BaseQingmaEntity;
import static com.ruoyi.yz.cnst.Const.CREATE_BY_PROGRAM;
import com.ruoyi.yz.customs.Message;
import static com.ruoyi.yz.enums.OrderStatus.STATUS_INIT;
import java.util.Calendar;
import org.apache.ibatis.type.Alias;

/**
 *
 * @author wmao
 */
@Alias("YouzanOrder")
public class YouzanOrder extends BaseQingmaEntity {

    private String tid;

    private String orderNo;

    private String copNo;

    private String transaction;
    
    private boolean seperated;

    private String kdtId;

    private String amount;

    private Message body;

    private String status;

    private String statusMessage;

    private String wayBillEnt;

    private String wayBillNo;

    private boolean alreadySyncPay;

    private ClearanceStatus syncPayStatus;

    private boolean alreadySyncOrder;

    private ClearanceStatus syncOrderStatus;

    private boolean alreadySyncWuliu;

    private ClearanceStatus syncWuliuStatus;

    private boolean alreadySyncDetails;

    private ClearanceStatus syncDetailsStatus;

    public YouzanOrder() {

    }

    public YouzanOrder(String kdtId) {
        this.kdtId = kdtId;
    }

    public void reset() {
        this.setStatus(STATUS_INIT.name());
        this.setStatusMessage(STATUS_INIT.getValue());
        this.setAlreadySyncDetails(false);
        this.setSyncDetailsStatus(null);
        this.setAlreadySyncOrder(false);
        this.setSyncOrderStatus(null);
        this.setAlreadySyncPay(false);
        this.setSyncPayStatus(null);
        this.setAlreadySyncWuliu(false);
        this.setSyncWuliuStatus(null);
        this.setWayBillEnt(null);
        this.setWayBillNo(null);
        this.setUpdateTime(Calendar.getInstance().getTime());
        this.setUpdateBy(CREATE_BY_PROGRAM);
        this.setRemark("重置订单清关信息！");
    }

    /**
     * @return the tid
     */
    public String getTid() {
        return tid;
    }

    /**
     * @param tid the tid to set
     */
    public void setTid(String tid) {
        this.tid = tid;
    }

    /**
     * @return the orderNo
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderNo the orderNo to set
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return the transaction
     */
    public String getTransaction() {
        return transaction;
    }

    /**
     * @param transaction the transaction to set
     */
    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    /**
     * @return the kdtId
     */
    public String getKdtId() {
        return kdtId;
    }

    /**
     * @param kdtId the kdtId to set
     */
    public void setKdtId(String kdtId) {
        this.kdtId = kdtId;
    }

    /**
     * @return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return the body
     */
    public Message getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(Message body) {
        this.body = body;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the wayBillNo
     */
    public String getWayBillNo() {
        return wayBillNo;
    }

    /**
     * @param wayBillNo the wayBillNo to set
     */
    public void setWayBillNo(String wayBillNo) {
        this.wayBillNo = wayBillNo;
    }

    /**
     * @return the syncPayStatus
     */
    public ClearanceStatus getSyncPayStatus() {
        return syncPayStatus;
    }

    /**
     * @param syncPayStatus the syncPayStatus to set
     */
    public void setSyncPayStatus(ClearanceStatus syncPayStatus) {
        this.syncPayStatus = syncPayStatus;
    }

    /**
     * @return the syncOrderStatus
     */
    public ClearanceStatus getSyncOrderStatus() {
        return syncOrderStatus;
    }

    /**
     * @param syncOrderStatus the syncOrderStatus to set
     */
    public void setSyncOrderStatus(ClearanceStatus syncOrderStatus) {
        this.syncOrderStatus = syncOrderStatus;
    }

    /**
     * @return the syncWuliuStatus
     */
    public ClearanceStatus getSyncWuliuStatus() {
        return syncWuliuStatus;
    }

    /**
     * @param syncWuliuStatus the syncWuliuStatus to set
     */
    public void setSyncWuliuStatus(ClearanceStatus syncWuliuStatus) {
        this.syncWuliuStatus = syncWuliuStatus;
    }

    /**
     * @return the statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return the syncDetailsStatus
     */
    public ClearanceStatus getSyncDetailsStatus() {
        return syncDetailsStatus;
    }

    /**
     * @param syncDetailsStatus the syncDetailsStatus to set
     */
    public void setSyncDetailsStatus(ClearanceStatus syncDetailsStatus) {
        this.syncDetailsStatus = syncDetailsStatus;
    }

    /**
     * @return the wayBillEnt
     */
    public String getWayBillEnt() {
        return wayBillEnt;
    }

    /**
     * @param wayBillEnt the wayBillEnt to set
     */
    public void setWayBillEnt(String wayBillEnt) {
        this.wayBillEnt = wayBillEnt;
    }

    /**
     * @return the alreadySyncPay
     */
    public boolean isAlreadySyncPay() {
        return alreadySyncPay;
    }

    /**
     * @param alreadySyncPay the alreadySyncPay to set
     */
    public void setAlreadySyncPay(boolean alreadySyncPay) {
        this.alreadySyncPay = alreadySyncPay;
    }

    /**
     * @return the alreadySyncOrder
     */
    public boolean isAlreadySyncOrder() {
        return alreadySyncOrder;
    }

    /**
     * @param alreadySyncOrder the alreadySyncOrder to set
     */
    public void setAlreadySyncOrder(boolean alreadySyncOrder) {
        this.alreadySyncOrder = alreadySyncOrder;
    }

    /**
     * @return the alreadySyncWuliu
     */
    public boolean isAlreadySyncWuliu() {
        return alreadySyncWuliu;
    }

    /**
     * @param alreadySyncWuliu the alreadySyncWuliu to set
     */
    public void setAlreadySyncWuliu(boolean alreadySyncWuliu) {
        this.alreadySyncWuliu = alreadySyncWuliu;
    }

    /**
     * @return the alreadySyncDetail
     */
    public boolean isAlreadySyncDetails() {
        return alreadySyncDetails;
    }

    /**
     * @param alreadySyncDetails the alreadySyncDetail to set
     */
    public void setAlreadySyncDetails(boolean alreadySyncDetails) {
        this.alreadySyncDetails = alreadySyncDetails;
    }

    /**
     * @return the copNo
     */
    public String getCopNo() {
        return copNo;
    }

    /**
     * @param copNo the copNo to set
     */
    public void setCopNo(String copNo) {
        this.copNo = copNo;
    }

    /**
     * @return the demolition
     */
    public boolean isSeperated() {
        return seperated;
    }

    /**
     * @param seperated the demolition to set
     */
    public void setSeperated(boolean seperated) {
        this.seperated = seperated;
    }
}
