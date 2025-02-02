package com.karacam.bookie.core.flow;

import com.karacam.bookie.configs.RedisConfig;
import com.karacam.bookie.core.BaseConstants;
import com.karacam.bookie.core.enums.FlowStage;
import com.karacam.bookie.core.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
public class FlowStepInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, Object> primaryRedisTemplate;
    private final SessionManager sessionManager;

    @Autowired
    public FlowStepInterceptor(@Qualifier(RedisConfig.PRIMARY_REDIS_TEMPLATE) RedisTemplate<String, Object> primaryRedisTemplate, SessionManager sessionManager) {
        this.primaryRedisTemplate = primaryRedisTemplate;
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        FlowStep flowStep = handlerMethod.getMethodAnnotation(FlowStep.class);

        if (flowStep == null) {
            return true;
        }

        HttpSession session = sessionManager.getCurrentSession();

        if (session == null) {
            session = sessionManager.createAnonymousSession();
            request.setAttribute(BaseConstants.ANONYMOUS_SESSION_ID_PAYLOAD, session.getId());
        }

        if (flowStep.stage() == FlowStage.INIT) {
            sessionManager.addCustomDataToSession(BaseConstants.CURRENT_SESSION_FLOW, flowStep.key());
        } else {
            String currentFlow = Optional.of(session).map(httpSession -> httpSession.getAttribute(BaseConstants.CURRENT_SESSION_FLOW)).map(Object::toString).orElse("");
            if (currentFlow.isEmpty() || !currentFlow.equals(flowStep.key())) {
                throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "FLOW-001");
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        FlowStep flowStep = handlerMethod.getMethodAnnotation(FlowStep.class);

        if (flowStep == null) {
            return;
        }

        if (flowStep.stage() == FlowStage.CONFIRMATION && ex == null && response.getStatus() < 400) {
            HttpSession session = sessionManager.getCurrentSession();
            if (session != null) {
                String transactionKey = Optional.of(session).map(httpSession -> httpSession.getAttribute(BaseConstants.CURRENT_TRANSACTION_KEY)).map(Object::toString).orElse(null);
                if (transactionKey != null) {
                    this.primaryRedisTemplate.delete(transactionKey);
                }
                if (flowStep.anonymousFlow()) {
                    this.sessionManager.killSession();
                } else {
                    this.sessionManager.removeDataFromSession(BaseConstants.CURRENT_SESSION_FLOW);
                }
            }
        }
    }
}
