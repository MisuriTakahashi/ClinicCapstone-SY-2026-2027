/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

/**
 *
 * @author PC
 */
public class FirstRunSetup extends javax.swing.JFrame {
    private static final int MAX_HEAD_ADMINS = 10;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FirstRunSetup.class.getName());

    // ==========================================
    // Head Admin creation wizard state
    // (moved over from HeadAdminMakerUi)
    // ==========================================
    private int totalHeadAdmins;
    private int currentIndex;
    private final java.util.List<AccountData.PendingHeadAdmin> pendingHeadAdmins = new java.util.ArrayList<>();
    private final AccountData accountData = new AccountData();

    /**
     * Creates new form FirstRunSetup
     */
    public FirstRunSetup() {
        
        initComponents(); 
    applyFlatLafStyles();
    setLocationRelativeTo(null);
    try {
        setIconImage(AppIcon.getIcon());
    } catch (Exception e) {
        // Fallback
    }
       CreatePanel.setVisible(false);
    }
    
    private void showStep() {

        
        NumberPlaceHolder.setText(currentIndex + "/" + totalHeadAdmins);

        AccNameField.setText("");
        AccPasswordField.setText("");
        ConfirmPasswordField1.setText("");

        boolean isFinalStep = currentIndex == totalHeadAdmins;

        // NextButton1 = "Confirm" (final step), NextButton2 = "Next" (non-final steps)
        ConfirmBtn.setVisible(isFinalStep);
        NextBtn.setVisible(!isFinalStep);

        AccNameField.requestFocusInWindow();
    }
      private void handleStepSubmit() {

        String name =
                AccNameField.getText() == null
                ? "" : AccNameField.getText().trim();

        char[] password = AccPasswordField.getPassword();
        char[] confirm = ConfirmPasswordField1.getPassword();

        try {

            String error = validateHeadAdminStep(name, password, confirm);

            if (error != null) {
                javax.swing.JOptionPane.showMessageDialog(
                        this,
                        error,
                        "Invalid Entry",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            String passwordHash = PasswordHasher.hashPassword(password);

            pendingHeadAdmins.add(
                    new AccountData.PendingHeadAdmin(name, passwordHash));

            if (currentIndex == totalHeadAdmins) {
                finishSetup();
            } else {
                currentIndex++;
                showStep();
            }

        } finally {
            java.util.Arrays.fill(password, '\0');
            java.util.Arrays.fill(confirm, '\0');
        }
    }

    private String validateHeadAdminStep(
            String name, char[] password, char[] confirm) {

        if (name.isEmpty()) {
            return "Username is required.";
        }

        if (name.length() > 255) {
            return "Username must be 255 characters or fewer.";
        }

        if (password.length == 0) {
            return "Password is required.";
        }

        if (confirm.length == 0) {
            return "Please confirm the password.";
        }

        if (!java.util.Arrays.equals(password, confirm)) {
            return "Passwords do not match.";
        }

        for (AccountData.PendingHeadAdmin pending : pendingHeadAdmins) {
            if (pending.name().equalsIgnoreCase(name)) {
                return "That username is already used for "
                        + "another Head Admin in this setup.";
            }
        }

        try {
            if (accountData.nameExists(name)) {
                return "That username already exists.";
            }
        } catch (java.sql.SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE,
                    "Failed to check existing accounts.", ex);
            return "Could not verify the username right now. "
                    + "Please try again.";
        }

        return null;
    }

    private void finishSetup() {

        try {
            accountData.createInitialHeadAdmins(pendingHeadAdmins);

        } catch (java.sql.SQLException | SecurityException ex) {

            logger.log(java.util.logging.Level.SEVERE,
                    "Failed to create Head Admin accounts.", ex);

            javax.swing.JOptionPane.showMessageDialog(
                    this,
                    "Could not create the Head Admin accounts: "
                    + ex.getMessage(),
                    "Setup Failed",
                    javax.swing.JOptionPane.ERROR_MESSAGE);

            return;

        } finally {
            pendingHeadAdmins.clear();
        }

        dispose();
        new LoginUi().setVisible(true);
    }
    
    
   private void applyFlatLafStyles() {
    // Window Base Style
    // NOTE: "$defaultFont.family" / "$font.family" are not valid FlatLaf
    // UI-default keys, which is what was causing the
    // "font.family not found in UI defaults" errors. Omitting the family
    // simply keeps each component's current font family while still
    // letting FlatLaf.style control size/weight/color.
    getRootPane().putClientProperty("FlatLaf.style", "font: 14");
 
    // ==========================================
    // 1. STYLE: Number Input Panel (jPanel2)
    // ==========================================
    NumberTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    NumberTxt.putClientProperty("JTextField.placeholderText", "e.g., 1");
    NumberTxt.putClientProperty("JTextField.arc", 12);
    NumberTxt.putClientProperty("JTextField.outline", "#CBD5E1");
    NumberTxt.putClientProperty("JTextField.padding", new java.awt.Insets(6, 10, 6, 10));
 
    ContinueBtn.putClientProperty("JButton.buttonType", "roundRect");
    ContinueBtn.putClientProperty("JButton.arc", 12);
 
    // ==========================================
    // 2. STYLE: Create Panel (jPanel3 Controls)
    // ==========================================
    
    // Header & Badge Styling
    jLabel7.putClientProperty("FlatLaf.style", "font: bold 22; foreground: #0F172A;");
    NumberPlaceHolder.putClientProperty("FlatLaf.style", "font: bold 22; foreground: #0284C7;");
 
    // Form Field Labels
    AccountNameLabel.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #475569;");
    AccountPasswordLabel.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #475569;");
    ConfirmPasswordLabel.putClientProperty("FlatLaf.style", "font: bold 13; foreground: #475569;");
 
    // Text & Password Input Fields
    AccNameField.putClientProperty("JTextField.placeholderText", "Enter admin username...");
    AccNameField.putClientProperty("JTextField.arc", 12);
    AccNameField.putClientProperty("JTextField.outline", "#CBD5E1");
    AccNameField.putClientProperty("JTextField.padding", new java.awt.Insets(6, 10, 6, 10));
 
    AccPasswordField.putClientProperty("JTextField.placeholderText", "Enter password...");
    AccPasswordField.putClientProperty("JTextField.arc", 12);
    AccPasswordField.putClientProperty("JTextField.outline", "#CBD5E1");
    AccPasswordField.putClientProperty("JTextField.padding", new java.awt.Insets(6, 10, 6, 10));
    AccPasswordField.putClientProperty("JPasswordField.showRevealButton", true); // Optional eye button to toggle visibility
 
    ConfirmPasswordField1.putClientProperty("JTextField.placeholderText", "Re-enter password...");
    ConfirmPasswordField1.putClientProperty("JTextField.arc", 12);
    ConfirmPasswordField1.putClientProperty("JTextField.outline", "#CBD5E1");
    ConfirmPasswordField1.putClientProperty("JTextField.padding", new java.awt.Insets(6, 10, 6, 10));
    ConfirmPasswordField1.putClientProperty("JPasswordField.showRevealButton", true);
 
    // Buttons (NextButton1 = Next / Intermediate, NextButton2 = Final Confirm)
    ConfirmBtn.putClientProperty("JButton.buttonType", "roundRect");
    ConfirmBtn.putClientProperty("JButton.arc", 12);
 
    NextBtn.putClientProperty("JButton.buttonType", "roundRect");
    NextBtn.putClientProperty("JButton.arc", 12);
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HowManyHadminPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        NumberTxt = new javax.swing.JTextField();
        ContinueBtn = new javax.swing.JButton();
        CreatePanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        NumberPlaceHolder = new javax.swing.JLabel();
        AccNameField = new javax.swing.JTextField();
        AccountNameLabel = new javax.swing.JLabel();
        AccountPasswordLabel = new javax.swing.JLabel();
        AccPasswordField = new javax.swing.JPasswordField();
        ConfirmPasswordLabel = new javax.swing.JLabel();
        ConfirmPasswordField1 = new javax.swing.JPasswordField();
        ConfirmBtn = new javax.swing.JButton();
        NextBtn = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        HowManyHadminPanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(15, 23, 42));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Create Head Admin");

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 65, 85));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("How many Head admin accounts would you like to create?");

        NumberTxt.setBackground(new java.awt.Color(255, 255, 255));
        NumberTxt.setColumns(12);
        NumberTxt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        NumberTxt.addActionListener(this::NumberTxtActionPerformed);

        ContinueBtn.setBackground(new java.awt.Color(0, 102, 204));
        ContinueBtn.setFont(new java.awt.Font("Yu Gothic UI", 1, 14)); // NOI18N
        ContinueBtn.setForeground(new java.awt.Color(255, 255, 255));
        ContinueBtn.setText("Continue");
        ContinueBtn.putClientProperty("JButton.buttonType", "roundRect");
        ContinueBtn.putClientProperty("FlatLaf.style", "font: 14 $font.family bold; background: #0066CC; foreground: #ffffff; arc: 12;");
        ContinueBtn.addActionListener(this::ContinueBtnActionPerformed);

        javax.swing.GroupLayout HowManyHadminPanelLayout = new javax.swing.GroupLayout(HowManyHadminPanel);
        HowManyHadminPanel.setLayout(HowManyHadminPanelLayout);
        HowManyHadminPanelLayout.setHorizontalGroup(
            HowManyHadminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(HowManyHadminPanelLayout.createSequentialGroup()
                .addGroup(HowManyHadminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(HowManyHadminPanelLayout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(NumberTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(HowManyHadminPanelLayout.createSequentialGroup()
                        .addGap(288, 288, 288)
                        .addComponent(ContinueBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        HowManyHadminPanelLayout.setVerticalGroup(
            HowManyHadminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, HowManyHadminPanelLayout.createSequentialGroup()
                .addContainerGap(283, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(24, 24, 24)
                .addComponent(NumberTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(ContinueBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(247, 247, 247))
        );

        getContentPane().add(HowManyHadminPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 0, 710, -1));

        CreatePanel.setBackground(new java.awt.Color(255, 255, 255));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Create Head Admin");

        NumberPlaceHolder.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        NumberPlaceHolder.setForeground(new java.awt.Color(0, 0, 0));

        AccNameField.addActionListener(this::AccNameFieldActionPerformed);

        AccountNameLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountNameLabel.setForeground(new java.awt.Color(0, 0, 0));
        AccountNameLabel.setText("Account Name:");

        AccountPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        AccountPasswordLabel.setForeground(new java.awt.Color(0, 0, 0));
        AccountPasswordLabel.setText("Account Password:");

        AccPasswordField.addActionListener(this::AccPasswordFieldActionPerformed);

        ConfirmPasswordLabel.setFont(new java.awt.Font("Yu Gothic UI", 1, 13)); // NOI18N
        ConfirmPasswordLabel.setForeground(new java.awt.Color(0, 0, 0));
        ConfirmPasswordLabel.setText("Confirm Password:");

        ConfirmPasswordField1.addActionListener(this::ConfirmPasswordField1ActionPerformed);

        ConfirmBtn.setBackground(new java.awt.Color(0, 102, 204));
        ConfirmBtn.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        ConfirmBtn.setForeground(new java.awt.Color(255, 255, 255));
        ConfirmBtn.setText("Confirm");
        ConfirmBtn.addActionListener(this::ConfirmBtnActionPerformed);

        NextBtn.setBackground(new java.awt.Color(0, 102, 204));
        NextBtn.setFont(new java.awt.Font("Yu Gothic UI", 1, 12)); // NOI18N
        NextBtn.setForeground(new java.awt.Color(255, 255, 255));
        NextBtn.setText("Next");
        NextBtn.addActionListener(this::NextBtnActionPerformed);

        javax.swing.GroupLayout CreatePanelLayout = new javax.swing.GroupLayout(CreatePanel);
        CreatePanel.setLayout(CreatePanelLayout);
        CreatePanelLayout.setHorizontalGroup(
            CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(CreatePanelLayout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NumberPlaceHolder, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CreatePanelLayout.createSequentialGroup()
                        .addGap(0, 174, Short.MAX_VALUE)
                        .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CreatePanelLayout.createSequentialGroup()
                                .addComponent(NextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CreatePanelLayout.createSequentialGroup()
                                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(CreatePanelLayout.createSequentialGroup()
                                        .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(ConfirmPasswordLabel)
                                            .addComponent(AccountPasswordLabel)
                                            .addComponent(AccountNameLabel))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(AccNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(ConfirmPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(AccPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CreatePanelLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ConfirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(82, 82, 82)))
                                .addGap(171, 171, 171))))))
        );
        CreatePanelLayout.setVerticalGroup(
            CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CreatePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(NumberPlaceHolder, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AccNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AccountNameLabel))
                .addGap(18, 18, 18)
                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(AccPasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(AccountPasswordLabel))
                .addGap(18, 18, 18)
                .addGroup(CreatePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ConfirmPasswordLabel)
                    .addComponent(ConfirmPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(ConfirmBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(181, 181, 181)
                .addComponent(NextBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(CreatePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(592, 0, 710, 710));

        jPanel1.setBackground(new java.awt.Color(15, 23, 42));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logo2.png"))); // NOI18N

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 1, 24)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Check in and inventory monitoring System ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(681, 681, 681))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(233, 233, 233)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(217, 217, 217)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 596, 712));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NumberTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NumberTxtActionPerformed
         ContinueBtnActionPerformed(evt);
    }//GEN-LAST:event_NumberTxtActionPerformed

    private void ContinueBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ContinueBtnActionPerformed
        String text =
            NumberTxt.getText() == null
            ? "" : NumberTxt.getText().trim();
    if (!text.matches("\\d+")) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter a whole number.",
                "Invalid Number",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    int count;
    try {
        count = Integer.parseInt(text);
    } catch (NumberFormatException ex) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Please enter a valid number.",
                "Invalid Number",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (count <= 0) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Number of Head Admins must be at least 1.",
                "Invalid Number",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }
    if (count > MAX_HEAD_ADMINS) {
        javax.swing.JOptionPane.showMessageDialog(
                this,
                "Number of Head Admins cannot exceed "
                + MAX_HEAD_ADMINS + ".",
                "Invalid Number",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        return;
    }

            // Move to the in-place Head Admin creation wizard
            totalHeadAdmins = count;
            currentIndex = 1;
            pendingHeadAdmins.clear();

            HowManyHadminPanel.setVisible(false);
            CreatePanel.setVisible(true);

            getContentPane().revalidate();
            getContentPane().repaint();

            showStep();
    }//GEN-LAST:event_ContinueBtnActionPerformed

    
    private void AccNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccNameFieldActionPerformed
         handleStepSubmit();
    }//GEN-LAST:event_AccNameFieldActionPerformed

    private void AccPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AccPasswordFieldActionPerformed
          handleStepSubmit();
    }//GEN-LAST:event_AccPasswordFieldActionPerformed

    private void ConfirmBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmBtnActionPerformed
        // "Next" — only visible on non-final Head Admins.
         handleStepSubmit();
       
    }//GEN-LAST:event_ConfirmBtnActionPerformed

    private void NextBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextBtnActionPerformed
        // "Confirm" — only visible on the final Head Admin.
        handleStepSubmit();
        
    }//GEN-LAST:event_NextBtnActionPerformed

    private void ConfirmPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmPasswordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConfirmPasswordField1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
              try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Failed to initialize FlatLaf", ex);
        }

        /* Create and display the form */
        EventQueue.invokeLater(() -> new FirstRunSetup().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField AccNameField;
    private javax.swing.JPasswordField AccPasswordField;
    private javax.swing.JLabel AccountNameLabel;
    private javax.swing.JLabel AccountPasswordLabel;
    private javax.swing.JButton ConfirmBtn;
    private javax.swing.JPasswordField ConfirmPasswordField1;
    private javax.swing.JLabel ConfirmPasswordLabel;
    private javax.swing.JButton ContinueBtn;
    private javax.swing.JPanel CreatePanel;
    private javax.swing.JPanel HowManyHadminPanel;
    private javax.swing.JButton NextBtn;
    private javax.swing.JLabel NumberPlaceHolder;
    private javax.swing.JTextField NumberTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
