package com.linyoga.operation;

import com.linyoga.operation.base.Operation;
import com.linyoga.operation.impl.TestAsyncOperationImpl;
import com.linyoga.operation.impl.TestOperationImpl;

import org.springframework.util.StopWatch;

/**
 * 测试链式操作类
 *
 * @author Kris
 * @email yogalinwork@gmail.com
 * @date 2019-06-27
 */
public class TestOperationService {

    public static void main(String[] args) {
        Operation testAsyncOperation = new TestAsyncOperationImpl();
        Operation testOperation = new TestOperationImpl();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        TestContext.builder()
                .name("init name")
                .build()
                // 增加操作类
                .addOperation(new TestOperationImpl())
                // 增加异步操作类
                .addAsyncOperation(testAsyncOperation)
                .addOperation(testOperation)
                .addAsyncOperation(new TestAsyncOperationImpl())
                .addAsyncOperation(new TestAsyncOperationImpl())
                .doExecute();
        stopWatch.stop();
        System.out.println("总耗时：" + stopWatch.getTotalTimeSeconds() + "s");
    }
}
