package com.linyoga.operation.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 链式上下文类
 *
 * @author Kris
 * @date 2018/09/17
 */
public class ChainContext {

    /**
     * 操作实现类列表
     */
    private List<Operation> opList;

    /**
     * 记录当前操作实现类的位置
     */
    private int index;

    /**
     * 线程池初始化
     */
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(4, 5
            , 30, TimeUnit.SECONDS
            , new ArrayBlockingQueue<>(10)
            , new ThreadFactoryBuilder().setNameFormat("ChainContext-pool-%d").build());

    /**
     * 增加操作
     *
     * @param operation {@link Operation}
     *                  return this
     */
    public ChainContext addOperation(Operation operation) {
        if (null == opList || opList.isEmpty()) {
            opList = new ArrayList<>();
        }
        opList.add(operation);
        return this;
    }

    /**
     * 增加异步操作
     *
     * @param operation {@link AsyncOperation} 异步操作类
     * @return this
     */
    public ChainContext addAsyncOperation(Operation operation) {
        return addOperation(operation);
    }

    /**
     * 执行操作实现类
     */
    public void doExecute() {
        // 操作列表为空，或当index索引等于操作列表大小即执行完所有操作
        if (null == opList || opList.isEmpty() || this.index == opList.size()) {
            pool.shutdown();
            return;
        }
        // 去除重复的操作实现类
        if (index == 0) {
            opList = this.opList.stream().distinct().collect(Collectors.toList());
        }
        Operation operation = this.opList.get(this.index++);
        // 如果为异步操作，则放入线程池运行
        if (operation instanceof AsyncOperation) {
            pool.execute(() -> operation.execute(this));
            doExecute();
            return;
        }
        operation.execute(this);
    }
}
