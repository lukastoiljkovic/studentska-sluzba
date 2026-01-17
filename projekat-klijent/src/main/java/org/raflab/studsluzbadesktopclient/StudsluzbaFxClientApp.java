package org.raflab.studsluzbadesktopclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class StudsluzbaFxClientApp extends Application {

    protected ConfigurableApplicationContext springContext;

    public static void main(String[] args) {
        launch(StudsluzbaFxClientApp.class, args);
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(StudsluzbaFxClientApp.class);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("RAF Studentska Služba");
        MainView mainView = springContext.getBean(MainView.class);
        primaryStage.setScene(mainView.createScene());
        //primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}