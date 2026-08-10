/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;


import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import net.miginfocom.swing.MigLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author PC
 */
public class Dashboard extends javax.swing.JFrame {
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());
    private static boolean darkMode = false;
    private GlassOverlayPanel glassOverlay = new GlassOverlayPanel();
    private AccountSystem loggedInAccount;
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
            applyMigLayouts();
            
            this.setSize(1366, 800);             // Sets a standard HD laptop size
            this.setMinimumSize(new java.awt.Dimension(1024, 600)); // Prevents it from getting too small
            this.setLocationRelativeTo(null);
            
            
            
            
            ((AbstractDocument) NameCheckIn.getDocument()).setDocumentFilter(new NameInputFilter()); //this is an input filter for NameCheckin which dont allows numbers
            ((AbstractDocument) ParentGurdianName.getDocument()).setDocumentFilter(new NameInputFilter()); //this also and input filter for the parentName which do not allows numbers too
            ((AbstractDocument) PhoneField.getDocument()).setDocumentFilter(new PhoneNumberFilter()); // this only allows 09 number and also 11 digits only 
            jButton1.setVisible(loggedInAccount.isAdmin());
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
            checkExpiredProducts();

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
        HeaderPanel.add(jButton2, "gapleft 10");

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
    MainPanel.add(LogsLabel, "spanx 2, alignx center, wrap");

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
        javax.swing.JButton[] headerButtons = {jButton1, jButton2};
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
        javax.swing.JButton[] actionButtons = {CheckInBTN, EditBTN, SentHomeBTN, ClearBTN};
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
 
   //ComboBox problem 
    private void medicineBox(){
        try{
            ArrayList<Medicine> medicine = productService.loadAll();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            
            for(Medicine p : medicine){
                model.addElement(p.getname());
            }
            
            jComboBox1.setModel(model);
            
        }catch(IOException ex){
            JOptionPane.showMessageDialog(this, "Error Laoding message" + ex.getMessage());
        }
    }


    //ps this will help display the inventory on the "inventory status"
    private VisitCsvHandling visitService = new VisitCsvHandling("visits.csv");
    private MedicineCsvHandling productService = new MedicineCsvHandling("products.csv", "inventory_activity.log");
    
    private void refreshInventoryStatusDisplay(){
        try{
             ArrayList<Medicine> medicine = productService.loadAll();
                 StringBuilder sb = new StringBuilder();
                 StringBuilder lowStockNames = new StringBuilder();
                 int lowStockCount = 0;

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
                         sb.append("\n");
            }
        }
                 
             InventoryStatusArea.setText(sb.toString());
             
             
            if (lowStockCount > 0) {
                showTopAlertBanner( lowStockNames + " is low on stock: " + lowStockCount );
            }
             
        
        }catch(IOException ex){
               JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage());
        }
    }
    
        
    private ArrayList<CheckinSystem> currentVisits = new ArrayList<>();
    private String selectedVisitLrn = null;
    
    //pinapakita yun table and counters and inaupdate
    private void refreshTableAndCounters(){
          try {
                 
              currentVisits = visitService.loadAll();
              
         DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();

         model.setRowCount(0);

         for (CheckinSystem v : visitService.loadAll()) {

                   model.addRow(new Object[]{
                    v.getStatus(),
                    v.getName(),
                    v.getGradeSection(),
                    v.getLrn(),
                    v.getMedicineDisplay(),
                    v.getReason(),
                    v.getGuardianName(),
                    v.getGuardianPhoneNums()
             });

         }

              int[] counts = visitService.getTodayCounts();
         
             VisitCounter.setText(String.valueOf(counts[0]));
             SentHomeCount.setText(String.valueOf(counts[1]));

              // Update footer with last sent home info
              /*CheckinSystem lastSentHome = null;
              for (CheckinSystem v : visitService.loadAll()) {
                  if ("Sent Home".equals(v.getStatus())) {
                      lastSentHome = v;
                  }
              }
              if (lastSentHome != null) {
                  SentHomeFooterLabel.setText("Last: " + lastSentHome.getName() + " — " + lastSentHome.getReason() + " · " + lastSentHome.getCheckInTime());
                  SentHomeFooterLabel.setForeground(new java.awt.Color(107, 114, 128));
              } else {
                  SentHomeFooterLabel.setText("No students sent home today.");
                  SentHomeFooterLabel.setForeground(java.awt.Color.GRAY);
              }*/

               } catch (IOException ex) {

                  JOptionPane.showMessageDialog(this, ex.getMessage());
          }
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
        jButton2 = new javax.swing.JButton();
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

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Logout");
        jButton2.addActionListener(this::jButton2ActionPerformed);

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
                .addComponent(jButton2)
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
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        SentHomeBTN.setText("Sent Home");
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
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
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
                .addContainerGap(41, Short.MAX_VALUE))
        );

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
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(569, 569, 569)
                                .addComponent(LogsLabel)))
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
                                .addGap(18, 18, 18)
                                .addComponent(LogsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(InventoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(19, Short.MAX_VALUE))
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
       if (loggedInAccount == null || !loggedInAccount.isAdmin()) {

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
            
                if (selectedVisitLrn == null) {
               JOptionPane.showMessageDialog(
                       this,
                       "Select a student from the table first."
               );
               return;
           }

           String newName = NameCheckIn.getText().trim();
           String newGradeSection = GSCheckIn.getText().trim();
           String newReason = ReasonArea.getText().trim();
           Object selectedMed = jComboBox1.getSelectedItem();

           if (newName.isEmpty() || newGradeSection.isEmpty()) {
               JOptionPane.showMessageDialog(
                       this,
                       "Name and Grade/Section cannot be empty."
               );
               return;
           }

           if (selectedMed == null) {
               JOptionPane.showMessageDialog(
                       this,
                       "Your inventory of medicine might be empty, please check first."
               );
               return;
           }

           String newMedUsed = selectedMed.toString();

           // Ask for the new medicine quantity
           int newMedsQty = 0;

           if (!newMedUsed.equalsIgnoreCase("None")) {

               String qtyInput = JOptionPane.showInputDialog(
                       this,
                       "How many pills of " + newMedUsed + "?",
                       "Medicine Quantity",
                       JOptionPane.QUESTION_MESSAGE
               );

               // User pressed Cancel
               if (qtyInput == null) {
                   return;
               }

               try {
                   newMedsQty = Integer.parseInt(qtyInput.trim());

                   if (newMedsQty <= 0) {
                       JOptionPane.showMessageDialog(
                               this,
                               "Medicine quantity must be greater than 0."
                       );
                       return;
                   }

               } catch (NumberFormatException ex) {
                   JOptionPane.showMessageDialog(
                           this,
                           "Please enter a valid number for the medicine quantity."
                   );
                   return;
               }
           }

           try {

               boolean success = visitService.editVisit(  selectedVisitLrn, newName, newGradeSection, newReason, newMedUsed, newMedsQty);

               if (!success) {

                   JOptionPane.showMessageDialog(
                           this,
                           "Could not find that student's record."
                   );

               } else {

                   refreshTableAndCounters();
                   clearCheckInForm();

                   JOptionPane.showMessageDialog(
                           this,
                           "Student record updated."
                   );
               }

           } catch (IOException ex) {

               JOptionPane.showMessageDialog(
                       this,
                       "Error editing record: " + ex.getMessage()
               );
           }
       }

        private void clearCheckInForm() {
            NameCheckIn.setText("");
            GSCheckIn.setText("");
            LRNField.setText("");
            ReasonArea.setText("");
            selectedVisitLrn = null;
            ReasonTable.clearSelection();
            
    }//GEN-LAST:event_EditBTNActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       
        int choice = JOptionPane.showConfirmDialog(
        this,
        "Are you sure you want to log out?",
        "Confirm logout",
        JOptionPane.YES_NO_OPTION
    );

    if (choice == JOptionPane.YES_OPTION) {
        new LoginUi().setVisible(true);
        this.dispose();
        }
    
    }//GEN-LAST:event_jButton2ActionPerformed
    
    
    private void CheckInBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckInBTNActionPerformed

     String name = NameCheckIn.getText().trim();
     String gradeSection = GSCheckIn.getText().trim();
     String lrn = LRNField.getText().trim();
     String reason = ReasonArea.getText().trim();
     String medUsed = jComboBox1.getSelectedItem().toString();
     
   
        //if else if statement for feilds 
        if (name.isEmpty() && gradeSection.isEmpty() && lrn.isEmpty()) {

       
            showToast(MainPanel,"Name, Grade/Section, and LRN are required!",false);
            return;

         } else if (name.isEmpty() && gradeSection.isEmpty()) {

            showToast( MainPanel,"Name and Grade/Section are required!",false);
            return;

         } else if (name.isEmpty() && lrn.isEmpty()) {

             showToast(MainPanel,"Name and LRN are required!",false);
             return;

         } else if (gradeSection.isEmpty() && lrn.isEmpty()) {

             showToast(MainPanel,"Grade/Section and LRN are required!",false);
             return;

         } else if (name.isEmpty()) {

             showToast(MainPanel,"Name is required!",false);
             return;

         } else if (gradeSection.isEmpty()) {

             showToast(MainPanel,"Grade/Section is required!",false);
             return;

         } else if (lrn.isEmpty()) {

             showToast(MainPanel, "LRN is required!", false);
             return;
         }

    // LRN Format Validation
    if (!lrn.matches("\\d{12}")) {
        showToast(MainPanel, "LRN must contain exactly 12 digits.", false);
        LRNField.requestFocus();
        return;
    }
    //Required Fields for Reason
    if(reason.isEmpty()){
        showToast(MainPanel, "Reason is needed to proceed on the check in" , false );
        return;
    }


    int wantsMed = JOptionPane.showConfirmDialog(this,
        "Would this student like to take medicine?",
        "Medicine",
        JOptionPane.YES_NO_OPTION);

    if (wantsMed == JOptionPane.YES_OPTION) {
        Object selectedMed = jComboBox1.getSelectedItem();

        if (selectedMed == null) {
            showToast(MainPanel, "Your inventory of medicine might be empty, please check first.", false);
            return;
        }   
        
        pendingmedUsed  = selectedMed.toString();
        
   
    try{
        
    Medicine medProduct = productService.findByName(pendingmedUsed);

    if (medProduct != null && medProduct.isExpired()) {
        showToast(MainPanel, medUsed + " is expired and cannot be given. Please choose another medicine.", false);
        return;
    }

    String qtyInput = JOptionPane.showInputDialog(this, "How many pills of " + pendingmedUsed + "?", "1");

    if (qtyInput == null) {
        return; // nurse cancelled
    }

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

    if (medProduct != null && medProduct.getquantity() < pendingmedsQty) {
        showToast(MainPanel, pendingmedUsed + " only has " + medProduct.getquantity() + " pcs left. Please enter a smaller amount.", false);
        return;
        }
     
   
    
    
    }catch(IOException ex){
        JOptionPane.showMessageDialog(this , "Error");
          return;
    }
    try {
        String existingName = visitService.findNameForLrn(lrn);
        
        if (existingName != null && !existingName.equalsIgnoreCase(name)) {
        JOptionPane.showMessageDialog(this,
            "This LRN is already registered under the name \"" + existingName + "\". Please verify the LRN or name.");
        return;
    }
        //Duplicate Check-in Check
         if (visitService.isCurrentlyCheckedIn(lrn)) {
        JOptionPane.showMessageDialog(this, "This student is already checked in.");
        return;
    }      
        
        //Refresh Displays and Clear Form
        refreshTableAndCounters();
        refreshInventoryStatusDisplay();

       
        ParentGurdianName.setText("");
        PhoneField.setText("");
        if (SentHomeInformationPanel.getParent() != null) {
        SentHomeInformationPanel.getParent().setComponentZOrder(SentHomeInformationPanel, 0);
    }
        glassOverlay.setBounds(0, 0, getWidth(), getHeight());
    
    // Optional: If using Option 2 (True Blur), capture screenshot here:
    // glassOverlay.setBlurImage(getBlurredSnapshot());
    
        glassOverlay.setVisible(true);

        // 2. Center the popup panel relative to the window frame
        
        java.awt.Dimension size = SentHomeInformationPanel.getPreferredSize();
        int x = (getWidth() - size.width) / 2;
        int y = (getHeight() - size.height) / 2;
        SentHomeInformationPanel.setBounds(x, y, size.width, size.height);
        SentHomeInformationPanel.setVisible(true);

        // 3. Refresh the layered pane stack
        getLayeredPane().revalidate();
        getLayeredPane().repaint();

        // 2. Make it visible and refresh rendering
        SentHomeInformationPanel.setVisible(true);
        this.revalidate();
        this.repaint();
        
        //testing kung gumagana yun Check btn
        System.out.println("Showing Guardian Panel...");

        } catch (IOException ex) {
             showToast(CheckInPanel, "Error saving check-in: " + ex.getMessage(), false);
            }
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

    private void ReasonTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ReasonTableMouseClicked
        int row = ReasonTable.getSelectedRow();
        if (row == -1) return;

        CheckinSystem selected = currentVisits.get(row);

        selectedVisitLrn = selected.getLrn();
        NameCheckIn.setText(selected.getName());
        GSCheckIn.setText(selected.getGradeSection());
        LRNField.setText(selected.getLrn());
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
                showToast(SentHomeInformationPanel,
                        "Guardian name is required.",
                        false);
                return;
            }

            if (guardianPhone.isEmpty()) {
                showToast(SentHomeInformationPanel,
                        "Guardian phone number is required.",
                        false);
                return;
            }

            if (!guardianPhone.matches("^09\\d{9}$")) {
                showToast(SentHomeInformationPanel,
                        "Phone number must start with 09 and contain exactly 11 digits.",
                        false);
                PhoneField.requestFocus();
                return;
            }
            
            if (!guardianName.matches("[\\p{L} .'-]+")) {
                showToast(SentHomeInformationPanel,
                        "Please enter a valid guardian name.",
                        false);
                ParentGurdianName.requestFocus();
                return;
            }

         String name = NameCheckIn.getText().trim();
         String gradeSection = GSCheckIn.getText().trim();
         String lrn = LRNField.getText().trim();
         String reason = ReasonArea.getText().trim();
         String medUsed = jComboBox1.getSelectedItem().toString();
         

         try {
             visitService.checkIn(name, gradeSection, lrn, reason, medUsed, pendingmedsQty, guardianName, guardianPhone);

             if (!medUsed.equals("None")) {
                 boolean deducted = productService.useMedicine(medUsed, name , pendingmedsQty);
                 if (!deducted) {
                     showToast(CheckInPanel, "Warning: " + medUsed + " is out of stock. Stock was not deducted.", false);
                 }
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

         } catch (IOException ex) {
             showToast(CheckInPanel, "Error saving check-in: " + ex.getMessage(), false);
         }
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
            
            if(row == -1){
                JOptionPane.showMessageDialog(this, "Select a Student in the Table First...");
                
                return;
              
            }
            
            String lrn = (String) ReasonTable.getValueAt(row, 3);
            
            try{
                
                CheckinSystem record = visitService.findActiveVisit(lrn);
                
                if(record == null){
                    JOptionPane.showMessageDialog(this, "This Student has Already been sent");
                    return;
                }
                
                boolean success =  visitService.markSentHome(lrn);
                
                if(success){
                    
                    refreshTableAndCounters();
                    
                    printSentHomeSlip(record);

                    JOptionPane.showMessageDialog(this, "Student successfully sent home.");
                }else{
                    JOptionPane.showMessageDialog(this, "Error... Unable to update the Student's Status");
                }
                
            }catch(IOException ex){
                 JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
            } 
            
    }  
        //print layout
        private void printSentHomeSlip(CheckinSystem record) {

         String sentHomeTime = java.time.LocalDateTime.now().format(
                 java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a"));

         StringBuilder sb = new StringBuilder();

         sb.append("            CLINIC — STUDENT SENT HOME SLIP\n              ");
         sb.append("========================================================\n\n");

         sb.append(String.format("%-18s: %s%n", "Name", record.getName()));
         sb.append(String.format("%-18s: %s%n", "Grade/Section", record.getGradeSection()));
         sb.append(String.format("%-18s: %s%n", "LRN", record.getLrn()));
         sb.append(String.format("%-18s: %s%n", "Reason for Visit", record.getReason()));
         sb.append(String.format("%-18s: %s%n", "Medicine Used", record.getMedUsed()));
         sb.append("\n--------------------------------------------------------\n\n");

         sb.append(String.format("%-18s: %s%n", "Checked In", record.getCheckInTime()));
         sb.append(String.format("%-18s: %s%n", "Sent Home", sentHomeTime));

         sb.append("\n--------------------------------------------------------\n\n");

         sb.append("Released to (Parent/Guardian):  ____________________________\n\n");
         sb.append("Guardian Signature:             ____________________________\n\n");
         sb.append("Nurse/Staff Signature:          ____________________________\n\n");

         sb.append("\n========================================================\n");
         sb.append("        Please keep this slip for your records.\n");

         JTextArea slip = new JTextArea(sb.toString());
         slip.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 12)); // monospace = clean alignment
         slip.setLineWrap(false);

         try {
             slip.print();
         } catch (java.awt.print.PrinterException ex) {
             JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage());
         }
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
    private void checkExpiredProducts() {
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
}
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
    private javax.swing.JButton jButton2;
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
