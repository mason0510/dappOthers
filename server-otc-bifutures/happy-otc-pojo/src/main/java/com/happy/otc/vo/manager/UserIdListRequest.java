package com.happy.otc.vo.manager;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhuligang
 * @Date: 2018/8/13 15:55
 * @Description:
 */
public class UserIdListRequest implements Serializable {
    private List<Long> userIdList;

    public List<Long> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Long> userIdList) {
        this.userIdList = userIdList;
    }
}
