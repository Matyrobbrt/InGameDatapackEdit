package com.matyrobbrt.igde.jei;

import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a draggable screen.
 */
public interface DraggableScreen {
    /**
     * The current position of the screen.
     *
     * @return the current position of the screen
     */
    @NotNull
    Rect2i getPosition();
}
