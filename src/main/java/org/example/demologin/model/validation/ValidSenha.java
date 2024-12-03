package org.example.demologin.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidatorSenha.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSenha {

	String message() default "A senha deve ter no mininimo 12 caracteres, e deve conter uma letra maiuscula, uma letra minuscula e um numero.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
