package com.happy.otc.vo.manager;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class UserIdentityRequest implements Serializable {
    @ApiModelProperty(value = "认证编号")
    private Long id;
    @ApiModelProperty(value = "状态 2:审核通过 3:审核不通过")
    private Byte status;
    @ApiModelProperty(value = "审核不通过理由(INCOMPLETE:边角不完整 FONT_ERROR:字体不清晰 LIGHT_ERROR:亮度不均匀)")
    private String[] rejectReasons;
    @ApiModelProperty(value = "其他理由")
    private String otherReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String[] getRejectReasons() {
        return rejectReasons;
    }

    public void setRejectReasons(String[] rejectReasons) {
        this.rejectReasons = rejectReasons;
    }

    public String getOtherReason() {
        return otherReason;
    }

    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }
}
