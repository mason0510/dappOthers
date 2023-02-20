package com.happy.otc.bifutures.vo;

import com.happy.otc.bifutures.entity.FuturesStrategy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018\11\22 0022.
 */
public class SaleFuturesStrategyVO implements Serializable {
    private static final long serialVersionUID = 3110578017603142588L;
    private List<FuturesStrategy> dataList;
    private Integer pageIndex;
    private Integer totalPage;

    public SaleFuturesStrategyVO(){}

    public SaleFuturesStrategyVO(List<FuturesStrategy> dataList, Integer pageIndex, Integer totalPage) {
        this.dataList = dataList;
        this.pageIndex = pageIndex;
        this.totalPage = totalPage;
    }

    public List<FuturesStrategy> getDataList() {
        return dataList;
    }

    public void setDataList(List<FuturesStrategy> dataList) {
        this.dataList = dataList;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public String toString() {
        return "SaleFuturesStrategyVO{" +
                "dataList=" + dataList +
                ", pageIndex=" + pageIndex +
                ", totalPage=" + totalPage +
                '}';
    }
}
