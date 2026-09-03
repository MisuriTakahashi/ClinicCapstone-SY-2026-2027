package clinic;

import java.time.LocalDateTime;

/** Immutable payload recorded whenever an emergency check-in is raised. */
public record EmergencyRecord(String studentId, EmergencyType emergencyType,
        String description, LocalDateTime timestamp) {
    public EmergencyRecord {
        if (!ValidationUtils.isValidLRN(studentId))
            throw new IllegalArgumentException("A valid 12-digit LRN is required.");
        if (emergencyType == null)
            throw new IllegalArgumentException("An emergency category is required.");
        if (description == null || description.trim().isEmpty())
            throw new IllegalArgumentException("Emergency details are required.");
        description = description.trim();
        timestamp = timestamp == null ? LocalDateTime.now() : timestamp;
    }
}
