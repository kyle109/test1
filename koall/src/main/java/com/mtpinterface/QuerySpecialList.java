package com.mtpinterface;


import java.io.FileInputStream;
import java.io.IOException;

import java.util.*;
import com.mtpinterface.util.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;



public class QuerySpecialList {

    private static Map reportObj;

    public static void main(String[] args) throws ClientProtocolException, IOException
    {
        Map reportObj = new HashMap();
        reportObj.put("testPass", 0);
        reportObj.put("testResult", new ArrayList<Map<String, String>>());
        reportObj.put("testName", "");
        reportObj.put("testAll", 0);
        reportObj.put("testFail", 0);
        reportObj.put("beginTime", (new Date()).toString());
        reportObj.put("totalTime", "0");
        reportObj.put("testSkip", 0);
        String filepath = "D:\\book.xlsx";
        int timestamp = (int) (System.currentTimeMillis());

        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filepath));
        int sheetNums = workbook.getNumberOfSheets();

        List<Map> rstList = new ArrayList<Map>();
        //遍历sheet
        for (int s = 0; s < sheetNums; s ++) {


            readSheet(filepath, s, reportObj, rstList, workbook);
        }

        reportObj.put("testResult", rstList);
        //读sheet第一页
        //readSheet(filepath, 0);

        int endTimestamp = (int) (System.currentTimeMillis());
        reportObj.put("totalTime", (endTimestamp - timestamp) + "ms");

        //引入report
        reportUtil report = new reportUtil();
        JSONObject reportJson = new JSONObject(reportObj);
        report.generateReportUtil(reportJson);
    }

    protected static void readSheet(String filepath, int sheetNum, Map reportObj, List<Map> rstList, XSSFWorkbook workbook) throws IOException {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

        //从Excel中读取数据
        ExcelUtil excelUtil = new ExcelUtil();

        XSSFSheet xssfSheet = workbook.getSheetAt(sheetNum);
        int row = xssfSheet.getLastRowNum();
        //for循环读取Excel表格中的内容
        for (int j = 1; j <= row; j++) {

            for (int i = 1; i <= 8; i++) {
                map.put(excelUtil.read(filepath, sheetNum, 0, i), excelUtil.read(filepath, sheetNum, j, i));
            }
            System.out.println(map.toString());//输出验证

            String json = JsonUtil.mapTojson(map);//转化map为json方便接下来处理

            //读取Excel表格中的URL
            //第一页、第二行、第六列
            String url = excelUtil.read(filepath, sheetNum, j, 5);
            if (url.equals("")) {
                continue;
            }

            //判断为N跳过
            String a = excelUtil.read(filepath, sheetNum, j, 10);
            if (a.equals("N")) {
                continue;
            }

            //动态获取预期结果

            String expect = excelUtil.read(filepath, sheetNum, j,7);

            //动态获取预期结果
            String[] strings = expect.split(",");
            int len = strings.length;
            Map<String, String> expectMap = new HashMap<String, String>();
            for (int t = 0; t < len; t ++) {
                String[] expectStrings = strings[t].split("=");
                expectMap.put(expectStrings[0], expectStrings[1]);
            }

            if (expect.equals("")) {
                continue;
            }

            //创建httpclient实例

            HttpclientUtil httpclient = new HttpclientUtil();

            int timestamp = (int) (System.currentTimeMillis());

            String entityFlow = httpclient.post(url, json);
            int endTimestamp = (int) (System.currentTimeMillis());

            System.out.println(entityFlow);

            //转化返回值为json数据，以便取出resultcode自动，进行状态码的校验

            JSONObject jo = new JSONObject(entityFlow);

            Map<String, String> map1 = new HashMap<String, String>();

            String status = jo.get("status").toString();

            //定义写入excle中的返回内容
            //String resultCode = jo.get("rst").toString();
            map1.put("status", status);
            //rst为空返回
            if (!jo.has("rst")){
                jo.put("rst", "");
            }
            if (!jo.has("msg")){
                jo.put("msg", "");
            }

            map1.put("rst", jo.get("rst").toString());

            map1.put("msg", jo.get("msg").toString());

            //写入接口返回值
            excelUtil.write(filepath, sheetNum, j, 8, (new JSONObject(map1)).toString());

            reportObj.put("testAll", Integer.parseInt(reportObj.get("testAll").toString()) + 1);
            Map<String, String> requstMap = new HashMap<String, String>();
            requstMap.put("className", map.get("协议"));
            requstMap.put("methodName", map.get("请求类型"));
            requstMap.put("description", map.get("名称"));
            requstMap.put("spendTime", (endTimestamp - timestamp) + "ms");
            boolean isSuccess = true;
            for (Map.Entry<String, String> entry: expectMap.entrySet()) {
                //对比预期实际结果
                //动态获取预期结果
                isSuccess = isSuccess && (jo.has(entry.getKey()))&& (jo.get(entry.getKey()).toString().equals(entry.getValue().toString()));

            }
//            if (status.equals("500") && jo.get("rst").toString().equals("0")) {
            if (isSuccess) {
//                    if (str1.contains(status) && str2.contains(jo.get("rst").toString())) {
                requstMap.put("status", "成功");
                excelUtil.write(filepath, sheetNum, j, 9, "pass");
                reportObj.put("testPass", Integer.parseInt(reportObj.get("testPass").toString()) + 1);

            } else {
                requstMap.put("status", "失败");
                reportObj.put("testFail", Integer.parseInt(reportObj.get("testFail").toString()) + 1);
                excelUtil.write(filepath, sheetNum, j, 9, "fail");

            }

            rstList.add(requstMap);
        }
    }
}
