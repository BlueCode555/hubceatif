package bj.hubcreatif.hubcreatif_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = DateValidator.class)
public @interface DateDebutValidator {
    String message() default "La date de fin doit être ultérieure à la date de début";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
