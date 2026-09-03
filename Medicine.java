/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.time.LocalDate;

/**
 *
 * @author PC
 */
public class Medicine {
        private String name; // the name of the item
        private String ExpDate; // exp date
        private int quantity; // quantity 
        private String purpose; // what the medicine is for
    
        private static final int LOW_STOCK_THRESHOLD = 10;
    
        public Medicine(String name , String ExpDate , int quantity){
            this(name, ExpDate, quantity, "");
        }

        public Medicine(String name, String ExpDate, int quantity, String purpose){
            this.name = name;
            this.ExpDate = ExpDate;
            this.quantity = quantity;
            this.purpose = purpose == null ? "" : purpose.trim();
        }
        
        //getters
        
        public String getname(){
            return name;
        }
        
        public String getExpDate(){
            return ExpDate;
        }

        /** Preferred descriptive name for the persisted expiration date. */
        public String getExpirationDate() {
            return ExpDate;
        }
        public int getquantity(){
            return quantity;
        }

        public String getPurpose(){
            return purpose;
        }
        
        
        //setters
        
        public void setname(String name)
        { 
            this.name = name; 
        }
        public void setExpDate(String expDate)
        { 
            this.ExpDate = expDate; 
        }
        public void setquantity(int quantity) 
        { 
            this.quantity = quantity; 
        }

        public void setPurpose(String purpose) {
            this.purpose = purpose == null ? "" : purpose.trim();
        }

        
        //eto yun nagchecheck kung expired na ba yun item na yon 
        public boolean isExpired(){
            try{
                // A medicine is unavailable on its stated expiration date
                // and every day thereafter.
                return !LocalDate.parse(ExpDate).isAfter(LocalDate.now());
            }catch(Exception e){
                return false; 
            }

        }
        
        //if stocks is low
        
         public boolean isLowStock() {
        return quantity < LOW_STOCK_THRESHOLD;
        }

        /** Status used by active inventory views and reports. */
        public String getInventoryStatus() {
            if (isExpired() && isLowStock()) return "Expired / Low Stock";
            if (isExpired()) return "Expired";
            if (isLowStock()) return "Low Stock";
            return "In Stock";
        }
    
        //eto yun nag papalit kung expired na 
       public String getStatus(){
            if(isExpired())
                return "Expired";
                return ExpDate;
        }    
       
       public String toCsvLine(){
           return "\"" + name + "\",\"" + ExpDate + "\"," + quantity;
       }
}
