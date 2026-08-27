/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;
import net.miginfocom.swing.MigLayout;
import java.awt.CardLayout;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author PC
 */
public class AdminPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminPanel.class.getName());
    private final MedicineData  productService = new MedicineData ("inventory_activity.log");
    private ArrayList<Medicine> currentProducts = new ArrayList<>();
    private String selectedProductName = null;
    private AccountSystem loggedInAccount;

    // Runtime-only responsive containers. NetBeans Designer remains the source of the
    // actual components and initComponents() is intentionally left generated.
    private final JPanel responsiveContent = new JPanel(new CardLayout());
    private JPanel inventoryView;
    private CardLayout contentCards;

    /**
     * Creates the administrator inventory panel.
     */
   public AdminPanel(AccountSystem account) {
    if (account == null || !account.canAccessAdminPanel()) {
        throw new SecurityException("Access denied. Administrator role required.");
    }

    com.formdev.flatlaf.FlatLightLaf.setup();
    initComponents();

    // ---- CRITICAL: free jPanel1 from NetBeans' absolute prison ----
    jPanel1.setLayout(new BorderLayout());
    jPanel1.setPreferredSize(new java.awt.Dimension(1500, 820));
    // -----------------------------------------------------------------

    setResizable(true);                    // was false — kills responsiveness
    setMinimumSize(new java.awt.Dimension(1500, 820));
    setIconImage(AppIcon.getIcon());

    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosing(java.awt.event.WindowEvent e) {
            SessionManager.saveSession(account);
        }
    });

    configureFlatLafUi();
    this.loggedInAccount = account;
    configureAccountManagementUi();
    installResponsiveLayout();
    ((AbstractDocument) ExpDate.getDocument()).setDocumentFilter(new DateInputFilter());
    setLocationRelativeTo(null);
    refreshInventoryScreen();
    refreshActivityLogDisplay();
    refreshInventoryTable();
    loadStatistics();
    refreshAccountTable();
}

    /** Applies the FlatLaf treatment after NetBeans creates the form controls. */
   private void configureFlatLafUi() {
    Color page = Color.decode("#F8FAFC");
    Color surface = Color.WHITE;
    Color border = Color.decode("#E2E8F0");
    Color primary = Color.decode("#2563EB");
    Color sidebar = Color.decode("#0F172A");

    getContentPane().setBackground(page);
    setTitle("Clinic · Admin Panel");
    jPanel1.setBackground(page);
    createHeader(primary);
    jPanel3.setBackground(sidebar);
    jPanel3.setBorder(BorderFactory.createEmptyBorder());

    styleCard(jPanel4, surface, border);
    styleCard(jPanel5, surface, border);
    styleCard(jPanel6, surface, border);
    jPanel5.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border), BorderFactory.createEmptyBorder(18, 18, 18, 18)));
    
    // --- Statistics Cards Styling ---
// 1. Apply the white card styling to all statistics panels
javax.swing.JPanel[] statCards = {
    cardWeeklyCheckIns, jPanel10, jPanel11, 
    CommonReasonPanel, FrequentlyUsedPanel, jPanel7
};
for (javax.swing.JPanel card : statCards) {
    styleCard(card, surface, border);
}

// 2. Standardize the subtitle fonts and colors (e.g., "Common Reason")
java.awt.Color textSecondary = Color.decode("#475569");
javax.swing.JLabel[] statTitles = {
    lblWeeklyTitle, lblInClinicTitle, lblSentHomeTitle, 
    CommonReasonTitle, FrequentlyUsedTitle, jLabel11
};
for (javax.swing.JLabel lbl : statTitles) {
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lbl.setForeground(textSecondary);
    lbl.setHorizontalAlignment(SwingConstants.CENTER);
}

// 3. Standardize the big number/value fonts and colors
javax.swing.JLabel[] statValues = {
    lblWeeklyCheckInsValue, lblInClinicValue, lblSentHomeValue, 
    CommonReasonLabel, FrequentlyUsedLabel
};
for (javax.swing.JLabel lbl : statValues) {
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
    lbl.setForeground(primary); // Uses your modern blue (#2563EB)
    lbl.setHorizontalAlignment(SwingConstants.CENTER);
}
    
// 4. Make the daily check-in count labels larger (the "0 Check-ins" text)
javax.swing.JLabel[] dayCountLabels = {
    lblMondayCount, lblTuesdayCount, lblWednesdayCount, 
    lblThursdayCount, lblFridayCount
};
for (javax.swing.JLabel lbl : dayCountLabels) {
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Increased from 11 to 16
    lbl.setForeground(Color.decode("#64748B"));
    lbl.setHorizontalAlignment(SwingConstants.CENTER);
}
    
    
    
    

    jLabel6.setText("Stock overview");
    jLabel1.setText("Inventory Logs");
    jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 22));
    jLabel2.setText("Manage stock");
    jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 16));

    // Sidebar buttons — dark theme
    styleSidebarButton(jButton5, "Inventory", true);      // starts active
    styleSidebarButton(jButton6, "Statistics", false);
    styleSidebarButton(AccManageBTN, "Account Management", false);

    // Back button — muted at bottom
    jButton7.setText("← Back");
    jButton7.setForeground(Color.decode("#94A3B8"));
    jButton7.setBackground(Color.decode("#0F172A"));
    jButton7.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    jButton7.putClientProperty(FlatClientProperties.STYLE, null);
    jButton7.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    jButton7.setFocusPainted(false);

    // Hover effect
    jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            jButton7.setBackground(Color.decode("#DC2626"));
            jButton7.setForeground(Color.WHITE);
        }
        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            jButton7.setBackground(Color.decode("#0F172A"));
            jButton7.setForeground(Color.decode("#94A3B8"));
        }
    });

    configureSidebarHover(jButton5);
    configureSidebarHover(jButton6);
    configureSidebarHover(AccManageBTN);
    AccManageBTN.addActionListener(event -> showAccountManagement());
    
    stylePrimaryButton(ExportBTN, "Export DB", Color.decode("#7C3AED"));
    stylePrimaryButton(AddBTN, "Add item", primary);
    styleSecondaryButton(EditBtn, "Edit");
    styleDangerButton(DeleteBTN, "Delete");
    styleSecondaryButton(ClearBtn, "Clear form");

    for (JComponent field : new JComponent[]{ProductName, ExpDate, Qty}) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                field == ProductName ? "e.g. Paracetamol" : field == ExpDate ? "YYYY-MM-DD" : "0");
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,10,6,10");
    }

    stockTable.setRowHeight(38);
    stockTable.setShowVerticalLines(false);
    stockTable.setShowHorizontalLines(true);
    stockTable.setGridColor(border);
    stockTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "background: #F1F5F9; foreground: #475569; font: +1");
    jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
    jScrollPane2.setBorder(BorderFactory.createEmptyBorder());
    InventoryLogs.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Recent stock activity will appear here.");
    InventoryLogs.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,0,0");
    InventoryLogs.setText("No recent activity\n\nChanges to stock levels will be recorded here.");
    InventoryLogs.setEditable(false);
    InventoryLogs.setForeground(Color.decode("#64748B"));
    InventoryLogs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    InventoryLogs.setMargin(new java.awt.Insets(18, 18, 18, 18));
    InventoryLogs.setBackground(surface);
     // Wrap long lines instead of letting them run off the edge
 InventoryLogs.setLineWrap(true);
 InventoryLogs.setWrapStyleWord(true);
stockTable.setToolTipText("Your available medical supplies");

    // Navigation is handled by the NetBeans-generated action methods.
}
    private void styleSidebarButton(JButton btn, String text, boolean active) {
    btn.setText(text);
    btn.setFocusPainted(false);
    btn.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 13));
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setOpaque(true);
    btn.setContentAreaFilled(true);
    btn.putClientProperty("sidebar.active", active);

    if (active) {
        // Disable so the current page can't be re-clicked,
        // and override FlatLaf's disabled colors to keep the dark active look.
        btn.setEnabled(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE,
                "disabledBackground: #1E293B; disabledText: #F8FAFC;");
        btn.setBackground(Color.decode("#1E293B"));
        btn.setForeground(Color.decode("#F8FAFC"));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, Color.decode("#2563EB")),
                BorderFactory.createEmptyBorder(10, 13, 10, 16)
        ));
    } else {
        // Inactive buttons stay enabled, clickable, and keep the hover effect
        btn.setEnabled(true);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn.putClientProperty(FlatClientProperties.STYLE, null);
        btn.setBackground(Color.decode("#0F172A"));
        btn.setForeground(Color.decode("#94A3B8"));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }
}

/** Adds the hover treatment once; the selected navigation item keeps its active color. */
private void configureSidebarHover(JButton btn) {
    btn.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent event) {
            if (!Boolean.TRUE.equals(btn.getClientProperty("sidebar.active"))) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.decode("#0F172A"));
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent event) {
            if (!Boolean.TRUE.equals(btn.getClientProperty("sidebar.active"))) {
                btn.setBackground(Color.decode("#0F172A"));
                btn.setForeground(Color.decode("#94A3B8"));
            }
        }
    });
}

    private void showToastNotification(String actionText, String itemName, int count, Color backgroundColor) {
        String countText = (count > 1) ? count + " products" : "1 product";
        String fullMessage = (count > 1) 
                ? actionText + " " + countText + " (" + itemName + " + " + (count - 1) + " more)"
                : actionText + " " + itemName;

        JLabel toast = new JLabel(fullMessage, SwingConstants.CENTER);
        toast.setOpaque(true);
        toast.setBackground(backgroundColor);
        toast.setForeground(Color.WHITE);
        toast.setFont(new Font("Segoe UI", Font.BOLD, 13));

        toast.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        toast.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        int toastWidth = Math.max(350, toast.getPreferredSize().width + 40);
        int toastHeight = 40;
        int targetX = (this.getWidth() - toastWidth) / 2;
        int startY = this.getHeight();
        int targetY = this.getHeight() - 80;

        toast.setBounds(targetX, startY, toastWidth, toastHeight);

        javax.swing.JLayeredPane layeredPane = this.getLayeredPane();
        layeredPane.add(toast, javax.swing.JLayeredPane.DRAG_LAYER);
        layeredPane.revalidate();

        javax.swing.Timer slideUp = new javax.swing.Timer(10, null);
        slideUp.addActionListener(new java.awt.event.ActionListener() {
            int currentY = startY;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (currentY > targetY) {
                    currentY -= 4;
                    toast.setLocation(targetX, currentY);
                    layeredPane.repaint();
                } else {
                    slideUp.stop();

                    javax.swing.Timer delay = new javax.swing.Timer(3000, evt -> {
                        javax.swing.Timer slideDown = new javax.swing.Timer(10, null);
                        slideDown.addActionListener(new java.awt.event.ActionListener() {
                            int returnY = targetY;

                            @Override
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                if (returnY < startY) {
                                    returnY += 4;
                                    toast.setLocation(targetX, returnY);
                                    layeredPane.repaint();
                                } else {
                                    slideDown.stop();
                                    layeredPane.remove(toast);
                                    layeredPane.repaint();
                                }
                            }
                        });
                        slideDown.start();
                    });
                    delay.setRepeats(false);
                    delay.start();
                }
            }
        });
        slideUp.start();
    }

    private void showStatistics() {
        loadStatistics();
        if (contentCards != null) {
            contentCards.show(responsiveContent, "statistics");
        }
        styleSidebarButton(jButton5, "Inventory", false);
        styleSidebarButton(jButton6, "Statistics", true);
        styleSidebarButton(AccManageBTN, "Account Management", false);
    }

    /** Applies the same FlatLaf card/typography treatment to the account panel. */
private void configureAccountManagementUi() {
    Color page   = Color.decode("#F8FAFC");
    Color border = Color.decode("#E2E8F0");
    Color primary = Color.decode("#2563EB");
    Color textSecondary = Color.decode("#475569");

    // Panel background
    AccountManagementPanel.setBackground(page);

    // ---------- Table (match stockTable) ----------
    ACTTable.setRowHeight(38);
    ACTTable.setShowVerticalLines(false);
    ACTTable.setShowHorizontalLines(true);
    ACTTable.setGridColor(border);
    ACTTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    ACTTable.setForeground(textSecondary);
    ACTTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "background: #F1F5F9; foreground: #475569; font: +1");
    jScrollPane3.setBorder(BorderFactory.createEmptyBorder());

    // ---------- Form fields ----------
    AccNameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "e.g. nurse_Teban");
    AccNameField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,10,6,10");
    AccNameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    AccNameField.setText("");                 // remove "jTextField1"

    AccPasswordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
    AccPasswordField.putClientProperty(FlatClientProperties.STYLE,
            "arc: 10; margin: 6,10,6,10; showRevealButton: true");
    AccPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    AccPasswordField.setText("");             // remove "jPasswordField1"

    ConfirmPasswordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "••••••••");
    ConfirmPasswordField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,10,6,10");
    ConfirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    ConfirmPasswordField.setText("");         // remove "jPasswordField1"

    // ---------- Labels ----------
    for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{
            AccountNameLabel, AccountPasswordLabel, ConfirmPasswordLabel}) {
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(textSecondary);
    }

    // ---------- Buttons ----------
    stylePrimaryButton(CAdminBTN, "Create Admin", primary);
    stylePrimaryButton(CUserBTN,  "Create User",  primary);
    styleDangerButton(AccDeleteBTN, "Delete");
    
    // Only Head Admin can create Admin accounts.
        CAdminBTN.setVisible(
            loggedInAccount != null
            && loggedInAccount.isHeadAdmin()
        );

        // Both Head Admin and Admin can create Users.
        CUserBTN.setVisible(
            loggedInAccount != null
            && loggedInAccount.isAdmin()
        );

        // Delete button is available to administrators.
        // The actual permission is checked again in AccountData.
        AccDeleteBTN.setVisible(
            loggedInAccount != null
            && loggedInAccount.isAdmin()
        );
}
private void showAccountManagement() {
    if (contentCards != null) {
        contentCards.show(responsiveContent, "accounts");
    }
    refreshAccountTable();
    styleSidebarButton(jButton5, "Inventory", false);
    styleSidebarButton(jButton6, "Statistics", false);
    styleSidebarButton(AccManageBTN, "Account Management", true);
}

private void showInventory() {
    if (contentCards != null) {
        contentCards.show(responsiveContent, "inventory");
    }
    styleSidebarButton(jButton5, "Inventory", true);
    styleSidebarButton(jButton6, "Statistics", false);
    styleSidebarButton(AccManageBTN, "Account Management", false);
}

/**
 * Installs responsive runtime containers around the components created by NetBeans Designer.
 * initComponents() itself remains generated and editable in the NetBeans GUI Builder.
 */
private void installResponsiveLayout() {
    contentCards = (CardLayout) responsiveContent.getLayout();
    
    // CRITICAL: Clear Designer-imposed sizes so MigLayout can fill the space
    statisticsContainer.setPreferredSize(null);
    statisticsContainer.setMinimumSize(null);
    AccountManagementPanel.setPreferredSize(null);
    AccountManagementPanel.setMinimumSize(null);
    
    // Match the page background
    responsiveContent.setBackground(Color.decode("#F8FAFC"));
    
    buildInventoryView();
    buildStatisticsLayout();
    buildAccountManagementLayout();
    
    jPanel1.removeAll();
    jPanel1.setLayout(new MigLayout("fill, insets 0, gap 0", 
        "[180] [grow]",   // Col 0: Fixed 180px sidebar | Col 1: Grows to fill rest
        "[30] [grow]"     // Row 0: Fixed 30px header | Row 1: Grows to fill rest
    ));
    
    jPanel1.add(jPanel2, "cell 0 0 2 1, growx");
    jPanel1.add(jPanel3, "cell 0 1, growy");
    jPanel1.add(responsiveContent, "cell 1 1, grow, push");
    
    responsiveContent.add(inventoryView, "inventory");
    responsiveContent.add(statisticsContainer, "statistics");
    responsiveContent.add(AccountManagementPanel, "accounts");
    
    contentCards.show(responsiveContent, "inventory");
    responsiveContent.revalidate();
    jPanel1.revalidate();
    jPanel1.repaint();
}

private void buildInventoryView() {
    inventoryView = new JPanel(new MigLayout(
        "fill, insets 20, gap 12",
        "[grow, fill] [320, grow, fill]", // Left side grows, right side min 320 but can grow
        "[pref] [grow, fill] [pref]"      // Row 1 (main table area) grows vertically
    ));
    inventoryView.setBackground(Color.decode("#F8FAFC"));
    
    // Titles
    inventoryView.add(jLabel6, "cell 0 0, growx");
    inventoryView.add(jLabel1, "cell 1 0, growx");
    
    // Stock Table (Left side, spans 2 rows vertically)
    inventoryView.add(jPanel4, "cell 0 1 1 2, grow, push");
    
    // Logs and Form (Right side, stacked)
    inventoryView.add(jPanel6, "cell 1 1, grow, push");
    inventoryView.add(jPanel5, "cell 1 2, growx"); // Form keeps preferred height but fills width
}

private void buildStatisticsLayout() {
    statisticsContainer.removeAll();
    statisticsContainer.setLayout(new MigLayout(
        "fill, insets 20, gap 15",
        "[grow] [grow] [grow] [grow]", // 4 columns
        "[pref] [pref] [grow, fill] [grow, fill] [pref]" 
    ));
    
    statisticsContainer.add(jLabel7, "cell 0 0 4 1, growx");
    statisticsContainer.add(lblReportingPeriod, "cell 0 1 4 1, growx");
    
    // First 3 cards span 2 rows vertically
    statisticsContainer.add(cardWeeklyCheckIns, "cell 0 2, spany 2, grow, push");
    statisticsContainer.add(jPanel10, "cell 1 2, spany 2, grow, push");
    statisticsContainer.add(jPanel11, "cell 2 2, spany 2, grow, push");
    
    // 4th column split into two rows
    statisticsContainer.add(FrequentlyUsedPanel, "cell 3 2, grow, push");
    statisticsContainer.add(CommonReasonPanel, "cell 3 3, grow, push");
    
    // Daily Check-ins container
    jPanel7.removeAll();
    jPanel7.setLayout(new MigLayout(
        "fill, insets 14, gap 10",
        "[grow] [grow] [grow] [grow] [grow]", 
        "[pref] [160px!]" // Fixed height row to match the new card size
    ));
    jPanel7.add(jLabel11, "cell 0 0 5 1, growx");
    
    configureDayCard(jPanel8, lblModayDay, lblMondayDate, lblMondayCount);
    configureDayCard(jPanel9, lblTuesdayDay, lblTuesdayDate, lblTuesdayCount);
    configureDayCard(jPanel12, lblWednesdayDay, lblWednesdayDate, lblWednesdayCount);
    configureDayCard(jPanel13, lblThursdayDay, lblThursdayDate, lblThursdayCount);
    configureDayCard(jPanel14, lblFridayDay, lblFridayDate, lblFridayCount);
    
    // INCREASED SIZE: Changed from 120px to 160px for larger squares
    jPanel7.add(jPanel8, "cell 0 1, w 160px!, h 160px!, center");
    jPanel7.add(jPanel9, "cell 1 1, w 160px!, h 160px!, center");
    jPanel7.add(jPanel12, "cell 2 1, w 160px!, h 160px!, center");
    jPanel7.add(jPanel13, "cell 3 1, w 160px!, h 160px!, center");
    jPanel7.add(jPanel14, "cell 4 1, w 160px!, h 160px!, center");
    
    statisticsContainer.add(jPanel7, "cell 0 4 4 1, growx");
}
private void configureDayCard(JPanel card, JLabel day, JLabel date, JLabel count) {
    card.removeAll();
    card.setLayout(new MigLayout(
        "fill, insets 12, gap 6", // Slightly more padding
        "[grow]",
        "[pref] [pref] [push, grow]" 
    ));
    
    day.setHorizontalAlignment(SwingConstants.CENTER);
    date.setHorizontalAlignment(SwingConstants.CENTER);
    count.setHorizontalAlignment(SwingConstants.CENTER);
    
    // Make the date number slightly larger to fill the new space better
    date.setFont(new Font("Segoe UI", Font.BOLD, 24)); 
    
    card.add(day, "growx, wrap");
    card.add(date, "growx, wrap");
    card.add(count, "growx, aligny bottom");
}
private void buildAccountManagementLayout() {
    AccountManagementPanel.removeAll();
    AccountManagementPanel.setLayout(new MigLayout(
        "fill, insets 24, gap 20",
        "[320, grow] [grow, fill]", // Left form min 320px, right table grows
        "[grow, fill] [pref]"       // Row 0: main content grows, Row 1: button row
    ));
    
    // Form panel on left, aligned to top
    AccountManagementPanel.add(createAccountFormPanel(), "cell 0 0, growy, aligny top");
    
    // Table on right, fills all available space
    AccountManagementPanel.add(jScrollPane3, "cell 1 0, grow, push");
    
    // Delete button below table, right-aligned
    AccountManagementPanel.add(AccDeleteBTN, "cell 1 1, east, w 140!, gaptop 16");
}

private JPanel createAccountFormPanel() {
    JPanel form = new JPanel(new MigLayout(
        "fillx, insets 0, gap 12",
        "[grow]",
        "[pref] [36px] [pref] [36px] [pref] [36px] [pref] [40px]"
    ));
    form.setOpaque(false);
    
    form.add(AccountNameLabel, "growx, wrap");
    form.add(AccNameField, "growx, wrap, h 36px!");
    
    form.add(AccountPasswordLabel, "growx, wrap, gaptop 8");
    form.add(AccPasswordField, "growx, wrap, h 36px!");
    
    form.add(ConfirmPasswordLabel, "growx, wrap, gaptop 8");
    form.add(ConfirmPasswordField, "growx, wrap, h 36px!");
    
    // Buttons row with gap above
    form.add(CUserBTN, "split 2, growx, gaptop 16");
    form.add(CAdminBTN, "growx");
    
    return form;
}

    private void createHeader(Color primary) {
        jPanel2.removeAll();
        jPanel2.setBackground(primary);
        jPanel2.setLayout(new BorderLayout(18, 0));

        JPanel identity = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 0));
        identity.setOpaque(false);
        JLabel name = new JLabel("CLINIC ");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JLabel section = new JLabel("ADMIN PANEL");
        section.setForeground(Color.decode("#DBEAFE"));
        section.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        identity.add(name);
        identity.add(section);

        jPanel2.add(identity, BorderLayout.WEST);
        jPanel2.revalidate();
        jPanel2.repaint();
    }

    private void styleCard(JComponent component, Color background, Color border) {
        component.setBackground(background);
        component.setBorder(BorderFactory.createLineBorder(border));
        component.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
    }
    private void stylePrimaryButton(JButton button, String text, Color color) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; font: bold; margin: 7,12,7,12");
    }

    private void styleSecondaryButton(JButton button, String text) {
        button.setText(text);
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #F1F5F9; foreground: #334155; borderColor: #E2E8F0; font: bold");
    }

    private void styleDangerButton(JButton button, String text) {
        button.setText(text);
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #FEF2F2; foreground: #DC2626; borderColor: #FECACA; font: bold");
    }
    
    private String normalizeDate(String input) throws DateTimeParseException {
        DateTimeFormatter looseFormat = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate date = LocalDate.parse(input, looseFormat); 
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);  
    }

    private void refreshActivityLogDisplay() {
        try {
            ArrayList<String> log = productService.loadActivityLog();
            StringBuilder sb = new StringBuilder();

            if (log.isEmpty()) {
                sb.append("No recent activity.\n\nChanges to stock levels will be recorded here.");
            } else {
                // Newest first, with a blank line between entries so each log
                // line reads as its own block instead of a cramped wall of text.
                for (int i = log.size() - 1; i >= 0; i--) {
                    sb.append(log.get(i)).append("\n\n");
                }
            }

            InventoryLogs.setText(sb.toString());
            InventoryLogs.setCaretPosition(0); // always open scrolled to the newest entry
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading activity log: " + ex.getMessage());
        }
    }
    
    private void refreshInventoryTable() {
        try {
            
            currentProducts = productService.loadAll();
            
            DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
            model.setRowCount(0);

            for (Medicine p : currentProducts) {
                model.addRow(new Object[]{
                    p.getStatus(),
                    p.getname(),
                    p.getquantity()
                });
            }
            refreshActivityLogDisplay();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
    
    private void refreshInventoryScreen() {
        try {
            currentProducts = productService.loadAll();
            DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
            model.setRowCount(0);

            for (Medicine p : currentProducts) {
                model.addRow(new Object[]{p.getStatus(), p.getname(), p.getquantity()});
            }
            refreshActivityLogDisplay();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage());
        }  
    }
    
  private void loadStatistics() {
        try {
            
            ArrayList<CheckinSystem> visits = new VisitData().loadAll();

            int weeklyCheckins = 0;
            int inClinic = 0;
            int sentHome = 0;

            int monday = 0;
            int tuesday = 0;
            int wednesday = 0;
            int thursday = 0;
            int friday = 0;

            Map<String, Integer> reasonCount = new HashMap<>();
            Map<String, Integer> medicineCount = new HashMap<>();

            // Get the current week's Monday and Friday
            LocalDate today = LocalDate.now();
            LocalDate mondayOfWeek = today.with(DayOfWeek.MONDAY);
            LocalDate fridayOfWeek = today.with(DayOfWeek.FRIDAY);

            DateTimeFormatter formatter =
                     DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");


            lblMondayDate.setText(
                    String.valueOf(mondayOfWeek.getDayOfMonth())
            );

            lblTuesdayDate.setText(
                    String.valueOf(mondayOfWeek.plusDays(1).getDayOfMonth())
            );

            lblWednesdayDate.setText(
                    String.valueOf(mondayOfWeek.plusDays(2).getDayOfMonth())
            );

            lblThursdayDate.setText(
                    String.valueOf(mondayOfWeek.plusDays(3).getDayOfMonth())
            );

            lblFridayDate.setText(
                    String.valueOf(mondayOfWeek.plusDays(4).getDayOfMonth())
            );


            for (CheckinSystem v : visits) {
                
                LocalDate visitDate;

                try {
                    visitDate = LocalDateTime
                            .parse(v.getCheckInTime(), formatter)
                            .toLocalDate();

                } catch (DateTimeParseException ex) {

                    continue;
                }

                if (!visitDate.isBefore(mondayOfWeek)
                        && !visitDate.isAfter(fridayOfWeek)) {

                    weeklyCheckins++;

                    switch (visitDate.getDayOfWeek()) {

                        case MONDAY:
                            monday++;
                            break;

                        case TUESDAY:
                            tuesday++;
                            break;

                        case WEDNESDAY:
                            wednesday++;
                            break;

                        case THURSDAY:
                            thursday++;
                            break;

                        case FRIDAY:
                            friday++;
                            break;

                        default:
                            break;
                    }
                }


                if ("In Clinic".equalsIgnoreCase(v.getStatus())) {
                    inClinic++;
                }

                if ("Sent Home".equalsIgnoreCase(v.getStatus())) {
                    sentHome++;
                }


                if (v.getReason() != null && !v.getReason().isBlank()) {
                    reasonCount.put(
                            v.getReason(),
                            reasonCount.getOrDefault(v.getReason(), 0) + 1
                    );
                }


                if (v.getMedUsed() != null && !v.getMedUsed().isBlank()) {
                    medicineCount.put(
                            v.getMedUsed(),
                            medicineCount.getOrDefault(v.getMedUsed(), 0) + 1
                    );
                }
            }
            // --- Calculate Most Common Reason ---
String topReason = "N/A";
int maxReasonCount = 0;
for (Map.Entry<String, Integer> entry : reasonCount.entrySet()) {
    if (entry.getValue() > maxReasonCount) {
        maxReasonCount = entry.getValue();
        topReason = entry.getKey();
    }
}

// --- Calculate Most Frequently Used Medicine ---
String topMedicine = "N/A";
int maxMedCount = 0;
for (Map.Entry<String, Integer> entry : medicineCount.entrySet()) {
    if (entry.getValue() > maxMedCount) {
        maxMedCount = entry.getValue();
        topMedicine = entry.getKey();
    }
}

// Clean up "None" or empty medicine entries so it looks professional
if (topMedicine.equalsIgnoreCase("None") || topMedicine.trim().isEmpty()) {
    topMedicine = "No medicine used";
}

// --- Update the UI Labels ---
CommonReasonLabel.setText(topReason);
FrequentlyUsedLabel.setText(topMedicine);

            lblWeeklyCheckInsValue.setText(
                    String.valueOf(weeklyCheckins)
            );

            lblInClinicValue.setText(
                    String.valueOf(inClinic)
            );

            lblSentHomeValue.setText(
                    String.valueOf(sentHome)
            );

            lblMondayCount.setText(
                    String.valueOf(monday)
            );

            lblTuesdayCount.setText(
                    String.valueOf(tuesday)
            );

            lblWednesdayCount.setText(
                    String.valueOf(wednesday)
            );

            lblThursdayCount.setText(
                    String.valueOf(thursday)
            );

            lblFridayCount.setText(
                    String.valueOf(friday)
            );

            DateTimeFormatter displayFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");

            lblReportingPeriod.setText(
                    "Reporting period: "
                    + mondayOfWeek.format(displayFormatter)
                    + " to "
                    + fridayOfWeek.format(displayFormatter)
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading statistics: " + ex.getMessage(),
                    "Statistics Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        AccManageBTN = new javax.swing.JButton();
        ExportBTN = new javax.swing.JButton();
        AccountManagementPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ACTTable = new javax.swing.JTable();
        AccNameField = new javax.swing.JTextField();
        AccPasswordField = new javax.swing.JPasswordField();
        ConfirmPasswordField = new javax.swing.JPasswordField();
        AccountNameLabel = new javax.swing.JLabel();
        AccountPasswordLabel = new javax.swing.JLabel();
        ConfirmPasswordLabel = new javax.swing.JLabel();
        CAdminBTN = new javax.swing.JButton();
        CUserBTN = new javax.swing.JButton();
        AccDeleteBTN = new javax.swing.JButton();
        statisticsContainer = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        lblReportingPeriod = new javax.swing.JLabel();
        cardWeeklyCheckIns = new javax.swing.JPanel();
        lblWeeklyTitle = new javax.swing.JLabel();
        lblWeeklyCheckInsValue = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        lblInClinicTitle = new javax.swing.JLabel();
        lblInClinicValue = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        lblSentHomeTitle = new javax.swing.JLabel();
        lblSentHomeValue = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        CommonReasonPanel = new javax.swing.JPanel();
        CommonReasonTitle = new javax.swing.JLabel();
        CommonReasonLabel = new javax.swing.JLabel();
        FrequentlyUsedPanel = new javax.swing.JPanel();
        FrequentlyUsedTitle = new javax.swing.JLabel();
        FrequentlyUsedLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        lblModayDay = new javax.swing.JLabel();
        lblMondayDate = new javax.swing.JLabel();
        lblMondayCount = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        lblTuesdayDay = new javax.swing.JLabel();
        lblTuesdayCount = new javax.swing.JLabel();
        lblTuesdayDate = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lblWednesdayDate = new javax.swing.JLabel();
        lblWednesdayDay = new javax.swing.JLabel();
        lblWednesdayCount = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        lblThursdayDate = new javax.swing.JLabel();
        lblThursdayCount = new javax.swing.JLabel();
        lblThursdayDay = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        lblFridayDate = new javax.swing.JLabel();
        lblFridayCount = new javax.swing.JLabel();
        lblFridayDay = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        ProductName = new javax.swing.JTextField();
        ExpDate = new javax.swing.JTextField();
        Qty = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        AddBTN = new javax.swing.JButton();
        EditBtn = new javax.swing.JButton();
        DeleteBTN = new javax.swing.JButton();
        ClearBtn = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InventoryLogs = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(51, 153, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1200, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, 30));

        jPanel3.setBackground(new java.awt.Color(15, 23, 42));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jButton5.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Inventory");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Statistic");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        jButton7.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("Return");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        AccManageBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        AccManageBTN.setForeground(new java.awt.Color(255, 255, 255));
        AccManageBTN.setText("Account Management");

        ExportBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        ExportBTN.setForeground(new java.awt.Color(255, 255, 255));
        ExportBTN.setText("Export Data To CSV");
        ExportBTN.addActionListener(this::ExportBTNActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(AccManageBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ExportBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccManageBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ExportBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 197, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 180, 620));

        AccountManagementPanel.setBackground(new java.awt.Color(255, 255, 255));

        ACTTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Name", "Role"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(ACTTable);
        if (ACTTable.getColumnModel().getColumnCount() > 0) {
            ACTTable.getColumnModel().getColumn(0).setResizable(false);
            ACTTable.getColumnModel().getColumn(1).setResizable(false);
        }

        AccNameField.addActionListener(this::AccNameFieldActionPerformed);

        AccPasswordField.addActionListener(this::AccPasswordFieldActionPerformed);

        ConfirmPasswordField.addActionListener(this::ConfirmPasswordFieldActionPerformed);

        AccountNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountNameLabel.setForeground(new java.awt.Color(0, 0, 0));
        AccountNameLabel.setText("Account Name");

        AccountPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountPasswordLabel.setForeground(new java.awt.Color(0, 0, 0));
        AccountPasswordLabel.setText("Account Password");

        ConfirmPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        ConfirmPasswordLabel.setForeground(new java.awt.Color(0, 0, 0));
        ConfirmPasswordLabel.setText("Confirm Password");

        CAdminBTN.setBackground(new java.awt.Color(0, 102, 204));
        CAdminBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        CAdminBTN.setForeground(new java.awt.Color(255, 255, 255));
        CAdminBTN.setText("Create Admin");
        CAdminBTN.addActionListener(this::CAdminBTNActionPerformed);

        CUserBTN.setBackground(new java.awt.Color(0, 102, 204));
        CUserBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        CUserBTN.setForeground(new java.awt.Color(255, 255, 255));
        CUserBTN.setText("Create User");
        CUserBTN.addActionListener(this::CUserBTNActionPerformed);

        AccDeleteBTN.setBackground(new java.awt.Color(0, 102, 204));
        AccDeleteBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        AccDeleteBTN.setForeground(new java.awt.Color(255, 255, 255));
        AccDeleteBTN.setText("Delete");
        AccDeleteBTN.addActionListener(this::AccDeleteBTNActionPerformed);

        javax.swing.GroupLayout AccountManagementPanelLayout = new javax.swing.GroupLayout(AccountManagementPanel);
        AccountManagementPanel.setLayout(AccountManagementPanelLayout);
        AccountManagementPanelLayout.setHorizontalGroup(
            AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                .addContainerGap(146, Short.MAX_VALUE)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                        .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(AccNameField)
                            .addComponent(AccPasswordField)
                            .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                            .addComponent(AccountNameLabel)
                            .addComponent(AccountPasswordLabel)
                            .addComponent(ConfirmPasswordLabel)
                            .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                                .addComponent(CUserBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CAdminBTN)))
                        .addGap(109, 109, 109)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                        .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(209, 209, 209))))
        );
        AccountManagementPanelLayout.setVerticalGroup(
            AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(AccountNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AccountPasswordLabel)
                .addGap(4, 4, 4)
                .addComponent(AccPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ConfirmPasswordLabel)
                .addGap(4, 4, 4)
                .addComponent(ConfirmPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CUserBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CAdminBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43))
        );

        jPanel1.add(AccountManagementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 1000, 620));

        statisticsContainer.setBackground(new java.awt.Color(248, 250, 252));
        statisticsContainer.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setBackground(new java.awt.Color(30, 41, 59));
        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(30, 41, 59));
        jLabel7.setText("Weekly student check-in report");
        statisticsContainer.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 987, 27));

        lblReportingPeriod.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblReportingPeriod.setForeground(new java.awt.Color(100, 116, 139));
        lblReportingPeriod.setText("Reporting period: Oct 1 - Oct 7");
        statisticsContainer.add(lblReportingPeriod, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 987, 16));

        cardWeeklyCheckIns.setBackground(new java.awt.Color(255, 255, 255));
        cardWeeklyCheckIns.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        cardWeeklyCheckIns.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblWeeklyTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblWeeklyTitle.setForeground(new java.awt.Color(71, 85, 105));
        lblWeeklyTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWeeklyTitle.setText("Weekly check-ins");
        cardWeeklyCheckIns.add(lblWeeklyTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 180, 17));

        lblWeeklyCheckInsValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblWeeklyCheckInsValue.setForeground(new java.awt.Color(29, 78, 216));
        lblWeeklyCheckInsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWeeklyCheckInsValue.setText("0");
        cardWeeklyCheckIns.add(lblWeeklyCheckInsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 180, 36));

        jLabel8.setForeground(new java.awt.Color(148, 163, 184));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Student Checkin This week");
        cardWeeklyCheckIns.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 180, 16));

        statisticsContainer.add(cardWeeklyCheckIns, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 64, 210, 308));
        cardWeeklyCheckIns.getAccessibleContext().setAccessibleName("");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInClinicTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblInClinicTitle.setForeground(new java.awt.Color(71, 85, 105));
        lblInClinicTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInClinicTitle.setText("Currently in Clinic");
        jPanel10.add(lblInClinicTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 180, 17));

        lblInClinicValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblInClinicValue.setForeground(new java.awt.Color(29, 78, 216));
        lblInClinicValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInClinicValue.setText("0");
        jPanel10.add(lblInClinicValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 180, 36));

        jLabel9.setForeground(new java.awt.Color(148, 163, 184));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Student Waiting for release");
        jPanel10.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 180, 16));

        statisticsContainer.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 64, 210, 308));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSentHomeTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSentHomeTitle.setForeground(new java.awt.Color(71, 85, 105));
        lblSentHomeTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSentHomeTitle.setText("Sent Home");
        jPanel11.add(lblSentHomeTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 180, 17));

        lblSentHomeValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblSentHomeValue.setForeground(new java.awt.Color(29, 78, 216));
        lblSentHomeValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSentHomeValue.setText("0");
        jPanel11.add(lblSentHomeValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 180, 36));

        jLabel10.setForeground(new java.awt.Color(148, 163, 184));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Student Sent home this week");
        jPanel11.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 170, 16));

        statisticsContainer.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 64, 210, 308));

        CommonReasonPanel.setBackground(new java.awt.Color(255, 255, 255));
        CommonReasonPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        CommonReasonPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        CommonReasonTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        CommonReasonTitle.setForeground(new java.awt.Color(71, 85, 105));
        CommonReasonTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CommonReasonTitle.setText("Common Reason");
        CommonReasonPanel.add(CommonReasonTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 280, 17));

        CommonReasonLabel.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        CommonReasonLabel.setForeground(new java.awt.Color(29, 78, 216));
        CommonReasonLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        CommonReasonLabel.setText("N/A");
        CommonReasonPanel.add(CommonReasonLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 290, 36));

        statisticsContainer.add(CommonReasonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 220, 320, 150));

        FrequentlyUsedPanel.setBackground(new java.awt.Color(255, 255, 255));
        FrequentlyUsedPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        FrequentlyUsedPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        FrequentlyUsedTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        FrequentlyUsedTitle.setForeground(new java.awt.Color(71, 85, 105));
        FrequentlyUsedTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        FrequentlyUsedTitle.setText("Frequently Used Medicine");
        FrequentlyUsedPanel.add(FrequentlyUsedTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 280, 17));

        FrequentlyUsedLabel.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        FrequentlyUsedLabel.setForeground(new java.awt.Color(29, 78, 216));
        FrequentlyUsedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        FrequentlyUsedLabel.setText("N/A");
        FrequentlyUsedPanel.add(FrequentlyUsedLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 290, 36));

        statisticsContainer.add(FrequentlyUsedPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 64, 320, 150));

        jPanel7.setBackground(new java.awt.Color(248, 250, 252));
        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Daily Check-ins");
        jPanel7.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 18, 300, 20));

        jPanel8.setBackground(new java.awt.Color(248, 250, 252));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel8.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblModayDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblModayDay.setForeground(new java.awt.Color(71, 85, 105));
        lblModayDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblModayDay.setText("Monday");
        jPanel8.add(lblModayDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 161, 16));

        lblMondayDate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblMondayDate.setForeground(new java.awt.Color(29, 78, 216));
        lblMondayDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMondayDate.setText("20");
        jPanel8.add(lblMondayDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 38, 161, 30));

        lblMondayCount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblMondayCount.setForeground(new java.awt.Color(100, 116, 139));
        lblMondayCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblMondayCount.setText("0 Check-ins");
        jPanel8.add(lblMondayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 83, 161, 15));

        jPanel7.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 65, 173, 118));

        jPanel9.setBackground(new java.awt.Color(248, 250, 252));
        jPanel9.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTuesdayDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTuesdayDay.setForeground(new java.awt.Color(71, 85, 105));
        lblTuesdayDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTuesdayDay.setText("Tuesday");
        jPanel9.add(lblTuesdayDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 161, -1));

        lblTuesdayCount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblTuesdayCount.setForeground(new java.awt.Color(100, 116, 139));
        lblTuesdayCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTuesdayCount.setText("0 Check-ins");
        jPanel9.add(lblTuesdayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 83, 161, 15));

        lblTuesdayDate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTuesdayDate.setForeground(new java.awt.Color(29, 78, 216));
        lblTuesdayDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTuesdayDate.setText("21");
        jPanel9.add(lblTuesdayDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 38, 161, 30));

        jPanel7.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(205, 65, 173, 118));

        jPanel12.setBackground(new java.awt.Color(248, 250, 252));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel12.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblWednesdayDate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblWednesdayDate.setForeground(new java.awt.Color(29, 78, 216));
        lblWednesdayDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWednesdayDate.setText("22");
        jPanel12.add(lblWednesdayDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 38, 161, 30));

        lblWednesdayDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblWednesdayDay.setForeground(new java.awt.Color(71, 85, 105));
        lblWednesdayDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWednesdayDay.setText("Wednesday");
        jPanel12.add(lblWednesdayDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 161, 16));

        lblWednesdayCount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblWednesdayCount.setForeground(new java.awt.Color(100, 116, 139));
        lblWednesdayCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWednesdayCount.setText("0 Check-ins");
        jPanel12.add(lblWednesdayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 83, 161, 15));

        jPanel7.add(jPanel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(388, 65, 173, 118));

        jPanel13.setBackground(new java.awt.Color(248, 250, 252));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblThursdayDate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblThursdayDate.setForeground(new java.awt.Color(29, 78, 216));
        lblThursdayDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblThursdayDate.setText("23");
        jPanel13.add(lblThursdayDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 36, 161, 30));

        lblThursdayCount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblThursdayCount.setForeground(new java.awt.Color(100, 116, 139));
        lblThursdayCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblThursdayCount.setText("0 Check-ins");
        jPanel13.add(lblThursdayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 83, 161, 15));

        lblThursdayDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblThursdayDay.setForeground(new java.awt.Color(71, 85, 105));
        lblThursdayDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblThursdayDay.setText("Thursday");
        jPanel13.add(lblThursdayDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 161, 16));

        jPanel7.add(jPanel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(571, 65, 173, 118));

        jPanel14.setBackground(new java.awt.Color(248, 250, 252));
        jPanel14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel14.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblFridayDate.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblFridayDate.setForeground(new java.awt.Color(29, 78, 216));
        lblFridayDate.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFridayDate.setText("24");
        jPanel14.add(lblFridayDate, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 36, 161, 30));

        lblFridayCount.setFont(new java.awt.Font("Segoe UI", 0, 11)); // NOI18N
        lblFridayCount.setForeground(new java.awt.Color(100, 116, 139));
        lblFridayCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFridayCount.setText("0 Check-ins");
        jPanel14.add(lblFridayCount, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 83, 161, 15));

        lblFridayDay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFridayDay.setForeground(new java.awt.Color(71, 85, 105));
        lblFridayDay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblFridayDay.setText("Friday");
        jPanel14.add(lblFridayDay, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 12, 161, 16));

        jPanel7.add(jPanel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(754, 65, 173, 118));

        statisticsContainer.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(22, 380, 950, 208));

        jPanel1.add(statisticsContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 30, 1000, 620));

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        stockTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Status", "Product", "Quantity"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stockTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(stockTable);
        if (stockTable.getColumnModel().getColumnCount() > 0) {
            stockTable.getColumnModel().getColumn(0).setResizable(false);
            stockTable.getColumnModel().getColumn(1).setResizable(false);
            stockTable.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 650, 470));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Inventory Logs");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 70, 230, 40));

        jPanel5.setBackground(new java.awt.Color(180, 180, 180));
        jPanel5.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180)), javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        jPanel5.setForeground(new java.awt.Color(0, 0, 0));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Inventory Details");

        ProductName.addActionListener(this::ProductNameActionPerformed);
        ProductName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ProductNameKeyTyped(evt);
            }
        });

        ExpDate.addActionListener(this::ExpDateActionPerformed);
        ExpDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ExpDateKeyTyped(evt);
            }
        });

        Qty.addActionListener(this::QtyActionPerformed);
        Qty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                QtyKeyTyped(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Product Name");

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("EXP Date");

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Quantity");

        AddBTN.setText("Add");
        AddBTN.addActionListener(this::AddBTNActionPerformed);

        EditBtn.setText("Edit");
        EditBtn.addActionListener(this::EditBtnActionPerformed);

        DeleteBTN.setText("Delete");
        DeleteBTN.addActionListener(this::DeleteBTNActionPerformed);

        ClearBtn.setText("Clear");
        ClearBtn.addActionListener(this::ClearBtnActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ProductName)
                    .addComponent(ExpDate)
                    .addComponent(Qty, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ClearBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(AddBTN)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(EditBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ExpDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Qty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AddBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(EditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ClearBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 360, 320, 270));

        InventoryLogs.setColumns(20);
        InventoryLogs.setRows(5);
        jScrollPane2.setViewportView(InventoryLogs);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 110, 320, 240));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Stock");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 210, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        Dashboard dashboard = new Dashboard(loggedInAccount);
        dashboard.setLocationRelativeTo(this);
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void QtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_QtyKeyTyped
         char c = evt.getKeyChar();

                //Allow only numbers
                if (!Character.isDigit(c)) {
                  evt.consume();
                  return;
                }
    }//GEN-LAST:event_QtyKeyTyped

    private void AddBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBTNActionPerformed
        
        String name = ProductName.getText().trim();
        String expDateInput = ExpDate.getText().trim();
        String quantityText = Qty.getText().trim();
        
        String expDate;
        try {
            expDate = normalizeDate(expDateInput);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (e.g. 2028-1-9 or 2028-01-09).");
            return;
        }

        if (name.isEmpty() || expDate.isEmpty() || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }
        
        //this checks if the medicine is already existed or not
        try {
            if (productService.nameExists(name)) {
                JOptionPane.showMessageDialog(this, "A product with this name already exists. Use Edit to update its stock instead.");
                return;
            }       } catch (Exception ex) {
            System.getLogger(AdminPanel.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        
        int quantity;
        
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a valid whole number.");
            return;
        }
        //this not allows -1 on the Quantity i think
        if (quantity < 0) {
            JOptionPane.showMessageDialog(this, "Quantity cannot be negative.");
            return;
        }

        try {
            String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
            productService.addItem(name, expDate, quantity, actor);
            
            refreshInventoryScreen();
            ClearBtnActionPerformed(null);
            showToastNotification("✓ Added " + name + " (" + quantity + " units) to inventory!", name, quantity, Color.decode("#10B981"));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + ex.getMessage());
        }
          
    }//GEN-LAST:event_AddBTNActionPerformed

    private void ClearBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearBtnActionPerformed
          ProductName.setText("");
          ExpDate.setText("");
          Qty.setText("");
          selectedProductName = null;
          stockTable.clearSelection();
    }//GEN-LAST:event_ClearBtnActionPerformed

    private void stockTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stockTableMouseClicked
       int viewRow = stockTable.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = stockTable.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= currentProducts.size()) {
            // Table and currentProducts are out of sync - refresh instead of crashing.
            refreshInventoryScreen();
            return;
        }

        Medicine selected = currentProducts.get(modelRow); // real object, correct types guaranteed - DJJ - ps I DON'T KNOW WHAT THIS DO BUT DO NOT REMOVE IT

        selectedProductName = selected.getname();
        ProductName.setText(selected.getname());
        ExpDate.setText(selected.getExpDate());
        Qty.setText(String.valueOf(selected.getquantity()));
        
    }//GEN-LAST:event_stockTableMouseClicked

    private void DeleteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteBTNActionPerformed
         int[] selectedRows = stockTable.getSelectedRows();

     if (selectedRows.length == 0) {
         JOptionPane.showMessageDialog(this, "Select at least one product to delete.");
         return;
        }

    for (int row : selectedRows) {
        int modelRow = stockTable.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= currentProducts.size()) {
            JOptionPane.showMessageDialog(this, "Table is out of sync - please try again.");
            refreshInventoryScreen();
            return;
        }
    }

    StringBuilder namesPreview = new StringBuilder();
    
       for (int row : selectedRows) {
           int modelRow = stockTable.convertRowIndexToModel(row);
           namesPreview.append("- ").append(currentProducts.get(modelRow).getname()).append("\n");
            }

         int confirm = JOptionPane.showConfirmDialog(this,
             "Are you sure you want to delete the following medicine pills?\n\n" + namesPreview,
             "Confirm Delete",
             JOptionPane.YES_NO_OPTION,
             JOptionPane.WARNING_MESSAGE);

          if (confirm != JOptionPane.YES_OPTION) {
             return;
         }

          try {
           for (int row : selectedRows) {
               int modelRow = stockTable.convertRowIndexToModel(row);
               String name = currentProducts.get(modelRow).getname();
                String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
                productService.deleteItem(name, actor);
             }

              refreshInventoryScreen();
              ClearBtnActionPerformed(null);

          } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, "Error deleting items: " + ex.getMessage());
          }
    }//GEN-LAST:event_DeleteBTNActionPerformed

    private void EditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditBtnActionPerformed
              if (selectedProductName == null) {
                JOptionPane.showMessageDialog(this, "Select a product from the table first.");
                return;
            }

            String newName = ProductName.getText().trim();
            String quantityText = Qty.getText().trim();
            String newExpDate;

            try {
                newExpDate = normalizeDate(ExpDate.getText().trim());
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date (e.g. 2028-1-9 or 2028-01-09).");
                return;
            }

            int newQuantity;
            try {
                newQuantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
                return;
            }

            // 0 is a perfectly valid quantity (out of stock) - only negative values are rejected.
            if (newQuantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.");
                return;
            }

            try {
                String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
                boolean success = productService.editItem(selectedProductName, newName, newExpDate, newQuantity, actor);
                if (!success) {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                } else {
                    refreshInventoryScreen();
                    ClearBtnActionPerformed(null);
                    showToastNotification("✎ Updated " + newName + " stock details successfully!", newName, newQuantity, Color.decode("#2563EB"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error editing item: " + ex.getMessage());
            }
    }//GEN-LAST:event_EditBtnActionPerformed

    private void QtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_QtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_QtyActionPerformed

    private void ExpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExpDateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ExpDateActionPerformed

    private void ProductNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProductNameKeyTyped
          char c = evt.getKeyChar();

         //Allow letters, spaces, and backspace
        if (!Character.isLetter(c)
            && !Character.isWhitespace(c)
            && c != '\b') {
            evt.consume();
         }
    }//GEN-LAST:event_ProductNameKeyTyped

    private void ProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ProductNameActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        showInventory();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void ExpDateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExpDateKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_ExpDateKeyTyped

    private void AccDeleteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccDeleteBTNActionPerformed
                int[] selectedRows = ACTTable.getSelectedRows();

        // Nothing selected
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an account to delete.",
                "No Account Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Build list of selected account names
        StringBuilder accountList = new StringBuilder();

        for (int row : selectedRows) {
            String name = ACTTable.getValueAt(row, 0).toString();

            accountList.append("- ")
                       .append(name)
                       .append("\n");
        }

        // Confirmation warning
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete the following account(s)?\n\n"
            + accountList,
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        // User clicked No
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            // Delete each selected account
            for (int row : selectedRows) {

                String name = ACTTable.getValueAt(row, 0).toString();

               accountService.deleteAccount(loggedInAccount, name);
            }

            JOptionPane.showMessageDialog(
                this,
                "Selected account(s) deleted successfully.",
                "Delete Successful",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Refresh the table
            refreshAccountTable();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                this,
                "Error deleting account: " + ex.getMessage(),
                "Delete Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }//GEN-LAST:event_AccDeleteBTNActionPerformed

    private void CAdminBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CAdminBTNActionPerformed
          createAccountFromForm("ADMIN");
    }//GEN-LAST:event_CAdminBTNActionPerformed

    private void CUserBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CUserBTNActionPerformed
          createAccountFromForm("USER");
    }//GEN-LAST:event_CUserBTNActionPerformed
        
    private final AccountData accountService = new AccountData();
            private void createAccountFromForm(String role) {

            // =========================
            // BASIC ACCESS CHECK
            // =========================

            if (loggedInAccount == null
                    || !loggedInAccount.isAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to create accounts.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // ADMIN CREATION
            // =========================

            if ("ADMIN".equalsIgnoreCase(role)
                    && !loggedInAccount.isHeadAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Only the Head Admin can create an Admin account.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // USER CREATION
            // =========================

            if ("USER".equalsIgnoreCase(role)
                    && !loggedInAccount.isAdmin()) {

                JOptionPane.showMessageDialog(
                    this,
                    "You do not have permission to create a User account.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            // =========================
            // READ FORM
            // =========================

            String name =
                AccNameField.getText().trim();

            char[] pw1 =
                AccPasswordField.getPassword();

            char[] pw2 =
                ConfirmPasswordField.getPassword();

            String password =
                new String(pw1);

            String confirmPassword =
                new String(pw2);

            // =========================
            // VALIDATION
            // =========================

            if (name.isEmpty()
                    || password.isEmpty()) {

                JOptionPane.showMessageDialog(
                    this,
                    "Name and password are required."
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match."
                );

                return;
            }

            // =========================
            // CREATE ACCOUNT
            // =========================

            try {

                if (accountService.nameExists(name)) {

                    JOptionPane.showMessageDialog(
                        this,
                        "An account with this name already exists."
                    );

                    return;
                }

                accountService.createAccount(
                    loggedInAccount,
                    name,
                    password,
                    role
                );

                JOptionPane.showMessageDialog(
                    this,
                    role + " account created for " + name + "."
                );

                AccNameField.setText("");
                AccPasswordField.setText("");
                ConfirmPasswordField.setText("");

                refreshAccountTable();

            } catch (SecurityException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE
                );

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Error creating account: "
                        + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
            
                    private void refreshAccountTable() {
            try {
                DefaultTableModel model = (DefaultTableModel) ACTTable.getModel();
                model.setRowCount(0);

                for (AccountSystem a : accountService.loadAll()) {
                    model.addRow(new Object[]{a.GetName(), a.getRole()});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage());
            }
        }

    
    private void AccPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccPasswordFieldActionPerformed
   
  
    
    private void ExportBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportBTNActionPerformed
            // 1. Ask for the report date
      javax.swing.JTextField dateField = new javax.swing.JTextField(LocalDate.now().toString());
     ((javax.swing.text.AbstractDocument) dateField.getDocument())
             .setDocumentFilter(new DateInputFilter());
     int result = JOptionPane.showConfirmDialog(this, dateField,
             "Report date (edit if you need a different day, e.g. 2026-1-1 or 2026-01-01):",
             JOptionPane.OK_CANCEL_OPTION);
     if (result != JOptionPane.OK_OPTION) return;

     LocalDate reportDate;
     try {
         reportDate = LocalDate.parse(normalizeDate(dateField.getText().trim()));
     } catch (DateTimeParseException ex) {
         JOptionPane.showMessageDialog(this, "Please enter a valid date (e.g. 2026-1-1 or 2026-01-01).");
         return;
     }

     ReportExporter exporter = new ReportExporter(productService);

     try {
         // 2. Check for records BEFORE asking where to save
         if (!exporter.hasRecordsForDate(reportDate)) {
             JOptionPane.showMessageDialog(this, "No records found for this date.");
             return;
         }

         // 3. Ask where to save
         javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
         String suggestedName = "Clinic_Report_" + reportDate + ".xlsx";
         chooser.setSelectedFile(new java.io.File(suggestedName));
         int choice = chooser.showSaveDialog(this);
         if (choice != javax.swing.JFileChooser.APPROVE_OPTION) return;

         java.io.File destination = chooser.getSelectedFile();
         if (!destination.getName().toLowerCase().endsWith(".xlsx")) {
             destination = new java.io.File(destination.getParentFile(), destination.getName() + ".xlsx");
         }

         // 4. Generate and save
         exporter.writeDailyReport(reportDate, destination);

         // 5. Success message
         JOptionPane.showMessageDialog(this, "Report exported successfully:\n" + destination.getAbsolutePath());

         // --- Archive/reset confirmation (only after a successful, confirmed export) ---
         int archiveChoice = JOptionPane.showConfirmDialog(this,
                 "Report exported successfully. Do you want to archive and reset the daily check-in records?",
                 "Archive Check-in Records", JOptionPane.YES_NO_OPTION);

         if (archiveChoice == JOptionPane.YES_OPTION) {
             VisitData visitData = new VisitData();
             int archivedCount = visitData.archiveDate(reportDate.toString());
             JOptionPane.showMessageDialog(this,
                     archivedCount + " check-in record(s) for " + reportDate + " were archived.\n"
                     + "They remain in the database for reports and statistics, "
                     + "but no longer appear as active on the Dashboard.");
         }

     } catch (SQLException ex) {
         JOptionPane.showMessageDialog(this, "Database error while exporting report: " + ex.getMessage(),
                 "Export Failed", JOptionPane.ERROR_MESSAGE);
     } catch (IOException ex) {
         JOptionPane.showMessageDialog(this, "File error while exporting report: " + ex.getMessage(),
                 "Export Failed", JOptionPane.ERROR_MESSAGE);
     }
    }//GEN-LAST:event_ExportBTNActionPerformed

    private void AccNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccNameFieldActionPerformed

    private void ConfirmPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConfirmPasswordFieldActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        showStatistics();
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new AdminPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable ACTTable;
    private javax.swing.JButton AccDeleteBTN;
    private javax.swing.JButton AccManageBTN;
    private javax.swing.JTextField AccNameField;
    private javax.swing.JPasswordField AccPasswordField;
    private javax.swing.JPanel AccountManagementPanel;
    private javax.swing.JLabel AccountNameLabel;
    private javax.swing.JLabel AccountPasswordLabel;
    private javax.swing.JButton AddBTN;
    private javax.swing.JButton CAdminBTN;
    private javax.swing.JButton CUserBTN;
    private javax.swing.JButton ClearBtn;
    private javax.swing.JLabel CommonReasonLabel;
    private javax.swing.JPanel CommonReasonPanel;
    private javax.swing.JLabel CommonReasonTitle;
    private javax.swing.JPasswordField ConfirmPasswordField;
    private javax.swing.JLabel ConfirmPasswordLabel;
    private javax.swing.JButton DeleteBTN;
    private javax.swing.JButton EditBtn;
    private javax.swing.JTextField ExpDate;
    private javax.swing.JButton ExportBTN;
    private javax.swing.JLabel FrequentlyUsedLabel;
    private javax.swing.JPanel FrequentlyUsedPanel;
    private javax.swing.JLabel FrequentlyUsedTitle;
    private javax.swing.JTextArea InventoryLogs;
    private javax.swing.JTextField ProductName;
    private javax.swing.JTextField Qty;
    private javax.swing.JPanel cardWeeklyCheckIns;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
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
    private javax.swing.JLabel lblFridayCount;
    private javax.swing.JLabel lblFridayDate;
    private javax.swing.JLabel lblFridayDay;
    private javax.swing.JLabel lblInClinicTitle;
    private javax.swing.JLabel lblInClinicValue;
    private javax.swing.JLabel lblModayDay;
    private javax.swing.JLabel lblMondayCount;
    private javax.swing.JLabel lblMondayDate;
    private javax.swing.JLabel lblReportingPeriod;
    private javax.swing.JLabel lblSentHomeTitle;
    private javax.swing.JLabel lblSentHomeValue;
    private javax.swing.JLabel lblThursdayCount;
    private javax.swing.JLabel lblThursdayDate;
    private javax.swing.JLabel lblThursdayDay;
    private javax.swing.JLabel lblTuesdayCount;
    private javax.swing.JLabel lblTuesdayDate;
    private javax.swing.JLabel lblTuesdayDay;
    private javax.swing.JLabel lblWednesdayCount;
    private javax.swing.JLabel lblWednesdayDate;
    private javax.swing.JLabel lblWednesdayDay;
    private javax.swing.JLabel lblWeeklyCheckInsValue;
    private javax.swing.JLabel lblWeeklyTitle;
    private javax.swing.JPanel statisticsContainer;
    private javax.swing.JTable stockTable;
    // End of variables declaration//GEN-END:variables
}
