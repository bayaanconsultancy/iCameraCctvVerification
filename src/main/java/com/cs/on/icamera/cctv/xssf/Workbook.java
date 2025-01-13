package com.cs.on.icamera.cctv.xssf;

import com.cs.on.icamera.cctv.error.VerificationException;
import com.cs.on.icamera.cctv.model.Cctv;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workbook {
	private static final Logger logger = LogManager.getLogger(Workbook.class);

	private Workbook() {
	}

	public static void writeCctvsToExcel(File file, boolean replace, String sheetName, boolean locked,
			Columns[] columns, List<Cctv> cctvs) throws VerificationException {
		logger.info("Writing file {} to sheet {} with columns {} and {} cctvs", file, sheetName, columns, cctvs.size());

		try (XSSFWorkbook workbook = createOrOpenWorkbook(file, replace)) {
			XSSFSheet sheet = createOrReplaceSheet(workbook, sheetName);

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
					case Integer ignored -> cell.setCellValue((Integer) value);
					case Number ignored -> cell.setCellValue((Double) value);
					case Boolean ignored -> cell.setCellValue((Boolean) value);
					case null -> cell.setCellValue("");
					default -> cell.setCellValue(String.valueOf(value));
					}
				}
				rownum++;
			}

			if (locked)
				sheet.protectSheet("!Camera");

			if (file.exists() && !file.delete())
				throw new VerificationException("Unable to replace file: " + file);
			try (FileOutputStream outputStream = new FileOutputStream(file)) {
				workbook.write(outputStream);
			}
		} catch (Exception e) {
			logger.error("Error writing cctvs to excel", e);
			throw new VerificationException(e);
		}
	}

	private static XSSFWorkbook createOrOpenWorkbook(File file, boolean replace)
			throws IOException, InvalidFormatException {
		return file.exists() && !replace ? new XSSFWorkbook(file) : new XSSFWorkbook();
	}

	private static XSSFSheet createOrReplaceSheet(XSSFWorkbook workbook, String sheetName) {
		if (workbook.getSheet(sheetName) != null)
			workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
		return workbook.createSheet(sheetName);
	}

	public static List<Cctv> readCctvsFromExcel(File file, String sheetName, Columns[] columns)
			throws VerificationException {
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
			throw new VerificationException(e);
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
