package bj.hubcreatif.hubcreatif_backend.specs;

import bj.hubcreatif.hubcreatif_backend.enums.FilterOperatorType;
import bj.hubcreatif.hubcreatif_backend.exception.InvalidFilterException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static bj.hubcreatif.hubcreatif_backend.enums.FilterOperatorType.resolve;


public class FilterSpecification<T> {
    public Specification<T> applyFilters(List<FilterCriteria> filters) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (FilterCriteria filter : filters) {
                if (filter.condition() == null || filter.value() == null) {
                    continue;
                }

                String field = filter.field();
                FilterOperatorType condition = resolve(filter.condition());
                Object value = filter.value();

                if(value instanceof  String) {
                    value = value.toString().toLowerCase();
                }

                Path<?> path = SpecificationUtils.getPath(root, field);
                Expression<?> expression = getExpression(path, builder);

                switch (condition) {
                    case EQUAL -> predicates.add(builder.equal(expression, value));
                    case NOT_EQUAL -> predicates.add(builder.notEqual(expression, value));
                    case CONTAINS -> predicates.add(builder.like( builder.lower(path.as(String.class)), "%" + value + "%"));
                    case STARTS_WITH -> predicates.add(builder.like(builder.lower(path.as(String.class)), value + "%"));
                    case ENDS_WITH -> predicates.add(builder.like(builder.lower(path.as(String.class)), "%" + value));
                    case GREATER_THAN -> predicates.add(builder.greaterThan(path.as(String.class), value.toString()));
                    case GREATER_EQUALS_THAN ->
                            predicates.add(builder.greaterThanOrEqualTo(path.as(String.class), value.toString()));
                    case LESS_THAN -> predicates.add(builder.lessThan(path.as(String.class), value.toString()));
                    case LESS_EQUALS_THAN -> predicates.add(builder.lessThanOrEqualTo(path.as(String.class), value.toString()));
                    case VALUES_IN -> {
                        List<String> values = Stream.of((String.valueOf(value)).split(",")).map(String::trim).toList();
                        predicates.add(expression.in(values));
                    }
                    default -> throw new InvalidFilterException("Invalid filter condition:: " + condition.name());
                }
            }
            // predicates.add(builder.greaterThan(root.get("deletedAt"), LocalDateTime.now()));
            // predicates.add(builder.isNull(root.get("deletedAt")));
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Expression<?> getExpression(Path<?> path, CriteriaBuilder builder) {
        if (path.getJavaType().equals(String.class)) {
            return builder.lower(path.as(String.class));
        } else {
            return path;
        }
    }
}
