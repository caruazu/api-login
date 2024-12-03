package org.example.demologin.config.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.demologin.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final MessageSource messageSource;
	private final HttpServletRequest request;
	private final UserService userService;

	public CustomAuthenticationFailureHandler(MessageSource messageSource, HttpServletRequest request, UserService userService) {
		this.messageSource = messageSource;
		this.request = request;
		this.userService = userService;
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
			String blockedMessage = messageSource.getMessage("Usuário bloqueado por número máximo de tentativas de senha", null, request.getLocale());
			addErrorMessage(request, response, blockedMessage);
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

	private void addErrorMessage(HttpServletRequest request, HttpServletResponse response, String blockedMessage) {
	}

	private String getClientIP() {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0];
		}
		return request.getRemoteAddr();
	}
}