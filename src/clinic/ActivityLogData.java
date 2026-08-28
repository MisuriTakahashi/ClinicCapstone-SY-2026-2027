/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Database-backed activity/audit logging.
 *
 * Activity records are stored in the encrypted H2 database instead of a
 * PLAINTEXT inventory_activity.log file.
 */
public final class ActivityLogData {

        private static final DateTimeFormatter LEGACY_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a");

    private ActivityLogData() {
    }

    public static void log(
            String action,
            String details,
            String actor) throws SQLException {

        try (Connection conn =
                     DatabaseManager.getConnection()) {

            log(conn, action, details, actor);
        }
    }

    static void log(
            Connection conn,
            String action,
            String details,
            String actor) throws SQLException {

        insert(
                conn,
                null,
                action,
                details,
                actor
        );
    }

    private static void insert(
            Connection conn,
            LocalDateTime timestamp,
            String action,
            String details,
            String actor) throws SQLException {

        if (conn == null) {
            throw new SQLException(
                    "Database connection is unavailable."
            );
        }

        String sql =
                "INSERT INTO ACTIVITY_LOG("
                + "TIMESTAMP, ACTION, DETAILS, ACTOR"
                + ") VALUES("
                + "COALESCE(?, CURRENT_TIMESTAMP), ?, ?, ?"
                + ")";

        String safeAction =
                (action == null || action.isBlank())
                ? "UNKNOWN"
                : action.trim();

        String safeDetails =
                details == null ? "" : details;

        String safeActor =
                (actor == null || actor.isBlank())
                ? "Unknown"
                : actor.trim();

        try (PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            if (timestamp == null) {
                ps.setTimestamp(1, null);
            } else {
                ps.setTimestamp(
                        1,
                        Timestamp.valueOf(timestamp)
                );
            }

            ps.setString(2, safeAction);
            ps.setString(3, safeDetails);
            ps.setString(4, safeActor);

            ps.executeUpdate();
        }
    }

    /** One raw ACTIVITY_LOG row, with no display formatting applied. */
    public record Entry(
            Timestamp timestamp,
            String action,
            String details,
            String actor) {
    }

    /** Result of parsing a USE_MEDICINE details string into its parts. */
    public record UsageInfo(
            String medicineName,
            int quantity) {
    }

    /**
     * Returns the raw ACTIVITY_LOG rows, newest first. This is the single place
     * that reads the table; loadFormatted() below builds display text from it,
     * and anything else (e.g. the daily report's Stock Overview section) that
     * needs to reason about individual entries should use this instead of
     * re-querying the table itself.
     */
    public static ArrayList<Entry> loadEntries()
            throws SQLException {

        ArrayList<Entry> entries =
                new ArrayList<>();

        String sql =
                "SELECT TIMESTAMP, ACTION, DETAILS, ACTOR "
                + "FROM ACTIVITY_LOG "
                + "ORDER BY TIMESTAMP DESC, ID DESC";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            while (rs.next()) {

                entries.add(new Entry(
                        rs.getTimestamp("TIMESTAMP"),
                        rs.getString("ACTION"),
                        rs.getString("DETAILS"),
                        rs.getString("ACTOR")
                ));
            }
        }

        return entries;
    }

    /**
     * Returns records newest-first, formatted similarly to the old activity
     * log so the existing Admin Panel and report exporter can keep using it.
     * The internal ACTION code (e.g. ADD_MEDICINE, DELETE_ACCOUNT) is never
     * shown to the user here — humanize() below converts it into a natural
     * sentence first. This is the one place that formatting happens, so the
     * Admin Panel and the Excel report automatically stay in sync.
     */
    public static ArrayList<String> loadFormatted()
            throws SQLException {

        ArrayList<String> lines =
                new ArrayList<>();

        for (Entry entry : loadEntries()) {

            String time =
                    entry.timestamp() == null
                    ? LocalDateTime.now()
                            .format(DISPLAY_FORMAT)
                    : entry.timestamp()
                            .toLocalDateTime()
                            .format(DISPLAY_FORMAT);

            String message =
                    humanize(
                            entry.action(),
                            entry.details()
                    );

            String actor =
                    entry.actor() == null
                    || entry.actor().isBlank()
                    ? "Unknown"
                    : entry.actor();

            lines.add(
                    "[" + time + "] "
                    + message
                    + " | By: " + actor
            );
        }

        return lines;
    }

    // ---------------------------------------------------------------
    // Human-readable formatting
    // ---------------------------------------------------------------
    //
    // The database keeps the internal ACTION codes (ADD_MEDICINE,
    // DELETE_ACCOUNT, etc.) because they're useful for the backend.
    // Everything below only affects what gets displayed to a person —
    // in the Admin Panel activity feed and in the Excel daily report,
    // both of which read through loadFormatted() above.

    private static final Pattern ADD_MEDICINE_PATTERN =
            Pattern.compile("^(\\d+)x (.+)$");

    private static final Pattern EDIT_MEDICINE_PATTERN =
            Pattern.compile("^(.+) -> (.+) \\((\\d+)x\\)$");

    private static final Pattern USE_MEDICINE_PATTERN =
            Pattern.compile("^Student (.+) used (\\d+)x (.+)$");

    private static final Pattern RESTOCK_MEDICINE_PATTERN =
            Pattern.compile("^Returned (\\d+)x (.+) \\(visit edited\\)$");

    /** Converts an ACTION code + its DETAILS into a natural sentence. */
    private static String humanize(String action, String details) {

        String safeDetails =
                details == null ? "" : details.trim();

        String code =
                action == null ? "" : action.trim().toUpperCase();

        switch (code) {

            case "ADD_MEDICINE": {
                Matcher m = ADD_MEDICINE_PATTERN.matcher(safeDetails);
                if (m.matches()) {
                    int qty = parseIntSafe(m.group(1));
                    String name = m.group(2);
                    return "Added " + qty + " " + unit(qty)
                            + " of " + name + " to inventory";
                }
                break;
            }

            case "EDIT_MEDICINE": {
                Matcher m = EDIT_MEDICINE_PATTERN.matcher(safeDetails);
                if (m.matches()) {
                    String oldName = m.group(1);
                    String newName = m.group(2);
                    int qty = parseIntSafe(m.group(3));
                    return "Updated medicine from " + oldName
                            + " to " + newName
                            + " with " + qty + " " + unit(qty);
                }
                break;
            }

            case "DELETE_MEDICINE":
                if (!safeDetails.isEmpty()) {
                    return "Removed " + safeDetails + " from inventory";
                }
                break;

            case "USE_MEDICINE": {
                Matcher m = USE_MEDICINE_PATTERN.matcher(safeDetails);
                if (m.matches()) {
                    String student = m.group(1);
                    int qty = parseIntSafe(m.group(2));
                    String product = m.group(3);
                    return student + " was given " + qty + " "
                            + unit(qty) + " of " + product;
                }
                break;
            }

            case "RESTOCK_MEDICINE": {
                Matcher m = RESTOCK_MEDICINE_PATTERN.matcher(safeDetails);
                if (m.matches()) {
                    int qty = parseIntSafe(m.group(1));
                    String product = m.group(2);
                    return "Returned " + qty + " " + unit(qty)
                            + " of " + product
                            + " to inventory after editing a visit";
                }
                break;
            }

            case "CREATE_ACCOUNT":
            case "DELETE_ACCOUNT":
                // These are already written as plain sentences when logged
                // (see AccountData / DatabaseManager) - just drop the
                // "protected default" wording used for the system-generated
                // default Head Admin message, and drop the technical prefix.
                return safeDetails.replaceAll(
                        "(?i)protected default ", "");

            default:
                break;
        }

        // Fallback for anything unrecognized (including imported legacy
        // lines): show the details as-is rather than a raw ACTION code.
        return safeDetails.isEmpty()
                ? humanizeUnknownAction(code)
                : safeDetails;
    }

    private static String humanizeUnknownAction(String code) {
        if (code.isEmpty()) {
            return "Activity recorded";
        }
        String words = code.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(words.charAt(0)) + words.substring(1);
    }

    private static String unit(int qty) {
        return qty == 1 ? "unit" : "units";
    }

    private static int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Parses a USE_MEDICINE details string (e.g. "Student John used 1x
     * Biogesic") into the medicine name and quantity used. Returns null if
     * the action isn't USE_MEDICINE or the details don't match the expected
     * shape. Shared by humanize() above and by the daily report's Stock
     * Overview section, so this parsing lives in exactly one place.
     */
    public static UsageInfo parseMedicineUsage(String action, String details) {
        if (!"USE_MEDICINE".equalsIgnoreCase(action) || details == null) {
            return null;
        }
        Matcher m = USE_MEDICINE_PATTERN.matcher(details.trim());
        if (!m.matches()) {
            return null;
        }
        int qty = parseIntSafe(m.group(2));
        String product = m.group(3);
        return new UsageInfo(product, qty);
    }

    /**
     * Imports the old plaintext log only once, when the database activity
     * table is empty. The application never writes to that file after this.
     */
    public static int importLegacyFile(
            File file)
            throws IOException, SQLException {

        if (file == null
                || !file.exists()
                || !file.isFile()) {

            return 0;
        }

        if (hasAnyRecords()) {
            return 0;
        }

        int imported = 0;

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(file));
             Connection conn =
                     DatabaseManager.getConnection()) {

            conn.setAutoCommit(false);

            try {

                String line;

                while ((line =
                        reader.readLine()) != null) {

                    ParsedLegacyLine parsed =
                            parseLegacyLine(line);

                    if (parsed == null) {
                        continue;
                    }

                    insert(
                            conn,
                            parsed.timestamp,
                            parsed.action,
                            parsed.details,
                            parsed.actor
                    );

                    imported++;
                }

                conn.commit();

            } catch (Exception ex) {

                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }

                if (ex instanceof SQLException sqlEx) {
                    throw sqlEx;
                }

                if (ex instanceof IOException ioEx) {
                    throw ioEx;
                }

                throw new IOException(
                        "Could not import the legacy activity log.",
                        ex
                );

            } finally {
                conn.setAutoCommit(true);
            }
        }

        return imported;
    }

    private static boolean hasAnyRecords()
            throws SQLException {

        String sql =
                "SELECT 1 FROM ACTIVITY_LOG LIMIT 1";

        try (Connection conn =
                     DatabaseManager.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs =
                     ps.executeQuery()) {

            return rs.next();
        }
    }

    private static ParsedLegacyLine parseLegacyLine(
            String line) {

        if (line == null) {
            return null;
        }

        String trimmed = line.trim();

        if (trimmed.isEmpty()) {
            return null;
        }

        LocalDateTime timestamp = null;
        String content = trimmed;

        int closeBracket =
                trimmed.indexOf(']');

        if (trimmed.startsWith("[")
                && closeBracket > 1) {

            String timestampText =
                    trimmed.substring(
                            1,
                            closeBracket
                    ).trim();

           try {

                timestamp =
                        LocalDateTime.parse(
                                timestampText,
                                LEGACY_TIMESTAMP_FORMAT
                        );

            } catch (DateTimeParseException ignored) {
                // Use current database timestamp.
            }

            content =
                    trimmed.substring(
                            closeBracket + 1
                    ).trim();
        }

        int byIndex =
                content.lastIndexOf(" | By:");

        String details =
                byIndex >= 0
                ? content.substring(
                        0,
                        byIndex
                ).trim()
                : content;

        String actor =
                byIndex >= 0
                ? content.substring(
                        byIndex
                        + " | By:".length()
                ).trim()
                : "Unknown";

        String action =
                "LEGACY_ACTIVITY";

        String lower =
                details.toLowerCase();

        if (lower.startsWith("added ")) {

            action = "ADD_MEDICINE";

        } else if (lower.startsWith("edited ")) {

            action = "EDIT_MEDICINE";

        } else if (lower.startsWith("deleted ")) {

            action = "DELETE_MEDICINE";

        } else if (
                lower.startsWith("student ")
                && lower.contains(" used ")) {

            action = "USE_MEDICINE";

        } else if (lower.startsWith("returned ")) {

            action = "RESTOCK_MEDICINE";
        }

        return new ParsedLegacyLine(
                timestamp,
                action,
                details,
                actor
        );
    }

    private record ParsedLegacyLine(
            LocalDateTime timestamp,
            String action,
            String details,
            String actor) {
    }
}