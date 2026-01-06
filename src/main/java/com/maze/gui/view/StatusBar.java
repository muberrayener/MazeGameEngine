package com.maze.gui.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Durum çubuğu - Alt taraf.
 */
public class StatusBar extends HBox {

    private Label statusLabel;

    public StatusBar() {
        setPadding(new Insets(5, 10, 5, 10));
        setStyle("-fx-background-color: #34495e; -fx-border-color: #2c3e50; -fx-border-width: 1 0 0 0;");

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        getChildren().add(statusLabel);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void appendStatus(String text) {
        statusLabel.setText(statusLabel.getText() + text);
    }
}