package com.matyrobbrt.igde.jei;

import net.minecraft.client.renderer.Rect2i;
import org.jetbrains.annotations.Nullable;

public interface DraggableScreen {
    @Nullable
    Rect2i getPosition();
}
