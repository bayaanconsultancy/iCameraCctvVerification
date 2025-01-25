package com.cs.on.icamera.cctv.swing;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDialogUtils {

    private FileDialogUtils() {
    }

    public static File openExcelFileDialog(JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xls", "xlsx");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Select Excel File");
        int result = fileChooser.showOpenDialog(parentFrame);

        if (result == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile();
        else
            return null;
    }

    public static File saveFileDialog(String suggestedFileName, JFrame parentFrame) {
        JFileChooser fileChooser = new JFileChooser();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileName = date + "-" + suggestedFileName + ".xlsx";
        fileChooser.setSelectedFile(new File(fileName));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("Save Excel File");

        int result = fileChooser.showSaveDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(parentFrame,
                        "File already exists. Do you want to overwrite it?",
                        "File Exists", JOptionPane.YES_NO_OPTION);

                if (overwrite != JOptionPane.YES_OPTION)
                    return null;
            }
            return selectedFile;
        }
        return null;
    }

    // Example UI using GridBagLayout to improve the overall user experience
    public static void showEnhancedUI() {
        JFrame frame = new JFrame("File Dialog Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("File Management Utility");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Spanning two columns
        frame.add(titleLabel, gbc);

        // Instruction Label
        JLabel instructionLabel = new JLabel("Select or save an Excel file using the buttons below:");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        instructionLabel.setForeground(new Color(70, 70, 70)); // Gray color
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(instructionLabel, gbc);

        // Open File Button
        JButton openFileButton = createStyledButton("Open Excel File");
        openFileButton.addActionListener(e -> {
            File selectedFile = openExcelFileDialog(frame);
            if (selectedFile != null) {
                JOptionPane.showMessageDialog(frame,
                        "File Selected: " + selectedFile.getAbsolutePath(),
                        "File Chooser",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        frame.add(openFileButton, gbc);

        // Save File Button
        JButton saveFileButton = createStyledButton("Save Excel File");
        saveFileButton.addActionListener(e -> {
            File saveFile = saveFileDialog("iCamera-File", frame);
            if (saveFile != null) {
                JOptionPane.showMessageDialog(frame,
                        "File Saved: " + saveFile.getAbsolutePath(),
                        "File Save Dialog",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 1;
        frame.add(saveFileButton, gbc);

        // Exit Button
        JButton exitButton = createStyledButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        frame.add(exitButton, gbc);

        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76)); // Green background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 139, 34), 2), // Darker green border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside the button
        ));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileDialogUtils::showEnhancedUI);
    }
}