package com.yami.trading.huobi.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 给spring容器注入这个ServerEndpointExporter对象
 *
 * 检测所有带有@serverEndpoint注解的bean并注册他们。
 */
@EnableWebSocket
@Configuration
public class WebSocketConfig {

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}
}
