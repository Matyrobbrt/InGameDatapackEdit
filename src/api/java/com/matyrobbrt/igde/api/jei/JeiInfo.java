package com.matyrobbrt.igde.api.jei;

import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

/**
 * Information for handling GUI ghost ingredients.
 *
 * @param T the type of the object
 * @param Z the type of the JEI ingredient
 */
@MethodsReturnNonnullByDefault
public interface JeiInfo<T, Z> {
    /**
     * Gets the class of the JEI ingredient compatible with this object.
     *
     * @return the class of the JEI ingredient
     */
    Class<Z> getIngredientClass();

    /**
     * Converts a JEI ingredient to a registry object.
     *
     * @param ingredient the ingredient
     * @return the registry object
     */
    @Nullable
    T convert(Z ingredient);

    /**
     * Tests if an ingredient is valid.
     * @param ingredient the ingredient
     * @return if the ingredient is valid
     */
    default boolean isValid(Z ingredient) {
        return true;
    }
}
