package com.example.springasync;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

// 当返回值是异步任务，Controller立即返回，Spring自动分配另一个线程处理异步任务，并返回异步结果
@RequiredArgsConstructor
@Slf4j
@RestController
public class AsyncController {

    private final AsyncMethod asyncMethod;

    // 最基本的异步任务
    @GetMapping("/callable")
    public Callable<String> callable() {
        log.info(Thread.currentThread().getName() + "收到请求");

        Callable<String> callable = () -> {
            log.info(Thread.currentThread().getName() + "执行异步任务");
            Thread.sleep(2000);
            return "This is response";
        };

        log.info(Thread.currentThread().getName() + "退出controller");
        return callable;
    }

    // 封装callable，传递超时时间、完成函数、超时函数
    @GetMapping("/webasynctask")
    public WebAsyncTask<String> webAsyncTask() {
        log.info(Thread.currentThread().getName() + "收到请求");

        WebAsyncTask<String> webAsyncTask = new WebAsyncTask<>(3000, () -> {
            log.info(Thread.currentThread().getName() + "执行异步任务");
            Thread.sleep(2000);
            return "This is response";
        });
        // 异步任务完成触发
        webAsyncTask.onCompletion(() -> log.info(Thread.currentThread().getName() + "执行完毕"));
        // 异步任务超时触发
        webAsyncTask.onTimeout(() -> {
            log.info(Thread.currentThread().getName() + "执行超时");
            // 超时的时候，直接抛异常，让外层统一处理超时异常
            throw new TimeoutException("执行超时");
        });

        return webAsyncTask;
    }

    // 异步任务封装，提供完成和超时回调函数，和WebAsyncTask一样。
    // 不同点是DeferredResult仅代表异步结果，异步任务是另外一个异步方法，异步方法负责将异步结果存储到DeferredResult。
    // WebAsyncTask代表异步任务。异步结果是异步线程返回。
    @GetMapping("/deferredresult")
    public DeferredResult<String> deferredResult() {
        log.info(Thread.currentThread().getName() + "收到请求");

        DeferredResult<String> deferredResult = new DeferredResult<>();
        asyncMethod.execute(deferredResult);

        // 异步任务完成回调
        deferredResult.onCompletion(() -> log.info(Thread.currentThread().getName() + "DeferredResult完成"));
        // 异步任务超时回调
        deferredResult.onTimeout(() -> log.info(Thread.currentThread().getName() + "DeferredResult超时"));

        return deferredResult;
    }
}
