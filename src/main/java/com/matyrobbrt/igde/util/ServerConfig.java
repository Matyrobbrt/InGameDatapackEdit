package com.matyrobbrt.igde.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.matyrobbrt.igde.InGameDatapackEdit;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.common.ForgeConfigSpec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ServerConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<String> DATAPACK_PATH;

    static {
        final var builder = new ForgeConfigSpec.Builder();

        DATAPACK_PATH = builder
                .comment("The path of the IGDE datapack.")
                .define("datapackPath", "igde");

        SPEC = builder.build();
    }

    public static Path getDatapackPath(MinecraftServer server) {
        final var path = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(DATAPACK_PATH.get());
        if (!Files.exists(path)) {
            final var gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            final var json = new JsonObject();
            final var pack = new JsonObject();
            json.add("pack", pack);
            pack.addProperty("pack_format", PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
            pack.addProperty("description", "IGDE datapack");
            try (final var writer = Files.newBufferedWriter(path.resolve("pack.mcmeta"))) {
                gson.toJson(json, writer);
            } catch (IOException e) {
                InGameDatapackEdit.LOGGER.error("Exception trying to create pack.mcmeta: ", e);
            }
        }
        return path;
    }
}
