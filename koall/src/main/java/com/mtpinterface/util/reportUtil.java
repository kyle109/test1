package com.mtpinterface.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;

public class reportUtil extends report {

    @Override
    public void generateReportUtil(JSONObject report) throws IOException {
        System.out.println(report);
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        String template = this.read(templatePath);
        BufferedWriter output = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(new File(path)),"UTF-8"));
//        template = template.replaceFirst("\\$\\{resultData\\}", Matcher.quoteReplacement(gson.toJson(report)));
        template = template.replaceFirst("\\$\\{resultData\\}", report.toString());
        System.out.println(report.toString());
        output.write(template);
        output.flush();
        output.close();
    }
}
