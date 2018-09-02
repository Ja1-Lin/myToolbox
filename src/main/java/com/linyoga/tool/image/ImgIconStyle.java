package com.linyoga.tool.image;

import java.awt.*;
import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合成图片中小图片的样式类
 *
 * @author Kris
 * @date 2018/08/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImgIconStyle {

    /**
     * 文件
     */
    private File file;

    /**
     * 横坐标
     */
    private int x;
    /**
     * 纵坐标
     */
    private int y;

    /**
     * 宽度
     */
    private int width;

    /**
     * 高度
     */
    private int height;

    private Color color;

}
