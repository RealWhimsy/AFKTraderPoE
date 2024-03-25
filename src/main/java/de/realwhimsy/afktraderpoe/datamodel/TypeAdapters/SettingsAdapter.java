package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;
import com.google.gson.*;
import de.realwhimsy.afktraderpoe.datamodel.Settings;

import java.lang.reflect.Type;

public class SettingsAdapter implements JsonSerializer<Settings>, JsonDeserializer<Settings> {

    @Override
    public JsonElement serialize(Settings src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ipAddress", src.getIpAddress());
        jsonObject.addProperty("port", src.getPort());
        jsonObject.addProperty("clientTxtPath", src.getClientTxtPath());
        jsonObject.addProperty("windowName", src.getWindowName());
        return jsonObject;
    }

    @Override
    public Settings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String ipAddress = jsonObject.get("ipAddress").getAsString();
        String port = jsonObject.get("port").getAsString();
        String clientTxtPath = jsonObject.get("clientTxtPath").getAsString();
        String windowName = jsonObject.get("windowName").getAsString();
        return new Settings(ipAddress, port, clientTxtPath, windowName);
    }
}

