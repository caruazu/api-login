package org.example.demologin.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.example.demologin.model.User;
import org.example.demologin.model.UserRole;
import org.example.demologin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserDetailsService {

	private final TokenService tokenService;
	private final EmailService emailService;

	@Value("${api.security.login.numero-maximo-tentativas}")
	private int MAX_ATTEMPTS;

	private final UserRepository userRepository;
	private final HttpServletRequest httpServletRequest;
	private final LoadingCache<String, Integer> attemptsCache;

	public UserService(UserRepository userRepository, HttpServletRequest httpServletRequest, TokenService tokenService, EmailService emailService){

		this.userRepository = userRepository;
		this.httpServletRequest = httpServletRequest;

		attemptsCache = CacheBuilder.newBuilder()
				.maximumSize(1000)
				.expireAfterWrite(Duration.ofMinutes(15))
				.build(new CacheLoader<>() {
					public Integer load(String key) {
						return 0;
					}
				});
		this.tokenService = tokenService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String ipAddress = getClientIP();
		if (isBlocked(ipAddress)) {
			throw new RuntimeException("Usuário bloqueado por muitas tentativas de acesso");
		}

		UserDetails user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Usuário não encontrado");
		}

		return user;
	}

	public UserDetails cadastrar(String username, String email, String password, UserRole role) {

		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setRole(role);
		user.setEnabled(false);

		User userDB = userRepository.save(user);

		String token = tokenService.gerar(userDB);
		emailService.enviarConfirmacao(userDB, token);

		return userDB;
	}

	private String getClientIP() {
		final String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0];
		}
		return httpServletRequest.getRemoteAddr();
	}

	public void loginFailed(String key) {
		int attempts = attemptsCache.getUnchecked(key);
		attempts++;
		attemptsCache.put(key, attempts);
	}

	public boolean isBlocked(String key) {
		try {
			return attemptsCache.get(key) >= MAX_ATTEMPTS;
		} catch (ExecutionException e) {
			return false;
		}
	}

	public void ativar(String username) {
		User userDB = (User) userRepository.findByUsername(username);
		userDB.setEnabled(true);
		userRepository.save(userDB);
	}

	public void senhaPedirNova(String username) {
		User userDB = (User) userRepository.findByUsername(username);
		if (userDB.isEnabled()) {
			String token = tokenService.gerar(userDB);
			emailService.enviarSenhaMudar(userDB, token);
		}
	}

	public void senhaMudar(String username, String password) {
		User userDB = (User) userRepository.findByUsername(username);
		userDB.setPassword(password);
		userRepository.save(userDB);
	}
}