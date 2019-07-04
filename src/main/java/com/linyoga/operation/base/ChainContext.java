package com.linyoga.operation.base;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 链式上下文类
 *
 * @author Kris
 * @date 2018/09/17
 */
@Slf4j
public abstract class ChainContext {

    /**
     * 操作实现类列表
     */
    private List<Operation> opList;

    /**
     * 缓存各个链式操作中的操作列表
     */
    private static Map<String,List<Operation>> opCacheMap = new HashMap<>(8);

    /**
     * 记录当前操作实现类的位置
     */
    private int index;

    /**
     * 是否已经完成
     */
    private boolean isComplete = false;

    /**
     * 线程池初始化
     * 拒绝策略：重新执行
     */
    private final static ThreadPoolExecutor POOL = new ThreadPoolExecutor(5, 40,
            30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(50),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("ChainContext-pool-%d");
                return thread;
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    /**
     * 增加操作
     *
     * @param operation {@see Operation}
     * @return this
     */
    public ChainContext addOperation(Operation operation) {
        if(isComplete){
            return this;
        }
        // 若缓存中有操作列表，则执行执行doExecute
        if (opList == null && (opList = opCacheMap.get(this.getClass().getSimpleName())) != null) {
            this.doExecute();
            return this;
        }
        if (null == opList || opList.isEmpty()) {
            opList = new ArrayList<>();
        }
        opList.add(operation);
        return this;
    }

    /**
     * 增加异步操作
     *
     * @param operation {@see AsyncOperation} 异步操作类
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
        if (isComplete || null == opList || opList.isEmpty()) {
            return;
        }

        // 在最后一步验证是否缓存到集合里
        if (this.index == opList.size()){
            // 若无缓存，再加入缓存集合中
            opCacheMap.putIfAbsent(this.getClass().getSimpleName(),opList);
            this.isComplete = true;
            return;
        }

        // 去除重复的操作实现类
        if (index == 0) {
            opList = this.opList.stream().distinct().collect(Collectors.toList());
        }

        Operation operation = this.opList.get(this.index++);
        // 如果为异步操作，则放入线程池运行
        if (operation instanceof AsyncOperation) {
            POOL.execute(() -> operation.execute(this));
            doExecute();
            return;
        }
        operation.execute(this);
    }
}
