package org.example.demologin.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class TokenService {

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

	public String decodificarToken(String token) {
		try {
			return JWT.require(Algorithm.HMAC512(chave))
					.withIssuer("api-demo-login")
					.build()
					.verify(token)
					.getSubject();
		} catch (TokenExpiredException e) {
			throw new RuntimeException("Token expirado", e);
		} catch (JWTVerificationException e) {
			throw new RuntimeException("token inválido", e);
		}
	}
}
