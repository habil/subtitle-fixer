package com.bozali.habil.fixer;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class Controller {

    final DirectoryChooser chooser = new DirectoryChooser();
    File selectedDir;
    public Label seledtedFolderLabel;
    public Button runButton;
    public ProgressBar progressbar;

    public void chooseAction() {
        chooser.setTitle("Choose Movie Directory");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        selectedDir = chooser.showDialog(null);
        if (selectedDir == null) {
            showDialog(Alert.AlertType.WARNING, "Folder Selection Warning", "You need to choose a directory");
        } else {
            seledtedFolderLabel.setText("Selected Folder : " + selectedDir.getPath());
            runButton.setDisable(false);
        }
    }

    public void runAction() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(10);
                    final int count = i;
                    Platform.runLater(() -> fixNames(selectedDir));
                    updateProgress(i, 10);
                }
                return null;
            }
        };

        progressbar.progressProperty().bind(task.progressProperty());
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();

        showDialog(Alert.AlertType.INFORMATION,"Result", "Mission completed!");
    }

    private void fixNames(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                fixNames(f);
            }
        } else {
            if (file.getName().matches(".*[\\.avi,\\.mpg,\\.wmv,\\.mkv,\\.mp4,\\Q.mpg-2\\E]")) {
                String movieName = file.getName().substring(0, file.getName().lastIndexOf("."));
                File parentFile = file.getParentFile();
                File[] files = parentFile.listFiles();
                String subtitleName, newSubtitleName;
                for (File f : files) {
                    if (f.getName().endsWith(".srt")) {
                        subtitleName = f.getName().substring(0, f.getName().lastIndexOf("."));
                        newSubtitleName = f.getPath().replace(subtitleName, movieName);
                        if (!new File(newSubtitleName).exists()) {
                            f.renameTo(new File(f.getPath().replace(subtitleName, movieName)));
                        }
                    }
                }
            }
        }
    }

    private void showDialog(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
