package com.matyrobbrt.igde;

import com.google.auto.service.AutoService;
import com.matyrobbrt.igde.api.IGDEApi;
import com.matyrobbrt.igde.api.RegistryData;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@AutoService(IGDEApi.class)
public class ApiImpl implements IGDEApi {
    private final Map<ResourceKey<Registry<?>>, RegistryData<?>> data = new HashMap<>();
    @Override
    public Map<ResourceKey<Registry<?>>, RegistryData<?>> getAllData() {
        return Collections.unmodifiableMap(data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable <T> RegistryData<T> getData(ResourceKey<Registry<T>> resourceKey) {
        return (RegistryData<T>) data.get(resourceKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void registerData(ResourceKey<Registry<T>> registry, RegistryData<T> data) {
        this.data.put((ResourceKey<Registry<?>>) (Object) registry, data);
    }
}
