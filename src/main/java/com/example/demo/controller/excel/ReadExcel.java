package com.example.demo.controller.excel;

import com.cputech.modules.usermsg.repository.SysUserRepository;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyanqi
 * @since 1.0 2017/12/4
 */
public class ReadExcel {
    @Autowired
    SysUserRepository sysUserRepository;

    private final static String excel2003L = ".xls";    //2003- 版本的excel
    private final static String excel2007U = ".xlsx";   //2007+ 版本的excel

    public static List<List<Object>> getBankListByExcel(InputStream in, String fileName) throws Exception {
        List<List<Object>> list = null;

        //创建Excel工作薄
        Workbook work = getWorkbook(in, fileName);
        if (null == work) {
            throw new Exception("创建Excel工作薄为空！");
        }
        Sheet sheet = null;
        Row row = null;
        Cell cell = null;


        list = new ArrayList<List<Object>>();
        //遍历Excel中所有的sheet
        for (int i = 0; i < work.getNumberOfSheets(); i++) {
            sheet = work.getSheetAt(i);
            if (sheet == null) {
                continue;
            }

            //遍历当前sheet中的所有行
            for (int j = sheet.getFirstRowNum(); j < sheet.getLastRowNum(); j++) {
                row = sheet.getRow(j);
                if (row == null || row.getFirstCellNum() == j) {
                    continue;
                }

                //遍历所有的列
                List<Object> li = new ArrayList<Object>();
                for (int y = row.getFirstCellNum(); y < row.getLastCellNum(); y++) {
                    cell = row.getCell(y);
                    li.add(getCellValue(cell));
                }
                list.add(li);
            }
        }
        work.close();
        return list;
    }

    /**
     * 描述：根据文件后缀，自适应上传文件的版本
     *
     * @param inStr,fileName
     * @return
     * @throws Exception
     */
    public static Workbook getWorkbook(InputStream inStr, String fileName) throws Exception {
        Workbook wb = null;
        String fileType = fileName.substring(fileName.lastIndexOf("."));
        if (excel2003L.equals(fileType)) {
            wb = new HSSFWorkbook(inStr);  //2003-
        } else if (excel2007U.equals(fileType)) {
            wb = new XSSFWorkbook(inStr);  //2007+
        } else {
            throw new Exception("解析的文件格式有误！");
        }
        return wb;
    }

    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell 每个表格
     * @return 表格内容
     */
    public static Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");  //日期格式化
        DecimalFormat df2 = new DecimalFormat("0.00");  //格式化数字

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                String string = cell.getRichStringCellValue().getString();
                if (string != null) {
                    value = string.trim();
                } else {
                    value = "";
                }
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    String s = df.format(cell.getNumericCellValue());
                    if (s != null) {
                        value = s.trim();
                    } else {
                        value = "";
                    }
                } else if ("m/d/yy".equals(cell.getCellStyle().getDataFormatString())) {
                    String s2 = sdf.format(cell.getDateCellValue());
                    if (s2 != null) {
                        value = s2.trim();
                    } else {
                        value = "";
                    }
                } else {
                    String s3 = df2.format(cell.getNumericCellValue());
                    if (s3 != null) {
                        value = s3.trim();
                    } else {
                        value = "";
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }

    public static void readExcel(String path,String fileName) throws Exception {

        File file = new File(path);
        FileInputStream in = new FileInputStream(file);
        List<List<Object>> bankListByExcel = getBankListByExcel(in,fileName);
        in.close();


        for (int i = 0; i < bankListByExcel.size(); i++) {
            List<Object> lo = bankListByExcel.get(i);
            System.out.println("联系人:"+lo.get(2).toString().trim()+"电话:"+lo.get(3).toString().trim());

        }


    }

    public static void main(String[] args) throws Exception {
        readExcel("/Users/huang/Desktop/上传名录.xlsx","上传名录.xlsx");
    }
}
