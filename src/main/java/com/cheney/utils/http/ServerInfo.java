package com.cheney.utils.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 内部服务器信息
 */
@Component
public class ServerInfo {

    public static String SERVER_CODE_HOST;

    @Value("${server.port}")
    public void setPort(String info) {
        ServerInfo.SERVER_CODE_HOST = info;
    }

}
