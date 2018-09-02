package com.linyoga.tool;

/**
 * 生成订单号
 * 要求：
 *  保证订单号唯一性
 *  特殊含义
 *  性能较好
 *
 * 组成部分：
 *  UNIX时间戳12位 + 用户user id后4位
 * @author Stephen
 * @date 2018/8/15 17:55
 */
public class OrderSnBuilder {

    private static final int MAX_VALUE = 10000;

    /**
     * 生成订单号
     * UNIX时间戳12位 + 用户user id后4位
     * @param userId
     * @return
     */
    public static String generate( Long userId ){
        if( null == userId ){
            return null;
        }
        //混淆userId
        //TODO 此方法不是最优解，减少了50%数，增加了碰撞机率
        userId = ( userId << 1 ) + 1;
        String result = String.format( "%04d", userId );
        if( userId >= MAX_VALUE ) {
            //若大于4位数则取后四位
            result  = result.substring( result.length() - 4 , result.length() );
        }
        //UNIX时间戳与userid后四位合成
        return new StringBuffer( String.valueOf( System.currentTimeMillis() ).substring( 1,13 ) )
                    .append( result ).toString();
    }

}
