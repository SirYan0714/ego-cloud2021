package com.xtan.constant;

import java.util.Arrays;
import java.util.List;

public class GatewayConstant {
    public static final String OAUTH_PREFIX = "oauth:jwt";
    public static final String AUTHORIZATION = "Authorization";
    public static final List<String> ALLOW_PATH = Arrays.asList("/oauth/token", "/auth-server/oauth/token");
}
