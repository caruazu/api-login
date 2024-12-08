package org.example.demologin.service;

import org.example.demologin.model.Email;
import org.example.demologin.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Value("${api.security.token.expiracao_minutos}")
	private Integer expiracaoMinutos;

	@Value("${api.security.validate-email-url}")
	private String validateEmailURL;

	@Value("${api.security.reset-password-url}")
	private String resetPasswordURL;

	private final JavaMailSender mailSender;

	public EmailService(JavaMailSender mailSender) {
		this.mailSender = mailSender;
		logger.debug("EmailService iniciado com sucesso.");
	}

	public void sendEmail(Email email) {
		logger.debug("Preparando para enviar e-mail para: {}", email.remetente());

		var message = new SimpleMailMessage();
		message.setFrom("no-reply@mail.com");
		message.setTo(email.remetente());
		message.setSubject(email.assunto());
		message.setText(email.mensagem());
		mailSender.send(message);

		logger.info("E-mail enviado com sucesso para: {}", email.remetente());
	}

	public void enviarConfirmacao(User user, String token) {
		logger.debug("Iniciando envio de confirmação de e-mail para o usuário: {}", user.getEmail());

		String assunto = "Confirmação de email";
		String url = validateEmailURL + "?token=" + token;
		String mensagem = "Acesse esse link nos próximos "+expiracaoMinutos+" minutos para ativar sua conta: " + url;

		Email email = new Email(user.getEmail(),assunto,mensagem);
		sendEmail(email);

		logger.info("Processo de envio de confirmação concluído para o usuário: {}", user.getEmail());
	}

	public void enviarSenhaMudar(User user, String token) {
		logger.debug("Iniciando envio de e-mail para alteração de senha para o usuário: {}", user.getEmail());

		String assunto = "Alteração de senha";
		String url = resetPasswordURL + "?token=" + token;
		String mensagem = "Acesse esse link nos próximos "+expiracaoMinutos+" minutos para alterar sua senha: " + url;

		Email email = new Email(user.getEmail(),assunto,mensagem);
		sendEmail(email);

		logger.info("Processo de envio de alteração de senha concluído para o usuário: {}", user.getEmail());
	}
}
