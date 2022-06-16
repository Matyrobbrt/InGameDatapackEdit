package com.matyrobbrt.igde.jei.info;

import com.matyrobbrt.igde.api.jei.JeiInfo;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

@MethodsReturnNonnullByDefault
public class ItemJeiInfo<T> implements JeiInfo<T, ItemStack> {

    private final Function<ItemStack, T> converter;

    public ItemJeiInfo(Function<ItemStack, T> converter) {
        this.converter = converter;
    }

    @Override
    public Class<ItemStack> getIngredientClass() {
        return ItemStack.class;
    }

    @Override
    public T convert(ItemStack ingredient) {
        return converter.apply(ingredient);
    }
}
