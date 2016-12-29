package com.yeepbank.android.utils;

import android.media.MediaMuxer;
import android.media.MediaRecorder;
import com.yeepbank.android.Cst;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static android.media.MediaRecorder.*;

/**
 * Created by WW on 2015/10/28.
 */
public class XMLUtil<T> {

    private static XMLUtil xmlUtil;

    private static DocumentBuilderFactory factory ;

    private static DocumentBuilder builder;

    private Document document;

    private XMLUtil(){}

    public static synchronized XMLUtil getInstances() throws ParserConfigurationException {
        if(xmlUtil == null){
            xmlUtil = new XMLUtil();
        }
        if(factory == null){
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        }
        return xmlUtil;
    }

    public String CreateXml(T t) throws IllegalAccessException {
        document = builder.newDocument();
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);
        Element request = document.createElement("request");
        document.appendChild(request);

        Element topic = document.createElement("topic");
        request.appendChild(topic);

        Element version = document.createElement("version");
        version.setTextContent("3.0");
        topic.appendChild(version);

        Element charset = document.createElement("charset");
        charset.setTextContent("UTF-8");
        topic.appendChild(charset);

        Element fundChannelId = document.createElement("fundChannelId");
        fundChannelId.setTextContent(Cst.PARAMS.FUND_CHANNEL_ID);
        topic.appendChild(fundChannelId);

        Element requestContent = document.createElement("requestContent");
        request.appendChild(requestContent);

        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            Element fieldElement = document.createElement(field.getName());
            fieldElement.setTextContent(field.get(t).toString());
            requestContent.appendChild(fieldElement);
        }
        Element signature = document.createElement("signature");
        request.appendChild(signature);

        Element signatureType = document.createElement("signatureType");
        signatureType.setTextContent("MD5");
        signature.appendChild(signatureType);

        Element signatureData = document.createElement("signatureData");
        signatureData.setTextContent("XXXXXXXXX");
        signature.appendChild(signatureData);

        try {
            Source source = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
