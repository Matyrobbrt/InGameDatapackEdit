package com.matyrobbrt.igde.jei;

import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.mixin.jei.accessor.GhostIngredientDragManagerAccessor;
import com.matyrobbrt.igde.mixin.jei.accessor.IngredientListOverlayAccessor;

public class JeiHelper {
    public static boolean isDraggingIngredient() {
        if (!InGameDatapackEdit.jeiLoaded)
            return false;
        return isDraggingIngredientInternal();
    }

    private static boolean isDraggingIngredientInternal() {
        return ((GhostIngredientDragManagerAccessor) ((IngredientListOverlayAccessor)
                IGDEJeiPlugin.runtime.getIngredientListOverlay()).getGhostIngredientDragManager()).getGhostIngredientDrag() != null;
    }
}
