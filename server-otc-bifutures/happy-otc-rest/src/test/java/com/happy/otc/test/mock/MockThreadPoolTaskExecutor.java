package com.happy.otc.test.mock;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class MockThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    @Override
    public void execute(Runnable task) {
        task.run();
    }
}
