package com.happy.otc.bifutures.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\20 0020.
 */
public class Params implements Serializable {
    private static final long serialVersionUID = -133085160648145778L;

    private Long id;
    /**
     * 变量名
     */
    private String name;
    /**
     * 值
     */
    private String value;

    /**
     * 备注
     */
    private String remark;
    /**
     * 插入时间
     */
    private Date time;


    private byte[] version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public byte[] getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        this.version = version;
    }
}
