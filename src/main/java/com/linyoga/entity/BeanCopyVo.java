package com.linyoga.entity;

import com.linyoga.annotation.Convert;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: Kris
 * @Date: 2018-09-02
 * @Time: 17:34
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanCopyVo implements Serializable {

    private String name;

    private List<BeanVo> beanCopies;
}
