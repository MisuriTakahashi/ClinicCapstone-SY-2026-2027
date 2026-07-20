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
    private String lrn; // this get the lrn of the student which is cannot be reuse since it's a unique number
    private String reason; // reason why the student in the clinic 
    private String medUsed; // meds pills used
    private String checkInTime; //time of checkin 
    private String status; // status of the the whenever a student in the clinic or sented home 
    
     public CheckinSystem(String name, 
             String gradeSection , 
             String lrn,
             String reason , 
             String medUsed , 
             String checkInTime , 
             String status ){
      
        this.name = name;
        this.gradeSection = gradeSection;
        this.lrn = lrn;
        this.reason = reason;
        this.medUsed = medUsed;
        this.checkInTime = checkInTime;
        this.status = status;
  }
    
         public String getName(){
         return name;
     }
         public String getGradeSection(){
         return gradeSection;
     }
         public String getLrn(){
         return lrn;
     }
         public String getReason(){
         return reason;
     }
         public String getMedUsed(){
         return medUsed;
     }
         public String getCheckInTime(){
         return checkInTime;
     }
         public String getStatus(){
         return status;
     }
         
     //this makes the sent home display change bitch ass nigger    
         public void setStatus(String status){
         this.status = status;
     }
     // sabi daw ganto yun gawin sa csv file eh said by my wife ai
         
       public String toCsvLine(){
       return "\"" + name + "\",\"" + gradeSection + "\",\"" + lrn + "\",\"" + reason + "\",\""
               + medUsed + "\",\"" + checkInTime + "\",\"" + status + "\"";
   }
        
}
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons
// bugs can type letter on names and reasons