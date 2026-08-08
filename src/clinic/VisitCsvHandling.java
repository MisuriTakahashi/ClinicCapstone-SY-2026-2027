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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author PC
 */
public class VisitCsvHandling {
    
    //this is the Time formmatter which makes the date and time for the check in function 
    private final File csvFile;
    private static final DateTimeFormatter TIME_FORMAT = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm" );
    
   //this is the path of the Csv
    public VisitCsvHandling(String path){
        this.csvFile = new File(path);
    }
    
    // this adds one new Check in row to csv 
   public void checkIn(String name , String gradeSection , String lrn , String medUsed,
                       String Reason , String guardianName, String guardianPhoneNums) throws IOException{
       String now = LocalDateTime.now().format(TIME_FORMAT);
       CheckinSystem visit = new CheckinSystem(name , gradeSection , lrn , medUsed , Reason ,now,
                                                "In Clinic" , guardianName ,guardianPhoneNums);
      
       try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile, true))) {
        bw.write(visit.toCsvLine());
        bw.newLine();
     }
   }
    
    //LOADS ALL THE Visits
    //reads every record fon the csv file 
    
    public ArrayList<CheckinSystem> loadAll() throws IOException {
    ArrayList<CheckinSystem> visits = new ArrayList<>();
    if (!csvFile.exists()) return visits;

    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (data.length >= 9) {
                for (int i = 0; i < data.length; i++) data[i] = data[i].replace("\"", "");
                visits.add(new CheckinSystem(
                        data[0], 
                        data[1], 
                        data[2], 
                        data[3], 
                        data[4], 
                        data[5], 
                        data[6], 
                        data[7], 
                        data[8]));
            }
        }
    }
    return visits;
}
     
     
      // Returns true if this LRN currently has an active ("In Clinic") visit
    public boolean isCurrentlyCheckedIn(String lrn) throws IOException {
        for (CheckinSystem v : loadAll()) {
            if (v.getLrn().equals(lrn) && v.getStatus().equals("In Clinic")) {
                return true;
            }
        }
        return false;
    }
     
    
     //MARK STUDENT AS SENT HOME
     //Counts today's total check-ins and students who have been sent home 
     public boolean markSentHome(String lrn) throws IOException {

        ArrayList<CheckinSystem> visits = loadAll();
            boolean updated = false;
    
            for (CheckinSystem v : visits) {

                 if (v.getLrn().equals(lrn) && v.getStatus().equals("In Clinic")) {

                  v.setStatus("Sent Home");
                    updated = true;
                    break;
                }
            }

    if (!updated) {
        return false;
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {

        for (CheckinSystem v : visits) {
            bw.write(v.toCsvLine());
            bw.newLine();
        }
    }
    return true;
}
      
     //this the counting function, when a students get check in it will getc counted 
     public int[] getTodayCounts() throws IOException {
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
    
     //Finds the student's currently active visit with an "In Clinic" status
     public CheckinSystem findActiveVisit(String lrn) throws IOException {
        for (CheckinSystem v : loadAll()) {

        if (v.getLrn().equals(lrn)
                && v.getStatus().equals("In Clinic")) {

            return v;
        }
    }
    return null;
    }
     
     //this the Edit Function
     public boolean editVisit(String lrn , String newName , String newGradeSection , 
             String newReason ,String newMedUsed ) throws IOException {
            ArrayList<CheckinSystem> visits = loadAll();
            boolean found = false;
         
            for (CheckinSystem v : visits) {
            if (v.getLrn().equals(lrn)) {
              v.setName(newName);
              v.setGradeSection(newGradeSection);
              v.setReason(newReason);
              v.setMedUsed(newMedUsed);
                found = true;
              }
           }
            if (!found) return false;
         
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile))) {
                for (CheckinSystem v : visits) {
                bw.write(v.toCsvLine());
                bw.newLine();
        
     }
                
}
        return true;
      
    }
     
     //this uses the name of student to find the LRN 
     public String findNameForLrn(String lrn) throws IOException {
               for (CheckinSystem v : loadAll()) {
               if (v.getLrn().equals(lrn)) {
               return v.getName();
                   }
               }
           return null;
        }  
     
     
}