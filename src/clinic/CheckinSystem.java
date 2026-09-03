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
    private String name;
    private String gradeSection;
    private String lrn;
    private String reason;
    private String medUsed;
    private int medsQty;
    private String checkInTime;
    private String status;
    private String guardianName;
    private String guardianPhoneNums;
    private String temperature;

    public CheckinSystem(String name, String gradeSection, String lrn, String reason,
                          String medUsed, int medsQty ,String checkInTime, String status,
                          String guardianName, String guardianPhoneNums) {
        this(name, gradeSection, lrn, reason, medUsed, medsQty, checkInTime, status, guardianName, guardianPhoneNums, "");
    }

    public CheckinSystem(String name, String gradeSection, String lrn, String reason,
                          String medUsed, int medsQty ,String checkInTime, String status,
                          String guardianName, String guardianPhoneNums, String temperature) {
        this.name = name;
        this.gradeSection = gradeSection;
        this.lrn = lrn;
        this.reason = reason;
        this.medUsed = medUsed;
        this.medsQty = medsQty;
        this.checkInTime = checkInTime;
        this.status = status;
        this.guardianName = guardianName;
        this.guardianPhoneNums = guardianPhoneNums;
        this.temperature = temperature == null ? "" : temperature;
    }
    
    //getters
    public String getName() { 
        return name;
    }
    public String getGradeSection() {
        return gradeSection;
    }
    public String getLrn() { 
        return lrn;
    }
    public String getReason() { 
        return reason; 
    }
    public String getMedUsed() {
        return medUsed;
    }
    public int getmedsQty(){
        return medsQty;
    }
    public String getCheckInTime() { 
        return checkInTime;
    }
    public String getStatus() {
        return status; 
    }
    public String getGuardianName() {
        return guardianName;
    }
    public String getGuardianPhoneNums() { 
        return guardianPhoneNums;
    }
    public String getTemperature() { return temperature; }
    
    //setters
    
    public void setName(String name) { 
        this.name = name; 
    }
    public void setGradeSection(String gradeSection) { 
        this.gradeSection = gradeSection; 
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public void setMedUsed(String medUsed) {
        this.medUsed = medUsed;
    }
    public void setMedsQty(int medsQty){
        this.medsQty = medsQty;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }
    public void setGuardianPhone(String guardianPhoneNums) {
        this.guardianPhoneNums = guardianPhoneNums;
    }
    public void setTemperature(String temperature) { this.temperature = temperature == null ? "" : temperature; }
    
    // Returns the medicine name and quantity for display.
    // Returns "None" when no medicine was used.
    public String getMedicineDisplay() {
        if (medUsed == null || medUsed.equals("None")) return "None";
        return medUsed + " x" + medsQty;
    }
   
    // Converts the check-in information into a CSV-formatted line for file storage.
    public String toCsvLine() {
        return "\"" + name + "\",\"" + gradeSection + "\",\"" + lrn + "\",\"" + reason + "\",\""
                + medUsed + "\",\"" + medsQty + "\",\""  + checkInTime + "\",\"" + status + "\",\""
                + guardianName + "\",\"" + guardianPhoneNums + "\"";
    }
    
}