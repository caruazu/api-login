package org.example.demologin.service;

import org.example.demologin.model.Email;
import org.example.demologin.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Value("${api.security.token.expiracao_minutos}")
	private Integer expiracaoMinutos;

	@Value("${api.security.validate-email-url}")
	private String validateEmailURL;

	@Value("${api.security.reset-password-url}")
	private String resetPasswordURL;

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void sendEmail(Email email) {
		var message = new SimpleMailMessage();
		message.setFrom("no-reply@mail.com");
		message.setTo(email.remetente());
		message.setSubject(email.assunto());
		message.setText(email.mensagem());
		mailSender.send(message);
	}

	public void enviarConfirmacao(User user, String token) {
		String assunto = "Confirmação de email";
		String url = validateEmailURL + "?token=" + token;
		String mensagem = "Acesse esse link nos próximos "+expiracaoMinutos+" minutos para ativar sua conta: " + url;

		Email email = new Email(user.getEmail(),assunto,mensagem);
		sendEmail(email);
	}

	public void enviarSenhaMudar(User user, String token) {
		String assunto = "Alteração de senha";
		String url = resetPasswordURL + "?token=" + token;
		String mensagem = "Acesse esse link nos próximos "+expiracaoMinutos+" minutos para alterar sua senha: " + url;

		Email email = new Email(user.getEmail(),assunto,mensagem);
		sendEmail(email);
	}
}
