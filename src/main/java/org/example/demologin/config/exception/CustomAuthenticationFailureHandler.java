package org.example.demologin.config.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demologin.service.EmailService;
import org.example.demologin.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

	private final MessageSource messageSource;
	private final HttpServletRequest request;
	private final UserService userService;

	public CustomAuthenticationFailureHandler(MessageSource messageSource, HttpServletRequest request, UserService userService) {
		this.messageSource = messageSource;
		this.request = request;
		this.userService = userService;

		logger.debug("CustomAuthenticationFailureHandler inicializado.");
	}

	@Override
	public void onAuthenticationFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException exception
	) throws IOException, ServletException {
		String ipAddress = getClientIP();
		userService.loginFailed(ipAddress);

		if (userService.isBlocked(ipAddress)) {
			logger.warn("IP bloqueado devido a múltiplas tentativas de login: {}", ipAddress);
			String blockedMessage = messageSource.getMessage("Usuário bloqueado por número máximo de tentativas de senha", null, request.getLocale());
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

	private String getClientIP() {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0];
		}
		return request.getRemoteAddr();
	}
}