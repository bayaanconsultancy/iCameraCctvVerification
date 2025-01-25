package com.cs.on.icamera.cctv.swing;

import com.cs.on.icamera.cctv.data.DataStore;
import com.cs.on.icamera.cctv.model.Cctv;
import com.cs.on.icamera.cctv.xssf.ExportColumns;
import com.cs.on.icamera.cctv.xssf.SheetNames;
import com.cs.on.icamera.cctv.xssf.TemplateColumns;
import com.cs.on.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ExportExcelWindow {

    private final List<Cctv> allCctvs;
    private final List<Cctv> okCctvs;

    public ExportExcelWindow() {
        this.allCctvs = DataStore.getCctvsToVerify();
        this.okCctvs = DataStore.getResources();

        JFrame frame = new JFrame("Export CCTV Details");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridBagLayout());
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // Light gray background

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20); // Padding between components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title Label
        JLabel titleLabel = new JLabel("CCTV Export Utility");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 102, 153)); // Dark blue color
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        frame.add(titleLabel, gbc);

        // Instructions Text Area
        JTextArea messageArea = getStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(messageArea); // Add scrollpane for better usability
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200))); // Subtle border
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        frame.add(scrollPane, gbc);

        // Download Button
        JButton downloadButton = createStyledButton("Download CCTV Templates");
        downloadButton.addActionListener(e -> downloadExcelTemplate(frame, downloadButton));
        gbc.gridy = 2;
        gbc.gridwidth = 1; // Reset to one column
        gbc.gridx = 0; // Align on the left
        frame.add(downloadButton, gbc);

        // Exit Button
        JButton exitButton = createStyledButton("Exit");
        exitButton.setEnabled(false); // Initially disabled
        exitButton.addActionListener(e -> System.exit(0));
        gbc.gridx = 1; // Align on the right
        frame.add(exitButton, gbc);

        // Finalizing frame
        frame.setVisible(true);
    }

    private JTextArea getStyledTextArea() {
        int errorCount = allCctvs.size() - okCctvs.size();
        JTextArea messageArea = new JTextArea("""
                %s encountered while checking the RTSP feeds of CCTV Cameras.
                
                1. Please download the CCTV details by 'Download CCTV Templates' button,
                2. Fill necessary details like CCTV Name, Inside Room, etc. in the 'CCTVs' sheet,
                3. Correct any incorrect information,
                4. Then re-verify the updated CCTV details template using this utility.
                """.formatted(errorCount == 0 ? "No errors" : errorCount + " errors"));

        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setForeground(new Color(70, 70, 70)); // Dark gray text
        messageArea.setBackground(new Color(250, 250, 250)); // Light background for better contrast
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
            File file = FileDialogUtils.saveFileDialog("iCamera-cctv-details", frame);
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), allCctvs);
                Workbook.writeCctvsToExcel(file, false, SheetNames.EXPORT, true, ExportColumns.values(), okCctvs);
                JOptionPane.showMessageDialog(frame,
                        "iCamera CCTV details Excel downloaded successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                exitButton.setEnabled(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Error creating excel: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}