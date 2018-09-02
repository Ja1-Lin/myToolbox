package com.linyoga.tool.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * 图片处理工具类
 * 功能描述：
 *  1.将两张图片合成一张
 *
 * 常用应用场景：
 *  1.分享二维码图片
 *
 * 测试用例
 * ImageToolTest
 * @author Kris
 * @date 2018/08/14
 */
public class ImageTool {

    /**
     * 合成图片
     * @param image 主图
     * @param imgIconStyleList 子图片
     * @param fontStyleList 字体
     * @param path 保存合成图路径
     */
    public static void composeImage(File image, java.util.List<ImgIconStyle> imgIconStyleList
            , java.util.List<FontIconStyle> fontStyleList
            , String path){
        try{
            InputStream inputStream = new FileInputStream(image);
            //解码当前JPEG数据流，返回BufferedImage对象
            BufferedImage buffImg = ImageIO.read(inputStream);
            /**
             *  将小图合成
             */
            if(null != imgIconStyleList){
                imgIconStyleList.forEach(imgIconStyle -> {
                    //得到画笔对象
                    Graphics g = buffImg.getGraphics();
                    //获取需合成的子图片
                    ImageIcon imgIcon = new ImageIcon(imgIconStyle.getFile().getPath());
                    //将小图片绘到大图片上。
                    //x,y .表示你的小图片在大图片上的位置。
                    g.drawImage(imgIcon.getImage()
                            ,imgIconStyle.getX(),imgIconStyle.getY()
                            ,imgIconStyle.getWidth(),imgIconStyle.getHeight()
                            ,null);
                    //设置颜色。
                    g.setColor(imgIconStyle.getColor());
                    //处理并结束画笔
                    g.dispose();
                });
            }
            /**
             * 字体合成
             */
            if(null != fontStyleList){
                fontStyleList.forEach(fontStyle -> {
                    //得到画笔对象
                    Graphics g0 = buffImg.getGraphics();
                    //最后一个参数用来设置字体的大小，这是用来在海报上面写上字的方法
                    g0.setColor(fontStyle.getColor());
                    //注意：linux下可能会出现中文乱码，需要将对应的字体放到｛JAVA_HOMT｝/jre/lib/fonts文件夹下
                    g0.setFont(fontStyle.getFont());
                    //绘画的内容以及位置
                    g0.drawString(fontStyle.getDrawStr(),fontStyle.getX(),fontStyle.getY());
                    //处理并结束画笔
                    g0.dispose();
                });
            }
            //写入指定文件
            OutputStream os;
            os = new FileOutputStream(new File(path));

            //创键编码器，用于编码内存中的图象数据。
            JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
            en.encode(buffImg);

            inputStream.close();
            os.close();
        }catch (FileNotFoundException e){
            throw new RuntimeException("文件未找到");
        }catch (IOException e) {
            throw new RuntimeException("IO异常");
        }
    }
}
