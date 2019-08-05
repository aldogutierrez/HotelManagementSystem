SET PERSIST local_infile = 1;

DROP DATABASE IF EXISTS HOTEL;
CREATE DATABASE HOTEL;
USE HOTEL;
/*
==================================== SQL RELATIONS ARE CREATED HERE ====================================
*/
DROP TABLE IF EXISTS USER;
CREATE TABLE USER
(
    uID INT AUTO_INCREMENT,
    uNAME VARCHAR(30),
    age INT,
    PRIMARY KEY (uID)
);
ALTER TABLE USER AUTO_INCREMENT = 1001;

DROP TABLE IF EXISTS Room;
CREATE TABLE Room
(
    roomNumber INT,
    bedNumber INT,
    floorLocation INT,
    reserveStatus BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (roomNumber)
);

DROP TABLE IF EXISTS Booking;
CREATE TABLE Booking
(
    uID INT,
    roomNumber INT,
    checkInDate DATE,
    checkOutDate DATE,
    updateAt date,
    PRIMARY KEY (uID, roomNumber, checkInDate),
    FOREIGN KEY (uID) REFERENCES USER(uID) ON DELETE CASCADE,
    FOREIGN KEY (roomNumber) REFERENCES Room(roomNumber) ON DELETE CASCADE
);

DROP TABLE IF EXISTS Prices;
CREATE TABLE Prices
(
    roomNumber INT,
    dailyPrice INT,
    penaltyFee INT,
    FOREIGN KEY (roomNumber) REFERENCES Room(roomNumber) ON DELETE CASCADE
);

DROP TABLE IF EXISTS BookingArchive;
CREATE TABLE BookingArchive
(
    uID INT,
    roomNumber INT,
    checkInDate DATE,
    checkOutDate DATE,
    PRIMARY KEY (uID, roomNumber, checkInDate),
    FOREIGN KEY (uID) REFERENCES USER(uID) ON DELETE CASCADE,
    FOREIGN KEY (roomNumber) REFERENCES Room(roomNumber) ON DELETE CASCADE
);

/*
==================================== SQL TRIGGERS ARE CREATED HERE =====================================
*/

DROP TRIGGER IF EXISTS Checkin;
DELIMITER//
CREATE TRIGGER CheckIn BEFORE INSERT ON Booking
FOR EACH ROW
BEGIN
    SET new.updateat = ifnull(new.updateat,now());
    UPDATE room 
    SET reserveStatus = true 
    WHERE roomNumber = new.roomNumber 
    AND new.checkindate <= CURDATE() 
    AND new.checkOutDate > CURDATE();
END //
Delimiter ;

DROP TRIGGER IF EXISTS Checkout;
DELIMITER//
CREATE TRIGGER CheckOut AFTER DELETE ON Booking
FOR EACH ROW
BEGIN
    UPDATE Room
    SET reserveStatus = false
    WHERE roomNumber = old.roomNumber
    AND old.checkOutDate <= CURDATE();
END //
DELIMITER ;

/*
=================================== SQL STORED PROCEDURE IS CREATED HERE ===============================
*/

DROP PROCEDURE IF EXISTS archiveBooking;
DELIMITER //
CREATE PROCEDURE archiveBooking (IN cutOffDate DATE)
BEGIN
    INSERT INTO BookingArchive (uID, roomNumber, checkInDate, checkOutDate)
	SELECT uID, roomNumber, checkInDate, checkOutDate
        FROM Booking
        WHERE Booking.updateAt < cutOffDate;
    DELETE FROM Booking WHERE Booking.updateAt < cutOffDate;
END //
DELIMITER ;
