package com.happy.otc.bifutures.pojo;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class RestrictionShareValue extends Restriction {

    /**
     * 引用参数
     */
    protected List<RefParameter> refParameters = new ArrayList<RefParameter>();

    /**
     * @param node
     * @param parameter
     */
    public RestrictionShareValue(Node node, RiskParameter parameter) {
        super(node, parameter);
        readRestrictionShareValue(node);
    }

    /**
     *
     * @param node
     */
    private void readRestrictionShareValue(Node node) {
        NodeList childNodes = node.getChildNodes();
        Node childNode = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            childNode = childNodes.item(i);
            String nodeName = childNode.getNodeName();
            if (nodeName.equals(RiskMetaConstants.PREF)) {
                createRefParameter(childNode);
            }
        }
    }

    /**
     *
     * @param node
     * @return
     */
    private void createRefParameter(Node node) {
        RefParameter ref = new RefParameter(node);
        refParameters.add(ref);
    }

    /**
     *
     *
     * @return
     */
    public List<RefParameter> getRefParameters() {
        return Collections.unmodifiableList(refParameters);
    }

}
