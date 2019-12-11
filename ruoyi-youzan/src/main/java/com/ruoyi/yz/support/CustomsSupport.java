/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import com.ruoyi.yz.customs.Authentication;
import com.ruoyi.yz.customs.BaseTransfer;
import com.ruoyi.yz.customs.Message;
import com.ruoyi.yz.customs.RequestMessage;
import com.ruoyi.yz.domain.CustomsPlat;
import static com.ruoyi.common.utils.UuidUtil.gen36UUID;
import com.ruoyi.common.core.domain.BaseQingmaEntity;
import java.util.List;
import static java.util.Objects.nonNull;

/**
 *
 * @author wmao
 * @param <T>
 */
public abstract class CustomsSupport<T> {

    protected CustomsPlat plat;

    public CustomsSupport() {

    }

    public CustomsSupport(CustomsPlat plat) {
        this.plat = plat;
    }

    public List<Message> generateRequest(T info, BaseQingmaEntity bqe) {
        List<Message> messages = null;
        if (nonNull(info)) {
            messages = assemble(info, bqe);
            for (Message message : messages) {
                message.setGuid(gen36UUID());
                message.setVersion(plat.getVersion());
                ((RequestMessage) message).setBaseTransfer(assembleBaseTransfer());
                ((RequestMessage) message).setAuthentication(assembleAuthentication());
            }
        }
        return messages;
    }

    private BaseTransfer assembleBaseTransfer() {
        BaseTransfer transfer = new BaseTransfer();
        transfer.setCopCode(plat.getCopCode());
        transfer.setCopName(plat.getCopName());
        transfer.setDxpId(plat.getDxpId());
        transfer.setDxpMode(plat.getDxpMode());
        return transfer;
    }

    private Authentication assembleAuthentication() {
        Authentication auth = new Authentication();
        return auth;
    }

    protected abstract List<Message> assemble(T info, BaseQingmaEntity bqe);

    /**
     * @return the plat
     */
    public CustomsPlat getPlat() {
        return plat;
    }

    /**
     * @param plat the plat to set
     */
    public void setPlat(CustomsPlat plat) {
        this.plat = plat;
    }
}
