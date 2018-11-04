package wsq.study.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import wsq.study.common.annotation.Excel;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings({"deprecation"})
public class ExcelUtil<T> {

    //文件格式
    private String fileFormat=".xls";

    // 表格标题
    private String sheetTitle = "Sheet1";

    // 数据集
    private Collection<T> datasets;
    // 日期输出格式
    private String dateFormat = "yyyy-MM-dd";
    // 输出流
    private OutputStream out = null;

    //Excel对象,用于获取表格标题行
    private Class<T> excelClass;

    //输出
    private HttpServletResponse response;

    //文件名称
    private String fileName;

    // 图片行行高
    public static int PICLINEHEIGHT = 60;

    public ExcelUtil() {
        super();
    }

    public ExcelUtil(String sheetTitle, Collection<T> datasets, Class<T> excelClass, HttpServletResponse response, String fileName) {
        this.sheetTitle = sheetTitle;
        this.datasets = datasets;
        this.excelClass = excelClass;
        this.response = response;
        this.fileName = fileName;
    }


    /**
     * 利用JAVA的反射机制，将集合中的数据输出到指定IO流中
     *
     * 如有图片,需将图片字段（byte）的顺序与表格中的图片列顺序对应
     *
     * 用 @Excel 注解标识 标题的 名称 以及 宽度 ，不需要传入 标题名称数组
     * @Author weisq
     * @throws Exception
     */
    private void createExcel() throws Exception {
        // 声明一个工作薄
        @SuppressWarnings("resource")
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = workbook.createSheet(sheetTitle);
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 标题样式
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        // 设置水平居中
        titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 设置垂直居中
        titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 标题字体
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontName("微软雅黑");
        titleFont.setColor(HSSFColor.BLACK.index);
        //字体大小
        titleFont.setFontHeightInPoints((short) 13);
        // 把字体应用到当前的样式
        titleStyle.setFont(titleFont);

        // 正文样式
        HSSFCellStyle bodyStyle = workbook.createCellStyle();
        bodyStyle.cloneStyleFrom(titleStyle);
        // 正文字体
        HSSFFont bodyFont = workbook.createFont();
        bodyFont.setFontName("微软雅黑");
        bodyFont.setColor(HSSFColor.BLACK.index);
        bodyFont.setFontHeightInPoints((short) 12);
        bodyStyle.setFont(bodyFont);
        int index = 0;
        HSSFRow row = null;
        //反射拿到Excel类
        Field[] field_header =excelClass.getDeclaredFields();
        if(null !=field_header && field_header.length>0){
            // 产生表格标题行
            row = sheet.createRow(index++);
            // 设置行高
            row.setHeightInPoints(30f);
        }

        // 标题行数据构建
        for (int i = 0; i < field_header.length; i++) {
            Field field = field_header[i];
            field.setAccessible(true);
            Excel excel = field.getAnnotation(Excel.class);
            String columnName="";
            int width=25;
            if (excel != null )
            {
                if(excel.ignore()){
                    continue;
                }else {
                    columnName = StringUtils.isBlank(excel.name())?field.getName():excel.name();
                    width =excel.width();
                }
            }else {
                columnName=field.getName();
            }
            HSSFCell cell = row.createCell(i);
            HSSFRichTextString text = new HSSFRichTextString(columnName);
            cell.setCellValue(text);
            cell.setCellStyle(titleStyle);
            // 设置列宽
            sheet.setColumnWidth(i, width * 256);
        }

        // 遍历集合数据，产生数据行
        Iterator<T> it = datasets.iterator();
        while (it.hasNext()) {
            row = sheet.createRow(index);
            // 设置行高
            row.setHeightInPoints(25f);
            T t = (T) it.next();
            // 利用反射，得到属性值
            Field[] fields = t.getClass().getDeclaredFields();
            int column = 0;
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Excel excel = field.getAnnotation(Excel.class);
                if (excel != null && excel.ignore())
                {
                    continue;
                }
                HSSFCell cell = row.createCell(column);
                cell.setCellStyle(bodyStyle);
                Object value = field.get(t);
                // 判断值的类型后进行强制类型转换
                String textValue = "";
                if (value instanceof Date) {
                    Date date = (Date) value;
                    String df = dateFormat;
                    if (excel != null)
                    {
                        df = excel.dateFormat();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat(df);
                    textValue = sdf.format(date);
                } else if (value instanceof byte[]) {
                    // 设置图片行行高
                    row.setHeightInPoints(PICLINEHEIGHT);
                    byte[] bsValue = (byte[]) value;
                    HSSFClientAnchor anchor
                            = new HSSFClientAnchor(0, 0, 1023, 255, (short)column, index, (short)i, index);
                    anchor.setAnchorType(2);
                    patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
                } else {
                    // 其它数据类型都当作字符串简单处理
                    if (value != null)
                    {
                        textValue = value.toString();
                    }
                }
                // 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
                if (textValue != null) {
                    Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                    Matcher matcher = p.matcher(textValue);
                    if (matcher.matches()) {
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(textValue));
                    } else {
                        cell.setCellValue(textValue);
                    }
                }
                column++;
            }
            index++;
        }
        workbook.write(out);
    }


    public void ExportExcelFile() throws Exception {

        out = new BufferedOutputStream(response.getOutputStream());
        try {
            //流格式下载
            response.setContentType("application/octet-stream");
            // 清空response
            response.reset();
            if(StringUtils.isBlank(fileName)){
                fileName= RandomStringUtils.random(16)+fileFormat;
            }else if(!StringUtils.contains(fileName,fileFormat)){
                fileName=fileName.trim()+fileFormat;
            }
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
            this.createExcel();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}

