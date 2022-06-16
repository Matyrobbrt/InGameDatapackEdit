package com.matyrobbrt.igde.network.message.tag;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public record TagEntry(String id, boolean required) {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(id);
        buf.writeBoolean(required);
    }
    public static TagEntry decode(FriendlyByteBuf buf) {
        return new TagEntry(buf.readUtf(), buf.readBoolean());
    }

    public JsonElement toJson() {
        if (required)
            return new JsonPrimitive(id);
        final var json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("required", false);
        return json;
    }

    public static TagEntry fromJson(JsonElement json) {
        final String s;
        final boolean flag;
        if (json.isJsonObject()) {
            final var jsonobject = json.getAsJsonObject();
            s = GsonHelper.getAsString(jsonobject, "id");
            flag = GsonHelper.getAsBoolean(jsonobject, "required", true);
        } else {
            s = GsonHelper.convertToString(json, "id");
            flag = true;
        }
        return new TagEntry(s, flag);
    }
}
