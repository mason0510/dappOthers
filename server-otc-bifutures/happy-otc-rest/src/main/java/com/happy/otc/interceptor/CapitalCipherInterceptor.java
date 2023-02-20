package com.happy.otc.interceptor;

import com.bitan.common.component.ApiResponseCode;
import com.bitan.common.constant.Constants;
import com.bitan.common.exception.BizException;
import com.bitan.common.utils.RedisUtil;
import com.happy.otc.annotation.CapitalCipherCheck;
import com.happy.otc.contants.MessageCode;
import com.happy.otc.contants.Contants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class CapitalCipherInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisUtil redisUtil;

    @Value("${capital.cipher.error.count}")
    private Integer cipherErrCount;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        CapitalCipherCheck annotation = method.getAnnotation(CapitalCipherCheck.class);
        if (annotation != null) {
            String strUserId = request.getHeader(Constants.REQUEST_HEADER_USER_ID);
            Long userId;
            if (StringUtils.isBlank(strUserId)) {
                BizException.fail(ApiResponseCode.NOT_LOG, "用户未登录");
            }
            userId = Long.valueOf(strUserId);

            String count = redisUtil.getStringValue(Contants.REDIS_USER_CAPITAL_CIPHER_ERR + userId);
            int intCount = StringUtils.isBlank(count) ? 0 : Integer.parseInt(count);

            if (intCount >= cipherErrCount) {
                BizException.fail(MessageCode.CAPITAL_CIPHER_ERR_OVER, "资金密码错误超过3次，账户被冻结24小时");
            }
        }
        // 没有注解通过拦截
        return super.preHandle(request, response, handler);
    }
}
