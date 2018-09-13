package com.sanctacc.mixtogether.config;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class LockInterceptor implements HandlerInterceptor {

    private Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String code = getCode(request);
        if (shouldSkip(request) || StringUtils.isEmpty(code)) {
            return true;
        }
        ReentrantLock newLock = new ReentrantLock(true);
        Lock oldLock = lockMap.putIfAbsent(code, newLock);
        if (oldLock == null) {
            newLock.lock();
        }
        else {
            oldLock.lock();
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String code = getCode(request);
        if (shouldSkip(request) || StringUtils.isEmpty(code)){
            return;
        }
        ReentrantLock lock = lockMap.get(code);
        if (!lock.hasQueuedThreads()) {
            lockMap.remove(code);
        }
        lock.unlock();
    }

    private boolean shouldSkip(HttpServletRequest request) {
            return HttpMethod.GET.toString().equals(request.getMethod());
    }

    private String getCode(HttpServletRequest request) {
        Map<String, String> attribute =
                (HashMap) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return attribute.get("code");
    }

}