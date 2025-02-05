package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.xssf.SheetNames;
import com.tcs.ion.icamera.cctv.xssf.TemplateColumns;
import com.tcs.ion.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DownloadTemplateWindow extends SwingWindow {

    public DownloadTemplateWindow() {
        super("Download Template Excel");
    }

    private JTextArea getTextArea() {
        int errorCount = DataStore.getOnvifCctvErrorCount();
        JTextArea messageArea = createTextArea(6, 1);
        messageArea.setText("""
                %s encountered while retrieving CCTV details and checking the RTSP feeds.
                
                1. Please download the CCTV details template Excel,
                2. Fill necessary details like CCTV Name, Inside Room, etc.,
                3. Correct the information which are incorrect,
                4. Then verify the updated CCTV details template using this utility.
                """.formatted(errorCount == 0 ? "No errors" : errorCount + " errors"));

        messageArea.setEditable(false);
        return messageArea;
    }


    private void downloadExcelTemplate(JButton exitButton) {
        try {
            File file = saveExcelFile("iCamera-cctv-template");
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), DataStore.getIdentifiedCctvs());
                showSuccessMessage("Template Excel downloaded successfully.");
                exitButton.setEnabled(true);
            }
        } catch (Exception e) {
            showErrorMessage("Error creating excel template: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
        }
    }

    @Override
    protected void buildUiAndFunctionality() {
        // Title Label
        JLabel titleLabel = createLabel("CCTV Template Download");

        // Middle Text Area
        JTextArea messageArea = getTextArea();
        JScrollPane scrollPane = new JScrollPane(messageArea); // Encapsulate in JScrollPane for better usability
        scrollPane.setPreferredSize(new Dimension(480, 340));

        // Exit Button (Disabled at start)
        JButton exitButton = createSkipButton("Exit", "Click to exit the utility.");
        exitButton.setEnabled(false);
        exitButton.addActionListener(e -> System.exit(0));

        // Download Button
        JButton downloadButton = createOkButton("Download Template", "Click to download the template Excel.");
        downloadButton.addActionListener(e -> downloadExcelTemplate(exitButton));

        add(titleLabel);
        add(scrollPane);
        add(downloadButton);
        add(exitButton);
    }
}