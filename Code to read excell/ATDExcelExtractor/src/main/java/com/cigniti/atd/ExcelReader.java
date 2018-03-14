package com.cigniti.atd;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author Kumar Gaurav
 *
 */
public class ExcelReader 
{
	public static void main(String[] args) throws IOException, SQLException {
        
		//String excelFilePath = "ATD Defects set1.xlsx";
		String excelFileName = "ATD Defects set1.xlsx";
		InputStream inputStream = ExcelReader.class.getClassLoader()
                .getResourceAsStream(excelFileName); 
        XSSFWorkbook  workbook = new XSSFWorkbook(inputStream);
        XSSFSheet  firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        String[] headerCols = {"Defect ID","Project ID","Status","Severity","Project","Detected on Date","Issue Type","Priority","Actual Fix Time","Closing Date"};
        Integer[] headerType = {1,1,1,1,1,1,1,1,1,1};
        List<Integer> headerTypeList = Arrays.asList(headerType);
        
        LinkedHashMap<String, Integer> headerMap = mapHeaders(firstSheet,headerCols);
        Collection<Integer> colPosition = headerMap.values();
       
        ArrayList<Integer> colPositionList = new ArrayList<Integer>(colPosition);
        ArrayList<List<Object>> rowValueList = new ArrayList<List<Object>>(0);
        int rowNum = 0;
        
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator() ;
            ArrayList<Object> colValueList = new ArrayList<Object>(0);
            
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                
                if(colPositionList.contains(cell.getColumnIndex())){ 
	                switch (cell.getCellType()) {
	                case HSSFCell.CELL_TYPE_FORMULA:
	                	colValueList.add(cell.getCellFormula());
	                    break;
	                case HSSFCell.CELL_TYPE_NUMERIC:
						if (DateUtil.isCellDateFormatted(cell)) {
							DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
							Date date = cell.getDateCellValue();
							String actualDate = df.format(date);
							colValueList.add(actualDate);
						} else {
							
							colValueList.add(String.format("%.0f", cell.getNumericCellValue()));
						}
	                	break;
	                case HSSFCell.CELL_TYPE_STRING:
	                	colValueList.add(cell.getStringCellValue());
	                    break;
	                case HSSFCell.CELL_TYPE_BLANK:
	                	colValueList.add(HSSFCell.CELL_TYPE_BLANK);
	                    break;
	                case HSSFCell.CELL_TYPE_BOOLEAN:
	                	colValueList.add(cell.getBooleanCellValue());
	                    break;
	                case HSSFCell.CELL_TYPE_ERROR:
	                	colValueList.add(cell.getErrorCellValue());
	                    break;
	                default:
	                	colValueList.add(cell.getStringCellValue());
	                    break;
	                	}
                }
               
            }
            if(rowNum == 0){
            	
            }else{
            	rowValueList.add(colValueList);
            }
            rowNum++;
        }
        System.out.println("Row list size is:"+rowValueList.size());
        System.out.println(rowValueList);
        inputStream.close();
        
        
   
        
        
       DBStatements.getCreateTable("DefectSummary", headerMap, headerTypeList);
       System.out.println("\n Table creation completed successfully"); 
       
   //    DBStatements.insertRowInDB(rowValueList);
       System.out.println("Records are inserted in the table");
    }
	
    /**
     * This method can be used to get the header row from the excel sheet.
     * @param sheet
     * @param colsToSelect
     * @return
     */
    public static LinkedHashMap<String, Integer> mapHeaders(XSSFSheet sheet, String[] colsToSelect) {
    	LinkedHashMap<String, Integer> headerMap = new LinkedHashMap<String, Integer>();
    	XSSFRow row = sheet.getRow(0);
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();
        for (String outColumn : colsToSelect) {
        Integer icol = null;
            for (int i = firstCellNum; i < lastCellNum; i++) {
            	if (row.getCell(i).getStringCellValue().equals(outColumn)){
            		 icol = new Integer(i);
            		 break;
            	}
            }
            headerMap.put(outColumn, icol);
        }
        return headerMap;
    }
}
 

