package com.roboworks.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Main extends Application {

    private static final int WIDTH = 7;
    private static final int HEIGHT = 5;
    private String defaultPort = "/dev/rfcomm0";
    private String serialPort = defaultPort;
    private Position deliver;
    private Position retrieve;
    private Color deliverColor = Color.GREEN;
    private Color retrieveColor = Color.BLUE;
    private Color gridColor1 = Color.RED;
    private Color gridColor2 = Color.BLACK;
    private Label[][] grid;
    private BluetoothController bluetoothController;

    @Override
    public void start(Stage stage) {
        /* Initialise legend */
        HBox deliverLegend = new HBox();
        HBox retrieveLegend = new HBox();
        retrieveLegend.getChildren().addAll(new Rectangle(15, 15, retrieveColor), new Label("Retrieve"));
        deliverLegend.getChildren().addAll(new Rectangle(15, 15, deliverColor), new Label("Deliver"));
        deliverLegend.setSpacing(10);
        retrieveLegend.setSpacing(10);
        deliverLegend.setAlignment(Pos.CENTER_LEFT);
        retrieveLegend.setAlignment(Pos.CENTER_LEFT);
        VBox legend = new VBox(deliverLegend, retrieveLegend);

        /* Initialise grid */
        GridPane gridPane = new GridPane();
        grid = makeGridPane(gridPane, WIDTH, HEIGHT);

        Button btnSubmit = new Button("Submit");

        btnSubmit.setOnAction(event -> {
            if (deliver != null) {
                bluetoothController.sendMessage(new Message(Message.Type.RETRIEVE, new Position(0, 0)));
                bluetoothController.sendMessage(new Message(Message.Type.DELIVER, deliver));
            }
            if (retrieve != null) bluetoothController.sendMessage(new Message(Message.Type.RETRIEVE, retrieve));
        });

        /* Set serial port */
        Label portLabel = new Label("Serial Port:");
        TextField portField = new TextField();
        portField.setText(defaultPort);
        portField.textProperty().addListener((c, o, n) -> {
            serialPort = n;
            System.out.println(serialPort);
        });
        HBox portBox = new HBox(portLabel, portField);
        portBox.setSpacing(10);
        portBox.setAlignment(Pos.CENTER_LEFT);
        Button connect = new Button("Connect");
        Button disconnect = new Button("Disconnect");
        connect.setOnAction(e -> {
            bluetoothController = new BluetoothController(serialPort);
            bluetoothController.open();
            portField.setDisable(true);
            disconnect.setDisable(false);
            connect.setDisable(true);
            btnSubmit.setDisable(false);
        });
        disconnect.setOnAction(e -> {
            bluetoothController.close();
            bluetoothController = null;
            portField.setDisable(false);
            connect.setDisable(false);
            disconnect.setDisable(true);
            btnSubmit.setDisable(true);
        });
        disconnect.setDisable(true);
        btnSubmit.setDisable(true);
        HBox connectBox = new HBox(connect, disconnect);
        connectBox.setSpacing(10);
        connectBox.setAlignment(Pos.CENTER_LEFT);
        VBox portVBox = new VBox(portBox, connectBox);
        portVBox.setSpacing(10);

        VBox vBox = new VBox(legend, gridPane, portVBox, btnSubmit);
        vBox.setSpacing(10);

        Scene scene = new Scene(vBox, 1280, 720);

        stage.setScene(scene);
        stage.getIcons().add(new Image("com/roboworks/gui/images/boebot.png"));
        stage.setTitle("Boebot bluetooth application");
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(Main.class);
    }

    private Label[][] makeGridPane(GridPane gridPane, int x, int y) {
        Label[][] grid = new Label[x][y];

        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                Color c = getGridColor(i, j);

                Label label = new Label();
                if ((j & 1) == 0) {
                    label.setText(Integer.toString(i + (j * x)));
                } else {
                    label.setText(Integer.toString((x - 1 + (j * x)) - i));
                }

                label.getProperties().put('x', i);
                label.getProperties().put('y', j);

                label.setAlignment(Pos.CENTER);
                label.setPrefSize(50, 50);
                label.setTextFill(Color.WHITE);
                label.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));

                label.setOnMouseClicked(e -> {
                    Label l = (Label) e.getSource();
                    int lx = (int) l.getProperties().get('x');
                    int ly = (int) l.getProperties().get('y');
                    Position newPos = new Position(lx, ly);

                    switch (e.getButton()) {
                        case PRIMARY:
                            deliver = updatePos(newPos, deliver, retrieve, l, deliverColor);
                            break;
                        case SECONDARY:
                            retrieve = updatePos(newPos, retrieve, deliver, l, retrieveColor);
                            break;
                    }
                });

                gridPane.add(label, i, j);
                grid[i][j] = label;
            }
        }

        return grid;
    }

    private Color getGridColor(int x, int y) {
        return (x & 1) == (y & 1) ? gridColor1 : gridColor2;
    }

    private Position updatePos(Position newPos, Position oldPos, Position conflict, Label label, Color color) {
        if (oldPos != null && oldPos.equals(newPos)) {
            label.setBackground(new Background(new BackgroundFill(getGridColor(newPos.getX(), newPos.getY()), CornerRadii.EMPTY, Insets.EMPTY)));
            return null;
        } else {
            if (conflict != null && conflict.equals(newPos)) return oldPos;
            if (oldPos != null) {
                grid[oldPos.getX()][oldPos.getY()].setBackground(new Background(new BackgroundFill(getGridColor(oldPos.getX(), oldPos.getY()), CornerRadii.EMPTY, Insets.EMPTY)));
            }
            label.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
            return newPos;
        }
    }
}
