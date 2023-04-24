// DataBase Project Room Reservation.
// Andrew Pries, Lukas Nilson, Ryan Storms.

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class RoomReservation {
    public static void main(String[] args) {
        try{
            // Connects to database.
            String dbURL = "jdbc:sqlite:trial.db";
            Connection conn = null;
            conn = DriverManager.getConnection(dbURL);
            Statement st = conn.createStatement();
            // Creates all the tables in the database.
            String createTableUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "ID TEXT PRIMARY KEY," +
                    "Email VARCHAR(255) UNIQUE NOT NULL," +
                    "Password VARCHAR(255) NOT NULL," +
                    "Fname VARCHAR(255)," +
                    "Lname VARCHAR(255)" +
                    ");";
            st.execute(createTableUsers);
            String createTableBuilding = "CREATE TABLE IF NOT EXISTS Building (" +
                    "B_code VARCHAR(255) PRIMARY KEY," +
                    "campus VARCHAR(255) NOT NULL" +
                    ");";
            st.execute(createTableBuilding);
            String createTableRooms = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "roomNumber INT PRIMARY KEY," +
                    "capacity INT NOT NULL," +
                    "B_code VARCHAR(255)," +
                    "FOREIGN KEY (B_code) REFERENCES Building (B_code)" +
                    ");";
            st.execute(createTableRooms);
            String createTableReservations = "CREATE TABLE IF NOT EXISTS reservations (" +
                    "reservationID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "ID TEXT, " +
                    "roomNumber INT, " +
                    "time VARCHAR(255) NOT NULL, " +
                    "date VARCHAR(255) NOT NULL, " +
                    "reservationName VARCHAR(255) NOT NULL, " +
                    "B_code VARCHAR(255), " +
                    "FOREIGN KEY (ID) REFERENCES users (ID), " +
                    "FOREIGN KEY (roomNumber) REFERENCES rooms (roomNumber), " +
                    "FOREIGN KEY (B_code) REFERENCES Building (B_code)" +
                    ");";

            st.execute(createTableReservations);
            String insertFirstUser = "INSERT OR IGNORE INTO users (ID, Email, Password, Fname, Lname)" +
                    "VALUES (" +
                    "'admin'," +
                    "'example@udmercy.edu'," +
                    "'admin'," +
                    "'firstName'," +
                    "'Lastname'" +
                    ");";
            st.execute(insertFirstUser);
            String insertSecondUser = "INSERT OR IGNORE INTO users (ID, Email, Password, Fname, Lname)" +
                    "VALUES (" +
                    "'admin2'," +
                    "'example@udmercy.edu'," +
                    "'admin2'," +
                    "'firstName'," +
                    "'Lastname'" +
                    ");";
            st.execute(insertSecondUser);
            SwingUtilities.invokeLater(() -> new LoginGUI().createAndShowGUI());
        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}

//Creates the GUI where the admins can log in.
class LoginGUI {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JFrame frame;
    private final int height = 150;
    private final int width = 350;

    void createAndShowGUI() {
        frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,height);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new GridLayout(3, 2));

        contentPane.add(new JLabel("ID:"));
        txtEmail = new JTextField();
        contentPane.add(txtEmail);

        contentPane.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        contentPane.add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> login());
        contentPane.add(btnLogin);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Uses the database to make sure the password and ID are correct for the login.
    private void login() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "SELECT * FROM users WHERE ID = ? AND Password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, txtEmail.getText());
            preparedStatement.setString(2, new String(txtPassword.getPassword()));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose();
                new OptionsGUI().createAndShowGUI();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid email or password!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}

// Creates a options menu for all the selection that the admin can make.
class OptionsGUI {
    private JFrame frame;
    private final int height = 400;
    private final int width= 400;

    void createAndShowGUI() {
        frame = new JFrame("Options");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(height,width);

        frame.setLayout(new GridLayout(6, 1));

        JButton btnAddRoom = new JButton("Add Room");
        btnAddRoom.addActionListener(e -> {
            frame.dispose();
            new AddRoomGUI().createAndShowGUI();
        });
        frame.add(btnAddRoom);

        JButton btnDeleteRoom = new JButton("Delete Room");
        btnDeleteRoom.addActionListener(e -> {
            frame.dispose();
            new DeleteRoomGUI().createAndShowGUI();
        });
        frame.add(btnDeleteRoom);

        JButton btnUpdateRoom = new JButton("Update Room");
        btnUpdateRoom.addActionListener(e -> {
            frame.dispose();
            new UpdateRoomGUI().createAndShowGUI();
        });
        frame.add(btnUpdateRoom);

        JButton btnBookRoom = new JButton("Book Room");
        btnBookRoom.addActionListener(e -> {
            frame.dispose();
            new BookRoomGUI().createAndShowGUI();
        });
        frame.add(btnBookRoom);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> {
            frame.dispose();
            new SearchGUI().createAndShowGUI();
        });
        frame.add(btnSearch);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            frame.dispose();
            new LoginGUI().createAndShowGUI();
        });
        frame.add(btnLogout);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// This creates the add room GUI.
class AddRoomGUI {
    private JFrame frame;
    private JTextField txtRoomNumber;
    private JTextField txtCapacity;
    private JTextField txtBCode;
    private final int height = 400;
    private final int width= 400;

    void createAndShowGUI() {
        frame = new JFrame("Add Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(height,width);

        frame.setLayout(new GridLayout(4, 2));

        frame.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        frame.add(txtRoomNumber);

        frame.add(new JLabel("Capacity:"));
        txtCapacity = new JTextField();
        frame.add(txtCapacity);

        frame.add(new JLabel("Building Code (i.e. ENG):"));
        txtBCode = new JTextField();
        frame.add(txtBCode);

        JButton btnAddRoom = new JButton("Add Room");
        btnAddRoom.addActionListener(e -> addRoom());
        frame.add(btnAddRoom);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new OptionsGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Adds room to database once user enters all variables.
    private void addRoom() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "INSERT INTO rooms (roomNumber, capacity, B_code) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
            preparedStatement.setInt(2, Integer.parseInt(txtCapacity.getText()));
            preparedStatement.setString(3, txtBCode.getText());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(frame, "Room added successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add room!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}

// Creates the delete room GUI
class DeleteRoomGUI {
    private JFrame frame;
    private JTextField txtRoomNumber;

    private JTextField txtBCode;
    private final int height = 400;
    private final int width= 400;

    void createAndShowGUI() {
        frame = new JFrame("Delete Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(height,width);

        frame.setLayout(new GridLayout(3, 2));

        frame.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        frame.add(txtRoomNumber);

        frame.add((new JLabel("Building Code:")));
        txtBCode = new JTextField();
        frame.add(txtBCode);

        JButton btnDeleteRoom = new JButton("Delete Room");
        btnDeleteRoom.addActionListener(e -> deleteRoom());
        frame.add(btnDeleteRoom);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new OptionsGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Finds which room it must delete from the user then deletes it form the database.
    private void deleteRoom() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "DELETE FROM rooms WHERE roomNumber = ? AND B_code = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
            preparedStatement.setString(2, txtBCode.getText());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(frame, "Room deleted successfully!");
                String reservationDeletions = "DELETE FROM reservations WHERE roomNumber = ? AND B_code = ?";
                PreparedStatement deleteReservationsStatement = connection.prepareStatement(reservationDeletions);
                deleteReservationsStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
                deleteReservationsStatement.setString(2, txtBCode.getText());
                deleteReservationsStatement.executeUpdate();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to delete room!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}

// Creates UpdateRoom GUI.
class UpdateRoomGUI {
    private JFrame frame;
    private JTextField txtRoomNumber;
    private JTextField txtB_Code;
    private final int height = 400;
    private final int width= 400;

    void createAndShowGUI() {
        frame = new JFrame("Update Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2));
        frame.setSize(height,width);

        frame.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        frame.add(txtRoomNumber);

        frame.add(new JLabel("Building Code: (I.E ENG):"));
        txtB_Code = new JTextField();
        frame.add(txtB_Code);

        JButton btnUpdateRoom = new JButton("Update Room");
        btnUpdateRoom.addActionListener(e -> updateRoom());
        frame.add(btnUpdateRoom);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new OptionsGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Finds what room the user wants to update and opens a new GUI.
    private void updateRoom() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "SELECT * FROM rooms WHERE roomNumber = ? AND B_Code = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
            preparedStatement.setString(2, txtB_Code.getText());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                new UpdateRoomDetailsGUI(
                        resultSet.getInt("roomNumber"),
                        resultSet.getInt("capacity"),
                        resultSet.getString("B_code")
                ).createAndShowGUI();
                frame.dispose();
            } else {
                JOptionPane.showMessageDialog(frame, "Room not found!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}

// Creates GUI of the details of the room you want to update.
class UpdateRoomDetailsGUI {
    private JFrame frame;
    private JTextField txtRoomNumber;
    private JTextField txtCapacity;
    private JTextField txtBCode;
    private int originalRoomNumber;
    private final int height = 400;
    private final int width= 400;

    UpdateRoomDetailsGUI(int roomNumber, int capacity, String bCode) {
        this.originalRoomNumber = roomNumber;
        txtRoomNumber = new JTextField(Integer.toString(roomNumber));
        txtCapacity = new JTextField(Integer.toString(capacity));
        txtBCode = new JTextField(bCode);
    }

    void createAndShowGUI() {
        frame = new JFrame("Update Room Details");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width,height);

        frame.setLayout(new GridLayout(4, 2));

        frame.add(new JLabel("Room Number:"));
        frame.add(txtRoomNumber);

        frame.add(new JLabel("Capacity:"));
        frame.add(txtCapacity);

        frame.add(new JLabel("B_Code:"));
        frame.add(txtBCode);

        JButton btnUpdateRoom = new JButton("Update Room");
        btnUpdateRoom.addActionListener(e -> saveRoomDetails());
        frame.add(btnUpdateRoom);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new UpdateRoomGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    // Saves the room details to the database.
    private void saveRoomDetails() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "UPDATE rooms SET roomNumber = ?, capacity = ?, B_code = ? WHERE roomNumber = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
            preparedStatement.setInt(2, Integer.parseInt(txtCapacity.getText()));
            preparedStatement.setString(3, txtBCode.getText());
            preparedStatement.setInt(4, originalRoomNumber);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(frame, "Room updated successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to update room!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}



// Creates the book room GUI.
class BookRoomGUI {

    private JFrame frame;
    private JTextField txtRoomNumber;
    private JTextField txtB_Code;
    private JTextField txtDate;
    private JTextField txtStartTime;
    private JTextField txtEndTime;
    private JTextField txtID;
    private JTextField txtReservationName;
    private final int height = 400;
    private final int width= 400;
    void createAndShowGUI() {
        frame = new JFrame("Book Room");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(height,width);

        frame.setLayout(new GridLayout(8, 2));

        frame.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        frame.add(txtRoomNumber);

        frame.add(new JLabel("Building Code:"));
        txtB_Code = new JTextField();
        frame.add(txtB_Code);

        frame.add(new JLabel("Date (YYYY-MM-DD):"));
        txtDate = new JTextField();
        frame.add(txtDate);

        frame.add(new JLabel("Start Time (HH:MM):"));
        txtStartTime = new JTextField();
        frame.add(txtStartTime);

        frame.add(new JLabel("End Time (HH:MM):"));
        txtEndTime = new JTextField();
        frame.add(txtEndTime);

        frame.add(new JLabel("Reservation Name:"));
        txtReservationName = new JTextField();
        frame.add(txtReservationName);

        frame.add(new JLabel("User ID:"));
        txtID = new JTextField();
        frame.add(txtID);

        JButton btnBookRoom = new JButton("Book Room");
        btnBookRoom.addActionListener(e -> bookRoom());
        frame.add(btnBookRoom);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new OptionsGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Adds the room you want to book to the the database/
    private void bookRoom() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            String query = "SELECT * FROM reservations WHERE roomNumber = ? AND B_code = ? AND date = ? AND time = ?";
            String txtTime = (txtStartTime.getText() + "-" + txtEndTime.getText());
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            preparedStatement.setInt(1, Integer.parseInt(txtRoomNumber.getText()));
            preparedStatement.setString(2, txtB_Code.getText());
            preparedStatement.setString(3, txtDate.getText());
            preparedStatement.setString(4, txtTime);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                JOptionPane.showMessageDialog(frame, "Room is already booked at this time!");
            } else {
                query = "INSERT INTO reservations (ID, roomNumber, B_code, date, time, reservationName) VALUES (?, ?, ?, ?, ?, ?)";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, txtID.getText());
                preparedStatement.setInt(2, Integer.parseInt(txtRoomNumber.getText()));
                preparedStatement.setString(3, txtB_Code.getText());
                preparedStatement.setString(4, txtDate.getText());
                preparedStatement.setString(5, txtTime);
                preparedStatement.setString(6, txtReservationName.getText());

                int result = preparedStatement.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(frame, "Room booked successfully!");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to book room!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }
}

// Creates the GUI for the search for a booked Room.
class SearchGUI {

    private JFrame frame;
    private JTextField txtReservationName;
    private JTextField txtUserName;
    private JTextField txtRoomNumber;
    private JTextField txtB_Code;
    private final int height = 400;
    private final int width= 400;

    void createAndShowGUI() {
        frame = new JFrame("Search Reservations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2));
        frame.setSize(height,width);

        frame.add(new JLabel("Reservation Name:"));
        txtReservationName = new JTextField();
        frame.add(txtReservationName);

        frame.add(new JLabel("User ID:"));
        txtUserName = new JTextField();
        frame.add(txtUserName);

        frame.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        frame.add(txtRoomNumber);

        frame.add(new JLabel("Building Code (I.E. ENG):"));
        txtB_Code = new JTextField();
        frame.add(txtB_Code);

        JButton btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> searchReservations());
        frame.add(btnSearch);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new OptionsGUI().createAndShowGUI();
        });
        frame.add(btnBack);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Finds the reservation from the database that was searched for by the User.
    private void searchReservations() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:trial.db")) {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM reservations WHERE 1=1");
            if (!txtReservationName.getText().isEmpty()) {
                queryBuilder.append(" AND LOWER(ReservationName) LIKE LOWER(?)");
            }
            if (!txtUserName.getText().isEmpty()) {
                queryBuilder.append(" AND EXISTS (SELECT * FROM users WHERE LOWER(ID) LIKE LOWER(?))");
            }
            if (!txtRoomNumber.getText().isEmpty()) {
                queryBuilder.append(" AND roomNumber = ?");
            }
            if (!txtB_Code.getText().isEmpty()) {
                queryBuilder.append(" AND B_code = ?");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());
            int parameterIndex = 1;
            if (!txtReservationName.getText().isEmpty()) {
                preparedStatement.setString(parameterIndex++, "%" + txtReservationName.getText() + "%");
            }
            if (!txtUserName.getText().isEmpty()) {
                preparedStatement.setString(parameterIndex++, "%" + txtUserName.getText() + "%");
            }
            if (!txtRoomNumber.getText().isEmpty()) {
                preparedStatement.setInt(parameterIndex++, Integer.parseInt(txtRoomNumber.getText()));
            }
            if (!txtB_Code.getText().isEmpty()) {
                preparedStatement.setString(parameterIndex++, txtB_Code.getText());
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            new SearchResultGUI(resultSet).createAndShowGUI();
            frame.dispose();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database error!");
        }
    }


}

// Creates the GUI for the results from the reservation search.
class SearchResultGUI {
    private JFrame frame;
    private JTable table;
    private final int height = 400;
    private final int width= 400;

    SearchResultGUI(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }

            table = new JTable(data, columnNames);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error processing search results!");
        }
    }

    void createAndShowGUI() {
        frame = new JFrame("Search Results");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(width,height);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton btnBack = new JButton("Back");
        btnBack.addActionListener(e -> {
            frame.dispose();
            new SearchGUI().createAndShowGUI();
        });
        frame.add(btnBack, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}