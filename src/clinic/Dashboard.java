/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;


import java.awt.Component;
import java.awt.Graphics2D;
import java.io.File;
import net.miginfocom.swing.MigLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import java.sql.SQLException;
import java.io.IOException;
import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import javax.swing.Timer;

/**
 *
 * @author PC
 */
public class Dashboard extends javax.swing.JFrame {
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private static boolean darkMode = false;
    private GlassOverlayPanel glassOverlay = new GlassOverlayPanel();
    private AccountSystem loggedInAccount;
    private long lastActivityTime;
    private Timer inactivityTimer;
    private AWTEventListener activityListener;
    private long tableRefreshGeneration = 0L;

    /**
     * Creates new form Dashboard
     */
  
            // Used when you just want to preview/open Dashboard
            // has no design whatsoever 
            /*public Dashboard() {
            initComponents();

            this.loggedInAccount = null;

            setLocationRelativeTo(null);
            startDateTimeClock();
            refreshTableAndCounters();
            refreshInventoryStatusDisplay();
            medicineBox();

            jButton1.setVisible(false);
            
        }*/
            
            // Used when opening Dashboard normally after login
        public Dashboard(AccountSystem account) {
            this.loggedInAccount = account;
            
            initComponents();                
            setIconImage(AppIcon.getIcon());

            
            // Window X (not Logout): remember this session so the user is
            // auto-logged-in next launch, instead of being forced to log in again
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    SessionManager.saveSession(loggedInAccount, lastActivityTime);
                }
            });
            
            setupSessionTimeoutMonitoring();
            
            applyMigLayouts();
            
            this.setSize(1366, 800);             // Sets a standard HD laptop size
            this.setMinimumSize(new java.awt.Dimension(1024, 600)); // Prevents it from getting too small
            this.setLocationRelativeTo(null);
            
            
            
            ReasonTable.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            ((AbstractDocument) NameCheckIn.getDocument()).setDocumentFilter(new NameInputFilter()); //this is an input filter for NameCheckin which dont allows numbers
            ((AbstractDocument) ParentGurdianName.getDocument()).setDocumentFilter(new NameInputFilter()); //this also and input filter for the parentName which do not allows numbers too
            ((AbstractDocument) PhoneField.getDocument()).setDocumentFilter(new PhoneNumberFilter()); // this only allows 09 number and also 11 digits only 
             
                setLocationRelativeTo(null);
            
            //this already shows the time and Table and Counters and display the inventoryStatus and medicinebox which is the comboBox
            startDateTimeClock();
            refreshTableAndCounters();
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentShown(java.awt.event.ComponentEvent e) {
                    refreshInventoryStatusDisplay(); // now safe — frame is showing
                }
            });
            medicineBox();
            
            InventoryStatusArea.setEditable(false);
            SentHomeInformationPanel.setVisible(false);

            SentHomeInformationPanel.setOpaque(true);
            SentHomeInformationPanel.setBackground(java.awt.Color.WHITE);

            // Add a visible border so the popup panel pops out clearly
            SentHomeInformationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200), 1));

            // Ensure child components are brought to the front layer
            SentHomeInformationPanel.revalidate();
            SentHomeInformationPanel.repaint();

            javax.swing.JLayeredPane layeredPane = this.getLayeredPane();

    // 2. Add the panel to a higher layer
            layeredPane.add(SentHomeInformationPanel, javax.swing.JLayeredPane.MODAL_LAYER);

            // 3. Position it over the center of the frame

            SentHomeInformationPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(180, 180, 180), 2));
            SentHomeInformationPanel.setBackground(java.awt.Color.WHITE);
            SentHomeInformationPanel.setOpaque(true);
            SentHomeInformationPanel.putClientProperty("JComponent.outline", "gray");
            SentHomeInformationPanel.putClientProperty("JComponent.arc", 15);
            SentHomeInformationPanel.setOpaque(true);
            SentHomeInformationPanel.setBackground(java.awt.Color.WHITE);

            // FlatLaf native arc rounding and subtle outline border
            SentHomeInformationPanel.putClientProperty("JComponent.arc", 20);
            SentHomeInformationPanel.putClientProperty("JComponent.outline", new java.awt.Color(210, 215, 220));

            // --- 2. Text Fields Styling (Pill/Rounded Style with Placeholders) ---
            ParentGurdianName.putClientProperty("JComponent.roundRect", true);
            ParentGurdianName.putClientProperty("JTextField.placeholderText", "Enter parent/guardian full name...");
            ParentGurdianName.putClientProperty("JTextField.showClearButton", true);

            PhoneField.putClientProperty("JComponent.roundRect", true);
            PhoneField.putClientProperty("JTextField.placeholderText", "e.g., 09123456789");
            PhoneField.putClientProperty("JTextField.showClearButton", true);

            // --- 3. Custom Button Styles (Blue Finish / Red Back) ---
            FinishBTN.putClientProperty("JButton.buttonType", "roundRect");
            FinishBTN.setBackground(new java.awt.Color(0, 102, 204));
            FinishBTN.setForeground(java.awt.Color.WHITE);

            InformationBackBTN.putClientProperty("JButton.buttonType", "roundRect");
            InformationBackBTN.setBackground(new java.awt.Color(153, 0, 0)); // Dark red tone
            InformationBackBTN.setForeground(java.awt.Color.WHITE);
            SentHomeInformationPanel.putClientProperty("JComponent.arc", 16);
            SentHomeInformationPanel.setBackground(java.awt.Color.WHITE);
            SentHomeInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 225, 230), 1, true),
                javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            // 2. Style the text input fields with round borders and placeholders
            ParentGurdianName.setText("");
            ParentGurdianName.putClientProperty("JComponent.roundRect", true);
            ParentGurdianName.putClientProperty("JTextField.placeholderText", "Enter parent/guardian full name...");
            ParentGurdianName.putClientProperty("JTextField.showClearButton", true);

            PhoneField.setText("");
            PhoneField.putClientProperty("JComponent.roundRect", true);
            PhoneField.putClientProperty("JTextField.placeholderText", "e.g., 09123456789");
            PhoneField.putClientProperty("JTextField.showClearButton", true);

            // 3. Style the Action Button to match your primary theme buttons
            FinishBTN.putClientProperty("JButton.buttonType", "roundRect");
            FinishBTN.setBackground(new java.awt.Color(37, 99, 235)); // Modern Blue (#2563EB)
            FinishBTN.setForeground(java.awt.Color.WHITE);


            getLayeredPane().add(glassOverlay, javax.swing.JLayeredPane.MODAL_LAYER);
            getLayeredPane().add(SentHomeInformationPanel, javax.swing.JLayeredPane.POPUP_LAYER);

            // Block mouse clicks from hitting the table underneath
            glassOverlay.addMouseListener(new java.awt.event.MouseAdapter() {});




            VisitPanel.putClientProperty("JComponent.arc", 25);
            SentHomePanel.putClientProperty("JComponent.arc", 25);
            CheckInPanel.putClientProperty("JComponent.arc", 25);
            
            InventoryPanel.putClientProperty("JComponent.arc", 25);

            java.awt.Color softSlate = new java.awt.Color(245, 247, 250); 
            CheckInPanel.setBackground(softSlate);
            
            InventoryPanel.setBackground(softSlate);
            MainPanel.setBackground(java.awt.Color.WHITE); // Bright, clean backdrop

            // --- 2. Flat UI Unified Table & ScrollPane Fixes ---
            ReasonTable.setFillsViewportHeight(true); // Keeps entire viewport area uniform white
            ReasonTable.setBackground(java.awt.Color.WHITE);
            jScrollPane2.getViewport().setBackground(java.awt.Color.WHITE); 
            jScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 225, 230)));

            // Clean up text areas and inventory panels
            InventoryStatusArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
            jScrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 225, 230)));
            ReasonArea.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
            jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(220, 225, 230)));

            // --- 3. Sleek Inputs & Smart Placeholders ---
            // Clear default text values ("Name" / "Grade/Section") so placeholders take over
            NameCheckIn.setText("");
            NameCheckIn.putClientProperty("JComponent.roundRect", true);
            NameCheckIn.putClientProperty("JTextField.placeholderText", "Enter student's full name...");
            NameCheckIn.putClientProperty("JTextField.showClearButton", true); // Quick clear 'X' icon

            GSCheckIn.setText("");
            GSCheckIn.putClientProperty("JComponent.roundRect", true);
            GSCheckIn.putClientProperty("JTextField.placeholderText", "e.g., Grade 12 - ICT");

            LRNField.setText("");
            LRNField.putClientProperty("JComponent.roundRect", true);
            LRNField.putClientProperty("JTextField.placeholderText", "e.g., 10940900001");
            
            // --- Search Bar Styling ---
            SearchField.putClientProperty("JComponent.roundRect", true);
            SearchField.putClientProperty("JTextField.placeholderText", "Search name, LRN, reason...");
            SearchField.putClientProperty("JTextField.showClearButton", true);
            

            // Live search: filter as the user types
            SearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e)  { applySearchFilter(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { applySearchFilter(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e){ applySearchFilter(); }
});
            
            

            // --- 4. Round Modern Component Elements ---
            CheckInBTN.putClientProperty("JButton.buttonType", "roundRect");
            EditBTN.putClientProperty("JButton.buttonType", "roundRect");
            ClearBTN.putClientProperty("JButton.buttonType", "roundRect");
            jButton1.putClientProperty("JButton.buttonType", "roundRect"); // Admin button
            jComboBox1.putClientProperty("JComponent.roundRect", true);
            SentHomeBTN.putClientProperty("JButton.buttonType", "roundRect");

            ThemeToggle.setText(null);
            ThemeToggle.setIcon(createMoonIcon());
            ThemeToggle.setToolTipText("Switch to Dark Mode");
            ThemeToggle.addActionListener(e -> {
                darkMode = ThemeToggle.isSelected();
                applyTheme();
            });

            // Apply the starting light theme
            applyTheme();
            refreshInventoryStatusDisplay();

        }
        
        private void setupSessionTimeoutMonitoring() {
            lastActivityTime = System.currentTimeMillis();

            activityListener = e -> lastActivityTime = System.currentTimeMillis();
            Toolkit.getDefaultToolkit().addAWTEventListener(activityListener,
                    AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);

            // Checks every 15 seconds - fine-grained enough even for the 5-minute test setting
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

     private void applyMigLayouts() {
    // ================= HEADER =================
        HeaderPanel.removeAll();
        HeaderPanel.setLayout(new MigLayout(
            "fillx, aligny center, insets 12 18", 
            "[][grow, center][]", ""));
        HeaderPanel.add(DateTimeLabel, "aligny center");            
        HeaderPanel.add(jLabel3, "alignx center");                  

        // CHANGE THIS LINE: Added "w 50!" to force width to 50px and prevent shrinking
        // Also added "gapleft 20" to separate it from the title
        HeaderPanel.add(ThemeToggle, "split 3, aligny center, gapleft 20, w 50!"); 

        HeaderPanel.add(jButton1, "gapleft 10");
        HeaderPanel.add(Logout, "gapleft 10");

    // ================= LEFT SIDEBAR (FORM) =================
    CheckInPanel.removeAll();
    CheckInPanel.setLayout(new MigLayout(
        "wrap 1, fillx, insets 20", 
        "[fill, grow]", ""));
    CheckInPanel.add(StudentCheckinLabel, "alignx center, gapbottom 15");
    CheckInPanel.add(jLabel7);
    CheckInPanel.add(NameCheckIn);
    CheckInPanel.add(jLabel8, "gaptop 8");
    CheckInPanel.add(GSCheckIn);
    CheckInPanel.add(LRNLabel, "gaptop 8");
    CheckInPanel.add(LRNField);
    CheckInPanel.add(jLabel9, "gaptop 8");
    CheckInPanel.add(jScrollPane1, "h 170!"); 
    CheckInPanel.add(jLabel11, "split 2, gaptop 10");
    CheckInPanel.add(jComboBox1, "w 140!");
    // Push buttons to the bottom
    CheckInPanel.add(CheckInBTN,  "growx, pushy, aligny bottom, gaptop 20 ");
    CheckInPanel.add(SentHomeBTN, "growx, gaptop 8 ");
    CheckInPanel.add(EditBTN,     "split 2, growx, gaptop 8 ");  // Edit + Clear share this row
    CheckInPanel.add(ClearBTN,    "growx ");                     // sits inline with Edit
    

    // ================= STAT CARDS (VISITS & SENT HOME) =================
    // Configure them directly (No CounterPanel wrapper)
   jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    VisitCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    SentHomeFooterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    SentHomeCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

    VisitPanel.removeAll();
    VisitPanel.setLayout(new MigLayout("wrap 1, fillx, insets 10", "[grow, fill]"));
    VisitPanel.add(jLabel1,      "growx, pushy, aligny bottom");   // space goes ABOVE the label
    VisitPanel.add(VisitCounter, "growx, pushy, aligny top, gaptop 4"); // number sticks right below it

    SentHomePanel.removeAll();
    SentHomePanel.setLayout(new MigLayout("wrap 1, fillx, insets 10", "[grow, fill]"));
    SentHomePanel.add(SentHomeFooterLabel, "growx, pushy, aligny bottom");
    SentHomePanel.add(SentHomeCount,       "growx, pushy, aligny top, gaptop 4");

    // ================= INVENTORY (TOP RIGHT) =================
    InventoryPanel.removeAll();
    InventoryPanel.setLayout(new MigLayout("fill, insets 5", "[grow, fill]", "[grow, fill]"));
    InventoryPanel.add(jScrollPane3, "grow");

    // ================= LOGS TABLE (BOTTOM) =================
    CheckInPanel1.removeAll();
    CheckInPanel1.setLayout(new MigLayout("fill, insets 10", "[grow, fill]", "[grow, fill]"));
    CheckInPanel1.add(jScrollPane2, "grow");

    // ================= MAIN LAYOUT ASSEMBLY =================
    MainPanel.removeAll();
    
    MainPanel.setLayout(new MigLayout(
        "fill, insets 10, gapx 12, gapy 6",
        "[400, fill][grow, fill][450, fill]", 
        "[][][220!][][grow, fill]")); 

    // Row 1: Header (Full Width)
    MainPanel.add(HeaderPanel, "spanx 3, growx, wrap");

    // Row 2-5: Left Sidebar (Spans all rows below header)
    MainPanel.add(CheckInPanel, "spany 4, grow");

    // Row 2: Section Labels
    MainPanel.add(OverviewLabel, "alignx center");          
    MainPanel.add(InventoryLabel, "alignx center, wrap");   

    // Row 3: The Panels (VisitPanel + SentHomePanel | InventoryPanel)
    // We add VisitPanel and SentHomePanel directly here using split 2
    MainPanel.add(VisitPanel, "split 2, grow");             
    MainPanel.add(SentHomePanel, "grow");                   
    MainPanel.add(InventoryPanel, "grow, wrap");

    // Row 4: Logs Label (Spans Cols 2 & 3)
    MainPanel.add(LogsLabel,   "alignx center");
    MainPanel.add(SearchField, "alignx right, gapleft push, w 220!, h 34!, wrap");

    // Row 5: The Table (Spans Cols 2 & 3, Grows to fill height)
    MainPanel.add(CheckInPanel1, "spanx 2, grow, wrap");

    MainPanel.revalidate();
    MainPanel.repaint();
    
    
}
     // ===== SVG ICONS (FlatLaf Extras) =====
// ===== SVG ICONS (FlatLaf Extras) =====
private javax.swing.Icon createSunIcon() {
    return new javax.swing.Icon() {
        public int getIconWidth() { return 22; }
        public int getIconHeight() { return 22; }
        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(java.awt.Color.BLACK);
            // Center circle
            g2.fillOval(x + 7, y + 7, 8, 8);
            // Rays
            g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 8; i++) {
                double a = Math.PI / 4 * i;
                g2.drawLine(x + 11 + (int) Math.round(7 * Math.cos(a)),
                            y + 11 + (int) Math.round(7 * Math.sin(a)),
                            x + 11 + (int) Math.round(10 * Math.cos(a)),
                            y + 11 + (int) Math.round(10 * Math.sin(a)));
            }
            g2.dispose();
        }
    };
}

private javax.swing.Icon createMoonIcon() {
    return new javax.swing.Icon() {
        public int getIconWidth() { return 22; }
        public int getIconHeight() { return 22; }
        public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw a crescent moon by subtracting one circle from another
            java.awt.geom.Area moon = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(x + 4, y + 3, 14, 16));
            moon.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(x + 9, y + 5, 12, 12)));
            g2.setColor(java.awt.Color.BLACK);
            g2.fill(moon);
            g2.dispose();
        }
    };
}
private void applyTheme() {
    try {
        if (darkMode) {
            com.formdev.flatlaf.FlatDarkLaf.setup();
        } else {
            com.formdev.flatlaf.FlatLightLaf.setup();
        }

        // --- COLOR PALETTE ---
        java.awt.Color pageBackground = darkMode ? new java.awt.Color(43, 43, 43) : java.awt.Color.WHITE;
        java.awt.Color panelBackground = darkMode ? new java.awt.Color(60, 63, 65) : new java.awt.Color(245, 247, 250);
        java.awt.Color cardBackground = darkMode ? new java.awt.Color(70, 73, 75) : java.awt.Color.WHITE;
        java.awt.Color inputBackground = darkMode ? new java.awt.Color(48, 50, 52) : java.awt.Color.WHITE;
        java.awt.Color textColor = darkMode ? new java.awt.Color(235, 235, 235) : new java.awt.Color(25, 25, 25);
        java.awt.Color headerColor = darkMode ? new java.awt.Color(30, 76, 120) : new java.awt.Color(51, 153, 255);
        java.awt.Color borderColor = darkMode ? new java.awt.Color(100, 104, 108) : new java.awt.Color(220, 225, 230);

        // --- HEADER BUTTONS (Reliable Styling) ---
        // We use standard methods for colors to ensure they stick and are readable
        javax.swing.JButton[] headerButtons = {jButton1, Logout};
        for (javax.swing.JButton btn : headerButtons) {
            btn.putClientProperty("JButton.buttonType", "roundRect");
            btn.putClientProperty("JComponent.arc", 15);
            btn.setBackground(java.awt.Color.WHITE);       // Always White
            btn.setForeground(java.awt.Color.BLACK);       // Always Black Text
            btn.setFont(btn.getFont().deriveFont(java.awt.Font.BOLD));
        }

        // Theme Toggle Button -> SVG icons
        ThemeToggle.setText(null);
        ThemeToggle.setIcon(darkMode ? createSunIcon() : createMoonIcon());
        ThemeToggle.setToolTipText(darkMode ? "Switch to Light Mode" : "Switch to Dark Mode");

        

        // 3. Styling (White Pill) - Keep this part as is
        ThemeToggle.putClientProperty("JButton.buttonType", "roundRect");
        ThemeToggle.putClientProperty("JComponent.arc", 15);
        ThemeToggle.setBackground(java.awt.Color.WHITE);
        ThemeToggle.setForeground(java.awt.Color.BLACK);
        // --- ACTION BUTTONS (Blue) ---
        javax.swing.JButton[] actionButtons = {CheckInBTN, EditBTN, SentHomeBTN, ClearBTN, };
        SearchField.setBackground(inputBackground); SearchField.setForeground(textColor);
        for (javax.swing.JButton btn : actionButtons) {
            btn.putClientProperty("JButton.buttonType", "roundRect");
            btn.putClientProperty("JComponent.arc", 10);
            btn.setBackground(new java.awt.Color(37, 99, 235)); // Modern Blue
            btn.setForeground(java.awt.Color.WHITE);
        }
        
        // Popup Buttons
        FinishBTN.putClientProperty("JButton.buttonType", "roundRect");
        FinishBTN.putClientProperty("JComponent.arc", 10);
        FinishBTN.setBackground(new java.awt.Color(37, 99, 235));
        FinishBTN.setForeground(java.awt.Color.WHITE);
        
        InformationBackBTN.putClientProperty("JButton.buttonType", "roundRect");
        InformationBackBTN.putClientProperty("JComponent.arc", 10);
        InformationBackBTN.setBackground(new java.awt.Color(153, 0, 0));
        InformationBackBTN.setForeground(java.awt.Color.WHITE);

        // --- APPLY COLORS TO COMPONENTS ---
        
        ThemeToggle.setSelected(darkMode);
        
        HeaderPanel.setBackground(headerColor);
        MainPanel.setBackground(pageBackground);
        CheckInPanel.setBackground(panelBackground);
        CheckInPanel1.setBackground(panelBackground);
        InventoryPanel.setBackground(panelBackground);
        VisitPanel.setBackground(cardBackground);
        SentHomePanel.setBackground(cardBackground);
        SentHomeInformationPanel.setBackground(cardBackground);

        // Borders
        javax.swing.border.Border softBorder = javax.swing.BorderFactory.createLineBorder(borderColor, 1, true);
        javax.swing.border.Border paddedBorder = javax.swing.BorderFactory.createCompoundBorder(
            softBorder, javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CheckInPanel.setBorder(paddedBorder);
        InventoryPanel.setBorder(paddedBorder);
        CheckInPanel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(softBorder, javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        VisitPanel.setBorder(softBorder);
        SentHomePanel.setBorder(softBorder);

        // Popup Panel Border
        javax.swing.border.TitledBorder titledBorder = javax.swing.BorderFactory.createTitledBorder(
            softBorder, "Parent/Guardian Information", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14), textColor);
        SentHomeInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(titledBorder, javax.swing.BorderFactory.createEmptyBorder(10, 15, 15, 15)));

        // Text Colors
        jLabel3.setForeground(java.awt.Color.WHITE);
        DateTimeLabel.setForeground(java.awt.Color.WHITE);
        InventoryLabel.setForeground(textColor);
        OverviewLabel.setForeground(textColor);
        LogsLabel.setForeground(textColor);
        StudentCheckinLabel.setForeground(textColor);
        jLabel1.setForeground(textColor);
        SentHomeFooterLabel.setForeground(textColor);
        VisitCounter.setForeground(textColor);
        SentHomeCount.setForeground(textColor);
        jLabel7.setForeground(textColor);
        jLabel8.setForeground(textColor);
        LRNLabel.setForeground(textColor);
        jLabel9.setForeground(textColor);
        jLabel11.setForeground(textColor);
        PGNameLabel.setForeground(textColor);
        PNField.setForeground(textColor);

        // Inputs
        ParentGurdianName.setBackground(inputBackground); ParentGurdianName.setForeground(textColor);
        PhoneField.setBackground(inputBackground); PhoneField.setForeground(textColor);
        NameCheckIn.setBackground(inputBackground); NameCheckIn.setForeground(textColor);
        LRNField.setBackground(inputBackground); LRNField.setForeground(textColor);
        GSCheckIn.setBackground(inputBackground); GSCheckIn.setForeground(textColor);
        ReasonArea.setBackground(inputBackground); ReasonArea.setForeground(textColor);
        InventoryStatusArea.setBackground(inputBackground); InventoryStatusArea.setForeground(textColor);
        jComboBox1.setBackground(inputBackground); jComboBox1.setForeground(textColor);

        // Table
        ReasonTable.setBackground(inputBackground);
        ReasonTable.setForeground(textColor);
        ReasonTable.getTableHeader().setBackground(cardBackground);
        ReasonTable.getTableHeader().setForeground(textColor);
        
        jScrollPane1.getViewport().setBackground(inputBackground);
        jScrollPane2.getViewport().setBackground(inputBackground);
        jScrollPane3.getViewport().setBackground(inputBackground);
        
        jScrollPane1.setBorder(softBorder);
        jScrollPane2.setBorder(softBorder);
        jScrollPane3.setBorder(softBorder);

        // --- FIX WINDOW RESIZING ---
        java.awt.Dimension currentSize = this.getSize();
        java.awt.Point currentLocation = this.getLocation();
        int currentState = this.getExtendedState();

        for (java.awt.Window window : java.awt.Window.getWindows()) {
            javax.swing.SwingUtilities.updateComponentTreeUI(window);
            // Removed window.pack() to prevent resizing
        }

        if (currentState == this.MAXIMIZED_BOTH) {
            this.setExtendedState(this.MAXIMIZED_BOTH);
        } else {
            this.setSize(currentSize);
            this.setLocation(currentLocation);
        }

    } catch (Exception ex) {
        logger.log(java.util.logging.Level.SEVERE, "Failed to apply theme", ex);
    }
}

    
    
    private void startDateTimeClock() {
    updateDateTimeLabel();

    new javax.swing.Timer(1000, e -> updateDateTimeLabel()).start();
}

private void updateDateTimeLabel() {
    java.time.format.DateTimeFormatter format =
        java.time.format.DateTimeFormatter.ofPattern(
            "EEEE, MMMM d, yyyy  |  hh:mm:ss a"
        );

    DateTimeLabel.setText(
        java.time.LocalDateTime.now().format(format)
    );
}
public class GlassOverlayPanel extends javax.swing.JPanel {
    public GlassOverlayPanel() {
        setOpaque(false); // Allows underlying content to render
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dark semi-transparent color (Adjust 120 alpha for darker/lighter effect)
        g2.setColor(new java.awt.Color(0, 0, 0, 120)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        g2.dispose();
        super.paintComponent(g);
    }
}
/** Filters the Check-in Logs table by whatever is in SearchField. Returns the number of matching rows.
 *  Only Name, LRN, and Reason are searchable - by design, not by omission. */
private int applySearchFilter() {
    String query = SearchField.getText().trim().toLowerCase();
    DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
    model.setRowCount(0);
    int matchCount = 0;
    for (CheckinSystem v : currentVisits) {
        if (query.isEmpty()
                || v.getName().toLowerCase().contains(query)
                || v.getLrn().toLowerCase().contains(query)
                || v.getReason().toLowerCase().contains(query)) {
            model.addRow(new Object[]{
                v.getStatus(), v.getName(), v.getGradeSection(), v.getLrn(),
                v.getMedicineDisplay(), v.getReason(),
                v.getGuardianName(), v.getGuardianPhoneNums()
            });
            matchCount++;
        }
    }
    return matchCount;
}
 
   //ComboBox problem 
    private void medicineBox(){
       DatabaseExecutor.run(
        () -> productService.loadAll(),
        medicine -> {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Medicine p : medicine) {
                model.addElement(p.getname());
            }
            jComboBox1.setModel(model);
        },
        ex -> JOptionPane.showMessageDialog(this, "Error loading medicine list: " + ex.getMessage())
    );
 }


    //ps this will help display the inventory on the "inventory status"
    private VisitData visitService = new VisitData();
    private MedicineData productService = new MedicineData("inventory_activity.log");
    
    // Tracks the last alert content we've already shown, so refreshing the
    // dashboard doesn't keep re-popping the same banner on every call.
     private String lastAlertSignature = "";

    /**
     * Single entry point for the inventory panel: reloads medicines, redraws
     * InventoryStatusArea, and checks low stock + expiration in one pass
     * using the real exp_date column. Safe to call after any inventory
     * change (add/edit/delete/restock/use) or a plain UI refresh — a banner
     * only pops when the alert contents actually changed since last time.
     */
    private void refreshInventoryStatusDisplay(){
        DatabaseExecutor.run(
            () -> productService.loadAll(),
            medicine -> {
                StringBuilder sb = new StringBuilder();
                StringBuilder lowStockNames = new StringBuilder();
                StringBuilder expiredNames = new StringBuilder();
                StringBuilder expiringTodayNames = new StringBuilder();
                int lowStockCount = 0;

                LocalDate today = LocalDate.now();
                java.time.format.DateTimeFormatter fmt =
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

                if (medicine.isEmpty()) {
                    sb.append("No items in inventory yet.");
                } else {
                    for (Medicine p : medicine) {
                        sb.append(p.getname())
                          .append(" — ")
                          .append(p.getquantity())
                          .append(" pcs — ")
                          .append(p.getStatus());

                        if (p.isLowStock()) {
                            sb.append(" --LOW STOCK-- ");
                            lowStockCount++;
                            if (lowStockNames.length() > 0) lowStockNames.append(", ");
                            lowStockNames.append(p.getname());
                        }

                        // Use the real exp_date column, not the getStatus() string.
                        LocalDate expDate = null;
                        try {
                            if (p.getExpDate() != null && !p.getExpDate().isBlank()) {
                                expDate = LocalDate.parse(p.getExpDate().trim(), fmt);
                            }
                        } catch (Exception ignored) {
                            // exp_date isn't in yyyy-MM-dd format — skip expiry check for this item
                        }

                        if (expDate != null) {
                            if (expDate.isBefore(today)) {
                                sb.append(" --EXPIRED-- ");
                                if (expiredNames.length() > 0) expiredNames.append(", ");
                                expiredNames.append(p.getname()).append(" (Exp: ").append(p.getExpDate()).append(")");
                            } else if (expDate.isEqual(today)) {
                                sb.append(" --EXPIRES TODAY-- ");
                                if (expiringTodayNames.length() > 0) expiringTodayNames.append(", ");
                                expiringTodayNames.append(p.getname());
                            }
                        }

                        sb.append("\n");
                    }
                }

                InventoryStatusArea.setText(sb.toString());

                // Build ONE combined banner covering low stock + expired + expiring
                // today, and only pop it when the content actually changed. Calling
                // showTopAlertBanner() more than once per refresh stacks banners on
                // top of each other at the same spot, hiding all but the last one —
                // so everything gets folded into a single message instead.
                String lowStockSignature = lowStockNames.toString();
                String expirySignature = expiredNames + "|" + expiringTodayNames;
                String combinedSignature = lowStockSignature + "||" + expirySignature;

                boolean hasAlerts = !lowStockSignature.isEmpty()
                        || expiredNames.length() > 0
                        || expiringTodayNames.length() > 0;

                if (hasAlerts && !combinedSignature.equals(lastAlertSignature)) {
                    StringBuilder banner = new StringBuilder();

                    if (!lowStockSignature.isEmpty()) {
                        banner.append("Low on stock (").append(lowStockCount)
                              .append("): ").append(lowStockSignature);
                    }
                    if (expiredNames.length() > 0) {
                        if (banner.length() > 0) banner.append("   |   ");
                        banner.append("⚠️ EXPIRED: ").append(expiredNames);
                    }
                    if (expiringTodayNames.length() > 0) {
                        if (banner.length() > 0) banner.append("   |   ");
                        banner.append("⚠️ Expiring today: ").append(expiringTodayNames);
                    }

                    showTopAlertBanner(banner.toString());
                }
                lastAlertSignature = combinedSignature;
            },
            ex -> JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage())
        );
    }
    
        
    private ArrayList<CheckinSystem> currentVisits = new ArrayList<>();
    private String selectedVisitLrn = null;
    private record DashboardCounts(ArrayList<CheckinSystem> visits, int[] counts) {}
    
    //pinapakita yun table and counters and inaupdate
    private void refreshTableAndCounters(){
    final long requestGeneration = ++tableRefreshGeneration;

    DatabaseExecutor.run(
        () -> new DashboardCounts(
            visitService.loadActive(),
            visitService.getTodayCounts()
        ),
        result -> {
            if (requestGeneration != tableRefreshGeneration) {
                return;
            }

            currentVisits = result.visits();
            applySearchFilter();

            int[] counts = result.counts();
            VisitCounter.setText(String.valueOf(counts[0]));
            SentHomeCount.setText(String.valueOf(counts[1]));
        },
        ex -> {
            if (requestGeneration == tableRefreshGeneration) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    );
}
    
 private void showToast(javax.swing.JPanel parentContainer, String message, boolean isSuccess) {
    // 1. Create and style the toast label
    javax.swing.JLabel toast = new javax.swing.JLabel(message, javax.swing.SwingConstants.CENTER);
    toast.setOpaque(true);
    
    if (isSuccess) {
        toast.setBackground(new java.awt.Color(16, 185, 129)); // Success Emerald Green
        toast.setForeground(java.awt.Color.WHITE);
    } else {
        toast.setBackground(new java.awt.Color(239, 68, 68));  // Error Coral Red
        toast.setForeground(java.awt.Color.WHITE);
    }
    
    toast.setFont(toast.getFont().deriveFont(java.awt.Font.BOLD, 13f));
    toast.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 12;");

    // 2. Position calculations relative to parent container
    int toastHeight = 36;
    int margin = 15;
    int toastWidth = parentContainer.getWidth() - (margin * 2);

    // Convert coordinates to window root pane layer
    java.awt.Point locationOnScreen = parentContainer.getLocationOnScreen();
    java.awt.Point frameLocation = this.getLocationOnScreen();
    
    int relativeX = locationOnScreen.x - frameLocation.x + margin;
    int startY = (locationOnScreen.y - frameLocation.y) + parentContainer.getHeight();
    int targetY = startY - toastHeight - margin;

    toast.setBounds(relativeX, startY, toastWidth, toastHeight);

    // 3. Add to JFrame's LayeredPane (DRAG_LAYER sits on top of all UI panels)
    javax.swing.JLayeredPane layeredPane = this.getLayeredPane();
    layeredPane.add(toast, javax.swing.JLayeredPane.DRAG_LAYER);
    layeredPane.revalidate();
    layeredPane.repaint();

    // 4. Slide UP Animation Timer
    javax.swing.Timer slideUpTimer = new javax.swing.Timer(10, null);
    slideUpTimer.addActionListener(new java.awt.event.ActionListener() {
        int currentY = startY;

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (currentY > targetY) {
                currentY = Math.max(targetY, currentY - 4);
                toast.setLocation(relativeX, currentY);
                layeredPane.repaint(); // Force refresh every step
            } else {
                slideUpTimer.stop();

                // 5. Hold and Slide DOWN
                javax.swing.Timer delayTimer = new javax.swing.Timer(2500, evt -> {
                    javax.swing.Timer slideDownTimer = new javax.swing.Timer(10, null);
                    slideDownTimer.addActionListener(new java.awt.event.ActionListener() {
                        int returnY = targetY;

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            if (returnY < startY) {
                                returnY += 4;
                                toast.setLocation(relativeX, returnY);
                                layeredPane.repaint();
                            } else {
                                slideDownTimer.stop();
                                layeredPane.remove(toast);
                                layeredPane.repaint();
                            }
                        }
                    });
                    slideDownTimer.start();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }
    });
    
    slideUpTimer.start();
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

        SentHomeInformationPanel = new javax.swing.JPanel();
        ParentGurdianName = new javax.swing.JTextField();
        PGNameLabel = new javax.swing.JLabel();
        PNField = new javax.swing.JLabel();
        PhoneField = new javax.swing.JTextField();
        FinishBTN = new javax.swing.JButton();
        InformationBackBTN = new javax.swing.JButton();
        MainPanel = new javax.swing.JPanel();
        HeaderPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        DateTimeLabel = new javax.swing.JLabel();
        Logout = new javax.swing.JButton();
        ThemeToggle = new javax.swing.JToggleButton();
        CheckInPanel = new javax.swing.JPanel();
        NameCheckIn = new javax.swing.JTextField();
        GSCheckIn = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        ReasonArea = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        CheckInBTN = new javax.swing.JButton();
        EditBTN = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        SentHomeBTN = new javax.swing.JButton();
        LRNLabel = new javax.swing.JLabel();
        LRNField = new javax.swing.JTextField();
        StudentCheckinLabel = new javax.swing.JLabel();
        ClearBTN = new javax.swing.JButton();
        InventoryPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        InventoryStatusArea = new javax.swing.JTextArea();
        InventoryLabel = new javax.swing.JLabel();
        CheckInPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        OverviewLabel = new javax.swing.JLabel();
        LogsLabel = new javax.swing.JLabel();
        SentHomePanel = new javax.swing.JPanel();
        SentHomeFooterLabel = new javax.swing.JLabel();
        SentHomeCount = new javax.swing.JLabel();
        VisitPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        VisitCounter = new javax.swing.JLabel();
        SearchField = new javax.swing.JTextField();
        SearchLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);

        SentHomeInformationPanel.setBackground(new java.awt.Color(255, 255, 255));
        SentHomeInformationPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder(null, "Parent/Guardian Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic UI", 1, 18), new java.awt.Color(0, 0, 0)))); // NOI18N
        SentHomeInformationPanel.setForeground(new java.awt.Color(0, 0, 0));

        ParentGurdianName.setBackground(new java.awt.Color(227, 226, 226));
        ParentGurdianName.setForeground(new java.awt.Color(0, 0, 0));

        PGNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        PGNameLabel.setForeground(new java.awt.Color(0, 0, 0));
        PGNameLabel.setText("Name :");

        PNField.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        PNField.setForeground(new java.awt.Color(0, 0, 0));
        PNField.setText("Phone Number :");

        PhoneField.setBackground(new java.awt.Color(227, 226, 226));
        PhoneField.setForeground(new java.awt.Color(0, 0, 0));

        FinishBTN.setBackground(new java.awt.Color(0, 102, 204));
        FinishBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        FinishBTN.setForeground(new java.awt.Color(255, 255, 255));
        FinishBTN.setText("Finish");
        FinishBTN.addActionListener(this::FinishBTNActionPerformed);

        InformationBackBTN.setBackground(new java.awt.Color(153, 0, 0));
        InformationBackBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        InformationBackBTN.setForeground(new java.awt.Color(255, 255, 255));
        InformationBackBTN.setText("Back");
        InformationBackBTN.addActionListener(this::InformationBackBTNActionPerformed);

        javax.swing.GroupLayout SentHomeInformationPanelLayout = new javax.swing.GroupLayout(SentHomeInformationPanel);
        SentHomeInformationPanel.setLayout(SentHomeInformationPanelLayout);
        SentHomeInformationPanelLayout.setHorizontalGroup(
            SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                .addGroup(SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                        .addGap(99, 99, 99)
                        .addGroup(SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                                .addComponent(PNField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                                .addComponent(PGNameLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ParentGurdianName, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                        .addGap(266, 266, 266)
                        .addGroup(SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(InformationBackBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(FinishBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(112, Short.MAX_VALUE))
        );
        SentHomeInformationPanelLayout.setVerticalGroup(
            SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentHomeInformationPanelLayout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addGroup(SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ParentGurdianName, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PGNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PhoneField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PNField))
                .addGap(18, 18, 18)
                .addComponent(FinishBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InformationBackBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));

        HeaderPanel.setBackground(new java.awt.Color(51, 153, 255));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Clinic Dashboard");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Admin Panel");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        DateTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N

        Logout.setBackground(new java.awt.Color(255, 255, 255));
        Logout.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        Logout.setForeground(new java.awt.Color(0, 0, 0));
        Logout.setText("Logout");
        Logout.addActionListener(this::LogoutActionPerformed);

        ThemeToggle.setBackground(new java.awt.Color(255, 255, 255));
        ThemeToggle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThemeToggle.setForeground(new java.awt.Color(0, 0, 0));
        ThemeToggle.setText("Mode");
        ThemeToggle.addActionListener(this::ThemeToggleActionPerformed);

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(155, 155, 155)
                .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Logout)
                .addGap(30, 30, 30))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)))
                .addGap(0, 23, Short.MAX_VALUE))
        );

        CheckInPanel.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        NameCheckIn.setBackground(new java.awt.Color(255, 255, 255));
        NameCheckIn.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        NameCheckIn.setForeground(new java.awt.Color(0, 0, 0));
        NameCheckIn.setText("Name");

        GSCheckIn.setBackground(new java.awt.Color(255, 255, 255));
        GSCheckIn.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        GSCheckIn.setForeground(new java.awt.Color(0, 0, 0));
        GSCheckIn.setText("Grade/Section");

        ReasonArea.setBackground(new java.awt.Color(255, 255, 255));
        ReasonArea.setColumns(20);
        ReasonArea.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        ReasonArea.setForeground(new java.awt.Color(0, 0, 0));
        ReasonArea.setRows(5);
        jScrollPane1.setViewportView(ReasonArea);

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Name");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Grade/Section");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Reason for Visit");

        CheckInBTN.setBackground(new java.awt.Color(0, 102, 204));
        CheckInBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        CheckInBTN.setForeground(new java.awt.Color(255, 255, 255));
        CheckInBTN.setText("Check In");
        CheckInBTN.addActionListener(this::CheckInBTNActionPerformed);

        EditBTN.setBackground(new java.awt.Color(0, 102, 204));
        EditBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        EditBTN.setForeground(new java.awt.Color(255, 255, 255));
        EditBTN.setText("Edit");
        EditBTN.addActionListener(this::EditBTNActionPerformed);

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Medicine used");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        SentHomeBTN.setBackground(new java.awt.Color(0, 102, 204));
        SentHomeBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        SentHomeBTN.setForeground(new java.awt.Color(255, 255, 255));
        SentHomeBTN.setText("Sent Home/Back");
        SentHomeBTN.addActionListener(this::SentHomeBTNActionPerformed);

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        LRNLabel.setForeground(new java.awt.Color(0, 0, 0));
        LRNLabel.setText("LRN");

        LRNField.setBackground(new java.awt.Color(255, 255, 255));
        LRNField.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        LRNField.setForeground(new java.awt.Color(0, 0, 0));
        LRNField.setText("LRN");
        LRNField.addActionListener(this::LRNFieldActionPerformed);
        LRNField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                LRNFieldKeyTyped(evt);
            }
        });

        StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        StudentCheckinLabel.setForeground(new java.awt.Color(0, 0, 0));
        StudentCheckinLabel.setText("Student Check-in");

        ClearBTN.setBackground(new java.awt.Color(0, 102, 204));
        ClearBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        ClearBTN.setForeground(new java.awt.Color(255, 255, 255));
        ClearBTN.setText("Clear");
        ClearBTN.addActionListener(this::ClearBTNActionPerformed);

        javax.swing.GroupLayout CheckInPanelLayout = new javax.swing.GroupLayout(CheckInPanel);
        CheckInPanel.setLayout(CheckInPanelLayout);
        CheckInPanelLayout.setHorizontalGroup(
            CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGap(142, 142, 142)
                .addComponent(StudentCheckinLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CheckInPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CheckInBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(EditBTN, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(SentHomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(CheckInPanelLayout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(NameCheckIn)
                                .addGroup(CheckInPanelLayout.createSequentialGroup()
                                    .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addComponent(jLabel7))
                                    .addGap(288, 288, 288)))
                            .addComponent(GSCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LRNField, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 37, Short.MAX_VALUE))
                    .addGroup(CheckInPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ClearBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        CheckInPanelLayout.setVerticalGroup(
            CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NameCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GSCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(EditBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ClearBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        InventoryPanel.setBackground(new java.awt.Color(226, 226, 226));
        InventoryPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        InventoryPanel.setForeground(new java.awt.Color(0, 0, 0));

        InventoryStatusArea.setBackground(new java.awt.Color(255, 255, 255));
        InventoryStatusArea.setColumns(20);
        InventoryStatusArea.setForeground(new java.awt.Color(0, 0, 0));
        InventoryStatusArea.setRows(5);
        jScrollPane3.setViewportView(InventoryStatusArea);

        javax.swing.GroupLayout InventoryPanelLayout = new javax.swing.GroupLayout(InventoryPanel);
        InventoryPanel.setLayout(InventoryPanelLayout);
        InventoryPanelLayout.setHorizontalGroup(
            InventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 493, Short.MAX_VALUE)
                .addContainerGap())
        );
        InventoryPanelLayout.setVerticalGroup(
            InventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InventoryPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        InventoryLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        InventoryLabel.setForeground(new java.awt.Color(0, 0, 0));
        InventoryLabel.setText("Inventory Status");

        CheckInPanel1.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Status", "Name", "Grade/Section", "LRN", "Medecine/Pills Used", "Reason", "Parent/Guardian Name", "Phone number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
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
            ReasonTable.getColumnModel().getColumn(5).setResizable(false);
            ReasonTable.getColumnModel().getColumn(6).setResizable(false);
            ReasonTable.getColumnModel().getColumn(7).setResizable(false);
        }

        javax.swing.GroupLayout CheckInPanel1Layout = new javax.swing.GroupLayout(CheckInPanel1);
        CheckInPanel1.setLayout(CheckInPanel1Layout);
        CheckInPanel1Layout.setHorizontalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1216, Short.MAX_VALUE)
                .addContainerGap())
        );
        CheckInPanel1Layout.setVerticalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        OverviewLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        OverviewLabel.setForeground(new java.awt.Color(0, 0, 0));
        OverviewLabel.setText("Overview");

        LogsLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        LogsLabel.setForeground(new java.awt.Color(0, 0, 0));
        LogsLabel.setText("Check-in Logs");

        SentHomePanel.setBackground(new java.awt.Color(255, 255, 255));

        SentHomeFooterLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        SentHomeFooterLabel.setForeground(new java.awt.Color(0, 0, 0));
        SentHomeFooterLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SentHomeFooterLabel.setText("Sent Home");

        SentHomeCount.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        SentHomeCount.setForeground(new java.awt.Color(0, 0, 0));
        SentHomeCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SentHomeCount.setText("0");

        javax.swing.GroupLayout SentHomePanelLayout = new javax.swing.GroupLayout(SentHomePanel);
        SentHomePanel.setLayout(SentHomePanelLayout);
        SentHomePanelLayout.setHorizontalGroup(
            SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentHomePanelLayout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SentHomeFooterLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentHomePanelLayout.createSequentialGroup()
                        .addComponent(SentHomeCount, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGap(44, 44, 44))
        );
        SentHomePanelLayout.setVerticalGroup(
            SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentHomePanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(SentHomeFooterLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SentHomeCount, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        VisitPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Today's Visits");

        VisitCounter.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        VisitCounter.setForeground(new java.awt.Color(0, 0, 0));
        VisitCounter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        VisitCounter.setText("0");

        javax.swing.GroupLayout VisitPanelLayout = new javax.swing.GroupLayout(VisitPanel);
        VisitPanel.setLayout(VisitPanelLayout);
        VisitPanelLayout.setHorizontalGroup(
            VisitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VisitPanelLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(VisitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(VisitPanelLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(VisitCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        VisitPanelLayout.setVerticalGroup(
            VisitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VisitPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VisitCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        SearchLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        SearchLabel.setForeground(new java.awt.Color(0, 0, 0));
        SearchLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SearchLabel.setText("Search:");

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(285, 285, 285)
                                .addComponent(OverviewLabel))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addComponent(VisitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(SentHomePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                                .addComponent(InventoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                                .addComponent(InventoryLabel)
                                .addGap(210, 210, 210))))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(LogsLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(81, 81, 81))
                            .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(28, Short.MAX_VALUE))))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(HeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(OverviewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(InventoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SentHomePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(VisitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(54, 54, 54)
                                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(LogsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(SearchLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(InventoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 67, Short.MAX_VALUE)))
                        .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(438, 438, 438)
                    .addComponent(SentHomeInformationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(649, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(251, 251, 251)
                    .addComponent(SentHomeInformationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(211, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
           if (loggedInAccount == null || !loggedInAccount.canAccessAdminPanel()) {

        showToast(
                MainPanel,
                "Access denied. Admin role required.",
                false
        );

        return;
    }

        new AdminPanel(loggedInAccount).setVisible(true);
        this.dispose(); 
    }//GEN-LAST:event_jButton1ActionPerformed

    
    //edit btn
    private void EditBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditBTNActionPerformed
            // ---------------------------------------------------------
            // SELECTION / STATUS GUARD
            // Do not rely only on JTable's selection mode - re-verify here,
            // right before the edit actually runs.
            // ---------------------------------------------------------
            int selectedRowCount = ReasonTable.getSelectedRowCount();

            if (selectedRowCount == 0 || selectedVisitLrn == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a student to edit.",
                        "No Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (selectedRowCount > 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select only one student to edit.",
                        "Multiple Selection",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int editViewRow = ReasonTable.getSelectedRow();
            int editModelRow = ReasonTable.convertRowIndexToModel(editViewRow);
            DefaultTableModel reasonModel = (DefaultTableModel) ReasonTable.getModel();
            Object statusValue = reasonModel.getValueAt(editModelRow, 0);
            String currentStatus = (statusValue == null) ? "" : statusValue.toString();

            if (!"In Clinic".equals(currentStatus)) {
                JOptionPane.showMessageDialog(
                        this,
                        "This record can no longer be edited because its "
                        + "status is \"" + currentStatus + "\".",
                        "Cannot Edit",
                        JOptionPane.WARNING_MESSAGE
                );
                clearCheckInForm();
                return;
            }

      
         String newName = NameCheckIn.getText().trim();
         String newGradeSection = GSCheckIn.getText().trim();
         String newLrn = LRNField.getText().trim();
         String newReason = ReasonArea.getText().trim();

         // ---------------------------------------------------------
         // BASIC VALIDATION
         // ---------------------------------------------------------

         if (newName.isEmpty() || newGradeSection.isEmpty()) {
             JOptionPane.showMessageDialog(
                     this,
                     "Name and Grade/Section cannot be empty.",
                     "Invalid Information",
                     JOptionPane.WARNING_MESSAGE
             );
             return;
         }

         if (!newLrn.matches("\\d{12}")) {
             JOptionPane.showMessageDialog(
                     this,
                     "The LRN must contain exactly 12 digits.",
                     "Invalid LRN",
                     JOptionPane.WARNING_MESSAGE
             );
             LRNField.requestFocus();
             return;
         }

         // ---------------------------------------------------------
         // ASK WHETHER MEDICINE SHOULD BE CHANGED
         // ---------------------------------------------------------

         int wantsMedicineChange = JOptionPane.showConfirmDialog(
                 this,
                 "Does this student want to change their medicine?",
                 "Change Medicine?",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.QUESTION_MESSAGE
         );

         if (wantsMedicineChange == JOptionPane.CLOSED_OPTION) {
             return;
         }

         String newMedUsed;
         int newMedsQty;

         // ---------------------------------------------------------
         // NO MEDICINE CHANGE
         // ---------------------------------------------------------

         if (wantsMedicineChange == JOptionPane.NO_OPTION) {

             // Keep the existing medicine exactly as it is.
             newMedUsed = selectedOldMedUsed;
             newMedsQty = selectedOldMedsQty;

         } else {

             // -----------------------------------------------------
             // LOAD CURRENT MEDICINE INVENTORY
             // -----------------------------------------------------

             ArrayList<Medicine> allMedicine;

             try {
                 allMedicine = productService.loadAll();

             } catch (SQLException ex) {

                 JOptionPane.showMessageDialog(
                         this,
                         "Unable to load the medicine inventory.\n\n"
                         + ex.getMessage(),
                         "Medicine Inventory Error",
                         JOptionPane.ERROR_MESSAGE
                 );

                 return;
             }

             ArrayList<Medicine> selectable = new ArrayList<>();

             for (Medicine m : allMedicine) {

                 // Don't allow expired medicine.
                 if (m.isExpired()) {
                     continue;
                 }

                 // Don't allow medicine with zero stock.
                 if (m.getquantity() <= 0) {
                     continue;
                 }

                 selectable.add(m);
             }

             final String NONE_LABEL = "None (no medicine)";

             // -----------------------------------------------------
             // NO MEDICINE AVAILABLE
             // -----------------------------------------------------

             if (selectable.isEmpty()) {

                 int proceedNoMed = JOptionPane.showConfirmDialog(
                         this,
                         "There is no medicine currently available in inventory.\n"
                         + "Do you want to remove this student's medicine?",
                         "No Medicine Available",
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.WARNING_MESSAGE
                 );

                 if (proceedNoMed != JOptionPane.YES_OPTION) {
                     return;
                 }

                 newMedUsed = "None";
                 newMedsQty = 0;

             } else {

                 // -------------------------------------------------
                 // BUILD MEDICINE CHOICES
                 // -------------------------------------------------

                 String[] choices = new String[selectable.size() + 1];

                 choices[0] = NONE_LABEL;

                 for (int i = 0; i < selectable.size(); i++) {

                     Medicine m = selectable.get(i);

                     choices[i + 1] =
                             m.getname()
                             + " — "
                             + m.getquantity()
                             + " available";
                 }

                 Object picked = JOptionPane.showInputDialog(
                         this,
                         "Select the new medicine:",
                         "Change Medicine",
                         JOptionPane.PLAIN_MESSAGE,
                         null,
                         choices,
                         choices[0]
                 );

                 // User cancelled.
                 if (picked == null) {
                     return;
                 }

                 String pickedLabel = picked.toString();

                 // -------------------------------------------------
                 // REMOVE MEDICINE
                 // -------------------------------------------------

                 if (pickedLabel.equals(NONE_LABEL)) {

                     newMedUsed = "None";
                     newMedsQty = 0;

                 } else {

                     Medicine pickedMed = null;

                     for (int i = 0; i < selectable.size(); i++) {

                         if (choices[i + 1].equals(pickedLabel)) {

                             pickedMed = selectable.get(i);
                             break;
                         }
                     }

                     if (pickedMed == null) {

                         JOptionPane.showMessageDialog(
                                 this,
                                 "That medicine is no longer available.\n"
                                 + "Please try again.",
                                 "Medicine Unavailable",
                                 JOptionPane.WARNING_MESSAGE
                         );

                         return;
                     }

                     int available = pickedMed.getquantity();
                     int qty;

                     // -------------------------------------------------
                     // ASK FOR QUANTITY
                     // -------------------------------------------------

                     while (true) {

                         String qtyInput = JOptionPane.showInputDialog(
                                 this,
                                 "How many pills of "
                                 + pickedMed.getname()
                                 + "?\n"
                                 + available
                                 + " available.",
                                 "1"
                         );

                         if (qtyInput == null) {
                             return;
                         }

                         try {

                             qty = Integer.parseInt(qtyInput.trim());

                         } catch (NumberFormatException ex) {

                             JOptionPane.showMessageDialog(
                                     this,
                                     "Please enter a valid whole number.",
                                     "Invalid Quantity",
                                     JOptionPane.WARNING_MESSAGE
                             );

                             continue;
                         }

                         if (qty <= 0) {

                             JOptionPane.showMessageDialog(
                                     this,
                                     "Quantity must be at least 1.",
                                     "Invalid Quantity",
                                     JOptionPane.WARNING_MESSAGE
                             );

                             continue;
                         }

                         if (qty > available) {

                             JOptionPane.showMessageDialog(
                                     this,
                                     "Only "
                                     + available
                                     + " pill(s) are available.",
                                     "Insufficient Stock",
                                     JOptionPane.WARNING_MESSAGE
                             );

                             continue;
                         }

                         break;
                     }

                     newMedUsed = pickedMed.getname();
                     newMedsQty = qty;
                 }
             }
         }

         // ---------------------------------------------------------
         // GUARDIAN INFORMATION
         // ---------------------------------------------------------

         String[] guardianResult =
                 promptGuardianUpdate(
                         selectedGuardianName,
                         selectedGuardianPhone
                 );

         if (guardianResult == null) {
             return;
         }

         String newGuardianName = guardianResult[0];
         String newGuardianPhone = guardianResult[1];

         // ---------------------------------------------------------
         // PERFORM EDIT
         // ---------------------------------------------------------

         try {

             String actor =
                     (loggedInAccount != null)
                     ? loggedInAccount.GetName()
                     : "Unknown";

             /*
              * IMPORTANT:
              *
              * selectedVisitLrn = OLD LRN
              * newLrn          = NEW LRN
              *
              * The old LRN identifies the existing student.
              * The new LRN is the value that gets saved.
              */
             boolean success =
                     visitService.editVisitWithMedicineAdjustment(
                             selectedVisitLrn,
                             newName,
                             newGradeSection,
                             newLrn,
                             newReason,
                             selectedOldMedUsed,
                             selectedOldMedsQty,
                             newMedUsed,
                             newMedsQty,
                             newGuardianName,
                             newGuardianPhone,
                             productService,
                             actor,
                             wantsMedicineChange == JOptionPane.YES_OPTION
                     );

             // -----------------------------------------------------
             // EDIT FAILED
             // -----------------------------------------------------

             if (!success) {

                 JOptionPane.showMessageDialog(
                         this,
                         "The student record could not be found or updated.",
                         "Update Failed",
                         JOptionPane.ERROR_MESSAGE
                 );

                 return;
             }

             // -----------------------------------------------------
             // REFRESH ONLY AFTER SUCCESSFUL DATABASE OPERATION
             // -----------------------------------------------------

             refreshTableAndCounters();
             refreshInventoryStatusDisplay();

             /*
              * Check whether the database actually detected a change.
              */
             if (visitService.wasLastEditChanged()) {

                 clearCheckInForm();

                 JOptionPane.showMessageDialog(
                         this,
                         "Student record updated successfully.",
                         "Update Successful",
                         JOptionPane.INFORMATION_MESSAGE
                 );

             } else {

                 /*
                  * TRUE NO-OP:
                  *
                  * Nothing changed, therefore:
                  * - No database UPDATE
                  * - No EDIT audit log
                  * - No medicine adjustment
                  */
                 JOptionPane.showMessageDialog(
                         this,
                         "No changes were made to the student record.",
                         "No Changes Made",
                         JOptionPane.INFORMATION_MESSAGE
                 );
             }

         } catch (SQLException ex) {

             JOptionPane.showMessageDialog(
                     this,
                     "The student record could not be updated.\n\n"
                     + ex.getMessage(),
                     "Update Failed",
                     JOptionPane.ERROR_MESSAGE
             );

         } catch (Exception ex) {

             JOptionPane.showMessageDialog(
                     this,
                     "An unexpected error occurred while editing the student record.\n\n"
                     + ex.getMessage(),
                     "Edit Error",
                     JOptionPane.ERROR_MESSAGE
             );
         }
    }
       
    
            /**
     * Guardian name/phone update flow as a controlled loop.
     * Returns {name, phone} on success (or the original values if the
     * nurse said "No" to updating). Returns null only if the nurse
     * cancels outright — the whole edit should then be aborted.
     */
    private String[] promptGuardianUpdate(String defaultName, String defaultPhone) {

        final int STEP_CONFIRM = 0, STEP_NAME = 1, STEP_PHONE = 2;
        int step = STEP_CONFIRM;
        String nameValue = defaultName;
        String phoneValue = defaultPhone;

        while (true) {
            switch (step) {

                case STEP_CONFIRM: {
                    int changeGuardian = JOptionPane.showConfirmDialog(this,
                            "Do you also want to update the guardian's name and phone number?",
                            "Update Guardian Info",
                            JOptionPane.YES_NO_OPTION);

                    if (changeGuardian != JOptionPane.YES_OPTION) {
                        return new String[]{ defaultName, defaultPhone }; // keep as-is
                    }
                    step = STEP_NAME;
                    break;
                }

                case STEP_NAME: {
                    String input = JOptionPane.showInputDialog(this, "Guardian's Name:", nameValue);
                    if (input == null) return null; // dialog closed -> cancel whole edit

                    input = input.trim();
                    if (!input.isEmpty() && input.matches("[\\p{L} .'-]+")) {
                        nameValue = input;
                        step = STEP_PHONE;
                        break;
                    }

                    int choice = JOptionPane.showOptionDialog(this,
                            "Please enter a valid guardian name.",
                            "Invalid Guardian Name",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Retry", "Back", "Cancel"}, "Retry");

                    if (choice == 0) {
                        nameValue = input; // Retry, keep what they typed
                    } else if (choice == 1) {
                        step = STEP_CONFIRM; // Back to the yes/no question
                    } else {
                        return null; // Cancel or closed
                    }
                    break;
                }

                case STEP_PHONE: {
                    String input = JOptionPane.showInputDialog(this, "Guardian's Phone Number:", phoneValue);
                    if (input == null) return null;

                    input = input.trim();
                    if (input.matches("^09\\d{9}$")) {
                        return new String[]{ nameValue, input };
                    }

                    int choice = JOptionPane.showOptionDialog(this,
                            "Invalid phone number. Please enter exactly 11 digits starting with 09.",
                            "Invalid Phone Number",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, new String[]{"Retry", "Back", "Cancel"}, "Retry");

                    if (choice == 0) {
                        phoneValue = input; // Retry
                    } else if (choice == 1) {
                        step = STEP_NAME; // Back to name step
                    } else {
                        return null;
                    }
                    break;
                }
            }
        }
    }
    

        private void clearCheckInForm() {
            NameCheckIn.setText("");
            GSCheckIn.setText("");
            LRNField.setText("");
            LRNField.setEditable(true); // re-enable for the next new check-in
            ReasonArea.setText("");
            selectedVisitLrn = null;
            ReasonTable.clearSelection();
            
    }//GEN-LAST:event_EditBTNActionPerformed

    private void LogoutActionPerformed(java.awt.event.ActionEvent evt) {                                       
       
         Object[] options = {"Export & Logout", "Logout", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Would you like to export your check-in logs before logging out?",
                "Export Check-in Logs?",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Esc key or closing the dialog itself -> treat the same as Cancel
        if (choice == JOptionPane.CLOSED_OPTION || choice == 2) {
            return;
        }

        if (choice == 0) { // Export & Logout
            boolean exported = exportTodaysCheckinLogs();
            if (!exported) {
                return; // exportTodaysCheckinLogs() already explained why - stay logged in
            }
        }

        // Reaches here for: choice == 1 (plain Logout), or choice == 0 after a successful export
        stopSessionTimeoutMonitoring();
        SessionManager.clearSession();
        new LoginUi().setVisible(true);
        this.dispose();
    }                                        

     /** Turns "1-1-2026" / "2026-1-1" style input into a strict "yyyy-MM-dd" string. */
    private String normalizeDate(String input) throws DateTimeParseException {
        DateTimeFormatter looseFormat = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate date = LocalDate.parse(input, looseFormat);
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /** Shows a date field PREFILLED with today's date so the user can confirm or edit it.
     *  Returns the confirmed date, or null if the user cancelled. */
    private LocalDate confirmReportDate() {
        javax.swing.JTextField dateField = new javax.swing.JTextField(LocalDate.now().toString());
        ((javax.swing.text.AbstractDocument) dateField.getDocument())
                .setDocumentFilter(new DateInputFilter());

        while (true) {
            int result = JOptionPane.showConfirmDialog(this, dateField,
                    "Report date (edit if you need a different day, e.g. 2026-1-1 or 2026-01-01):",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return null; // user cancelled
            }
            try {
                return LocalDate.parse(normalizeDate(dateField.getText().trim()));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid date (e.g. 2026-1-1 or 2026-01-01).");
                // loop back so they can fix it instead of losing their place
            }
        }
    }

    /** Exports check-in logs for a confirmed date, then archives & resets the active list.
     *  Returns true only if the export actually succeeded (and the user did not cancel). */
     private boolean exportTodaysCheckinLogs() {
        LocalDate reportDate = confirmReportDate();
        if (reportDate == null) {
            return false; // user cancelled the date confirmation - stay logged in
        }

        String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
        ReportExporter exporter = new ReportExporter(productService);

        try {
            if (!exporter.hasCheckinRecordsForDate(reportDate)) {
                int proceed = JOptionPane.showConfirmDialog(this,
                        "There are no check-in records for " + reportDate + ". Log out without exporting?",
                        "Nothing to Export",
                        JOptionPane.YES_NO_OPTION);
                return proceed == JOptionPane.YES_OPTION;
            }

            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            String suggestedName = "CheckIn_Log_" + reportDate + ".xlsx";
            chooser.setSelectedFile(new java.io.File(suggestedName));
            int saveChoice = chooser.showSaveDialog(this);
            if (saveChoice != javax.swing.JFileChooser.APPROVE_OPTION) {
                return false; // user cancelled the Save dialog - stay logged in
            }

            java.io.File destination = chooser.getSelectedFile();
            if (!destination.getName().toLowerCase().endsWith(".xlsx")) {
                destination = new java.io.File(destination.getParentFile(), destination.getName() + ".xlsx");
            }

            // Export first. Only if this line completes without throwing do we move on.
            exporter.writeCheckinReport(reportDate, destination, actor);
            
            // Export succeeded -> now archive & reset the active daily check-in list for this date.
            try {
                int archivedCount = visitService.archiveDate(reportDate.toString());
                JOptionPane.showMessageDialog(this,
                        "Check-in logs exported and archived successfully:\n" + destination.getAbsolutePath()
                        + "\n\n" + archivedCount + " check-in record(s) for " + reportDate + " were archived.\n"
                        + "The daily Check-in Logs have been reset for the next day.");
                return true;
            } catch (SQLException archiveEx) {
                JOptionPane.showMessageDialog(this,
                        "Check-in logs were exported successfully, but they could not be archived/reset:\n"
                        + archiveEx.getMessage() + "\n\nYou have not been logged out.",
                        "Archive Failed", JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "The Check-in Logs could not be exported.\nNo records were archived or reset.\nYou have not been logged out.\n\nDatabase error: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "The Check-in Logs could not be exported.\nNo records were archived or reset.\nYou have not been logged out.\n\nFile error: " + ex.getMessage(),
                    "Export Failed", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }                     
   
    private boolean noMedicineWarningShown = false;
    
    private void CheckInBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInBTNActionPerformed
       
        noMedicineWarningShown = false;

        String name = NameCheckIn.getText().trim();
        String gradeSection = GSCheckIn.getText().trim();
        String lrn = LRNField.getText().trim();
        String reason = ReasonArea.getText().trim();

        // REQUIRED FIELD VALIDATION
        if (name.isEmpty() && gradeSection.isEmpty() && lrn.isEmpty()) {
            showToast(MainPanel, "Name, Grade/Section, and LRN are required!", false);
            return;
        } else if (name.isEmpty() && gradeSection.isEmpty()) {
            showToast(MainPanel, "Name and Grade/Section are required!", false);
            return;
        } else if (name.isEmpty() && lrn.isEmpty()) {
            showToast(MainPanel, "Name and LRN are required!", false);
            return;
        } else if (gradeSection.isEmpty() && lrn.isEmpty()) {
            showToast(MainPanel, "Grade/Section and LRN are required!", false);
            return;
        } else if (name.isEmpty()) {
            showToast(MainPanel, "Name is required!", false);
            return;
        } else if (gradeSection.isEmpty()) {
            showToast(MainPanel, "Grade/Section is required!", false);
            return;
        } else if (lrn.isEmpty()) {
            showToast(MainPanel, "LRN is required!", false);
            return;
        }

        // LRN FORMAT VALIDATION
        if (!lrn.matches("\\d{12}")) {
            showToast(MainPanel, "LRN must contain exactly 12 digits.", false);
            LRNField.requestFocus();
            return;
        }

        // REASON VALIDATION
        if (reason.isEmpty()) {
            showToast(MainPanel, "Reason is needed to proceed on the check in", false);
            return;
        }
        
        
            // CHECK IF STUDENT IS ALREADY CHECKED IN
           try {
            String activeStatus = visitService.getActiveVisitStatus(lrn);
            if (activeStatus != null) {
                String phrase = switch (activeStatus) {
                    case "In Clinic" -> "checked in";
                    case "Sent Home" -> "sent home";
                    case "Sent Back" -> "sent back to class";
                    default -> "in an active visit";
                };
                showToast(MainPanel, "This student is already " + phrase + ".", false);
                return;
            }
        } catch (Exception ex) {
            showToast(
                MainPanel,
                "Unable to verify the student's current check-in status.",
                false
            );
            return;
        }
        
        // MEDICINE CHECK
        if (jComboBox1.getItemCount() == 0) {

            int proceedWithoutMed = JOptionPane.showConfirmDialog(
                this,
                "There is no medicine available in the inventory.\n"
                + "Do you want to proceed with the check-in without giving any medicine?",
                "No Medicine Available",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (proceedWithoutMed != JOptionPane.YES_OPTION) {
                return;
            }

            pendingmedUsed = "None";
            pendingmedsQty = 0;

        } else {
            
            // MEDICINE SELECTION
            int wantsMed = JOptionPane.showConfirmDialog(this,
                "Would this student like to take medicine?", "Medicine", JOptionPane.YES_NO_OPTION);

            if (wantsMed == JOptionPane.CLOSED_OPTION) return;

            if (wantsMed == JOptionPane.YES_OPTION) {
                Object selectedMed = jComboBox1.getSelectedItem();

                if (selectedMed == null) {
                    showToast(MainPanel, "Your inventory of medicine might be empty, please check first.", false);
                    return;
                }

                pendingmedUsed = selectedMed.toString();

                try {
                    Medicine medProduct = productService.findByName(pendingmedUsed);

                    // CHECK IF MEDICINE IS EXPIRED
                    if (medProduct != null && medProduct.isExpired()) {
                        showToast(MainPanel,
                            pendingmedUsed + " is expired and cannot be given. Please choose another medicine.",
                            false);
                        return;
                    }

                    // ASK FOR QUANTITY
                    String qtyInput = JOptionPane.showInputDialog(this,
                        "How many pills of " + pendingmedUsed + "?", "1");

                    if (qtyInput == null) return; // cancelled

                    // VALIDATE QUANTITY
                    try {
                        pendingmedsQty = Integer.parseInt(qtyInput.trim());
                    } catch (NumberFormatException ex) {
                        showToast(MainPanel, "Please enter a valid whole number for pill quantity.", false);
                        return;
                    }

                    if (pendingmedsQty <= 0) {
                        showToast(MainPanel, "Pill quantity must be at least 1.", false);
                        return;
                    }

                    // CHECK AVAILABLE STOCK
                    if (medProduct != null && medProduct.getquantity() < pendingmedsQty) {
                        showToast(MainPanel,
                            pendingmedUsed + " only has " + medProduct.getquantity()
                            + " pcs left. Please enter a smaller amount.", false);
                        return;
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Unable to check the selected medicine.", "Medicine Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } else {
                // NO - Student does not want medicine
                pendingmedUsed = "None";
                pendingmedsQty = 0;
            }
        }

        // CHECK EXISTING STUDENT / LRN
        try {
          

            // REFRESH INFORMATION
            refreshTableAndCounters();
            refreshInventoryStatusDisplay();

            // CLEAR PARENT/GUARDIAN FIELDS
            ParentGurdianName.setText("");
            PhoneField.setText("");

            // SHOW PARENT/GUARDIAN PANEL USING LAYERING
            if (SentHomeInformationPanel.getParent() != null) {
                SentHomeInformationPanel.getParent().setComponentZOrder(SentHomeInformationPanel, 0);
            }

            glassOverlay.setBounds(0, 0, getWidth(), getHeight());
            glassOverlay.setVisible(true);

            // CENTER PARENT/GUARDIAN PANEL
            java.awt.Dimension size = SentHomeInformationPanel.getPreferredSize();
            int x = (getWidth() - size.width) / 2;
            int y = (getHeight() - size.height) / 2;
            SentHomeInformationPanel.setBounds(x, y, size.width, size.height);
            SentHomeInformationPanel.setVisible(true);

            // REFRESH LAYERED PANE
            getLayeredPane().revalidate();
            getLayeredPane().repaint();
            this.revalidate();
            this.repaint();

        } catch (Exception ex) {
            showToast(CheckInPanel, "Error saving check-in: " + ex.getMessage(), false);
        }
    }//GEN-LAST:event_CheckInBTNActionPerformed
 
    
    private void LRNFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LRNFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LRNFieldActionPerformed

    private void LRNFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LRNFieldKeyTyped
        
             char c = evt.getKeyChar();

            // Allow only numbers
                if (!Character.isDigit(c)) {
                  evt.consume();
                  return;
                }

             // Limit to 12 digits
             if (LRNField.getText().length() >= 12) {
              evt.consume();
            }
             
    }//GEN-LAST:event_LRNFieldKeyTyped
   
    private String selectedOldMedUsed = "None";
    private int selectedOldMedsQty = 0;
    private String selectedGuardianName = "";
    private String selectedGuardianPhone = "";
    
    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReasonTableMouseClicked
       
            if (ReasonTable.getSelectedRowCount() != 1) {
        return;
    }

    int viewRow = ReasonTable.getSelectedRow();

    if (viewRow == -1) {
        return;
    }

    // Convert the visible table row to the model row.
    // This is important when the table is sorted.
    int modelRow = ReasonTable.convertRowIndexToModel(viewRow);

    DefaultTableModel model =
            (DefaultTableModel) ReasonTable.getModel();

    // Get the LRN directly from the row the user actually clicked.
    Object lrnValue = model.getValueAt(modelRow, 3);

    if (lrnValue == null) {
        return;
    }

    String clickedLrn = lrnValue.toString().trim();

    // Find the matching visit in the ORIGINAL currentVisits list.
    CheckinSystem selected = null;

    for (CheckinSystem visit : currentVisits) {
        if (visit.getLrn() != null
                && visit.getLrn().trim().equals(clickedLrn)) {

            selected = visit;
            break;
        }
    }

    if (selected == null) {
        JOptionPane.showMessageDialog(
                this,
                "Unable to find the selected student's visit.",
                "Selection Error",
                JOptionPane.WARNING_MESSAGE
        );
        return;
    }

    // ---------------------------------------------------------
    // STATUS GATE: only an "In Clinic" record may be edited.
    // ---------------------------------------------------------
    String currentStatus = selected.getStatus();

    if (!"In Clinic".equals(currentStatus)) {
        JOptionPane.showMessageDialog(
                this,
                "This record can no longer be edited because its "
                + "status is \"" + currentStatus + "\".",
                "Cannot Edit",
                JOptionPane.WARNING_MESSAGE
        );
        clearCheckInForm();
        return;
    }

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

    private void ThemeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemeToggleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ThemeToggleActionPerformed
    
    private String pendingmedUsed = "None";
    private int pendingmedsQty = 0;
    
    
    
    private void FinishBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FinishBTNActionPerformed
             String guardianName = ParentGurdianName.getText().trim();
            String guardianPhone = PhoneField.getText().trim();
           
            if (guardianName.isEmpty()) {
                showToast(SentHomeInformationPanel, "Guardian name is required.", false);
                return;
            }
            if (guardianPhone.isEmpty()) {
                showToast(SentHomeInformationPanel, "Guardian phone number is required.", false);
                return;
            }
            if (!guardianPhone.matches("^09\\d{9}$")) {
                showToast(SentHomeInformationPanel, "Phone number must start with 09 and contain exactly 11 digits.", false);
                PhoneField.requestFocus();
                return;
            }
            if (!guardianName.matches("[\\p{L} .'-]+")) {
                showToast(SentHomeInformationPanel, "Please enter a valid guardian name.", false);
                ParentGurdianName.requestFocus();
                return;
            }

         String name = NameCheckIn.getText().trim();
         String gradeSection = GSCheckIn.getText().trim();
         String lrn = LRNField.getText().trim();
         String reason = ReasonArea.getText().trim();
         String medUsed = pendingmedUsed;

         String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";

         DatabaseExecutor.run(
             () -> visitService.checkInWithMedicine(
                     name, gradeSection, lrn, reason, medUsed, pendingmedsQty,
                     guardianName, guardianPhone, productService, actor),
             result -> {
                 if (!medUsed.equals("None") && !result.medicineDeducted()) {
                     showToast(CheckInPanel, "Warning: " + medUsed + " is out of stock. Stock was not deducted.", false);
                 }

                 refreshTableAndCounters();
                 refreshInventoryStatusDisplay();

                 NameCheckIn.setText("");
                 GSCheckIn.setText("");
                 LRNField.setText("");
                 ReasonArea.setText("");
                 SentHomeInformationPanel.setVisible(false);
                 glassOverlay.setVisible(false);
                 this.revalidate();
                 this.repaint();

                 showToast(CheckInPanel, name + " checked in successfully. Guardian info recorded.", true);
             },
             ex -> showToast(CheckInPanel, "Error saving check-in: " + ex.getMessage(), false)
         );
    }//GEN-LAST:event_FinishBTNActionPerformed

    private void InformationBackBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InformationBackBTNActionPerformed
        
        SentHomeInformationPanel.setVisible(false);
        glassOverlay.setVisible(false);

            this.revalidate();
            this.repaint();
    }//GEN-LAST:event_InformationBackBTNActionPerformed

    private void ClearBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearBTNActionPerformed
       clearCheckInForm();
       showToast(CheckInPanel, "Form cleared.", true);
    }//GEN-LAST:event_ClearBTNActionPerformed
   
    private void SentHomeBTNActionPerformed(java.awt.event.ActionEvent evt) {                                         
            

            int row = ReasonTable.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a Student in the Table First...");
                return;
            }

            if (ReasonTable.getSelectedRowCount() > 1) {
                JOptionPane.showMessageDialog(this,
                        "Please select only one student at a time to send home.",
                        "Multiple Students Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String lrn = (String) ReasonTable.getValueAt(row, 3);

            try {
                
                CheckinSystem record = visitService.findActiveVisit(lrn);

                if (record == null) {
                    JOptionPane.showMessageDialog(this, "This Student has Already been sent");
                    return;
                }
                
                Object[] pickerOptions = {"Sent Home", "Sent Back to Classroom", "Cancel"};
                int choice = JOptionPane.showOptionDialog(
                        this,
                        "Sent home Or Sent Back to room?",
                        "Student Status",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        pickerOptions,
                        pickerOptions[0]);

                if (choice != 0 && choice != 1) {
                    // Cancel selected, or dialog closed with the X button — do nothing
                    return;
                }

                if (choice == 1) {
                    // ---- SENT BACK TO CLASSROOM ----
                    String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
                    boolean backSuccess = visitService.markSentBack(lrn, actor);
                    
                    if (!backSuccess) {
                        JOptionPane.showMessageDialog(this, "Error... Unable to update the Student's Status");
                        return;
                    }

                    refreshTableAndCounters();
                    JOptionPane.showMessageDialog(this,
                            record.getName() + " has been sent back to the classroom.");
                    return;
                }

                // ---- SENT HOME (choice == 0) — existing flow, unchanged below ----
                String actor = (loggedInAccount != null) ? loggedInAccount.GetName() : "Unknown";
                boolean success = visitService.markSentHome(lrn, actor);

                if (!success) {
                    JOptionPane.showMessageDialog(this, "Error... Unable to update the Student's Status");
                    return;
                }

                refreshTableAndCounters();

                String safeName = record.getName().replaceAll("[^a-zA-Z0-9]", "_");
                String defaultFileName = "SentHome_" + safeName + "_" + java.time.LocalDate.now() + ".pdf";

                javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
                fileChooser.setDialogTitle("Save Sent Home Slip");
                fileChooser.setSelectedFile(new java.io.File(defaultFileName));
                fileChooser.setFileFilter(
                        new javax.swing.filechooser.FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

                int userChoice = fileChooser.showSaveDialog(this);
                if (userChoice != javax.swing.JFileChooser.APPROVE_OPTION) {
                    // Status is already updated to Sent Home - the user just chose not to save a slip right now.
                    return;
                }

                java.io.File pdfFile = fileChooser.getSelectedFile();
                if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
                    pdfFile = new java.io.File(pdfFile.getParentFile(), pdfFile.getName() + ".pdf");
                }

                generateSentHomePdf(record, pdfFile);

                int openChoice = JOptionPane.showConfirmDialog(this,
                        "Sent Home slip generated successfully. Would you like to open it?",
                        "Slip Generated", JOptionPane.YES_NO_OPTION);

                if (openChoice == JOptionPane.YES_OPTION) {
                    try {
                        if (java.awt.Desktop.isDesktopSupported()) {
                            java.awt.Desktop.getDesktop().open(pdfFile);
                        } else {
                            JOptionPane.showMessageDialog(this, "Saved to: " + pdfFile.getAbsolutePath());
                        }
                    } catch (java.io.IOException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Could not open the PDF automatically. Saved to:\n" + pdfFile.getAbsolutePath());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Saved to: " + pdfFile.getAbsolutePath());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
            }
    }  
        //print layout
        private void printSentHomeSlip(CheckinSystem record) {

            String staffName = (loggedInAccount != null) ? loggedInAccount.GetName() : "N/A";
            String sentHomeTime = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));

            java.util.List<String[]> fields = new java.util.ArrayList<>();
            fields.add(new String[]{"Name", record.getName()});
            fields.add(new String[]{"Grade/Section", record.getGradeSection()});
            fields.add(new String[]{"LRN", record.getLrn()});
            fields.add(new String[]{"Reason for Visit", record.getReason()});
            fields.add(new String[]{"Medicine Used", record.getMedicineDisplay()});
            fields.add(new String[]{"Checked In", record.getCheckInTime()});
            fields.add(new String[]{"Sent Home", sentHomeTime});
            fields.add(new String[]{"Parent/Guardian", dashOrValue(record.getGuardianName())});
            fields.add(new String[]{"Guardian Phone", dashOrValue(record.getGuardianPhoneNums())});
            fields.add(new String[]{"Clinic Staff", staffName});

            java.awt.print.Printable printable = (graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) return java.awt.print.Printable.NO_SUCH_PAGE;

                Graphics2D g2 = (Graphics2D) graphics;
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                int y = 20;
                g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
                g2.drawString("[School name] CLINIC", 10, y);
                y += 20;
                g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 13));
                g2.drawString("STUDENT SENT HOME SLIP", 10, y);
                y += 8;
                g2.drawLine(10, y, (int) pageFormat.getImageableWidth() - 10, y);
                y += 25;

                g2.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
                for (String[] field : fields) {
                    g2.drawString(String.format("%-16s: %s", field[0], field[1]), 10, y);
                    y += 20;
                }

                y += 25;
                g2.drawString("Released to (Parent/Guardian): ____________________________", 10, y);
                y += 40;
                g2.drawString("Guardian Signature:             ____________________________", 10, y);
                y += 40;
                g2.drawString("Nurse/Staff Signature:          ____________________________", 10, y);
                y += 30;

                g2.setFont(new java.awt.Font("SansSerif", java.awt.Font.ITALIC, 10));
                g2.drawString("Please keep this slip for your records.", 10, y);

                return java.awt.print.Printable.PAGE_EXISTS;
            };

            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setPrintable(printable);
            job.setJobName("SentHomeSlip_" + record.getLrn());

            if (job.printDialog()) {
                try {
                    job.print();
                } catch (java.awt.print.PrinterException ex) {
                    JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage());
                }
            }
}
        
    private void generateSentHomePdf(CheckinSystem record, File destination) throws IOException {
    String staffName = (loggedInAccount != null) ? loggedInAccount.GetName() : "N/A";
    String sentHomeTime = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));

    try (org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument()) {
        org.apache.pdfbox.pdmodel.PDPage page =
                new org.apache.pdfbox.pdmodel.PDPage(org.apache.pdfbox.pdmodel.common.PDRectangle.LETTER);
        document.addPage(page);

        // Embedded fonts loaded per-document (PDFont instances are tied to one PDDocument)
       org.apache.pdfbox.pdmodel.font.PDFont fontRegular =
        loadSlipFont(document, "/Assets/Fonts/ClinicSans-Regular.ttf");

        org.apache.pdfbox.pdmodel.font.PDFont fontBold =
                loadSlipFont(document, "/Assets/Fonts/ClinicSans-Bold.ttf");

        org.apache.pdfbox.pdmodel.font.PDFont fontItalic =
                loadSlipFont(document, "/Assets/Fonts/ClinicSans-Italic.ttf");

        try (org.apache.pdfbox.pdmodel.PDPageContentStream content =
                     new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page)) {

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            float leading = 20;

            content.setFont(fontBold, 16);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("[Your School Name] CLINIC");
            content.endText();
            y -= 20;

            content.setFont(fontBold, 13);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("SENT HOME RECORD");
            content.endText();
            y -= 10;

            content.moveTo(margin, y);
            content.lineTo(page.getMediaBox().getWidth() - margin, y);
            content.stroke();
            y -= 25;

            String[][] fields = {
                {"Name", record.getName()},
                {"Grade/Section", record.getGradeSection()},
                {"LRN", record.getLrn()},
                {"Reason for Visit", record.getReason()},
                {"Medicine Used", record.getMedicineDisplay()},
                {"Checked In", record.getCheckInTime()},
                {"Sent Home", sentHomeTime},
                {"Parent/Guardian", dashOrValue(record.getGuardianName())},
                {"Guardian Phone", dashOrValue(record.getGuardianPhoneNums())},
                {"Clinic Staff", staffName}
            };

            content.setFont(fontRegular, 12);
            for (String[] field : fields) {
                content.beginText();
                content.newLineAtOffset(margin, y);
                content.showText(String.format("%-16s: %s", field[0], safeText(field[1])));
                content.endText();
                y -= leading;
            }

            y -= 25;
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("Released to (Parent/Guardian): ____________________________");
            content.endText();
            y -= 45;

            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("Guardian Signature:             ____________________________");
            content.endText();
            y -= 45;

            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("Nurse/Staff Signature:          ____________________________");
            content.endText();
            y -= 30;

            content.setFont(fontItalic, 10);
            content.beginText();
            content.newLineAtOffset(margin, y);
            content.showText("Please keep this slip for your records.");
            content.endText();
        }

        document.save(destination);
    }
}

    private org.apache.pdfbox.pdmodel.font.PDFont slipFontRegular;
    private org.apache.pdfbox.pdmodel.font.PDFont slipFontBold;
    private org.apache.pdfbox.pdmodel.font.PDFont slipFontItalic;

    private org.apache.pdfbox.pdmodel.font.PDFont loadSlipFont(
            org.apache.pdfbox.pdmodel.PDDocument document, String resourcePath) throws IOException {
        try (java.io.InputStream is = Dashboard.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException("Missing bundled font resource: " + resourcePath);
            }
            return org.apache.pdfbox.pdmodel.font.PDType0Font.load(document, is);
        }
    }
    
// PDFBox's built-in Helvetica font can't render every Unicode character.
// This keeps generation from crashing on unusual characters typed into Reason/Name fields.
private String safeText(String value) {
    if (value == null) return "-";
    return value.replaceAll("[^\\x20-\\x7E]", "?");
}

    private String dashOrValue(String value) {
    return (value == null || value.isBlank()) ? "—" : value;
     }
        
    private void showTopAlertBanner(String message) {
    javax.swing.JLabel alert = new javax.swing.JLabel(message, javax.swing.SwingConstants.CENTER);
    alert.setOpaque(true);
    alert.setBackground(new java.awt.Color(225, 29, 72)); // Modern Crimson/Coral Warning
    alert.setForeground(java.awt.Color.WHITE);
    alert.setFont(alert.getFont().deriveFont(java.awt.Font.BOLD, 13f));
    
    // Modern FlatLaf pill shape
    alert.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "arc: 12;");

    int bannerHeight = 40;
    int bannerWidth = this.getWidth() - 100;
    int startY = -bannerHeight; // Hidden above the frame
    int targetY = 15;           // Slides down right over the header area
    int relativeX = 50;

    alert.setBounds(relativeX, startY, bannerWidth, bannerHeight);

    javax.swing.JLayeredPane layeredPane = this.getLayeredPane();
    layeredPane.add(alert, javax.swing.JLayeredPane.DRAG_LAYER);
    layeredPane.revalidate();

    // Slide DOWN Animation
    javax.swing.Timer slideTimer = new javax.swing.Timer(10, null);
    slideTimer.addActionListener(new java.awt.event.ActionListener() {
        int currentY = startY;
        
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (currentY < targetY) {
                currentY += 3;
                alert.setLocation(relativeX, currentY);
                layeredPane.repaint();
            } else {
                slideTimer.stop();

                // Stay visible for 5 seconds so staff can read all expired items
                javax.swing.Timer delayTimer = new javax.swing.Timer(5000, evt -> {
                    javax.swing.Timer slideUpTimer = new javax.swing.Timer(10, null);
                    slideUpTimer.addActionListener(new java.awt.event.ActionListener() {
                        int returnY = targetY;

                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            if (returnY > startY) {
                                returnY -= 3;
                                alert.setLocation(relativeX, returnY);
                                layeredPane.repaint();
                            } else {
                                slideUpTimer.stop();
                                layeredPane.remove(alert);
                                layeredPane.repaint();
                            }
                        }
                    });
                    slideUpTimer.start();
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }
    });
    
    slideTimer.start();
}
    /*private void checkExpiredProducts() {
    try {
        
        ArrayList<Medicine> medicine = productService.loadAll();
        ArrayList<String> expiredItems = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Try multiple date formatters if needed (e.g. yyyy-MM-dd or MM/dd/yyyy)
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Medicine p : medicine) {
            // Option A: If status itself is marked as "Expired" or "OutOfStock"
            if (p.getStatus() != null && p.getStatus().equalsIgnoreCase("Expired")) {
                expiredItems.add(p.getname());
                continue;
            }

            // Option B: If status stores a date string like "2026-08-01"
            try {
                LocalDate expDate = LocalDate.parse(p.getStatus().trim(), formatter);
                if (expDate.isBefore(today) || expDate.isEqual(today)) {
                    expiredItems.add(p.getname() + " (Exp: " + p.getStatus() + ")");
                }
            } catch (Exception ignored) {
                // Skips items where getStatus() isn't a date string
            }
        }

        if (!expiredItems.isEmpty()) {
            String alertMsg = "⚠️ EXPIRED INVENTORY DETECTED: " + String.join(", ", expiredItems);
            showTopAlertBanner(alertMsg);
        }

    } catch (Exception ex) {
        logger.log(java.util.logging.Level.SEVERE, "Error checking product expiration", ex);
    }
}*/
    
    private void toggleSentHomePanel(boolean visible) {
    SentHomeInformationPanel.setVisible(visible);
    
    // Explicitly toggle visibility for all components inside the panel
    for (Component comp : SentHomeInformationPanel.getComponents()) {
        comp.setVisible(visible);
    }
    
    this.revalidate();
    this.repaint();
}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        //java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CheckInBTN;
    private javax.swing.JPanel CheckInPanel;
    private javax.swing.JPanel CheckInPanel1;
    private javax.swing.JButton ClearBTN;
    private javax.swing.JLabel DateTimeLabel;
    private javax.swing.JButton EditBTN;
    private javax.swing.JButton FinishBTN;
    private javax.swing.JTextField GSCheckIn;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JButton InformationBackBTN;
    private javax.swing.JLabel InventoryLabel;
    private javax.swing.JPanel InventoryPanel;
    private javax.swing.JTextArea InventoryStatusArea;
    private javax.swing.JTextField LRNField;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JButton Logout;
    private javax.swing.JLabel LogsLabel;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JTextField NameCheckIn;
    private javax.swing.JLabel OverviewLabel;
    private javax.swing.JLabel PGNameLabel;
    private javax.swing.JLabel PNField;
    private javax.swing.JTextField ParentGurdianName;
    private javax.swing.JTextField PhoneField;
    private javax.swing.JTextArea ReasonArea;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel SearchLabel;
    private javax.swing.JButton SentHomeBTN;
    private javax.swing.JLabel SentHomeCount;
    private javax.swing.JLabel SentHomeFooterLabel;
    private javax.swing.JPanel SentHomeInformationPanel;
    private javax.swing.JPanel SentHomePanel;
    private javax.swing.JLabel StudentCheckinLabel;
    private javax.swing.JToggleButton ThemeToggle;
    private javax.swing.JLabel VisitCounter;
    private javax.swing.JPanel VisitPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
