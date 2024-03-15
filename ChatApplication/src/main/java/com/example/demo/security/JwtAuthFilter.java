package com.example.demo.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.utils.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;
    
//    @Autowired
//    UserConversationService userConversationService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }
        
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if(jwtUtil.validateToken(token, userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                String url = request.getRequestURI();
                if (url.matches("/account/forgot/answer") || url.matches("/account/forgot/reset")) {
                    String tokenType = jwtUtil.extractClaim(token, claims -> claims.get("type", String.class));
                    if ((tokenType != null && tokenType.equals("answer") && url.matches("/account/forgot/answer"))
                    		|| tokenType != null && tokenType.equals("reset") && url.matches("/account/forgot/reset")) {
                       //doing nothing
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                }
                
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }

        filterChain.doFilter(request, response);
    }
}

//if(url.matches("/group/\\d+")) {
//    // Extract group name and conversation id from URL
//    String[] parts = url.split("/");
//    String conversationId = parts[2];
//    
//    // Query userconversation table to fetch the role of the current user for the conversation
//    //String role = userConversationService.getUserRole(username, conversationId);
//    
//    // Add role to authorities
//    List<GrantedAuthority> authorities = new ArrayList<>(userDetails.getAuthorities());
//    //authorities.add(new SimpleGrantedAuthority(role));
//    
//    // Update authentication token with new authorities
//    authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
//}