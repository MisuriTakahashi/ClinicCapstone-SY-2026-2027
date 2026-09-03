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
     
        java.awt.EventQueue.invokeLater(() -> {
        LOADINGSCREEN screen = new LOADINGSCREEN();
        screen.setVisible(true);
        LS.run(screen);
        });

    }
    
}
