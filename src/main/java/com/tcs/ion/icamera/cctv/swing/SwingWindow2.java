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

public abstract class SwingWindow2 {

    // Color palette
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color FOREGROUND_COLOR = new Color(50, 50, 50);
    private static final Color ACCENT_COLOR = new Color(0, 120, 215);
    private static final Color BORDER_COLOR = new Color(180, 180, 180);

    private final JFrame frame;
    private int gridy = 0;

    protected SwingWindow2(String title) {
        frame = new JFrame(title);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
    }

    /**
     * Adds components to GridBagLayout with full width and fixed height based on constraints.
     */
    protected void add(Component component, int width, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = gridy++;
        gbc.gridwidth = width;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        gbc.fill = fill;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(component, gbc);
    }

    /**
     * Creates a user-friendly label with consistent settings.
     */
    protected JLabel createLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(FOREGROUND_COLOR);
        return label;
    }

    /**
     * Creates a fixed-size text field.
     */
    protected JTextField createTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setPreferredSize(new Dimension(200, 30)); // Fixed size
        configureComponent(textField);
        return textField;
    }

    /**
     * Creates a password field with the same fixed size as text fields.
     */
    protected JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setPreferredSize(new Dimension(200, 30));
        configureComponent(passwordField);
        return passwordField;
    }

    /**
     * Creates a styled button for consistent visuals.
     */
    protected JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        return button;
    }

    /**
     * Creates a progress bar with percentage display.
     */
    protected JProgressBar createProgressBar() {
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 30));
        return progressBar;
    }

    /**
     * Shows a dialog with success, warning, or error messages.
     */
    protected void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(frame, message, title, messageType);
    }

    protected void showSuccessMessage(String message) {
        showMessage(message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showWarningMessage(String message) {
        showMessage(message, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    protected void showErrorMessage(String message) {
        showMessage(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Starts a background task with visual progress updates.
     */
    protected JProgressBar runWithProgress(JLabel statusLabel, Runnable todo, IntSupplier progress, IntSupplier count, IntSupplier totalCount, BooleanSupplier isComplete, SwingWindow2 swingWindow) {
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

    /**
     * Opens a file chooser for Excel files.
     */
    protected File openExcelFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xls", "xlsx"));
        fileChooser.setDialogTitle("Select Excel File");
        int result = fileChooser.showOpenDialog(frame);
        return result == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    /**
     * Saves to an Excel file after verifying for overwrites.
     */
    protected File saveExcelFile(String suggestedFileName) {
        JFileChooser fileChooser = new JFileChooser();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        fileChooser.setSelectedFile(new File(date + "-" + suggestedFileName + ".xlsx"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
        fileChooser.setDialogTitle("Save Excel File");

        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(frame, "File exists. Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return null;
                }
            }
            return selectedFile;
        }
        return null;
    }

    /**
     * Utilities for the JFrame lifecycle.
     */
    public void show() {
        SwingUtilities.invokeLater(() -> {
            buildUiAndFunctionality();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    protected void next(Runnable action) {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            action.run();
        });
    }

    protected void next(SwingWindow2 sw) {
        SwingUtilities.invokeLater(() -> {
            frame.dispose();
            sw.show();
        });
    }

    /**
     * Configures common UI settings for components.
     */
    private void configureComponent(JComponent component) {
        component.setBackground(Color.WHITE);
        component.setForeground(FOREGROUND_COLOR);
        component.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        component.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    /**
     * Abstract method to build UI and define functionality.
     */
    protected abstract void buildUiAndFunctionality();
}