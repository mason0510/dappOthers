/**
 * Yztz.com Inc.
 * Copyright (c) 2013-2015 All Rights Reserved.
 */
package com.happy.otc.bifutures.utill;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author hao
 * @version $Id: XLSUtils.java, v 0.1 Mar 26, 2015 1:24:10 PM hao Exp $
 */
public class ExcelUtil {

    /***
     * 读取单元格的值
     *
     * @param cell
     * @return
     * @Title: getCellValue
     * @Date : 2014-9-11 上午10:52:07
     */
    public static String getCellValue(Cell cell) {
        Object result = "";
        if (cell != null) {
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                result = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                result = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                result = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                result = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_ERROR:
                result = cell.getErrorCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                break;
            default:
                break;
            }
        }
        return result.toString();
    }

    public static List<Row> readExcel_xlsx(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("文件不存在！");
        }

        XSSFWorkbook wb = null;
        List<Row> rowList = new ArrayList<Row>();
        // 去读Excel  
        wb = new XSSFWorkbook(inputStream);

        // 读取Excel 2007版，xlsx格式  
        rowList = readExcel(wb);

        return rowList;
    }

    /**
     * //读取Excel 2007版，xlsx格式
     *
     * @return
     * @throws Exception
     * @Title: readExcel_xlsx
     * @Date : 2014-9-11 上午11:43:11
     */
    public static List<Row> readExcel_xlsx(String xlsPath) throws IOException {
        // 判断文件是否存在  
        File file = new File(xlsPath);
        if (!file.exists()) {
            throw new IOException("文件名为" + file.getName() + "Excel文件不存在！");
        }
        System.out.println("ssss");
        XSSFWorkbook wb = null;
        List<Row> rowList = new ArrayList<Row>();
        FileInputStream fis = new FileInputStream(file);
        // 去读Excel  
        wb = new XSSFWorkbook(fis);

        // 读取Excel 2007版，xlsx格式  
        rowList = readExcel(wb);

        return rowList;
    }

    /**
     * inputstream
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static List<Row> readExcel_xls(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IOException("文件名不存在！");
        }
        HSSFWorkbook wb = null;// 用于Workbook级的操作，创建、删除Excel  
        List<Row> rowList = new ArrayList<Row>();

        // 读取Excel  
        wb = new HSSFWorkbook(inputStream);

        // 读取Excel 97-03版，xls格式  
        rowList = readExcel(wb);

        return rowList;
    }

    /***
     * 读取Excel(97-03版，xls格式)
     *
     * @throws Exception
     * @Title: readExcel
     * @Date : 2014-9-11 上午09:53:21
     */
    public static List<Row> readExcel_xls(String xlsPath) throws IOException {

        // 判断文件是否存在  
        File file = new File(xlsPath);
        if (!file.exists()) {
            throw new IOException("文件名为" + file.getName() + "Excel文件不存在！");
        }

        HSSFWorkbook wb = null;// 用于Workbook级的操作，创建、删除Excel  
        List<Row> rowList = new ArrayList<Row>();

        // 读取Excel  
        wb = new HSSFWorkbook(new FileInputStream(file));

        // 读取Excel 97-03版，xls格式  
        rowList = readExcel(wb);

        return rowList;
    }

    /**
     * 通用读取Excel
     *
     * @param wb
     * @return
     * @Title: readExcel
     * @Date : 2014-9-11 上午11:26:53
     */
    private static List<Row> readExcel(Workbook wb) {
        List<Row> rowList = new ArrayList<Row>();
        Sheet sheet = null;
        int sheetCount = wb.getNumberOfSheets();//获取可以操作的总数量  

        // 获取sheet数目  
        for (int t = 0; t < sheetCount; t++) {
            // 获取设定操作的sheet  
            sheet = wb.getSheetAt(t);

            //获取最后行号  
            int lastRowNum = sheet.getLastRowNum();

            Row row = null;
            // 循环读取  
            for (int i = 0; i <= lastRowNum; i++) {
                row = sheet.getRow(i);
                if (row != null) {
                    rowList.add(row);

                }
            }
        }
        return rowList;
    }


    /**
     * 1.创建 workbook
     * 
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook() {
        return new HSSFWorkbook();
    }
    
    /**
     * 1.创建x workbook
     * 
     * @return
     */
    public static XSSFWorkbook getXSSFWorkbook() {
        return new XSSFWorkbook();
    }

    /**
     * 2.创建 sheet
     * 
     * @param hssfWorkbook
     * @param sheetName sheet 名称
     * @return
     */
    public static HSSFSheet getHSSFSheet(HSSFWorkbook hssfWorkbook, String sheetName) {
        return hssfWorkbook.createSheet(sheetName);
    }
    
    /**
     * 2.创建 sheet
     * 
     * @param
     * @param sheetName sheet 名称
     * @return
     */
    public static XSSFSheet getXSSFSheet(XSSFWorkbook xssfWorkbook, String sheetName) {
        return xssfWorkbook.createSheet(sheetName);
    }

    /**
     * 3.写入表头信息
     * 
     * @param hssfWorkbook
     * @param hssfSheet
     * @param headInfoList List<Map<String, Object>> key: title 列标题 columnWidth 列宽 dataKey 列对应的
     *        dataList item key
     */
    @SuppressWarnings("static-access")
    public void writeHeader(HSSFWorkbook hssfWorkbook, HSSFSheet hssfSheet, List<Map<String, Object>> headInfoList) {
        HSSFCellStyle cs = hssfWorkbook.createCellStyle();
        HSSFFont font = hssfWorkbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(font.BOLDWEIGHT_BOLD);
        cs.setFont(font);
        cs.setAlignment(cs.ALIGN_CENTER);

        HSSFRow r = hssfSheet.createRow(0);
        r.setHeight((short) 380);
        HSSFCell c = null;
        Map<String, Object> headInfo = null;
        // 处理excel表头
        for (int i = 0, len = headInfoList.size(); i < len; i++) {
            headInfo = headInfoList.get(i);
            c = r.createCell(i);
            c.setCellValue(headInfo.get("title").toString());
            c.setCellStyle(cs);
            if (headInfo.containsKey("columnWidth")) {
                hssfSheet.setColumnWidth(i, (short) (((Integer) headInfo.get("columnWidth") * 8) / ((double) 1 / 20)));
            }
        }
    }

    /**
     * 4.写入内容部分
     * 
     * @param hssfWorkbook
     * @param hssfSheet
     * @param startIndex 从1开始，多次调用需要加上前一次的dataList.size()
     * @param headInfoList List<Map<String, Object>> key: title 列标题 columnWidth 列宽 dataKey 列对应的
     *        dataList item key
     * @param dataList
     */
    public void writeContent(HSSFSheet hssfSheet, int startIndex, List<Map<String, Object>> headInfoList,
            List<Map<String, Object>> dataList) {
        Map<String, Object> headInfo = null;
        HSSFRow r = null;
        HSSFCell c = null;
        // 处理数据
        Map<String, Object> dataItem = null;
        Object v = null;
        for (int i = 0, rownum = startIndex, len = (startIndex + dataList.size()); rownum < len; i++, rownum++) {
            r = hssfSheet.createRow(rownum);
            r.setHeightInPoints(16);
            dataItem = dataList.get(i);
            for (int j = 0, jlen = headInfoList.size(); j < jlen; j++) {
                headInfo = headInfoList.get(j);
                c = r.createCell(j);
                v = dataItem.get(headInfo.get("dataKey").toString());

                if (v instanceof String) {
                    c.setCellValue((String) v);
                } else if (v instanceof Boolean) {
                    c.setCellValue((Boolean) v);
                } else if (v instanceof Calendar) {
                    c.setCellValue((Calendar) v);
                } else if (v instanceof Double) {
                    c.setCellValue((Double) v);
                } else if (v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Float) {
                    c.setCellValue(Double.parseDouble(v.toString()));
                } else if (v instanceof HSSFRichTextString) {
                    c.setCellValue((HSSFRichTextString) v);
                } else {
                    c.setCellValue(v.toString());
                }
            }
        }
    }

    public static void writeContent2(HSSFSheet hssfSheet, int startIndex, List<List<Object>> dataList) {
        HSSFRow r = null;
        HSSFCell c = null;
        // 处理数据
        List<Object> dataItem = null;
        Object v = null;
        for (int i = 0, rownum = startIndex, len = (startIndex + dataList.size()); rownum < len; i++, rownum++) {
            r = hssfSheet.createRow(rownum);
            r.setHeightInPoints(16);
            dataItem = dataList.get(i);
            for (int j = 0, jlen = dataItem.size(); j < jlen; j++) {
                c = r.createCell(j);
                v = dataItem.get(j);

                if (v instanceof String) {
                    c.setCellValue((String) v);
                } else if (v instanceof Boolean) {
                    c.setCellValue((Boolean) v);
                } else if (v instanceof Calendar) {
                    c.setCellValue((Calendar) v);
                } else if (v instanceof Double) {
                    c.setCellValue((Double) v);
                } else if (v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Float) {
                    c.setCellValue(Double.parseDouble(v.toString()));
                } else if (v instanceof HSSFRichTextString) {
                    c.setCellValue((HSSFRichTextString) v);
                } else {
                    c.setCellValue(v.toString());
                }
            }
        }
    }

    public static void writeContent3(Sheet sheet, int startIndex, List<Object> dataList) {
        Row r = null;
        Cell c = null;
        // 处理数据
        Object v = null;
        r = sheet.createRow(startIndex);
        r.setHeightInPoints(16);
        for (int j = 0, jlen = dataList.size(); j < jlen; j++) {
            c = r.createCell(j);
            v = dataList.get(j);

            if (v instanceof String) {
                c.setCellValue((String) v);
            } else if (v instanceof Boolean) {
                c.setCellValue((Boolean) v);
            } else if (v instanceof Calendar) {
                c.setCellValue((Calendar) v);
            } else if (v instanceof Double) {
                c.setCellValue((Double) v);
            } else if (v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Float) {
                c.setCellValue(Double.parseDouble(v.toString()));
            } else if (v instanceof HSSFRichTextString) {
                c.setCellValue((HSSFRichTextString) v);
            } else if (v != null) {
                c.setCellValue(v.toString());
            }
        }
    }
    
    public static void write2FilePath(HSSFWorkbook hssfWorkbook, String filePath) throws IOException {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOut);
        } finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
    }

    public static void write2OutputStream(Workbook workbook, OutputStream fileOut) throws IOException {
         workbook.write(fileOut);
    }

    /**
     * 导出excel code example: List<Map<String, Object>> headInfoList = new
     * ArrayList<Map<String,Object>>(); Map<String, Object> itemMap = new HashMap<String, Object>();
     * itemMap.put("title", "序号1"); itemMap.put("columnWidth", 25); itemMap.put("dataKey", "XH1");
     * headInfoList.add(itemMap);
     * 
     * itemMap = new HashMap<String, Object>(); itemMap.put("title", "序号2");
     * itemMap.put("columnWidth", 50); itemMap.put("dataKey", "XH2"); headInfoList.add(itemMap);
     * 
     * itemMap = new HashMap<String, Object>(); itemMap.put("title", "序号3");
     * itemMap.put("columnWidth", 25); itemMap.put("dataKey", "XH3"); headInfoList.add(itemMap);
     * 
     * List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>(); Map<String, Object>
     * dataItem = null; for(int i=0; i < 100; i++){ dataItem = new HashMap<String, Object>();
     * dataItem.put("XH1", "data" + i); dataItem.put("XH2", 88888888f); dataItem.put("XH3",
     * "脉兜V5.."); dataList.add(dataItem); }
     * POIUtil.exportExcel2FilePath("test sheet 1","F:\\temp\\customer2.xls", headInfoList,
     * dataList);
     * 
     * @param sheetName sheet名称
     * @param filePath 文件存储路径， 如：f:/a.xls
     * @param headInfoList List<Map<String, Object>> key: title 列标题 columnWidth 列宽 dataKey 列对应的
     *        dataList item key
     * @param dataList List<Map<String, Object>> 导出的数据
     * @throws java.io.IOException
     * 
     */
    @SuppressWarnings("static-access")
    public static void exportExcel2FilePath(String sheetName, String filePath, List<Map<String, Object>> headInfoList,
                                            List<Map<String, Object>> dataList) throws IOException {
        ExcelUtil poiUtil = new ExcelUtil();
        // 1.创建 Workbook
        HSSFWorkbook hssfWorkbook = poiUtil.getHSSFWorkbook();
        // 2.创建 Sheet
        HSSFSheet hssfSheet = poiUtil.getHSSFSheet(hssfWorkbook, sheetName);
        // 3.写入 head
        poiUtil.writeHeader(hssfWorkbook, hssfSheet, headInfoList);
        // 4.写入内容
        poiUtil.writeContent(hssfSheet, 1, headInfoList, dataList);
        // 5.保存文件到filePath中
        poiUtil.write2FilePath(hssfWorkbook, filePath);
    }

}
