package com.xtan.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtan.constant.GatewayConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckJwtFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        //得到请求的路径如果是登录之类的就放行，如果不是就检查是否有token
        if (GatewayConstant.ALLOW_PATH.contains(path)) {
            //放行
            return chain.filter(exchange);
        }
        //从请求头里面拿到jwt
        HttpHeaders headers = exchange.getRequest().getHeaders();
        List<String> list = headers.get(GatewayConstant.AUTHORIZATION);
        if (!ObjectUtils.isEmpty(list)) {
            //如果list 不是空
            String auth = list.get(0);
            String authorization = auth.replaceAll("bearer ", "");
            if (!StringUtils.isEmpty(authorization) && redisTemplate.hasKey(GatewayConstant.OAUTH_PREFIX
                    + authorization)) {
                //如果有jwt 就看和redis 里面的是不是一样的
                return chain.filter(exchange);
            }
        }
        //这里就是没有jwt 了，返回401
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        Map<String, Object> map = new HashMap<>();
        map.put("code", HttpStatus.UNAUTHORIZED.value());
        map.put("msg", "非法访问");
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = null;
        DataBuffer buffer = null;
        try {
            bytes = objectMapper.writeValueAsBytes(map);
            buffer = response.bufferFactory().wrap(bytes);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 执行顺序越小越先要比-1 小
     *
     * @return
     */
    @Override
    public int getOrder() {
        return -2;
    }
}
