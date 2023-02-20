package com.happy.otc.bifutures.pojo;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Map;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class SchemeRisk extends RiskObject {


    /**
     * 方案类型
     */
    private Integer type;

    private Levers levers;

    /**
     *
     * @param node
     * @param parent
     * @param root
     * @param isCopy
     */
    public SchemeRisk(Node node, RiskObject parent, RiskObject root, boolean isCopy) {
        super(node, parent, root, isCopy);
        readAttributes(node);
        readChildNodes(node);
    }

    private void readAttributes(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        String strValue = attributes.getNamedItem(RiskMetaConstants.TYPE).getNodeValue();
        if (strValue != null) {
            type = Integer.valueOf(strValue);
        }
    }

    /**
     *
     * @param node
     */
    private void readChildNodes(Node node) {
        NodeList childNodes = node.getChildNodes();
        Node childNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(RiskMetaConstants.LEVERS)) {
                createLevers(childNode, this.isCopy);
            }
        }
    }

    /**
     * Getter method for property <tt>levers</tt>.
     *
     * @return property value of levers
     */
    public Levers getLevers() {
        return levers;
    }

    /**
     *
     *
     * @param node
     * @param isCopy
     */
    private void createLevers(Node node, boolean isCopy) {
        Levers tlevers = new Levers(node, this, this.getRoot(), isCopy);
        childrenRo.add(tlevers);
        levers = tlevers;
    }

    /**
     *
     * 根据杠杆值获取杠杆风险
     *
     * @param leverValue
     * @return
     */
    /* public LeverRisk getLeverRisk(Integer leverValue) {
         LeverRisk sr = leverRiskMap.get(leverValue);
         if (sr == null) {
             logger.error("没有找到杠杆类型：" + leverValue);
             throw new IllegalArgumentException("没有找到杠杆类型：" + leverValue);
         }
         return sr;
     }*/

    /**
     * Getter method for property <tt>type</tt>.
     *
     * @return property value of type
     */
    public Integer getType() {
        return type;
    }


    @Override
    public Map<String, Object> toObjectMap() {
        Map<String, Object> objMap = super.toObjectMap();
        if (levers != null) {
            //  objMap.put(RiskMetaConstants.MAX_MONEY, levers.getMaxMoneyObjectMap());
        }
        return objMap;
    }

}
