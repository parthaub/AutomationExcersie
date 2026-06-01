package com.automationexercise.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * ExcelUtils.java
 *
 * PURPOSE:
 *   Reads test data from Excel (.xlsx) files.
 *   This enables DATA-DRIVEN TESTING: one test can run with many
 *   different inputs by reading rows from an Excel spreadsheet.
 *
 * APACHE POI CLASSES:
 *   XSSFWorkbook = represents the entire Excel file
 *   XSSFSheet    = one sheet/tab inside the workbook
 *   Row          = one row of data
 *   Cell         = one cell (intersection of row + column)
 *
 * EXAMPLE USAGE:
 *   ExcelUtils excel = new ExcelUtils("src/test/resources/testdata/TestData.xlsx", "LoginData");
 *   String email = excel.getCellData(1, 0);    // Row 1, Column 0
 *   String pass  = excel.getCellData(1, 1);    // Row 1, Column 1
 *   excel.close();
 */
public class ExcelUtils {

    private XSSFWorkbook workbook;    // The entire Excel file
    private XSSFSheet    sheet;       // The specific sheet we're reading
    private String       filePath;    // Used for error messages

    /**
     * Constructor: opens the Excel file and selects the given sheet.
     *
     * @param filePath   path to the .xlsx file (e.g., "src/test/resources/testdata/TestData.xlsx")
     * @param sheetName  name of the sheet tab to read (e.g., "LoginData")
     */
    public ExcelUtils(String filePath, String sheetName) {
        this.filePath = filePath;
        try {
            // FileInputStream opens the file for reading
            FileInputStream fis = new FileInputStream(filePath);

            // XSSFWorkbook reads the Excel file into memory
            workbook = new XSSFWorkbook(fis);

            // Get the specific sheet by name
            sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException(
                    "[ExcelUtils] Sheet '" + sheetName + "' not found in " + filePath
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("[ExcelUtils] Cannot open file: " + filePath + " — " + e.getMessage());
        }
    }

    /**
     * Returns the total number of rows in the sheet (including header row).
     * Use this to know how many data rows to loop through.
     *
     * @return total row count
     */
    public int getRowCount() {
        // getPhysicalNumberOfRows() counts rows that actually have data
        return sheet.getPhysicalNumberOfRows();
    }

    /**
     * Gets the value of a specific cell as a String.
     *
     * Row 0 = the HEADER row (column names like "email", "password")
     * Row 1 = first data row
     *
     * @param rowNum  row index (0-based: 0 = header, 1 = first data row)
     * @param colNum  column index (0-based: 0 = first column, 1 = second column)
     * @return        cell value as String, or empty string if cell is empty
     */
    public String getCellData(int rowNum, int colNum) {
        // Get the row
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";  // Row doesn't exist → return empty string

        // Get the cell from that row
        Cell cell = row.getCell(colNum);
        if (cell == null) return "";  // Cell doesn't exist → return empty string

        // Convert cell value to String based on its type
        // (Excel cells can be text, number, boolean, formula, or blank)
        switch (cell.getCellType()) {
            case STRING:
                // Text cell: return the string value directly
                return cell.getStringCellValue().trim();

            case NUMERIC:
                // Number cell: convert to long (removes decimal) then to String
                // This handles cases like "1234567890" stored as a number in Excel
                return String.valueOf((long) cell.getNumericCellValue());

            case BOOLEAN:
                // Boolean cell: return "true" or "false"
                return String.valueOf(cell.getBooleanCellValue());

            case BLANK:
                return "";

            default:
                return "";
        }
    }

    /**
     * Gets a cell value by column HEADER NAME instead of column number.
     * More readable than using column numbers.
     *
     * EXAMPLE: getCellData(1, "email") instead of getCellData(1, 0)
     *
     * @param rowNum      the data row (1-based: 1 = first data row)
     * @param columnName  the header text in row 0
     * @return            cell value as String
     */
    public String getCellData(int rowNum, String columnName) {
        // First, find which column number has this header name
        Row headerRow = sheet.getRow(0);  // Row 0 is always the header

        int colNum = -1;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            // Compare cell text to the column name we're looking for
            if (headerRow.getCell(i).getStringCellValue().equalsIgnoreCase(columnName)) {
                colNum = i;
                break;
            }
        }

        if (colNum == -1) {
            throw new RuntimeException("[ExcelUtils] Column '" + columnName + "' not found in header row.");
        }

        // Now use the numeric version to get the data
        return getCellData(rowNum, colNum);
    }

    /**
     * Closes the workbook to release the file lock.
     * ALWAYS call this when you're done reading, especially in @AfterClass.
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            System.err.println("[ExcelUtils] Warning: could not close workbook — " + e.getMessage());
        }
    }
}
