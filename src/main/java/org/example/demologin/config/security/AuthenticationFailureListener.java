package org.example.demologin.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.example.demologin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private UserService userService;

	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
		final String enderecoIp = resgatarIP();
		userService.loginFailed(enderecoIp);
	}

	private String resgatarIP() {
		String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
		if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(httpServletRequest.getRemoteAddr())) {
			return httpServletRequest.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}
