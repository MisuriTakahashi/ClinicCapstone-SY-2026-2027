package clinic;

public enum EmergencyType {
    FAINTED_UNCONSCIOUS("Fainted / Unconscious"),
    MAJOR_INJURY("Major Injury / Trauma"),
    SEVERE_ALLERGIC_REACTION("Severe Allergic Reaction"),
    ACUTE_MEDICAL_DISTRESS("Acute Medical Distress"),
    OTHER("Other Emergency");

    private final String label;

    EmergencyType(String label) { this.label = label; }

    public String getLabel() { return label; }

    @Override public String toString() { return label; }
}
