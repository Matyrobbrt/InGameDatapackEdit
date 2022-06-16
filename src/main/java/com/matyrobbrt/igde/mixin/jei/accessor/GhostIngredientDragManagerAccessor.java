package com.matyrobbrt.igde.mixin.jei.accessor;

import mezz.jei.gui.ghost.GhostIngredientDrag;
import mezz.jei.gui.ghost.GhostIngredientDragManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GhostIngredientDragManager.class)
public interface GhostIngredientDragManagerAccessor {
    @Accessor(
            value = "ghostIngredientDrag",
            remap = false
    )
    GhostIngredientDrag<?> getGhostIngredientDrag();
}
