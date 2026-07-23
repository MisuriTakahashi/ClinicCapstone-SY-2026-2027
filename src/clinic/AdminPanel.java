/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

/**
 *
 * @author PC
 */
public class AdminPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminPanel.class.getName());
    

    /**
     * Creates the administrator inventory panel.
     */
    public AdminPanel() {
        initComponents();
        configureFlatLafUi();
        statisticsContainer.setVisible(false);
        ((AbstractDocument) ExpDate.getDocument()).setDocumentFilter(new DateInputFilter());
        setLocationRelativeTo(null);
        refreshInventoryScreen();
        refreshActivityLogDisplay();
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

        jLabel6.setText("Stock overview");
        jLabel6.setFont(new Font("Segoe UI", Font.BOLD, 22));
        jLabel1.setText("Inventory activity");
        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 22));
        jLabel2.setText("Manage stock");
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 16));

        styleNavigationButton(jButton5, "Inventory", true);
        styleNavigationButton(jButton6, "Statistics", false);
        styleNavigationButton(jButton7, "← Back", false);

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
        stockTable.setToolTipText("Your available medical supplies");

        
        jButton5.addActionListener(event -> showInventory());
        jButton6.addActionListener(event -> showStatistics());
    }

   

    

    private void showStatistics() {
        jPanel4.setVisible(false);
    jPanel5.setVisible(false);
    jPanel6.setVisible(false);
    jLabel1.setVisible(false);
    jLabel6.setVisible(false);

    statisticsContainer.setVisible(true);

    styleNavigationButton(jButton5, "Inventory", false);
    styleNavigationButton(jButton6, "Statistics", true);

    jPanel1.revalidate();
    jPanel1.repaint();
    }

    private void showInventory() {
       statisticsContainer.setVisible(false);

    jPanel4.setVisible(true);
    jPanel5.setVisible(true);
    jPanel6.setVisible(true);
    jLabel1.setVisible(true);
    jLabel6.setVisible(true);

    styleNavigationButton(jButton5, "Inventory", true);
    styleNavigationButton(jButton6, "Statistics", false);

    jPanel1.revalidate();
    jPanel1.repaint();
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

    private void styleNavigationButton(JButton button, String text, boolean selected) {
        button.setText(text);
        button.setForeground(Color.BLACK);
        button.putClientProperty(FlatClientProperties.STYLE, selected
                ? "arc: 10; background: #E2E8F0; hoverBackground: #CBD5E1; borderWidth: 0; font: bold +1"
                : "arc: 10; background: #F8FAFC; hoverBackground: #E2E8F0; borderWidth: 0; font: bold +1");
    }

    private void stylePrimaryButton(JButton button, String text, Color color) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; borderWidth: 0; font: bold; margin: 7,12,7,12");
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
                 LocalDate date = LocalDate.parse(input, looseFormat); // accepts "2028-1-9" or "2028-01-09"
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);  // always outputs "2028-01-09"
    }
    
    private ProductCsvService productService = new ProductCsvService("products.csv", "inventory_activity.log");
    private ArrayList<Product> currentProducts = new ArrayList<>();
    private String selectedProductName = null;

    private void refreshActivityLogDisplay(){
        try{
             ArrayList<String> log = productService.loadActivityLog();
             StringBuilder sb = new StringBuilder();
        
                if (log.isEmpty()) {
                      sb.append("No recent activity.\n\nChanges to stock levels will be recorded here.");
                } else {
                      for (String line : log) {
                      sb.append(line).append("\n");
            }
        }

        InventoryLogs.setText(sb.toString());
        
        }catch(IOException ex){
            JOptionPane.showMessageDialog(this, "Error loading activity log: " + ex.getMessage());
        }
    }
    
    
    //eto yun nagpapakita ng mga shis sa table and inupdate
    private void refreshInventoryScreen(){
        try {
             ArrayList<Product> currentProducts = productService.loadAll();

                DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                model.setRowCount(0);

            for (Product p : currentProducts) {
                 model.addRow(new Object[]{p.getStatus(), p.getname(), p.getquantity()});
             }
                 refreshActivityLogDisplay();
                 
             } catch (IOException ex) {
                 JOptionPane.showMessageDialog(this, "Error loading inventory: " + ex.getMessage());
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

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setText("Inventory");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setText("Statistic");

        jButton7.setText("Return");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 361, Short.MAX_VALUE)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 0, 190, 650));

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
        cardWeeklyCheckIns.add(lblWeeklyTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 275, 17));

        lblWeeklyCheckInsValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblWeeklyCheckInsValue.setForeground(new java.awt.Color(29, 78, 216));
        lblWeeklyCheckInsValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWeeklyCheckInsValue.setText("0");
        cardWeeklyCheckIns.add(lblWeeklyCheckInsValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 275, 36));

        jLabel8.setForeground(new java.awt.Color(148, 163, 184));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Student Checkin This week");
        cardWeeklyCheckIns.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 275, 16));

        statisticsContainer.add(cardWeeklyCheckIns, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 64, 310, 308));
        cardWeeklyCheckIns.getAccessibleContext().setAccessibleName("");

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel10.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblInClinicTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblInClinicTitle.setForeground(new java.awt.Color(71, 85, 105));
        lblInClinicTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInClinicTitle.setText("Currently in Clinic");
        jPanel10.add(lblInClinicTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 275, 17));

        lblInClinicValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblInClinicValue.setForeground(new java.awt.Color(29, 78, 216));
        lblInClinicValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblInClinicValue.setText("0");
        jPanel10.add(lblInClinicValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 275, 36));

        jLabel9.setForeground(new java.awt.Color(148, 163, 184));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Student Waiting for release");
        jPanel10.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 275, 16));

        statisticsContainer.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(334, 64, 319, 308));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(226, 232, 240)));
        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblSentHomeTitle.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblSentHomeTitle.setForeground(new java.awt.Color(71, 85, 105));
        lblSentHomeTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSentHomeTitle.setText("Sent Home");
        jPanel11.add(lblSentHomeTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 18, 275, 17));

        lblSentHomeValue.setFont(new java.awt.Font("Segoe UI", 1, 30)); // NOI18N
        lblSentHomeValue.setForeground(new java.awt.Color(29, 78, 216));
        lblSentHomeValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSentHomeValue.setText("0");
        jPanel11.add(lblSentHomeValue, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 75, 275, 36));

        jLabel10.setForeground(new java.awt.Color(148, 163, 184));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("Student Sent home this week");
        jPanel11.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 155, 275, 16));

        statisticsContainer.add(jPanel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(669, 64, 319, 308));

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

        jPanel1.add(statisticsContainer, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 30, 1020, 620));

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
        ));
        stockTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stockTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(stockTable);

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
        new Dashboard().show();
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void QtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_QtyKeyTyped
         //char c = evt.getKeyChar();

            // Allow only numbers
                //if (!Character.isDigit(c)) {
                  //evt.consume();
                  //return;
                //}
    }//GEN-LAST:event_QtyKeyTyped

    private void AddBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBTNActionPerformed
          String name = ProductName.getText().trim();
          String expDate = ExpDate.getText().trim();
          String quantityText = Qty.getText().trim();
          
            try {
                 expDate = normalizeDate(ExpDate.getText().trim());
             } catch (DateTimeParseException ex) {
                 JOptionPane.showMessageDialog(this, "Please enter a valid date (e.g. 2028-1-9 or 2028-01-09).");
              return;
             }

          if(name.isEmpty() || expDate.isEmpty() || quantityText.isEmpty()){
              JOptionPane.showMessageDialog(this, "All Field Are Required.");
              return;
          }
          
          int quantity;
          try{
              quantity = Integer.parseInt(quantityText);
          }catch(NumberFormatException ex){
              JOptionPane.showMessageDialog(this, "Quantity must be a whole number nigga.");
              return;
          }
           try {
              productService.addItem(name, expDate, quantity);
              refreshInventoryScreen();
              ClearBtnActionPerformed(null);
            }catch (IOException ex) {
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
         int row = stockTable.getSelectedRow();
            if (row == -1) return;

            selectedProductName = (String) stockTable.getValueAt(row, 1); // Product column
            int quantity = (int) stockTable.getValueAt(row, 2);

             ProductName.setText(selectedProductName);
             Qty.setText(String.valueOf(quantity));
             // Note: expDate isn't shown in the table columns you have — see note below
    }//GEN-LAST:event_stockTableMouseClicked

    private void DeleteBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteBTNActionPerformed
             if (selectedProductName == null) {
                JOptionPane.showMessageDialog(this, "Select a product from the table first.");
                 return;
                 }

                 try {
                       boolean success = productService.deleteItem(selectedProductName);
                 if (!success) {
                         JOptionPane.showMessageDialog(this, "Product not found.");
                     } else {
                 refreshInventoryScreen();
                 ClearBtnActionPerformed(null);
                     }
            }catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + ex.getMessage());
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

            
             try {
                  boolean success = productService.editItem(selectedProductName, newName, newExpDate, newQuantity);
                      if (!success) {
                         JOptionPane.showMessageDialog(this, "Product not found.");
                      } else {
                    refreshInventoryScreen();
                    ClearBtnActionPerformed(null);
                     }
             }catch (IOException ex) {
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
          //char c = evt.getKeyChar();

        // Allow letters, spaces, and backspace
        /*if (!Character.isLetter(c)
            && !Character.isWhitespace(c)
            && c != '\b') {
            evt.consume();
         }*/
    }//GEN-LAST:event_ProductNameKeyTyped

    private void ProductNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ProductNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ProductNameActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

    private void ExpDateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ExpDateKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_ExpDateKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        UIManager.put("Component.arc", 10);
        UIManager.put("Button.arc", 10);
        UIManager.put("TextComponent.arc", 10);

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new AdminPanel().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddBTN;
    private javax.swing.JButton ClearBtn;
    private javax.swing.JButton DeleteBTN;
    private javax.swing.JButton EditBtn;
    private javax.swing.JTextField ExpDate;
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
