package Data_Objects;

import java.sql.Date;
import java.sql.Time;

public class Transaction {
    private int transactionID;
    private int studentID;
    private String courseCode;
    private Date date;
    private Time borrowTime;
    private Time returnTime;
    private String equipmentCondition;

    public Transaction(int transactionID, int studentID, String courseCode, Date date, Time borrowTime, Time returnTime,
            String equipmentCondition) {
        this.transactionID = transactionID;
        this.studentID = studentID;
        this.courseCode = courseCode;
        this.date = date;
        this.borrowTime = borrowTime;
        this.returnTime = returnTime;
        this.equipmentCondition = equipmentCondition;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getBorrowTime() {
        return borrowTime;
    }

    public void setBorrowTime(Time borrowTime) {
        this.borrowTime = borrowTime;
    }

    public Time getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Time returnTime) {
        this.returnTime = returnTime;
    }

    public String getEquipmentCondition() {
        return equipmentCondition;
    }

    public void setEquipmentCondition(String equipmentCondition) {
        this.equipmentCondition = equipmentCondition;
    }
}
