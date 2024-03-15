package com.example.demo;


import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
    private AuthorizationManager<Message<?>> messageAuthorizationManager;
	
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/group","/private");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/private");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        		.setAllowedOriginPatterns("http://localhost:3000")
                .withSockJS();
    }

    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
    
//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        AuthorizationManager<Message<?>> myAuthorizationRules = messageAuthorizationManager;
//        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(myAuthorizationRules);
//        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(this.context);
//        authz.setAuthorizationEventPublisher(publisher);
//        registration.interceptors(new ChannelInterceptor() {
//          @Override
//          public Message<?> preSend(Message<?> message, MessageChannel channel) {
//              StompHeaderAccessor accessor =
//                      MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//              assert accessor != null;
//              if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//
//                  String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
//                  assert authorizationHeader != null;
//                  String token = authorizationHeader.substring(7);
//
//                  String username = jwtUtil.extractUsername(token);
//                  UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                  List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
//                  User user = userRepository.findById(Long.parseLong(username)).get();
//            		authorities.add(new SimpleGrantedAuthority("ROLE_USER_"+ user.getUserId()));
//            		List<UserGroup> groups = groupRepository.findAll();
//                
//	                 for(UserGroup group: groups) {
//	              	   System.out.println(group.getUsers().size());
//	              	   for(User us : group.getUsers()) {
//	              		   if(us.getUserId().compareTo(user.getUserId()) == 0) {
//	              			   	authorities.add(new SimpleGrantedAuthority("ROLE_GROUP_"+ group.getGroupId()));
//	              		   }
//	              	   }
//	                  }
//                  UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//                  SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//                  accessor.setUser(usernamePasswordAuthenticationToken);
//              }else if (!StompCommand.DISCONNECT.equals(accessor.getCommand())){
//            	  SecurityContextHolder.getContext().setAuthentication((Authentication) accessor.getUser());
//              }
//
//              return message;
//          }
//
//      }, authz);
//   }
}
