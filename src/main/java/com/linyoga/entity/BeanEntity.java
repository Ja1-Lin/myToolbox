package com.linyoga.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeanEntity implements Serializable {

    private String name;
}

