package com.ty.cm.utils.document;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CSV文件读写工具类
 *
 * @Author Tommy
 * @Date 2022/12/8
 */
@Slf4j
public class CSVUtil {

    /** 表头Key **/
    public static final String COL_NAMES = "colNames";

    /** 数据Key **/
    public static final String DATAS = "datas";

    /**
     * 读取整个CSV文件
     *
     * @param filePath CSV文件路径
     * @return Map<String, List<String>>
     */
    public static Map<String, List<?>> read(String filePath) {
        Map<String, List<?>> dataMap = Maps.newHashMap();
        File file = getFile(filePath);
        if (null != file && file.length() > 0) {
            Table table = Table.read().csv(filePath);
            if (table.columnCount() > 0) {
                // 获取列名
                List<String> colNames = table.columnNames();

                // 遍历每一行数据
                List<List<String>> csvDatas = Lists.newArrayList();
                table.stream().forEach(row -> {
                    List<String> rowData = Lists.newArrayList();
                    for (String n : colNames) {
                        Object v = row.getObject(n);
                        rowData.add(null == v? null : v.toString());
                    }
                    csvDatas.add(rowData);
                });

                // 封装数据
                dataMap.put(COL_NAMES, colNames);
                dataMap.put(DATAS, csvDatas);
            }
        } else {
            log.warn("数据文件为空：" + filePath);
        }
        return dataMap;
    }

    /**
     * 读取CSV文件数据
     *
     * @param filePath CSV文件路径
     * @return List<List<String>>
     */
    @SuppressWarnings("unchecked")
    public static List<List<String>> readData(String filePath) {
        List<List<String>> csvDatas = Lists.newArrayList();
        Map<String, List<?>> dataMap = read(filePath);
        if (dataMap.containsKey(DATAS)) {
            csvDatas = (List<List<String>>) dataMap.get(DATAS);
        }
        return csvDatas;
    }

    /**
     * 生成CSV文件
     *
     * @param colNames  列名
     * @param dataset   数据集
     * @param savePath  CSV的保存路径
     * @return file 返回新生成的CSV文件对象
     */
    public static File write(List<String> colNames, List<List<String>> dataset, String savePath) {
        File newFile = null;
        if (CollectionUtils.isNotEmpty(colNames) && CollectionUtils.isNotEmpty(dataset) && StringUtils.isNotBlank(savePath)) {
            // 构建列对象
            List<Column<?>> columns = new ArrayList<>(colNames.size());
            for (String n : colNames) {
                columns.add(StringColumn.create(n));
            }

            // 构建数据表格
            Table table = Table.create(columns);
            dataset.forEach(items -> {
                Row row = table.appendRow();
                for (int i = 0; i < items.size(); i++) {
                    row.setString(i, items.get(i));
                }
            });

            // 将数据表格保存到CSV文件
            newFile = new File(savePath);
            if (!newFile.getParentFile().exists()) {
                newFile.getParentFile().mkdirs();
            }
            table.write().csv(newFile);
        } else {
            log.warn("参数无效：colNames=" + colNames + "\tsavePath=" + savePath);
        }
        return newFile;
    }

    /**
     * 获取CSV文件对象
     * <p>
     *     若文件不存在，则返回null
     * </p>
     *
     * @param filePath CSV文件路径
     * @return File
     */
    public static File getFile(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file;
            }
            log.warn("文件不存在或非文件：" + filePath);
        }
        return null;
    }
}
