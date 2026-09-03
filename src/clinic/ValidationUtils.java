package clinic;

/** Shared validation rules for student and inventory data. */
public final class ValidationUtils {
    private ValidationUtils() { }

    public static boolean isValidName(String value) {
        return value != null && value.trim().matches("^[a-zA-Z\\s\\-']+$");
    }

    public static boolean isValidGradeSection(String value) {
        return value != null && value.trim().matches("^[a-zA-Z0-9\\s\\-]+$");
    }

    public static boolean isValidLRN(String value) {
        return value != null && value.trim().matches("^\\d{12}$");
    }

    /** Exactly 11 numeric digits in the local Philippine mobile format: 09XXXXXXXXX. */
    public static boolean isValidPhoneNumber(String value) {
        return value != null && value.trim().matches("^09\\d{9}$");
    }

    public static boolean isValidAllergy(String value) {
        return value != null && value.trim().matches("^[a-zA-Z\\s,\\-]+$");
    }

    public static boolean isValidGuardianName(String value) {
        return value != null && value.trim().matches("^[a-zA-Z\\s]+$");
    }

    public static boolean isValidHealthCondition(String value) {
        return value != null && value.trim().matches("^[a-zA-Z0-9\\s,.()\\-]+$");
    }

    public static boolean isValidMedicineText(String value) {
        return value != null && value.trim().matches("^[a-zA-Z][a-zA-Z0-9\\s()\\-/,.'']*$");
    }
}
