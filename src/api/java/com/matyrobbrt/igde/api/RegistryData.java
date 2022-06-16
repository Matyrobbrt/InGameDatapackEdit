package com.matyrobbrt.igde.api;

import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.api.jei.JeiInfo;
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * Data for a registry.
 *
 * @param <T> the type of the object
 */
@MethodsReturnNonnullByDefault
public interface RegistryData<T> {
    /**
     * Gets the object's renderer.
     * @return the object's renderer
     */
    ObjectRenderer<T> getRenderer();

    /**
     * Gets the {@link JeiInfo} associated with this registry.
     * @return the {@link JeiInfo}, or if one doesn't exist, {@code null}.
     */
    @Nullable
    default JeiInfo<T, ?> getJeiInfo() {
        return null;
    }
}
