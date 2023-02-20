package com.happy.otc.bifutures.pojo;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class MetaRisk extends RiskObject {

    /**
     * 方案风险map
     */
    private Map<Integer, SchemeRisk> schemeRiskMap = new HashMap<Integer, SchemeRisk>();

    /**
     *
     * @param node
     * @param parent
     * @param isCopy
     */
    public MetaRisk(Node node, RiskObject parent, boolean isCopy) {
        super(node, parent, null, isCopy);
        setRoot(this);
        readChildNodes(node);
    }

    /**
     * 根据类型找方案风险
     *
     * @param type
     */
    public SchemeRisk findSchemeRiskByType(Integer type) {
        return schemeRiskMap.get(type);
    }

    /**
     *
     * @param node
     */
    private void readChildNodes(Node node) {

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if (childNode.getNodeName().equals(RiskMetaConstants.SCHEME)) {
                createSchemeRisk(childNode, isCopy);
            }
        }

    }

    /**
     * 创建方案风险
     *
     * @param node
     */
    private void createSchemeRisk(Node node, boolean isCopy) {
        SchemeRisk sr = new SchemeRisk(node, this, this.getRoot(), isCopy);
        childrenRo.add(sr);
        schemeRiskMap.put(sr.getType(), sr);
    }
}
