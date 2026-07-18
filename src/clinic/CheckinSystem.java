/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clinic;

/**
 *
 * @author PC
 */
public class CheckinSystem {
    
    private String name; // ofc the name OF THE FUCKING STUDENT YOU DUMB FUCK WHY YOU READING THIS SHIIIIIIIIIIIIT???!?!?!
    private String gradeSection; // Grade and fucking section bro what you expect
    private String reason; // reason why the student in the clinic 
    private String medUsed; // meds pills used
    private String checkInTime; //time of checkin 
    private String status; // status of the the whenever a student in the clinic or sented home 
    
     public CheckinSystem(String name, 
             String gradeSection , 
             String reason , 
             String medUsed , 
             String checkInTime , 
             String status ){
      
        this.name = name;
        this.gradeSection = gradeSection;
        this.reason = reason;
        this.medUsed = medUsed;
        this.checkInTime = checkInTime;
        this.status = status;
  }
    
         public String getName(){
         return name;
     }
         public String getGradeSection(){
         return name;
     }
         public String getReason(){
         return name;
     }
         public String getMedUsed(){
         return name;
     }
         public String getCheckInTime(){
         return name;
     }
         public String getStatus(){
         return name;
     }
         
     //this makes the sent home display change bitch ass nigger    
         public void getsetStatus(String status){
         this.status = status;
     }
     // sabi daw ganto yun gawin sa csv file eh said by my wife ai 
       public String toCsvLine(){
       return "\"" + name + "\",\"" + gradeSection + "\",\"" + reason + "\",\""
               + medUsed + "\",\"" + checkInTime + "\",\"" + status + "\"";
   }
}
