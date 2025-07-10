package stock_management_system;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.DB; // Import the DB class for database connection

public class LoginController implements Initializable {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;
    @FXML
    private Button loginButton;
    @FXML
    private Hyperlink signupRedirectButton;

    private Connection connection;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get connection using DB utility class
        connection = DB.getConnection();
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please fill in both fields.");
            messageLabel.setVisible(true);
            return;
        }

        // SQL query to check the user credentials
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // User found, handle successful login
                String role = resultSet.getString("role");

                // Navigate to appropriate dashboard based on user role
                if ("admin".equals(role)) {
                    // Open admin dashboard
                    loadPage("/stock_management_system/AdminDashboard.fxml");
                } else if ("user".equals(role)) {
                    // Open user dashboard
                    loadPage("/stock_management_system/UserDashboard.fxml");
                }

            } else {
                // Invalid login credentials
                messageLabel.setText("Invalid username or password.");
                messageLabel.setVisible(true);
            }

        } catch (SQLException ex) {
            // Database error handling
            ex.printStackTrace();
            messageLabel.setText("Database error occurred.");
            messageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleGoToSignup(ActionEvent event) {
        // Navigate to Signup page
        loadPage("/stock_management_system/Signup.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load page: " + fxmlPath);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
