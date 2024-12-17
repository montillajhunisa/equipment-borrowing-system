package Client;

import Data_Objects.*;
import Server.ServerRemote;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Time;
import java.util.*;

public class Client {
    static final Scanner keyboard = new Scanner(System.in);
    static int studentID = 0;
    static String courseCode = "";

    public static void main(String[] args) {
        try {
            System.out.println("\n======================================================");
            System.out.println("              EQUIPMENT BORROWING SYSTEM");
            System.out.println("======================================================");
            System.out.print("Enter the Host Address of the Server: ");
            String host = keyboard.nextLine();

            Registry registry = LocateRegistry.getRegistry(host, 3030);
            ServerRemote remote = (ServerRemote) registry.lookup("borrowingSystem");

            System.out.print("Enter Student ID: ");
            studentID = Integer.parseInt(keyboard.nextLine());

            if (verifyStudent(remote, studentID)) {
                System.out.print("Enter Course Code: ");
                courseCode = keyboard.nextLine();
                if (verifyFaculty(remote, courseCode)) {
                    menu(remote);
                } else {
                    System.out.println("Course Code Not Found.");
                }
            } else {
                System.out.println("Student Not Found.");
            }
        } catch (RemoteException | NotBoundException exc) {
            System.out.println("Failed to connect to server.");
        }
    }

    public static void menu(ServerRemote remote) {
        while (true) {
            System.out.println("\n======================================================");
            System.out.println("                      MAIN MENU");
            System.out.println("======================================================");
            System.out.println("[1] Borrow Equipment");
            System.out.println("[2] Show Available Equipments");
            System.out.println("[3] Show Borrowed List");
            System.out.println("[4] View Requests");
            System.out.println("[5] Exit");
            System.out.println("======================================================");
            int choice = enterChoice(1, 5);

            try {
                switch (choice) {
                    case 1 -> addTransaction(remote);
                    case 2 -> showAvailableEquipments(remote);
                    case 3 -> showBorrowedList(remote);
                    case 4 -> viewRequests(remote);
                    case 5 -> terminate();
                }
            } catch (RemoteException e) {
                System.out.println("Failed to connect to server.");
            }
        }
    }

    private static void showAvailableEquipments(ServerRemote remote) {
        try {
            Map<String, Integer> availableEquipments = remote.getAvailableEquipments();

            System.out.println("------------------------------------------------------");
            System.out.printf("%32s%n", "EQUIPMENTS");
            System.out.println("------------------------------------------------------");
            System.out.printf("%-40s%-15s%n", "Equipment Name", "Quantity");
            System.out.println("------------------------------------------------------");
            for (Map.Entry<String,Integer> e : availableEquipments.entrySet()) {
                System.out.printf("%-40s%-15s%n", e.getKey(), e.getValue());
            }
            System.out.println("------------------------------------------------------");
        } catch (RemoteException e) {
            System.out.println("Failed to connect to server.");
        }
    }

    private static void showBorrowedList(ServerRemote remote) {
        try {
            ArrayList<String> trans = remote.viewBorrowedList(studentID);

            if (trans.size() > 0) {
                System.out.println("--------------------------------------------------------------------------------" +
                        "-------------------------------------------------------------------------------------------" +
                        "-----------------------");
                System.out.printf("%-15s%-14s%-17s%-20s%-18s%-24s%-18s%-15s%-15s%-23s%-19s%n", "Course Code", "Date",
                        "Serial Number", "Equipment Name", "Requested Time", "Expected Return Time", "Request Status", "Borrow Time",
                        "Return Time", "Equipment Condition", "Borrowed Status");
                System.out.println("--------------------------------------------------------------------------------" +
                        "-------------------------------------------------------------------------------------------" +
                        "-----------------------");

                for (int i = 0; i < trans.size(); i++) {
                    String[] t = trans.get(i).trim().split(":::");
                    System.out.printf("%-15s%-14s%-17s%-20s%-18s%-24s%-18s%-15s%-15s%-23s%-19s%n", t[0], t[1], t[2], t[3],
                            t[4], t[5], t[6], t[7], t[8], t[9], t[10]);
                }
                System.out.println("--------------------------------------------------------------------------------" +
                        "-------------------------------------------------------------------------------------------" +
                        "-----------------------");
            } else {
                    System.out.println("There are no borrowed list to show.");
            }
        } catch (RemoteException e) {
            System.out.println("Failed to connect to server.");
        }
    }

    private static void addTransaction(ServerRemote remote) throws RemoteException {
        try {
            showAvailableEquipments(remote);
            System.out.print("Enter your Expected Return Time [HH:MM format]: ");
            String eRTime = keyboard.nextLine() + ":00";
            Time expectedReturnTime = Time.valueOf(eRTime);

            int availableQuantity = 0;
            for (int quantity : remote.getAvailableEquipments().values()) {
                availableQuantity += quantity;
            }
            System.out.print("How many equipment are you borrowing? ");
            int totalQuantity = Integer.parseInt(keyboard.nextLine());

            if (availableQuantity >= totalQuantity) {
                int count = 0;
                while (count < totalQuantity) {
                    System.out.print("Enter the Name of Equipment: ");
                    String equipmentName = keyboard.nextLine();
                    if (remote.addTransaction(studentID, courseCode, equipmentName, expectedReturnTime, count)) {
                        System.out.println("Request successful.");
                        count++;
                    } else {
                        System.out.println("Request failed.");
                    }
                }
            } else {
                System.out.println("There are only " + availableQuantity + " equipments available! Please try again.");
            }
        } catch (Exception ex) {
            System.out.println("Borrow Request Failed.");
        }
    }

    private static void viewRequests(ServerRemote remote) {
        try {
            for (Map.Entry<String, String> entry : remote.viewRequests(studentID).entrySet()) {
                switch (entry.getValue()) {
                    case "Approved" -> System.out.println("Your Borrow Request for the " + entry.getKey() +
                            " has been APPROVED!");
                    case "Rejected" -> System.out.println("Your Borrow Request for the " + entry.getKey() +
                            " has been REJECTED.");
                    case "In Process" -> System.out.println("Your Borrow Request for the " + entry.getKey() +
                            " is still being processed.");
                }
            }
        } catch (InputMismatchException | RemoteException e){
            System.out.println("Invalid input! Please enter a valid ID number.");
        }
    }

    private static boolean verifyStudent(ServerRemote remote, int studentID) throws RemoteException {
        boolean student = false;

        for (Borrower b : remote.getBorrowers()) {
            if (studentID == b.getStudentID()) {
                student = true;
                break;
            }
        }
        return student;
    }

    private static boolean verifyFaculty(ServerRemote remote, String courseCode) throws RemoteException {
        boolean courseCodeFound = false;

        for(Faculty f : remote.getFaculties()) {
            if (courseCode.equals(f.getCourseCode())) {
                courseCodeFound = true;
                break;
            }
        }
        return courseCodeFound;
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