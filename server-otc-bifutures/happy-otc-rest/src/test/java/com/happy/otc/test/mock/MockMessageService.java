package com.happy.otc.test.mock;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.message.vo.SendJpushMessageRequest;
import com.bitan.message.vo.SendMailMessageRequest;
import com.bitan.message.vo.SendMessageRequest;
import com.bitan.message.vo.SendOtcMessageRequest;
import com.happy.otc.service.remote.IMessageService;

import java.util.ArrayList;
import java.util.List;

public class MockMessageService implements IMessageService {
    public List<SendOtcMessageRequest> otcRequest = new ArrayList<>();

    @Override
    public Result<Boolean> sendMessage(SendMessageRequest sendMessageRequest) throws BizException {
        return null;
    }

    @Override
    public Result<Boolean> sendOtcMessage(SendOtcMessageRequest sendOtcMessageRequest) throws BizException {
        otcRequest.add(sendOtcMessageRequest);
        return Result.createSuccess(true);
    }

    @Override
    public Result<Boolean> sendMailMessage(SendMailMessageRequest sendMailMessageRequest) throws BizException {
        return null;
    }

    @Override
    public Result<Boolean> sendJpushMessage(SendJpushMessageRequest sendJpushMessageRequest) throws BizException {
        return null;
    }

    public void clear() {
        otcRequest.clear();
    }
}
