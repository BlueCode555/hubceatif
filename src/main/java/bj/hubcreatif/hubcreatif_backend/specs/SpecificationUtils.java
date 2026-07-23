package bj.hubcreatif.hubcreatif_backend.specs;

import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public class SpecificationUtils {
    /**
     * Permet d'accéder dynamiquement à un champ, y compris les relations.
     * Exemple: "customer.name" ou "customer.address.city"
     */
    public static <T> Path<?> getPath(Root<T> root, String path) {
        String[] parts = path.split("\\.");
        Path<?> current = root;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (current instanceof From<?, ?> from) {
                // S'il reste d'autres parties après, join (relation)
                if (i < parts.length - 1) {
                    current = from.join(part, JoinType.LEFT);
                } else {
                    current = from.get(part); // dernière propriété
                }
            } else {
                current = current.get(part);
            }
        }

        return current;
    }
}
