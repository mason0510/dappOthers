package com.happy.otc.vo.manager;

import java.io.Serializable;
import java.util.List;

public class AddSellerRequest implements Serializable {
    private List<Long> userIdList;

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }
}
