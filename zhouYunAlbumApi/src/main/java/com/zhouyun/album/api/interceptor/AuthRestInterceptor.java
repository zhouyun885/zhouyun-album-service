package com.zhouyun.album.api.interceptor;

import com.zhouyun.album.constant.ConfigKeyConstant;
import com.zhouyun.client.annotation.IgnoreAppToken;
import com.zhouyun.client.config.AppAuthConfig;
import com.zhouyun.common.constant.CommonConstants;
import com.zhouyun.common.context.BaseContextHandler;
import com.zhouyun.common.enums.AuthExceptionCodeEnum;
import com.zhouyun.common.jwtkits.IJWTInfo;
import com.zhouyun.common.jwtkits.JWTHelper;
import com.zhouyun.common.jwtkits.JWTInfo;
import com.zhouyun.common.jwtkits.TokenVo;
import com.zhouyun.core.exception.BizException;
import com.zhouyun.core.redis.RedisUtil;
import com.zhouyun.service.utils.JwtTokenUtil;
import com.zhouyun.tools.utils.ConverterUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author zhouyun
 * @Date 2020/10/19
 */
@Slf4j
public class AuthRestInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AppAuthConfig appAuthConfig;
    @Autowired
    private ConfigKeyConstant configKeyConstant;

    //token过期时间距离当前时间小于等于30分钟的话，就执行刷新替换动作
    private static long maxMillis = 30 * 60 * 1000l;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HandlerMethod handlerMethod = null;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        } else {
            return super.preHandle(request, response, handler);
        }
        // 配置该注解，说明不进行用户拦截
        IgnoreAppToken annotation = handlerMethod.getBeanType().getAnnotation(IgnoreAppToken.class);
        if (annotation == null) {
            annotation = handlerMethod.getMethodAnnotation(IgnoreAppToken.class);
        }
        if (annotation != null) {
            return super.preHandle(request, response, handler);
        }

        //优先从Header中获取Token
        String tokenOfThroughSession = request.getHeader(appAuthConfig.getTokenHeader());
        if (StringUtils.isBlank(tokenOfThroughSession)) {
            //其次从Cookies中获取Token
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (null != cookie && cookie.getName().equals(appAuthConfig.getTokenHeader())) {
                        tokenOfThroughSession = cookie.getValue();
                        break;
                    }
                }
            }
            //最后从URL追加的参数获取Token
            if (StringUtils.isBlank(tokenOfThroughSession) && request.getMethod().equals(RequestMethod.GET.name())) {
                tokenOfThroughSession = request.getParameter(appAuthConfig.getTokenHeader());
            }
        }

        if (StringUtils.isBlank(tokenOfThroughSession)) {
            throw new BizException(AuthExceptionCodeEnum.TOKEN_NOT_NULL);
        }

        //可能为空/为过期时间/为新重置的token
        IJWTInfo infoOfJwtFromToken = jwtTokenUtil.getInfoFromToken(tokenOfThroughSession);
        String userCode = infoOfJwtFromToken.getUserCode();
        if (StringUtils.isBlank(userCode)) {
            throw new BizException(AuthExceptionCodeEnum.JWT_TOKEN_EXPIRED);
        }
        Object redisTokenRealValueString = redisUtil.get(userCode);
        //为空的话，正常情况下为重置密码/手动退出操作导致的结果
        if (null == redisTokenRealValueString) {
            throw new BizException(AuthExceptionCodeEnum.JWT_TOKEN_USER_CODE_NOT_EXIST);
        }
        Map<String, Object> tokenInfoMap = (Map<String, Object>) redisTokenRealValueString;
        //从redis存取的tokenMap里获取到最新的token，再进行比较
        String token = (String) tokenInfoMap.get(CommonConstants.JWT_TOKEN_MAP_KEY_TOKEN);
        if (StringUtils.isBlank(token)) {
            throw new BizException(AuthExceptionCodeEnum.JWT_TOKEN_EXPIRED);
        } else if (!token.equals(tokenOfThroughSession)) {
            //比较redis的token是不是当前的token，如果不是说明token已过期，需要中心登录
            throw new BizException(AuthExceptionCodeEnum.JWT_TOKEN_USER_CODE_NOT_EXIST);
        }
        //比较正确之后再计算过期时间，在最后半个小时里进行token的刷新
        Date tokenExpireTime = (Date) tokenInfoMap.get(CommonConstants.JWT_TOKEN_MAP_KEY_TOKEN_EXPIRE_TIME);
        if (tokenExpireTime != null) {
            //当前请求的token离过期还剩余时间在30m内的话，执行刷新token动作
            long millis = tokenExpireTime.getTime() - Calendar.getInstance().getTimeInMillis();
            if (millis <= 0) {
                throw new BizException(AuthExceptionCodeEnum.JWT_TOKEN_EXPIRED);
            } else if (millis <= maxMillis) {
                IJWTInfo fromToken = JWTHelper.getInfoFromToken(token, appAuthConfig.getPubKeyPath());
                TokenVo tokenVo = JWTHelper.generateToken(new JWTInfo(fromToken.getUserName(), fromToken.getUserId(), fromToken.getUserName(), fromToken.getCustomerCompanyId(), fromToken.getAppId(), fromToken.getUserCode()),configKeyConstant.jwtPriKeyPath, ConverterUtils.toInteger(configKeyConstant.jwtExpireTime));
                //为避免同一操作页面，多个请求都落在半小时刷新token区间段，保证只重新生成一次token
                tokenOfThroughSession = tokenVo.getToken();
                Map<String, Object> tokenMap = new HashMap<>();
                tokenMap.put(CommonConstants.JWT_TOKEN_MAP_KEY_TOKEN, tokenVo.getToken());
                tokenMap.put(CommonConstants.JWT_TOKEN_MAP_KEY_TOKEN_EXPIRE_TIME, tokenVo.getExpireTime());
                redisUtil.set(fromToken.getUserCode(), tokenMap, ConverterUtils.toLong(tokenVo.getExpire()));
            }
        }
        //返回Token：有两种情况1)原始有效期内的token2)或者刷新阶段后的token
        response.setHeader(appAuthConfig.getTokenHeader(), tokenOfThroughSession);
        //设置用户基本信息，设置用户会话上下文用户基本信息供API层使用,基于ThreadLocal方式存储
        BaseContextHandler.setUserCompany(infoOfJwtFromToken.getCustomerCompanyId());
        BaseContextHandler.setUserName(infoOfJwtFromToken.getUserName());
        BaseContextHandler.setNickName(infoOfJwtFromToken.getNickName());
        BaseContextHandler.setUserId(infoOfJwtFromToken.getUserId());
        BaseContextHandler.setAppId(infoOfJwtFromToken.getAppId());
        BaseContextHandler.setToken(tokenOfThroughSession);
        log.info("tokenOfThroughSession={} \\r\\n appId={}, userName={},userCode={}", tokenOfThroughSession, infoOfJwtFromToken.getAppId(), infoOfJwtFromToken.getUserName(), infoOfJwtFromToken.getUserCode());
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContextHandler.remove();
        super.afterCompletion(request, response, handler, ex);
    }
}
