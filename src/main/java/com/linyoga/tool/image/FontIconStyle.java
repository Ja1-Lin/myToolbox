package com.linyoga.tool.image;

import java.awt.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 合成图片中字体样式
 *
 * @author Kris
 * @date 2018/08/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FontIconStyle {

    /**
     * 文字内容
     */
    private String drawStr;
    /**
     * 横坐标
     */
    private int x;
    /**
     * 纵坐标
     */
    private int y;
    /**
     * 文字样式
     */
    private Font font;

    /**
     * 文字颜色
     */
    private Color color;

}
