/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package clinic;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author PC
 */
public class LoginUi extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginUi.class.getName());

    /**
     * Creates new form LoginUi
     */
    public LoginUi() {
        com.formdev.flatlaf.FlatLightLaf.setup();
        setUndecorated(true);
        initComponents();           
        setIconImage(AppIcon.getIcon());
        setLocationRelativeTo(null);
    
        // Panels
        jPanel3.setBackground(new Color(240, 244, 248)); 
        jPanel1.setBackground(Color.WHITE);
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc: 20;");

        // Typography
        jLabel3.setForeground(new Color(0x1E, 0x29, 0x3B)); 
        jLabel3.setFont(jLabel3.getFont().deriveFont(Font.BOLD, 32f));

        Color labelColor = new Color(0x47, 0x55, 0x69);
        jLabel6.setForeground(labelColor);
        jLabel4.setForeground(labelColor);
        jLabel6.setFont(jLabel6.getFont().deriveFont(Font.BOLD, 16f));
        jLabel4.setFont(jLabel4.getFont().deriveFont(Font.BOLD, 16f));

        // Text Fields
        jTextField1.setText(""); 
        jTextField1.setBackground(null);
        jTextField1.setForeground(null);
        jTextField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username...");
        jTextField1.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        jTextField1.putClientProperty(FlatClientProperties.STYLE, ""
            + "arc: 12;"
            + "margin: 4,10,4,10;"
            + "focusWidth: 2"
        );
        
        jPasswordField1.setText("");
        jPasswordField1.setBackground(null);
        jPasswordField1.setForeground(null);
        jPasswordField1.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password...");
        jPasswordField1.putClientProperty(FlatClientProperties.STYLE, ""
            + "arc: 12;"
            + "margin: 4,10,4,10;"
            + "showRevealButton: true;"
            + "focusWidth: 2"
        );

        // Login Button
        jButton1.setBackground(null);
        jButton1.setForeground(null);
        jButton1.setFont(jButton1.getFont().deriveFont(Font.BOLD, 16f));
        jButton1.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        jButton1.putClientProperty(FlatClientProperties.STYLE, ""
            + "arc: 12;"
            + "background: #0284C7;"              
            + "foreground: #FFFFFF;"              
            + "hoverBackground: #0369A1;"          
            + "focusedBackground: #0369A1"
        );

        // Red Exit Button Styling
        ExitBTN.setText("Exit"); 
        ExitBTN.setFont(ExitBTN.getFont().deriveFont(Font.BOLD, 16f));
        ExitBTN.setFocusable(false);
        ExitBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ExitBTN.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        
        ExitBTN.putClientProperty(FlatClientProperties.STYLE, ""
            + "arc: 12;"
            + "background: #EF4444;"       // Solid Red
            + "foreground: #FFFFFF;"       // Crisp White Text
            + "hoverBackground: #DC2626;"  // Darker Red on Hover
            + "focusedBackground: #DC2626;"
            + "pressedBackground: #B91C1C" // Deep Red on Press
        );

        // Enable Window Dragging
        java.awt.event.MouseAdapter dragListener = new java.awt.event.MouseAdapter() {
            private int mouseX, mouseY;

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(java.awt.event.MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        };

        jPanel3.addMouseListener(dragListener);
        jPanel3.addMouseMotionListener(dragListener);
    }

    // Smooth Fade-Out Effect
// Smooth Fade-Out Effect (Linux & Cross-Platform Safe)
private void fadeOutAndOpenDashboard(AccountSystem account) {
    java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    boolean isTranslucencySupported = gd.isWindowTranslucencySupported(java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT);

    // If translucency is not supported by the OS/WM (e.g. Linux X11/Wayland), transition instantly
    if (!isTranslucencySupported) {
        new Dashboard(account).setVisible(true);
        dispose();
        return;
    }

    javax.swing.Timer timer = new javax.swing.Timer(20, null);
    timer.addActionListener(e -> {
        float opacity = getOpacity();
        opacity -= 0.05f;
        
        if (opacity <= 0.05f) {
            setOpacity(0.0f);
            timer.stop();
            new Dashboard(account).setVisible(true);
            dispose();
        } else {
            setOpacity(opacity);
        }
    });
    timer.start();
}
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        ExitBTN = new javax.swing.JButton();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1.setBackground(new java.awt.Color(0, 102, 255));
        jButton1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("Login");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(this::jButton1ActionPerformed);
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 360, 98, 30));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 153, 255));
        jLabel3.setText("Login");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 200, -1, -1));

        jTextField1.setBackground(new java.awt.Color(204, 204, 204));
        jTextField1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 260, 310, -1));

        jPasswordField1.setBackground(new java.awt.Color(204, 204, 204));
        jPasswordField1.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.add(jPasswordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 310, 310, 40));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 255));
        jLabel4.setText("Password:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 320, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 255));
        jLabel6.setText("Username:");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 260, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/logo2.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 60, -1, -1));

        ExitBTN.setBackground(new java.awt.Color(0, 102, 255));
        ExitBTN.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        ExitBTN.setForeground(new java.awt.Color(0, 0, 0));
        ExitBTN.setText("Exit");
        ExitBTN.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ExitBTN.addActionListener(this::ExitBTNActionPerformed);
        jPanel1.add(ExitBTN, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 360, 100, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(96, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 900, 580));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
          String userName = jTextField1.getText().trim();
          char[] passwordChars = jPasswordField1.getPassword();
          String password = new String(passwordChars);

          if (userName.isEmpty() || password.isEmpty()) {
              JOptionPane.showMessageDialog(this,
                      "Please enter your credentials.");
              return;
          }

          AccountData accountService = new AccountData();

          try {

              AccountSystem account = accountService.authenticate(userName, password);

              if (account == null) {
                  JOptionPane.showMessageDialog(this,
                          "Wrong username or password.");
                  return;
              }

              fadeOutAndOpenDashboard(account);
              

          } catch (Exception ex) {

              JOptionPane.showMessageDialog(this,
                      "Error reading accounts: " + ex.getMessage());
          }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void ExitBTNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitBTNActionPerformed
      java.awt.GraphicsDevice gd = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    boolean isTranslucencySupported = gd.isWindowTranslucencySupported(java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT);

    // If translucency is not supported, exit immediately
    if (!isTranslucencySupported) {
        System.exit(0);
        return;
    }

    javax.swing.Timer timer = new javax.swing.Timer(15, null);
    timer.addActionListener(e -> {
        float opacity = getOpacity();
        opacity -= 0.1f;
        if (opacity <= 0.05f) {
            timer.stop();
            System.exit(0);
        } else {
            setOpacity(opacity);
        }
    });
    timer.start();
    }//GEN-LAST:event_ExitBTNActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginUi().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ExitBTN;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
