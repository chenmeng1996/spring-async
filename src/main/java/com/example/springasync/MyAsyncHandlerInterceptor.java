package com.example.springasync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 统一处理异步结果的拦截器，不是必须的
 */
@Slf4j
@Component
public class MyAsyncHandlerInterceptor implements AsyncHandlerInterceptor {

    //Controller返回后触发，所以该方法由Controller线程（nio线程1）执行
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request,
                                               HttpServletResponse response,
                                               Object handler) throws Exception {
        log.info(Thread.currentThread().getName() + "afterConcurrentHandlingStarted");
    }

    // 异步任务
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        log.info(Thread.currentThread().getName() + "preHandle");
        return true;
    }

    // 得到异步结果触发
    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.info(Thread.currentThread().getName()+ "服务调用完成，返回结果给客户端");
    }

    // response异步结果触发
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        log.info(Thread.currentThread().getName() + "afterCompletion");
        if (ex != null) {
            log.info("发生异常:" + ex.getMessage());
        }
    }
}
