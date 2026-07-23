package bj.hubcreatif.hubcreatif_backend.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

public final class AppUtil {

    private AppUtil() {}

    // ── Constantes globales ───────────────────────────────────────────────────

    public static Map<String, String> mapConstant;

    // ── Validations de chaînes ────────────────────────────────────────────────

    public static boolean notNullString(final String value) {
        return value != null
                && !value.isBlank()
                && !value.equals("null")
                && !value.equals("NULL")
                && !value.equals("undefined")
                && !value.equals("UNDEFINED");
    }

    public static boolean isNullString(final String value) {
        return !notNullString(value);
    }

    /** Retourne null si la chaîne est vide ou invalide */
    public static String emptyToNull(String str) {
        return notNullString(str) ? str : null;
    }

    /** Retourne "" si la chaîne est null ou invalide */
    public static String nullToEmpty(String str) {
        return notNullString(str) ? str : "";
    }

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*"
                     + "@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        return email.matches(regex);
    }

    // ── Constantes de configuration ───────────────────────────────────────────

    public static String getConstant(String key, String defaultValue) {
        if (mapConstant == null) return defaultValue;
        String value = mapConstant.get(key);
        return value == null ? defaultValue : value;
    }

    public static int pageSize() {
        return Integer.parseInt(getConstant(ConstantsUtil.PAGE_SIZE, "25"));
    }

    public static String getFrontEndUrl() {
        return mapConstant != null
                ? mapConstant.getOrDefault("FRONTEND_URL", "http://localhost:4200")
                : "http://localhost:4200";
    }

    // ── Utilisateur connecté ──────────────────────────────────────────────────

    /**
     * Retourne l'ID de l'utilisateur connecté en lisant directement le SecurityContext,
     * sans passer par ApplicationContextHolder (anti-pattern évité).
     *
     * Cette méthode est réservée aux utilitaires qui n'ont pas accès au contexte Spring.
     */
    public static String connectedUserKeycloakUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        if (auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject(); // sub = UUID Keycloak immuable
        }
        return null;
    }

    // ── Validation Jakarta ────────────────────────────────────────────────────

    @SuppressWarnings("all")
    public static <T> void validate(T t) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if (!violations.isEmpty()) throw new ConstraintViolationException(violations);
    }

    // ── Dates ─────────────────────────────────────────────────────────────────

    public static String frenchDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static LocalDate parseFrenchDate(String dateString) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ── Collections ───────────────────────────────────────────────────────────

    public static <T> List<T> convertToList(Object input, Function<Object, T> mapper) {
        List<T> result = new ArrayList<>();
        if (input instanceof List<?>) {
            for (Object item : (List<?>) input) {
                try {
                    T value = mapper.apply(item);
                    if (value != null) result.add(value);
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    public static <E> List<E> mapToList(Map<String, E> map) {
        return new ArrayList<>(map.values());
    }

    // ── Génération de codes ───────────────────────────────────────────────────

    public static String generateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}