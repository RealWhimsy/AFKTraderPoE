package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;

import com.google.gson.*;
import de.realwhimsy.afktraderpoe.datamodel.AppResponse;

import java.lang.reflect.Type;

public class AppResponseAdapter implements JsonSerializer<AppResponse>, JsonDeserializer<AppResponse> {

    @Override
    public AppResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String action = jsonObject.get("action").getAsString();
        String content = jsonObject.get("content").getAsString();
        return new AppResponse(action, content);
    }

    @Override
    public JsonElement serialize(AppResponse src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", src.getAction());
        jsonObject.addProperty("content", src.getContent());
        return jsonObject;
    }
}
