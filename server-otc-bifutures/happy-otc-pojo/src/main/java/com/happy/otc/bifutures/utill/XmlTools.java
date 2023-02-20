package com.happy.otc.bifutures.utill;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class XmlTools {

    /**
     * Private constructor.
     */
    private XmlTools() {
    }



    public static Document parseDoc(InputStream stream) throws ParserConfigurationException, IOException, SAXException {
        return parseDoc(stream, false);
    }

    public static Document parseDoc(InputStream stream, boolean nameSpaceAware) throws ParserConfigurationException, IOException, SAXException {
        Document doc = null;
            // Create the new DocumentBuilderFactory
            DocumentBuilderFactory docBF = DocumentBuilderFactory.newInstance();
            docBF.setNamespaceAware(nameSpaceAware);
            docBF.setValidating(false);

            // Create the new DocumentBuilder
            DocumentBuilder docBuilder = docBF.newDocumentBuilder();

            //Remove all dtd definitions found in entity, because there's no dtd files available in the manager distribution.

            // Parse the file
            doc = docBuilder.parse(stream);

            // Normalize text representation
            doc.getDocumentElement().normalize();
        // Return the document
        return doc;
    }
}
