package org.example.demologin.config.exception;

import org.example.demologin.config.exception.error.ObjetoNotFoundException;
import org.example.demologin.config.exception.error.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllersExceptionHandler {

	public record FieldErrorDTO(String field,String message) {}

	private static List<FieldErrorDTO> getFieldErrorDTOList(MethodArgumentNotValidException ex) {
		return ex.getBindingResult()
				.getAllErrors()
				.stream()
				.map(
						error -> new FieldErrorDTO(
								((FieldError) error).getField(),
								error.getDefaultMessage()
						)
				)
				.collect(Collectors.toList());
	}

	@ExceptionHandler(ObjetoNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseTemplate handleNotaNotFoundException(ObjetoNotFoundException ex) {
		return new ResponseTemplate(ex.getMessage(),null);
	}

//	Erros de login

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ResponseTemplate TratadorErros401BadCredentials(BadCredentialsException ex){
		return new ResponseTemplate("Senha incorreta",ex.getLocalizedMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ResponseTemplate TratadorErros401Autenticação(AuthenticationException ex) {
		return new ResponseTemplate("Falha no processo de autenticação",ex.getLocalizedMessage());
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ResponseTemplate TratadorErrosUsernameNotFound(UsernameNotFoundException ex) {
		return new ResponseTemplate(ex.getMessage(),null);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	@ResponseBody
	public ResponseTemplate TratadorErrosUserAlreadyExists(UserAlreadyExistsException ex) {
		return new ResponseTemplate(ex.getMessage(),null);
	}

	@ExceptionHandler({
		DisabledException.class,
		LockedException.class,
		CredentialsExpiredException.class,
		AccountExpiredException.class
	})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ResponseTemplate TratadorErrosAccountStatusException(AccountStatusException ex) {
		return new ResponseTemplate("Usuário está desabilitado",null);
	}

//	Erro geral

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseTemplate TratadorErros500InternalSeverError(Exception e) {
		return new ResponseTemplate("Erro no servidor",null);
	}
}