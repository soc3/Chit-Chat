/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package groupon.database;

import com.google.gson.Gson;
import java.sql.ResultSet;

/**
 *
 * @author sushant oberoi
 */
public class JsonParsing {

    private Gson gson;

    public JsonParsing() {
        gson = new Gson();
    }

    public String objectToJson(ResultSet obj) {
        // 1. Java object to JSON, and assign to a String

        String jsonInString = gson.toJson(obj);
        return jsonInString;
    }

    public ResultSet jsonToObject(String json) {
        // 1. JSON to Java object, read it from a Json String.
        ResultSet obj = gson.fromJson(json, ResultSet.class);
        return obj;
    }
}
