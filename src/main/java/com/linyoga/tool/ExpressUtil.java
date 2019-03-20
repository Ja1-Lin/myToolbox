package com.linyoga.tool;

import com.google.common.base.Strings;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import lombok.Data;

/**
 * 物流信息查询工具类
 *
 * @author Kris
 * @date 2018/09/25
 */
public class ExpressUtil {

    private static final String key = "b6761b54f86f202f";

    /**
     * header中用户代理信息，模拟浏览器
     */
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";

    /**
     * 返回格式为JSON的物流信息的接口地址
     */
    private static final String QUERY_URL_GET_JSON = "https://www.kuaidi100.com/query";
    private static final HttpRequest QUERY_URL_GET_JSON_REQUEST = HttpRequest.get(QUERY_URL_GET_JSON).header(Header.USER_AGENT,USER_AGENT);

    /**
     * 返回格式为HTML物流信息的接口地址
     */
    private static final String QUERY_URL_GET_HTML = "http://www.kuaidi100.com/applyurl";
    private static final HttpRequest QUERY_URL_GET_HTML_REQUEST = HttpRequest.get(QUERY_URL_GET_HTML).header(Header.USER_AGENT,USER_AGENT);

    /**
     * 获取物流公司代码的接口地址
     */
    private static final String GET_COM_CODE_URL = "http://kuaidi100.wlphp.com/api.php";
    private static final HttpRequest GET_COM_CODE_URL_REQUEST = HttpRequest.get(GET_COM_CODE_URL).header(Header.USER_AGENT,USER_AGENT);

    /**
     * 根据运单号获取物流公司代码的接口地址
     */
    private static final String GET_COM_CODE_BY_NU_URL = "http://www.kuaidi100.com/autonumber/autoComNum";
    private static final HttpRequest GET_COM_CODE_BY_NU_URL_REQUEST = HttpRequest.get(GET_COM_CODE_BY_NU_URL).header(Header.USER_AGENT,USER_AGENT);

    /**
     * 根据物流公司代码和运单号获取html格式物流详细信息的路径
     *
     * @param com 快递公司代码
     * @param nu  快递单号
     * @return html格式的地址
     */
    public static String getHtmlUrlApi(String com, String nu) {
        return QUERY_URL_GET_HTML_REQUEST.form("key",key)
                .form("com",com)
                .form("nu",nu)
                .execute().body();
    }

    /**
     * 根据运单号获取html格式物流详细信息的路径
     *
     * @param nu 运单号
     */
    public static String getHtmlUrlApiByNu(String nu) {
        AutoComNum autoComNum = getComCodeByNu(nu);
        String html = null;
        if (Objects.nonNull(autoComNum)) {
            for (AutoComCode autoComCode : autoComNum.getAuto()) {
                html = getHtmlUrlApi(autoComCode.getComCode(), nu);
                if (!Strings.isNullOrEmpty(html)) {
                    break;
                }
            }
        }
        return html;
    }

    /**
     * 获取运单的详细物流信息
     *
     * @param com 物流公司代码拼音
     * @param nu  运单号
     * @return 物流信息VO
     */
    public static ExpressVO getJsonQuery(String com, String nu) {
        return new Gson().fromJson(
                QUERY_URL_GET_JSON_REQUEST
                        .form("type",com)
                        .form("postid",nu)
                        .execute().body()
                , ExpressVO.class);
    }

    /**
     * 根据运单号获取json格式的物流详细信息
     * @param nu 运单号
     * @return 有可能为null。注意判断
     */
    public static ExpressVO getJsonByNu(String nu) {
        AutoComNum autoComNum = getComCodeByNu(nu);
        ExpressVO expressVO = null;
        if (Objects.nonNull(autoComNum)) {
            for (AutoComCode autoComCode : autoComNum.getAuto()) {
                if ( (expressVO = getJsonQuery(autoComCode.getComCode(), nu)) != null) {
                    break;
                }
            }
        }
        return expressVO;
    }

    /**
     * 获取物流公司代码
     *
     * @param comName 公司名字
     */
    public static ComCode getComCodeByName(String comName) {
        return new Gson().fromJson(GET_COM_CODE_URL_REQUEST.form("type",comName).execute().body()
                , ComCode.class);
    }

    /**
     * 根据运单号获取快递公司code
     *
     * @param nu 运单号
     */
    public static AutoComNum getComCodeByNu(String nu) {
        return new Gson().fromJson(GET_COM_CODE_BY_NU_URL_REQUEST.form("text",nu).execute().body()
                , AutoComNum.class);
    }

    public static void main(String[] agrs) {
        String nu = "3102336465343";
        System.out.println("htmlApiByNu : " + getHtmlUrlApiByNu(nu));
        System.out.println("comcode : " + getComCodeByName("百世汇通").toString());
        System.out.println("getJsonByNu : " + getJsonByNu(nu).toString());
    }

    /**
     * 快递物流信息
     */
    @Data
    public static class ExpressVO {

        /** 状态信息描述 */
        private String message;

        /** 状态码 */
        private String status;

        /** 快递公司拼音 */
        private String com;

        /** 状态信息描述 */
        private String nu;

        /** 1表示成功，0表示错误 */
        private String ischeck;

        /** 具体到达的派送点,逆序 */
        private List<ExpressDetailVO> data;

    }

    /**
     * 快递物流详细信息
     */
    @Data
    public static class ExpressDetailVO {

        /** 到达时间 */
        private Date time;

        /** 到达时间 */
        private Date ftime;

        /** 到达地点具体信息 */
        private String context;

        /** 到达地点 */
        private String location;
    }

    /**
     * 物流公司代码
     */
    @Data
    public static class ComCode {

        /** 状态码 */
        private String sta;
        private String msg;

        /** 状态码 */
        private String code;
    }

    /**
     * 根据运单号获取快递公司code返回的json对象
     */
    @Data
    public static class AutoComNum {
        private String comCode;
        private String num;

        /** 存储物流公司代码的列表 */
        private List<AutoComCode> auto;
    }

    /**
     * 物流公司代码
     */
    @Data
    public static class AutoComCode {

        /** 物流公司代码 */
        private String comCode;

        private String id;
        private Long noCount;
        private String noPre;
    }
}
