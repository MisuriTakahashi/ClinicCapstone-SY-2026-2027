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
       // 
       try {
            
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf: " + ex.getMessage());
        }

       
        javax.swing.UIManager.put("JTextField.placeholderText", "");

       
        java.awt.EventQueue.invokeLater(() -> {
            //new Dashboard().setVisible(true);
            new Dashboard().setVisible(true);
           
        });
        
    }
    
}
