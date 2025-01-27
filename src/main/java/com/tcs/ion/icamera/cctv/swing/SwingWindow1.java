package com.tcs.ion.icamera.cctv.swing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;

public abstract class SwingWindow1 {

    // Professional color palette
    protected static final Color BACKGROUND_COLOR = new Color(224, 224, 224);
    protected static final Color FOREGROUND_COLOR = new Color(32, 32, 64);
    protected static final Color ACCENT_COLOR = new Color(0, 128, 255);
    protected static final Color BORDER_COLOR = new Color(128, 128, 192);
    private final JFrame frame;
    private int gridy = 0;

    protected SwingWindow1(String title) {
        frame = new JFrame(title);
        frame.setSize(640, 480);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    protected static JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    protected static JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setBackground(Color.WHITE);
        textField.setForeground(FOREGROUND_COLOR);
        textField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return textField;
    }

    protected static JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setBackground(Color.WHITE);
        textArea.setForeground(FOREGROUND_COLOR);
        textArea.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    protected static JButton createOkButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(FOREGROUND_COLOR);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(128, 192, 128), 1), // Border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        return button;
    }

    protected static JButton createSkipButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(FOREGROUND_COLOR);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(192, 128, 128), 1), // Border
                BorderFactory.createEmptyBorder(5, 10, 5, 10) // Padding
        ));
        return button;
    }

    public static JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Display percentage
        return progressBar;
    }

    protected static JScrollPane createScrollPanel(JTextArea textArea) {
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return scrollPane;
    }

    protected JPasswordField createPasswordField(int i) {
        JPasswordField passwordField = new JPasswordField(i);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(FOREGROUND_COLOR);
        passwordField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        return passwordField;
    }

    protected void add(Component... components) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridy = gridy++;
        int gridx = 0;
        for (Component component : components) {
            gbc.gridx = gridx++;
            frame.add(component, gbc);
        }
    }

    // Abstract method that subclasses must implement
    protected abstract void buildUiAndFunctionality();

    public void show() {
        SwingUtilities.invokeLater(() -> {
            this.buildUiAndFunctionality();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public void next(Runnable action) {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            action.run();
        });
    }

    public void next(SwingWindow1 sw) {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            sw.show();
        });
    }

    protected void doInBackground(Runnable todo, Runnable next) {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                todo.run();
                return null;
            }

            @Override
            protected void done() {
                next.run();
            }
        };

        worker.execute();
    }

    protected JDialog getLoadingDialog(String message) {
        JDialog loadingDialog = new JDialog(frame, "Please Wait", true);
        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingDialog.add(loadingLabel, BorderLayout.CENTER);
        loadingDialog.setSize(300, 100);
        loadingDialog.setLocationRelativeTo(frame);
        return loadingDialog;
    }

    protected JProgressBar runWithProgress(JLabel statusLabel, Runnable todo, IntSupplier progress, IntSupplier count, IntSupplier totalCount, BooleanSupplier isComplete, SwingWindow1 swingWindow) {
        JProgressBar progressBar = createProgressBar();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                progressBar.setValue(progress.getAsInt());
                statusLabel.setText(String.format("Processed %d of %d.", count.getAsInt(), totalCount.getAsInt()));
                if (isComplete.getAsBoolean()) {
                    timer.cancel();
                    next(swingWindow);
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
        new Thread(todo).start();
        return progressBar;
    }

    protected void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    protected File openExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Select Excel File");
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        else
            return null;
    }

    protected void repaintFrame() {
        frame.repaint();
    }

    protected void revalidateFrame() {
        frame.revalidate();
    }

    protected void disableFrame() {
        frame.setEnabled(false);
    }

    protected void enableFrame() {
        frame.setEnabled(true);
    }

    protected File saveExcelFile(String suggestedFileName) {
        // Constants for easy maintenance
        final String DATE_FORMAT = "yyyy-MM-dd";
        final String FILE_EXTENSION = ".xlsx";
        final String FILTER_DESCRIPTION = "Excel Files";
        final String DIALOG_TITLE = "Save Excel File";
        final String FILE_EXISTS_WARNING = "File already exists. Do you want to overwrite it?";
        final String FILE_EXISTS_TITLE = "File Exists";

        // Setup file chooser
        JFileChooser fileChooser = new JFileChooser();
        String date = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        String fileName = date + "-" + suggestedFileName + FILE_EXTENSION;
        fileChooser.setSelectedFile(new File(fileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter(FILTER_DESCRIPTION, "xlsx"));
        fileChooser.setDialogTitle(DIALOG_TITLE);

        // Show save dialog
        if (fileChooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File selectedFile = fileChooser.getSelectedFile();

        // Handle file existence
        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                    frame,
                    FILE_EXISTS_WARNING,
                    FILE_EXISTS_TITLE,
                    JOptionPane.YES_NO_OPTION
            );

            // Only proceed if user confirms overwrite
            if (overwrite != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return selectedFile;
    }
}