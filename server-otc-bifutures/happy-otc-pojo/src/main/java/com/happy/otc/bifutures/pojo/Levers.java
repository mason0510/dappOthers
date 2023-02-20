package com.happy.otc.bifutures.pojo;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class Levers extends RiskObject {
    private Map<Integer, LeverRisk> leverRiskMap = new HashMap<Integer, LeverRisk>();

    /**
     *
     * @param node
     * @param parent
     * @param root
     * @param isCopy
     */
    public Levers(Node node, RiskObject parent, RiskObject root, boolean isCopy) {
        super(node, parent, root, isCopy);
        readChildNodes(node);
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
            if (childNode.getNodeName().equals(RiskMetaConstants.LEVER)) {
                createLeverRisk(childNode, isCopy);
            }
        }
    }

    /**
     *
     *
     * @param lever
     * @return
     */
    public LeverRisk getLeverRiskByValue(Integer lever) {
        return leverRiskMap.get(lever);
    }

    /**
     *
     * @param node
     */
    private void createLeverRisk(Node node, boolean isCopy) {
        LeverRisk lever = new LeverRisk(node, this, this.getRoot(), isCopy);
        childrenRo.add(lever);
        leverRiskMap.put(lever.getValue(), lever);
    }

    @SuppressWarnings("rawtypes")
    private static List asList(Object[] a) {
        return java.util.Arrays.asList(a);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> toObjectMap() {

        Map<String, Object> objMap = new HashMap<String, Object>();
        putParameters(objMap);
        List<LeverRisk> lrl = asList(leverRiskMap.values().toArray());
        Collections.sort(lrl, new Comparator<LeverRisk>() {

            @Override
            public int compare(LeverRisk l1, LeverRisk l2) {
                return -l1.getValue().compareTo(l2.getValue());
            }
        });
        List<Map<String, Object>> objList = new ArrayList<Map<String, Object>>();
        for (LeverRisk lr : lrl) {
            objList.add(lr.toObjectMap());
        }
        objMap.put(RiskMetaConstants.LEVERS, objList);
        return objMap;
    }

    /**
     * 获取所有杠杆最大操盘额
     *
     * @return
     */
    public BigDecimal getMaxMoney() {
        BigDecimal max = BigDecimal.ZERO;
        for (LeverRisk lr : leverRiskMap.values()) {
            RiskParameter rp = lr.getRiskParameter("maxMoney");
            String strMaxValue = rp.getValue();
            if (strMaxValue != null) {
                BigDecimal maxValue = new BigDecimal(strMaxValue);
                if (maxValue.compareTo(max) > 0) {
                    max = maxValue;
                }
            }
        }
        return max;
    }

    public Map<String, Object> getMaxMoneyObjectMap() {
        Map<String, Object> maxMoney = new HashMap<String, Object>();
        maxMoney.put(RiskMetaConstants.VALUE, getMaxMoney().toString());
        return maxMoney;
    }
}
