/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.Objects.isNull;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * @author wmao
 */
public class XmlDateSupport extends XmlAdapter<String, Date> {

    private static final DateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public Date unmarshal(String str) throws Exception {
        return isBlank(str) ? null : DF.parse(str);
    }

    @Override
    public String marshal(Date date) throws Exception {
        return isNull(date) ? "" : DF.format(date);
    }
}
