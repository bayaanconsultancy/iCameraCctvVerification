package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DownloadTemplateWindow {

    public DownloadTemplateWindow() {
        JFrame frame = new JFrame("Download Template Excel");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("CCTV Template Download");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153)); // Dark blue color
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across 2 columns
        frame.add(titleLabel, gbc);

        // Middle Text Area
        JTextArea messageArea = getStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(messageArea); // Encapsulate in JScrollPane for better usability
        scrollPane.setPreferredSize(new Dimension(480, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Subtle border
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(scrollPane, gbc);

        // Download Button
        JButton downloadButton = createStyledButton("Download Template");
        downloadButton.addActionListener(e -> downloadExcelTemplate(frame, downloadButton));

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        frame.add(downloadButton, gbc);

        // Exit Button (Disabled at start)
        JButton exitButton = createStyledButton("Exit");
        exitButton.setEnabled(false);
        exitButton.addActionListener(e -> System.exit(0));

        gbc.gridy = 3;
        frame.add(exitButton, gbc);

        // Finalizing Frame
        frame.setVisible(true);
    }

    private JTextArea getStyledTextArea() {
        int errorCount = DataStore.getOnvifErrorCount();
        JTextArea messageArea = new JTextArea("""
                %s encountered while retrieving CCTV details and checking the RTSP feeds.
                
                1. Please download the CCTV details template Excel,
                2. Fill necessary details like CCTV Name, Inside Room, etc.,
                3. Correct the information which are incorrect,
                4. Then verify the updated CCTV details template using this utility.
                """.formatted(errorCount == 0 ? "No errors" : errorCount + " errors"));

        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setForeground(new Color(80, 80, 80)); // Dark gray text color
        messageArea.setBackground(new Color(250, 250, 250)); // Light background to stand out
        return messageArea;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(0, 153, 76)); // Green background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 51), 1), // Dark green border
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding
        ));
        return button;
    }

    private void downloadExcelTemplate(JFrame frame, JButton exitButton) {
        try {
            File file = FileDialogUtils.saveFileDialog("iCamera-cctv-template", frame);
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(),
                        DataStore.getIdentifiedCctvs());
                JOptionPane.showMessageDialog(frame, "Template Excel downloaded successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                exitButton.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error creating excel template: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}