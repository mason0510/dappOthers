package com.happy.otc.controller;

import com.bitan.common.utils.RedisUtil;
import com.happy.otc.service.ICapitalDetailService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertArrayEquals;


public class CapitalControllerTest extends BaseJunit4Test {
    @Autowired
    ICapitalDetailService capitalDetailService;
    @Autowired
    RedisUtil redisUtil;

    //@Test
    @Transactional
    public void getCapitalDetailInfo() {
    }

    //@Test
    public void CancelCapitalDetail() {
//       assertEquals(4, newLine.getClosePrice(), 0);
    }



}