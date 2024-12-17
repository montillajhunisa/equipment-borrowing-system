-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:3306
-- Generation Time: May 11, 2023 at 08:20 AM
-- Server version: 5.7.36
-- PHP Version: 7.4.26

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `cybercrusaders`
--

-- --------------------------------------------------------

--
-- Table structure for table `borrower`
--

DROP TABLE IF EXISTS `borrower`;
CREATE TABLE IF NOT EXISTS `borrower` (
  `Student_ID` int(7) NOT NULL,
  `Student_Name` varchar(30) NOT NULL,
  `Student_Year_Level` int(1) NOT NULL,
  `Student_Telno` varchar(11) NOT NULL,
  PRIMARY KEY (`Student_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `borrower`
--

INSERT INTO `borrower` (`Student_ID`, `Student_Name`, `Student_Year_Level`, `Student_Telno`) VALUES
(2220123, 'Sabian Smith', 2, '09254445632'),
(2220231, 'Kent Lee', 2, '09265486543'),
(2220456, 'Zildjan Ars', 2, '09264897653');

-- --------------------------------------------------------

--
-- Table structure for table `equipment`
--

DROP TABLE IF EXISTS `equipment`;
CREATE TABLE IF NOT EXISTS `equipment` (
  `Serial_Number` varchar(10) NOT NULL,
  `Equipment_Name` varchar(20) NOT NULL,
  `Brand_Name` varchar(20) NOT NULL,
  `Date_Acquired` date NOT NULL,
  `Equipment_Condition` enum('Good Condition','Damaged','Under Repair') NOT NULL,
  `Availability` enum('Yes','No') NOT NULL,
  PRIMARY KEY (`Serial_Number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`Serial_Number`, `Equipment_Name`, `Brand_Name`, `Date_Acquired`, `Equipment_Condition`, `Availability`) VALUES
('CIS-024791', 'Serial Cable', 'Roline', '2019-12-18', 'Damaged', 'No'),
('CIS-123456', 'Switch', 'Cisco', '2020-08-20', 'Under Repair', 'No'),
('CIS-452450', 'LAN Cable Tester', 'Colohas', '2020-12-18', 'Good Condition', 'Yes'),
('CIS-492004', 'Router', 'Juniper', '2021-10-25', 'Under Repair', 'No'),
('CIS-494221', 'Switch', 'Cisco', '2020-08-20', 'Under Repair', 'No'),
('CIS-642217', 'Crimping Tool', 'TICONN', '2020-12-18', 'Good Condition', 'Yes'),
('CIS-656784', 'Crimping Tool', 'TICONN', '2019-12-18', 'Good Condition', 'Yes'),
('CIS-722470', 'Access Point', 'Aruba', '2020-08-10', 'Good Condition', 'Yes'),
('CIS-846640', 'Serial Cable', 'Roline', '2019-12-18', 'Good Condition', 'Yes'),
('CIS-898546', 'Access Point', 'Aruba', '2019-08-10', 'Good Condition', 'Yes'),
('CIS-901124', 'LAN Cable Tester', 'Colohas', '2019-07-28', 'Good Condition', 'No'),
('CIS-910764', 'Router', 'Juniper', '2021-10-25', 'Good Condition', 'Yes');

-- --------------------------------------------------------

--
-- Table structure for table `faculty`
--

DROP TABLE IF EXISTS `faculty`;
CREATE TABLE IF NOT EXISTS `faculty` (
  `Course_Code` int(4) NOT NULL,
  `Faculty_Name` varchar(30) NOT NULL,
  PRIMARY KEY (`Course_Code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `faculty`
--

INSERT INTO `faculty` (`Course_Code`, `Faculty_Name`) VALUES
(9400, 'Zach Stanton'),
(9401, 'Fatima Bauer'),
(9402, 'Florence Jacobs');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE IF NOT EXISTS `transaction` (
  `Transaction_ID` int(9) NOT NULL AUTO_INCREMENT,
  `Student_ID` int(7) NOT NULL,
  `Course_Code` int(4) NOT NULL,
  `Date` date NOT NULL,
  `Borrow_Time` time DEFAULT NULL,
  `Return_Time` time DEFAULT NULL,
  `Equipment_Condition` enum('Good Condition','Damaged','Under Repair') DEFAULT NULL,
  PRIMARY KEY (`Transaction_ID`),
  KEY `Student_ID` (`Student_ID`),
  KEY `Course_Code` (`Course_Code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`Transaction_ID`, `Student_ID`, `Course_Code`, `Date`, `Borrow_Time`, `Return_Time`, `Equipment_Condition`) VALUES
(1, 2220123, 9400, '2022-04-24', '09:00:00', '10:30:00', 'Good Condition'),
(2, 2220231, 9400, '2022-04-24', '09:00:00', '10:30:00', 'Good Condition'),
(3, 2220456, 9401, '2022-04-24', '16:00:00', '17:30:00', 'Good Condition'),
(4, 2220231, 9401, '2023-04-24', '15:32:39', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `transaction_details`
--

DROP TABLE IF EXISTS `transaction_details`;
CREATE TABLE IF NOT EXISTS `transaction_details` (
  `Transaction_ID` int(9) NOT NULL AUTO_INCREMENT,
  `Serial_Number` varchar(10) NOT NULL,
  `Requested_Time` time NOT NULL,
  `Expected_Return_Time` time DEFAULT NULL,
  `Request_Status` enum('Approved','Rejected') DEFAULT NULL,
  `Borrowed_Status` enum('Returned','Borrowed') DEFAULT NULL,
  PRIMARY KEY (`Transaction_ID`) USING BTREE,
  KEY `Serial_Number` (`Serial_Number`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `transaction_details`
--

INSERT INTO `transaction_details` (`Transaction_ID`, `Serial_Number`, `Requested_Time`, `Expected_Return_Time`, `Request_Status`, `Borrowed_Status`) VALUES
(1, 'CIS-452450', '09:00:00', '10:30:00', 'Approved', 'Returned'),
(2, 'CIS-898546', '09:00:00', '10:30:00', 'Approved', 'Returned'),
(3, 'CIS-901124', '16:00:00', '17:30:00', 'Approved', 'Returned'),
(4, 'CIS-898546', '15:00:53', '16:30:53', 'Approved', 'Borrowed');

--
-- Constraints for dumped tables
--

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`Student_ID`) REFERENCES `borrower` (`Student_ID`),
  ADD CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`Course_Code`) REFERENCES `faculty` (`Course_Code`);

--
-- Constraints for table `transaction_details`
--
ALTER TABLE `transaction_details`
  ADD CONSTRAINT `transaction_details_ibfk_1` FOREIGN KEY (`Serial_Number`) REFERENCES `equipment` (`Serial_Number`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
