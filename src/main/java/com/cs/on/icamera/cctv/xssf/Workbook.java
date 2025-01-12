package com.cs.on.icamera.cctv.xssf;

import com.cs.on.icamera.cctv.model.Cctv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class Workbook {
    private static final Logger logger = LogManager.getLogger(Workbook.class);

    private Workbook() {
    }

    public static void writeCctvsToExcel(File file, String sheetName, Columns[] columns, List<Cctv> cctvs) {
        logger.info("Writing file {} to sheet {} with columns {} and {} cctvs", file, sheetName, columns, cctvs.size());
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);

            int rownum = 0;
            Row headerRow = sheet.createRow(rownum);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i].colName());
            }

            rownum++;
            for (Cctv cctv : cctvs) {
                Row row = sheet.createRow(rownum);
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = cctv.getClass().getMethod(columns[i].getter()).invoke(cctv);

                    switch (value) {
                        case Number ignored -> cell.setCellValue((Double) value);
                        case Boolean ignored -> cell.setCellValue((Boolean) value);
                        case null -> cell.setCellValue("");
                        default -> cell.setCellValue(String.valueOf(value));
                    }
                }
                rownum++;
            }
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
        } catch (Exception e) {
            logger.error("Error writing cctvs to excel", e);
        }
    }


    public static List<Cctv> readCctvsFromExcel(File file, String sheetName, Columns[] columns) {
        List<Cctv> cctvs = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(file)) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int headerRowNum = 0;
            Row headerRow = sheet.getRow(headerRowNum);
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                headerIndex.put(cell.getStringCellValue(), i);
            }
            int rowStart = headerRowNum + 1;
            int rowEnd = sheet.getLastRowNum();
            for (int i = rowStart; i <= rowEnd; i++) {
                Row row = sheet.getRow(i);
                Cctv cctv = new Cctv();
                for (Columns column : columns) {
                    Cell cell = row.getCell(headerIndex.get(column.colName()));
                    Object value = getObject(cell);
                    cctv.getClass().getMethod(column.setter(), column.type()).invoke(cctv, value);
                }
                cctvs.add(cctv);
            }
        } catch (Exception e) {
            logger.error("Error reading cctvs from excel", e);
        }
        return cctvs;
    }

    private static Object getObject(Cell cell) throws IOException {
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case BOOLEAN -> cell.getBooleanCellValue();
            case BLANK -> null;
            case ERROR -> throw new IOException("Error reading cell value");
            case FORMULA -> throw new IOException("Reading a formula cell is not supported");
            default -> cell.getStringCellValue();
        };
    }
}
