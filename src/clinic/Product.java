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
public class Product {
        private String name; // the name of the item
        private String ExpDate; // exp date
        private int quantity; // quantity 
    
        private static final int LOW_STOCK_THRESHOLD = 10;
    
        public Product(String name , String ExpDate , int quantity){
            this.name = name;
            this.ExpDate = ExpDate;
            this.quantity = quantity;
        }
        
        public String getname(){
            return name;
        }
        
        public String getExpDate(){
            return ExpDate;
        }
        public int getquantity(){
            return quantity;
        }
        
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

        
        //eto yun nagchecheck kung expired na ba yun item na yon 
        public boolean isExpired(){
            try{
                return LocalDate.parse(ExpDate).isBefore(LocalDate.now());
            }catch(Exception e){
                return false; 
            }

        }
        
        //if stocks is low
        
         public boolean isLowStock() {
        return quantity < LOW_STOCK_THRESHOLD;
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
