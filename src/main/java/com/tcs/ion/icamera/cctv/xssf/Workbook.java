package com.tcs.ion.icamera.cctv.xssf;

import com.tcs.ion.icamera.cctv.error.VerificationException;
import com.tcs.ion.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbook {
    private static final Logger logger = LogManager.getLogger(Workbook.class);

    private Workbook() {
    }

    // Write CCTVs to Excel File
    public static void writeCctvsToExcel(File file, boolean replace, String sheetName, boolean locked, Columns[] columns, List<Cctv> cctvs) throws VerificationException {
        logger.info("Writing file {} to sheet {} with {} columns and {} cctvs", file, sheetName, columns.length, cctvs.size());

        try (XSSFWorkbook workbook = createOrOpenWorkbook(file, replace)) {
            XSSFSheet sheet = createOrReplaceSheet(workbook, sheetName);

            writeHeaders(sheet, columns);
            writeRows(sheet, columns, cctvs);

            if (locked) {
                sheet.protectSheet(generatePassword());
            }

            saveWorkbook(workbook, file);
        } catch (Exception e) {
            logger.error("Error writing CCTVs to Excel", e);
            throw new VerificationException(e);
        }
    }

    // Creates or opens a workbook
    private static XSSFWorkbook createOrOpenWorkbook(File file, boolean replace) throws IOException, InvalidFormatException {
        return (file.exists() && !replace) ? new XSSFWorkbook(file) : new XSSFWorkbook();
    }

    // Creates or replaces a sheet in a workbook
    private static XSSFSheet createOrReplaceSheet(XSSFWorkbook workbook, String sheetName) {
        if (workbook.getSheet(sheetName) != null) {
            workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
        }
        return workbook.createSheet(sheetName);
    }

    // Writes headers to the Excel sheet
    private static void writeHeaders(XSSFSheet sheet, Columns[] columns) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            headerRow.createCell(i).setCellValue(columns[i].colName());
        }
    }

    // Writes data rows to the Excel sheet
    private static void writeRows(XSSFSheet sheet, Columns[] columns, List<Cctv> cctvs) throws ReflectiveOperationException {
        for (int rowIndex = 1; rowIndex <= cctvs.size(); rowIndex++) {
            Cctv cctv = cctvs.get(rowIndex - 1);
            Row row = sheet.createRow(rowIndex);
            for (int colIndex = 0; colIndex < columns.length; colIndex++) {
                Cell cell = row.createCell(colIndex);
                Object value = cctv.getClass().getMethod(columns[colIndex].getter()).invoke(cctv);
                setValueToCell(cell, value);
            }
        }
    }

    // Saves the workbook to the file
    private static void saveWorkbook(XSSFWorkbook workbook, File file) throws IOException, VerificationException {
        if (file.exists() && !file.delete()) {
            throw new VerificationException("Unable to replace file: " + file);
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
        }
    }

    // Generalized method to set cell values
    private static void setValueToCell(Cell cell, Object value) {
        switch (value) {
            case null -> cell.setCellValue("");
            case Integer ignored -> cell.setCellValue((Integer) value);
            case Number ignored -> cell.setCellValue((Double) value);
            case Boolean ignored -> cell.setCellValue((Boolean) value);
            default -> cell.setCellValue(String.valueOf(value));
        }
    }

    // Read CCTVs from Excel File
    public static List<Cctv> readCctvsFromExcel(File file, String sheetName, Columns[] columns) throws VerificationException {
        List<Cctv> cctvs = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheet(sheetName);

            Map<String, Integer> headerIndex = buildHeaderIndex(sheet, columns);
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cctv cctv = parseRowIntoCctv(row, columns, headerIndex);
                    cctvs.add(cctv);
                }
            }
        } catch (Exception e) {
            logger.error("Error reading CCTVs from Excel", e);
            throw new VerificationException(e);
        }
        return cctvs;
    }

    // Builds a map of header indices
    private static Map<String, Integer> buildHeaderIndex(XSSFSheet sheet, Columns[] columns) throws VerificationException {
        Map<String, Integer> headerIndex = new HashMap<>();
        Row headerRow = sheet.getRow(0);

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            String header = headerRow.getCell(i).getStringCellValue();
            headerIndex.put(header, i);
        }

        for (Columns column : columns) {
            if (!headerIndex.containsKey(column.colName())) {
                throw new VerificationException("Column '" + column.colName() + "' is missing in the Excel file.");
            }
        }

        return headerIndex;
    }

    // Parses a row into a Cctv object
    private static Cctv parseRowIntoCctv(Row row, Columns[] columns, Map<String, Integer> headerIndex)
            throws ReflectiveOperationException, IOException {
        Cctv cctv = new Cctv();
        for (Columns column : columns) {
            Cell cell = row.getCell(headerIndex.get(column.colName()));
            Object value = parseCellValue(cell, column, column.type());
            cctv.getClass().getMethod(column.setter(), column.type()).invoke(cctv, value);
        }
        return cctv;
    }

    // Parses the value of a cell based on type
    private static Object parseCellValue(Cell cell, Columns column, Class<?> type) throws IOException {
        Object object = getCellValue(cell);
        String typeName = type.getSimpleName();
        logger.info("Parsing value '{}' as type '{}' for column '{}'.", object, typeName, column.colName());

        return object == null ? null : switch (typeName) {
            case "String" -> object.toString();
            case "Integer" -> castToNumber(column, object, Integer.class, Number::intValue);
            case "Double" -> castToNumber(column, object, Double.class, Number::doubleValue);
            case "Boolean" -> handleBooleanCasting(column, object);
            default -> throw new IllegalArgumentException("Unsupported type: " + typeName);
        };
    }

    private static <T> T castToNumber(Columns column, Object object, Class<T> type, java.util.function.Function<Number, T> converter) {
        if (object instanceof Number number) {
            return converter.apply(number);
        }
        throw new IllegalArgumentException(object + " cannot be cast to " + type.getSimpleName() + " for column " + column.colName());
    }

    private static Boolean handleBooleanCasting(Columns column, Object object) {
        if (object instanceof Boolean bool) {
            return bool;
        } else if (object instanceof String string) {
            String value = string.trim().toLowerCase();
            // Simplified checks using a map
            return switch (value) {
                case "y", "yes", "true", "t", "1" -> true;
                case "n", "no", "false", "f", "0" -> false;
                default -> null;
            };
        } else if (object instanceof Number number) {
            // Numeric to Boolean mapping
            return number.intValue() == 1 ? true : number.intValue() == 0 ? false : null;
        }
        throw new IllegalArgumentException(object + " cannot be cast to Boolean for column " + column.colName());
    }

    private static Object getCellValue(Cell cell) throws IOException {
        return cell == null ? null : switch (cell.getCellType()) {
            case BLANK -> null;
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case ERROR -> throw new IOException("Error reading cell value");
            case FORMULA -> throw new UnsupportedOperationException("Formula cells are not supported");
            default -> throw new IllegalArgumentException("Unsupported cell type: " + cell.getCellType());
        };
    }

    // Password generator for locking sheets
    private static String generatePassword() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "!Camera" + now.getMonthValue();
    }
}