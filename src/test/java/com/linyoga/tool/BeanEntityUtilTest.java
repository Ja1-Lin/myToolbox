package com.linyoga.tool;

import com.google.common.collect.Lists;

import com.linyoga.entity.BeanCopyEntity;
import com.linyoga.entity.BeanCopyVo;
import com.linyoga.entity.BeanEntity;

import org.junit.Before;
import org.junit.Test;

/**
 * 测试用例
 * @author: Kris
 * @Date: 2018-09-02
 * @Time: 17:14
 * @Description:
 */
public class BeanEntityUtilTest {

    private BeanCopyEntity beanCopyEntity = new BeanCopyEntity();

    @Before
    public void before(){
        BeanEntity beanEntity = BeanEntity.builder()
                .name("first")
                .build();
        BeanEntity second = BeanEntity.builder()
                .name("second")
                .build();
        beanCopyEntity.setBeanCopies(Lists.newArrayList(beanEntity,second));
    }

    @Test
    public void copyTest(){
        System.out.println("beanCopyEntity:" + beanCopyEntity.toString());
        BeanCopyVo beanVo = BeanCopyUtil.copyBean(beanCopyEntity , BeanCopyVo.class);
        System.out.println("BeanCopyVo:" + beanVo.toString());
    }
}
