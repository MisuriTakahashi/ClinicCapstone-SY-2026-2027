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
    private String checkInTime;
    private String status;
    private String guardianName;
    private String guardianPhoneNums;

    public CheckinSystem(String name, String gradeSection, String lrn, String reason,
                          String medUsed, String checkInTime, String status,
                          String guardianName, String guardianPhoneNums) {
        this.name = name;
        this.gradeSection = gradeSection;
        this.lrn = lrn;
        this.reason = reason;
        this.medUsed = medUsed;
        this.checkInTime = checkInTime;
        this.status = status;
        this.guardianName = guardianName;
        this.guardianPhoneNums = guardianPhoneNums;
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
    public void setStatus(String status) {
        this.status = status;
    }
    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }
    public void setGuardianPhone(String guardianPhoneNums) {
        this.guardianPhoneNums = guardianPhoneNums;
    }

    public String toCsvLine() {
        return "\"" + name + "\",\"" + gradeSection + "\",\"" + lrn + "\",\"" + reason + "\",\""
                + medUsed + "\",\"" + checkInTime + "\",\"" + status + "\",\""
                + guardianName + "\",\"" + guardianPhoneNums + "\"";
    }
}