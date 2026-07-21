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
import java.awt.GridLayout;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author PC
 */
public class AdminPanel extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AdminPanel.class.getName());
    private JPanel statisticsPanel;

    /**
     * Creates the administrator inventory panel.
     */
    public AdminPanel() {
        initComponents();
        configureFlatLafUi();
        setLocationRelativeTo(null);
        refreshInventoryScreen();
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
        jTextArea1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Recent stock activity will appear here.");
        jTextArea1.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,0,0");
        jTextArea1.setText("No recent activity\n\nChanges to stock levels will be recorded here.");
        jTextArea1.setEditable(false);
        jTextArea1.setForeground(Color.decode("#64748B"));
        jTextArea1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        jTextArea1.setMargin(new java.awt.Insets(18, 18, 18, 18));
        jTextArea1.setBackground(surface);
        stockTable.setToolTipText("Your available medical supplies");

        createStatisticsPanel();
        jButton5.addActionListener(event -> showInventory());
        jButton6.addActionListener(event -> showStatistics());
    }

    private void createStatisticsPanel() {
        statisticsPanel = new JPanel(new BorderLayout(0, 18));
        statisticsPanel.setBackground(Color.decode("#F8FAFC"));
        statisticsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM d");

        JLabel title = new JLabel("Weekly student check-in report");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        JLabel description = new JLabel("Reporting period: " + weekStart.format(dateFormat) + " - " + weekEnd.format(dateFormat));
        description.setForeground(Color.decode("#64748B"));
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JPanel titleStack = new JPanel(new GridLayout(2, 1, 0, 3));
        titleStack.setOpaque(false);
        titleStack.add(title);
        titleStack.add(description);
        heading.add(titleStack, BorderLayout.WEST);

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setOpaque(false);
        cards.add(createStatisticCard("Weekly check-ins", "0", "Students checked in this week"));
        cards.add(createStatisticCard("Currently in clinic", "0", "Students awaiting release"));
        cards.add(createStatisticCard("Sent home", "0", "Students sent home this week"));

        JPanel summary = new JPanel(new BorderLayout());
        styleCard(summary, Color.WHITE, Color.decode("#E2E8F0"));
        JLabel summaryTitle = new JLabel("Daily check-ins");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setBorder(BorderFactory.createEmptyBorder(20, 22, 10, 22));
        summary.add(summaryTitle, BorderLayout.NORTH);
        summary.add(createWeeklyActivity(weekStart), BorderLayout.CENTER);

        statisticsPanel.add(heading, BorderLayout.NORTH);
        statisticsPanel.add(cards, BorderLayout.CENTER);
        statisticsPanel.add(summary, BorderLayout.SOUTH);
        jPanel1.add(statisticsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 82, 987, 548));
        statisticsPanel.setVisible(false);
    }

    private JPanel createWeeklyActivity(LocalDate weekStart) {
        JPanel activity = new JPanel(new GridLayout(1, 7, 10, 0));
        activity.setOpaque(false);
        activity.setBorder(BorderFactory.createEmptyBorder(8, 22, 22, 22));
        DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("EEE");
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d");

        for (int day = 0; day < 7; day++) {
            LocalDate date = weekStart.plusDays(day);
            JPanel dayCard = new JPanel(new GridLayout(3, 1, 0, 3));
            dayCard.setBackground(Color.decode("#F8FAFC"));
            dayCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode("#E2E8F0")), BorderFactory.createEmptyBorder(10, 6, 10, 6)));
            JLabel dayName = new JLabel(date.format(dayFormat), SwingConstants.CENTER);
            dayName.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayName.setForeground(Color.decode("#475569"));
            JLabel dateNumber = new JLabel(date.format(dateFormat), SwingConstants.CENTER);
            dateNumber.setFont(new Font("Segoe UI", Font.BOLD, 20));
            dateNumber.setForeground(Color.decode("#1D4ED8"));
            JLabel count = new JLabel("0 check-ins", SwingConstants.CENTER);
            count.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            count.setForeground(Color.decode("#64748B"));
            dayCard.add(dayName);
            dayCard.add(dateNumber);
            dayCard.add(count);
            activity.add(dayCard);
        }
        return activity;
    }

    private JPanel createStatisticCard(String title, String value, String caption) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5));
        styleCard(card, Color.WHITE, Color.decode("#E2E8F0"));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E2E8F0")), BorderFactory.createEmptyBorder(18, 20, 18, 20)));
        JLabel cardTitle = new JLabel(title);
        cardTitle.setForeground(Color.decode("#475569"));
        cardTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        JLabel cardValue = new JLabel(value);
        cardValue.setForeground(Color.decode("#1D4ED8"));
        cardValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        JLabel cardCaption = new JLabel(caption);
        cardCaption.setForeground(Color.decode("#94A3B8"));
        cardCaption.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        card.add(cardTitle);
        card.add(cardValue);
        card.add(cardCaption);
        return card;
    }

    private void showStatistics() {
        jPanel4.setVisible(false);
        jPanel5.setVisible(false);
        jPanel6.setVisible(false);
        jLabel1.setVisible(false);
        jLabel6.setVisible(false);
        statisticsPanel.setVisible(true);
        styleNavigationButton(jButton5, "Inventory", false);
        styleNavigationButton(jButton6, "Statistics", true);
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    private void showInventory() {
        statisticsPanel.setVisible(false);
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
    
    private ProductCsvService productService = new ProductCsvService("products.csv", "inventory_activity.log");
    private ArrayList<Product> currentProducts = new ArrayList<>();
    private String selectedProductName = null;

    
    private void refreshInventoryScreen(){
        try {
        ArrayList<Product> currentProducts = productService.loadAll();

        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
        model.setRowCount(0);

        for (Product p : currentProducts) {
            model.addRow(new Object[]{p.getStatus(), p.getname(), p.getquantity()});
        }

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
        jTextArea1 = new javax.swing.JTextArea();
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
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1200, -1));

        jPanel3.setBackground(new java.awt.Color(204, 204, 204));
        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setText("Inventory");

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

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addGap(18, 18, 18))
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
        // TODO add your handling code here:
    }//GEN-LAST:event_QtyKeyTyped

    private void AddBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBTNActionPerformed
          String name = ProductName.getText().trim();
          String expDate = ExpDate.getText().trim();
          String quantityText = Qty.getText().trim();

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
             String newExpDate = ExpDate.getText().trim();
             String quantityText = Qty.getText().trim();

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
    private javax.swing.JTextField ProductName;
    private javax.swing.JTextField Qty;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTable stockTable;
    // End of variables declaration//GEN-END:variables
}
