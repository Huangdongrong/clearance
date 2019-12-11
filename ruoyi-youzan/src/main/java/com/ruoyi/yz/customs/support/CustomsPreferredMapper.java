/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.yz.customs.support;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;

/**
 *
 * @author wmao
 */
public class CustomsPreferredMapper extends NamespacePrefixMapper {

    @Override
    public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
        if (containsIgnoreCase(namespaceUri, "http://www.chinaport.gov.cn/ceb")){
            return "ceb";
        }else if (containsIgnoreCase(namespaceUri, "http://www.w3.org/2001/XMLSchema-instance")){
            return "xsi";
        }
        return suggestion;
    }
}
