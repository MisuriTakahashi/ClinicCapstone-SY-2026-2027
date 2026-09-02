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
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import javax.swing.Timer;

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
    private final FadePanel responsiveContent = new FadePanel(new CardLayout());
    private JPanel inventoryView;
    private CardLayout contentCards;
    private long lastActivityTime;
    private Timer inactivityTimer;
    private AWTEventListener activityListener;
    

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
            SessionManager.saveSession(account, lastActivityTime);
        }
    });
    
    setupSessionTimeoutMonitoring();

    configureFlatLafUi();
    this.loggedInAccount = account;
    configureAccountManagementUi();
    installResponsiveLayout();
    ((AbstractDocument) ExpDate.getDocument()).setDocumentFilter(new DateInputFilter());
    setLocationRelativeTo(null);
    
   // AdminPanel.java constructor — replace:
   //     refreshInventoryScreen();
   //     refreshActivityLogDisplay();
   //
   //     refreshInventoryTable();
   //     loadStatistics();
   //     refreshAccountTable();
   // with:

    refreshInventoryScreen();   // already refreshes the activity log internally
    loadStatistics();
    refreshAccountTable();
}
   
   private void setupSessionTimeoutMonitoring() {
    lastActivityTime = System.currentTimeMillis();

    activityListener = e -> lastActivityTime = System.currentTimeMillis();
    Toolkit.getDefaultToolkit().addAWTEventListener(activityListener,
            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

    inactivityTimer = new Timer(15_000, e -> checkInactivity());
    inactivityTimer.start();
}

private void checkInactivity() {
    long elapsed = System.currentTimeMillis() - lastActivityTime;
    if (elapsed >= SessionManager.SESSION_TIMEOUT) {
        handleSessionExpired();
    }
}

private void handleSessionExpired() {
    stopSessionTimeoutMonitoring();
    SessionManager.clearSession();
    JOptionPane.showMessageDialog(this,
            "Your session has expired due to inactivity. Please log in again.");
    new LoginUi().setVisible(true);
    this.dispose();
}

private void stopSessionTimeoutMonitoring() {
    if (inactivityTimer != null) {
        inactivityTimer.stop();
        inactivityTimer = null;
    }
    if (activityListener != null) {
        Toolkit.getDefaultToolkit().removeAWTEventListener(activityListener);
        activityListener = null;
    }
}

   // Custom panel that supports alpha transparency for fade animations
private class FadePanel extends JPanel {
    private float alpha = 1.0f;

    public FadePanel(java.awt.LayoutManager layout) {
        super(layout);
        setOpaque(false); 
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        super.paintComponent(g2);
        g2.dispose();
    }
}
private void animateContentIn(JPanel panel) {
    // Force Swing to initialize component bounds before animating
    panel.revalidate();
    panel.doLayout();

    final int startX = responsiveContent.getWidth(); // Start off-screen right
    final int targetX = 0; // Align with responsiveContent top-left

    panel.setLocation(startX, 0);

    javax.swing.Timer timer = new javax.swing.Timer(10, null);
    timer.addActionListener(new java.awt.event.ActionListener() {
        int currentX = startX;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (currentX > targetX) {
                currentX = Math.max(targetX, currentX - 50); // Frame step rate
                panel.setLocation(currentX, 0);
                responsiveContent.repaint();
            } else {
                panel.setLocation(targetX, 0);
                responsiveContent.revalidate();
                responsiveContent.repaint();
                timer.stop();
            }
        }
    });
    timer.start();
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
    cardWeeklyCheckIns, jPanel10, SentbackPanel, jPanel11, 
    CommonReasonPanel, FrequentlyUsedPanel, jPanel7
};
for (javax.swing.JPanel card : statCards) {
    styleCard(card, surface, border);
}

// 2. Standardize the subtitle fonts and colors
java.awt.Color textSecondary = Color.decode("#475569");
javax.swing.JLabel[] statTitles = {
    lblWeeklyTitle, lblInClinicTitle, SentbackTitle, lblSentHomeTitle, 
    CommonReasonTitle, FrequentlyUsedTitle, jLabel11
};
for (javax.swing.JLabel lbl : statTitles) {
    lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lbl.setForeground(textSecondary);
    lbl.setHorizontalAlignment(SwingConstants.CENTER);
}

// 3. Standardize the big number/value fonts and colors
javax.swing.JLabel[] statValues = {
    lblWeeklyCheckInsValue, lblInClinicValue, SentBackValue, lblSentHomeValue, 
    CommonReasonLabel, FrequentlyUsedLabel
};
for (javax.swing.JLabel lbl : statValues) {
    lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
    lbl.setForeground(primary);
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
    jLabel1.setText("Activities Logs");
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
    stylePrimaryButton(RestockBTN, "Add item", primary);
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
        responsiveContent.revalidate();
        animateContentIn(statisticsContainer);
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
    styleSecondaryButton(ResetPassword, "Reset Password");
    styleDangerButton(AccDeleteBTN, "Delete");
    
    // Only Head Admin can create Admin accounts.
        CAdminBTN.setVisible(
            loggedInAccount != null
            && loggedInAccount.isHeadAdmin()
        );
         ResetPassword.setVisible(
             loggedInAccount != null && loggedInAccount.isHeadAdmin()
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
        responsiveContent.revalidate();
        animateContentIn(AccountManagementPanel);
    }
    refreshAccountTable();
    styleSidebarButton(jButton5, "Inventory", false);
    styleSidebarButton(jButton6, "Statistics", false);
    styleSidebarButton(AccManageBTN, "Account Management", true);
}

private void showInventory() {
    if (contentCards != null) {
        contentCards.show(responsiveContent, "inventory");
        responsiveContent.revalidate();
        animateContentIn(inventoryView);
    }
   
        refreshActivityLogDisplay();
    
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
    
    statisticsContainer.setPreferredSize(null);
    statisticsContainer.setMinimumSize(null);
    AccountManagementPanel.setPreferredSize(null);
    AccountManagementPanel.setMinimumSize(null);
    
    responsiveContent.setBackground(Color.decode("#F8FAFC"));
    
    buildInventoryView();
    buildStatisticsLayout();
    buildAccountManagementLayout();
    
    jPanel1.removeAll();
    jPanel1.setLayout(new MigLayout("fill, insets 0, gap 0", 
        "[180!] [grow, fill]", 
        "[30!] [grow, fill]"
    ));
    
    jPanel1.add(jPanel2, "cell 0 0 2 1, growx");
    jPanel1.add(jPanel3, "cell 0 1, growy");
    jPanel1.add(responsiveContent, "cell 1 1, grow");
    
    responsiveContent.add(inventoryView, "inventory");
    responsiveContent.add(statisticsContainer, "statistics");
    responsiveContent.add(AccountManagementPanel, "accounts");
    
    contentCards.show(responsiveContent, "inventory");
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

// Helper method to format 3-label statistic cards vertically centered
private void configureStatCard(JPanel card, JLabel title, JLabel value, JLabel subtitle) {
    card.removeAll();
    card.setLayout(new MigLayout(
        "fill, insets 20 16 20 16",
        "[grow, fill]",
        "[pref] [push, center] [pref]"
    ));

    title.setHorizontalAlignment(SwingConstants.CENTER);
    value.setHorizontalAlignment(SwingConstants.CENTER);
    subtitle.setHorizontalAlignment(SwingConstants.CENTER);

    card.add(title, "growx, wrap");
    card.add(value, "growx, center, wrap");
    card.add(subtitle, "growx, wrap");
}

// Helper method to format 2-label statistic cards
private void configureTwoLabelCard(JPanel card, JLabel title, JLabel value) {
    card.removeAll();
    card.setLayout(new MigLayout(
        "fill, insets 20 16 20 16",
        "[grow, fill]",
        "[pref] [push, center]"
    ));

    title.setHorizontalAlignment(SwingConstants.CENTER);
    value.setHorizontalAlignment(SwingConstants.CENTER);

    card.add(title, "growx, wrap");
    card.add(value, "growx, center");
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

    // Convert card interior layouts to MigLayout for perfect alignment
    configureStatCard(cardWeeklyCheckIns, lblWeeklyTitle, lblWeeklyCheckInsValue, jLabel8);
    configureStatCard(jPanel10, lblInClinicTitle, lblInClinicValue, jLabel9);
    configureStatCard(SentbackPanel, SentbackTitle, SentBackValue, SentbackLabel);
    configureStatCard(jPanel11, lblSentHomeTitle, lblSentHomeValue, jLabel10);

    configureTwoLabelCard(FrequentlyUsedPanel, FrequentlyUsedTitle, FrequentlyUsedLabel);
    configureTwoLabelCard(CommonReasonPanel, CommonReasonTitle, CommonReasonLabel);

    // Columns 0 & 1 span 2 rows vertically
    statisticsContainer.add(cardWeeklyCheckIns, "cell 0 2, spany 2, grow, push");
    statisticsContainer.add(jPanel10, "cell 1 2, spany 2, grow, push");

    // Column 2 split into two rows: Sent Back (top) and Sent Home (bottom)
    statisticsContainer.add(SentbackPanel, "cell 2 2, grow, push");
    statisticsContainer.add(jPanel11, "cell 2 3, grow, push");

    // Column 3 split into two rows: Frequently Used (top) and Common Reason (bottom)
    statisticsContainer.add(FrequentlyUsedPanel, "cell 3 2, grow, push");
    statisticsContainer.add(CommonReasonPanel, "cell 3 3, grow, push");

    // Daily Check-ins container
    jPanel7.removeAll();
    jPanel7.setLayout(new MigLayout(
        "fill, insets 14, gap 10",
        "[grow] [grow] [grow] [grow] [grow]", 
        "[pref] [160px!]" 
    ));
    jPanel7.add(jLabel11, "cell 0 0 5 1, growx");

    configureDayCard(jPanel8, lblModayDay, lblMondayDate, lblMondayCount);
    configureDayCard(jPanel9, lblTuesdayDay, lblTuesdayDate, lblTuesdayCount);
    configureDayCard(jPanel12, lblWednesdayDay, lblWednesdayDate, lblWednesdayCount);
    configureDayCard(jPanel13, lblThursdayDay, lblThursdayDate, lblThursdayCount);
    configureDayCard(jPanel14, lblFridayDay, lblFridayDate, lblFridayCount);

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
        "fill, insets 12, gap 6",
        "[grow]",
        "[pref] [pref] [push, grow]" 
    ));
    
    day.setHorizontalAlignment(SwingConstants.CENTER);
    date.setHorizontalAlignment(SwingConstants.CENTER);
    count.setHorizontalAlignment(SwingConstants.CENTER);
    
    date.setFont(new Font("Segoe UI", Font.BOLD, 36)); 
    
    card.add(day, "growx, wrap");
    card.add(date, "growx, wrap");
    card.add(count, "growx, aligny bottom");
}
private void buildAccountManagementLayout() {
    AccountManagementPanel.removeAll();

    // Reset preferred size constraints set by NetBeans Designer
    jScrollPane3.setPreferredSize(null);
    jScrollPane3.setMinimumSize(null);
    jScrollPane3.setMaximumSize(null);
    AccDeleteBTN.setPreferredSize(null);

    // Main panel layout: 2 columns, 1 row for overall content
    AccountManagementPanel.setLayout(new MigLayout(
        "fill, insets 40 50 40 50, gap 50", 
        "[320!, fill] [grow, fill]", // Left: 320px fixed form, Right: table & delete button area
        "[grow, fill]"
    ));

    // Left Side Container (Form centered vertically)
    JPanel leftContainer = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow] [pref] [grow]"));
    leftContainer.setOpaque(false);
    leftContainer.add(createAccountFormPanel(), "cell 0 1, growx");

    // Right Side Container (Table on top, Delete button horizontally centered below table)
    JPanel rightContainer = new JPanel(new MigLayout(
        "fill, insets 0, gap 16", 
        "[grow]", 
        "[grow, fill] [40px!]"
    ));
    rightContainer.setOpaque(false);
    
    rightContainer.add(jScrollPane3, "cell 0 0, grow");
    rightContainer.add(AccDeleteBTN, "cell 0 1, center, w 140!, h 40!");

    // Add containers to main panel
    AccountManagementPanel.add(leftContainer, "cell 0 0");
    AccountManagementPanel.add(rightContainer, "cell 1 0");

    AccountManagementPanel.revalidate();
    AccountManagementPanel.repaint();
}

private JPanel createAccountFormPanel() {
    JPanel form = new JPanel(new MigLayout(
        "fillx, insets 0, gap 8", 
        "[grow] [grow]", 
        "[pref] [38px!] [pref] [38px!] [pref] [38px!] [16px!] [40px!] [8px!] [40px!]" // Added rows
    ));
    form.setOpaque(false);

    // ===== ACCOUNT NAME =====
    form.add(AccountNameLabel, "cell 0 0 2 1, growx");
    form.add(AccNameField, "cell 0 1 2 1, growx, h 38px!");

    // ===== ACCOUNT PASSWORD =====
    form.add(AccountPasswordLabel, "cell 0 2 2 1, growx, gaptop 6");
    form.add(AccPasswordField, "cell 0 3 2 1, growx, h 38px!");

    // ===== CONFIRM PASSWORD =====
    form.add(ConfirmPasswordLabel, "cell 0 4 2 1, growx, gaptop 6");
    form.add(ConfirmPasswordField, "cell 0 5 2 1, growx, h 38px!");

    // ===== BUTTONS =====
    form.add(CUserBTN, "cell 0 7, growx, h 40!");
    form.add(CAdminBTN, "cell 1 7, growx, h 40!");
    
    // ===== RESET PASSWORD BUTTON =====
    form.add(ResetPassword, "cell 0 9 2 1, growx, h 40!"); // <--- ADD THIS (spans 2 columns)

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
        DatabaseExecutor.run(
                () -> productService.loadActivityLog(),
                log -> {
                    StringBuilder sb = new StringBuilder();

                    if (log.isEmpty()) {
                        sb.append("No recent activity.\n\nChanges to stock levels will be recorded here.");
                    } else {
                        for (int i = log.size() - 1; i >= 0; i--) {
                            sb.append(log.get(i)).append("\n\n");
                        }
                    }

                    InventoryLogs.setText(sb.toString());
                    InventoryLogs.setCaretPosition(0);
                },
                ex -> JOptionPane.showMessageDialog(this, "Error loading activity log: " + ex.getMessage())
        );
    }
   /* 
    private void refreshInventoryTable() {
            DatabaseExecutor.run(
               () -> accountService.loadAll(),
               accounts -> {
                   DefaultTableModel model = (DefaultTableModel) ACTTable.getModel();
                   model.setRowCount(0);

                   for (AccountSystem a : accounts) {
                       model.addRow(new Object[]{a.GetName(), a.getRole()});
                   }
               },
               ex -> JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage())
       );
    }
    */
    
    private void refreshInventoryScreen() {
        DatabaseExecutor.run(
                () -> productService.loadAll(),
                products -> {
                    currentProducts = products;
                    DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                    model.setRowCount(0);

                    for (Medicine p : currentProducts) {
                        model.addRow(new Object[]{p.getStatus(), p.getname(), p.getquantity()});
                    }
                    refreshActivityLogDisplay();
                },
                ex -> JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage())
        );
    }
    
  private void loadStatistics() {
    // The only change: the JDBC call moves off the EDT. Everything that
    // used to happen after loadAll() now happens in applyStatistics(),
    // invoked here on the EDT once the background load finishes.
    DatabaseExecutor.run(
            () -> new VisitData().loadAll(),
            this::applyStatistics,
            ex -> JOptionPane.showMessageDialog(
                    this,
                    "Error loading statistics: " + ex.getMessage(),
                    "Statistics Error",
                    JOptionPane.ERROR_MESSAGE)
    );
}

/** Computes and displays the statistics cards from an already-loaded visit list. */
private void applyStatistics(ArrayList<CheckinSystem> visits) {
    try {
        LocalDate today = LocalDate.now();
        WeeklyStats stats = computeWeeklyStats(visits, today);

        lblMondayDate.setText(String.valueOf(stats.mondayOfWeek().getDayOfMonth()));
        lblTuesdayDate.setText(String.valueOf(stats.mondayOfWeek().plusDays(1).getDayOfMonth()));
        lblWednesdayDate.setText(String.valueOf(stats.mondayOfWeek().plusDays(2).getDayOfMonth()));
        lblThursdayDate.setText(String.valueOf(stats.mondayOfWeek().plusDays(3).getDayOfMonth()));
        lblFridayDate.setText(String.valueOf(stats.mondayOfWeek().plusDays(4).getDayOfMonth()));

        CommonReasonLabel.setText(stats.topReason());
        FrequentlyUsedLabel.setText(stats.topMedicine());

        lblWeeklyCheckInsValue.setText(String.valueOf(stats.weeklyCheckins()));
        lblInClinicValue.setText(String.valueOf(stats.inClinic()));
        lblSentHomeValue.setText(String.valueOf(stats.sentHome()));
        SentBackValue.setText(String.valueOf(stats.sentBack()));

        lblMondayCount.setText(String.valueOf(stats.monday()));
        lblTuesdayCount.setText(String.valueOf(stats.tuesday()));
        lblWednesdayCount.setText(String.valueOf(stats.wednesday()));
        lblThursdayCount.setText(String.valueOf(stats.thursday()));
        lblFridayCount.setText(String.valueOf(stats.friday()));

        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        lblReportingPeriod.setText(
                "Reporting period: "
                + stats.mondayOfWeek().format(displayFormatter)
                + " to "
                + stats.fridayOfWeek().format(displayFormatter)
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
 * Aggregated Monday-Friday statistics for the week containing {@code anyDayInWeek},
 * computed from an already-loaded visit list. Shared by the Admin Panel's on-screen
 * statistics cards (applyStatistics, above) and the Excel daily report
 * (ReportExporter.writeDailyReport), so the two can never disagree.
 *
 * Counting rules are unchanged from the original applyStatistics(): every visit
 * passed in is counted, so a caller using loadAll() (which includes archived
 * visits) gets the same archived-inclusive behavior as before.
 */
public static WeeklyStats computeWeeklyStats(ArrayList<CheckinSystem> visits, LocalDate anyDayInWeek) {

    int weeklyCheckins = 0;
    int inClinic = 0;
    int sentBack = 0;
    int sentHome = 0;

    int monday = 0;
    int tuesday = 0;
    int wednesday = 0;
    int thursday = 0;
    int friday = 0;

    Map<String, Integer> reasonCount = new HashMap<>();
    Map<String, Integer> medicineCount = new HashMap<>();

    LocalDate mondayOfWeek = anyDayInWeek.with(DayOfWeek.MONDAY);
    LocalDate fridayOfWeek = anyDayInWeek.with(DayOfWeek.FRIDAY);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

    for (CheckinSystem v : visits) {

        LocalDate visitDate;

        try {
            visitDate = LocalDateTime.parse(v.getCheckInTime(), formatter).toLocalDate();
        } catch (DateTimeParseException ex) {
            continue;
        }

        if (!visitDate.isBefore(mondayOfWeek) && !visitDate.isAfter(fridayOfWeek)) {

            weeklyCheckins++;

            switch (visitDate.getDayOfWeek()) {
                case MONDAY:    monday++;    break;
                case TUESDAY:   tuesday++;   break;
                case WEDNESDAY: wednesday++; break;
                case THURSDAY:  thursday++;  break;
                case FRIDAY:    friday++;    break;
                default: break;
            }
        }

        if ("In Clinic".equalsIgnoreCase(v.getStatus())) {
            inClinic++;
        }
        if ("Sent Back".equalsIgnoreCase(v.getStatus())) {
            sentBack++;
        }
        if ("Sent Home".equalsIgnoreCase(v.getStatus())) {
            sentHome++;
        }

        if (v.getReason() != null && !v.getReason().isBlank()) {
            reasonCount.put(v.getReason(), reasonCount.getOrDefault(v.getReason(), 0) + 1);
        }

        if (v.getMedUsed() != null && !v.getMedUsed().isBlank()) {
            medicineCount.put(v.getMedUsed(), medicineCount.getOrDefault(v.getMedUsed(), 0) + 1);
        }
    }

    String topReason = "N/A";
    int maxReasonCount = 0;
    for (Map.Entry<String, Integer> entry : reasonCount.entrySet()) {
        if (entry.getValue() > maxReasonCount) {
            maxReasonCount = entry.getValue();
            topReason = entry.getKey();
        }
    }

    String topMedicine = "N/A";
    int maxMedCount = 0;
    for (Map.Entry<String, Integer> entry : medicineCount.entrySet()) {
        if (entry.getValue() > maxMedCount) {
            maxMedCount = entry.getValue();
            topMedicine = entry.getKey();
        }
    }

    if (topMedicine.equalsIgnoreCase("None") || topMedicine.trim().isEmpty()) {
        topMedicine = "No medicine used";
    }

    return new WeeklyStats(
            weeklyCheckins, inClinic, sentBack, sentHome,
            monday, tuesday, wednesday, thursday, friday,
            topReason, topMedicine,
            mondayOfWeek, fridayOfWeek
    );
}

/**
 * Immutable result of computeWeeklyStats() — one Monday-Friday week's worth of
 * clinic statistics, in the same shape used by both the on-screen cards and the
 * Excel STATISTICS section.
 */
public record WeeklyStats(
        int weeklyCheckins, int inClinic, int sentBack, int sentHome,
        int monday, int tuesday, int wednesday, int thursday, int friday,
        String topReason, String topMedicine,
        LocalDate mondayOfWeek, LocalDate fridayOfWeek) {
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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        LRNLabel = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        RestockBTN = new javax.swing.JButton();
        RestockBTN1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        AccManageBTN = new javax.swing.JButton();
        ExportBTN = new javax.swing.JButton();
        StudentManagementBTN = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stockTable = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        InventoryLogs = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
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
        ResetPassword = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Grade & Section", "LRN", "Parent/Guardian Name", "Phone number", "Allergy", "Health Condition"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
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
        jScrollPane4.setViewportView(ReasonTable);
        if (ReasonTable.getColumnModel().getColumnCount() > 0) {
            ReasonTable.getColumnModel().getColumn(0).setResizable(false);
            ReasonTable.getColumnModel().getColumn(1).setResizable(false);
            ReasonTable.getColumnModel().getColumn(2).setResizable(false);
            ReasonTable.getColumnModel().getColumn(3).setResizable(false);
            ReasonTable.getColumnModel().getColumn(4).setResizable(false);
            ReasonTable.getColumnModel().getColumn(5).setResizable(false);
            ReasonTable.getColumnModel().getColumn(6).setResizable(false);
        }

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Name:");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Grade and Section");

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        LRNLabel.setForeground(new java.awt.Color(255, 255, 255));
        LRNLabel.setText("LRN:");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Allergy:");

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Health Condition:");

        jTextField2.setText("jTextField2");

        jTextField3.setText("jTextField2");

        jTextField4.setText("jTextField2");

        jTextField5.setText("jTextField2");

        jTextField6.setText("jTextField2");

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Parent/Gurdian Name");

        jTextField7.setText("jTextField2");

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Phone Number");

        jTextField8.setText("jTextField2");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel29)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel30))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel31))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(1, 1, 1)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jButton2.setText("Edit");

        jButton3.setText("Import Exel");

        jButton4.setText("Clear");

        jButton6.setText("Delete");

        jTextField1.setText("jTextField1");

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Search:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(259, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 634, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(374, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton6))
                .addGap(39, 39, 39))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(80, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(60, 60, 60)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 477, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(83, Short.MAX_VALUE)))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1020, 620));

        RestockBTN.setText("Restock");
        RestockBTN.addActionListener(this::RestockBTNActionPerformed);
        jPanel1.add(RestockBTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 590, 110, 50));

        RestockBTN1.setText("Edit");
        RestockBTN1.addActionListener(this::RestockBTN1ActionPerformed);
        jPanel1.add(RestockBTN1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 590, 100, 50));

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

        StudentManagementBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 10)); // NOI18N
        StudentManagementBTN.setForeground(new java.awt.Color(255, 255, 255));
        StudentManagementBTN.setText("Student Mangement");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(13, 13, 13))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ExportBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(AccManageBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(StudentManagementBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StudentManagementBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addComponent(AccManageBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ExportBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 180, 620));

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 650, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 110, 650, 470));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel1.setText("Inventory Logs");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 70, 230, 40));

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
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 110, 320, 470));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel6.setText("Stock");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 70, 210, 40));

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
        AccountNameLabel.setText("Account Name");

        AccountPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountPasswordLabel.setText("Account Password");

        ConfirmPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
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

        ResetPassword.setBackground(new java.awt.Color(0, 102, 204));
        ResetPassword.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        ResetPassword.setForeground(new java.awt.Color(255, 255, 255));
        ResetPassword.setText("Reset Password");
        ResetPassword.addActionListener(this::ResetPasswordActionPerformed);

        javax.swing.GroupLayout AccountManagementPanelLayout = new javax.swing.GroupLayout(AccountManagementPanel);
        AccountManagementPanel.setLayout(AccountManagementPanelLayout);
        AccountManagementPanelLayout.setHorizontalGroup(
            AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AccountManagementPanelLayout.createSequentialGroup()
                .addContainerGap(149, Short.MAX_VALUE)
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
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))
                    .addGroup(AccountManagementPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ResetPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(81, 81, 81))))
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
                .addContainerGap(93, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(AccountManagementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AccDeleteBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ResetPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(49, 49, 49))
        );

        jPanel1.add(AccountManagementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1020, 620));

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
        stopSessionTimeoutMonitoring();
        Dashboard dashboard = new Dashboard(loggedInAccount);
        dashboard.setLocationRelativeTo(this);
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void RestockBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestockBTNActionPerformed
        
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
          
    }//GEN-LAST:event_RestockBTNActionPerformed

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

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        showInventory();
    }//GEN-LAST:event_jButton5ActionPerformed

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
    DatabaseExecutor.run(
            () -> accountService.loadAll(),
            accounts -> {
                DefaultTableModel model = (DefaultTableModel) ACTTable.getModel();
                model.setRowCount(0);

                for (AccountSystem a : accounts) {
                    model.addRow(new Object[]{a.GetName(), a.getRole()});
                }
            },
            ex -> JOptionPane.showMessageDialog(this, "Error loading accounts: " + ex.getMessage())
    );
}
    
    private void AccPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccPasswordFieldActionPerformed
   
     /** Small holder so the background export task can report both the saved
     *  file and whether the audit-log write succeeded, in one callback. */
    private record ExportOutcome(java.io.File file, boolean auditLogged) {}
  
    
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

        String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
        ReportExporter exporter = new ReportExporter(productService);

        // 2. Check for records BEFORE asking where to save
        try {
            if (!exporter.hasRecordsForDate(reportDate)) {
                JOptionPane.showMessageDialog(this, "No records found for this date.");
                return;
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(this, "Error checking records for this date: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Ask where to save
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        String suggestedName = "Clinic_Report_" + reportDate + ".xlsx";
        chooser.setSelectedFile(new java.io.File(suggestedName));
        int choice = chooser.showSaveDialog(this);
        if (choice != javax.swing.JFileChooser.APPROVE_OPTION) return; // cancelled -> never logged

        java.io.File destination = chooser.getSelectedFile();
        if (!destination.getName().toLowerCase().endsWith(".xlsx")) {
            destination = new java.io.File(destination.getParentFile(), destination.getName() + ".xlsx");
        }
        final java.io.File finalDestination = destination;

        // 4. Generate and save OFF the EDT via the existing DatabaseExecutor
        // architecture, so POI's workbook build/style/autosize/write never
        // blocks the UI. The button is disabled for the duration so a
        // double-click can't kick off two overlapping exports.
        ExportBTN.setEnabled(false);
        ExportBTN.setText("Generating...");

        DatabaseExecutor.run(
                () -> {
                    boolean auditLogged = exporter.writeDailyReport(reportDate, finalDestination, actor);
                    return new ExportOutcome(finalDestination, auditLogged);
                },
                outcome -> {
                    ExportBTN.setEnabled(true);
                    ExportBTN.setText("Export DB");

                    // 6. Refresh the Activities / Audit Log immediately — reuses the
                    // existing implementation, no second loading system.
                    refreshActivityLogDisplay();

                    // 5. Success message
                    String successMsg = "Report exported successfully:\n" + outcome.file().getAbsolutePath();
                    if (!outcome.auditLogged()) {
                        successMsg += "\n\nNote: the file was saved, but the EXPORT_REPORT "
                                + "activity could not be recorded in the audit log (database "
                                + "issue). The exported file itself is complete and unaffected.";
                    }
                    JOptionPane.showMessageDialog(this, successMsg);

                    // --- Archive/reset confirmation (only after a successful, confirmed export) ---
                    int archiveChoice = JOptionPane.showConfirmDialog(this,
                            "Report exported successfully. Do you want to archive and reset the daily check-in records?",
                            "Archive Check-in Records", JOptionPane.YES_NO_OPTION);

                    if (archiveChoice == JOptionPane.YES_OPTION) {
                        DatabaseExecutor.run(
                                () -> new VisitData().archiveDate(reportDate.toString()),
                                archivedCount -> {
                                    refreshInventoryScreen();
                                    JOptionPane.showMessageDialog(this,
                                            archivedCount + " check-in record(s) for " + reportDate + " were archived.\n"
                                            + "They remain in the database for reports and statistics, "
                                            + "but no longer appear as active on the Dashboard.");
                                },
                                ex -> JOptionPane.showMessageDialog(this,
                                        "Error archiving records: " + ex.getMessage(),
                                        "Archive Failed", JOptionPane.ERROR_MESSAGE)
                        );
                    }
                },
                ex -> {
                    // Export failed: file save failed, or a DB error happened while
                    // loading the records used to build the report. No EXPORT_REPORT
                    // entry exists in this case (writeDailyReport only logs after the
                    // .xlsx write already succeeded), so there's nothing to refresh.
                    ExportBTN.setEnabled(true);
                    ExportBTN.setText("Export DB");
                    JOptionPane.showMessageDialog(this, "Error exporting report: " + ex.getMessage(),
                            "Export Failed", JOptionPane.ERROR_MESSAGE);
                }
        );
    }//GEN-LAST:event_ExportBTNActionPerformed

    private void AccNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccNameFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AccNameFieldActionPerformed

    private void ConfirmPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmPasswordFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConfirmPasswordFieldActionPerformed

    private void ResetPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetPasswordActionPerformed
       
        int viewRow = ACTTable.getSelectedRow();

        if (viewRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select an account to reset the password for.",
                "No Account Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String targetName = ACTTable.getValueAt(viewRow, 0).toString();

        if (loggedInAccount == null || !loggedInAccount.isHeadAdmin()) {
            JOptionPane.showMessageDialog(
                this,
                "Only Head Admin accounts can reset passwords.",
                "Access Denied",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (loggedInAccount.GetName().equalsIgnoreCase(targetName)) {
            JOptionPane.showMessageDialog(
                this,
                "You cannot reset your own password using this function.",
                "Password Reset Not Available",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to reset the password\nfor account \"" + targetName + "\"?",
            "Reset Password",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        char[] newPassword = null;
        char[] confirmPassword = null;

        try {
            while (true) {

                javax.swing.JPasswordField newPasswordField = new javax.swing.JPasswordField();
                int newResult = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Enter a new password:", newPasswordField},
                    "Set New Password",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (newResult != JOptionPane.OK_OPTION) {
                    return;
                }

                newPassword = newPasswordField.getPassword();

                if (newPassword.length == 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "The new password cannot be empty.",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                javax.swing.JPasswordField confirmPasswordField = new javax.swing.JPasswordField();
                int confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    new Object[]{"Re-enter the new password:", confirmPasswordField},
                    "Confirm New Password",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (confirmResult != JOptionPane.OK_OPTION) {
                    return;
                }

                confirmPassword = confirmPasswordField.getPassword();

                if (confirmPassword.length == 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please confirm the new password.",
                        "Invalid Password",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                if (!java.util.Arrays.equals(newPassword, confirmPassword)) {
                    JOptionPane.showMessageDialog(
                        this,
                        "The passwords do not match.\nPlease try again.",
                        "Password Mismatch",
                        JOptionPane.WARNING_MESSAGE
                    );
                    continue;
                }

                break;
            }

            boolean success = accountService.resetPassword(
                loggedInAccount, targetName, newPassword
            );

            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "The password for account \"" + targetName + "\" has been\nsuccessfully updated.",
                    "Password Updated",
                    JOptionPane.INFORMATION_MESSAGE
                );
                refreshAccountTable();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Unable to update the password.\nPlease try again.",
                    "Password Update Failed",
                    JOptionPane.ERROR_MESSAGE
                );
            }

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
                "Unable to update the password.\nPlease try again.",
                "Password Update Failed",
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            if (newPassword != null) java.util.Arrays.fill(newPassword, ' ');
            if (confirmPassword != null) java.util.Arrays.fill(confirmPassword, ' ');
        }
    }//GEN-LAST:event_ResetPasswordActionPerformed

    private void RestockBTN1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RestockBTN1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RestockBTN1ActionPerformed

    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReasonTableMouseClicked

        int row = ReasonTable.getSelectedRow();

        if (row == -1) return;

        CheckinSystem selected = currentVisits.get(row);

        selectedVisitLrn = selected.getLrn();
        selectedOldMedUsed = selected.getMedUsed();
        selectedOldMedsQty = selected.getmedsQty();
        selectedGuardianName = selected.getGuardianName();
        selectedGuardianPhone = selected.getGuardianPhoneNums();

        NameCheckIn.setText(selected.getName());
        GSCheckIn.setText(selected.getGradeSection());
        LRNField.setText(selected.getLrn());
        LRNField.setEditable(true);
        ReasonArea.setText(selected.getReason());
        jComboBox1.setSelectedItem(selected.getMedUsed());

    }//GEN-LAST:event_ReasonTableMouseClicked

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
    private javax.swing.JButton CAdminBTN;
    private javax.swing.JButton CUserBTN;
    private javax.swing.JPasswordField ConfirmPasswordField;
    private javax.swing.JLabel ConfirmPasswordLabel;
    private javax.swing.JButton ExportBTN;
    private javax.swing.JTextArea InventoryLogs;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JButton ResetPassword;
    private javax.swing.JButton RestockBTN;
    private javax.swing.JButton RestockBTN1;
    private javax.swing.JButton StudentManagementBTN;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTable stockTable;
    // End of variables declaration//GEN-END:variables
}
