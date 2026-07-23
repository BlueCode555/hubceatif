package bj.hubcreatif.hubcreatif_backend.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static bj.hubcreatif.hubcreatif_backend.utils.AppUtil.isNullString;

public class ValueNotExistsValidator implements ConstraintValidator<ValueNotExists, String> {
    @PersistenceContext
    private final EntityManager entityManager;

    private Class<?> entityClass;
    private String fieldName;

    public ValueNotExistsValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(ValueNotExists constraintAnnotation) {
        this.entityClass = constraintAnnotation.entity();
        this.fieldName = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (isNullString(value)) return true;

        String jpql = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :value";

        Long count = entityManager.createQuery(jpql, Long.class)
                .setParameter("value", value)
                .getSingleResult();

        return count > 0; //
    }
}
