package bj.hubcreatif.hubcreatif_backend.enums;

public enum FilterOperatorType {

    EQUAL("eq", "Égal à"),
    NOT_EQUAL("neq", "Différent de"),
    CONTAINS("contains", "Contient"),
    STARTS_WITH("start", "Commençant par"),
    ENDS_WITH("ends", "Finissant par"),
    GREATER_THAN("gt", "Supérieur à"),
    GREATER_EQUALS_THAN("gte", "Supérieur ou égal à"),
    LESS_THAN("lt", "Inférieur à"),
    LESS_EQUALS_THAN("lte", "Inférieur ou égal à"),
    VALUES_IN("in", "Est dans la liste"),
    BETWEEN("between", "Compris entre"),
    ;

    private static final FilterOperatorType[] VALUES;

    static {
        VALUES = values();
    }

    private final String value;
    private final String description;

    FilterOperatorType(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static FilterOperatorType[] getValues() {
        return VALUES;
    }

    public static FilterOperatorType resolve(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Filter operator cannot be null");
        }
        for (FilterOperatorType status : VALUES) {
            if (value.equals(status.value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Cannot resolve " + value);
    }


    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}

