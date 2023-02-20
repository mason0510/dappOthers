package com.happy.otc.service.remote;

import com.bitan.common.exception.BizException;
import com.bitan.common.vo.Result;
import com.bitan.message.vo.SendJpushMessageRequest;
import com.bitan.message.vo.SendMailMessageRequest;
import com.bitan.message.vo.SendMessageRequest;
import com.bitan.message.vo.SendOtcMessageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by niyang on 2017/8/11.
 */
@FeignClient("message-server")
@RequestMapping(value = "/message-rest")
public interface IMessageService {

    /**
     * 发送短信
     *
     * @param sendMessageRequest
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/send-message", method = RequestMethod.POST)
    public Result<Boolean> sendMessage(@RequestBody SendMessageRequest sendMessageRequest) throws BizException;

    /**
     * otc发送短信
     *
     * @param sendOtcMessageRequest
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/send-otc-message", method = RequestMethod.POST)
    public Result<Boolean> sendOtcMessage(@RequestBody SendOtcMessageRequest sendOtcMessageRequest) throws BizException;

    /**
     * 发送邮件
     *
     * @param sendMailMessageRequest
     * @return
     * @throws BizException
     */
    @RequestMapping(value = "/send-mail-message", method = RequestMethod.POST)
    public Result<Boolean> sendMailMessage(@RequestBody SendMailMessageRequest sendMailMessageRequest) throws BizException;

    @ApiOperation(value = "推送极光消息")
    @RequestMapping(value = "/send-jpush-message", method = RequestMethod.POST)
    public Result<Boolean> sendJpushMessage(@RequestBody SendJpushMessageRequest sendJpushMessageRequest) throws BizException;
}
