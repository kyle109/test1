package com.mtpinterface.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelUtil {

    public String read(String filepath, int sheetNum, int rowNum, int cellNum)
            throws FileNotFoundException, IOException{

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filepath));

        XSSFSheet sheet = workbook.getSheetAt(sheetNum);

        XSSFRow row = sheet.getRow(rowNum);
        //行为空处理
        if(row==null){
            return "";
        }

        XSSFCell cell = row.getCell(cellNum);
        if(cell==null){
            return "";
        }

        //Json数据均为字符型，需先将cell设置格式为String

        cell.setCellType(XSSFCell.CELL_TYPE_STRING);
        //cell.setCellType(XSSFCell.);

        String cellValue = cell.getStringCellValue();

        return cellValue;

    }

    public void write(String filepath, int sheetNum, int rowNum, int cellNum, String value){

        try {

            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filepath));

            XSSFSheet sheet = workbook.getSheetAt(sheetNum);

            XSSFRow row = sheet.getRow(rowNum);

            XSSFCell cell = row.createCell(cellNum);

            cell.setCellValue(value);



            FileOutputStream out = new FileOutputStream(filepath);

            workbook.write(out);

            out.close();

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
            // TODO: handle exception
        }
    }
}
