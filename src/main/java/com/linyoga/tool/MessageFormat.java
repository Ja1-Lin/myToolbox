package com.linyoga.tool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串模板替换
 *
 * @author Kris
 * @date 2018/09/04
 */
public class MessageFormat {

    public static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * 简单实现${}模板功能
     * 如${aa} cc ${bb} 其中 ${aa}, ${bb} 为占位符. 可用相关变量进行替换
     *
     * @param templateStr            模板字符串
     * @param data                   替换的变量值
     * @param defaultNullReplaceVals 默认null值替换字符, 如果不提供, 则为字符串""
     * @return 返回替换后的字符串, 如果模板字符串为null, 则返回null
     */
    public static String templateReplace(String templateStr, Map<String, ?> data, String... defaultNullReplaceVals) {
        if (templateStr == null) {
            return null;
        }
        if (data == null) {
            data = Collections.EMPTY_MAP;
        }
        String nullReplaceVal = defaultNullReplaceVals.length > 0 ? defaultNullReplaceVals[0] : "";

        StringBuffer newValue = new StringBuffer(templateStr.length());

        Matcher matcher = PATTERN.matcher(templateStr);

        while (matcher.find()) {
            String key = matcher.group(1);
            String r = data.get(key) != null ? data.get(key).toString() : nullReplaceVal;
            matcher.appendReplacement(newValue, r.replaceAll("\\\\", "\\\\\\\\")); //这个是为了替换windows下的文件目录在java里用\\表示
        }

        matcher.appendTail(newValue);

        return newValue.toString();
    }

    /**
     * 当匹配到的value值为null时，默认用""替代
     */
    public static String templateReplace(String templateStr, Map<String, ?> data) {
        return templateReplace(templateStr, data, "");
    }

    public static void main(String[] args) {
        String templateStr = "你好，${订单号}你的订单号${订单号},$已支付成功，服务热线：${服务热线}";
        Map<String, String> map = new HashMap<>();
        map.put("订单号", "12345");
        map.put("已支付成功", "123456");
        map.put("服务热线", "0755-33333");
        System.out.println("replaceTemplateStr : " + templateReplace(templateStr, map, ""));
    }
}
