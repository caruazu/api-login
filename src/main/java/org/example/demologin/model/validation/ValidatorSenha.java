package org.example.demologin.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

public class ValidatorSenha implements ConstraintValidator<ValidSenha, String>{

	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

		PasswordValidator passwordValidator = new PasswordValidator(
			Arrays.asList(
				// no mínimo 12 caracteres de comprimento e no máximo 32
				new LengthRule(12, 32),
				// pelo menos uma letra minúscula.
				new CharacterRule(EnglishCharacterData.UpperCase, 1),
				// pelo menos uma letra maiúscula.
				new CharacterRule(EnglishCharacterData.LowerCase, 1),
				// pelo menos um número
				new CharacterRule(EnglishCharacterData.Digit, 1),
				new WhitespaceRule()
			)
		);

		RuleResult result = passwordValidator.validate(new PasswordData(password));

		return result.isValid();
	}
}