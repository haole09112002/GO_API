package com.GOBookingAPI.config;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.GOBookingAPI.security.Token.FirebaseProvider;


@Configuration
//@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements 
//WebSocketMessageBrokerConfigurer 
WebSocketConfigurer
{

	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(webSocketHandler(), "/handler");
	}

    @Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }
//	private FirebaseProvider provider;
//	
//	private ApplicationContext context;
//	
//	private static final AntPathMatcher MATCHER = new AntPathMatcher();
//	
//	public WebSocketConfig(FirebaseProvider provider, ApplicationContext context) {
//		this.provider = provider;
//		this.context = context;
//	}
//
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/ws").withSockJS();
//	}
//
//	@Override
//	public void configureMessageBroker(MessageBrokerRegistry registry) {
//		registry.setApplicationDestinationPrefixes("/app");
//		registry.enableSimpleBroker("/topic");
//	}

	

//	private AuthorizationManager<Message<?>> makeMessageAuthorizationManager(){
//		 MessageMatcherDelegatingAuthorizationManager.Builder message = new MessageMatcherDelegatingAuthorizationManager.Builder();
//		 
//		 message
//		 	.simpDestMatchers("/topic/user/**")
//		 	.authenticated()
//		 	.simpTypeMatchers(SimpMessageType.MESSAGE)
//		 	.denyAll().anyMessage().permitAll();
//		 return message.build();
//	}
//
//	@Override
//	public void configureClientInboundChannel(ChannelRegistration registration) {
//
//		AuthorizationManager<Message<?>> authorizationManager = makeMessageAuthorizationManager();
//		AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
//		
//		AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
//		authInterceptor.setAuthorizationEventPublisher(publisher);
//		registration.interceptors(provider ,authInterceptor ,new RejectClientMessagesOnchannelsChannelInterceptor());
//	}
//	
//	private String[] paths = new String[] {
//			"/topic/user/*"
//	};
//	
//	private class RejectClientMessagesOnchannelsChannelInterceptor implements ChannelInterceptor{
//
//		@Override
//		public Message<?> preSend(Message<?> message, MessageChannel channel) {
//			if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
//				String destination = (String) message.getHeaders().get("simpDestination");
//				for(String path : paths) {
//					if(MATCHER.match(path, destination)) {
//						message = null;
//					}
//				}
//			}
//			return message;
//		}
//	}
//
//	private class DestinationLevelAuthorizationChannelInterceptor implements ChannelInterceptor{
//
//		@Override
//		public Message<?> preSend(Message<?> message, MessageChannel channel) {
//			if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)) {
//				String destination = (String) message.getHeaders().get("simpDestination");
//				Map<String, String> params = MATCHER.extractUriTemplateVariables("/topic/user/{userid}/**", destination);
//				
//				
//			}
//			
//		}
//		
//	}


}
