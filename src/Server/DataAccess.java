package Server;

import Data_Objects.*;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DataAccess {
    private static Connection connection;

    public DataAccess() {
    }

    public static boolean setConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cybercrusaders", "root", "");
        } catch (Exception ex) {
            System.out.println("Database connection failed.");
            return false;
        }
        return true;
    }

   public static ArrayList<Equipment> getEquipments() {
        ArrayList<Equipment> equipments = new ArrayList<>();

        try {
            String query = "SELECT * FROM Equipment ORDER BY Equipment_Name";
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Equipment equipment = new Equipment(rs.getString(1), rs.getString(2),
                        rs.getString(3), rs.getDate(4), rs.getString(5), rs.getString(6));
                equipments.add(equipment);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get equipments.");
        }
       return equipments;
   }

    public static Map<String, Integer> getAvailableEquipments() {
        Map<String, Integer> availableEquipments = new HashMap<>();

        String query = "SELECT Equipment_Name, COUNT(*) as Available "
                + "FROM equipment "
                + "WHERE Availability = 'Yes' "
                + "GROUP BY Equipment_Name";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String equipmentName = rs.getString("Equipment_Name");
                int availableCount = rs.getInt("Available");
                availableEquipments.put(equipmentName, availableCount);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get available equipment.");
        }
        return availableEquipments;
    }

    public static ArrayList<String> getSerialNumbers(String equipmentName) {
        String query =  "SELECT Serial_Number FROM equipment WHERE Equipment_Name = ? AND Availability = 'Yes' ORDER BY RAND() LIMIT 1";
        ArrayList<String> serialNumber = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, equipmentName);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                serialNumber.add(rs.getString("Serial_Number"));
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Failed to get serial numbers.");
        }
        return serialNumber;
   }

    public static ArrayList<String> getSerialNumbersOfAllEquipment() {
        String query =  "SELECT Serial_Number FROM equipment";
        ArrayList<String> serialNumber = new ArrayList<>();

        try {
            Statement ps = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = ps.executeQuery(query);
            while (rs.next()) {
                serialNumber.add(rs.getString("Serial_Number"));
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Failed to get serial numbers of all equipments.");
        }
        return serialNumber;
    }

   public static String getEquipmentAvailability(String serialNumber){
        String query = "SELECT Availability FROM equipment WHERE Serial_Number = ?";
        String equipmentAvailability = "";

        try{
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, serialNumber);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                equipmentAvailability = rs.getString("Availability");
            }
            rs.close();
        } catch (Exception e) {
            System.out.println("Failed to return the equipment availability.");
        }
        return equipmentAvailability;
   }

    public static void returnEquipment(int studentID, String serialNumber, String condition) {
        String query = "UPDATE transaction SET Return_Time = ?, Equipment_Condition = ? WHERE Transaction_ID IN " +
                "(SELECT Transaction_ID FROM transaction_details WHERE Serial_Number = ?) AND Student_ID = ?";
        String query2 = "UPDATE transaction_details SET Borrowed_Status = ? WHERE Serial_Number = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            Time timeReturned = Time.valueOf(LocalTime.now());
            ps.setTime(1, timeReturned);
            ps.setString(2, condition);
            ps.setString(3, serialNumber);
            ps.setInt(4, studentID);
            ps.executeUpdate();

            PreparedStatement ps2 = connection.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps2.setString(1, "Returned");
            ps2.setString(2, serialNumber);
            ps2.executeUpdate();

            System.out.println("Equipment has been returned.");
        } catch (Exception ex) {
            System.out.println("Failed to update the return of equipment.");
        }
    }

    public static boolean addTransaction(Transaction transaction, Transaction_Details transactionDetails) {
        String query = "INSERT INTO transaction(Student_ID, Course_Code, Date, Borrow_Time, " +
                "Return_Time, Equipment_Condition) VALUES(?, ?, ?, ?, ?, ?)";
        String query2 = "INSERT INTO transaction_details(Serial_Number, Requested_Time, Expected_Return_Time, " +
                "Request_Status, Borrowed_Status) VALUES(?, ?, ?, ?, ?)";
        boolean successful = false;

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, transaction.getStudentID());
            ps.setString(2, transaction.getCourseCode());
            ps.setDate(3, transaction.getDate());
            ps.setTime(4, transaction.getBorrowTime());
            ps.setTime(5, transaction.getReturnTime());
            ps.setString(6, transaction.getEquipmentCondition());
            ps.executeUpdate();

            PreparedStatement ps2 = connection.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
            ps2.setString(1, transactionDetails.getSerialNumber());
            ps2.setTime(2, transactionDetails.getRequestedTime());
            ps2.setTime(3, transactionDetails.getExpectedReturnTime());
            ps2.setString(4, transactionDetails.getRequestStatus());
            ps2.setString(5, transactionDetails.getBorrowedStatus());
            ps2.executeUpdate();

            // Updating of equipment availability will serve as "Reservation" for the Student
            updateEquipmentAvailability(transactionDetails.getSerialNumber(), "No");
            successful = true;
        } catch (Exception ex) {
        }
        return successful;
    }

    public static void updateEquipmentAvailability(String serialNumber, String availability){
        String query = "UPDATE equipment SET Availability = ? WHERE Serial_Number = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, availability);
            ps.setString(2, serialNumber);

            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Failed to get requested time.");
        }
    }

    public static void addEquipment(Equipment equipment) {
        String query = "INSERT INTO equipment(Serial_Number, Equipment_Name, Brand_Name, Date_Acquired, Equipment_Condition, Availability) VALUES(?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, equipment.getSerialNumber());
            ps.setString(2, equipment.getEquipmentName());
            ps.setString(3, equipment.getBrandName());
            ps.setDate(4, equipment.getDateAcquired());
            ps.setString(5, equipment.getEquipmentCondition());
            ps.setString(6, equipment.getAvailability());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("New Equipment added.");
            }
        } catch (Exception ex) {
            System.out.println("Failed to add new equipment.");
        }
    }

    public static void deleteEquipment(String serialNumber) {
        String query = "DELETE FROM EQUIPMENT WHERE Serial_Number = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, serialNumber);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Failed to add new equipment.");
        }
    }

    public static void updateEquipmentCondition(String serialNumber, String newCondition, String newAvailability) {
        try {
            String query = "UPDATE equipment SET Equipment_Condition = ?, Availability = ? WHERE Serial_Number = ?";
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, newCondition);
            ps.setString(2, newAvailability);
            ps.setString(3, serialNumber);

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Equipment condition updated successfully.");
            }
        } catch (Exception ex) {
            System.out.println("Failed to update transaction.");
        }
    }

    public static ArrayList<Borrower> getBorrowers(){
        ArrayList<Borrower> borrowers = new ArrayList<>();
        try {
            String query = "SELECT * FROM Borrower ORDER BY Student_Name";
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Borrower b = new Borrower(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4));
                borrowers.add(b);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get borrowers.");
        }
        return borrowers;
    }

    public static ArrayList<Faculty> getFaculties(){
        ArrayList<Faculty> faculty = new ArrayList<>();

        try {
            String query = "SELECT * FROM Faculty ORDER BY Faculty_Name";
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                Faculty f = new Faculty(rs.getString(1), rs.getString(2));
                faculty.add(f);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get faculties.");
        }
        return faculty;
    }

    public static ArrayList<String> getBorrowedList(int studentID) {
        ArrayList<String> borrowed = new ArrayList<>();
        String regex = ":::";
        String query = "SELECT Course_Code, Date, transaction_details.Serial_Number, equipment.Equipment_Name, " +
                "Requested_Time, Expected_Return_Time, Request_Status, Borrow_Time, Return_Time, " +
                "transaction.Equipment_Condition, Borrowed_Status \n" +
                "FROM transaction \n" +
                "NATURAL JOIN transaction_details \n" +
                "JOIN equipment \n" +
                "ON transaction_details.Serial_Number = Equipment.Serial_Number \n" +
                "WHERE Student_ID = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, studentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String result = rs.getString("Course_Code") + regex + rs.getDate("Date") + regex +
                        rs.getString("Serial_Number") + regex +  rs.getString("Equipment_Name") +
                        regex +  rs.getTime("Requested_Time") + regex +
                        rs.getTime("Expected_Return_Time") + regex + rs.getString("Request_Status")
                        + regex + rs.getTime("Borrow_Time") + regex + rs.getTime("Return_Time") +
                        regex + rs.getString("Equipment_Condition") + regex +
                        rs.getString("Borrowed_Status");
                borrowed.add(result);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get borrowed list.");
        }
        return borrowed;
    }

    public static Map<Transaction, Transaction_Details> getTransactionAndTransactionDetails(int studentID) {
        Map<Transaction, Transaction_Details> transactions = new LinkedHashMap<>();
        String query = "SELECT * \n" +
                "FROM transaction \n" +
                "NATURAL JOIN transaction_details \n" +
                "WHERE Student_ID = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1, studentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction trans = new Transaction(rs.getInt("Transaction_ID"),
                        rs.getInt("Student_ID"), rs.getString("Course_Code"),
                        rs.getDate("Date"), rs.getTime("Borrow_Time"),
                        rs.getTime("Return_Time"), rs.getString("Equipment_Condition"));
                Transaction_Details transDet = new Transaction_Details(rs.getInt("Transaction_ID"),
                        rs.getString("Serial_Number"), rs.getTime("Requested_Time"),
                        rs.getTime("Expected_Return_Time"), rs.getString("Request_Status"),
                        rs.getString("Borrowed_Status"));
                transactions.put(trans, transDet);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get transaction and transaction details.");
        }
        return transactions;
    }

    public static ArrayList<Transaction_Details> getTransactionDetails(int studentID){
        ArrayList<Transaction_Details> td = new ArrayList<>();
        String query = "SELECT t.student_id, td.transaction_id, td.serial_number, td.requested_time, " +
                "td.expected_return_time, td.request_status, td. borrowed_status \n" +
                "FROM transaction t \n" +
                "JOIN transaction_details td \n" +
                "ON t.transaction_id = td.transaction_id \n" +
                "WHERE Student_ID = ? ORDER BY Transaction_ID";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setInt(1,studentID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Transaction_Details transactionDetails = new Transaction_Details(rs.getInt(2),
                        rs.getString(3), rs.getTime(4), rs.getTime(5),
                        rs.getString(6), rs.getString(7));
                td.add(transactionDetails);
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Failed to get borrow details.");
        }
        return td;
    }

    public static boolean showBorrowRequests(){
        String query = "SELECT transaction.transaction_id, student_id, course_code, serial_number, date, requested_time, expected_return_time\n" +
                "FROM transaction\n" +
                "JOIN transaction_details\n" +
                "ON transaction.transaction_id = transaction_details.transaction_id\n" +
                "WHERE Request_Status IS NULL ORDER BY transaction.Transaction_ID ASC";
        boolean requestsFound = false;

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = ps.executeQuery();

            System.out.println("-----------------------------------------------------------------------------------" +
                    "---------------------------------");
            System.out.printf("%67s%n", "BORROW REQUESTS");
            System.out.println("-----------------------------------------------------------------------------------" +
                    "---------------------------------");
            System.out.printf("%-18s%-14s%-15s%-17s%-14s%-18s%-24s%n", "Transaction ID", "Student ID", "Course Code", "Serial Number", "Date",
                    "Requested Time", "Expected Return Time");
            System.out.println("-----------------------------------------------------------------------------------" +
                   "---------------------------------");

            while (rs.next()) {
                int transactionID = rs.getInt(1);
                int studentID = rs.getInt(2);
                String courseCode = rs.getString(3);
                Date date = rs.getDate(5);
                String serialNumber = rs.getString(4);
                Time requestedTime = rs.getTime(6);
                Time expectedReturnTime = rs.getTime(7);

                System.out.printf("%-18s%-14s%-15s%-17s%-14s%-18s%-24s%n", transactionID, studentID, courseCode, serialNumber, date,
                        requestedTime, expectedReturnTime);
                requestsFound = true;
            }

            if(!requestsFound){
                System.out.printf("%68s%n","No requests yet.");
            }
            rs.close();
            System.out.println("-----------------------------------------------------------------------------------" +
                    "---------------------------------");
        } catch (Exception ex) {
            System.out.println("Failed to get borrow requests.");
        }
        return requestsFound;
    }

    public static void acceptorDeclineRequest(int transactionID, char request){
        String query = "UPDATE Transaction_Details SET Request_Status = ?, Borrowed_Status = ? WHERE Transaction_ID = ?";
        String query2 = "UPDATE Transaction SET Borrow_Time = ? WHERE Transaction_ID = ?";
        String query3 = "SELECT Serial_Number FROM Transaction_Details WHERE Transaction_ID = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement ps2 = connection.prepareStatement(query2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            PreparedStatement ps3 = connection.prepareStatement(query3, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (request == 'a') {
                ps.setString(1, "Approved");
                ps.setString(2, "Borrowed");
                ps.setInt(3, transactionID);
                ps2.setTime(1, Time.valueOf(LocalTime.now()));
                ps2.setInt(2, transactionID);
            } else {
                ps.setString(1,"Rejected");
                ps.setString(2, null);
                ps.setInt(3, transactionID);
                ps2.setTime(1, null);
                ps2.setInt(2, transactionID);
                ps3.setInt(1, transactionID);
                updateERTime(transactionID);
                ResultSet rs = ps3.executeQuery();
                if (rs.next()) {
                     updateEquipmentAvailability(rs.getString("Serial_Number"), "Yes");
                }
            }

            int rowsUpdated = ps.executeUpdate();
            int rowsUpdated2 = ps2.executeUpdate();
            if (rowsUpdated > 0 && rowsUpdated2 > 0) {
                System.out.println("Transaction updated successfully.");
            } 
        } catch (Exception ex) {
            System.out.println("Failed to update transaction.");
            ex.printStackTrace();
        }
    }

    public static void updateERTime(int transactionID){
        String query = "UPDATE Transaction_Details SET Expected_Return_Time = ? WHERE Transaction_ID = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ps.setString(1, null);
            ps.setInt(2, transactionID);
            ps.executeUpdate();
        } catch (Exception ex) {
            System.out.println("Failed to update time.");
        }
    }
}
