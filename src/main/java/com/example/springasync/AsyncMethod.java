package com.example.springasync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AsyncMethod {

    // @Async会启动新的线程执行方法
    @Async
    public void execute(DeferredResult<String> deferredResult) {
        log.info(Thread.currentThread().getName() + "开始处理Task");
        try {
            //模拟长时间任务调用
            TimeUnit.SECONDS.sleep(2);
            //2s后给DeferredResult发送消息，告诉DeferredResult已经处理完了，可以返回给客户端
            deferredResult.setResult("异步返回的结果");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Async
    public void executeWithInput(DeferredResult<String> deferredResult, String param) {
        log.info(Thread.currentThread().getName() + "开始处理Task");
        try {
            //模拟长时间任务调用
            TimeUnit.SECONDS.sleep(2);
            //2s后给DeferredResult发送消息，告诉DeferredResult已经处理完了，可以返回给客户端
            deferredResult.setResult("hello," + param);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
