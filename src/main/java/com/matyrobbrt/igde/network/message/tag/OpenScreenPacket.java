package com.matyrobbrt.igde.network.message.tag;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.api.IGDEApi;
import com.matyrobbrt.igde.client.screen.EditTagScreen;
import com.matyrobbrt.igde.network.Packet;
import com.matyrobbrt.igde.util.ServerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record OpenScreenPacket<T>(ResourceKey<Registry<T>> registry, ResourceLocation tag, List<TagEntry> added,
                               List<TagEntry> removed, boolean replace) implements Packet {
    @Override
    public void handle(Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final var data = IGDEApi.INSTANCE.getData(registry);
            Minecraft.getInstance().setScreen(new EditTagScreen<>(
                    registry,
                    Minecraft.getInstance().level.registryAccess().registryOrThrow(registry),
                    tag,
                    added, removed,
                    replace,
                    Objects.requireNonNull(data)
            ));
        });
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(registry.location());
        buf.writeResourceLocation(tag);
        buf.writeCollection(added, (buf1, tagEntry) -> tagEntry.encode(buf1));
        buf.writeCollection(removed, (buf1, tagEntry) -> tagEntry.encode(buf1));
        buf.writeBoolean(replace);
    }

    public static <T> OpenScreenPacket<T> decode(FriendlyByteBuf buf) {
        return new OpenScreenPacket<>(
                ResourceKey.createRegistryKey(buf.readResourceLocation()),
                buf.readResourceLocation(),
                buf.readList(TagEntry::decode),
                buf.readList(TagEntry::decode),
                buf.readBoolean()
        );
    }

    @Nullable
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static OpenScreenPacket<?> make(MinecraftServer server, ResourceKey<Registry<?>> registry, ResourceLocation tag) {
        final var tagsFolder = ServerConfig.getDatapackPath(server);
        final var tagLocation = tagsFolder.resolve("data/" + tag.getNamespace() + "/" + TagManager.getTagDir(registry) + "/" + tag.getPath() + ".json");
        if (!Files.exists(tagLocation)) {
            return new OpenScreenPacket(registry, tag, new ArrayList<>(), new ArrayList<>(), false);
        } else {
            final List<TagEntry> added = new ArrayList<>();
            final List<TagEntry> removed = new ArrayList<>();
            try (final var reader = Files.newBufferedReader(tagLocation)) {
                final var json = TagEntry.GSON.fromJson(reader, JsonObject.class);
                final var replace = GsonHelper.getAsBoolean(json, "replace", false);
                if (json.has("remove")) {
                    for (JsonElement entry : GsonHelper.getAsJsonArray(json, "remove")) {
                        removed.add(TagEntry.fromJson(entry));
                    }
                }
                GsonHelper.getAsJsonArray(json, "values").forEach(value -> added.add(TagEntry.fromJson(value)));
                return new OpenScreenPacket(registry, tag, added, removed, replace);
            } catch (IOException e) {
                InGameDatapackEdit.LOGGER.error("Exception trying to read tag values: ", e);
                return null;
            }
        }
    }
}
