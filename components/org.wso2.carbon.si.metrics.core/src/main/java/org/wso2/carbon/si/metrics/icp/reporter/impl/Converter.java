package org.wso2.carbon.si.metrics.icp.reporter.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Converter {

    public static JsonArray convertSourceDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject object = new JsonObject();
            String streamId = jsonObject.get("outputStreamId").getAsString();
            object.addProperty("name", streamId);
            JsonObject details = new JsonObject();
            details.addProperty("name", streamId);
            details.addProperty("type", jsonObject.get("inputStreamId").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            details.addProperty("annotation", jsonObject.get("inputStreamSiddhiApp").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertSinkDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject object = new JsonObject();
            String streamId = jsonObject.get("inputStreamId").getAsString();
            object.addProperty("name", streamId);
            JsonObject details = new JsonObject();
            details.addProperty("name", streamId);
            details.addProperty("type", jsonObject.get("outputStreamId").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            details.addProperty("annotation", jsonObject.get("inputStreamSiddhiApp").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertQueryDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject object = new JsonObject();
            object.addProperty("name", jsonObject.get("queryName").getAsString());
            JsonObject details = new JsonObject();
            details.addProperty("name", jsonObject.get("queryName").getAsString());
            details.addProperty("inputStream", jsonObject.get("inputStreamId").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            details.addProperty("outputStream", jsonObject.get("outputStreamId").getAsString());
            details.addProperty("query", jsonObject.get("query").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertSiddhiAppDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject object = new JsonObject();
            object.addProperty("name", jsonObject.get("appName").getAsString());
            object.addProperty("status", jsonObject.get("status").getAsString());
            JsonObject details = new JsonObject();
            details.addProperty("name", jsonObject.get("appName").getAsString());
            details.addProperty("status", jsonObject.get("status").getAsString());
            details.addProperty("age", jsonObject.get("age").getAsString());
            details.addProperty("isStatEnabled", jsonObject.get("isStatEnabled").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertTablesDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject jsonObject = element.getAsJsonObject();
            JsonObject object = new JsonObject();
            object.addProperty("name", jsonObject.get("tableId").getAsString());
            JsonObject details = new JsonObject();
            details.addProperty("name", jsonObject.get("tableId").getAsString());
            details.addProperty("definition", jsonObject.get("table").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertWindowsDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject object = new JsonObject();
            JsonObject jsonObject = element.getAsJsonObject();
            object.addProperty("name", jsonObject.get("windowId").getAsString());
            object.addProperty("id", jsonObject.get("windowName").getAsString());
            JsonObject details = new JsonObject();
            details.addProperty("name", jsonObject.get("windowId").getAsString());
            details.addProperty("definition", jsonObject.get("window").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }

    public static JsonArray convertAggregationsDetails(JsonArray arr) {
        JsonArray updatedArr = new JsonArray(arr.size());
        for (JsonElement element: arr ){
            JsonObject object = new JsonObject();
            JsonObject jsonObject = element.getAsJsonObject();
            object.addProperty("name", jsonObject.get("outputStreamId").getAsString());
            JsonObject details = new JsonObject();
            details.addProperty("inputStream", jsonObject.get("inputStreamId").getAsString());
            details.addProperty("outputStream", jsonObject.get("outputStreamId").getAsString());
            details.addProperty("name", jsonObject.get("outputStreamId").getAsString());
            details.addProperty("appName", jsonObject.get("appName").getAsString());
            object.add("details", details);
            updatedArr.add(object);
        }
        return updatedArr;
    }
}
