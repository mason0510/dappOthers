package com.happy.otc.bifutures.pojo;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class RestrictionFormula extends Restriction {
    /**
     * JS 格式化去除回车换行
     */
    private static final Pattern Format_Formula = Pattern.compile("\t|\r|\n");

    /**
     * 公式文本
     */
    private String formulaStr;

    /**
     * @param node
     * @param riskParameter
     */
    public RestrictionFormula(Node node, RiskParameter riskParameter) {
        super(node, riskParameter);
        readRestrictionFormula(node);
    }

    /**
     * @param node
     */
    private void readRestrictionFormula(Node node) {
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            Node item = node.getChildNodes().item(i);
            if (StringUtils.isNotBlank(item.getTextContent())) {
                String str = item.getTextContent();
                this.formulaStr = formatJsString(str);
                return;
            }
        }
    }

    /**
     * Getter method for property <tt>formulaStr</tt>.
     *
     * @return property value of formulaStr
     */
    public String getFormulaStr() {
        return formulaStr;
    }

    /**
     * Setter method for property <tt>formulaStr</tt>.
     *
     * @param formulaStr value to be assigned to property formulaStr
     */
    void setFormulaStr(String formulaStr) {
        this.formulaStr = formulaStr;
    }



    @Override
    public Object calculate(Object... args) {
        Object result = null;
        if (StringUtils.isNotBlank(formulaStr)) {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("javascript");
            try {
                engine.eval(formulaStr);
                if (engine instanceof Invocable) {
                    Invocable invoke = (Invocable) engine;
                    result = invoke.invokeFunction(name, args);
                }
            } catch (NoSuchMethodException e) {

            } catch (ScriptException e) {

            }
        }
        return result;

    }

    /**
     * 格式化JS 文本，去掉将回车换行
     *
     * @param str
     * @return
     */
    public static String formatJsString(String str) {
        if (StringUtils.isNotBlank(str)) {
            Matcher m = Format_Formula.matcher(str);
            return m.replaceAll("");
        }
        return null;
    }


    @Override
    public String toJsonValue() {
        return formulaStr;
    }
}
