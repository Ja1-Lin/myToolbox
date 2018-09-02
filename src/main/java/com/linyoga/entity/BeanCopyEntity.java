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
 * @Time: 17:33
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeanCopyEntity implements Serializable {

    private String name;

    @Convert(name = "beanCopies" , ignorePropertry = "" , className = BeanVo.class)
    private List<BeanEntity> beanCopies;
}
