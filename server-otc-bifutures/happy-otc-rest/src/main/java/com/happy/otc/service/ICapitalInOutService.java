package com.happy.otc.service;

import com.happy.otc.dto.CapitalInOutDTO;
import com.happy.otc.entity.CapitalInOut;
import com.github.pagehelper.PageInfo;

public interface ICapitalInOutService {

    public PageInfo<CapitalInOutDTO> getCapitalInOutList(CapitalInOut capitalInOut, Integer pageIndex, Integer pageSize);

    public Boolean delCapitalInOut(Long id);

    public Boolean updateCapitalInOut(CapitalInOut capitalInOut);

    public Boolean addCapitalInOut(CapitalInOut capitalInOut);

    Boolean withdrawals(Long id);
}
