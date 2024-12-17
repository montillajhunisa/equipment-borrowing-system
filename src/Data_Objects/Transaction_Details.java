package Data_Objects;

import java.sql.Time;

public class Transaction_Details {
    private int transactionID;
    private String serialNumber;
    private Time requestedTime;
    private Time expectedReturnTime;
    private String requestStatus;
    private String borrowedStatus;

    public Transaction_Details(int transactionID, String serialNumber, Time requestedTime, Time expectedReturnTime, String requestStatus,
                                 String borrowedStatus) {
        this.transactionID = transactionID;
        this.serialNumber = serialNumber;
        this.requestedTime = requestedTime;
        this.expectedReturnTime = expectedReturnTime;
        this.requestStatus = requestStatus;
        this.borrowedStatus = borrowedStatus;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Time getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public Time getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(Time requestedTime) {
        this.requestedTime = requestedTime;
    }

    public void setExpectedReturnTime(Time expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getBorrowedStatus() {
        return borrowedStatus;
    }

    public void setBorrowedStatus(String borrowedStatus) {
        this.borrowedStatus = borrowedStatus;
    }
}
