package com.matyrobbrt.igde.jei;

import com.matyrobbrt.igde.client.screen.EditTagScreen;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.ingredients.TypedIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.Rect2i;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
class MutateEntriesGhostIngredientHandler implements IGhostIngredientHandler<EditTagScreen.MutateEntriesScreen> {
    @Override
    public <I> List<Target<I>> getTargets(EditTagScreen.MutateEntriesScreen gui, I ingr, boolean doStart) {
        final var jeiInfo = gui.getRegistryData().getJeiInfo();
        if (jeiInfo != null) {
            Object ingredient = ingr;
            if (ingr instanceof TypedIngredient<?> typed) {
                ingredient = typed.getIngredient();
            }
            if (jeiInfo.getIngredientClass().isInstance(ingredient) && jeiInfo.isValid(ingredient)) {
                return List.of(
                        new Target<>() {
                            @Override
                            public Rect2i getArea() {
                                return gui.getObjectPos();
                            }

                            @Override
                            public void accept(I ingredient) {
                                final var obj = jeiInfo.convert(ingredient);
                                if (obj != null) {
                                    final var name = gui.getRegistry().getKey(obj);
                                    if (name != null)
                                        gui.updateValue(name.toString());
                                }
                            }
                        }
                );
            }
        }
        return List.of();
    }

    @Override
    public void onComplete() {

    }

    @Override
    public boolean shouldHighlightTargets() {
        return true;
    }
}
