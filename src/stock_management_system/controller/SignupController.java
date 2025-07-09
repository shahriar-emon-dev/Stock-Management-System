package stock_management_system.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import stock_management_system.DATA.DatabaseConnection;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SignupController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SignupController.class.getName());

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$"
    );

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField addressField;
    @FXML private Label messageLabel;
    @FXML private Button signupButton;
    @FXML private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setItems(FXCollections.observableArrayList("admin", "staff"));
        roleComboBox.getSelectionModel().select("staff");

        messageLabel.setVisible(false);
        addValidationListeners();
        LOGGER.info("SignupController initialized.");
    }

    private void addValidationListeners() {
        usernameField.textProperty().addListener((obs, oldV, newV) ->
            toggleError(usernameField, newV.length() < 3)
        );
        emailField.textProperty().addListener((obs, oldV, newV) ->
            toggleError(emailField, !newV.isEmpty() && !EMAIL_PATTERN.matcher(newV).matches())
        );
        passwordField.textProperty().addListener((obs, oldV, newV) ->
            toggleError(passwordField, !newV.isEmpty() && !PASSWORD_PATTERN.matcher(newV).matches())
        );
        confirmPasswordField.textProperty().addListener((obs, oldV, newV) ->
            toggleError(confirmPasswordField, !newV.equals(passwordField.getText()))
        );
    }

    private void toggleError(Control field, boolean error) {
        if (error && !field.getStyleClass().contains("field-error")) {
            field.getStyleClass().add("field-error");
        } else if (!error) {
            field.getStyleClass().removeAll("field-error");
        }
    }

    @FXML
    private void handleSignup() {
        messageLabel.setVisible(false);

        StringBuilder errs = new StringBuilder();
        if (firstNameField.getText().trim().isEmpty())      errs.append("First name is required.\n");
        if (lastNameField.getText().trim().isEmpty())       errs.append("Last name is required.\n");
        if (usernameField.getText().trim().length() < 3)     errs.append("Username ≥3 characters.\n");
        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches())
            errs.append("Valid email is required.\n");
        if (!PASSWORD_PATTERN.matcher(passwordField.getText()).matches())
            errs.append("Password must be 8+ chars, include upper, lower, digit.\n");
        if (!passwordField.getText().equals(confirmPasswordField.getText()))
            errs.append("Passwords do not match.\n");
        if (roleComboBox.getValue() == null)
            errs.append("Please select a role.\n");

        if (errs.length() > 0) {
            showMessage(errs.toString(), "error");
            return;
        }

        signupButton.setDisable(true);
        signupButton.setText("Creating…");

        try (Connection conn = DatabaseConnection.getConnection()) {
            // check duplicate username/email
            String dupQ = "SELECT COUNT(*) FROM users WHERE username=? OR email=?";
            try (PreparedStatement ps = conn.prepareStatement(dupQ)) {
                ps.setString(1, usernameField.getText().trim());
                ps.setString(2, emailField.getText().trim());
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showMessage("Username or email already taken.", "error");
                    return;
                }
            }

            conn.setAutoCommit(false);

            // insert users
            String userIns = "INSERT INTO users(username,password_hash,role,email,created_at) VALUES(?,?,?,?,NOW())";
            try (PreparedStatement pu = conn.prepareStatement(userIns, Statement.RETURN_GENERATED_KEYS)) {
                pu.setString(1, usernameField.getText().trim());
                pu.setString(2, hash(passwordField.getText()));
                pu.setString(3, roleComboBox.getValue());
                pu.setString(4, emailField.getText().trim());
                pu.executeUpdate();

                ResultSet gen = pu.getGeneratedKeys();
                if (!gen.next()) throw new SQLException("Failed to retrieve user ID");
                int userId = gen.getInt(1);

                // insert profile
                String profIns = "INSERT INTO user_profiles(user_id, first_name, last_name, phone_number, address) VALUES(?,?,?,?,?)";
                try (PreparedStatement pp = conn.prepareStatement(profIns)) {
                    pp.setInt(1, userId);
                    pp.setString(2, firstNameField.getText().trim());
                    pp.setString(3, lastNameField.getText().trim());
                    pp.setString(4, phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
                    pp.setString(5, addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                    pp.executeUpdate();
                }
            }

            conn.commit();
            showMessage("Account created! Redirecting…", "success");

            PauseTransition wait = new PauseTransition(Duration.seconds(2));
            wait.setOnFinished(e -> redirectToLogin());
            wait.play();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Signup failed", e);
            showMessage("Registration error. Please try again.", "error");
        } finally {
            signupButton.setDisable(false);
            signupButton.setText("Create Account");
        }
    }

    @FXML
    private void handleGoToLogin() {
        redirectToLogin();
    }

    private void redirectToLogin() {
        try {
            Parent loginRoot = FXMLLoader.load(
                getClass().getResource("/com/stock_management_system/Login.fxml")
            );
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Stock Management — Login");
            stage.centerOnScreen();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot load Login.fxml", ex);
            showMessage("Could not load login screen.", "error");
        }
    }

    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] dig = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : dig) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException ne) {
            throw new RuntimeException(ne);
        }
    }

    private void showMessage(String text, String type) {
        messageLabel.setText(text);
        messageLabel.getStyleClass().removeAll("error","success");
        messageLabel.getStyleClass().add(type);
        messageLabel.setVisible(true);
    }
}
