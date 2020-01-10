/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils.xml;

import com.ruoyi.common.compressor.Compressor;
import com.ruoyi.common.compressor.XmlCompressor;
import static com.ruoyi.common.constant.Constants.UTF8;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;
import static java.lang.Boolean.TRUE;
import java.util.HashMap;
import static java.util.Objects.nonNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import static javax.xml.bind.Marshaller.JAXB_ENCODING;
import static javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT;
import static javax.xml.bind.Marshaller.JAXB_FRAGMENT;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.RegExUtils.replaceAll;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trim;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * 封装了XML转换成object，object转换成XML的代码
 *
 * @author Steven
 *
 */
public final class XmlUtil {

    private static final Logger LOG = LoggerFactory.getLogger(XmlUtil.class);
    
    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param <T>
     * @param t
     * @param mapper
     * @return
     */
    public static <T> String unescapeConvertToXml(T t, NamespacePrefixMapper mapper) {
       String ctnt = unescapeHtml4(convertToXml(t, mapper));
       /*if(containsIgnoreCase(ctnt, "&")){
           ctnt = replaceAll(ctnt, "&", "&amp;");
       }*/
       return ctnt;
    }
    
    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param <T>
     * @param t
     * @return
     */
    public static <T> String unescapeConvertToXml(T t) {
        return unescapeConvertToXml(t, null);
    }
    
    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param <T>
     * @param t
     * @return
     */
    public static <T> String convertToXml(T t) {
        return convertToXml(t, null);
    }

    /**
     * 将对象直接转换成String类型的 XML输出
     *
     * @param <T>
     * @param t
     * @param mapper
     * @return
     */
    public static <T> String convertToXml(T t, NamespacePrefixMapper mapper) {
        String xml = null;
        try {
            // 创建输出流
            StringWriter sw = new StringWriter();
            // 利用jdk中自带的转换类实现
            /*
            Map<String, Object> properties = new HashMap<String, Object>(1) {
                {
                    put(SESSION_EVENT_LISTENER, new NullPolicySessionEventListener());
                }
            };*/
            JAXBContext context = JAXBContextFactory.createContext(new Class[]{t.getClass()}, new HashMap<>());
            Marshaller marshaller = context.createMarshaller();
            // 格式化xml输出的格式
            //marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, FALSE);
            marshaller.setProperty(JAXB_FORMATTED_OUTPUT, TRUE);
            marshaller.setProperty(JAXB_ENCODING, UTF8);
            marshaller.setProperty(JAXB_FRAGMENT, TRUE);
            if (nonNull(mapper)) {
                marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
            }
            // 将对象转换成输出流形式的xml
            marshaller.marshal(t, sw);
            xml = sw.toString();
        } catch (JAXBException ex) {
            ex.printStackTrace();
            LOG.error("failed to convert javabean to xml {}", ex.getMessage());
        }
        return xml;
    }

    /**
     * 将String类型的xml转换成对象
     *
     * @param <T>
     * @param clazz
     * @param xmlStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertXmlStrToObject(Class clazz, String xmlStr) {
        T xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            SAXParserFactory sax = SAXParserFactory.newInstance();
            sax.setNamespaceAware(false);
            XMLReader xmlReader = sax.newSAXParser().getXMLReader();
            Source source = new SAXSource(xmlReader, new InputSource(sr));
            xmlObject = (T) unmarshaller.unmarshal(source);
        } catch (JAXBException | ParserConfigurationException | SAXException ex) {
            ex.printStackTrace();
            LOG.error("failed to convert xml to java object {}", ex.getMessage());
        }
        return xmlObject;
    }

    /**
     * 将file类型的xml转换成对象
     *
     * @param clazz
     * @param xmlPath
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object convertXmlFileToObject(Class clazz, String xmlPath) {
        Object xmlObject = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            FileReader fr = new FileReader(xmlPath);
            xmlObject = unmarshaller.unmarshal(fr);
        } catch (FileNotFoundException | JAXBException e) {
            e.printStackTrace();
            LOG.error("failed to convert xml file to java object {}", e.getMessage());
        }
        return xmlObject;
    }
    
    public static String unformat(String xml){
        String unformat = null;
        if(isNotBlank(xml)){
            String[] splitted = split(xml, "\n");
            if(isNotEmpty(splitted)){
                StringBuilder xmlStr = new StringBuilder();
                for(String sp : splitted){
                    xmlStr.append(trim(sp));
                }
                Compressor compressor = new XmlCompressor();
                unformat = compressor.compress(xmlStr.toString().replaceAll("\n", ""));
            }
        }
        return unformat;
    }
}
