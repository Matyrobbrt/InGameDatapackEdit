package com.matyrobbrt.igde.network.message.tag;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.network.Packet;
import com.matyrobbrt.igde.util.ServerConfig;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public record UpdateTagPacket<T>(ResourceKey<Registry<T>> registry, ResourceLocation tag, Collection<TagEntry> added,
                                 Collection<TagEntry> removed, boolean replace) implements Packet {
    @Override
    public void handle(Context context) {
        if (context.getSender() == null)
            return;
        final var tagsFolder = ServerConfig.getDatapackPath(context.getSender().server);
        final var tagLocation = tagsFolder.resolve("data/" + tag.getNamespace() + "/" + TagManager.getTagDir(registry) + "/" + tag.getPath() + ".json");
        final var json = new JsonObject();
        json.addProperty("replace", replace);
        final var added = new JsonArray();
        added().forEach(entry -> added.add(entry.toJson()));
        json.add("values", added);

        final var removed = new JsonArray();
        removed().forEach(entry -> removed.add(entry.id()));
        json.add("remove", removed);
        try {
            Files.deleteIfExists(tagLocation);
            Files.createDirectories(tagLocation.getParent());
            try (final var writer = Files.newBufferedWriter(tagLocation, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                TagEntry.GSON.toJson(json, writer);
            }
        } catch (IOException e) {
            InGameDatapackEdit.LOGGER.error("Exception trying to write tag values: ", e);
        }
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(registry.location());
        buf.writeResourceLocation(tag);
        buf.writeCollection(added, (buf1, tagEntry) -> tagEntry.encode(buf1));
        buf.writeCollection(removed, (buf1, tagEntry) -> tagEntry.encode(buf1));
        buf.writeBoolean(replace);
    }

    public static <T> UpdateTagPacket<T> decode(FriendlyByteBuf buf) {
        return new UpdateTagPacket<>(
                ResourceKey.createRegistryKey(buf.readResourceLocation()),
                buf.readResourceLocation(),
                buf.readList(TagEntry::decode),
                buf.readList(TagEntry::decode),
                buf.readBoolean()
        );
    }
}
