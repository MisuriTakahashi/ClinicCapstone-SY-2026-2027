/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author PC
 */
public class VisitCsvService {
    
    //eto yun ginawa ni ser sa dateformatter na ginawa naten noong nag print tayo 
    private final File csvFile;
    private static final DateTimeFormatter TIME_FORMAT = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm" );
    
   //sabi daw ni ai eto daw yun naghahanap kung ano ginagamit na CSV file maybe maybe not
    public VisitCsvService(String path){
        this.csvFile = new File(path);
    }
    
    // this adds one new Check in row to csv 
    public void checkIn(String name , String gradeSection , String reason , String medUsed){
        String now = LocalDateTime.now().format(TIME_FORMAT);
        CheckinSystem visit = new CheckinSystem(name , gradeSection , reason , medUsed , now , "In clinic ");
        
         try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
            bw.write(visit.toCsvLine());
            bw.newLine();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }
    }
    
    //LOADS ALL THE Visits
    //reads every record fon the csv file 
    
     public ArrayList<CheckinSystem> loadAll()  {
        ArrayList<CheckinSystem> visits = new ArrayList<>();
        if (!csvFile.exists()) return visits;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                String[] data = 
                        line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (data.length >= 6) {
                    for (int i = 0; i < data.length; i++) 
                        data[i] = data[i].replace("\"", "");
                    visits.add(new CheckinSystem(
                            data[0], 
                            data[1], 
                            data[2], 
                            data[3], 
                            data[4], 
                            data[5]));
                }
            }
        } catch (FileNotFoundException ex) {
            System.getLogger(VisitCsvService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } catch (IOException ex) {
            System.getLogger(VisitCsvService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        return visits;
    }
     
     //MARK STUDENT AS SENT HOME
     //finds  a students visit and changes to the status from "in clinic" to "sent home" 
     public void markSentHome(String name , String checkInTIme){
         ArrayList<CheckinSystem> visit = loadAll();
         
         for(CheckinSystem v : visit){
             if(v.getName().equals(name) && v.getCheckInTime().equals(checkInTIme)){
         }
     }
             try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
                for (CheckinSystem v : visit) {
                bw.write(v.toCsvLine());
                bw.newLine();
            }
        } catch (IOException ex) {
            System.getLogger(VisitCsvService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }
     public int[] getTodayCounts() {
         String today = LocalDate.now().toString();
         int totalToday = 0;
         int sentHomeToday = 0;
         
         for(CheckinSystem v : loadAll()){
             if (v.getCheckInTime().startsWith(today)){
                 totalToday++;
                    if(v.getStatus().equals("Sent Home")) sentHomeToday++;
             }
         }
          return new int[]{totalToday, sentHomeToday};
     }
   //nigga 
    
}
