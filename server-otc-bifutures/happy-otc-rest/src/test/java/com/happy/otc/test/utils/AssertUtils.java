package com.happy.otc.test.utils;

import com.bitan.common.exception.BizException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AssertUtils {
    public static void assertErrorCode(Runnable runnable, int code) {
        try {
            runnable.run();
            fail("should throw exception with code: " + code);
        } catch (BizException e) {
            assertEquals(code, e.getCode().intValue());
        }
    }
}
