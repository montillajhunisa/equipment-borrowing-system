package Data_Objects;

import java.io.Serializable;

public class Borrower implements Serializable {
    private int studentID;
    private String studentName;
    private int yearLevel;
    private String telNo;

    public Borrower(int studentID, String studentName, int yearLevel, String telNo){
        this.studentID = studentID;
        this.studentName = studentName;
        this.yearLevel = yearLevel;
        this.telNo = telNo;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }
}