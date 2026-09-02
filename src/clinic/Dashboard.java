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
            CheckInPopup.putClientProperty("JComponent.arc", 25);
            
            InventoryPanel.putClientProperty("JComponent.arc", 25);

            java.awt.Color softSlate = new java.awt.Color(245, 247, 250); 
            CheckInPopup.setBackground(softSlate);
            
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
    CheckInPopup.removeAll();
    CheckInPopup.setLayout(new MigLayout(
        "wrap 1, fillx, insets 20", 
        "[fill, grow]", ""));
    CheckInPopup.add(StudentCheckinLabel, "alignx center, gapbottom 15");
    CheckInPopup.add(jLabel7);
    CheckInPopup.add(NameCheckIn);
    CheckInPopup.add(jLabel8, "gaptop 8");
    CheckInPopup.add(GSCheckIn);
    CheckInPopup.add(LRNLabel, "gaptop 8");
    CheckInPopup.add(LRNField);
    CheckInPopup.add(jLabel9, "gaptop 8");
    CheckInPopup.add(jScrollPane1, "h 170!"); 
    CheckInPopup.add(jLabel11, "split 2, gaptop 10");
    CheckInPopup.add(jComboBox1, "w 140!");
    // Push buttons to the bottom
    CheckInPopup.add(CheckInBTN,  "growx, pushy, aligny bottom, gaptop 20 ");
    CheckInPopup.add(SentHomeBTN, "growx, gaptop 8 ");
    CheckInPopup.add(EditBTN,     "split 2, growx, gaptop 8 ");  // Edit + Clear share this row
    CheckInPopup.add(ClearBTN,    "growx ");                     // sits inline with Edit
    

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
    MainPanel.add(CheckInPopup, "spany 4, grow");

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
        CheckInPopup.setBackground(panelBackground);
        CheckInPanel1.setBackground(panelBackground);
        InventoryPanel.setBackground(panelBackground);
        VisitPanel.setBackground(cardBackground);
        SentHomePanel.setBackground(cardBackground);
        SentHomeInformationPanel.setBackground(cardBackground);

        // Borders
        javax.swing.border.Border softBorder = javax.swing.BorderFactory.createLineBorder(borderColor, 1, true);
        javax.swing.border.Border paddedBorder = javax.swing.BorderFactory.createCompoundBorder(
            softBorder, javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        CheckInPopup.setBorder(paddedBorder);
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
          DatabaseExecutor.run(
            () -> new DashboardCounts(visitService.loadActive(), visitService.getTodayCounts()),
            result -> {
                currentVisits = result.visits();
                applySearchFilter(); // rebuilds the table, honoring the current search text
                int[] counts = result.counts();
                VisitCounter.setText(String.valueOf(counts[0]));
                SentHomeCount.setText(String.valueOf(counts[1]));
            },
            ex -> JOptionPane.showMessageDialog(this, ex.getMessage())
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

        HeaderPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        DateTimeLabel = new javax.swing.JLabel();
        Logout = new javax.swing.JButton();
        ThemeToggle = new javax.swing.JToggleButton();
        SidePanel = new javax.swing.JPanel();
        HomeBTN = new javax.swing.JButton();
        CheckinBTN = new javax.swing.JButton();
        StatisticBTN = new javax.swing.JButton();
        InventoryBTN = new javax.swing.JButton();
        InventoryBTN1 = new javax.swing.JButton();
        MainPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        ECheckin = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
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
        CheckInPanel1 = new javax.swing.JPanel();
        CheckInBTN = new javax.swing.JButton();
        SentHomeBTN = new javax.swing.JButton();
        SearchField = new javax.swing.JTextField();
        SearchLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        HeaderPanel.setBackground(new java.awt.Color(51, 153, 255));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Clinic Dashboard");

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
                .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(162, 162, 162)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 404, Short.MAX_VALUE)
                .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Logout)
                .addContainerGap())
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 25, Short.MAX_VALUE))
        );

        getContentPane().add(HeaderPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1760, -1));

        SidePanel.setBackground(new java.awt.Color(51, 153, 255));

        HomeBTN.setText("Home");

        CheckinBTN.setText("Check in");

        StatisticBTN.setText("Statistic");
        StatisticBTN.addActionListener(this::StatisticBTNActionPerformed);

        InventoryBTN.setText("Inventory");

        InventoryBTN1.setText("Management");

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
                    .addComponent(InventoryBTN1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                .addContainerGap())
        );
        SidePanelLayout.setVerticalGroup(
            SidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SidePanelLayout.createSequentialGroup()
                .addGap(233, 233, 233)
                .addComponent(HomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(CheckinBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(StatisticBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InventoryBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(InventoryBTN1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
        );

        getContentPane().add(SidePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 77, 270, 760));

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setBackground(new java.awt.Color(255, 255, 255));
        jTable1.setForeground(new java.awt.Color(0, 0, 0));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Time", "Name", "Section", "Temp", "Symptoms", "Medicine", "Status"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
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
                .addContainerGap(104, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(jLabel18)
                    .addComponent(jLabel15)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(ECheckin, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING))))))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                .addComponent(ECheckin)
                .addContainerGap())
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

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(jLabel20))
                            .addComponent(jLabel19)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(53, 53, 53))))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(22, 22, 22)
                        .addComponent(jLabel20))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                            .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        getContentPane().add(MainPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 86, 1490, 750));

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

        jTextField1.setText("jTextField1");

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
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel9)
                                    .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, 274, Short.MAX_VALUE)
                                    .addComponent(jTextField1)))
                            .addComponent(jLabel8))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        CheckInPopupLayout.setVerticalGroup(
            CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPopupLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CheckInPopupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(27, 27, 27)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        getContentPane().add(CheckInPopup, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 290, 420, 360));

        CheckInPanel1.setBackground(new java.awt.Color(226, 226, 226));
        CheckInPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        CheckInBTN.setBackground(new java.awt.Color(0, 102, 204));
        CheckInBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        CheckInBTN.setForeground(new java.awt.Color(255, 255, 255));
        CheckInBTN.setText("Check In");
        CheckInBTN.addActionListener(this::CheckInBTNActionPerformed);

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
                .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(653, Short.MAX_VALUE))
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
                    .addComponent(SentHomeBTN, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(CheckInPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 1490, 730));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1490, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 90, 1490, 750));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
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
            String existingName = visitService.findNameForLrn(lrn);

            // CHECK IF LRN BELONGS TO ANOTHER STUDENT
            if (existingName != null && !existingName.equalsIgnoreCase(name)) {
                JOptionPane.showMessageDialog(this,
                    "This LRN is already registered under the name \"" + existingName
                    + "\". Please verify the LRN or name.",
                    "LRN Already Registered", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // CHECK IF STUDENT IS ALREADY CHECKED IN
            if (visitService.isCurrentlyCheckedIn(lrn)) {
                JOptionPane.showMessageDialog(this,
                    "This student is already checked in.", "Already Checked In", JOptionPane.WARNING_MESSAGE);
                return;
            }

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
            showToast(CheckInPopup, "Error saving check-in: " + ex.getMessage(), false);
        }
    }//GEN-LAST:event_CheckInBTNActionPerformed
 
       
    private String selectedOldMedUsed = "None";
    private int selectedOldMedsQty = 0;
    private String selectedGuardianName = "";
    private String selectedGuardianPhone = "";
    
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

    private void ThemeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ThemeToggleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ThemeToggleActionPerformed

    private void StatisticBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_StatisticBTNActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_StatisticBTNActionPerformed
    
    private String pendingmedUsed = "None";
    private int pendingmedsQty = 0;
    
    
       
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
    private javax.swing.JPanel CheckInPanel1;
    private javax.swing.JPanel CheckInPopup;
    private javax.swing.JButton CheckinBTN;
    private javax.swing.JLabel DateTimeLabel;
    private javax.swing.JButton ECheckin;
    private javax.swing.JPanel HeaderPanel;
    private javax.swing.JButton HomeBTN;
    private javax.swing.JButton InventoryBTN;
    private javax.swing.JButton InventoryBTN1;
    private javax.swing.JLabel LRNLabel;
    private javax.swing.JButton Logout;
    private javax.swing.JPanel MainPanel;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JTextField SearchField;
    private javax.swing.JLabel SearchLabel;
    private javax.swing.JButton SentHomeBTN;
    private javax.swing.JPanel SidePanel;
    private javax.swing.JButton StatisticBTN;
    private javax.swing.JLabel StudentCheckinLabel;
    private javax.swing.JToggleButton ThemeToggle;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
