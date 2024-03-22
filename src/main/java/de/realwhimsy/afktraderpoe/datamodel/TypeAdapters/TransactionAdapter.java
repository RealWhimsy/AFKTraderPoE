package de.realwhimsy.afktraderpoe.datamodel.TypeAdapters;

import com.google.gson.*;
import de.realwhimsy.afktraderpoe.datamodel.Item;
import de.realwhimsy.afktraderpoe.datamodel.Price;
import de.realwhimsy.afktraderpoe.datamodel.Transaction;

import java.lang.reflect.Type;

public class TransactionAdapter implements JsonSerializer<Transaction>, JsonDeserializer<Transaction> {

    @Override
    public JsonElement serialize(Transaction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("buyer", src.getBuyer());
        jsonObject.add("price", context.serialize(src.getPrice()));
        jsonObject.addProperty("league", src.getLeague());
        jsonObject.add("item", context.serialize(src.getItem()));
        return jsonObject;
    }

    @Override
    public Transaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String buyer = jsonObject.get("buyer").getAsString();
        Price price = context.deserialize(jsonObject.get("price"), Price.class);
        String league = jsonObject.get("league").getAsString();
        Item item = context.deserialize(jsonObject.get("item"), Item.class);
        return new Transaction(buyer, price, league, item);
    }
}