package Server;

import Data_Objects.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Map;

public interface ServerRemote extends Remote {
    ArrayList<Borrower> getBorrowers() throws  RemoteException;
    ArrayList<Faculty> getFaculties() throws RemoteException;
    Map<String, Integer> getAvailableEquipments() throws RemoteException;
    boolean addTransaction(int studentID, String courseCode, String equipmentName, Time expectedReturnTime,
                        int totalQuantity) throws RemoteException;
    Map<String, String> viewRequests(int studentID) throws RemoteException;
    ArrayList<String> viewBorrowedList(int studentID) throws RemoteException;
}