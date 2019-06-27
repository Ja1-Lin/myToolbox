package com.linyoga.operation.impl;

import com.linyoga.operation.TestContext;
import com.linyoga.operation.base.ChainContext;
import com.linyoga.operation.base.Operation;

import org.springframework.util.StopWatch;

/**
 * 同步操作实现类
 *
 * @author Kris
 * @email yogalinwork@gmail.com
 * @date 2019-06-27
 */
public class TestOperationImpl implements Operation {

    @Override
    public void execute(ChainContext context) {
        TestContext testContext = (TestContext) context;
        System.out.println("TestOperation 开始，name：" + testContext.getName());
        testContext.setName("hahah");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000 * 10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        stopWatch.stop();
        System.out.println("TestOperation 结束，用时：" + stopWatch.getTotalTimeSeconds() + "s");
        testContext.doExecute();
    }


}
