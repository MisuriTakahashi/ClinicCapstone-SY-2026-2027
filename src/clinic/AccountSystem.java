/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 *
 * @author PC
 */
public class AccountSystem {
    private String name;
    private String password;
    private String role;
    
        public AccountSystem(String name, String password, String role){
            this.name = name;
            this.password = password;
            this.role = role;
        }
        
        //getters
        public String GetName(){
            return name;
        }
        public String GetPassword(){
            return password;
        }
         public String getRole() {
             return role;
        }
         
        //setters
        public void SetName(String name){
            this.name = name;
        }
        public void SetPassword(String password){
            this.password = password;
        }
        public void setRole(String role) {
            this.role = role; 
        }

        public boolean isAdmin() {
            return role.equalsIgnoreCase("Admin");
        }
        
        public String toCsvLine(){
            return "\"" + name + "\",\"" + password + "\",\"" + role + "\"";
        }
    
    
}
