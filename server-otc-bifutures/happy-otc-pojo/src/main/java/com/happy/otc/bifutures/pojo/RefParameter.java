package com.happy.otc.bifutures.pojo;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class RefParameter {

    /**
     * 引用参数名称
     */
    private String name;

    /**
     * 引用参数类别
     */
    private String category;

    /**
     * @param node
     */
    public RefParameter(Node node) {
        readRefParameter(node);
    }

    /**
     *
     * @param node
     */
    private void readRefParameter(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node nameNode = attributes.getNamedItem(RiskMetaConstants.NAME);
        if (nameNode != null) {
            name = nameNode.getNodeValue();
        }
        Node categoryNode = attributes.getNamedItem(RiskMetaConstants.CATEGORY);
        if (categoryNode != null) {
            category = categoryNode.getNodeValue();
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
     * Setter method for property <tt>name</tt>.
     *
     * @param name
     *            value to be assigned to property name
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property <tt>category</tt>.
     *
     * @return property value of category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Setter method for property <tt>category</tt>.
     *
     * @param category
     *            value to be assigned to property category
     */
    void setCategory(String category) {
        this.category = category;
    }

}
