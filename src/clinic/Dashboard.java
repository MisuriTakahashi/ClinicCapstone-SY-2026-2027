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
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
        setLocationRelativeTo(null);
        startDateTimeClock();
        refreshTableAndCounters();
        
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
        PrintBTN.putClientProperty("JButton.buttonType", "roundRect");
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
    
    private VisitCsvService visitService = new VisitCsvService("visits.csv");
    
    private void refreshTableAndCounters(){
        try{
            DefaultTableModel model = (DefaultTableModel) ReasonTable.getModel();
            model.setRowCount(0);

             for (CheckinSystem v : visitService.loadAll()) {
            model.addRow(new Object[]{v.getName(), v.getGradeSection(), v.getLrn(), v.getReason()});
        }

        int[] counts = visitService.getTodayCounts();
        VisitCounter.setText(String.valueOf(counts[0]));
        SentHomeCount.setText(String.valueOf(counts[1]));
        
        }catch(IOException ex){
            JOptionPane.showMessageDialog(this, "Error loading visits: " + ex.getMessage());
        }
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
        ThemeToggle = new javax.swing.JToggleButton();
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
        PrintBTN = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFocusable(false);
        setResizable(false);

        MainPanel.setBackground(new java.awt.Color(255, 255, 255));

        HeaderPanel.setBackground(new java.awt.Color(51, 153, 255));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Clinic DashBoard");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Admin Panel");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        DateTimeLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N

        ThemeToggle.setBackground(new java.awt.Color(255, 255, 255));
        ThemeToggle.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        ThemeToggle.setForeground(new java.awt.Color(0, 0, 0));
        ThemeToggle.setText("Mode");

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Logout");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout HeaderPanelLayout = new javax.swing.GroupLayout(HeaderPanel);
        HeaderPanel.setLayout(HeaderPanelLayout);
        HeaderPanelLayout.setHorizontalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderPanelLayout.createSequentialGroup()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(DateTimeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 564, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(159, 159, 159)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addGap(17, 17, 17))
        );
        HeaderPanelLayout.setVerticalGroup(
            HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(HeaderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HeaderPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DateTimeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HeaderPanelLayout.createSequentialGroup()
                        .addGap(0, 3, Short.MAX_VALUE)
                        .addGroup(HeaderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ThemeToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        CounterPanel.setBackground(new java.awt.Color(229, 226, 226));

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
                .addContainerGap(79, Short.MAX_VALUE))
        );
        VisitPanelLayout.setVerticalGroup(
            VisitPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(VisitPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(VisitCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
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
                .addContainerGap(78, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Name");

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Grade/Section");

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));
        jLabel9.setText("Reason for Visit");

        CheckInBTN.setBackground(new java.awt.Color(0, 102, 204));
        CheckInBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        CheckInBTN.setForeground(new java.awt.Color(255, 255, 255));
        CheckInBTN.setText("Check In");
        CheckInBTN.addActionListener(this::CheckInBTNActionPerformed);

        PrintBTN.setBackground(new java.awt.Color(0, 102, 204));
        PrintBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        PrintBTN.setForeground(new java.awt.Color(255, 255, 255));
        PrintBTN.setText("Print");
        PrintBTN.addActionListener(this::PrintBTNActionPerformed);

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setText("Medicine used");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        SentHomeBTN.setBackground(new java.awt.Color(0, 102, 204));
        SentHomeBTN.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        SentHomeBTN.setForeground(new java.awt.Color(255, 255, 255));
        SentHomeBTN.setText("Sent Home");
        SentHomeBTN.addActionListener(this::SentHomeBTNActionPerformed);

        LRNLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
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
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LRNLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(CheckInPanelLayout.createSequentialGroup()
                            .addGap(3, 3, 3)
                            .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(CheckInPanelLayout.createSequentialGroup()
                                    .addComponent(jLabel11)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel9)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(CheckInPanelLayout.createSequentialGroup()
                            .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(PrintBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel8)
                        .addComponent(jLabel7)
                        .addComponent(GSCheckIn)
                        .addComponent(NameCheckIn)
                        .addComponent(LRNField)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        CheckInPanelLayout.setVerticalGroup(
            CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CheckInPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
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
                .addGap(18, 18, 18)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(CheckInPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PrintBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckInBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SentHomeBTN, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        InventoryPanel.setBackground(new java.awt.Color(226, 226, 226));
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
                .addGap(14, 14, 14)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        InventoryPanelLayout.setVerticalGroup(
            InventoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InventoryPanelLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        InventoryLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        InventoryLabel.setForeground(new java.awt.Color(0, 0, 0));
        InventoryLabel.setText("Inventory Status");

        CheckInPanel1.setBackground(new java.awt.Color(226, 226, 226));

        ReasonTable.setBackground(new java.awt.Color(255, 255, 255));
        ReasonTable.setForeground(new java.awt.Color(0, 0, 0));
        ReasonTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Name", "Grade/Section", "LRN", "Reason"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(ReasonTable);
        if (ReasonTable.getColumnModel().getColumnCount() > 0) {
            ReasonTable.getColumnModel().getColumn(0).setResizable(false);
            ReasonTable.getColumnModel().getColumn(1).setResizable(false);
            ReasonTable.getColumnModel().getColumn(2).setResizable(false);
            ReasonTable.getColumnModel().getColumn(3).setResizable(false);
        }

        javax.swing.GroupLayout CheckInPanel1Layout = new javax.swing.GroupLayout(CheckInPanel1);
        CheckInPanel1.setLayout(CheckInPanel1Layout);
        CheckInPanel1Layout.setHorizontalGroup(
            CheckInPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CheckInPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
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

        javax.swing.GroupLayout MainPanelLayout = new javax.swing.GroupLayout(MainPanel);
        MainPanel.setLayout(MainPanelLayout);
        MainPanelLayout.setHorizontalGroup(
            MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(HeaderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(MainPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(MainPanelLayout.createSequentialGroup()
                                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(CounterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, MainPanelLayout.createSequentialGroup()
                                        .addGap(190, 190, 190)
                                        .addComponent(OverviewLabel)))
                                .addGap(18, 18, 18)
                                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(InventoryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, MainPanelLayout.createSequentialGroup()
                                        .addComponent(InventoryLabel)
                                        .addGap(160, 160, 160)))))
                        .addContainerGap(30, Short.MAX_VALUE))
                    .addGroup(MainPanelLayout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(StudentCheckinLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(LogsLabel)
                        .addGap(254, 254, 254))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LogsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(StudentCheckinLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CheckInPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CheckInPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(MainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        new AdminPanel().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void PrintBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrintBTNActionPerformed
             PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable((graphics, pageFormat, pageIndex) -> {
                    if (pageIndex > 0) return Printable.NO_SUCH_PAGE;

                 Graphics2D g2 = (Graphics2D) graphics;
                 g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                 ReasonTable.printAll(g2);
                 return Printable.PAGE_EXISTS;
    });

             boolean ok = job.printDialog();
                     if (ok) {
                          try {
                              job.print();
                } catch (PrinterException ex) {
                     JOptionPane.showMessageDialog(this, "Printing Failed:\n" + ex.getMessage());
                }
            }
    }//GEN-LAST:event_PrintBTNActionPerformed

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
        
            if (!lrn.matches("\\d{12}")) {
                JOptionPane.showMessageDialog( this,"LRN must contain exactly 12 digits.");
                LRNField.requestFocus();
                return;
            }
        
        if(name.isEmpty() || gradeSection.isEmpty() || lrn.isEmpty()){
            JOptionPane.showMessageDialog(this, "Name, Grade/Section and LRN are NEEDED to get CHECK IN...");
            return;
        }
            try {
                 if (visitService.isCurrentlyCheckedIn(lrn)) {
                 JOptionPane.showMessageDialog(this, "This student is already checked in.");
                 return;
             }

             visitService.checkIn(name, gradeSection, lrn, reason, medUsed);
          refreshTableAndCounters();

            NameCheckIn.setText("");
            GSCheckIn.setText("");
            LRNField.setText("");
            ReasonArea.setText("");

         } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving check-in: " + ex.getMessage());
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
    private void SentHomeBTNActionPerformed(java.awt.event.ActionEvent evt) {                                         
            int row = ReasonTable.getSelectedRow();
            
            if(row == -1){
                JOptionPane.showMessageDialog(this, "Select a Student in the Table First...");
                return;
                }
            String lrn = (String) ReasonTable.getValueAt(row, 2);
            
            try{
                boolean success = visitService.markSentHome(lrn);
                
                if(!success){
                    JOptionPane.showMessageDialog(this, "This Student has Already been sent");
                }else{
                    refreshTableAndCounters();
                }
                
            }catch(IOException ex){
                 JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage());
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
    private javax.swing.JButton PrintBTN;
    private javax.swing.JTextArea ReasonArea;
    private javax.swing.JTable ReasonTable;
    private javax.swing.JButton SentHomeBTN;
    private javax.swing.JLabel SentHomeCount;
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
