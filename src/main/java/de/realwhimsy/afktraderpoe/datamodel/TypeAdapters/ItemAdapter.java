package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;

import com.google.gson.*;
import de.realwhimsy.afktraderpoe.datamodel.Item;

import java.lang.reflect.Type;

public class ItemAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", src.getName());
        jsonObject.addProperty("amount", src.getAmount());
        return jsonObject;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
    JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String name = jsonObject.get("name").getAsString();
        int amount = jsonObject.get("amount").getAsInt();
        return new Item(name, amount);
    }
}