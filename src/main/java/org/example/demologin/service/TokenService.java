package org.example.demologin.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.example.demologin.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TokenService {
	private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

	@Value("${api.security.token.secret}")
	private String chave;

	@Value("${api.security.token.expiracao_minutos}")
	private Integer expiracaoMinutos;

	@Value("${spring.jackson.time-zone}")
	private static String fusoHorario;

	@Value("${spring.jackson.time-zone}")
	public void setMyConfig(String fusoHorario) {
		TokenService.fusoHorario = fusoHorario;
	}

	private static Instant gerarDataExpiracao(Integer minutos) {
		return ZonedDateTime.now(ZoneId.of(fusoHorario)).plusMinutes(minutos).toInstant();
	}

	public String gerar(User user) {
		logger.debug("Gerando token para o usuário: {}", user.getUsername());

		try {
			String token = JWT.create()
					.withIssuer("api-demo-login")
					.withSubject(user.getUsername())
					.withExpiresAt(gerarDataExpiracao(expiracaoMinutos))
					.sign(Algorithm.HMAC512(chave));

			logger.info("token gerado para o usuário: {}", user.getUsername());
			return token;
		} catch (IllegalArgumentException e) {
			logger.error("Erro de argumento ilegal ao gerar o token JWT");
			throw new RuntimeException(e);
		} catch (JWTCreationException e) {
			logger.warn("Erro ao gerar o token JWT");
			throw new RuntimeException("erro ao gerar o token JWT", e);
		}
	}

	public String decodificar(String token) {
		logger.debug("Decodificando token");

		try {
			String username = JWT.require(Algorithm.HMAC512(chave))
					.withIssuer("api-demo-login")
					.build()
					.verify(token)
					.getSubject();

			logger.info("Token decodificado com sucesso");
			return username;
		} catch (TokenExpiredException e) {
			logger.warn("Token expirado");
			throw new RuntimeException("Token expirado", e);
		} catch (JWTVerificationException e) {
			logger.warn("Token inválido");
			throw new RuntimeException("token inválido", e);
		}
	}
}
