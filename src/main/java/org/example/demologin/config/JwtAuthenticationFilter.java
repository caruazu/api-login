package org.example.demologin.config;

import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demologin.service.TokenService;
import org.example.demologin.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenService tokenService;
	private final UserService userService;

	public JwtAuthenticationFilter(TokenService tokenService, UserService userService) {
		this.tokenService = tokenService;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(
			@Nullable HttpServletRequest request,
			@Nullable HttpServletResponse response,
			@Nullable FilterChain filterChain
	) throws ServletException, IOException {
		String token = null;
		if (request != null) {
			token = this.recoverToken(request);
		}
		if (token != null) {
			String username = tokenService.decodificar(token);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails user = userService.loadUserByUsername(username);
				var authentication = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		if (filterChain != null) {
			filterChain.doFilter(request, response);
		}
	}

	private String recoverToken(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.replace("Bearer ", "");
		} else {
			return null;
		}
	}
}
