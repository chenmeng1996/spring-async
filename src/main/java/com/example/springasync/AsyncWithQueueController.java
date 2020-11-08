package com.example.springasync;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@RequiredArgsConstructor
@RestController
public class AsyncWithQueueController {

    private final BlockingQueue<RequestVO<String, String>> queue = new LinkedBlockingDeque<>(100);
    private final AsyncMethod asyncMethod;

    // 请求太多时，处理异步任务的线程池数量会不够，导致拒绝请求。
    // 用缓冲队列存储请求，线程池顺序执行，可以解决。
    @GetMapping("/queue")
    public DeferredResult<String> deferredResult(String param) throws InterruptedException {
        DeferredResult<String> deferredResult = new DeferredResult<>();
        RequestVO<String, String> requestVO = new RequestVO<>(param, deferredResult);
        // put：如果队列满，则阻塞等待
        queue.put(requestVO);
        return deferredResult;
    }

    @AllArgsConstructor
    @Data
    static class RequestVO<I, O> {
        //请求参数
        private I params;
        //响应结果
        private DeferredResult<O> result;
    }

    // 监听队列
    @RequiredArgsConstructor
    @Component
    private class QueueListener {

        private final QueueExecutor executor;

        @PostConstruct
        public void init() {
            executor.start();
        }

        @PreDestroy
        public void destroy() {

        }
    }

    // 处理队列的后台线程
    @Component
    private class QueueExecutor extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    // 当队列空，阻塞等待
                    RequestVO<String, String> requestVO = queue.take();
                    // 启动异步线程执行（因为配置了线程池，实际上是提交到线程池）
                    asyncMethod.executeWithInput(requestVO.getResult(), requestVO.getParams());
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
