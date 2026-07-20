/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package clinic;

/**
 *
 * @author PC
 */
public class Clinic {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // new LoginUi().show();
       try {
            // 1. Force FlatLaf globally before ANY frame renders
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
        }

        // 2. Clear default text components to use sleek placeholders
        javax.swing.UIManager.put("JTextField.placeholderText", "");

        // 3. Launch your Login Window first
        java.awt.EventQueue.invokeLater(() -> {
            new LoginUi().setVisible(true);
            //new AdminPanel().setVisible(true);
        });
        
    }
    
}
