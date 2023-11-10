package com.nosuchfield.lucene;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReadAndWriterCsvFlie {
    // 需要写入的 csv 文件路径
    public static final String QUERY_PATH = "/home/labot/guhao/PycharmProject/QueryHint/data1/dataset/query/test_data-q2t-query.csv";
    public static final String ALL_TITLE_PATH = "/home/labot/guhao/PycharmProject/QueryHint/data1/dataset/title/allTitle.csv";
    public static final String writeCsvFilePath = "/home/labot/guhao/PycharmProject/QueryHint/data1/dataset/title/q2t_lucene.csv";

    /**
     * 生成 csv 文件
     */
    public static void writeCsvFile(String writeCsvFilePath,List<List<String>> s) {
        // 创建 CSV Writer 对象, 参数说明（写入的文件路径，分隔符，编码格式)
        CsvWriter csvWriter = new CsvWriter(writeCsvFilePath,'\t', StandardCharsets.UTF_8);

        try {
            // 定义 header 头
            String[] headers = {"t1", "t2","t3", "t4", "t5","t6", "t7", "t8","t9","t10"};
            // 写入 header 头
            csvWriter.writeRecord(headers);

            // 写入行
            for(int row = 0; row < s.size(); row++){
                if(s.get(row).size() != 10){
                    int len = 10 - s.get(row).size();
                    for(int i = 0;i < len;i++){
//                        System.out.println(i);
                        s.get(row).add(String.valueOf(' '));
                    }
                }
                csvWriter.writeRecord((String[]) Arrays.asList(s.get(row).get(0),s.get(row).get(1),s.get(row).get(2),s.get(row).get(3),s.get(row).get(4),s.get(row).get(5),s.get(row).get(6),s.get(row).get(7),s.get(row).get(8),s.get(row).get(9)).toArray());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            csvWriter.close();
        }
    }
        /**
     * 读取 csv 文件
     * @return
     */
    public static List<String> readCsvFile(String readCsvFilePath, Boolean flag) {
        // 缓存读取的数据
        List<String[]> content = new ArrayList<>();
        List<String> r_list = new ArrayList<>();

        try {
            // 创建 CSV Reader 对象, 参数说明（读取的文件路径，分隔符，编码格式)
            CsvReader csvReader = new CsvReader(readCsvFilePath, '\t', StandardCharsets.UTF_8);
            // 跳过表头
            if (flag){
                csvReader.readHeaders();
            }

            // 读取除表头外的内容
            while (csvReader.readRecord()) {
                // 读取一整行
//                String line = csvReader.getRawRecord();
//                System.out.println(line);
                content.add(csvReader.getValues());
            }
            csvReader.close();

            for (int row = 0; row < content.size(); row++) {
                // 读取第 row 行，第 0 列的数据
                String query = content.get(row)[0];
                r_list.add(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r_list;
    }

    public static void main(String[] args) {
//        writeCsvFile(WRITE_CSV_FILE_PATH);
//        readCsvFile(QUERY_PATH);

    }
}
