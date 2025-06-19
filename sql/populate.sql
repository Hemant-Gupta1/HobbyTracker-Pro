-- Create Users table to store login credentials and roles
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT, -- Unique identifier for each user
    Username VARCHAR(50) UNIQUE NOT NULL,  -- Username for login
    PasswordHash VARCHAR(64) NOT NULL,     -- SHA-256 hash of the password
    Role VARCHAR(10) NOT NULL              -- Role: 'admin' or 'user'
);

-- Insert sample users (passwords are SHA-256 hashes of 'adminpass' and 'userpass')
INSERT INTO Users (Username, PasswordHash, Role) VALUES
    ('admin', '8c6976e5b5410415bde908bd4dee15dfb16a7a60c6f6a7a5e0e3c6c8e6b6a6e6', 'admin'),
    ('user',  '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 'user');

-- Create People table to store information about individuals
CREATE TABLE People (
    PersonID INT PRIMARY KEY,   -- Unique identifier for each person
    Name VARCHAR(50),           -- Name of the person
    Age INT,                    -- Age of the person
    Gender VARCHAR(10)          -- Gender of the person
);

-- Insert sample data into the People table
INSERT INTO People (PersonID, Name, Age, Gender)
VALUES
    (1, 'Riya', 25, 'Female'),
    (2, 'Hemant', 30, 'Male'),
    (3, 'Aaryan', 28, 'Male');

-- Create Hobbies table to store different hobbies
CREATE TABLE Hobbies (
    HobbyID INT PRIMARY KEY,    -- Unique identifier for each hobby
    HobbyName VARCHAR(50),      -- Name of the hobby
    Category VARCHAR(50)        -- Category of the hobby (e.g., Sports, Arts)
);

-- Insert sample data into the Hobbies table
INSERT INTO Hobbies (HobbyID, HobbyName, Category)
VALUES
    (1, 'Dancing', 'Arts'),
    (2, 'Playing', 'Sports'),
    (3, 'Sleeping', 'Leisure'),
    (4, 'Studying', 'Education');

-- Create PersonHobbies table to associate people with their hobbies
CREATE TABLE PersonHobbies (
    PersonID INT,               -- Identifier of the person
    HobbyID INT,                -- Identifier of the hobby
    FOREIGN KEY (PersonID) REFERENCES People(PersonID),   -- Ensure PersonID exists in People table
    FOREIGN KEY (HobbyID) REFERENCES Hobbies(HobbyID),    -- Ensure HobbyID exists in Hobbies table
    PRIMARY KEY (PersonID, HobbyID)   -- Combination of PersonID and HobbyID is unique
);

-- Insert sample data into the PersonHobbies table
INSERT INTO PersonHobbies (PersonID, HobbyID)
VALUES
    (1, 1),
    (2, 2), 
    (2, 3), 
    (3, 3),
    (1, 4); 

-- Create HobbyEquipment table to store equipment related to hobbies
CREATE TABLE HobbyEquipment (
    HobbyID INT,                -- Identifier of the hobby
    EquipmentName VARCHAR(50),  -- Name of the equipment
    FOREIGN KEY (HobbyID) REFERENCES Hobbies(HobbyID),    -- Ensure HobbyID exists in Hobbies table
    PRIMARY KEY (HobbyID, EquipmentName)  -- Combination of HobbyID and EquipmentName is unique
);

-- Insert sample data into the HobbyEquipment table
INSERT INTO HobbyEquipment (HobbyID, EquipmentName)
VALUES
    (1, 'Shoes'),
    (1, 'Clothes'),
    (2, 'Bat'),
    (2, 'Ball'),
    (2, 'Wicket'),
    (3, 'Bed'),
    (3, 'Pillow'),
    (4, 'Book'),
    (4, 'Pen');
