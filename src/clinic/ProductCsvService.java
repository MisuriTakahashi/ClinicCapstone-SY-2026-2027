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
public class ProductCsvService {
    private final File csvFile;
    private final File activityLogFile;
    private static final DateTimeFormatter Time_Format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public ProductCsvService(String csvPath , String activityLogFile){
        this.csvFile = new File(csvPath);
        this.activityLogFile = new File(activityLogFile);
    }
    
       public ArrayList<Product> loadAll() throws IOException  {
        ArrayList<Product> products = new ArrayList<>();
        if(!csvFile.exists()) return products;
        
        try(BufferedReader br = new BufferedReader (new FileReader(csvFile))){
            String line;
            while((line = br.readLine()) != null){
                 String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (data.length >= 3) {
                    String name = data[0].replace("\"", "");
                    String expDate = data[1].replace("\"", "");
                    int quantity = Integer.parseInt(data[2].trim());
                    products.add(new Product(name, expDate, quantity));
                }
            }
        }
            return products;
    }
    
   
       public void addItem(String name, String expDate, int quantity) throws IOException {
        Product product = new Product(name, expDate, quantity);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(product.toCsvLine());
            bw.newLine();
        }
        logActivity("Added " + quantity + "x " + name);
    }
    
    
       private void logActivity(String message) throws IOException {
        String timestamp = LocalDateTime.now().format(Time_Format);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(activityLogFile, true))) {
            bw.write("[" + timestamp + "] " + message);
            bw.newLine();
        }
    }
          
    
        public boolean editItem(String currentName , String newName ,String newExpDate , int newQuantity ) throws IOException {
         ArrayList<Product> products = loadAll();
         boolean found = false;
         
         for(Product p : products){
             if(p.getname().equals(currentName)){
                p.setname(newName);
                p.setExpDate(newExpDate);
                p.setquantity(newQuantity);
                found = true;
             }
         }
          if (!found) return false;

        rewriteFile(products);
        logActivity("Edited " + currentName + " -> " + newName + " (" + newQuantity + "x)");
        return true;
    }
    
    
        private void rewriteFile(ArrayList<Product> products) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
            for (Product p : products) {
                bw.write(p.toCsvLine());
                bw.newLine();
            }
        }
    }
      
      
        public boolean deleteItem(String name) throws IOException {
        ArrayList<Product> products = loadAll();
        boolean removed = products.removeIf(p -> p.getname().equals(name));

        if (!removed) return false;

        rewriteFile(products);
        logActivity("Deleted " + name);
        return true;
    }
       
      
       // Reads the activity log back for display — most recent entries last
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
       
    
}
