package com.uol.yt120.lecampus.utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * This class is used for creating JSON string
 * data communicating between Fragments and Activity
 */
public class JsonDataProcessor {

    public String createJSONStringForPassData(String from, String to, String data){
        String dataJSONString;
        JSONObject dataJSON = new JSONObject();
        try {

            dataJSON.put("from", from);
            dataJSON.put("to", to);
            dataJSON.put("data", data);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataJSONString = dataJSON.toString();

        return dataJSONString;
    }

    //Encapsulate data from two String array to one JSONString
    public String encapDataToJSONString(String[] dataLabels, String[] dataValues) {
        String dataJSONString;
        JSONObject dataJSON = new JSONObject();
        try {
            for (int i=0; i<dataLabels.length;i++) {
                dataJSON.put(dataLabels[i], dataValues[i]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataJSONString = dataJSON.toString();
        return dataJSONString;
    }

//    public static String encapInputStreamToJSONString(InputStream inputStream) {
//
//    }
}
