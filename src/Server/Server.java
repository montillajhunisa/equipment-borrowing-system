package Server;

import Data_Objects.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Server extends UnicastRemoteObject implements ServerRemote {
    static final Scanner keyboard = new Scanner(System.in);

    public Server() throws RemoteException {}

    public static void main(String[] args) {
        try {
            ServerRemote stub = new Server();
            Registry reg = LocateRegistry.createRegistry(3030);
            reg.rebind("borrowingSystem", stub);

            if (DataAccess.setConnection()) {
                System.out.println("======================================================");
                System.out.println("              EQUIPMENT BORROWING SYSTEM");
                System.out.println("======================================================");
                menu();
            }
        } catch (RemoteException exc) {
            exc.printStackTrace();
        }
    }

    public static void menu() throws RemoteException {
        while (true) {
            System.out.println("\n======================================================");
            System.out.println("                      MAIN MENU");
            System.out.println("======================================================");
            System.out.println("[1] Accept Borrow Request");
            System.out.println("[2] Return Equipment");
            System.out.println("[3] Show All Equipments");
            System.out.println("[4] Show Available Equipments");
            System.out.println("[5] Add Equipment");
            System.out.println("[6] Delete Equipment");
            System.out.println("[7] Update Equipment Condition");
            System.out.println("[8] Show Borrower List");
            System.out.println("[9] Exit");
            System.out.println("======================================================");
            int choice = enterChoice(1, 9);

            switch (choice) {
                case 1 -> acceptBorrowerRequests();
                case 2 -> returnEquipment();
                case 3 -> showAllEquipments();
                case 4 -> showAvailableEquipments();
                case 5 -> addEquipment();
                case 6 -> deleteEquipment();
                case 7 -> {
                    showEquipmentCondition();
                    updateEquipmentCondition();
                }
                case 8 -> showBorrowerList();
                case 9 -> terminate();
            }
        }
    }

    @Override
    public Map<String, Integer> getAvailableEquipments() throws RemoteException {
        return DataAccess.getAvailableEquipments();
    }

    @Override
    public boolean addTransaction(int studentID, String courseCode, String equipmentName, Time expectedReturnTime,
                               int totalQuantity) throws RemoteException {
        Transaction transaction;
        Transaction_Details transactionDetails;
        boolean successful = false;

        int transactionID = 0;
        Date date = new Date(System.currentTimeMillis());
        Time time =  null;
        Time requestedTime = Time.valueOf(LocalTime.now());
        Time returnTime = null;
        String equipmentCondition = null;
        String requestStatus = null;
        String borrowedStatus = null;

        for (int i = 0; i <= totalQuantity; i++) {
            String serialNumber = "";

            for (int j = 0; j < DataAccess.getSerialNumbers(equipmentName).size(); j++) {
                serialNumber = DataAccess.getSerialNumbers(equipmentName).get(j);
            }
            if (DataAccess.getEquipmentAvailability(serialNumber).equals("Yes")) {
                transaction = new Transaction(transactionID, studentID, courseCode, date, time, returnTime, equipmentCondition);
                transactionDetails = new Transaction_Details(transactionID, serialNumber, requestedTime, expectedReturnTime,
                        requestStatus, borrowedStatus);
                if (DataAccess.addTransaction(transaction, transactionDetails)) {
                    successful = true;
                }
            }
        }
        return successful;
    }

    @Override
    public Map<String, String> viewRequests(int studentID) throws RemoteException {
        String status = "";
        String equipment = "";
        Map<String, String> requests = new HashMap<>();
        ArrayList<Equipment> equipments = DataAccess.getEquipments();

        try {
            for (Transaction_Details transactionDetails : DataAccess.getTransactionDetails(studentID)) {
                String sn = transactionDetails.getSerialNumber();

                if (transactionDetails.getRequestStatus() == null) {
                    status = "In Process";
                } else {
                    status = transactionDetails.getRequestStatus();
                }

                for (Equipment e : equipments) {
                    if (sn.equals(e.getSerialNumber())) {
                        equipment = e.getEquipmentName() + " with the Serial Number [" + e.getSerialNumber() + "]";
                        break;
                    }
                }

                requests.put(equipment, status);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public ArrayList<String> viewBorrowedList(int studentID) throws RemoteException {
        ArrayList<String> borrowed = DataAccess.getBorrowedList(studentID);
        return borrowed;
    }

    @Override
    public ArrayList<Borrower> getBorrowers() throws RemoteException {
        return DataAccess.getBorrowers();
    }

    @Override
    public ArrayList<Faculty> getFaculties() throws RemoteException {
        return DataAccess.getFaculties();
    }

    private static void showAllEquipments() {
        ArrayList<Equipment> equipments = DataAccess.getEquipments();

        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("%55s%n", "EQUIPMENTS");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        System.out.printf("%-17s%-19s%-14s%-16s%-23s%-12s%n", "Serial Number", "Equipment Name", "Brand Name",
                "Date Acquired", "Equipment Condition", "Availability");
        System.out.println("-----------------------------------------------------------------------------------------------------");
        for (Equipment e : equipments) {
            System.out.printf("%-17s%-19s%-14s%-16s%-23s%-12s%n", e.getSerialNumber(), e.getEquipmentName(), e.getBrandName(),
                    e.getDateAcquired(),
            e.getEquipmentCondition(), e.getAvailability());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------");
    }

    private static void showAvailableEquipments() {
        Map<String, Integer> availableEquipments = DataAccess.getAvailableEquipments();

        System.out.println("------------------------------------------------------");
        System.out.printf("%32s%n", "EQUIPMENTS");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-40s%-15s%n", "Equipment Name", "Quantity");
        System.out.println("------------------------------------------------------");
        for (Map.Entry<String,Integer> e : availableEquipments.entrySet()) {
            System.out.printf("%-40s%-15s%n", e.getKey(), e.getValue());
        }
        System.out.println("------------------------------------------------------");
    }

    public static void acceptBorrowerRequests() {
        if (DataAccess.showBorrowRequests()) {
            char choice = 'y';

            do {
                System.out.print("Enter the Transaction ID you wish to select: ");
                int transactionID = Integer.parseInt(keyboard.nextLine());
                System.out.print("Accept or Reject? [a/r]: ");

                char request = keyboard.nextLine().charAt(0);
                try {
                    if (request == 'a' || request == 'r') {
                        DataAccess.acceptorDeclineRequest(transactionID, request);
                    } else {
                        System.out.println("Please enter a valid choice. [a/r] ");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a single character.");
                }

                do {
                    System.out.print("Do you wish to select another [y/n]: ");
                    choice = keyboard.nextLine().charAt(0);
                    if (choice != 'y' && choice != 'n') {
                        System.out.print("Invalid input. Please only enter either y or n.");
                    }
                } while (choice != 'y' && choice != 'n');
            } while (choice == 'y');
        }
    }

    private static void showBorrowerList() {
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%37s%n", "BORROWERS");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("%-14s%-26s%-14s%-11s%n", "Student ID", "Student Name", "Year Level", "Tel. No.");
        System.out.println("-----------------------------------------------------------------");
        for (Borrower b : DataAccess.getBorrowers()) {
            System.out.printf("%-14s%-26s%-14s%-11s%n", b.getStudentID(), b.getStudentName(), b.getYearLevel(), b.getTelNo());
        }
        System.out.println("-----------------------------------------------------------------");
    }

    private static void returnEquipment() {
        boolean studentFound = false;
        int choice = 0;

    do {
        try {
            System.out.print("\nEnter the Student ID of the Borrower: ");
            int studentID = Integer.parseInt(keyboard.nextLine());

            for (Borrower b : DataAccess.getBorrowers()) {
                if (studentID == b.getStudentID()) {
                    studentFound = true;
                    break;
                }
            }

            if (studentFound) {
                Map<Transaction, Transaction_Details> borrowed = DataAccess.getTransactionAndTransactionDetails(studentID);
                ArrayList<Equipment> equipments = DataAccess.getEquipments();
                int size = 0;

                for (Map.Entry<Transaction, Transaction_Details> t : borrowed.entrySet()) {
                    Transaction_Details td = t.getValue();

                    if (td != null && "Borrowed".equals(td.getBorrowedStatus())) {
                        size++;
                    }
                }

                if (size > 0) {
                    System.out.println("------------------------------------------------------");
                    System.out.printf("%30s%n", "EQUIPMENTS");
                    System.out.println("------------------------------------------------------");
                    System.out.printf("%-25s%-25s%n", "Serial Number", "Equipment Name");
                    System.out.println("------------------------------------------------------");
                    for (Map.Entry<Transaction, Transaction_Details> tran : borrowed.entrySet()) {
                        Transaction_Details td = tran.getValue();

                        if (td != null && "Borrowed".equals(td.getBorrowedStatus())) {
                            String sn = td.getSerialNumber();
                            for (Equipment e : equipments) {
                                if (sn.equals(e.getSerialNumber())) {
                                    System.out.printf("%-25s%-25s%n", e.getSerialNumber(), e.getEquipmentName());
                                }
                            }
                        }
                    }
                    System.out.println("------------------------------------------------------");

                    String serialAndCondition = updateEquipmentCondition();

                    String[] array = serialAndCondition.split(":::");
                    DataAccess.returnEquipment(studentID, array[0], array[1]);

                } else {
                    System.out.println("There are no borrowed equipment for student.");
                }

                do {
                    System.out.print("Do you wish to select another [y/n]: ");
                    choice = keyboard.nextLine().charAt(0);
                    if (choice != 'y' && choice != 'n') {
                        System.out.println("Invalid input. Please only enter either y or n.");
                    }
                } while (choice != 'y' && choice != 'n');
            } else {
                System.out.println("Student Not Found.");
            }
        } catch (Exception ex) {
            System.out.println("Invalid input. Please enter a valid input.");
        }
        } while (choice == 'y');
    }

    private static void addEquipment(){
        String serialNumber = generateSerialNumber();
        LocalDate localDate = LocalDate.now();
        Date date = Date.valueOf(localDate);
        String condition = "Good Condition";
        String availability = "Yes";

        System.out.print("Enter Equipment Name: ");
        String equipmentName = keyboard.nextLine();
        System.out.print("Enter Brand Name: ");
        String brandName = keyboard.nextLine();

        // validates that the serial number is unique by continuously checking the database for any duplicates
        while (DataAccess.getSerialNumbersOfAllEquipment().contains(serialNumber)) {
            serialNumber = generateSerialNumber();
        }

        Equipment equipment = new Equipment(serialNumber, equipmentName, brandName, date, condition, availability);
        DataAccess.addEquipment(equipment);
    }

    private static void deleteEquipment() {
        ArrayList<Equipment> equipments = DataAccess.getEquipments();
        boolean deleted = false;

        System.out.println("------------------------------------------------------");
        System.out.printf("%32s%n", "EQUIPMENTS");
        System.out.println("------------------------------------------------------");
        System.out.printf("%-25s%-25s%n", "Serial Number", "Equipment Name");
        System.out.println("------------------------------------------------------");
        for (Equipment e : equipments) {
            System.out.printf("%-25s%-25s%n", e.getSerialNumber(), e.getEquipmentName());
        }
        System.out.println("------------------------------------------------------");
        System.out.print("Enter Serial Number: ");
        String serialNumber = keyboard.nextLine();

        for (Equipment equipment : DataAccess.getEquipments()){
            if (serialNumber.equals(equipment.getSerialNumber())){
                for (Equipment eq : DataAccess.getEquipments()) {
                    if (!eq.getSerialNumber().equals(serialNumber)) {
                        DataAccess.deleteEquipment(serialNumber);
                        deleted = true;
                    }
                }
            }
        }

        if (deleted) {
            System.out.println("Equipment has been deleted.");
        } else {
            System.out.println("Equipment was not found or could not be deleted.");
        }
    }

    private static String updateEquipmentCondition() {
        boolean updated = false;
        ArrayList<Equipment> equipments = DataAccess.getEquipments();

        System.out.print("Enter Serial Number: ");

        String serialNumber = keyboard.nextLine();
        String availability = "";
        String condition = "";

        for (Equipment equipment : equipments) {
            if (serialNumber.equals(equipment.getSerialNumber())) {
                System.out.print("\nChoose Equipment Condition: \n[1] Good Condition \n[2] Damaged \n[3] Under Repair\n");
                int choice = enterChoice(1, 3);

                switch (choice) {
                    case 1 -> condition = "Good Condition";
                    case 2 -> condition = "Damaged";
                    case 3 -> condition = "Under Repair";
                }

                if (condition.equals("Good Condition")){
                    availability = "Yes";
                } else {
                    availability = "No";
                }

                updated = true;
                DataAccess.updateEquipmentCondition(serialNumber, condition, availability);
            }
        }

        if (!updated) {
            System.out.println("Equipment was not found or could not be updated.");
        }

        return serialNumber + ":::" + condition;
    }

    private static void showEquipmentCondition() {
        ArrayList<Equipment> equipments = DataAccess.getEquipments();

        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%40s%n", "EQUIPMENTS");
        System.out.println("----------------------------------------------------------------------");
        System.out.printf("%-25s%-25s%-25s%n", "Serial Number", "Equipment Name", "Equipment Condition");
        System.out.println("----------------------------------------------------------------------");
        for (Equipment e : equipments) {
            System.out.printf("%-25s%-25s%-25s%n", e.getSerialNumber(), e.getEquipmentName(), e.getEquipmentCondition());
        }
        System.out.println("----------------------------------------------------------------------");
    }

    private static String generateSerialNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000000); // generate a random number between 0 and 999999
        String formattedNumber = String.format("%06d", randomNumber); // pad the number with leading zeros if necessary
        return "CIS-" + formattedNumber;
    }

    private static int enterChoice(int min, int max) {
        int choice = 0;

        do {
            try {
                System.out.print("Input the number of your choice: ");
                choice = Integer.parseInt(keyboard.nextLine());
                if (choice < min || choice > max) {
                    System.out.println("\nInvalid Choice! " +
                            "Accepted choices are only from " + min + " to " + max + ".");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("\nChoices should be integers. Please try again");
            }
        } while (choice < min || choice > max);
        return choice;
    } // end of enterChoice method

    private static void terminate() {
        System.out.println("\n======================================================");
        System.out.println("           THANK YOU FOR USING THE PROGRAM!           ");
        System.out.println("======================================================");
        System.exit(0);
    } // end of terminate method
}