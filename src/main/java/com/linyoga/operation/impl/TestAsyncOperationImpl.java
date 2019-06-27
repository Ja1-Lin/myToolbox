package com.linyoga.operation.impl;

import com.linyoga.operation.TestContext;
import com.linyoga.operation.base.AsyncOperation;
import com.linyoga.operation.base.ChainContext;

import org.springframework.util.StopWatch;

/**
 * 异步操作实现类
 *
 * @author Kris
 * @email yogalinwork@gmail.com
 * @date 2019-06-27
 */
public class TestAsyncOperationImpl implements AsyncOperation {

    @Override
    public void execute(ChainContext context) {
        TestContext testContext = (TestContext) context;
        System.out.println("TestAsyncOperation 开始，name：" + testContext.getName());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000 * 10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println("TestAsyncOperation 结束，用时：" + stopWatch.getTotalTimeSeconds() + "s");
//        if(testContext.getName().equals("hahah")){
//            throw new RuntimeException("模拟抛异常");
//        }
    }
}
