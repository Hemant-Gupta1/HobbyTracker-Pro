import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class imt2022030_hobbies {

    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/hobbies";

    static final String USER = "root";
    static final String PASSWORD = "admin";

    public static void execLines(String pathx, Connection conn, Statement stmt) {
        System.out.println("Inserting data");
        String lines = "";
        Path path = Paths.get(pathx);
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lines = new String(bytes);
        String[] arrlines = lines.split(";");
        for (String line : arrlines) {
            if (!line.trim().isEmpty()) {
                try {
                    stmt.executeUpdate(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet exec = null;

        Scanner sc = new Scanner(System.in);

        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Creating statement...");
            System.out.println("hello");
            stmt = conn.createStatement();
            conn.setAutoCommit(false);

            execLines("C:\\Users\\Hemant Gupta\\Downloads\\AK\\imt2022030_hobbies_JDBC_Project\\sql\\populate.sql",conn, stmt);
        

            while (true) {
                System.out.println("""
                        1. List Hobbies
                        2. List people
                        3. Fetch PersonHobbies 
                        4. Insert into PersonHobbies 
                        5. List all Equipment and HobbyId
                        6. Update People table entry
                        7. Delete entry from HobbyEquipment 
                        8. Find hobbies of particular person
                        9. List all hobbies with the equipment
                        10. List all hobbies and the number of equipment items required for each
                        11. Count the number of people who have each hobby
                        12. Insert into HobbyEquipment and update hobby id
                        13. Exit
                        """);
                System.out.print("Enter option: ");
                int option = sc.nextInt();
                
                if (option == 1) {
                    String sql_query = "SELECT * FROM Hobbies;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                    }
                } else if (option == 2) {
                    String sql_query = "SELECT * FROM People;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("PersonID: " + exec.getInt("PersonID"));
                        System.out.println("Name: " + exec.getString("Name"));
                        System.out.println("Age: " + exec.getInt("Age"));
                        System.out.println("Gender: " + exec.getString("Gender"));
                    }

                } else if (option == 3) {
                    String sql_query = "SELECT * FROM PersonHobbies;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("PersonID: " + exec.getInt("PersonID"));
                    }
                } else if (option == 4) {
                    int person_id;
                    int hoby_id;
                    int temp;
                    System.out.println("Enter PersonID:");
                    person_id = sc.nextInt();
                    System.out.println("Enter HobbyID:");
                    hoby_id = sc.nextInt();
                    String sql_query = "INSERT INTO PersonHobbies (PersonID, HobbyID) VALUES (" + person_id + ","
                            + hoby_id
                            + ");";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                } else if (option == 5) {
                    String sql_query = "SELECT * FROM HobbyEquipment;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyID: " + exec.getInt("HobbyID"));
                        System.out.println("EquipmentName: " + exec.getString("EquipmentName"));
                    }

                } else if (option == 6) {
                    int person_id;
                    int age;
                    int temp;
                    System.out.println("Enter PersonID:");
                    person_id = sc.nextInt();
                    System.out.println("Enter New Age:");
                    age = sc.nextInt();
                    String name, gender;
                    System.out.println("Enter New name:");
                    name = sc.next();
                    System.out.println("Enter Gender:");
                    gender = sc.next();

                    String sql_query = "UPDATE People SET Name = '" + name + "', Age = " + age + ", Gender = '"
                            + gender + "' WHERE PersonID = '" + person_id + "';";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                } else if (option == 7) {
                    int temp;
                    String name;
                    int hoby_id;

                    System.out.println("Enter HobbyID:");
                    hoby_id = sc.nextInt();

                    System.out.println("Enter EquipmentName:");
                    sc.nextLine();
                    name = sc.nextLine();
                    String sql_query = "DELETE FROM HobbyEquipment WHERE HobbyID = " + hoby_id
                            + " AND EquipmentName = '"
                            + name + "';";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Failed");
                    }
                }

                else if (option == 8) {
                    System.out.println("Enter Name");
                    String name = sc.next();
                    String sql_query = "SELECT p.Name, h.HobbyName FROM People p INNER JOIN PersonHobbies ph ON p.PersonID = ph.PersonID INNER JOIN Hobbies h ON ph.HobbyID = h.HobbyID WHERE p.Name = '"
                            + name + "';";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("Name: " + exec.getString("Name"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                    }
                }

                else if (option == 9) {

                    String sql_query = "SELECT h.HobbyName, he.EquipmentName FROM Hobbies h LEFT JOIN HobbyEquipment he ON h.HobbyID = he.HobbyID;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("EquipmentName: " + exec.getString("EquipmentName"));
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                    }
                } else if (option == 10) {
                    String sql_query = "SELECT h.HobbyName, COUNT(he.EquipmentName) AS NumberOfEquipment FROM Hobbies h LEFT JOIN HobbyEquipment he ON h.HobbyID = he.HobbyID GROUP BY h.HobbyName;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                        System.out.println("NumberOfEquipment: " + exec.getInt("NumberOfEquipment"));
                    }
                } else if (option == 11) {

                    String sql_query = "SELECT h.HobbyName, COUNT(*) AS NumberOfPeople FROM Hobbies h INNER JOIN PersonHobbies ph ON h.HobbyID = ph.HobbyID GROUP BY h.HobbyName;";
                    exec = stmt.executeQuery(sql_query);
                    while (exec.next()) {
                        System.out.println("HobbyName: " + exec.getString("HobbyName"));
                        System.out.println("NumberOfPeople: " + exec.getInt("NumberOfPeople"));
                    }
                } else if (option == 12) {
                    int temp;
                    System.out.println("Enter Hobby Id:");
                    int id = sc.nextInt();
                    System.out.println("Enter Equipment name:");
                    String equipment = sc.next();

                    String sql_query = "INSERT INTO HobbyEquipment (HobbyID, EquipmentName) VALUES (" + id + ", '"
                            + equipment + "');";
                    temp = stmt.executeUpdate(sql_query);
                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Failed");
                    }

                    System.out.println("Enter the new Hobby Id:");
                    id = sc.nextInt();

                    sql_query = "UPDATE HobbyEquipment SET HobbyID = " + id + " WHERE EquipmentName = '" + equipment
                            + "';";
                    temp = stmt.executeUpdate(sql_query);

                    if (temp == 1) {
                        System.out.println("Success");
                    } else {
                        System.out.println("Fail");
                    }
                    conn.commit();
                } else
                    break;
                conn.commit();
            }
            sc.close();
            stmt.close();
            conn.close();
        } catch (SQLException se) { // Handle errors for JDBC
            se.printStackTrace();
            System.out.println("Rolling back data");
            try {
                if (conn != null)
                    conn.rollback();
            } catch (SQLException se2) {
                System.out.println("Rollback failed");
                se2.printStackTrace();
            }
        } catch (Exception e) { // Handle errors for Class.forName
            e.printStackTrace();
        } finally { // finally block used to close resources regardless of whether an exception was
                    // thrown or not
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            } // end finally try
        } // end try

        System.out.println("End of Code");
    }

}