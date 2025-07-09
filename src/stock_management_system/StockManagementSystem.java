package stock_management_system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StockManagementSystem extends Application {

    @Override
public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(
        getClass().getResource("/com/stock_management_system/Login.fxml")
    );
    stage.setScene(new Scene(root));
    stage.setTitle("Stock Management System");
    stage.setResizable(false);
    stage.show();
}
}
