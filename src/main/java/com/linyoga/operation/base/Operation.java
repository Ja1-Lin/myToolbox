package com.linyoga.operation.base;

/**
 * 流程操作抽象接口
 *
 * @author Kris
 * @date 2018/09/14
 */
public interface Operation {

    /**
     * 流程执行方法
     *
     * @param context 上下文信息，存放共用的数据
     */
    void execute(ChainContext context);
}
