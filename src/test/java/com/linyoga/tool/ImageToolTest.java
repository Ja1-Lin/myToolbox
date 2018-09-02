package com.linyoga.tool;

import com.google.common.collect.Lists;

import com.linyoga.tool.image.FontIconStyle;
import com.linyoga.tool.image.ImageTool;
import com.linyoga.tool.image.ImgIconStyle;

import org.junit.Test;

import java.awt.*;
import java.io.File;

/**
 * @author: Kris
 * @Date: 2018-09-02
 * @Time: 17:07
 * @Description:
 */
public class ImageToolTest {

    @Test
    public void composeTest(){
        //创建主图文件
        File masterImg = new File("");
        //创建小图文件
        File iconImg = new File("");
        ImgIconStyle imgIconStyle = ImgIconStyle.builder()
                .file(iconImg)
                .x(294).y(281)
                .width(175).height(175)
                .color(Color.WHITE)
                .build();

        //构建字体样式
        FontIconStyle fontIconStyle = FontIconStyle.builder()
                .drawStr("填写上去的文字")
                .x(120).y(200)
                .font(new Font("宋体",Font.BOLD,55))
                .color(Color.WHITE)
                .build();

        //开始合成图片
        ImageTool.composeImage(masterImg
                , Lists.newArrayList(imgIconStyle)
                , Lists.newArrayList(fontIconStyle)
                , "保存的路径");
    }
}
