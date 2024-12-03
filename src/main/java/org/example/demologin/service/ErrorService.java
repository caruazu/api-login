package org.example.demologin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ErrorService {

	@Autowired
	private MessageSource messageSource;

	public String getErrorMessage(String code) {
		return messageSource.getMessage(code, null, Locale.getDefault());
	}
}
