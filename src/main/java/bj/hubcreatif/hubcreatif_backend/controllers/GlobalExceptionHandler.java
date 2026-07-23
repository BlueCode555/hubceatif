package bj.hubcreatif.hubcreatif_backend.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.client.ResourceAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestionnaire global des exceptions REST — version fusionnée.
 *
 * Ordre de spécificité (du plus précis au plus général) :
 *
 *   DoublonImportException          → 409  payload enrichi (jobExistant)
 *   AccessDeniedException           → 403  action hors de la commune de l'utilisateur
 *   EntityNotFoundException         → 404
 *   MethodArgumentNotValidException → 400  erreurs @Valid sur @RequestBody
 *   ConstraintViolationException    → 400  violations Jakarta (@NotNull, @Size…)
 *   IllegalArgumentException        → 400  paramètre invalide, code non supporté
 *   IllegalStateException           → 422  état métier incohérent (sources vides…)
 *   RuntimeException                → 500  stacktrace loguée, message exposé
 *   Exception                       → 500  filet de sécurité, message générique
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── 403 Forbidden ─────────────────────────────────────────────────────────

    /**
     * Déclenché quand un utilisateur non SUPER-ADMIN tente d'importer, lister
     * ou supprimer un import en dehors de sa propre commune (ImportJobService).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex) {
        return erreur(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ── 404 Not Found ─────────────────────────────────────────────────────────

    /** Entité introuvable en base (Spring ou Jakarta EntityNotFoundException). */
    @ExceptionHandler({EntityNotFoundException.class,
                       jakarta.persistence.EntityNotFoundException.class})
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        return erreur(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ── 400 Bad Request ───────────────────────────────────────────────────────

    /** Erreurs de validation @Valid sur les @RequestBody. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " : " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return erreur(HttpStatus.BAD_REQUEST, "Données invalides : " + details);
    }

    /** Violations de contraintes Jakarta (@NotNull, @Size…). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException ex) {
        String details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .collect(Collectors.joining(", "));
        return erreur(HttpStatus.BAD_REQUEST, "Contrainte violée : " + details);
    }

    /**
     * Paramètre invalide, code indicateur non supporté, combinaison impossible.
     * Ex : indicateurCode="XYZ" non supporté, OTP invalide, email déjà utilisé.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return erreur(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ── 422 Unprocessable Entity ──────────────────────────────────────────────

    /**
     * Les données sont techniquement valides mais inutilisables dans le contexte
     * métier courant.
     * Ex : sources d'estimation vides après filtrage par commune,
     *      quota de tentatives dépassé, compte non activé.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalState(IllegalStateException ex) {
        return erreur(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // ── 409 Conflict : violation de contrainte unique en base ────────────────

    /**
     * Déclenché lors d'une inscription simultanée avec le même email ou NPI.
     * Cas rare (race condition) rendu possible uniquement sous forte concurrence.
     * Le rate limiter réduit déjà la probabilité d'y arriver.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Collision de contrainte unique : {}", ex.getMostSpecificCause().getMessage());
        return erreur(HttpStatus.CONFLICT,
                "Une inscription est déjà en cours pour ces informations. " +
                "Veuillez patienter un instant puis réessayer.");
    }

    // ── 503 Service Unavailable — service tiers injoignable ──────────────────

    /**
     * Déclenché quand un service externe (Keycloak, SMTP…) est temporairement
     * inaccessible (DNS failure, timeout réseau, coupure…).
     * Retourne 503 pour que le frontend puisse afficher un message adapté
     * ("serveur d'auth indisponible, réessayez") plutôt qu'une erreur générique.
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<?> handleResourceAccess(ResourceAccessException ex) {
        log.warn("Service externe injoignable : {}", ex.getMessage());
        return erreur(HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage() != null ? ex.getMessage()
                        : "Un service externe est temporairement indisponible. Veuillez réessayer.");
    }

    // ── 500 Internal Server Error — RuntimeException ──────────────────────────

    /**
     * Erreurs internes connues (service tiers injoignable, email non envoyé…).
     * La stacktrace complète est loguée ; le message d'exception est exposé
     * au client (utile pour le debug en dev, à restreindre en prod si sensible).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        log.error("Erreur runtime [{}] : {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return erreur(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue. Veuillez réessayer.");
    }

    // ── 500 Internal Server Error — filet de sécurité ────────────────────────

    /**
     * Dernier recours : intercepte toute Exception non couverte par les handlers
     * précédents (ex : IOException, SQLException hors transaction…).
     * La stacktrace est loguée mais JAMAIS exposée au client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneric(Exception ex) {
        log.error("Exception non gérée [{}] : {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        return erreur(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne est survenue. Contactez l'administrateur.");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private ResponseEntity<?> erreur(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "statut",     status.value(),
                "message",    message != null ? message : "Erreur sans message",
                "horodatage", LocalDateTime.now().toString()
        ));
    }
}