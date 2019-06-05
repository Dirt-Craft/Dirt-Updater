package net.dirtcraft.dirtupdater.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Inject;
import net.dirtcraft.dirtupdater.DirtUpdater;
import org.slf4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

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

    public static JsonObject getJsonObjFromString(String jsonString){
        return new JsonParser().parse(jsonString).getAsJsonObject();
    }

    public static String getArtifactUrlFromJson(JsonObject js){
        if(!js.isJsonObject()){
            return null;
        }
        StringBuilder url = new StringBuilder();
        url
                .append(js.get("url").toString())
                .append("artifact/")
                .append(js.get("artifacts").getAsJsonArray().get(0).getAsJsonObject().get("fileName").toString());

        return url.toString().replace("\"", "");
    }

    public static File getFileFromURL(String url){
        // ToDo: Download the file. Maybe add as parameter the desired path aswell?
        return null;
    }

}
