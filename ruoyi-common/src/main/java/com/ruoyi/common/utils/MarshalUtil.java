/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruoyi.common.utils;

import java.io.StringReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.trim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 *
 * @author wmao
 */
public class MarshalUtil {

    private static final Logger LOG = LoggerFactory.getLogger(MarshalUtil.class);

    private static final String XML_PREFIX = "<?xml";
    private static final String XML_SUFFIX = "?>";

    public static Object unmarshall(final Class<?> cla, final String content) throws JAXBException, ParserConfigurationException, SAXException {
        Object obj = null;
        if (isNotBlank(content)) {
            String ctnt = trim(content.replaceAll("& ", "&"));
            ctnt = XML_PREFIX + substringAfter(unescapeHtml4(ctnt), XML_PREFIX);
            JAXBContext jaxbContext = JAXBContext.newInstance(cla);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SAXParserFactory sax = SAXParserFactory.newInstance();
            sax.setNamespaceAware(false);
            XMLReader xmlReader = sax.newSAXParser().getXMLReader();
            Source source = new SAXSource(xmlReader, new InputSource(new StringReader(ctnt)));
            obj = unmarshaller.unmarshal(source);
        }
        return obj;
    }
}
