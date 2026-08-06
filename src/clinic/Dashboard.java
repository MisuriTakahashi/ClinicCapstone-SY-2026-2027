/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;


import java.awt.Graphics2D;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
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
    private boolean darkMode = false;
    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        ((AbstractDocument) NameCheckIn.getDocument()).setDocumentFilter(new NameInputFilter());
        setLocationRelativeTo(null);
        startDateTimeClock();
        refreshTableAndCounters();
        refreshInventoryStatusDisplay();
        medicineBox();
        InventoryStatusArea.setEditable(false);
        
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
        
        
        
        VisitPanel.putClientProperty("JComponent.arc", 25);
        SentHomePanel.putClientProperty("JComponent.arc", 25);
        CheckInPanel.putClientProperty("JComponent.arc", 25);
        InventoryPanel.putClientProperty("JComponent.arc", 25);
        
        java.awt.Color softSlate = new java.awt.Color(245, 247, 250); 
        CheckInPanel.setBackground(softSlate);
        CounterPanel.setBackground(softSlate);
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
        jButton1.putClientProperty("JButton.buttonType", "roundRect"); // Admin button
        jComboBox1.putClientProperty("JComponent.roundRect", true);
        SentHomeBTN.putClientProperty("JButton.buttonType", "roundRect");
        
        ThemeToggle.setText("Dark mode");
        ThemeToggle.addActionListener(e -> {
            darkMode = ThemeToggle.isSelected();
            applyTheme();
        });

        // Apply the starting light theme
        applyTheme();
        checkExpiredProducts();
        
    }
    private void applyTheme() {
        try {
            if (darkMode) {
                com.formdev.flatlaf.FlatDarkLaf.setup();
            } else {
                com.formdev.flatlaf.FlatLightLaf.setup();
            }
            // Ternary operator
            java.awt.Color pageBackground = darkMode
                    ? new java.awt.Color(43, 43, 43)
                    : java.awt.Color.WHITE;
            java.awt.Color panelBackground = darkMode
                    ? new java.awt.Color(60, 63, 65)
                    : new java.awt.Color(245, 247, 250);
            java.awt.Color cardBackground = darkMode
                    ? new java.awt.Color(70, 73, 75)
                    : java.awt.Color.WHITE;
            java.awt.Color inputBackground = darkMode
                    ? new java.awt.Color(48, 50, 52)
                    : java.awt.Color.WHITE;
            java.awt.Color textColor = darkMode
                    ? new java.awt.Color(235, 235, 235)
                    : new java.awt.Color(25, 25, 25);
            java.awt.Color headerColor = darkMode
                    ? new java.awt.Color(30, 76, 120)
                    : new java.awt.Color(51, 153, 255);
            java.awt.Color borderColor = darkMode
                    ? new java.awt.Color(100, 104, 108)
                    : new java.awt.Color(220, 225, 230);

            ThemeToggle.setText(darkMode ? "Light mode" : "Dark mode");
            ThemeToggle.setSelected(darkMode);

            HeaderPanel.setBackground(headerColor);
            MainPanel.setBackground(pageBackground);
            CounterPanel.setBackground(panelBackground);
            CheckInPanel.setBackground(panelBackground);
            CheckInPanel1.setBackground(panelBackground);
            InventoryPanel.setBackground(panelBackground);
            VisitPanel.setBackground(cardBackground);
            SentHomePanel.setBackground(cardBackground);
            
            SentHomeInformationPanel.setBackground(cardBackground);
            PGNameLabel.setForeground(textColor);
            PNField.setForeground(textColor);
            ParentGurdianName.setBackground(inputBackground);
            ParentGurdianName.setForeground(textColor);
            PhoneField.setBackground(inputBackground);
            PhoneField.setForeground(textColor);
            
            

            // Header and section titles
            jLabel3.setForeground(java.awt.Color.WHITE);
            DateTimeLabel.setForeground(java.awt.Color.WHITE);
            InventoryLabel.setForeground(textColor);
            OverviewLabel.setForeground(textColor);
            LogsLabel.setForeground(textColor);
            StudentCheckinLabel.setForeground(textColor);

            // Summary cards and check-in labels
            jLabel1.setForeground(textColor);
            jLabel4.setForeground(textColor);
            VisitCounter.setForeground(textColor);
            SentHomeCount.setForeground(textColor);
            jLabel7.setForeground(textColor);
            jLabel8.setForeground(textColor);
            LRNLabel.setForeground(textColor);
            jLabel9.setForeground(textColor);
            jLabel11.setForeground(textColor);

            // Inputs, text areas, tables, and their scroll panes
            NameCheckIn.setBackground(inputBackground);
            NameCheckIn.setForeground(textColor);
            LRNField.setBackground(inputBackground);
            LRNField.setForeground(textColor);
            GSCheckIn.setBackground(inputBackground);
            GSCheckIn.setForeground(textColor);
            ReasonArea.setBackground(inputBackground);
            ReasonArea.setForeground(textColor);
            InventoryStatusArea.setBackground(inputBackground);
            InventoryStatusArea.setForeground(textColor);
            ReasonTable.setBackground(inputBackground);
            ReasonTable.setForeground(textColor);
            ReasonTable.getTableHeader().setBackground(cardBackground);
            ReasonTable.getTableHeader().setForeground(textColor);
            jComboBox1.setBackground(inputBackground);
            jComboBox1.setForeground(textColor);

            jScrollPane1.getViewport().setBackground(inputBackground);
            jScrollPane2.getViewport().setBackground(inputBackground);
            jScrollPane3.getViewport().setBackground(inputBackground);
            jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
            jScrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));
            jScrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(borderColor));

            for (java.awt.Window window : java.awt.Window.getWindows()) {
                javax.swing.SwingUtilities.updateComponentTreeUI(window);
                window.pack();
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
 
   //ComboBox problem 
    private void medicineBox(){
        try{
            ArrayList<Product> products = productService.loadAll();
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            
            for(Product p : products){
                model.addElement(p.getname());
            }
            
            jComboBox1.setModel(model);
            
        }catch(IOException ex){
            JOptionPane.showMessageDialog(this, "Error Laoding message" + ex.getMessage());
        }
    }


    //ps this will help display the inventory on the "inventory status"
    private VisitCsvService visitService = new VisitCsvService("visits.csv");
    private ProductCsvService productService = new ProductCsvService("products.csv", "inventory_activity.log");
    
    private void refreshInventoryStatusDisplay(){
        try{
             ArrayList<Product> products = productService.loadAll();
                 StringBuilder sb = new StringBuilder();

                 if (products.isEmpty()) {
                     sb.append("No items in inventory yet.");
                 } else {
                     for (Product p : products) {
                      sb.append(p.getname())
                          .append(" — ")
                          .append(p.getquantity())
                          .append(" pcs — ")
                          .append(p.getStatus())
                          .append("\n");
            }
        }
                 
             InventoryStatusArea.setText(sb.toString());
        
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
                    v.getName(),
                    v.getGradeSection(),
                    v.getLrn(),
                    v.getReason(),
                    v.getGuardianName(),
                    v.getGuardianPhoneNums()
            });

        }

             int[] counts = visitService.getTodayCounts();
        
            VisitCounter.setText(String.valueOf(counts[0]));
            SentHomeCount.setText(String.valueOf(counts[1]));

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

        MainPanel = new javax.swing.JPanel();
        HeaderPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        DateTimeLabel = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        CounterPanel = new javax.swing.JPanel();
        VisitPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        VisitCounter = new javax.swing.JLabel();
        SentHomePanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        SentHomeCount = new javax.swing.JLabel();
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
        InventoryPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        InventoryStatusArea = new javax.swing.JTextArea();
        InventoryLabel = new javax.swing.JLabel();
        CheckInPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        ReasonTable = new javax.swing.JTable();
        OverviewLabel = new javax.swing.JLabel();
        LogsLabel = new javax.swing.JLabel();
        StudentCheckinLabel = new javax.swing.JLabel();
        ThemeToggle = new javax.swing.JToggleButton();
        SentHomeInformationPanel = new javax.swing.JPanel();
        ParentGurdianName = new javax.swing.JTextField();
        PGNameLabel = new javax.swing.JLabel();
        PNField = new javax.swing.JLabel();
        PhoneField = new javax.swing.JTextField();
        FinishBTN = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setResizable(false);

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

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addGap(30, 30, 30))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 11, Short.MAX_VALUE))
        );

        CounterPanel.setBackground(new java.awt.Color(229, 226, 226));
        CounterPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
                .addContainerGap(64, Short.MAX_VALUE))
        );
        VisitPanelLayout.setVerticalGroup(
            VisitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VisitPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VisitCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        SentHomePanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Sent Home");

        SentHomeCount.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        SentHomeCount.setForeground(new java.awt.Color(0, 0, 0));
        SentHomeCount.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SentHomeCount.setText("0");

        javax.swing.GroupLayout SentHomePanelLayout = new javax.swing.GroupLayout(SentHomePanel);
        SentHomePanel.setLayout(SentHomePanelLayout);
        SentHomePanelLayout.setHorizontalGroup(
            SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentHomePanelLayout.createSequentialGroup()
                .addContainerGap(58, Short.MAX_VALUE)
                .addGroup(SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentHomePanelLayout.createSequentialGroup()
                        .addComponent(SentHomeCount, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGap(68, 68, 68))
        );
        SentHomePanelLayout.setVerticalGroup(
            SentHomePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SentHomePanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SentHomeCount, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout CounterPanelLayout = new javax.swing.GroupLayout(CounterPanel);
        CounterPanel.setLayout(CounterPanelLayout);
        CounterPanelLayout.setHorizontalGroup(
            CounterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CounterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(VisitPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(SentHomePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        CounterPanelLayout.setVerticalGroup(
            CounterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CounterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CounterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(VisitPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(SentHomePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
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

        javax.swing.GroupLayout CheckInPanelLayout = new javax.swing.GroupLayout(CheckInPanel);
        CheckInPanel.setLayout(CheckInPanelLayout);
        CheckInPanelLayout.setHorizontalGroup(
            CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(GSCheckIn)
                    .addComponent(NameCheckIn)
                    .addComponent(LRNField)
                    .addComponent(jScrollPane1)
                    .addGroup(CheckInPanelLayout.createSequentialGroup()
                        .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(CheckInPanelLayout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel9)
                            .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addGroup(CheckInPanelLayout.createSequentialGroup()
                                .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(EditBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(40, 40, 40)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        CheckInPanelLayout.setVerticalGroup(
            CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NameCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(GSCheckIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LRNField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboBox1)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EditBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
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
            .addGroup(InventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        InventoryPanelLayout.setVerticalGroup(
            InventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, InventoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
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
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Grade/Section", "LRN", "Reason", "Parent/Guardian Name", "Phone number"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
        }

        javax.swing.GroupLayout CheckInPanel1Layout = new javax.swing.GroupLayout(CheckInPanel1);
        CheckInPanel1.setLayout(CheckInPanel1Layout);
        CheckInPanel1Layout.setHorizontalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        CheckInPanel1Layout.setVerticalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addContainerGap())
        );

        OverviewLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        OverviewLabel.setForeground(new java.awt.Color(0, 0, 0));
        OverviewLabel.setText("Overview");

        LogsLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        LogsLabel.setForeground(new java.awt.Color(0, 0, 0));
        LogsLabel.setText("Check-in Logs");

        StudentCheckinLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        StudentCheckinLabel.setForeground(new java.awt.Color(0, 0, 0));
        StudentCheckinLabel.setText("Student Check-in");

        ThemeToggle.setBackground(new java.awt.Color(255, 255, 255));
        ThemeToggle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThemeToggle.setForeground(new java.awt.Color(0, 0, 0));
        ThemeToggle.setText("Mode");
        ThemeToggle.addActionListener(this::ThemeToggleActionPerformed);

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(183, 183, 183)
                .addComponent(StudentCheckinLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LogsLabel)
                .addGap(267, 267, 267))
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(CounterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(220, 220, 220)
                                .addComponent(OverviewLabel)))
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE)
                                .addComponent(InventoryLabel)
                                .addGap(235, 235, 235))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(InventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CheckInPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(29, 29, 29))
        );
        MainPanelLayout.setVerticalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addComponent(HeaderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(InventoryLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(OverviewLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(InventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(CounterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LogsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout SentHomeInformationPanelLayout = new javax.swing.GroupLayout(SentHomeInformationPanel);
        SentHomeInformationPanel.setLayout(SentHomeInformationPanelLayout);
        SentHomeInformationPanelLayout.setHorizontalGroup(
            SentHomeInformationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addComponent(ParentGurdianName, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(128, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, SentHomeInformationPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FinishBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(238, 238, 238))
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
                .addGap(45, 45, 45)
                .addComponent(FinishBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(239, 239, 239)
                    .addComponent(SentHomeInformationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(240, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(217, 217, 217)
                    .addComponent(SentHomeInformationPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(217, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new AdminPanel().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    
    //edit btn
    private void EditBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EditBTNActionPerformed
            
        if (selectedVisitLrn == null) {
    
               JOptionPane.showMessageDialog(this, "Select a student from the table first.");
               return;
        }

          String newName = NameCheckIn.getText().trim();
          String newGradeSection = GSCheckIn.getText().trim();
          String newReason = ReasonArea.getText().trim();
          Object selectedMed = jComboBox1.getSelectedItem();

        if (newName.isEmpty() || newGradeSection.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Grade/Section cannot be empty.");
            return;
        }

        if (selectedMed == null) {
            
            JOptionPane.showMessageDialog(this, "Your inventory of medicine might be empty, please check first.");
            return;
        }
        
        String newMedUsed = selectedMed.toString();

        try {
            boolean success = visitService.editVisit(selectedVisitLrn, newName, newGradeSection, newReason, newMedUsed);

            if (!success) {
                
               JOptionPane.showMessageDialog(this, "Could not find that student's record.");
            
            } else {
                
                refreshTableAndCounters();
                clearCheckInForm();
                JOptionPane.showMessageDialog(this, "Student record updated.");
            }

        } catch (IOException ex) {
            
            JOptionPane.showMessageDialog(this, "Error editing record: " + ex.getMessage());
          
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
     Object selectedMed = jComboBox1.getSelectedItem();
  
    //Medicine Null Check
    if (selectedMed == null) {
        showToast(CheckInPanel, "Inventory error: Medicine list is empty!", false);
        return;
    }

    //Required Fields Validation
    if (name.isEmpty() || gradeSection.isEmpty() || lrn.isEmpty()) {
        showToast(CheckInPanel, "Name, Grade/Section, and LRN are required!", false);
        return;
    }

    // LRN Format Validation
    if (!lrn.matches("\\d{12}")) {
        showToast(CheckInPanel, "LRN must contain exactly 12 digits.", false);
        LRNField.requestFocus();
        return;
    }
    //Required Fields for Reason
    if(reason.isEmpty()){
        showToast(CheckInPanel, "Reason is needed to proceed on the check in" , false );
        return;
    }
    
    String medUsed = selectedMed.toString();
    Product medProduct; 
    
     try {
            
        medProduct = productService.findByName(medUsed);
            
         if (medProduct != null && medProduct.isExpired()) {
             int choice = JOptionPane.showConfirmDialog(this,
              medUsed + " is expired and cannot be given.\n\n"
              + "Click YES to check in this student WITHOUT recording medicine use.\n"
              + "Click NO to go back and pick a different medicine.",
              "Expired Medicine",
                 JOptionPane.YES_NO_OPTION,
                 JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                medUsed = "None"; 
            } else {
                return;
            }
        }
         
         if(medProduct != null && !medUsed.equals("none") && medProduct.getquantity() <= 0){
              int choice = JOptionPane.showConfirmDialog(this,
                medUsed + " has 0 stock remaining.\n\n"
                + "Do you want to proceed without using meds?",
                "Out of Stock",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
              if (choice == JOptionPane.YES_OPTION) {
                     medUsed = "None";
                     
              }else{
                  return;
              }
         }
             } catch (IOException ex) {
              JOptionPane.showMessageDialog(this, "ERROR" + ex.getMessage());
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
        SentHomeInformationPanel.show();
        
        //testing kung gumagana yun Check btn
        System.out.println("Showing Guardian Panel...");

    } catch (IOException ex) {
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

    private void FinishBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FinishBTNActionPerformed
            String guardianName = ParentGurdianName.getText().trim();
            String guardianPhone = PhoneField.getText().trim();

         if (guardianName.isEmpty() || guardianPhone.isEmpty()) {
             showToast(SentHomeInformationPanel, "Guardian name and phone number are required.", false);
             return;
         }

         String name = NameCheckIn.getText().trim();
         String gradeSection = GSCheckIn.getText().trim();
         String lrn = LRNField.getText().trim();
         String reason = ReasonArea.getText().trim();
         String medUsed = jComboBox1.getSelectedItem().toString();

         try {
             visitService.checkIn(name, gradeSection, lrn, reason, medUsed, guardianName, guardianPhone);

             if (!medUsed.equals("None")) {
                 boolean deducted = productService.useMedicine(medUsed, name);
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

             showToast(CheckInPanel, name + " checked in successfully. Guardian info recorded.", true);

         } catch (IOException ex) {
             showToast(CheckInPanel, "Error saving check-in: " + ex.getMessage(), false);
         }
    }//GEN-LAST:event_FinishBTNActionPerformed
   
    private void SentHomeBTNActionPerformed(java.awt.event.ActionEvent evt) {                                         
            
            int row = ReasonTable.getSelectedRow();
            
            if(row == -1){
                JOptionPane.showMessageDialog(this, "Select a Student in the Table First...");
                
                return;
              
            }
            
            String lrn = (String) ReasonTable.getValueAt(row, 2);
            
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
   
    private void printSentHomeSlip(CheckinSystem record) {
            String slipText =
            "CLINIC — SENT HOME SLIP\n" +
        "====================================\n" +
        "Name: " + record.getName() + "\n" +
        "Grade/Section: " + record.getGradeSection() + "\n" +
        "LRN: " + record.getLrn() + "\n" +
        "Reason for record visit: " + record.getReason() + "\n" +
        "Medicine Used: " + record.getMedUsed() + "\n" +
        "Checked In: " + record.getCheckInTime() + "\n" +
        "Sent Home: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n";

             JTextArea slip = new JTextArea(slipText);
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
        
        ArrayList<Product> products = productService.loadAll();
        ArrayList<String> expiredItems = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        // Try multiple date formatters if needed (e.g. yyyy-MM-dd or MM/dd/yyyy)
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Product p : products) {
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        com.formdev.flatlaf.FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CheckInBTN;
    private javax.swing.JPanel CheckInPanel;
    private javax.swing.JPanel CheckInPanel1;
    private javax.swing.JPanel CounterPanel;
    private javax.swing.JLabel DateTimeLabel;
    private javax.swing.JButton EditBTN;
    private javax.swing.JButton FinishBTN;
    private javax.swing.JTextField GSCheckIn;
    private javax.swing.JPanel HeaderPanel;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}
