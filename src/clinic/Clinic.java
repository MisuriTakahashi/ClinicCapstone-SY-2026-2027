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
      
       DatabaseManager.initializeDatabase();
      
       // TEMPORARY — run once to archive old pre-existing records, then remove this block.
       /*try (java.sql.Connection conn = DatabaseManager.getConnection();
          java.sql.Statement stmt = conn.createStatement()) {
           int rows = stmt.executeUpdate(
               "UPDATE VISITS SET archived = TRUE WHERE check_in_time < '" + java.time.LocalDate.now() + "' AND archived = FALSE");
           System.out.println("Archived " + rows + " old record(s).");
       } catch (java.sql.SQLException e) {
           e.printStackTrace();
       }*/
       
       // TEMPORARY — run once to migrate old accounts.csv into H2, then remove this block.
       
      /*try {
            int migrated = new AccountData().migrateFromCsv("accounts.csv");
           System.out.println("Migrated " + migrated + " account(s) from accounts.csv.");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       
        javax.swing.UIManager.put("JTextField.placeholderText", "");

       
        java.awt.EventQueue.invokeLater(() -> {
            //new Dashboard().setVisible(true);
            new LoginUi().setVisible(true);
           
        });
        
    }
    
}
