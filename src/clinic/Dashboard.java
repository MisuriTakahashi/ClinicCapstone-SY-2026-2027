package clinic;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.BasicStroke;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 * Main dashboard shell. Legacy controller code that referenced controls removed
 * from the NetBeans form has been removed; the generated form remains intact.
 */
public class Dashboard extends javax.swing.JFrame {
    private static final Color MAIN_BACKGROUND = Color.decode("#F8F9FA");
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_DARK = Color.decode("#1E293B");
    private static final Color CARD_BORDER = Color.decode("#E5E7EB");
    private static final Color TABLE_GRID = Color.decode("#F1F5F9");
    private static final Color ACCENT_BLUE = Color.decode("#2563EB");
    private static final Color TABLE_HEADER = Color.decode("#1E293B");
    private static final Color TABLE_HOVER = Color.decode("#F1F5F9");
    private javax.swing.JPanel dimmingOverlay;
    private final AccountSystem loggedInAccount;
    private javax.swing.JPanel currentPanel;
    private StudentDetails selectedStudent;
    private int studentSearchGeneration;
    private final javax.swing.JButton CancelCheckinBTN = new javax.swing.JButton("Cancel");
    private javax.swing.JButton activeNavButton;
    private javax.swing.JLabel homeTotalTodayValue;
    private javax.swing.JLabel homeActiveVisitsValue;
    private javax.swing.JLabel homeHighTempsValue;
    private javax.swing.JLabel homeLowStockValue;
    private SymptomChartPanel homeSymptomChart;
    private WeeklyBarChartPanel weeklyBarChart;
    private HealthConditionsPieChartPanel healthConditionsPieChart;
    private javax.swing.JLabel statisticSpecialNeedsValue;
    private javax.swing.JLabel statisticAllergyValue;
    private javax.swing.JLabel statisticMedicineValue;
    private javax.swing.JLabel statisticSentHomeValue;
    private final javax.swing.JButton popupEmergencyBTN = new javax.swing.JButton("Emergency");
    private final javax.swing.JButton expressEmergencyBTN = new javax.swing.JButton("Emergency");
    private final javax.swing.JButton expressDispositionBTN = new javax.swing.JButton("Send Home / Back");
    private final javax.swing.JButton archiveVisitBTN = new javax.swing.JButton("Archive Selected");
    private final javax.swing.JButton exportReportBTN = new javax.swing.JButton("Export Report");
    private java.util.List<CheckinSystem> recentVisits = java.util.List.of();

    public Dashboard(AccountSystem account) {
        loggedInAccount = account;
    FlatLightLaf.setup();
    initComponents();
    applyModernContentTheme();
    styleSidePanel();
     javax.swing.Timer clockTimer = new javax.swing.Timer(1000, e -> {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy  |  hh:mm:ss a");
        DateTimeLabel.setText(now.format(formatter));
    });
    clockTimer.start();

    // Create the overlay — size will be set dynamically when shown
    dimmingOverlay = new DimmingOverlay();
    getContentPane().add(dimmingOverlay, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1760, 850));
    getContentPane().setComponentZOrder(dimmingOverlay, 1);

    // Click outside the popup to close
    dimmingOverlay.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            closeCheckinPopup();
        }
    });

    UserSession.start(account);
    configureNavigation();
    configureClinicWorkflow();
    configureStatistics();
    installSessionPersistence();
    setIconImage(AppIcon.getIcon());
    setLocationRelativeTo(null);
    updateActiveButton(HomeBTN);
    }

    public Dashboard() {
        this(null);
    }

    /** Refreshes the remembered session without storing a password. */
    private void installSessionPersistence() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent event) {
                SessionManager.saveSession(loggedInAccount);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowGainedFocus(java.awt.event.WindowEvent event) {
                SessionManager.saveSession(loggedInAccount);
            }
        });
    }

    /** Keeps the generated AbsoluteLayout untouched while ensuring that only
     * one of its overlapping content panels can ever be painted. */
    private void configureNavigation() {
        HomeBTN.addActionListener(e -> showPanel(MainPanel));
        CheckinBTN.addActionListener(e -> showPanel(CheckInPanel1));
        StatisticBTN.addActionListener(e -> showPanel(StatisticPanel));

        for (Component panel : new Component[]{MainPanel, CheckInPanel1,
                CheckInPopup, StatisticPanel}) {
            panel.setVisible(false);
        }
        showPanel(MainPanel);
    }
    private void styleSidePanel() {
    // 1. Define Colors
    java.awt.Color sidePanelBg = new java.awt.Color(15, 23, 42); // Dark Slate
    java.awt.Color activeText = java.awt.Color.WHITE;
    java.awt.Color inactiveText = new java.awt.Color(148, 163, 184); // Muted Blue-Gray
    java.awt.Color hoverBg = new java.awt.Color(30, 41, 59); // Slightly lighter slate for hover
    java.awt.Color blueLine = new java.awt.Color(59, 130, 246); // The bright blue line

    // Apply background to SidePanel
    SidePanel.setBackground(sidePanelBg);
    jLabel3.setForeground(java.awt.Color.WHITE); // Title color

    // 2. Setup all navigation buttons
    javax.swing.JButton[] navButtons = {HomeBTN, CheckinBTN, StatisticBTN, InventoryBTN};
    
    for (javax.swing.JButton btn : navButtons) {
        // Base inactive style
        btn.setBackground(sidePanelBg);
        btn.setForeground(inactiveText);
        btn.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 14));
        btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 24, 12, 24));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true); // Important for background color to show

        // 3. Add Smart Hover Logic
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Only apply hover style if this button is NOT the active one
                if (btn != activeNavButton) {
                    btn.setBackground(hoverBg);
                    btn.setForeground(java.awt.Color.WHITE);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn == activeNavButton) {
                    // Re-apply active style to ensure it stays white with the blue line
                    btn.setBackground(sidePanelBg); // Or a specific active bg if you want
                    btn.setForeground(activeText);
                    btn.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 14));
                    btn.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                        javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, blueLine),
                        javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 24)
                    ));
                } else {
                    // Revert to inactive style
                    btn.setBackground(sidePanelBg);
                    btn.setForeground(inactiveText);
                    btn.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 14));
                    btn.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 24, 12, 24));
                }
            }
        });
    }
}

    /** Applies the light theme only to content right of the dark navigation rail. */
    private void applyModernContentTheme() {
        for (javax.swing.JPanel panel : new javax.swing.JPanel[]{MainPanel, CheckInPanel1, StatisticPanel}) {
            panel.setBackground(MAIN_BACKGROUND);
        }

        for (javax.swing.JComponent card : new javax.swing.JComponent[]{
                jPanel2, jPanel3, jPanel4, jPanel5, jPanel6, jPanel7, jPanel8,
                jPanel9, jPanel10, jPanel11, jPanel12, jPanel13, jPanel14, jPanel15}) {
            styleCard(card);
        }

        styleTable(ReasonTable, -1);
        styleTable(jTable1, 6);
        styleTable(jTable2, 3);

        stylePrimaryButton(CheckInBTN);
        stylePrimaryButton(SentHomeBTN);
        stylePrimaryButton(ECheckin);
        stylePrimaryButton(jButton1);
        EmergencyBTN.setBackground(Color.decode("#DC2626"));
        EmergencyBTN.setForeground(Color.WHITE);
        EmergencyBTN.setFocusPainted(false);

        for (javax.swing.JScrollPane pane : new javax.swing.JScrollPane[]{jScrollPane1, jScrollPane2, jScrollPane3}) {
            pane.setBackground(CARD_BACKGROUND);
            pane.getViewport().setBackground(CARD_BACKGROUND);
            pane.setBorder(new RoundedBorder(CARD_BORDER, 14));
        }
    }

    private static void styleCard(javax.swing.JComponent card) {
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(new RoundedBorder(CARD_BORDER, 14));
        card.putClientProperty("JComponent.arc", 14);
    }

    private static void stylePrimaryButton(javax.swing.JButton button) {
        button.setBackground(ACCENT_BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
    }

    private static void styleTable(javax.swing.JTable table, int statusColumn) {
        table.setRowHeight(38);
        table.setFont(new Font("Yu Gothic UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setBackground(CARD_BACKGROUND);
        table.setSelectionBackground(Color.decode("#DBEAFE"));
        table.setSelectionForeground(TEXT_DARK);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(TABLE_GRID);
        table.setIntercellSpacing(new java.awt.Dimension(0, 1));

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Yu Gothic UI", Font.BOLD, 13));
        header.setPreferredSize(new java.awt.Dimension(header.getPreferredSize().width, 40));
        header.setDefaultRenderer(new ModernTableHeaderRenderer());

        for (int column = 0; column < table.getColumnCount(); column++) {
            table.getColumnModel().getColumn(column).setCellRenderer(new ModernTableCellRenderer());
        }
        if (statusColumn >= 0 && statusColumn < table.getColumnCount()) {
            table.getColumnModel().getColumn(statusColumn).setCellRenderer(new StatusBadgeRenderer());
        }
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override public void mouseMoved(java.awt.event.MouseEvent event) {
                table.putClientProperty("dashboard.hoverRow", table.rowAtPoint(event.getPoint()));
                table.repaint();
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseExited(java.awt.event.MouseEvent event) {
                table.putClientProperty("dashboard.hoverRow", -1);
                table.repaint();
            }
        });
    }

    private static final class ModernTableHeaderRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean selected, boolean focused, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            setOpaque(true);
            setBackground(TABLE_HEADER);
            setForeground(Color.WHITE);
            setFont(new Font("Yu Gothic UI", Font.BOLD, 13));
            setHorizontalAlignment(LEFT);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    private static class ModernTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean selected, boolean focused, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            int hoverRow = table.getClientProperty("dashboard.hoverRow") instanceof Integer hovered ? hovered : -1;
            setOpaque(true);
            setForeground(TEXT_DARK);
            setBackground(selected ? Color.decode("#DBEAFE") : row == hoverRow ? TABLE_HOVER : CARD_BACKGROUND);
            setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return this;
        }
    }

    private static final class StatusBadgeRenderer extends ModernTableCellRenderer {
        @Override public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                boolean selected, boolean focused, int row, int column) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, column);
            String status = value == null ? "" : value.toString().trim().toLowerCase(Locale.ROOT);
            if (status.contains("clinic") || status.equals("active")) {
                setBackground(Color.decode("#E0F2FE")); setForeground(Color.decode("#0284C7"));
            } else if (status.contains("home") || status.contains("refer")) {
                setBackground(Color.decode("#FEE2E2")); setForeground(Color.decode("#DC2626"));
            } else if (status.contains("back")) {
                setBackground(Color.decode("#DCFCE7")); setForeground(Color.decode("#16A34A"));
            }
            setHorizontalAlignment(CENTER);
            return this;
        }
    }

    private static final class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int arc;

        RoundedBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override public java.awt.Insets getBorderInsets(Component component) {
            return new java.awt.Insets(1, 1, 1, 1);
        }

        @Override public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(color);
            g.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g.dispose();
        }
    }

  private void showPanel(javax.swing.JPanel target) {
    if (target == null || currentPanel == target) {
        return;
    }
    
    for (javax.swing.JPanel panel : new javax.swing.JPanel[]{MainPanel,
            CheckInPanel1, CheckInPopup, StatisticPanel}) {
        panel.setVisible(panel == target);
    }
    currentPanel = target;
    
    if (target == CheckInPanel1) {
        loadStudentChooser(SearchField.getText().trim());
    }
    
    // Update which button is highlighted
    if (target == MainPanel) {
        updateActiveButton(HomeBTN);
    } else if (target == CheckInPanel1) {
        updateActiveButton(CheckinBTN);
    } else if (target == StatisticPanel) {
        updateActiveButton(StatisticBTN);
    }
    // Note: InventoryBTN opens a new window, so it doesn't need panel switching
    
    getContentPane().revalidate();
    getContentPane().repaint();
}

    private boolean requireAdminPanelAccess() {
        if (loggedInAccount != null && loggedInAccount.canAccessAdminPanel()) {
            return true;
        }
        JOptionPane.showMessageDialog(this,
                "Inventory and management are available to Admin accounts only.",
                "Access Denied", JOptionPane.WARNING_MESSAGE);
        return false;
    }

    /** Wires the existing Dashboard controls to the existing visit/student DAOs. */

    /**
     * Adds live statistics to the existing NetBeans-generated panels without
     * changing their layout, dimensions, colors, or navigation structure.
     */
    private void configureStatistics() {
        setupHomeStatistics();
        setupStatisticCards();
        configureIncidentLogTable();
        setupStatisticCharts();

        // The generated form already contains the titles. The values are kept
        // visually consistent by using the same Yu Gothic UI family.
        refreshStatistics();
    }

    private void setupHomeStatistics() {
        homeTotalTodayValue = addCardValue(jPanel12);
        homeActiveVisitsValue = addCardValue(jPanel10);
        homeHighTempsValue = addCardValue(jPanel9);
        homeLowStockValue = addCardValue(jPanel11);

        // The existing home panel is already titled "SYMPTOM DISTRIBUTION
        // (THIS WEEK)". Replace only its empty body with a paint-only chart.
        homeSymptomChart = new SymptomChartPanel();
        installChartInExistingPanel(jPanel3, homeSymptomChart, jLabel13, 8);
    }

    private javax.swing.JLabel addCardValue(javax.swing.JPanel card) {
        javax.swing.JLabel value = new javax.swing.JLabel("0", javax.swing.SwingConstants.CENTER);
        value.setFont(new Font("Yu Gothic UI", Font.BOLD, 28));
        value.setForeground(new Color(15, 23, 42));
        value.setOpaque(false);

        // Keep the generated title/layout untouched. The value is painted in
        // the card through the panel's existing child hierarchy.
        card.add(value, 0);
        card.setComponentZOrder(value, 0);
        positionCardValue(card, value);
        card.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                positionCardValue(card, value);
            }
        });
        return value;
    }

    private void positionCardValue(javax.swing.JPanel card, javax.swing.JLabel value) {
        int w = Math.max(80, card.getWidth() - 24);
        int h = 48;
        int y = Math.max(30, card.getHeight() / 2 - 12);
        value.setBounds(12, y, w, h);
    }

    private void installChartInExistingPanel(javax.swing.JPanel host,
            javax.swing.JComponent chart, javax.swing.JLabel title, int topPadding) {
        // Do not alter the host's existing background/border or preferred
        // size. The generated GroupLayout uses these panels' preferred sizes
        // when sizing the surrounding UI, so preserve that size before using
        // absolute child positions for the chart itself.
        java.awt.Dimension originalPreferredSize = host.getPreferredSize();
        host.setLayout(null);
        host.setPreferredSize(originalPreferredSize);
        title.setBounds(8, 8, Math.max(1, host.getWidth() - 16), Math.max(18, title.getHeight()));
        host.add(title);
        host.add(chart);
        chart.setBounds(8, Math.max(title.getY() + title.getHeight() + topPadding, 28),
                Math.max(20, host.getWidth() - 16),
                Math.max(20, host.getHeight() - title.getY() - title.getHeight() - topPadding - 8));
        host.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                title.setBounds(8, 8, Math.max(1, host.getWidth() - 16), title.getHeight());
                chart.setBounds(8, Math.max(title.getY() + title.getHeight() + topPadding, 28),
                        Math.max(20, host.getWidth() - 16),
                        Math.max(20, host.getHeight() - title.getY() - title.getHeight() - topPadding - 8));
            }
        });
    }

    private void setupStatisticCharts() {
        weeklyBarChart = new WeeklyBarChartPanel();
        healthConditionsPieChart = new HealthConditionsPieChartPanel();
        jLabel28.setText("Health Conditions & Allergies");
        installChartInExistingPanel(jPanel4, weeklyBarChart, jLabel22, 8);
        installChartInExistingPanel(jPanel14, healthConditionsPieChart, jLabel28, 8);
    }

    private void refreshStatistics() {
        DatabaseExecutor.run(() -> loadDashboardStatistics(), stats -> {
            if (homeTotalTodayValue != null) homeTotalTodayValue.setText(Integer.toString(stats.totalToday));
            if (homeActiveVisitsValue != null) homeActiveVisitsValue.setText(Integer.toString(stats.activeVisits));
            if (homeHighTempsValue != null) homeHighTempsValue.setText(Integer.toString(stats.highTemps));
            if (homeLowStockValue != null) homeLowStockValue.setText(Integer.toString(stats.lowStock));

            if (homeSymptomChart != null) homeSymptomChart.setData(stats.weeklySymptoms);
            if (weeklyBarChart != null) weeklyBarChart.setData(stats.weekdayCounts);
            if (healthConditionsPieChart != null) healthConditionsPieChart.setData(stats.healthConditionCounts);

            updateStatisticCards(stats);
            loadEmergencyIncidentLog();
        }, ex -> {
            // Statistics must never prevent the dashboard from opening.
            // Keep the existing UI values when the database is unavailable.
        });
    }

    private void setupStatisticCards() {
        statisticSpecialNeedsValue = createStatisticCardValue(jPanel5, jLabel6,
                "Number of Student with Special Needs");
        statisticAllergyValue = createStatisticCardValue(jPanel6, jLabel21,
                "Number of Student with Allergy");
        statisticMedicineValue = createStatisticCardValue(jPanel7, jLabel23,
                "Total Medicine Used this Month");
        statisticSentHomeValue = createStatisticCardValue(jPanel13, jLabel24,
                "Total of Student Sent home / Referred to Hospital");
    }

    private javax.swing.JLabel createStatisticCardValue(javax.swing.JPanel card,
            javax.swing.JLabel legacyLabel, String titleText) {
        // The form-generated title labels are only 15px tall. Replace their
        // visual role with independently positioned title/value labels so a
        // wrapped title cannot clip the metric in compact card sizes.
        legacyLabel.setVisible(false);
        card.setMinimumSize(new java.awt.Dimension(245, 112));
        card.setPreferredSize(new java.awt.Dimension(245, 112));
        javax.swing.JLabel value = new javax.swing.JLabel("0", javax.swing.SwingConstants.CENTER);
        value.setFont(new Font("Yu Gothic UI", Font.BOLD, 32));
        value.setOpaque(false);
        javax.swing.JLabel title = new javax.swing.JLabel("<html><div style='width:220px;'>"
                + escapeHtml(titleText) + "</div></html>", javax.swing.SwingConstants.LEFT);
        title.setFont(new Font("Yu Gothic UI", Font.BOLD, 12));
        title.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        title.setOpaque(false);
        java.awt.Color background = card.getBackground() == null
                ? UIManager.getColor("Panel.background") : card.getBackground();
        if (background == null) background = java.awt.Color.WHITE;
        int brightness = (background.getRed() * 299 + background.getGreen() * 587
                + background.getBlue() * 114) / 1000;
        java.awt.Color foreground = brightness < 145 ? java.awt.Color.WHITE : new java.awt.Color(31, 41, 55);
        title.setForeground(foreground);
        value.setForeground(foreground);
        card.setLayout(null);
        card.add(title);
        card.add(value);
        card.setComponentZOrder(title, 0);
        card.setComponentZOrder(value, 0);
        positionStatisticCardValue(card, title, value);
        card.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                positionStatisticCardValue(card, title, value);
            }
        });
        return value;
    }

    private void positionStatisticCardValue(javax.swing.JPanel card, javax.swing.JLabel title,
            javax.swing.JLabel value) {
        int width = Math.max(80, card.getWidth() - 24);
        title.setBounds(12, 10, width, 36);
        int valueY = 47;
        value.setBounds(12, valueY, width, Math.max(46, card.getHeight() - valueY - 10));
    }

    private void updateStatisticCards(DashboardStatistics stats) {
        setStatisticCardText(statisticSpecialNeedsValue, stats.specialNeeds);
        setStatisticCardText(statisticAllergyValue, stats.allergies);
        setStatisticCardText(statisticMedicineValue, stats.medicineUsedThisMonth);
        setStatisticCardText(statisticSentHomeValue, stats.sentHomeOrBack);
    }

    private void setStatisticCardText(javax.swing.JLabel label, int value) {
        if (label == null) {
            return;
        }
        label.setText(Integer.toString(value));
        label.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }

    private void configureIncidentLogTable() {
        jTable2.getColumnModel().getColumn(0).setHeaderValue("Date");
        jTable2.getTableHeader().repaint();
        javax.swing.table.DefaultTableCellRenderer left = new javax.swing.table.DefaultTableCellRenderer();
        left.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jTable2.getColumnModel().getColumn(0).setCellRenderer(left);
        jTable2.getColumnModel().getColumn(1).setCellRenderer(left);
        jTable2.getColumnModel().getColumn(2).setCellRenderer(left);
        jTable2.getColumnModel().getColumn(3).setCellRenderer(center);
    }

    private static DashboardStatistics loadDashboardStatistics() throws java.sql.SQLException {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate friday = monday.plusDays(4);

        int[] weekdays = new int[5];
        int totalToday = 0;
        int activeVisits = 0;
        int highTemps = 0;
        int lowStock = 0;
        int specialNeeds = 0;
        int allergies = 0;
        int medicineUsedThisMonth = 0;
        int sentHomeOrBack = 0;
        Map<String, Integer> symptoms = new LinkedHashMap<>();
        Map<String, Integer> healthConditions = new LinkedHashMap<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            // VISITS stores check_in_time as yyyy-MM-dd hh:mm a. Prefix matching
            // by ISO date is intentionally used because it is stable and indexed
            // enough for this small desktop application's data volume.
            String visitSql = "SELECT check_in_time, status, reason, meds_qty, med_used, temperature "
                    + "FROM VISITS WHERE archived = FALSE";
            try (PreparedStatement ps = conn.prepareStatement(visitSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String time = rs.getString("check_in_time");
                    LocalDate date = parseVisitDate(time);
                    String status = safeText(rs.getString("status"));
                    String reason = safeText(rs.getString("reason"));
                    String medicine = safeText(rs.getString("med_used"));
                    int medsQty = Math.max(0, rs.getInt("meds_qty"));
                    String temperature = safeText(rs.getString("temperature"));
                    try {
                        if (!temperature.isBlank() && Double.parseDouble(temperature) >= 38.0) highTemps++;
                    } catch (NumberFormatException ignored) { }

                    if (date != null) {
                        if (date.equals(today)) totalToday++;
                        if (date.isEqual(monday) || (date.isAfter(monday) && date.isBefore(friday.plusDays(1)))) {
                            if (!reason.isBlank()) {
                                String normalized = reason.trim();
                                symptoms.merge(normalized, 1, Integer::sum);
                            }
                        }
                        if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth()
                                && !medicine.isBlank() && !"none".equalsIgnoreCase(medicine)) {
                            medicineUsedThisMonth += medsQty;
                        }
                    }

                    if ("In Clinic".equalsIgnoreCase(status)) activeVisits++;
                    if ("Sent Home".equalsIgnoreCase(status) || "Sent Back".equalsIgnoreCase(status)) sentHomeOrBack++;
                }
            }

            // The weekly chart represents every check-in logged this work week,
            // including visits later archived from the active-visit table.
            try (PreparedStatement ps = conn.prepareStatement("SELECT check_in_time FROM VISITS");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = parseVisitDate(rs.getString("check_in_time"));
                    if (date != null && !date.isBefore(monday) && !date.isAfter(friday)) {
                        weekdays[date.getDayOfWeek().getValue() - 1]++;
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM STUDENTS WHERE status = 'ACTIVE' AND health_conditions IS NOT NULL "
                    + "AND TRIM(health_conditions) <> ''");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) specialNeeds = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM STUDENTS WHERE status = 'ACTIVE' AND allergy IS NOT NULL "
                    + "AND TRIM(allergy) <> ''");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) allergies = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT health_conditions, allergy FROM STUDENTS WHERE status = 'ACTIVE' "
                    + "AND ((health_conditions IS NOT NULL AND TRIM(health_conditions) <> '') "
                    + "OR (allergy IS NOT NULL AND TRIM(allergy) <> ''))");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    addHealthConditionCounts(healthConditions, rs.getString("health_conditions"));
                    addHealthConditionCounts(healthConditions, rs.getString("allergy"));
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT COUNT(*) FROM MEDICINES WHERE quantity <= 10");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lowStock = rs.getInt(1);
            }

        }

        return new DashboardStatistics(totalToday, activeVisits, highTemps, lowStock,
                specialNeeds, allergies, medicineUsedThisMonth, sentHomeOrBack,
                weekdays, symptoms, healthConditions);
    }

    /** Adds comma-separated health conditions/allergies as individual chart categories. */
    private static void addHealthConditionCounts(Map<String, Integer> counts, String rawValue) {
        if (rawValue == null || rawValue.isBlank()) return;
        for (String value : rawValue.split(",")) {
            String condition = value.trim();
            if (!condition.isEmpty()) counts.merge(condition, 1, Integer::sum);
        }
    }

    private static boolean columnExists(Connection conn, String table, String column)
            throws java.sql.SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, table.toUpperCase(Locale.ROOT),
                column.toUpperCase(Locale.ROOT))) {
            return rs.next();
        }
    }

    private static LocalDate parseVisitDate(String value) {
        if (value == null || value.isBlank()) return null;
        String datePart = value.length() >= 10 ? value.substring(0, 10) : value;
        try {
            return LocalDate.parse(datePart);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private void loadEmergencyIncidentLog() {
        DatabaseExecutor.run(() -> {
            java.util.List<Object[]> rows = new java.util.ArrayList<>();
            String sql = "SELECT check_in_time, name, reason, status FROM VISITS "
                    + "WHERE archived = FALSE AND (UPPER(status) = 'SENT HOME' OR UPPER(status) = 'SENT BACK') "
                    + "ORDER BY id DESC";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next() && rows.size() < 100) {
                    rows.add(new Object[]{rs.getString("check_in_time"), rs.getString("name"),
                        rs.getString("reason"), rs.getString("status")});
                }
            }
            return rows;
        }, rows -> {
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);
            for (Object[] row : rows) model.addRow(row);
        }, ex -> {
            // Keep the existing table intact on a transient database error.
        });
    }

    private record DashboardStatistics(
            int totalToday, int activeVisits, int highTemps, int lowStock,
            int specialNeeds, int allergies, int medicineUsedThisMonth, int sentHomeOrBack,
            int[] weekdayCounts, Map<String, Integer> weeklySymptoms,
            Map<String, Integer> healthConditionCounts) {}

    private abstract static class BaseChartPanel extends javax.swing.JPanel {
        protected static final Color TEXT = new Color(15, 23, 42);
        protected static final Color MUTED = new Color(100, 116, 139);
        protected static final Color GRID = new Color(226, 232, 240);
        protected static final Color BLUE = new Color(37, 99, 235);

        BaseChartPanel() {
            setOpaque(false);
        }

        protected void setupGraphics(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font("Yu Gothic UI", Font.PLAIN, 11));
        }

        protected void drawNoData(Graphics2D g) {
            g.setColor(MUTED);
            String text = "No data available";
            java.awt.FontMetrics fm = g.getFontMetrics();
            g.drawString(text, Math.max(0, (getWidth() - fm.stringWidth(text)) / 2),
                    Math.max(20, getHeight() / 2));
        }
    }

    private static final class WeeklyBarChartPanel extends BaseChartPanel {
        private int[] values = new int[5];

        void setData(int[] counts) {
            values = counts == null ? new int[5] : java.util.Arrays.copyOf(counts, 5);
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            setupGraphics(g);
            int left = 34, right = 12, top = 8, bottom = 28;
            int w = getWidth() - left - right, h = getHeight() - top - bottom;
            if (w <= 20 || h <= 20) { g.dispose(); return; }

            int max = 0;
            for (int v : values) max = Math.max(max, v);
            if (max == 0) { drawNoData(g); g.dispose(); return; }

            g.setColor(GRID);
            for (int i = 0; i <= 4; i++) {
                int y = top + h - (h * i / 4);
                g.drawLine(left, y, left + w, y);
            }
            g.setColor(TEXT);
            int barSpace = w / 5;
            for (int i = 0; i < 5; i++) {
                int barW = Math.max(18, barSpace / 2);
                int bh = (int) Math.round((values[i] / (double) max) * (h - 8));
                int x = left + i * barSpace + (barSpace - barW) / 2;
                int y = top + h - bh;
                g.setColor(BLUE);
                g.fillRoundRect(x, y, barW, bh, 8, 8);
                g.setColor(TEXT);
                String val = Integer.toString(values[i]);
                g.drawString(val, x + Math.max(0, (barW - g.getFontMetrics().stringWidth(val)) / 2), Math.max(top + 11, y - 4));
                String day = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"}[i];
                g.drawString(day, x + Math.max(0, (barW - g.getFontMetrics().stringWidth(day)) / 2), top + h + 18);
            }
            g.dispose();
        }
    }

    private static final class HealthConditionsPieChartPanel extends BaseChartPanel {
        private Map<String, Integer> values = new LinkedHashMap<>();

        void setData(Map<String, Integer> data) {
            values = data == null ? new LinkedHashMap<>() : new LinkedHashMap<>(data);
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            setupGraphics(g);
            int total = values.values().stream().mapToInt(Integer::intValue).sum();
            if (total <= 0) { drawNoData(g); g.dispose(); return; }

            // Center the complete pie-chart group within the existing panel
            // while keeping the current panel size, title, and legend style.
            int size = Math.min(getHeight() - 18, Math.min(getWidth() / 2, 210));
            int legendWidth = 190;
            int groupWidth = size + 20 + legendWidth;
            int groupX = Math.max(8, (getWidth() - groupWidth) / 2);
            int x = groupX, y = Math.max(6, (getHeight() - size) / 2);
            int start = 90;
            int remaining = 360;
            int[] sliceAngles = new int[values.size()];
            int idx = 0;
            for (int v : values.values()) {
                int angle = (idx == values.size() - 1) ? remaining : (int) Math.round(v * 360.0 / total);
                sliceAngles[idx++] = Math.max(0, angle);
                remaining -= angle;
            }

            idx = 0;
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                g.setColor(chartColor(idx));
                g.fillArc(x, y, size, size, start, sliceAngles[idx]);
                start += sliceAngles[idx++];
            }

            int legendX = x + size + 20;
            int legendY = Math.max(20, y + 18);
            idx = 0;
            for (Map.Entry<String, Integer> entry : values.entrySet()) {
                int value = Math.max(0, entry.getValue());
                g.setColor(chartColor(idx));
                g.fillRoundRect(legendX, legendY - 10, 10, 10, 3, 3);
                g.setColor(TEXT);
                int percentage = (int) Math.round(value * 100.0 / total);
                String text = entry.getKey() + " (" + value + ", " + percentage + "%)";
                g.drawString(text, legendX + 16, legendY);
                legendY += 24;
                idx++;
            }
            g.dispose();
        }

        private static Color chartColor(int index) {
            return switch (index % 3) {
                case 0 -> new Color(37, 99, 235);
                case 1 -> new Color(16, 185, 129);
                default -> new Color(245, 158, 11);
            };
        }
    }

    private static final class SymptomChartPanel extends BaseChartPanel {
        private Map<String, Integer> values = new LinkedHashMap<>();

        void setData(Map<String, Integer> data) {
            values = data == null ? new LinkedHashMap<>() : new LinkedHashMap<>(data);
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0.create();
            setupGraphics(g);
            if (values.isEmpty()) { drawNoData(g); g.dispose(); return; }

            // Show all categories, ordered by count, without hiding data.
            java.util.List<Map.Entry<String, Integer>> entries = new java.util.ArrayList<>(values.entrySet());
            entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
            int maxBars = Math.min(entries.size(), 8);
            int left = 8, right = 8, top = 6;
            int rowH = Math.max(18, (getHeight() - top - 8) / maxBars);
            int max = entries.stream().limit(maxBars).mapToInt(Map.Entry::getValue).max().orElse(1);
            for (int i = 0; i < maxBars; i++) {
                Map.Entry<String, Integer> e = entries.get(i);
                int y = top + i * rowH;
                String label = e.getKey();
                if (label.length() > 18) label = label.substring(0, 17) + "…";
                g.setColor(MUTED);
                g.drawString(label, left, y + 12);
                int bx = left + 105;
                int bw = Math.max(2, getWidth() - bx - right - 28);
                int bar = (int) Math.round(e.getValue() / (double) max * bw);
                g.setColor(BLUE);
                g.fillRoundRect(bx, y + 3, bar, Math.max(10, rowH - 8), 6, 6);
                g.setColor(TEXT);
                g.drawString(Integer.toString(e.getValue()), bx + bar + 6, y + 13);
            }
            g.dispose();
        }
    }

    private void configureClinicWorkflow() {
        installExpressActionButtons();
        jButton2.addActionListener(e -> findExpressStudent());
        LrnField.addActionListener(e -> findExpressStudent());
        ECheckin.addActionListener(e -> submitExpressCheckin());
        jButton1.addActionListener(e -> submitPopupCheckin());
        popupEmergencyBTN.addActionListener(e -> showEmergencyPrompt(selectedStudent));
        expressEmergencyBTN.addActionListener(e -> showEmergencyPrompt(selectedStudent));
        expressDispositionBTN.addActionListener(e -> sendExpressStudentHomeOrBack());
        archiveVisitBTN.addActionListener(e -> archiveSelectedRecentVisit());
        exportReportBTN.addActionListener(e -> exportRoleScopedReport());
        ReasonTable.setModel(createCheckInTableModel());
        ReasonTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ReasonTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectStudentFromChooser();
        });
        SearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { loadStudentChooser(SearchField.getText()); }
        });
        ReasonField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
            @Override public void removeUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
            @Override public void changedUpdate(DocumentEvent e) { recommendMedicineFromReason(); }
        });
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { recommendPopupMedicine(); }
            @Override public void removeUpdate(DocumentEvent e) { recommendPopupMedicine(); }
            @Override public void changedUpdate(DocumentEvent e) { recommendPopupMedicine(); }
        });
        configureCheckinPopupCard();
        loadStudentChooser("");
        loadMedicineChoices();
        refreshRecentVisits();
    }

    /** Keeps the Check-In view limited to the five student-identification fields. */
    private static DefaultTableModel createCheckInTableModel() {
        return new DefaultTableModel(new Object[]{"Name", "Grade & Section", "LRN",
            "Parent/Guardian Name", "Phone Number"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    /** Adds the express actions without relying on generated form coordinates. */
    private void installExpressActionButtons() {
        jPanel8.removeAll();
        jPanel8.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
        c.insets = new java.awt.Insets(4, 8, 4, 8);
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; jPanel8.add(jLabel14, c);
        c.gridx = 1; c.fill = java.awt.GridBagConstraints.HORIZONTAL; c.weightx = 1; jPanel8.add(LrnField, c);
        c.gridx = 2; c.weightx = 0; jPanel8.add(jButton2, c);
        c.gridx = 0; c.gridy++; c.fill = java.awt.GridBagConstraints.NONE; jPanel8.add(jLabel15, c);
        c.gridx = 1; c.gridwidth = 2; c.fill = java.awt.GridBagConstraints.HORIZONTAL; jPanel8.add(NameField, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 1; c.fill = java.awt.GridBagConstraints.NONE; jPanel8.add(jLabel16, c);
        c.gridx = 1; c.gridwidth = 2; c.fill = java.awt.GridBagConstraints.HORIZONTAL; jPanel8.add(ReasonField, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 1; c.fill = java.awt.GridBagConstraints.NONE; jPanel8.add(jLabel18, c);
        c.gridx = 1; c.gridwidth = 2; c.fill = java.awt.GridBagConstraints.HORIZONTAL; jPanel8.add(TempField, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 1; c.fill = java.awt.GridBagConstraints.NONE; jPanel8.add(jLabel17, c);
        c.gridx = 1; c.gridwidth = 2; c.fill = java.awt.GridBagConstraints.HORIZONTAL; jPanel8.add(MedicineField, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 1; c.fill = java.awt.GridBagConstraints.HORIZONTAL; jPanel8.add(ECheckin, c);
        c.gridx = 1; jPanel8.add(expressEmergencyBTN, c);
        c.gridx = 2; jPanel8.add(expressDispositionBTN, c);
        c.gridx = 0; c.gridy++; jPanel8.add(archiveVisitBTN, c);
        c.gridx = 1; c.gridwidth = 2; jPanel8.add(exportReportBTN, c);
        jPanel8.revalidate();
        jPanel8.repaint();
    }
    private class DimmingOverlay extends javax.swing.JPanel {
    public DimmingOverlay() {
        setOpaque(false);
        setVisible(false);
    }
    
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        java.awt.Graphics2D g2d = (java.awt.Graphics2D) g.create();
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        // Semi-transparent dark slate background to simulate a blur/dim effect
        g2d.setColor(new java.awt.Color(15, 23, 42, 120)); 
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}

    /** Replaces the generated fixed-width popup content with a readable card. */
    private void configureCheckinPopupCard() {
    CheckInPopup.removeAll();
    CheckInPopup.setBackground(new java.awt.Color(255, 255, 255));
    CheckInPopup.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240), 2),
            javax.swing.BorderFactory.createEmptyBorder(24, 28, 24, 28)));
    CheckInPopup.setLayout(new java.awt.GridBagLayout());

    StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 20));
    StudentCheckinLabel.setForeground(new java.awt.Color(15, 23, 42));
    StudentCheckinLabel.setText("Student Check-in");

    for (javax.swing.JLabel label : new javax.swing.JLabel[]{jLabel7, jLabel8,
             LRNLabel, jLabel9, jLabel29, jLabel11, jLabel30}) {
        label.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 13));
        label.setForeground(new java.awt.Color(71, 85, 105));
    }

    jLabel10.setText("Reason");
    jLabel12.setText("Medicine");
    jLabel10.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 12));
    jLabel12.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 12));
    jLabel10.setForeground(new java.awt.Color(51, 65, 85));
    jLabel12.setForeground(new java.awt.Color(51, 65, 85));

    jButton1.setText("Complete Check-in");
    jButton1.setBackground(new java.awt.Color(37, 99, 235));
    jButton1.setForeground(new java.awt.Color(255, 255, 255));
    jButton1.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 13));
    jButton1.setBorderPainted(false);
    jButton1.setFocusPainted(false);

    popupEmergencyBTN.setBackground(new java.awt.Color(185, 28, 28));
    popupEmergencyBTN.setForeground(java.awt.Color.WHITE);
    popupEmergencyBTN.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 13));
    popupEmergencyBTN.setBorderPainted(false);

    CancelCheckinBTN.setText("Cancel");
    CancelCheckinBTN.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 13));
    CancelCheckinBTN.setForeground(new java.awt.Color(100, 116, 139));
    CancelCheckinBTN.setBackground(new java.awt.Color(241, 245, 249));
    CancelCheckinBTN.setBorderPainted(false);
    CancelCheckinBTN.setFocusPainted(false);
    CancelCheckinBTN.addActionListener(e -> closeCheckinPopup());

    java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
    c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = java.awt.GridBagConstraints.WEST;
    c.fill = java.awt.GridBagConstraints.HORIZONTAL; c.weightx = 1; c.insets = new java.awt.Insets(0, 0, 16, 0);
    CheckInPopup.add(StudentCheckinLabel, c);

    int row = 1;
    for (javax.swing.JLabel label : new javax.swing.JLabel[]{jLabel7, jLabel8,
             LRNLabel, jLabel9, jLabel29, jLabel11}) {
        c.gridy = row++; c.insets = new java.awt.Insets(4, 0, 4, 0);
        CheckInPopup.add(label, c);
    }

    c.gridwidth = 1; c.weightx = 0; c.gridy = row; c.gridx = 0;
    c.insets = new java.awt.Insets(16, 0, 6, 12); CheckInPopup.add(jLabel10, c);
    c.gridx = 1; c.weightx = 1; c.fill = java.awt.GridBagConstraints.HORIZONTAL;
    c.insets = new java.awt.Insets(16, 0, 6, 0); CheckInPopup.add(jTextField1, c);

    c.gridy = ++row; c.gridx = 0; c.weightx = 0; c.insets = new java.awt.Insets(6, 0, 16, 12);
    CheckInPopup.add(jLabel12, c);
    c.gridx = 1; c.weightx = 1; c.insets = new java.awt.Insets(6, 0, 16, 0);
    CheckInPopup.add(jComboBox1, c);

    c.gridy = ++row; c.gridx = 0; c.weightx = 0; c.insets = new java.awt.Insets(6, 0, 16, 12);
    CheckInPopup.add(jLabel30, c);
    c.gridx = 1; c.weightx = 1; c.insets = new java.awt.Insets(6, 0, 16, 0);
    CheckInPopup.add(jTextField2, c);

    c.gridy = ++row; c.gridx = 0; c.gridwidth = 1; c.weightx = 0.5;
    c.insets = new java.awt.Insets(8, 0, 0, 8); CheckInPopup.add(CancelCheckinBTN, c);
    c.gridx = 1; c.weightx = 0.5; c.insets = new java.awt.Insets(8, 4, 0, 4);
    CheckInPopup.add(popupEmergencyBTN, c);
    c.gridx = 2; c.weightx = 0.5; c.insets = new java.awt.Insets(8, 8, 0, 0);
    CheckInPopup.add(jButton1, c);

    // Set bounds WITHOUT re-adding to content pane
    CheckInPopup.setBounds(500, 150, 540, 560);
    CheckInPopup.setVisible(false);
}


    /** Positions the panel in the center of the check-in workspace. */
   private void centerCheckinPopup() {
     int width = 540, height = 560;
    int panelWidth = CheckInPanel1.getWidth() > 0 ? CheckInPanel1.getWidth()
            : getContentPane().getWidth();
    int panelHeight = CheckInPanel1.getHeight() > 0 ? CheckInPanel1.getHeight()
            : getContentPane().getHeight();
    int panelX = CheckInPanel1.getWidth() > 0 ? CheckInPanel1.getX() : 0;
    int panelY = CheckInPanel1.getHeight() > 0 ? CheckInPanel1.getY() : 0;
    int x = panelX + Math.max(0, (panelWidth - width) / 2);
    int y = panelY + Math.max(0, (panelHeight - height) / 2);
    CheckInPopup.setBounds(x, y, width, height);
}
    private void showCheckinPopup() {
    centerCheckinPopup();

    // Force overlay to cover the entire frame, including the side panel
    dimmingOverlay.setBounds(0, 0, getContentPane().getWidth(), getContentPane().getHeight());

    // Z-order: 0 = topmost. Popup on top, overlay directly beneath it.
    getContentPane().setComponentZOrder(CheckInPopup, 0);
    getContentPane().setComponentZOrder(dimmingOverlay, 1);

    dimmingOverlay.setVisible(true);
    CheckInPopup.setVisible(true);
    CheckInPopup.requestFocusInWindow();

    getContentPane().revalidate();
    getContentPane().repaint();
}

    private void closeCheckinPopup() {
    CheckInPopup.setVisible(false);
    dimmingOverlay.setVisible(false);
    jTextField1.setText("");
    jTextField2.setText("");
    if (jComboBox1.getItemCount() > 0) jComboBox1.setSelectedIndex(0);
    selectedStudent = null;

    getContentPane().revalidate();
    getContentPane().repaint();
}

    /** Suggests an in-stock medicine based on the purpose saved in Inventory. */
    private void recommendMedicineFromReason() {
        if (!MedicineField.getText().trim().isEmpty()) return;
        final String reason = ReasonField.getText();
        if (reason == null || reason.isBlank()) return;

        DatabaseExecutor.run(() -> new MedicineData().loadAll(), medicines -> {
            if (!MedicineField.getText().trim().isEmpty()
                    || !reason.equals(ReasonField.getText())) return;
            String suggested = findMedicineForReason(reason, medicines);
            if (suggested != null) MedicineField.setText(suggested);
        }, ex -> {
            // Inventory lookup failure must not break the check-in form.
        });
    }

    private static String findMedicineForReason(String reason, java.util.List<Medicine> medicines) {
        String normalizedReason = normalizeForMatching(reason);
        if (normalizedReason.isBlank()) return null;

        // Prefer a medicine whose saved purpose directly matches the reason.
        for (Medicine medicine : medicines) {
            if (medicine.getquantity() <= 0 || medicine.isExpired()) continue;
            String purpose = normalizeForMatching(medicine.getPurpose());
            if (!purpose.isBlank() && matchesPurpose(normalizedReason, purpose)) {
                return medicine.getname();
            }
        }

        // Backward-compatible fallback for older inventory rows without a purpose.
        if (containsAny(normalizedReason, "fever", "lagnat", "may lagnat", "maylagnat",
                "nilalagnat", "mainit ang katawan", "mainit katawan")) {
            for (Medicine medicine : medicines) {
                if (medicine.getquantity() > 0 && !medicine.isExpired()
                        && medicine.getname() != null
                        && normalizeForMatching(medicine.getname()).contains("paracetamol")) {
                    return medicine.getname();
                }
            }
        }
        return null;
    }

    private static boolean matchesPurpose(String reason, String purpose) {
        String[] reasonWords = reason.split("\\s+");
        for (String word : reasonWords) {
            if (word.length() >= 4 && purpose.contains(word)) return true;
        }
        String[] purposeWords = purpose.split("\\s+");
        for (String word : purposeWords) {
            if (word.length() >= 4 && reason.contains(word)) return true;
        }
        return containsAny(reason, "fever", "lagnat", "cough", "ubo", "headache", "sakit ng ulo",
                "allergy", "pangati", "itch", "stomach", "tiyan", "wound", "sugat", "cold", "sipon")
                && containsAny(purpose, "fever", "lagnat", "cough", "ubo", "headache", "sakit ng ulo",
                "allergy", "pangati", "itch", "stomach", "tiyan", "wound", "sugat", "cold", "sipon");
    }

    private static String normalizeForMatching(String text) {
        if (text == null) return "";
        return text.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) if (text.contains(keyword)) return true;
        return false;
    }

    private void loadMedicineChoices() {
        DatabaseExecutor.run(() -> new MedicineData().loadAll(), medicines -> {
            javax.swing.DefaultComboBoxModel<String> model =
                    new javax.swing.DefaultComboBoxModel<>();
            model.addElement("None");
            for (Medicine medicine : medicines) model.addElement(medicine.getname());
            jComboBox1.setModel(model);
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load medicines: " + ex.getMessage(), "Medicine Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void recommendPopupMedicine() {
        String reason = jTextField1.getText() == null ? "" : jTextField1.getText().trim();
        if (reason.isBlank()) {
            if (jComboBox1.getItemCount() > 0) jComboBox1.setSelectedIndex(0);
            return;
        }
        final String requestedReason = reason;
        DatabaseExecutor.run(() -> new MedicineData().loadAll(), medicines -> {
            if (!requestedReason.equals(jTextField1.getText().trim())) return;
            String suggested = findMedicineForReason(requestedReason, medicines);
            if (suggested == null) return;
            for (int i = 0; i < jComboBox1.getItemCount(); i++) {
                if (suggested.equalsIgnoreCase(String.valueOf(jComboBox1.getItemAt(i)))) {
                    jComboBox1.setSelectedIndex(i);
                    return;
                }
            }
        }, ex -> {
            // Inventory lookup failure must not break check-in.
        });
    }

    private void selectStudentFromChooser() {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) return;
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        String lrn = String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
        findStudent(lrn, student -> {
            if (!lrn.equals(currentSelectedChooserLrn())) return;
            selectedStudent = student;
            if (student != null) populatePopupStudent(student);
        });
    }

    private String currentSelectedChooserLrn() {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) return null;
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        return String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
    }

    private void openCheckinForSelection() {
        int selectedRow = ReasonTable.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Select a student from the table first.",
                "Student Required", JOptionPane.WARNING_MESSAGE);
        return;
    }
    int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
    String lrn = String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
    
    findStudent(lrn, student -> {
        if (!lrn.equals(currentSelectedChooserLrn())) return;
        if (student == null) {
            JOptionPane.showMessageDialog(this, "The selected student is no longer active.",
                    "Student Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        selectedStudent = student;
        populatePopupStudent(student);
        showCheckinPopup(); 
    });
    }

    private void populatePopupStudent(StudentDetails student) {
        jLabel7.setText(detail("Name", student.name()));
        jLabel8.setText(detail("Grade & Section", student.gradeSection()));
        LRNLabel.setText(detail("LRN", student.lrn()));
        jLabel9.setText(detail("Allergies", blankAsNone(student.allergy())));
        jLabel29.setText(detail("Health Conditions", blankAsNone(student.healthConditions())));
        jLabel11.setText(detail("Guardian / Contact", blankAsNone(student.parentName())
                + " · " + blankAsNone(student.phoneNumber())));
    }

    private static String detail(String label, String value) {
        return "<html><b>" + escapeHtml(label) + ":</b> " + escapeHtml(value) + "</html>";
    }

    private static String escapeHtml(String value) {
        return value == null ? "" : value.replace("&", "&amp;")
                .replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String blankAsNone(String value) {
        return value == null || value.isBlank() ? "None" : value;
    }

    private void findExpressStudent() {
        String lrn = LrnField.getText().trim();
        if (lrn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a student LRN first.",
                    "LRN Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        findStudent(lrn, student -> {
            if (student == null) {
                selectedStudent = null;
                jLabel15.setText("Name:");
                JOptionPane.showMessageDialog(this, "No active student was found for that LRN.",
                        "Student Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedStudent = student;
            jLabel15.setText("Name: " + student.name());
        });
    }

    private void submitExpressCheckin() {
        if (selectedStudent == null || !selectedStudent.lrn().equals(LrnField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Search and select a valid student before checking in.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String temperature = validateTemperature(TempField.getText().trim());
        if (temperature == null) return;
        submitCheckin(selectedStudent, ReasonField.getText().trim(), MedicineField.getText().trim(), temperature);
    }

    private void submitPopupCheckin() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Select a student before proceeding.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object item = jComboBox1.getSelectedItem();
        String medicine = item == null ? "None" : item.toString();
        String temperature = validateTemperature(jTextField2.getText().trim());
        if (temperature == null) return;
        submitCheckin(selectedStudent, jTextField1.getText().trim(), medicine, temperature);
    }

    /** Collects the category and narrative required for an emergency payload. */
    private void showEmergencyPrompt(StudentDetails student) {
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Search and select a student first.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        javax.swing.JComboBox<EmergencyType> category = new javax.swing.JComboBox<>(EmergencyType.values());
        javax.swing.JTextArea details = new javax.swing.JTextArea(5, 28);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        Object[] message = {"Emergency category / severity:", category,
                "Specific incident details:", new javax.swing.JScrollPane(details)};
        if (JOptionPane.showConfirmDialog(this, message, "Emergency Check-in",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            EmergencyRecord emergency = new EmergencyRecord(student.lrn(),
                    (EmergencyType) category.getSelectedItem(), details.getText(), java.time.LocalDateTime.now());
            submitEmergencyCheckin(student, emergency);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Emergency Details Required",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void submitEmergencyCheckin(StudentDetails student, EmergencyRecord emergency) {
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        ECheckin.setEnabled(false);
        jButton1.setEnabled(false);
        popupEmergencyBTN.setEnabled(false);
        expressEmergencyBTN.setEnabled(false);
        DatabaseExecutor.run(() -> new VisitData().checkInEmergency(student.name(), student.gradeSection(),
                emergency, student.parentName(), student.phoneNumber(), actor), result -> {
            ECheckin.setEnabled(true); jButton1.setEnabled(true);
            popupEmergencyBTN.setEnabled(true); expressEmergencyBTN.setEnabled(true);
            refreshRecentVisits(); refreshStatistics(); closeCheckinPopup();
            ReasonField.setText(""); MedicineField.setText(""); TempField.setText("");
            JOptionPane.showMessageDialog(this, "Emergency check-in recorded for " + student.name() + ".",
                    "Emergency Recorded", JOptionPane.WARNING_MESSAGE);
        }, ex -> {
            ECheckin.setEnabled(true); jButton1.setEnabled(true);
            popupEmergencyBTN.setEnabled(true); expressEmergencyBTN.setEnabled(true);
            String message = "23505".equals(ex instanceof java.sql.SQLException sql ? sql.getSQLState() : null)
                    ? student.name() + " is already checked in." : "Unable to record the emergency: " + ex.getMessage();
            JOptionPane.showMessageDialog(this, message, "Emergency Check-in Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void sendExpressStudentHomeOrBack() {
        if (selectedStudent == null || !selectedStudent.lrn().equals(LrnField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Search and select a student first.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object[] choices = {"Send Home", "Send Back to Class", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Choose an action for " + selectedStudent.name() + ".",
                "Clinic Visit Action", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, choices, choices[0]);
        if (choice != 0 && choice != 1) return;
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        expressDispositionBTN.setEnabled(false);
        StudentDetails student = selectedStudent;
        DatabaseExecutor.run(() -> choice == 0 ? new VisitData().markSentHome(student.lrn(), actor)
                        : new VisitData().markSentBack(student.lrn(), actor), updated -> {
            expressDispositionBTN.setEnabled(true);
            if (!updated) {
                JOptionPane.showMessageDialog(this, "This student does not have an active clinic visit.",
                        "Visit Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshRecentVisits(); refreshStatistics();
        }, ex -> { expressDispositionBTN.setEnabled(true); JOptionPane.showMessageDialog(this,
                "Unable to update the visit: " + ex.getMessage(), "Visit Action Error", JOptionPane.ERROR_MESSAGE); });
    }

    private void submitCheckin(StudentDetails student, String reason, String medicine) {
        submitCheckin(student, reason, medicine, "");
    }

    private void submitCheckin(StudentDetails student, String reason, String medicine, String temperature) {
        if (reason == null || reason.isBlank()) {
            JOptionPane.showMessageDialog(this, "Enter a reason for the clinic visit.",
                    "Reason Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String medicineName = medicine == null || medicine.isBlank() ? "None" : medicine;
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();

        DatabaseExecutor.run(() -> new VisitData().isCurrentlyCheckedIn(student.lrn()), alreadyCheckedIn -> {
            if (alreadyCheckedIn) {
                JOptionPane.showMessageDialog(this,
                        student.name() + " is already checked in.",
                        "Already Checked In", JOptionPane.WARNING_MESSAGE);
                return;
            }
            performCheckinSave(student, reason, medicineName, temperature, actor);
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to verify the student's current clinic status: " + ex.getMessage(),
                "Check-in Error", JOptionPane.ERROR_MESSAGE));
    }

    private void performCheckinSave(StudentDetails student, String reason, String medicineName,
            String temperature, String actor) {
        ECheckin.setEnabled(false);
        jButton1.setEnabled(false);
        DatabaseExecutor.run(() -> new VisitData().checkInWithMedicine(
                student.name(), student.gradeSection(), student.lrn(), reason, medicineName,
                "None".equalsIgnoreCase(medicineName) ? 0 : 1,
                student.parentName(), student.phoneNumber(), temperature, new MedicineData(), actor), result -> {
            ECheckin.setEnabled(true);
            jButton1.setEnabled(true);
            refreshRecentVisits();
            refreshStatistics();
            closeCheckinPopup();
            ReasonField.setText("");
            MedicineField.setText("");
            TempField.setText("");
            JOptionPane.showMessageDialog(this,
                    result.medicineDeducted() || "None".equalsIgnoreCase(medicineName)
                            ? "Student checked in successfully."
                            : "Student checked in, but the selected medicine was unavailable.",
                    "Check-in Complete", JOptionPane.INFORMATION_MESSAGE);
        }, ex -> {
            ECheckin.setEnabled(true);
            jButton1.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to save the check-in: " + ex.getMessage(),
                    "Check-in Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    /** Validates clinic temperature without changing the existing field/UI. */
    private String validateTemperature(String raw) {
        if (raw == null || raw.isBlank()) return "";
        try {
            double value = Double.parseDouble(raw);
            if (!Double.isFinite(value) || value < 34.0 || value > 45.0) {
                JOptionPane.showMessageDialog(this,
                        "Enter a valid temperature between 34.0 and 45.0 °C.",
                        "Invalid Temperature", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return String.format(Locale.ROOT, "%.1f", value);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid temperature, for example 37.5.",
                    "Invalid Temperature", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void loadStudentChooser(String search) {
        final String requestedSearch = search == null ? "" : search.trim();
        final int requestId = ++studentSearchGeneration;
        DatabaseExecutor.run(() -> findStudents(requestedSearch), students -> {
            if (requestId != studentSearchGeneration
                    || !requestedSearch.equals(SearchField.getText().trim())) return;
            DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
            model.setRowCount(0);
            for (StudentDetails student : students) {
                model.addRow(new Object[]{student.name(), student.gradeSection(), student.lrn(),
                        student.parentName(), student.phoneNumber()});
            }
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load students: " + ex.getMessage(), "Student Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    private void refreshRecentVisits() {
        DatabaseExecutor.run(() -> new VisitData().loadActive(), visits -> {
            recentVisits = java.util.List.copyOf(visits);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            for (CheckinSystem visit : visits) {
                model.addRow(new Object[]{visit.getCheckInTime(), visit.getName(),
                        visit.getGradeSection(), displayTemperature(visit.getTemperature()), visit.getReason(), visit.getMedUsed(),
                        visit.getStatus()});
            }
        }, ex -> JOptionPane.showMessageDialog(this,
                "Unable to load recent clinic visits: " + ex.getMessage(), "Visit Load Error",
                JOptionPane.ERROR_MESSAGE));
    }

    /** Uses a clear clinical-log label for visits without a recorded temperature. */
    private static String displayTemperature(String temperature) {
        return temperature == null || temperature.isBlank() ? "Not Recorded" : temperature.trim();
    }

    /** Archives the one recent visit selected in the single-selection table. */
    private void archiveSelectedRecentVisit() {
        int row = jTable1.getSelectedRow();
        if (row < 0 || row >= recentVisits.size()) {
            JOptionPane.showMessageDialog(this, "Select one recent clinic visit to archive.",
                    "Visit Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CheckinSystem visit = recentVisits.get(row);
        if (JOptionPane.showConfirmDialog(this, "Archive the visit for " + visit.getName()
                + "? It will be removed from the active/recent list but retained in reports.",
                "Archive Visit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                != JOptionPane.YES_OPTION) return;
        archiveVisitBTN.setEnabled(false);
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        DatabaseExecutor.run(() -> new VisitData().archiveVisit(visit.getLrn(), visit.getCheckInTime(), actor), archived -> {
            archiveVisitBTN.setEnabled(true);
            if (!archived) {
                JOptionPane.showMessageDialog(this, "The selected visit was already archived or changed.",
                        "Archive Unavailable", JOptionPane.WARNING_MESSAGE);
                return;
            }
            refreshRecentVisits();
            refreshStatistics();
        }, ex -> {
            archiveVisitBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to archive the visit: " + ex.getMessage(),
                    "Archive Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void exportRoleScopedReport() {
        LocalDate today = LocalDate.now();
        exportReportBTN.setEnabled(false);
        // Query the persisted check-in log rather than the visible table: a visit
        // archived earlier today is still a valid historical check-in for export.
        DatabaseExecutor.run(() -> new ReportExporter(new MedicineData()).hasCheckinRecordsForDate(today),
                hasCheckins -> {
            exportReportBTN.setEnabled(true);
            if (!hasCheckins) {
                JOptionPane.showMessageDialog(this,
                        "There are no students checked in today. Cannot generate a report.",
                        "Export Report", JOptionPane.WARNING_MESSAGE);
                return;
            }
            chooseAndExportRoleScopedReport(today);
        }, ex -> {
            exportReportBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to verify today's check-ins: " + ex.getMessage(),
                    "Export Report", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void chooseAndExportRoleScopedReport(LocalDate today) {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Export Clinic Report");
        if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        java.io.File destination = chooser.getSelectedFile();
        if (!destination.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
            destination = new java.io.File(destination.getAbsolutePath() + ".xlsx");
        }
        AccountSystem sessionAccount = UserSession.getCurrentUser();
        boolean fullAdminReport = sessionAccount != null && sessionAccount.isAdmin();
        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        java.io.File reportFile = destination;
        exportReportBTN.setEnabled(false);
        DatabaseExecutor.run(() -> new ReportExporter(new MedicineData()).writeReport(
                today, reportFile, actor, fullAdminReport), auditLogged -> {
            exportReportBTN.setEnabled(true);
            String scope = fullAdminReport ? "full clinic report" : "check-in logs report";
            JOptionPane.showMessageDialog(this, "Exported " + scope + " to:\n"
                    + reportFile.getAbsolutePath() + (auditLogged ? "" : "\nThe export audit entry could not be saved."),
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        }, ex -> {
            exportReportBTN.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Unable to export report: " + ex.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    private void findStudent(String lrn, java.util.function.Consumer<StudentDetails> onSuccess) {
        DatabaseExecutor.run(() -> findStudentByLrn(lrn), onSuccess, ex ->
                JOptionPane.showMessageDialog(this, "Unable to find student: " + ex.getMessage(),
                        "Student Search Error", JOptionPane.ERROR_MESSAGE));
    }

    private static StudentDetails findStudentByLrn(String lrn) throws java.sql.SQLException {
        String sql = "SELECT name, grade_section, lrn, allergy, health_conditions, parent_name, phone_number "
                + "FROM STUDENTS WHERE lrn = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lrn);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? studentFromResult(rs) : null;
            }
        }
    }

    /** Loads every active registered student for the Check-In chooser. */
    private static java.util.ArrayList<StudentDetails> findStudents(String search) throws java.sql.SQLException {
        java.util.ArrayList<StudentDetails> students = new java.util.ArrayList<>();
        String sql =
                "SELECT s.name, s.grade_section, s.lrn, s.allergy, s.health_conditions, "
                + "s.parent_name, s.phone_number "
                + "FROM STUDENTS s "
                + "WHERE s.status = 'ACTIVE' "
                + "AND (UPPER(s.name) LIKE ? OR UPPER(s.grade_section) LIKE ? OR UPPER(s.lrn) LIKE ?) "
                + "ORDER BY s.name, s.lrn";
        String filter = "%" + (search == null ? "" : search.trim().toUpperCase()) + "%";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, filter);
            ps.setString(2, filter);
            ps.setString(3, filter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) students.add(studentFromResult(rs));
            }
        }
        return students;
    }

    private static StudentDetails studentFromResult(ResultSet rs) throws java.sql.SQLException {
        return new StudentDetails(rs.getString("name"), rs.getString("grade_section"),
                rs.getString("lrn"), rs.getString("allergy"), rs.getString("health_conditions"),
                rs.getString("parent_name"), rs.getString("phone_number"));
    }

    private record StudentDetails(String name, String gradeSection, String lrn, String allergy,
            String healthConditions, String parentName, String phoneNumber) {
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do 
NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        SidePanel = new javax.swing.JPanel();
        HomeBTN = new javax.swing.JButton();
        CheckinBTN = new javax.swing.JButton();
        StatisticBTN = new javax.swing.JButton();
        InventoryBTN = new javax.swing.JButton();
        Logout = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        CheckInPopup = new javax.swing.JPanel();
        StudentCheckinLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        LRNLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        CheckInPanel1 = new javax.swing.JPanel();
        CheckInBTN = new javax.swing.JButton();
        EmergencyBTN = new javax.swing.JButton();
        SentHomeBTN = new javax.swing.JButton();
        SearchField = new javax.swing.JTextField();
        SearchLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        StatisticPanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel27 = new javax.swing.JLabel();
        MainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        ReasonField = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        LrnField = new javax.swing.JTextField();
        ECheckin = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        MedicineField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        TempField = new javax.swing.JTextField();
        NameField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        DateTimeLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SidePanel.setBackground(new java.awt.Color(51, 153, 255));

        HomeBTN.setText("Home");

        CheckinBTN.setText("Check in");

        StatisticBTN.setText("Statistic");
        StatisticBTN.addActionListener(this::StatisticBTNActionPerformed);

        InventoryBTN.setText("Inventory and Management");
        InventoryBTN.addActionListener(this::InventoryBTNActionPerformed);

        Logout.setBackground(new java.awt.Color(255, 255, 255));
        Logout.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        Logout.setForeground(new java.awt.Color(0, 0, 0));
        Logout.setText("Logout");
        Logout.addActionListener(this::LogoutActionPerformed);

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Clinic Dashboard");

        javax.swing.GroupLayout SidePanelLayout = new javax.swing.GroupLayout(SidePanel);
        SidePanel.setLayout(SidePanelLayout);
        SidePanelLayout.setHorizontalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CheckinBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addComponent(StatisticBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addComponent(InventoryBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .addComponent(Logout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SidePanelLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addComponent(jLabel3)
                .addGap(138, 138, 138)
                .addComponent(HomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(CheckinBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StatisticBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InventoryBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 276, Short.MAX_VALUE)
                .addComponent(Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(SidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, -3, 270, 840));

        CheckInPopup.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPopup.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        StudentCheckinLabel.setForeground(new java.awt.Color(0, 0, 0));
        StudentCheckinLabel.setText("Student Check-in");

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Name:");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Grade/Section:");

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        LRNLabel.setForeground(new java.awt.Color(0, 0, 0));
        LRNLabel.setText("LRN:");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Allergy:");

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Reason");

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setText("Medicine");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setBackground(new java.awt.Color(0, 153, 204));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Proceed");

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Status:");

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setText("Health Condition:");

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setText("Temperature");

        javax.swing.GroupLayout CheckInPopupLayout = new javax.swing.GroupLayout(CheckInPopup);
        CheckInPopup.setLayout(CheckInPopupLayout);
        CheckInPopupLayout.setHorizontalGroup(
            CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPopupLayout.createSequentialGroup()
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CheckInPopupLayout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addComponent(StudentCheckinLabel))
                    .addGroup(CheckInPopupLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CheckInPopupLayout.createSequentialGroup()
                                .addGap(127, 127, 127)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel7)
                            .addGroup(CheckInPopupLayout.createSequentialGroup()
                                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel30))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, 274, Short.MAX_VALUE)
                                    .addComponent(jTextField1)
                                    .addComponent(jTextField2)))
                            .addComponent(jLabel8)
                            .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel29)
                            .addComponent(jLabel11))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        CheckInPopupLayout.setVerticalGroup(
            CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPopupLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(26, 26, 26)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(27, 27, 27)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        getContentPane().add(CheckInPopup, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 240, 420, 410));

        CheckInPanel1.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        CheckInBTN.setBackground(new java.awt.Color(0, 102, 204));
        CheckInBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        CheckInBTN.setForeground(new java.awt.Color(255, 255, 255));
        CheckInBTN.setText("Check In");
        CheckInBTN.addActionListener(this::CheckInBTNActionPerformed);

        EmergencyBTN.setBackground(new java.awt.Color(185, 28, 28));
        EmergencyBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        EmergencyBTN.setForeground(new java.awt.Color(255, 255, 255));
        EmergencyBTN.setText("Emergency");
        EmergencyBTN.addActionListener(this::EmergencyBTNActionPerformed);

        SentHomeBTN.setBackground(new java.awt.Color(0, 102, 204));
        SentHomeBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        SentHomeBTN.setForeground(new java.awt.Color(255, 255, 255));
        SentHomeBTN.setText("Sent Home/Back");
        SentHomeBTN.addActionListener(this::SentHomeBTNActionPerformed);

        SearchLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        SearchLabel.setForeground(new java.awt.Color(0, 0, 0));
        SearchLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchLabel.setText("Search:");

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Name", "Grade & Section", "LRN", "Parent/Guardian Name", "Phone number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ReasonTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ReasonTableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(ReasonTable);
        if (ReasonTable.getColumnModel().getColumnCount() > 0) {
            ReasonTable.getColumnModel().getColumn(0).setResizable(false);
            ReasonTable.getColumnModel().getColumn(1).setResizable(false);
            ReasonTable.getColumnModel().getColumn(2).setResizable(false);
            ReasonTable.getColumnModel().getColumn(3).setResizable(false);
            ReasonTable.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout CheckInPanel1Layout = new javax.swing.GroupLayout(CheckInPanel1);
        CheckInPanel1.setLayout(CheckInPanel1Layout);
        CheckInPanel1Layout.setHorizontalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CheckInPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addGap(456, 456, 456)
                .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(EmergencyBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(465, Short.MAX_VALUE))
        );
        CheckInPanel1Layout.setVerticalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 615, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(CheckInBTN, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(EmergencyBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SentHomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(CheckInPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1490, 840));

        StatisticPanel.setBackground(new java.awt.Color(255, 255, 255));
        StatisticPanel.setForeground(new java.awt.Color(0, 0, 0));

        jLabel22.setText("Weekly Bar Graph");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(319, 319, 319)
                .addComponent(jLabel22)
                .addContainerGap(387, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jLabel22)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Number of Student with Special Needs ");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Number of Student with Allergy");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Total Medicine Used this Month");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel24.setText("Total of Student Sent home/ Referred to Hospital");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(191, Short.MAX_VALUE))
        );

        jLabel28.setText("Pie Chart");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(322, 322, 322)
                .addComponent(jLabel28)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jLabel28)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        jLabel25.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setText("STATISTICS & HEALTH MONITORS");

        jLabel26.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setText("CHRONIC HEALTH CONDITIONS");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Data", "Name", "Symptom", "Action"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable2);
        if (jTable2.getColumnModel().getColumnCount() > 0) {
            jTable2.getColumnModel().getColumn(0).setResizable(false);
            jTable2.getColumnModel().getColumn(1).setResizable(false);
            jTable2.getColumnModel().getColumn(2).setResizable(false);
            jTable2.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 642, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
        );

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setText("EMERGENCY & REFERRAL INCIDENT LOG");

        javax.swing.GroupLayout StatisticPanelLayout = new javax.swing.GroupLayout(StatisticPanel);
        StatisticPanel.setLayout(StatisticPanelLayout);
        StatisticPanelLayout.setHorizontalGroup(
            StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addGroup(StatisticPanelLayout.createSequentialGroup()
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel26))
                        .addGap(18, 18, 18)
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(StatisticPanelLayout.createSequentialGroup()
                                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel27))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        StatisticPanelLayout.setVerticalGroup(
            StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(StatisticPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25)
                .addGap(34, 34, 34)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(StatisticPanelLayout.createSequentialGroup()
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(27, 27, 27)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(StatisticPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(42, 42, 42))
        );

        getContentPane().add(StatisticPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1490, 840));

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Time", "Name", "Section", "Temp", "Symptoms", "Medicine", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setResizable(false);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
        );

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("SYMPTOM DISTRIBUTION (THIS WEEK)");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(111, 111, 111)
                .addComponent(jLabel13)
                .addContainerGap(171, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(180, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(151, 151, 151))
        );

        jPanel8.setBackground(new java.awt.Color(248, 247, 247));

        jButton2.setText("Search");

        jLabel14.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setText("Student LRN:");

        jLabel15.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setText("Name:");

        jLabel16.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setText("Reason:");

        ECheckin.setText("Check in");

        jLabel17.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setText("Medicine:");

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setText("Temperature:");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel16)
                                    .addComponent(jLabel17))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ReasonField, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                    .addComponent(MedicineField)
                                    .addComponent(TempField)
                                    .addComponent(NameField, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(LrnField)))
                            .addComponent(jLabel18))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(ECheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(jButton2)))))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(LrnField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(NameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(ReasonField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(TempField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(MedicineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ECheckin))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setText("Express Check in");

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setText("Recent Clinic Visit and Action");

        jLabel2.setText("High Temps");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(74, 74, 74))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel5.setText("Low Stock");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addComponent(jLabel5)
                .addContainerGap(84, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        jLabel1.setText("Active Visits");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jLabel4.setText("Total Today");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addComponent(jLabel4)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        DateTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        DateTimeLabel.setForeground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        getContentPane().add(MainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, -4, 1490, 840));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void InventoryBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InventoryBTNActionPerformed
        if (!requireAdminPanelAccess()) return;
        SessionManager.saveSession(loggedInAccount);
        new AdminPanel(loggedInAccount).setVisible(true);
        dispose();
    }//GEN-LAST:event_InventoryBTNActionPerformed
    private void CheckInBTNActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = ReasonTable.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, 
            "Please select a student from the table first.",
            "Student Required", JOptionPane.WARNING_MESSAGE);
        return;
    }
        openCheckinForSelection();
    }

    private void EmergencyBTNActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first.",
                    "Emergency Check-In", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = ReasonTable.convertRowIndexToModel(selectedRow);
        String lrn = String.valueOf(ReasonTable.getModel().getValueAt(modelRow, 2));
        findStudent(lrn, student -> {
            if (!lrn.equals(currentSelectedChooserLrn())) return;
            if (student == null) {
                JOptionPane.showMessageDialog(this, "The selected student is no longer active.",
                        "Student Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            selectedStudent = student;
            showEmergencyPromptForActiveVisit(student);
        });
    }

    private void showEmergencyPromptForActiveVisit(StudentDetails student) {
        javax.swing.JComboBox<EmergencyType> category = new javax.swing.JComboBox<>(EmergencyType.values());
        javax.swing.JTextArea details = new javax.swing.JTextArea(5, 28);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        Object[] message = {"Emergency category / severity:", category,
                "Specific incident details:", new javax.swing.JScrollPane(details)};
        if (JOptionPane.showConfirmDialog(this, message, "Emergency Check-in",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            EmergencyRecord emergency = new EmergencyRecord(student.lrn(),
                    (EmergencyType) category.getSelectedItem(), details.getText(), java.time.LocalDateTime.now());
            String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
            EmergencyBTN.setEnabled(false);
            DatabaseExecutor.run(() -> new VisitData().recordEmergencyForActiveVisit(emergency, actor), updated -> {
                EmergencyBTN.setEnabled(true);
                if (!updated) {
                    JOptionPane.showMessageDialog(this, "The selected student no longer has an active clinic visit.",
                            "Emergency Check-In", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                refreshRecentVisits();
                refreshStatistics();
                JOptionPane.showMessageDialog(this, "Emergency record added for " + student.name() + ".",
                        "Emergency Recorded", JOptionPane.WARNING_MESSAGE);
            }, ex -> {
                EmergencyBTN.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Unable to record the emergency: " + ex.getMessage(),
                        "Emergency Check-In Error", JOptionPane.ERROR_MESSAGE);
            });
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Emergency Details Required",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void LogoutActionPerformed(java.awt.event.ActionEvent evt) {
        if (loggedInAccount != null) {
            try {
                ActivityLogData.log("LOGOUT", "User signed out.", loggedInAccount.GetName());
            } catch (java.sql.SQLException ignored) {
                // Logging failure must not trap a user in the application.
            }
        }
        SessionManager.clearSession();
        UserSession.clear();
        new LoginUi().setVisible(true);
        dispose();
    }

/** Updates the navigation buttons to show which panel is currently active. */
private void updateActiveButton(javax.swing.JButton btn) {
    activeNavButton = btn; // Track the active button
    
    java.awt.Color sidePanelBg = new java.awt.Color(15, 23, 42);
    java.awt.Color inactiveText = new java.awt.Color(148, 163, 184);
    java.awt.Color blueLine = new java.awt.Color(59, 130, 246);

    javax.swing.JButton[] navButtons = {HomeBTN, CheckinBTN, StatisticBTN, InventoryBTN};
    
    for (javax.swing.JButton b : navButtons) {
        if (b == btn) {
            // ACTIVE STATE: White text, Bold, Blue Line
            b.setForeground(java.awt.Color.WHITE);
            b.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.BOLD, 14));
            b.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createMatteBorder(0, 4, 0, 0, blueLine),
                javax.swing.BorderFactory.createEmptyBorder(12, 20, 12, 24)
            ));
        } else {
            // INACTIVE STATE: Muted text, Plain, No line
            b.setForeground(inactiveText);
            b.setFont(new java.awt.Font("Yu Gothic UI", java.awt.Font.PLAIN, 14));
            b.setBorder(javax.swing.BorderFactory.createEmptyBorder(12, 24, 12, 24));
        }
    }
}

    private void SentHomeBTNActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = ReasonTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a checked-in student first.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String lrn = currentSelectedChooserLrn();
        if (lrn == null || lrn.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "The selected student could not be identified.",
                    "Student Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object[] options = {"Sent Home", "Sent Back", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "What should happen to the selected student?",
                "Visit Action",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice != 0 && choice != 1) return;

        String actor = loggedInAccount == null ? "Unknown" : loggedInAccount.GetName();
        DatabaseExecutor.run(
                () -> choice == 0
                        ? new VisitData().markSentHome(lrn, actor)
                        : new VisitData().markSentBack(lrn, actor),
                updated -> {
                    if (!updated) {
                        JOptionPane.showMessageDialog(this,
                                "The selected student no longer has an active clinic visit.",
                                "Visit Not Found", JOptionPane.WARNING_MESSAGE);
                        loadStudentChooser(SearchField.getText().trim());
                        return;
                    }
                    selectedStudent = null;
                    loadStudentChooser(SearchField.getText().trim());
                    refreshRecentVisits();
                    refreshStatistics();
                },
                ex -> JOptionPane.showMessageDialog(this,
                        "Unable to update the clinic visit: " + ex.getMessage(),
                        "Visit Update Error", JOptionPane.ERROR_MESSAGE));
    }

    private void StatisticBTNActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(StatisticPanel);
    }

    private void ThemeToggleActionPerformed(java.awt.event.ActionEvent evt) {
        // Legacy action removed: its original controls no longer exist in the generated form.
    }

    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {
        selectStudentFromChooser();
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CheckInBTN;
    private javax.swing.JPanel CheckInPanel1;
    private javax.swing.JPanel CheckInPopup;
    private javax.swing.JButton EmergencyBTN;
    private javax.swing.JButton CheckinBTN;
    private javax.swing.JLabel DateTimeLabel;
    private javax.swing.JButton ECheckin;
    private javax.swing.JButton HomeBTN;
    private javax.swing.JButton InventoryBTN;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JButton Logout;
    private javax.swing.JTextField LrnField;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JTextField MedicineField;
    private javax.swing.JTextField NameField;
    private javax.swing.JTextField ReasonField;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel SearchLabel;
    private javax.swing.JButton SentHomeBTN;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JButton StatisticBTN;
    private javax.swing.JPanel StatisticPanel;
    private javax.swing.JLabel StudentCheckinLabel;
    private javax.swing.JTextField TempField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
