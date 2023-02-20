package com.happy.otc.bifutures.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public class Param implements Serializable {

    private static final long serialVersionUID = -7906340109706100686L;

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

    public Param(Long id){
        this.id = id;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((time == null) ? 0 : time.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Param other = (Param) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (time == null) {
            if (other.time != null)
                return false;
        } else if (!time.equals(other.time))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Param [id=" + id + ", name=" + name + ", value=" + value + ", remark=" + remark + ", time=" + time
                + ", version=" + Arrays.toString(version) + "]";
    }
}
