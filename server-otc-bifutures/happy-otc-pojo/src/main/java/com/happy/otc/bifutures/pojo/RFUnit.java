/**
 * Yztz.com Inc.
 * Copyright (c) 2013-2015 All Rights Reserved.
 */
package com.happy.otc.bifutures.pojo;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * risk file 单元
 * 
 * @author hao
 * @version $Id: RCFUnit.java, v 0.1 Jul 22, 2015 9:04:47 AM hao Exp $
 */
public abstract class RFUnit {

    private List<RFUnit> subUnits = new ArrayList<RFUnit>();

    private Map<String, String> attributes = new HashMap<String, String>();

    private String desc;
    
    private String characterData;


    public void addRFUnit(RFUnit rfUnit) {
        if (rfUnit != null) {
            subUnits.add(rfUnit);
        }
    }

    public void deleteUnit(RFUnit unit) {
        if (unit != null) {
            subUnits.remove(unit);
        }
    }

    public void addAttributes(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            attributes.put(key, value);
        }
    }

    public abstract String getUnitName();


    /**
     * Setter method for property <tt>desc</tt>.
     * 
     * @param desc value to be assigned to property desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Setter method for property <tt>characterData</tt>.
     * 
     * @param characterData value to be assigned to property characterData
     */
    public void setCharacterData(String characterData) {
        this.characterData = characterData;
    }

    public Element convertToDOM(Document domDocument, Element parentElement) {
        String unitName = getUnitName();
        if (StringUtils.isNotBlank(unitName)) {
            Element element = domDocument.createElement(unitName);

            if (StringUtils.isNotBlank(characterData)) {
                CDATASection cdata = domDocument.createCDATASection(characterData);
                element.appendChild(cdata);
            }
            if (StringUtils.isNotBlank(desc)) {
                Element descEle = domDocument.createElement(RiskMetaConstants.DESC);
                descEle.setTextContent(desc);
                element.appendChild(descEle);
            }
            for (Entry<String, String> attr : attributes.entrySet()) {
                element.setAttribute(attr.getKey(), attr.getValue());
            }
            for (RFUnit unit : subUnits) {
                element.appendChild(unit.convertToDOM(domDocument, element));
            }
            if (parentElement != null) {
                parentElement.appendChild(element);
            } else {
                domDocument.appendChild(element);
            }
            return element;
        }
        return null;

    }
}
