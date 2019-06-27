package com.linyoga.operation;

import com.linyoga.operation.base.ChainContext;

import lombok.Builder;
import lombok.Data;

/**
 * 下单流程-上下文实体类 作用： 用于存储流程中的数据
 *
 * @author Kris
 * @date 2018/09/14
 */
@Data
@Builder
public class TestContext extends ChainContext {

    /**
     * 上下文需传递参数
     */
    private String name;

}
