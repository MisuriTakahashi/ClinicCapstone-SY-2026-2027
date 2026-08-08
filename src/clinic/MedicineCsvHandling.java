/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class MedicineCsvHandling {
   
    private final File csvFile;
    private final File activityLogFile;
    private static final DateTimeFormatter Time_Format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private ArrayList<Medicine> medicine = new ArrayList<>();
    public MedicineCsvHandling(String csvPath , String activityLogFile){
        this.csvFile = new File(csvPath);
        this.activityLogFile = new File(activityLogFile);
    }
    
       // Loads all products from the CSV file
       public ArrayList<Medicine> loadAll() throws IOException  {
        
        if(!csvFile.exists()) return medicine;
        
        try(BufferedReader br = new BufferedReader (new FileReader(csvFile))){
            String line;
            while((line = br.readLine()) != null){
                 String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 3) {
                    String name = data[0].replace("\"", "");
                    String expDate = data[1].replace("\"", "");
                    int quantity = Integer.parseInt(data[2].trim());
                    medicine.add(new Medicine(name, expDate, quantity));
                }
            }
        }
            return medicine;
    }
    
       
       //this add the new items
       public void addItem(String name, String expDate, int quantity) throws IOException {
        Medicine medicine = new Medicine(name, expDate, quantity);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(medicine.toCsvLine());
            bw.newLine();
        }
        logActivity("Added " + quantity + "x " + name);
    }
    
       //this shows the Log Activity on the Products like if the item is deducted by a student or delete or edited 
       private void logActivity(String message) throws IOException {
        String timestamp = LocalDateTime.now().format(Time_Format);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(activityLogFile, true))) {
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        }
    }
          
        //this edit the current item like if you want to change date or add another stocks
        public boolean editItem(String currentName , String newName ,String newExpDate , int newQuantity ) throws IOException {
         ArrayList<Medicine> medicine = loadAll();
         boolean found = false;
         
         for(Medicine p : medicine){
             if(p.getname().equals(currentName)){
                p.setname(newName);
                p.setExpDate(newExpDate);
                p.setquantity(newQuantity);
                found = true;
             }
         }
          if (!found) return false;

        rewriteFile(medicine);
        logActivity("Edited " + currentName + " -> " + newName + " (" + newQuantity + "x)");
        return true;
    }
    
        //this rewrites the Data on the File if Edited and Deleted 
        private void rewriteFile(ArrayList<Medicine> products) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Medicine p : products) {
                bw.write(p.toCsvLine());
                bw.newLine();
            }
        }
    }
      
        //this is just an delete button
        public boolean deleteItem(String name) throws IOException {
        ArrayList<Medicine> products = loadAll();
        boolean removed = products.removeIf(p -> p.getname().equals(name));

        if (!removed) return false;

        rewriteFile(products);
        logActivity("Deleted " + name);
        return true;
    }
       
      
       // Reads the activity log back for display  most recent entries last
        public ArrayList<String> loadActivityLog() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        if (!activityLogFile.exists()) return lines;

        try (BufferedReader br = new BufferedReader(new FileReader(activityLogFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }
     
        //this is the checking of the medicine that the student use it also checks if no stocks 
        public boolean useMedicine(String productName , String studentName) throws IOException{
                
            ArrayList<Medicine> products = loadAll();
            boolean found = false;
            
            for(Medicine p : products){
            
                if(p.getname().equalsIgnoreCase(productName)){
            
                if(p.getquantity () <= 0){
                    return false; // no stocks
                }
                
                p.setquantity(p.getquantity () - 1);
                found = true;
                break;
            }
        }
            if(!found){
                return false;
            }
            
            rewriteFile(products);
            
            logActivity("Student " + studentName + " Used 1x " + productName);
            
            return true; 
    }
        //this is the method for not duplicating the name 
        public boolean nameExists(String name) throws IOException {
        
            for (Medicine p : loadAll()) {
            if (p.getname().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
        //this just find the name i think
        public Medicine findByName(String name) throws IOException {
            
            for (Medicine p : loadAll()) {
            if (p.getname().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }
}
