package bj.hubcreatif.hubcreatif_backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueEntityValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEntity {
    String message() default "Une entrée avec ces valeurs existe déjà";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * L'entité à interroger (nom de la classe JPA)
     */
    Class<?> entity();

    /**
     * Clause WHERE de la requête, avec les paramètres nommés (ex: :code, :libelle)
     */
    String condition();

    /**
     * Liste des noms de propriétés dans l'objet à valider (doivent matcher les noms dans `condition`)
     */
    String[] parameters();

    /**
     * Champ ID à exclure (optionnel)
     */
    String idField() default "";
}
