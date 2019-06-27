package com.linyoga.tool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 腾讯视频工具类
 *
 * @author Kris
 * @date 2018/11/27
 */
public class TxVideoUtil {

    /** 初始化线程池 */
    private static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(5);

    private static ExecutorService executorService = new ThreadPoolExecutor(10, 64
            , 0L, TimeUnit.SECONDS
            , new LinkedBlockingQueue<>()
            , new ThreadFactoryBuilder().setNameFormat("TxVideo-thread-%d").build());

    /** 存储视频链接地址的集合 */
    private static ConcurrentHashMap<String, FutureTask<String>> videoUrlMaps = new ConcurrentHashMap<>(16);

    /** 获取视频真实播放地址第一步的URL */
    private final static String HTTP_VIDEO_URL_FIRST = "http://vv.video.qq.com/getinfo?vids=%s&platform=101001&charge=0&otype=json";

    /** 获取视频真实播放地址第二步的URL */
    private final static String HTTP_VIDEO_URL_SECOND = "http://vv.video.qq.com/getkey?format=2&otype=json&vt=150&vid=%s&ran=02E9477521511726081&charge=0&filename=%s.mp4&platform=11";

    /**
     * 获取腾讯视频真实播放地址
     * https://blog.csdn.net/Szu_IT_Man/article/details/80449751
     *
     * @param vids
     * @return
     */
    private static String getVideoUrlByVids(final String vids) throws IOException {
        //获取真实视频的请求地址url
        String response = new OkHttpClient().newCall(new Request.Builder()
                .url(String.format(HTTP_VIDEO_URL_FIRST, vids))
                .build())
                .execute().body().string();
        ObjectMapper mapper = new ObjectMapper();
        response = response.substring(response.indexOf("=") + 1, response.length() - 1);
        StringBuffer url = new StringBuffer(mapper
                .readTree(response)
                .get("vl").get("vi").get(0).get("ul").get("ui").get(0).get("url")
                .textValue()
        );
        //获取真实视频的请求地址url中的vkey值
        String outJson2 = new OkHttpClient().newCall(new Request.Builder()
                .url(String.format(HTTP_VIDEO_URL_SECOND, vids, vids))
                .build())
                .execute().body().string();
        String key = mapper.readTree(outJson2.substring(outJson2.indexOf("=") + 1, outJson2.length() - 1)).get("key").textValue();
        //最后将前面两个获取的值进行拼接成真实播放地址
        return url.append(vids).append(".mp4?vkey=").append(key).toString();
    }

    /**
     * 根据vids从map缓存中获取视频播放链接地址
     *
     * @param vids 视频ID
     * @return
     * @throws IOException
     */
    public static String getUrlByVidsFromMaps(final String vids) throws IOException {
        Future<String> future;
        if ((future = videoUrlMaps.get(vids)) == null) {
            //定义Future可以异步获取结果
            FutureTask<String> task = new FutureTask<>(() -> getVideoUrlByVids(vids));
            //为了防止复合操作，if-null-add 普通map底层无法通过加锁确保原子性，所以用concurrentHashMap的putIfAbsent
            future = videoUrlMaps.putIfAbsent(vids, task);
            if (future == null) {
                future = task;
                //执行异步线程
                executorService.execute(task);
                //指定时间后执行一次移除操作
                exec.schedule(() -> videoUrlMaps.remove(vids), 2, HOURS);
            }
        }
        String url = null;
        try {
            //获取结果
            url = future.get();
        } catch (CancellationException e) {
            videoUrlMaps.remove(vids, future);
        } catch (ExecutionException e) {
            videoUrlMaps.remove(vids, future);
            System.out.println(e.getCause());
        } catch (InterruptedException e) {
            videoUrlMaps.remove(vids, future);
            System.out.println(e.getCause());
        }
        return url == null ? getVideoUrlByVids(vids) : url;
    }


    public static void main(String[] args) throws java.lang.InterruptedException, IOException {
        ExecutorService executor = new ThreadPoolExecutor(2, 10
                , 0L, TimeUnit.SECONDS
                , new LinkedBlockingQueue<>()
                , new ThreadFactoryBuilder().setNameFormat("TxVideo-test-thread-%d").build());
        for (int i = 0; i < 2; ++i) {
            executor.submit(() -> {
                try {
                    System.out.println(getUrlByVidsFromMaps("w0647n5294g"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        SECONDS.sleep(2);
        System.out.println(getUrlByVidsFromMaps("w0647n5294g"));
        SECONDS.sleep(20);
        System.out.println(getUrlByVidsFromMaps("w0647n5294g"));
//        System.out.println( getUrlByVidsFromMap( "m0742o3vons" ) );
    }
}
