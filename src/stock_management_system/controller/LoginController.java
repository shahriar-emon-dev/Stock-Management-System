package stock_management_system.controller;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import stock_management_system.DATA.DatabaseConnection;
import stock_management_system.model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField    usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         messageLabel;
    @FXML private Label         welcomeLabel;
    @FXML private Button        loginButton;
    @FXML private Hyperlink     signupRedirectButton;
    @FXML private ImageView     logoImage;  // if you load a logo at runtime

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // hide feedback until needed
        messageLabel.setVisible(false);
        welcomeLabel.setVisible(false);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter both username and password.");
            messageLabel.setVisible(true);
            return;
        }

        String sql = """
            SELECT
              u.id, u.username, u.role, u.email,
              p.first_name, p.last_name, p.phone_number, p.address
            FROM users u
            LEFT JOIN user_profiles p ON u.id = p.user_id
            WHERE u.username = ? AND u.password = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);
            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    messageLabel.setText("Invalid username or password.");
                    messageLabel.setVisible(true);
                    return;
                }

                // build the User object
                int    id        = rs.getInt("id");
                String usern     = rs.getString("username");
                String role      = rs.getString("role");
                String email     = rs.getString("email");
                String fn        = rs.getString("first_name");
                String ln        = rs.getString("last_name");
                String phone     = rs.getString("phone_number");
                String address   = rs.getString("address");
                String fullName  = ((fn!=null?fn:"") + " " + (ln!=null?ln:"")).trim();

                User user = new User(
                  id,
                  usern,
                  null,       // never store plaintext in memory
                  fullName,
                  email,
                  phone,
                  address,
                  role
                );

                // show a quick welcome
                welcomeLabel.setText("Welcome, " + (fullName.isEmpty()?usern:fullName) + "!");
                welcomeLabel.setVisible(true);

                // after 1s, switch scenes
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> {
                    try {
                        String fxml = role.equalsIgnoreCase("admin")
                            ? "/com/stock_management_system/AdminDashboard.fxml"
                            : "/com/stock_management_system/Profile.fxml";

                        FXMLLoader loader = new FXMLLoader(
                            getClass().getResource(fxml)
                        );
                        Parent root = loader.load();

                        // inject user into the next controller
                        if (role.equalsIgnoreCase("admin")) {
                            AdminDashboardController adc = loader.getController();
                            adc.initData(user);
                        } else {
                            ProfileController pc = loader.getController();
                            pc.initData(user);
                        }

                        Stage stage = (Stage) loginButton.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Stock Management System");
                        stage.centerOnScreen();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        messageLabel.setText("Failed to load next screen.");
                        messageLabel.setVisible(true);
                    }
                });
                pause.play();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            messageLabel.setText("Database error. Please try again later.");
            messageLabel.setVisible(true);
        }
    }

    @FXML
    private void handleGoToSignup(ActionEvent event) {
        try {
            Parent signup = FXMLLoader.load(
                getClass().getResource("/com/stock_management_system/Signup.fxml")
            );
            Stage stage = (Stage) signupRedirectButton.getScene().getWindow();
            stage.setScene(new Scene(signup));
            stage.setTitle("Create Account");
            stage.centerOnScreen();
        } catch (IOException ex) {
            ex.printStackTrace();
            messageLabel.setText("Could not open signup page.");
            messageLabel.setVisible(true);
        }
    }
}
