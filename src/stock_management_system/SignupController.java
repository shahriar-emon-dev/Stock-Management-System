package stock_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import util.DB; // Import the DB class for database connection

public class SignupController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox; // ComboBox for role selection
    @FXML
    private TextField addressField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button signupButton;
    
    private Connection connection;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up the role ComboBox options
        roleComboBox.getItems().addAll("Admin", "User"); // Populating the ComboBox with roles
        connection = DB.getConnection();  // Initialize DB connection
    }    

    @FXML
    private void handleSignup(ActionEvent event) {
        // Collecting data from the input fields
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue(); // Get the selected role
        String address = addressField.getText();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            messageLabel.setText("Please fill in all required fields.");
            messageLabel.setVisible(true);
            return;
        }

        // Validate password confirmation
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            messageLabel.setVisible(true);
            return;
        }

        // SQL query to insert the user into the database
        String query = "INSERT INTO users (first_name, last_name, username, email, phone, password, role, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            // Setting the values to be inserted into the database
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, username);
            statement.setString(4, email);
            statement.setString(5, phone);
            statement.setString(6, password);
            statement.setString(7, role);
            statement.setString(8, address);

            // Execute the query and check if the insertion was successful
            int result = statement.executeUpdate();
            if (result > 0) {
                messageLabel.setText("Account created successfully.");
                messageLabel.setVisible(true);
                // Redirect to the login page after successful signup
                redirectToLoginPage();
            } else {
                messageLabel.setText("Error creating account.");
                messageLabel.setVisible(true);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Database error occurred.");
            messageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        // Navigate to the Login page (if not signed up successfully yet)
        redirectToLoginPage();
    }

    private void redirectToLoginPage() {
        try {
            // Load the Login.fxml page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stock_management_system/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) signupButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading the login page.");
            messageLabel.setVisible(true);
        }
    }
}
