package com.happy.otc.vo;

import com.happy.otc.enums.IdentityRejectReasonEnum;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserIdentityVO implements Serializable {
    @ApiModelProperty(value = "认证编号")
    private Long id;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "证件类型 0:身份证 1:护照")
    private Byte documentType;

    @ApiModelProperty(value = "身份证号码")
    private String identityNumber;

    @ApiModelProperty(value = "图片地址 身份证正面")
    private String imageAddress1;

    @ApiModelProperty(value = "图片地址 身份证反面")
    private String imageAddress2;

    @ApiModelProperty(value = "图片地址 身份证手持")
    private String imageAddress3;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "认证状态 0:未认证 1:待认证 2:已认证 3:认证驳回")
    private Byte status;

    @ApiModelProperty(value = "驳回理由 0:无 1:边角不完整 2:字体不清晰 3:亮度不均匀")
    private String rejectReason;

    @ApiModelProperty(value = "用户类型 0:普通用户 1:商户")
    private Byte userOtcType;

    @ApiModelProperty(value = "其他驳回理由")
    private String otherReason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Byte getDocumentType() {
        return documentType;
    }

    public void setDocumentType(Byte documentType) {
        this.documentType = documentType;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getImageAddress1() {
        return imageAddress1;
    }

    public void setImageAddress1(String imageAddress1) {
        this.imageAddress1 = imageAddress1;
    }

    public String getImageAddress2() {
        return imageAddress2;
    }

    public void setImageAddress2(String imageAddress2) {
        this.imageAddress2 = imageAddress2;
    }

    public String getImageAddress3() {
        return imageAddress3;
    }

    public void setImageAddress3(String imageAddress3) {
        this.imageAddress3 = imageAddress3;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public List<IdentityRejectReasonEnum> getRejectReasonList() {
        if (StringUtils.isBlank(rejectReason)) {
            return Collections.emptyList();
        } else {
            List<IdentityRejectReasonEnum> list = new ArrayList<IdentityRejectReasonEnum>();
            String[] reasons = rejectReason.split(",");
            for (String item : reasons) {
                IdentityRejectReasonEnum reasonEnum = IdentityRejectReasonEnum.getInstance(Integer.parseInt(item));
                list.add(reasonEnum);
            }
            return list;
        }
    }

    public Byte getUserOtcType() {
        return userOtcType;
    }

    public void setUserOtcType(Byte userOtcType) {
        this.userOtcType = userOtcType;
    }

    public String getOtherReason() {
        return otherReason;
    }

    public void setOtherReason(String otherReason) {
        this.otherReason = otherReason;
    }
}