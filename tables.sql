create database iiita;

 use iiita;

 CREATE TABLE admin (
    Id INT  AUTO_INCREMENT,
    admin_id  Varchar(100) PRIMARY KEY,
    name VARCHAR(100),
    admin_password VARCHAR(255),
    email VARCHAR(255),
      UNIQUE KEY `unique_id` (`id`)
);
INSERT INTO admin (admin_id, name, admin_password, email)
VALUES ('admin1', 'Trilok', 'admin1', 'admin1@example.com')
,('admin2', 'Darshan', 'admin2', 'admin2@example.com');

 
CREATE TABLE member (
    id INT AUTO_INCREMENT ,
    institute_id VARCHAR(50) NOT NULL PRIMARY KEY,
    instituteid_password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
      UNIQUE KEY `unique_id` (`id`)
);
 Insert into member (institute_id, instituteid_password, name, email) values ("IIT2022038","Tmeena@82","Trilok Meena","trilokmeena1708344@gmail.com");


CREATE TABLE engineer (
    id INT AUTO_INCREMENT ,
    engineer_id VARCHAR(50) NOT NULL PRIMARY KEY,
    engineerid_password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
      UNIQUE KEY `unique_id` (`id`)
);
 Insert into engineer (engineer_id, engineerid_password, name, email) values ("ENG2024001","1234","Ravi","ravi@gmail.com");




CREATE TABLE archives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_format VARCHAR(50) NOT NULL,
    date_stored DATE NOT NULL,
    file LONGBLOB
);



CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE complaints (
    id INT AUTO_INCREMENT,
    token VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    institute_id VARCHAR(50) NOT NULL,
    Complaint_Type VARCHAR(255) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    location VARCHAR(255) NOT NULL,
    room_no VARCHAR(50),
    subject VARCHAR(255) NOT NULL,
    message TEXT,
    attachment LONGBLOB,
    attachment_format VARCHAR(50),
    UNIQUE KEY `unique_id` (`id`)
);
ALTER TABLE complaints
ADD FOREIGN KEY (institute_id) REFERENCES member(institute_id);

ALTER TABLE complaints
ADD COLUMN Status VARCHAR(255) DEFAULT 'Pending';




 CREATE TABLE Notification (
    id INT AUTO_INCREMENT PRIMARY KEY,
     url VARCHAR(255) NOT NULL,
     title VARCHAR(255) NOT NULL
 );
 
 


 
 
 CREATE TABLE task_assignment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_token VARCHAR(36) NOT NULL,
    engineer_id VARCHAR(50) NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deadline_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'unanswered',
    FOREIGN KEY (complaint_token) REFERENCES complaints(token),
    FOREIGN KEY (engineer_id) REFERENCES engineer(engineer_id)
);


CREATE TABLE tasknotification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receiver_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    notification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Unread',
    FOREIGN KEY (receiver_id) REFERENCES engineer(engineer_id)
);
 
 

 
 CREATE TABLE Usernotification (
    id INT AUTO_INCREMENT PRIMARY KEY,
    receiver_id VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    notification_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Unread',
    FOREIGN KEY (receiver_id) REFERENCES member(institute_id)
);
 
 
 
 
 CREATE TABLE adminnotifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'Unread',
    date_received TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
