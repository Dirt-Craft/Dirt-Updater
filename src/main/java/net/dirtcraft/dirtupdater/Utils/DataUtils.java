package net.dirtcraft.dirtupdater.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import net.dirtcraft.dirtupdater.DirtUpdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

public class DataUtils {
    public static String getStringFromURL(String url) {
        try (InputStream in = new URL(url).openStream(); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        } catch (IOException e) {
            return e.toString();
        }
    }

    public static JsonObject getJsonObjFromString(String jsonString) {
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }

    public static String getArtifactJarFromJson(JsonObject js) {
        if(!js.isJsonObject()) return null;

        return js.get("url").toString() +
                "artifact" + "/" + "build" + "/" + "libs" + "/" +
                js.get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("fileName").toString()
                        .replace("\"", "");
    }

    public static String getFileName(JsonObject js) {
        if(!js.isJsonObject()) return null;

        return js.get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("fileName").toString()
                .replace("\"", "");
    }

    public static Integer getBuildNumber(JsonObject js) {
        if(!js.isJsonObject()) return null;

        return js.get("number").getAsInt();

    }

    public static HashMap<String, String> jsonToMap() {
        return new Gson().fromJson(DirtUpdater.globalJSONString, new TypeToken<HashMap<String, String>>(){}.getType());
    }

    public static void logPlugins() {
        DirtUpdater.getLogger().info("---- Plugins To Check Updates ----");
        jsonToMap().keySet().forEach(name -> DirtUpdater.getLogger().info("> " + name));
    }

}