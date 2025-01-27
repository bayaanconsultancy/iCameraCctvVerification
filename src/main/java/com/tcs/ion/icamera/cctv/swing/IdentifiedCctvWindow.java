package com.tcs.ion.icamera.cctv.swing;

import com.tcs.ion.icamera.cctv.data.DataStore;
import com.tcs.ion.icamera.cctv.onvif.OnvifEnquiry;
import com.tcs.ion.icamera.cctv.xssf.SheetNames;
import com.tcs.ion.icamera.cctv.xssf.TemplateColumns;
import com.tcs.ion.icamera.cctv.xssf.Workbook;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class IdentifiedCctvWindow extends SwingWindow {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public IdentifiedCctvWindow() {
        super("CCTV Authentication");
    }

    @Override
    protected void buildUiAndFunctionality() {
        // Title Label
        JLabel titleLabel = createLabel("Provide CCTV Username and Password");
        add(titleLabel);

        // Discovered Count Label and Value
        int identifiedCount = DataStore.getIdentifiedCctvCount();
        JLabel discoveredCountLabel = createLabel("Discovered CCTVs: ");
        JLabel discoveredCountValue = createLabel(String.valueOf(identifiedCount));
        add(discoveredCountLabel, discoveredCountValue);

        // Username Label and Text Field
        JLabel usernameLabel = new JLabel("ONVIF Username:");
        usernameField = createTextField(20);
        usernameField.setText("admin");
        add(usernameLabel, usernameField);

        // Password Label and Password Field
        JLabel passwordLabel = createLabel("ONVIF Password:");
        passwordField = createPasswordField(20);
        add(passwordLabel, passwordField);

        String hintText;
        if (identifiedCount > 0) {
            hintText = "Enter credentials that are valid for the majority of CCTVs.";
            discoveredCountValue.setForeground(Color.GREEN);

            JButton getDetailsButton = getGetDetailsButton(identifiedCount);
            add(getDetailsButton);
        } else {
            hintText = "Download the template and manually fill CCTV details to verify.";
            discoveredCountValue.setForeground(Color.RED);
            usernameField.setEnabled(false);
            passwordField.setEnabled(false);

            JButton downloadTemplateButton = createSkipButton("Download Template Excel", "Click to download template excel.");
            downloadTemplateButton.addActionListener(e -> downloadExcelTemplate());
            add(downloadTemplateButton);
        }

        // Hint Label
        JLabel hintLabel = createLabel(hintText);
        add(hintLabel);

        SwingUtilities.invokeLater(passwordField::requestFocusInWindow);
    }

    private void downloadExcelTemplate() {
        try {
            File file = saveExcelFile("iCamera-cctv-template");
            if (file != null) {
                Workbook.writeCctvsToExcel(file, true, SheetNames.TEMPLATE, false, TemplateColumns.values(), DataStore.getIdentifiedCctvs());
                showSuccessMessage("Excel template created successfully.");
            }
        } catch (Exception e) {
            showErrorMessage("Error creating excel template: " + e.getMessage());
        }
    }


    private JButton getGetDetailsButton(int discoveredCount) {
        JButton getDetailsButton = createOkButton("Get Details of " + discoveredCount + " CCTVs", "Click to get stream and other details for " + discoveredCount + " CCTVs.");
        getDetailsButton.addActionListener(e -> {
            disableFrame();

            JLabel waitLabel = createLabel("Please wait...");
            waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(waitLabel);

            revalidateFrame();
            repaintFrame();

            new Thread(() -> {
                DataStore.setOnvifCredential(usernameField.getText(), String.valueOf(passwordField.getPassword()));
                OnvifEnquiry.enquire();

                if (DataStore.getUnauthorizedCctvCount() > 0) {
                    next(new UsernamePasswordWindow());
                } else {
                    next(new DownloadTemplateWindow());
                }
            }).start();
        });
        return getDetailsButton;
    }

}