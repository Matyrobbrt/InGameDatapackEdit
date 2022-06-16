package com.matyrobbrt.igde.api;

import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * The main interface used for interacting with InGameDatapackEdit's API.
 */
public interface IGDEApi {
    /**
     * The IGDE mod id.
     */
    String MOD_ID = "igde";

    /**
     * The singleton InGameDatapackEdit API instance.
     */
    IGDEApi INSTANCE = Util.make(() -> {
        final var loader = ServiceLoader.load(IGDEApi.class).iterator();
        if (!loader.hasNext()) {
            throw new NullPointerException("No IGDEApi was found on the classpath");
        }
        final var api = loader.next();
        if (loader.hasNext()) {
            throw new IllegalArgumentException("More than one IGDEApi was found!");
        }
        return api;
    });

    /**
     * Gets an immutable view of all registered registry datas
     *
     * @return an immutable view of all registered registry datas
     */
    Map<ResourceKey<Registry<?>>, RegistryData<?>> getAllData();

    /**
     * Gets the data linked to a registry.
     * @param resourceKey the key of the registry
     * @param <T> the type of the registry
     * @return the data, or if one is not present, {@code null}
     */
    @Nullable
    <T> RegistryData<T> getData(ResourceKey<Registry<T>> resourceKey);

    /**
     * Registers data for a registry.
     *
     * @param registry the key of the registry
     * @param data the registry data
     * @param <T>      the type of the registry
     */
    <T> void registerData(ResourceKey<Registry<T>> registry, RegistryData<T> data);
}
