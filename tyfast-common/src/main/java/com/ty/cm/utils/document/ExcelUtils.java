package com.ty.cm.utils.document;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ty.cm.utils.lock.KeyLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.jxls.transform.poi.PoiTransformer.POI_CONTEXT_KEY;

/**
 * Excel读写工具类
 *
 * @Author Tommy
 * @Date 2022/12/7
 */
@Slf4j
public class ExcelUtils {

    /** Key锁 **/
    private static final KeyLock KEY_LOCK;

    /** 数字格式化类，防止长数字成为科学计数法形式，或者int变为double形式 **/
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("0.0");

    /** 日期格式化类 **/
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /** 支持的Excel类型 **/
    public static final String SUPPORTED_TYPES = "xls|xlsx";

    /** 标题 **/
    public static final String COLS_HEADING = "colsHeading";

    /** 表头Key **/
    public static final String COLS_TITLE = "colsTitle";

    static {
        KEY_LOCK = new KeyLock();
    }

    /**
     * 读取整个Excel数据
     *
     * @param filePath  Excel文件路径
     * @return Map<String, List<List<String>>> 每个Sheet即为Map的K,V
     */
    public static Map<String, List<List<String>>> readExcel(String filePath) throws Exception {
        Map<String, List<List<String>>> dataMap = Maps.newHashMap();
        File file = getFile(filePath);
        if (null != file) {
            try (FileInputStream stream = new FileInputStream(file); Workbook book = getWorkbook(stream, file)) {
                // 读取每个Sheet数据
                int scount = getSheetCount(book);
                for (int i = 0; i < scount; i++) {
                    Sheet sheet = book.getSheetAt(i);
                    dataMap.put(StringUtils.trim(sheet.getSheetName()), readSheet(sheet, 0));
                }
            }
        }
        return dataMap;
    }

    /**
     * 读取Excel Sheet数据
     *
     * @param filePath      Excel文件路径
     * @param sheetIndex    要读取的Sheet的索引
     * @return List<List<String>>
     */
    public static List<List<String>> readSheet(String filePath, int sheetIndex) throws Exception {
        return readSheet(filePath, sheetIndex, 0);
    }

    /**
     * 读取Excel Sheet数据
     *
     * @param filePath      Excel文件路径
     * @param sheetIndex    要读取的Sheet的索引
     * @param startRowIndex 开始行索引，即从哪行开始读取
     * @return List<List<String>>
     */
    public static List<List<String>> readSheet(String filePath, int sheetIndex, int startRowIndex) throws Exception {
        List<List<String>> sheetDataList = Lists.newArrayList();
        File file = getFile(filePath);
        if (null != file) {
            try (FileInputStream stream = new FileInputStream(file); Workbook book = getWorkbook(stream, file)) {
                sheetDataList = readSheet(book, sheetIndex, startRowIndex);
            }
        }
        return sheetDataList;
    }

    /**
     * 读取Excel Sheet数据
     *
     * @param book          Excel工作簿对象
     * @param sheetIndex    要读取的Sheet的索引
     * @param startRowIndex 开始行索引，即从哪行开始读取
     * @return List<List<String>>
     */
    public static List<List<String>> readSheet(Workbook book, int sheetIndex, int startRowIndex) throws Exception {
        return readSheet(book.getSheetAt(sheetIndex), startRowIndex);
    }

    /**
     * 读取Excel Sheet数据
     *
     * @param sheet
     * @param startRowIndex 开始行索引，即从哪行开始读取
     * @return List<List<String>>
     */
    public static List<List<String>> readSheet(Sheet sheet, int startRowIndex) {
        List<List<String>> sheetDataList = Lists.newArrayList();

        // 获取总行数
        int rowCount = sheet.getLastRowNum() + 1;

        // 若存在行数据，则读取每一行的数据
        if (null != sheet.getRow(startRowIndex)) {
            for (int i = startRowIndex; i < rowCount; i++) {
                Row row = sheet.getRow(i);
                if (null != row) {
                    // 获取每行总列数
                    short cellCount = row.getLastCellNum();

                    // 读取每列数据，并组合成行数据
                    List<String> rowData = Lists.newArrayList();
                    StringBuilder dataBuild = new StringBuilder();
                    for (short c = 0; c < cellCount; c++) {
                        String cval = getCellValue(row.getCell(c));
                        rowData.add(cval);
                        dataBuild.append(cval);
                    }
                    if (StringUtils.isNotBlank(dataBuild)) {
                        sheetDataList.add(rowData);
                    }
                }
            }
        }
        return sheetDataList;
    }

    /**
     * 生成Excel文件
     *
     * @param dataset       数据集
     * @param templatePath  模板路径
     * @param savePath      Excel的保存路径
     * @return file 返回新生成的Excel文件对象
     */
    public static File write(Map<String, Object> dataset, String templatePath, String savePath) throws Exception {
        return write(Lists.newArrayList(), dataset, templatePath, savePath);
    }

    /**
     * 生成Excel文件
     *
     * @param colsTitle     表头(列名称集合)
     * @param dataset       数据集
     * @param templatePath  模板路径
     * @param savePath      Excel的保存路径
     * @return file 返回新生成的Excel文件对象
     */
    public static File write(List<String> colsTitle, Map<String, Object> dataset, String templatePath, String savePath) throws Exception {
        File newFile = null;
        if (isValid(templatePath) && isValid(savePath)) {
            File templateFile = getFile(templatePath);
            if (null != templateFile) {
                newFile = new File(savePath);
                try {
                    Context context = new Context();

                    // 载入模型数据
                    if (!dataset.containsKey(COLS_TITLE)) {
                        context.putVar(COLS_TITLE, colsTitle);
                    }
                    if (null != dataset) {
                        dataset.forEach((k, v) -> context.putVar(k, v));
                    }

                    // 同一时间同一个文件，只能由一个线程生成
                    if (null == KEY_LOCK || (KEY_LOCK.lock(savePath) && !newFile.exists())) {
                        try (InputStream templateStream = new FileInputStream(templateFile); OutputStream outputStream = new FileOutputStream(newFile)) {
                            // 父目录不存在，则创建
                            if (!newFile.getParentFile().exists()) {
                                newFile.getParentFile().mkdirs();
                            }

                            // 添加JXLS2自定义函数工具类
                            context.putVar(POI_CONTEXT_KEY, new JxlsUtils());

                            // 生成Excel文件
                            JxlsHelper.getInstance().setUseFastFormulaProcessor(false).processTemplate(templateStream, outputStream, context);
                        }
                    } else if (newFile.exists()) {
                        log.warn("文件已存在，本次不重复生成：" + savePath);
                    }
                } catch (Exception e) {
                    log.error("TargetPath: " + savePath + "\t" + e.getMessage(), e);
                    newFile = null;
                    e.printStackTrace();
                }
            }
        }
        return newFile;
    }

    /**
     * 删除Excel Sheet 并保存为新Excel文件
     *
     * @param book      Excel工作簿对象
     * @param sheetName Sheet名称
     * @param savePath  Excel的保存路径
     * @return boolean
     */
    public static boolean deleteSheet(Workbook book, String sheetName, String savePath) throws Exception {
        return deleteSheet(book, book.getSheetIndex(sheetName), savePath);
    }

    /**
     * 删除Excel Sheet 并保存为新Excel文件
     *
     * @param book       Excel工作簿对象
     * @param sheetIndex Sheet索引
     * @param savePath   Excel的保存路径
     * @return boolean
     */
    public static boolean deleteSheet(Workbook book, int sheetIndex, String savePath) throws Exception {
        if (sheetIndex > -1) {
            book.removeSheetAt(sheetIndex);
            if (isValid(savePath)) {
                File file = new File(savePath);
                file.getParentFile().mkdirs();

                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    book.write(outputStream);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 获取Excel Cell单元格值
     *
     * <p>
     *     单元格值统一转换为String格式
     * </p>
     *
     * @param cell 单元格对象
     * @return String
     */
    public static String getCellValue(Cell cell) {
        if (null == cell)
            return null;

        String value = StringUtils.EMPTY;
        switch (cell.getCellType()) {
            case FORMULA:
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    value = NUMBER_FORMAT.format(cell.getNumericCellValue());
                }
                break;
            case STRING:
                value = StringUtils.trim(cell.getStringCellValue());
                break;
            case BLANK:
                break;
        }
        value = StringUtils.endsWith(value, ".0")? value.substring(0, value.length() - 2) : value;
        return value;
    }

    /**
     * 设置Excel的列宽度为自动
     *
     * @param book Excel工作簿对象
     */
    public static void setColumnAutoWidth(Workbook book) {
        int scount = getSheetCount(book);
        for (int i = 0; i < scount; i++) {
            Sheet sheet = book.getSheetAt(i);
            int colNum = sheet.getRow(sheet.getLastRowNum() - 1).getPhysicalNumberOfCells();
            for (int j = 0; j < colNum; j++) {
                sheet.autoSizeColumn(j);
            }
        }
    }

    /**
     * 获取Excel的Sheet个数
     *
     * @param filePath Excel文件路径
     * @return int
     */
    public static int getSheetCount(String filePath) throws Exception {
        int count = 0;
        File file = getFile(filePath);
        if (null != file) {
            try (FileInputStream stream = new FileInputStream(file); Workbook book = getWorkbook(stream, file)) {
                count = getSheetCount(book);
            }
        }
        return count;
    }

    /**
     * 获取Excel的Sheet个数
     *
     * @param book Excel工作簿对象
     * @return int
     */
    public static int getSheetCount(Workbook book) {
        return null != book? book.getNumberOfSheets() : 0;
    }

    /**
     * 获取Excel工作簿对象
     *
     * @param stream Excel文件流
     * @param file   Excel文件对象
     * @return Workbook
     */
    public static Workbook getWorkbook(FileInputStream stream, File file) throws IOException {
        Workbook book = null;
        try {
            book = new XSSFWorkbook(stream);
        } catch (Exception e) {
            stream = new FileInputStream(file);
            book = new HSSFWorkbook(stream);
        }
        return book;
    }

    /**
     * 获取Excel文件对象
     * <p>
     *     若文件不存在，则返回null
     * </p>
     *
     * @param filePath Excel文件路径
     * @return File
     */
    public static File getFile(String filePath) {
        if (isValid(filePath)) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file;
            }
            log.warn("文件不存在或非文件：" + filePath);
        }
        return null;
    }

    /**
     * 判断是否为有效的Excel文件
     *
     * @param fileName Excel文件名或含文件名的路径
     * @return boolean
     */
    public static boolean isValid(String fileName) {
        boolean flag = StringUtils.isNotBlank(fileName) && FilenameUtils.getExtension(fileName).matches("(?i)" + SUPPORTED_TYPES);
        if (!flag) {
            log.warn("不是一个有效的Excel：" + fileName);
        }
        return flag;
    }
}
