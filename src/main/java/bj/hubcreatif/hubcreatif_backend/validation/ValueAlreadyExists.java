package bj.hubcreatif.hubcreatif_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = ValueAlreadyExistsValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueAlreadyExists {
    String message() default "Cette valeur existe déjà";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Classe de l'entité JPA à interroger
     */
    Class<?> entity();

    /**
     * Nom du champ à vérifier dans l'entité
     */
    String field() default "code";
}
