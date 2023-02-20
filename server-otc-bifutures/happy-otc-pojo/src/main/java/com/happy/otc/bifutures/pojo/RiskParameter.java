package com.happy.otc.bifutures.pojo;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class RiskParameter extends RFUnit {

    private boolean isCopy;

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数值
     */
    private String value;

    /**
     * 参数显示名称
     */
    private String distName;

    /**
     * 参数表述
     */
    private String desc;

    /**
     * 约束条件
     */
    private Restriction restriction;

    /**
     * 所在风险对象
     */
    private RiskObject riskObject;

    /**
     * 引用类别
     */
    private String refCategory;

    /**
     * 引用参数名
     */
    private String refName;

    /**
     * 正则表达式
     */
    private String regex;

    /**
     * @param node
     * @param riskObject
     */
    public RiskParameter(Node node, RiskObject riskObject, boolean isCopy) {
        this.isCopy = isCopy;
        this.riskObject = riskObject;
        readParameter(node);

    }

    /**
     * @param node
     */
    private void readParameter(Node node) {
        readAttributes(node);
        readChildNodes(node);
    }

    /**
     * @param node
     */
    private void readChildNodes(Node node) {
        NodeList childNodes = node.getChildNodes();
        Node childNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            childNode = childNodes.item(i);
            String nodeName = childNode.getNodeName();
            if (nodeName.equals(RiskMetaConstants.RESTRICTION)) {
                restriction = createRestriction(childNode);
            } else if (nodeName.equals(RiskMetaConstants.DESC)) {
                desc = createDesc(childNode);
            }
        }
    }

    /**
     * @param node
     * @return
     */
    private Restriction createRestriction(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        String type = attributes.getNamedItem(RiskMetaConstants.TYPE).getNodeValue();
        if (type.equals(RiskMetaConstants.FORMULA)) {
            return new RestrictionFormula(node, this);
        } else if (type.equals(RiskMetaConstants.SHARE)) {
            return new RestrictionShareValue(node, this);
        }
        return null;
    }

    /**
     * @param node
     * @return
     */
    private String createDesc(Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if (StringUtils.isNotBlank(item.getTextContent())) {
                return item.getTextContent();
            }
        }
        return null;
    }

    /**
     * @param node
     */
    private void readAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node nameNode = attributes.getNamedItem(RiskMetaConstants.NAME);
        if (nameNode != null) {
            name = nameNode.getNodeValue();
        }
        Node valueNode = attributes.getNamedItem(RiskMetaConstants.VALUE);
        if (valueNode != null) {
            value = valueNode.getNodeValue();
        }
        Node distNameNode = attributes.getNamedItem(RiskMetaConstants.DIST_NAME);
        if (distNameNode != null) {
            distName = distNameNode.getNodeValue();
        }

        Node refCategoryNode = attributes.getNamedItem(RiskMetaConstants.REF_CATEGORY);
        if (refCategoryNode != null) {
            refCategory = refCategoryNode.getNodeValue();
        }

        Node refNameNode = attributes.getNamedItem(RiskMetaConstants.REF_NAME);
        if (refNameNode != null) {
            refName = refNameNode.getNodeValue();
        }

    }

    /**
     * Getter method for property <tt>name</tt>.
     *
     * @return property value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter method for property <tt>value</tt>.
     *
     * @return property value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * Getter method for property <tt>riskObject</tt>.
     *
     * @return property value of riskObject
     */
    public RiskObject getRiskObject() {
        return riskObject;
    }

    /**
     * @param value
     */
    public void replaceValue(String value) {
        /*if (!isCopy) {
            throw new IllegalArgumentException("试图修改非拷贝风险对象");
        }*/
        this.value = value;
        addAttributes(RiskMetaConstants.VALUE, value);
    }

    /**
     * Getter method for property <tt>restriction</tt>.
     *
     * @return property value of restriction
     */
    public Restriction getRestriction() {
        return restriction;
    }

    /**
     * Setter method for property <tt>restriction</tt>.
     *
     * @param restriction value to be assigned to property restriction
     */
    void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    /**
     * @return
     */
    public Map<String, Object> toObjectMap() {
        Map<String, Object> objMap = new HashMap<String, Object>();
        if (this.isRef()) {
            RiskParameter rp = getRiskObject().getRoot().findParameter(this.refCategory, this.refName);
            objMap.putAll(rp.toObjectMap());
        } else {
            objMap.put(RiskMetaConstants.VALUE, value);
            if (desc != null) {
                objMap.put(RiskMetaConstants.DESC, desc);
            }
            if (restriction != null && restriction.toJsonValue() != null) {
                objMap.put(RiskMetaConstants.RESTRICTION, restriction.toJsonValue());
            }
        }
        return objMap;
    }

    /**
     * Getter method for property <tt>distName</tt>.
     *
     * @return property value of distName
     */
    public String getDistName() {
        return distName;
    }

    /**
     * Setter method for property <tt>distName</tt>.
     *
     * @param distName value to be assigned to property distName
     */
    void setDistName(String distName) {
        this.distName = distName;
    }

    /**
     * Getter method for property <tt>refCategory</tt>.
     *
     * @return property value of refCategory
     */
    public String getRefCategory() {
        return refCategory;
    }

    /**
     * Getter method for property <tt>refName</tt>.
     *
     * @return property value of refName
     */
    public String getRefName() {
        return refName;
    }

    /**
     * Getter method for property <tt>desc</tt>.
     *
     * @return property value of desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @return
     */
    public Boolean isRef() {
        return StringUtils.isNotBlank(refCategory) && StringUtils.isNotBlank(refName);
    }

    @Override
    public String getUnitName() {
        return RiskMetaConstants.P;
    }
    public String getRegex() {
        return regex;
    }

}