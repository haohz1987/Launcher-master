package com.handpay.config;

public class UPConfig
{
    // 银联服务地址
    public static String		UPHost;
    public static String	UPTestHost;	// 测试
    public static String	UPProHost;	// 生产

    public static final String	SECURITYCHIPTP			= "51";
    public static final String	SPID					= "0001";
    public static final int		CARD_LIMIT				= 10;
    public static final int		CARD_DELETE_LIMIT		= 5;
    public static String	PREV_QUERY_BANKCARD;
    public static String	PREV_DELETE_BANKCARD;
    public static final int		SUPER_TRANS_AMOUNT_LEN	= 12;
    public static final int		TRANS_AMOUNT_LEN		= 10;
}
