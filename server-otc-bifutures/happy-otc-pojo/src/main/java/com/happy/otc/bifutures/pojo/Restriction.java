package com.happy.otc.bifutures.pojo;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class Restriction {
    /**
     * 受约束参数
     */
    protected RiskParameter riskParameter;

    /**
     * 约束名称
     */
    protected String name;

    /**
     * 类型
     */
    protected String type;

    /**
     * @param node
     * @param parameter
     */
    public Restriction(Node node, RiskParameter parameter) {
        readAttributes(node);
        riskParameter = parameter;
    }

    /**
     *
     * @param node
     */
    private void readAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node nameNode = attributes.getNamedItem(RiskMetaConstants.NAME);
        if (nameNode != null) {
            name = nameNode.getNodeValue();

        }
        Node typeNode = attributes.getNamedItem(RiskMetaConstants.TYPE);
        if (typeNode != null) {
            type = typeNode.getNodeValue();

        }
    }

    /**
     * Getter method for property <tt>riskParameter</tt>.
     *
     * @return property value of riskParameter
     */
    public RiskParameter getRiskParameter() {
        return riskParameter;
    }

    /**
     *
     * @param riskParameter
     *            value to be assigned to property riskParameter
     */
    void setRiskParameter(RiskParameter riskParameter) {
        this.riskParameter = riskParameter;
    }

    /**
     *

     * @return
     */
    public Object calculate(Object... args) {
        return null;
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
     * Getter method for property <tt>type</tt>.
     *
     * @return property value of type
     */
    public String getType() {
        return type;
    }

    /**
     *
     *
     * @return
     */
    public String toJsonValue() {

        return null;
    }
}
