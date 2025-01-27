package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.model.Cctv;
import com.tcs.ion.icamera.cctv.xssf.ExportColumns;
import com.tcs.ion.icamera.cctv.xssf.SheetNames;
import com.tcs.ion.icamera.cctv.xssf.TemplateColumns;
import com.tcs.ion.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class ExportExcelWindow extends SwingWindow {

    private final List<Cctv> allCctvs;
    private final List<Cctv> okCctvs;

    public ExportExcelWindow() {
        super("Export CCTV Details");

        this.allCctvs = DataStore.getCctvsToVerify();
        this.okCctvs = DataStore.getResources();
    }


    private JTextArea getTextArea() {
        int errorCount = allCctvs.size() - okCctvs.size();
        JTextArea messageArea = createTextArea(6, 1);
        messageArea.setText("""
                %s encountered while checking the RTSP feeds of CCTV Cameras.
                
                1. Please download the CCTV details by 'Download CCTV Templates' button,
                2. Fill necessary details like CCTV Name, Inside Room, etc. in the 'CCTVs' sheet,
                3. Correct any incorrect information,
                4. Then re-verify the updated CCTV details template using this utility.
                """.formatted(errorCount == 0 ? "No errors" : errorCount + " errors"));

        messageArea.setEditable(false);
        return messageArea;
    }


    private void downloadExcelTemplate(JButton exitButton) {
        try {
            File file = saveExcelFile("iCamera-cctv-details");
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), allCctvs);
                Workbook.writeCctvsToExcel(file, false, SheetNames.EXPORT, true, ExportColumns.values(), okCctvs);
                showSuccessMessage("iCamera CCTV details Excel downloaded successfully.");
                exitButton.setEnabled(true);
            }
        } catch (Exception e) {
            showErrorMessage("Error creating excel: " + (e.getCause() == null ? e.getMessage() : e.getCause().getMessage()));
        }
    }

    @Override
    protected void buildUiAndFunctionality() {
        // Title Label
        JLabel titleLabel = createLabel("CCTV Export Utility");
        add(titleLabel);

        // Instructions Text Area
        JTextArea messageArea = getTextArea();
        JScrollPane scrollPane = createScrollPanel(messageArea);
        add(scrollPane);

        // Download Button
        JButton downloadButton = createOkButton("Download CCTV Template", "Click to download the CCTV template Excel.");

        // Exit Button
        JButton exitButton = createSkipButton("Exit", "Click to exit.");
        exitButton.setEnabled(false); // Initially disabled

        downloadButton.addActionListener(e -> downloadExcelTemplate(exitButton));
        exitButton.addActionListener(e -> System.exit(0));

        add(downloadButton);
        add(exitButton);
    }
}