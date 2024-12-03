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
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllersExceptionHandler {
	private Properties errorMensagens;

	public ControllersExceptionHandler() throws IOException {
		errorMensagens = new Properties();
		InputStream input = getClass().getClassLoader().getResourceAsStream("ErrorMessages.properties");
		errorMensagens.load(input);
	}

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

//	Validacao de dados

	@ExceptionHandler(HandlerMethodValidationException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ResponseBody
	public ResponseTemplate TratadorErrors400(HandlerMethodValidationException ex){
		String errorMessage = errorMensagens.getProperty("error.HandlerMethodValidationException");
		return new ResponseTemplate(errorMessage,ex.getDetailMessageArguments());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ResponseTemplate TratadorErrors400(MethodArgumentNotValidException ex){
		String errorMessage = errorMensagens.getProperty("error.MethodArgumentNotValidException");
		return new ResponseTemplate(errorMessage,getFieldErrorDTOList(ex));
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
		String errorMessage = errorMensagens.getProperty("error.BadCredentialsException");
		return new ResponseTemplate(errorMessage,ex.getLocalizedMessage());
	}

	@ExceptionHandler(AuthenticationException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ResponseTemplate TratadorErros401Autenticacao(AuthenticationException ex) {
		String errorMessage = errorMensagens.getProperty("error.AuthenticationException");
		return new ResponseTemplate(errorMessage,ex.getLocalizedMessage());
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
	public ResponseTemplate TratadorErrosUserAlreadyExists() {
		String errorMessage = errorMensagens.getProperty("error.UserAlreadyExistsException");
		return new ResponseTemplate(errorMessage,null);
	}

	@ExceptionHandler({
		DisabledException.class,
		LockedException.class,
		CredentialsExpiredException.class,
		AccountExpiredException.class
	})
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ResponseTemplate TratadorErrosAccountStatusException() {
		String errorMessage = errorMensagens.getProperty("error.AccountExpiredException");
		return new ResponseTemplate(errorMessage,null);
	}

//	Erro geral

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseTemplate TratadorErros500InternalSeverError(Exception e) {
		String errorMessage = errorMensagens.getProperty("error.Exception");
		return new ResponseTemplate(errorMessage,null);
	}
}