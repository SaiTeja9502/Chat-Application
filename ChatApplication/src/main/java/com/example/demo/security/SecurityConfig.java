package com.example.demo.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Autowired
    UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.csrf((csrf)->csrf.disable())
                .authorizeHttpRequests((authz)-> authz
                .requestMatchers("/group/**").hasAnyRole("ADMIN","USER")
                .requestMatchers("/login").permitAll()
                .requestMatchers("/refreshToken").permitAll()
                .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                
       return  http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	    provider.setUserDetailsService(userDetailsService);
	    provider.setPasswordEncoder(passwordEncoder());
	    return provider;
	}

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public MessageMatcherDelegatingAuthorizationManager.Builder messageMatcherDelegatingAuthorizationManagerBuilder() {
        return MessageMatcherDelegatingAuthorizationManager.builder();
    }
    
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        System.out.println("Hello world");
        messages
            .nullDestMatcher().authenticated()
            .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.UNSUBSCRIBE, SimpMessageType.DISCONNECT, SimpMessageType.HEARTBEAT).permitAll()
            .simpSubscribeDestMatchers("/user/{userId}/private").access((message, matcher) -> {
                String userId = matcher.getVariables().get("userId");
                System.out.println("Inside user private");
                return hasrole("ROLE_USER_" + userId);
            })
            .simpSubscribeDestMatchers("/chatroom/public/{groupId}").access((message, matcher) -> {
                String groupId = matcher.getVariables().get("groupId");
                return hasrole("ROLE_GROUP_" + groupId);
            })
            .simpMessageDestMatchers("/app/message/{groupId}").access((message, matcher) -> {
                String groupId = matcher.getVariables().get("groupId");
                return hasrole("ROLE_GROUP_" + groupId);
            })
            .simpMessageDestMatchers("/app/private-message/{userId}").access((message, matcher) -> {
            	String userId = matcher.getVariables().get("userId");
                return hasrole("ROLE_USER_" + userId);
            })
           .anyMessage().denyAll();

        return messages.build();
    }
    
    

    private AuthorizationDecision hasrole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
        	System.out.println("no authentication");
        	return new AuthorizationDecision(false);
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        System.out.println("authentication");
        boolean auth = false;
        for(GrantedAuthority authority: authorities) {
        	if(authority.getAuthority().equalsIgnoreCase(role)) {
        		auth = true;
        		System.out.println("setter");
        		break;
        	}
        }
        return new AuthorizationDecision(auth);
    }
}
