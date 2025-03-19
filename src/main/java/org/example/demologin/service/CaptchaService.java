package org.example.demologin.service;

import org.example.demologin.model.CaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class CaptchaService {

	@Value("${api.security.captcha.token.secret}")
	private String secretKey;

	@Value("${api.security.captcha.host}")
	private String verifyUrl;

	@Value("${api.security.captcha.disable}")
	private Boolean captchaDisabled;

	public void validateCaptcha(String recaptchaResponse) {
		if (!captchaDisabled) {
			MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
			requestBody.add("secret", secretKey);
			requestBody.add("response", recaptchaResponse);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<CaptchaResponse> responseEntity = restTemplate.postForEntity(verifyUrl, requestEntity, CaptchaResponse.class);

			CaptchaResponse response = responseEntity.getBody();
			if (response == null || !response.success()) {
				throw new BadCredentialsException("CAPTCHA inválido");
			}
		}
	}
}
