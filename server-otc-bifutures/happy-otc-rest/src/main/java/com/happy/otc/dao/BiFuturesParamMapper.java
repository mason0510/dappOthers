package com.happy.otc.dao;

import com.happy.otc.bifutures.entity.Param;
import com.happy.otc.bifutures.entity.Params;

import java.util.List;

/**
 * Created by Administrator on 2018\11\19 0019.
 */
public interface BiFuturesParamMapper {
     void insert(Param param);
     int update(Param param);
    /**
     * 通过变量名获取
     *
     * @param name
     * @return
     */
     Params getByName(String name);
    /**
     * 获取所有的参数
     *
     * @return
     */
     List<Params> getAll();
}
