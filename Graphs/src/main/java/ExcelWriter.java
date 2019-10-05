import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelWriter {

    private static String[] columns = {"Client1", "Client2", "Client3"};
    private List<Entry> entries =  new ArrayList<>();
    private LogReader logReader = new LogReader();

    public void writeToExcel(String folder, int clients, String mode, String algorithm) throws IOException {
        // Create a Workbook
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
        CreationHelper createHelper = workbook.getCreationHelper();

        // Create a Sheet
        Entry client1 = logReader.readEntry(folder + "/"+ mode + algorithm+ "_c1.log", "Client1");
        Entry client2 = logReader.readEntry(folder + "/"+ mode + algorithm+ "_c2.log", "Client2");
        Entry client3 = logReader.readEntry(folder + "/"+ mode + algorithm+ "_c3.log", "Client3");

        entries.add(client1);
        entries.add(client2);
        entries.add(client3);

        Sheet sheet = workbook.createSheet("Employee");

        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.RED.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        // Create Other rows and cells with employees data
        int rowNum = 1;
        int counter = 0;
        for(Integer bitrate : entries.get(0).getPlayingBitrate()) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(entries.get(0).getPlayingBitrate().get(counter));
            row.createCell(1).setCellValue(entries.get(1).getPlayingBitrate().get(counter));
            row.createCell(2).setCellValue(entries.get(2).getPlayingBitrate().get(counter));

            counter++;
        }

		// Resize all columns to fit the content size
        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream("poi-generated-file.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        // Closing the workbook
        workbook.close();
    }
}