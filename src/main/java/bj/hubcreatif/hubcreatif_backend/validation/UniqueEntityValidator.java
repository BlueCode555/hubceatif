package bj.hubcreatif.hubcreatif_backend.validation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jboss.logging.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class UniqueEntityValidator implements ConstraintValidator<UniqueEntity, Object> {
    private final static Logger logger = Logger.getLogger(UniqueEntityValidator.class);

    @PersistenceContext
    private final EntityManager entityManager;

    private Class<?> entityClass;
    private String condition;
    private String[] parameters;
    private String idField;

    public UniqueEntityValidator(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void initialize(UniqueEntity constraintAnnotation) {
        this.entityClass = constraintAnnotation.entity();
        this.condition = constraintAnnotation.condition();
        this.parameters = constraintAnnotation.parameters();
        this.idField = constraintAnnotation.idField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            if (value == null) return true;

            StringBuilder jpql = new StringBuilder("SELECT COUNT(e) FROM ");
            jpql.append(entityClass.getSimpleName()).append(" e WHERE ").append(condition);

            Map<String, Object> params = new HashMap<>();
            for (String param : parameters) {
                Field field = value.getClass().getDeclaredField(param);
                field.setAccessible(true);
                Object fieldValue = field.get(value);
                params.put(param, fieldValue);
            }

            if (!idField.isEmpty()) {
                Field id = value.getClass().getDeclaredField(idField);
                id.setAccessible(true);
                Object idValue = id.get(value);
                if (idValue != null) {
                    jpql.append(" AND e.").append(idField).append(" != :_id");
                    params.put("_id", idValue);
                }
            }

            TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                query.setParameter(entry.getKey(), entry.getValue());
            }

            return query.getSingleResult() == 0;

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }
}
