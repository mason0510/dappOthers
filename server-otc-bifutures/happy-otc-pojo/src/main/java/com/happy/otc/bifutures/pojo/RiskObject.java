package com.happy.otc.bifutures.pojo;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class RiskObject extends RFUnit{

    /**
     * 此对象是否为拷贝
     */
    protected boolean isCopy;

    /**
     * 风险名称
     */
    protected String name;

    /**
     * 风险类别
     */
    protected String category;

    /**
     * 父节点数据
     */
    protected RiskObject parent;

    /**
     * 根节点
     */
    protected RiskObject root;

    /**
     * 子节点数据
     */
    protected List<RiskObject> childrenRo = new ArrayList<RiskObject>();

    /**
     * 子节点
     */
    protected Map<String, RiskObject> subObjects = new HashMap<String, RiskObject>();

    /**
     * 风险参数
     */
    protected Map<String, RiskParameter> parameters = new ConcurrentHashMap<String, RiskParameter>();
    private Map<String, String> attributes = new HashMap<String, String>();
    /**
     * 风险参数列表
     */
    protected List<RiskParameter> parameterList = new ArrayList<RiskParameter>();

    /**
     *
     * @param node
     * @param parent
     * @param root
     */
    public RiskObject(Node node, RiskObject parent, RiskObject root, boolean isCopy) {
        this.parent = parent;
        this.root = root;
        this.isCopy = isCopy;
        readRiskObject(node);
    }

    /**
     * 空构造方法
     */
    public RiskObject() {}
    /**
     * 读取属性和参数
     *
     * @param node
     */
    private void readRiskObject(Node node) {
        NamedNodeMap nodeMap = node.getAttributes();
        Node nameNode = nodeMap.getNamedItem(RiskMetaConstants.NAME);
        if (nameNode != null) {
            name = nameNode.getNodeValue();
            this.addAttributes(RiskMetaConstants.NAME, name);
        }

        Node cateNode = nodeMap.getNamedItem(RiskMetaConstants.CATEGORY);
        if (cateNode != null) {
            category = cateNode.getNodeValue();
            this.addAttributes(RiskMetaConstants.CATEGORY, category);
        }
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeName().equals(RiskMetaConstants.P)) {
                createParameter(childNode);
            } else if (childNode.getNodeName().equals(RiskMetaConstants.RISK_OBJECT)) {
                createRiskObject(childNode, isCopy);
            }
        }
    }
    public void addAttributes(String key, String value) {
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            attributes.put(key, value);
        }
    }

    @Override
    public String getUnitName() {
        return RiskMetaConstants.RISK_OBJECT;
    }

    /**
     * 创建RiskObject 对象
     *
     * @param childNode
     * @param isCopy2
     */
    private void createRiskObject(Node childNode, boolean isCopy2) {
        RiskObject ro = new RiskObject(childNode, this, this.getRoot(), isCopy2);
        childrenRo.add(ro);
        subObjects.put(ro.getName(), ro);
        this.addRFUnit(ro);
    }

    /**
     *
     * 替换参数
     *
     * @param pCategory
     * @param pName
     * @param value
     */
    public void replaceParameterValue(String pCategory, String pName, String value) {
        if (!isCopy) {
            throw new IllegalArgumentException("试图修改非拷贝风险对象");
        }
        RiskParameter parameter = findParameter(pCategory, pName);
        if (parameter != null) {
            parameter.replaceValue(value);
        }
    }

    public RiskParameter getParameter(String pName) {
        if (pName != null) {
            return parameters.get(pName);
        }
        return null;
    }

    /**
     *
     * 查找风险参数

     */
    public RiskParameter findParameter(String pCategory, String pName) {
        if (pCategory != null && pName != null) {
            RiskObject ro = findRiskObject(pCategory);
            if (ro != null) {
                RiskParameter p = ro.getRiskParameter(pName);
                if (p.isRef()) {
                    return findParameter(p.getRefCategory(), p.getRefName());
                }
                return p;
            }
        }
        return null;
    }

    /**
     * 查找风险对象
     *
     * @param categoryFind
     * @return
     */
    public RiskObject findRiskObject(String categoryFind) {
        if (categoryFind != null) {
            List<RiskObject> all = getAllRiskObjects();
            for (RiskObject ro : all) {
                if (categoryFind.equals(ro.getCategory())) {
                    return ro;
                }
            }
        }
        return null;
    }

    /**
     * 获取所欲RiskObject对象
     *
     * @return
     */
    private List<RiskObject> getAllRiskObjects() {
        List<RiskObject> roList = new ArrayList<RiskObject>();
        putAllRiskObjects(roList, this);
        return roList;
    }

    /**
     * 递归查找所有RiskObject
     *
     * @param roList
     * @param ro
     */
    private void putAllRiskObjects(List<RiskObject> roList, RiskObject ro) {
        roList.add(ro);
        for (RiskObject children : ro.getChildrenRo()) {
            putAllRiskObjects(roList, children);
        }
    }

    /**
     *
     * @param node
     */
    protected void createParameter(Node node) {
        RiskParameter p = new RiskParameter(node, this, isCopy);
        parameters.put(p.getName(), p);

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

    /**
     * Getter method for property <tt>children</tt>.
     *
     * @return property value of children
     */
    public List<RiskObject> getChildrenRo() {
        return Collections.unmodifiableList(childrenRo);
    }

    /**
     *
     * 根据参数名获取对象
     *
     * @param pName
     * @return
     */
    public RiskParameter getRiskParameter(String pName) {
        RiskParameter rp = parameters.get(pName);
        if (rp == null) {

            throw new IllegalArgumentException("没有找到风险参数名：" + pName);
        }
        if (rp.isRef()) {
            return getRoot().findParameter(rp.getRefCategory(), rp.getRefName());
        }
        return rp;
    }

    /**
     * 转化为ObjectMap 提供给前台JSON
     *
     * @return
     */
    public Map<String, Object> toObjectMap() {
        Map<String, Object> objMap = new HashMap<String, Object>();
        putParameters(objMap);
        putChildrenRo(objMap);
       /* int version = JSONObject.parseObject(String.valueOf(objMap)).toString().hashCode();
        objMap.put(RiskMetaConstants.VERSION, version);*/
        return objMap;
    }

    /**
     *
     * @param objMap
     */
    private void putChildrenRo(Map<String, Object> objMap) {
        for (RiskObject ro : childrenRo) {
            Map<String, Object> riskObjectMap = ro.toObjectMap();
            objMap.put(ro.getName(), riskObjectMap);
        }
    }

    /**
     *
     * @param objMap
     */
    protected void putParameters(Map<String, Object> objMap) {
        for (RiskParameter p : parameters.values()) {
            objMap.put(p.getName(), p.toObjectMap());
        }
    }

    /**
     * Getter method for property <tt>parent</tt>.
     *
     * @return property value of parent
     */
    public RiskObject getParent() {
        return parent;
    }

    /**
     * Setter method for property <tt>parent</tt>.
     *
     * @param parent
     *            value to be assigned to property parent
     */
    void setParent(RiskObject parent) {
        this.parent = parent;
    }

    /**
     * Getter method for property <tt>root</tt>.
     *
     * @return property value of root
     */
    public RiskObject getRoot() {
        return root;
    }

    /**
     * Setter method for property <tt>root</tt>.
     *
     * @param root
     *            value to be assigned to property root
     */
    void setRoot(RiskObject root) {
        this.root = root;
    }
    /**
     * 获取所有子节点参数
     *
     * @return
     */
    public List<RiskParameter> getAllSubParameters() {
        List<RiskParameter> allSubParameter = new ArrayList<RiskParameter>();
        allSubParameter.addAll(parameterList);
        for (RiskObject ro : childrenRo) {
            allSubParameter.addAll(ro.getAllSubParameters());
        }
        return allSubParameter;
    }

    /**
     * 根据名字获取子节点
     *
     * @param name
     * @return
     */
    public RiskObject getRiskObject(String name) {
        if (name != null) {
            return this.subObjects.get(name);
        }
        return null;
    }

}
