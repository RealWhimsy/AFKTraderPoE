package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;

import com.google.gson.*;
import de.realwhimsy.afktraderpoe.datamodel.Price;

import java.lang.reflect.Type;

public class PriceAdapter implements JsonSerializer<Price>, JsonDeserializer<Price> {

    @Override
    public JsonElement serialize(Price src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("amount", src.getAmount());
        jsonObject.addProperty("currency", src.getCurrency());
        return jsonObject;
    }

    @Override
    public Price deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int amount = jsonObject.get("amount").getAsInt();
        String currency = jsonObject.get("currency").getAsString();
        return new Price(amount, currency);
    }
}
