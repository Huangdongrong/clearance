//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.09.03 at 11:36:21 AM CST 
//


package com.ruoyi.yz.wuliu.ydkj.apply;

import com.ruoyi.yz.base.BaseCif;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}buztype"/>
 *         &lt;element ref="{}version"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "buztype",
    "version"
})
@XmlRootElement(name = "head")
public class YdApplyHead  extends BaseCif{

    @XmlElement(required = true)
    protected String buztype;
    @XmlElement(required = true)
    private String version;

    /**
     * Gets the value of the buztype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuztype() {
        return buztype;
    }

    /**
     * Sets the value of the buztype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuztype(String value) {
        this.buztype = value;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

}
