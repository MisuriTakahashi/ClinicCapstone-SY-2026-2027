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

/**
 * Database-backed activity/audit logging.
 *
 * Activity records are stored in the encrypted H2 database instead of a
 * plaintext inventory_activity.log file.
 */
public final class ActivityLogData {

    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

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

    /**
     * Returns records newest-first, formatted similarly to the old activity
     * log so the existing Admin Panel and report exporter can keep using it.
     */
    public static ArrayList<String> loadFormatted()
            throws SQLException {

        ArrayList<String> lines =
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

                Timestamp timestamp =
                        rs.getTimestamp("TIMESTAMP");

                String action =
                        rs.getString("ACTION");

                String details =
                        rs.getString("DETAILS");

                String actor =
                        rs.getString("ACTOR");

                String time =
                        timestamp == null
                        ? LocalDateTime.now()
                                .format(DISPLAY_FORMAT)
                        : timestamp
                                .toLocalDateTime()
                                .format(DISPLAY_FORMAT);

                StringBuilder line =
                        new StringBuilder()
                                .append("[")
                                .append(time)
                                .append("] ")
                                .append(
                                        action == null
                                        ? "UNKNOWN"
                                        : action
                                );

                if (details != null
                        && !details.isBlank()) {

                    line.append(": ")
                            .append(details);
                }

                line.append(" | By: ")
                        .append(
                                actor == null
                                || actor.isBlank()
                                ? "Unknown"
                                : actor
                        );

                lines.add(line.toString());
            }
        }

        return lines;
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
                                DISPLAY_FORMAT
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