package com.happy.otc.bifutures.pojo;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class LeverRisk extends RiskObject {
    /**
     * 杠杆数额
     */
    private Integer value;

    /**
     *
     * @param node
     * @param parent
     * @param root
     * @param isCopy
     */
    public LeverRisk(Node node, RiskObject parent, RiskObject root, boolean isCopy) {
        super(node, parent, root, isCopy);
        readLeverRisk(node);
    }

    /**
     * 读取数据
     *
     * @param node
     */
    private void readLeverRisk(Node node) {
        readAttributes(node);
        readChildNodes(node);
    }

    /**
     * 读取属性数据
     *
     * @param node
     */
    private void readAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        Node valueNode = attributes.getNamedItem(RiskMetaConstants.VALUE);
        if (valueNode != null) {
            String strValue = valueNode.getNodeValue();
            if (StringUtils.isNotBlank(strValue)) {
                value = Integer.valueOf(strValue);
            }
        }

    }

    /**
     *
     * @param node
     */
    private void readChildNodes(Node node) {
        //暂时没有其他子节点
    }

    /**
     * Getter method for property <tt>value</tt>.
     *
     * @return property value of value
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Setter method for property <tt>value</tt>.
     *
     * @param value
     *            value to be assigned to property value
     */
    void setValue(Integer value) {
        this.value = value;
    }


    @Override
    public Map<String, Object> toObjectMap() {
        Map<String, Object> objMap = super.toObjectMap();
        objMap.put(RiskMetaConstants.VALUE, value);
        return objMap;
    }
}
