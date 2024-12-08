package org.example.demologin.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.example.demologin.config.exception.error.UserAlreadyExistsException;
import org.example.demologin.model.User;
import org.example.demologin.model.UserRole;
import org.example.demologin.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Value("${api.security.login.numero-maximo-tentativas}")
	private int MAX_ATTEMPTS;

	private final TokenService tokenService;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final HttpServletRequest httpServletRequest;
	private final LoadingCache<String, Integer> attemptsCache;

	public UserService(UserRepository userRepository, HttpServletRequest httpServletRequest, TokenService tokenService, EmailService emailService){
		this.userRepository = userRepository;
		this.httpServletRequest = httpServletRequest;
		this.tokenService = tokenService;
		this.emailService = emailService;

		attemptsCache = CacheBuilder.newBuilder()
			.maximumSize(1000)
			.expireAfterWrite(Duration.ofMinutes(15))
			.build(new CacheLoader<>() {
				public Integer load(String key) {
					return 0;
				}
			});

		logger.debug("UserService inicializado.");
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.debug("Carregando usuário pelo username: {}", username);
		String ipAddress = getClientIP();
		logger.debug("Endereço IP do cliente: {}", ipAddress);

		if (isBlocked(ipAddress)) {
			logger.warn("Usuário bloqueado devido ao excesso de tentativas falhas. IP: {}", ipAddress);
			throw new RuntimeException("Usuário bloqueado por muitas tentativas de acesso");
		}

		UserDetails user = userRepository.findByUsername(username);
		if (user == null) {
			logger.warn("Usuário não encontrado: {}", username);
			throw new UsernameNotFoundException("Usuário nao existe.");
		}

		logger.info("Usuário encontrado: {}", username);
		return user;
	}

	public User loadUserByIdAndRole(Long id) {
		logger.debug("Carregando usuário por ID: {} com papel: {}", id, UserRole.USER);

		User userDB = (User) userRepository.findByIdAndRole(id,UserRole.USER);
		if (userDB == null){
			logger.warn("Usuário não encontrado para o ID: {} com papel: {}", id, UserRole.USER);
			throw new UsernameNotFoundException("Usuário nao existe.");
		}

		logger.info("Usuário encontrado: {} - Role: {}", userDB.getUsername(), userDB.getRole());
		return userDB;
	}

	public void cadastrar(String username, String email, String password, UserRole role) {
		logger.debug("Iniciando cadastro de novo usuário. Username: {}, Email: {}", username, email);

		if (
			userRepository.findByUsername(username) != null  ||
			userRepository.findByEmail(email) != null
		) {
			logger.error("Tentativa de cadastro de usuário já existente. Username: {}, Email: {}", username, email);
			throw new UserAlreadyExistsException("");
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setRole(role);
		user.setEnabled(false);

		User userDB = userRepository.save(user);

		String token = tokenService.gerar(userDB);
		emailService.enviarConfirmacao(userDB, token);

		logger.info("Cadastro realizado com sucesso para o usuário: {}", userDB.getUsername());
	}

	private String getClientIP() {
		final String xfHeader = httpServletRequest.getHeader("X-Forwarded-For");
		if (xfHeader != null) {
			return xfHeader.split(",")[0];
		}
		return httpServletRequest.getRemoteAddr();
	}

	public void loginFailed(String key) {
		logger.debug("Falha de login detectada");
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
		logger.debug("Ativando usuário: {}", username);

		User userDB = (User) loadUserByUsername(username);
		userDB.setEnabled(true);
		userRepository.save(userDB);

		logger.info("Usuário ativado: {}", username);
	}

	public void senhaPedirNova(String username) {
		logger.debug("Solicitação de redefinição de senha para o usuário: {}", username);

		User userDB = (User) loadUserByUsername(username);
		if (userDB.isEnabled()) {
			String token = tokenService.gerar(userDB);
			emailService.enviarSenhaMudar(userDB, token);
		} else {
			logger.warn("Usuário não está habilitado: {}", username);
			throw new DisabledException("Usuário desabilitado");
		}

		logger.info("Realizada a solicitação de redefinição de senha para o usuário: {}", username);
	}

	public void senhaMudar(String username, String password) {
		logger.debug("Alterando senha do usuário: {}", username);

		User userDB = (User) loadUserByUsername(username);
		userDB.setPassword(password);
		userRepository.save(userDB);

		logger.info("Senha alterada com sucesso para o usuário: {}", username);
	}

	public void desativar(Long id) {
		logger.debug("Desativando usuário com ID: {}", id);

		User userDB = loadUserByIdAndRole(id);
		userDB.setEnabled(false);
		userRepository.save(userDB);

		logger.info("Usuário desativado com sucesso. ID: {}, Username: {}", id, userDB.getUsername());
	}
}
