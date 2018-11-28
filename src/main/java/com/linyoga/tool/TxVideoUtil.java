package com.linyoga.tool;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.http.HttpUtil;

/**
 * 腾讯视频工具类
 *
 * @author Kris
 * @date 2018/11/27
 */
public class TxVideoUtil {

    /** 初始化线程池 */
    private static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

    /** 存储视频链接地址的集合 */
    private static Map<String,String> videoUrlMap = new HashMap<>(16);

    /** 获取视频真实播放地址第一步的URL */
    private static String HTTP_VIDEO_URL_FIRST = "http://vv.video.qq.com/getinfo?vids=%s&platform=101001&charge=0&otype=json";

    /** 获取视频真实播放地址第二步的URL */
    private static String HTTP_VIDEO_URL_SECOND = "http://vv.video.qq.com/getkey?format=2&otype=json&vt=150&vid=%s&ran=02E9477521511726081&charge=0&filename=%s.mp4&platform=11";


    /**
     * 获取腾讯视频真实播放地址
     * https://blog.csdn.net/Szu_IT_Man/article/details/80449751
     * @param vids
     * @return
     */
    private static String getVideoUrlByVids(String vids) throws IOException {
        String response = HttpUtil.get(String.format(HTTP_VIDEO_URL_FIRST , vids));
        ObjectMapper mapper = new ObjectMapper();
        response = response.substring( response.indexOf("=") + 1 , response.length() - 1 );
        StringBuffer url = new StringBuffer( mapper
                .readTree( response )
                .get("vl").get("vi").get(0).get("ul").get("ui").get(0).get("url")
                .textValue()
        );
        String outJson2 = HttpUtil.get(String.format( HTTP_VIDEO_URL_SECOND , vids,vids ));
        String key = mapper.readTree( outJson2.substring( outJson2.indexOf("=") + 1 , outJson2.length() - 1 ) ).get("key").textValue();
        return url.append(vids).append(".mp4?vkey=").append(key).toString();
    }

    /**
     * 根据vids从map缓存中获取视频播放链接地址
     * @param vids 视频ID
     * @return
     * @throws IOException
     */
    public static String getUrlByVidsFromMap(String vids)throws IOException{
        String url ;
        if( (url = videoUrlMap.get(vids)) == null ){
            url = getVideoUrlByVids( vids );
            videoUrlMap.put( vids , url );
            exec.schedule(() -> videoUrlMap.remove(vids),2, TimeUnit.HOURS);
        }
        return url;
    }

    public static void main(String[] args) throws IOException{
        System.out.println(getUrlByVidsFromMap("w0647n5294g"));
        System.out.println(getUrlByVidsFromMap("w0647n5294g"));
        System.out.println(getUrlByVidsFromMap("m0742o3vons"));
    }
}
