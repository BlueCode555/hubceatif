package bj.hubcreatif.hubcreatif_backend.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Paramètres d'authentification chargés depuis application.yml / variables d'environnement.
 * <p>
 * Exemple application.yml :
 * <p>
 * auth:
 * auth:
 * dev-mode: ${DEV_MODE:false}
 * code-expiration-minutes: ${CODE_EXPIRATION_MINUTES:5}
 * max-tentatives: ${MAX_TENTATIVES_CODE:5}
 * keycloak-realm: ${KEYCLOAK_REALM:hubcreatif}
 * keycloak-client-id: ${KEYCLOAK_CLIENT_ID:hubcreatif-backend}
 * keycloak-client-secret: ${KEYCLOAK_CLIENT_SECRET:}
 * keycloak-url: ${KEYCLOAK_URL:http://localhost:8080}
 */
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    /**
     * Active le mode développement.
     * En DEV : tout code numérique (même "1") est accepté sans vérification.
     * ⚠️ Doit être false en production — contrôlé UNIQUEMENT côté backend via DEV_MODE=true.
     */
    private boolean devMode = false;

    /**
     * Durée de validité d'un code OTP en minutes (défaut : 5).
     */
    private int codeExpirationMinutes = 5;

    /**
     * Nombre maximum de tentatives incorrectes avant blocage du code (défaut : 5).
     */
    private int maxTentatives = 5;

    // ── Keycloak ──────────────────────────────────────────────────────────────

    private String keycloakRealm = "hubcreatif";
    private String keycloakClientId = "hubcreatif-backend";
    private String keycloakClientSecret = "";
    private String keycloakUrl = "http://localhost:8080";

    /**
     * Compte technique admin Keycloak — utilisé uniquement par KeycloakAdminService
     */
    private String keycloakAdminUser = "admin";
    private String keycloakAdminPassword = "";

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean v) {
        this.devMode = v;
    }

    public int getCodeExpirationMinutes() {
        return codeExpirationMinutes;
    }

    public void setCodeExpirationMinutes(int v) {
        this.codeExpirationMinutes = v;
    }

    public int getMaxTentatives() {
        return maxTentatives;
    }

    public void setMaxTentatives(int v) {
        this.maxTentatives = v;
    }

    public String getKeycloakRealm() {
        return keycloakRealm;
    }

    public void setKeycloakRealm(String v) {
        this.keycloakRealm = v;
    }

    public String getKeycloakClientId() {
        return keycloakClientId;
    }

    public void setKeycloakClientId(String v) {
        this.keycloakClientId = v;
    }

    public String getKeycloakClientSecret() {
        return keycloakClientSecret;
    }

    public void setKeycloakClientSecret(String v) {
        this.keycloakClientSecret = v;
    }

    public String getKeycloakUrl() {
        return keycloakUrl;
    }

    public void setKeycloakUrl(String v) {
        this.keycloakUrl = v;
    }

    public String getKeycloakAdminUser() {
        return keycloakAdminUser;
    }

    public void setKeycloakAdminUser(String v) {
        this.keycloakAdminUser = v;
    }

    public String getKeycloakAdminPassword() {
        return keycloakAdminPassword;
    }

    public void setKeycloakAdminPassword(String v) {
        this.keycloakAdminPassword = v;
    }

    /**
     * URL complète du token endpoint Keycloak
     */
    public String getTokenUrl() {
        return keycloakUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/token";
    }

    /**
     * URL complète du logout endpoint Keycloak
     */
    public String getLogoutUrl() {
        return keycloakUrl + "/realms/" + keycloakRealm + "/protocol/openid-connect/logout";
    }
}