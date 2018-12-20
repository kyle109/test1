package com.mtpinterface.util;

import java.util.LinkedHashMap;
import org.json.JSONObject;

public class JsonUtil {

    public static String mapTojson(LinkedHashMap<String, String> map){

        JSONObject jo = new JSONObject();

        jo.put("map", map);

        String json = jo.get("map").toString();

        return json;

    }
}
