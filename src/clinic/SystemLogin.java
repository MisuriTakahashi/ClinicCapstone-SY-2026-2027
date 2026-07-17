/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import javax.swing.JOptionPane;

/**
 *
 * @author PC
 */
public class SystemLogin {
    
      public static String CheckLogin(String username, String password){
            
            String adminUser = "adminUser123";
            String adminPass = "adminPass123";
            
            String UserNurse = "UserNurse123";
            String UserPass = "PassNurse123";
            
            if(username.equalsIgnoreCase(adminUser) && password.equalsIgnoreCase(adminPass)){
                return "admin";
            }else if(username.equalsIgnoreCase(UserNurse) && password.equalsIgnoreCase(UserPass)){
                return "User";
            }else{
            return "invalid";
        }
     }
}
