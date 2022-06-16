package com.matyrobbrt.igde.client.screen.base;

import com.matyrobbrt.igde.jei.JeiHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class DraggableScreen extends Screen implements com.matyrobbrt.igde.jei.DraggableScreen {
    protected final PositionAccessor posAccessor;
    protected final int screenWidth;
    protected final int screenHeight;
    protected Rect2i pos;

    protected int draggingXDistance;
    protected int draggingYDistance;

    protected DraggableScreen(Component pTitle, PositionAccessor positionAccessor, int width, int height) {
        super(pTitle);
        this.posAccessor = positionAccessor;
        this.screenWidth = width;
        this.screenHeight = height;
    }

    @Override
    protected void init() {
        updateChildren();
    }

    @Override
    public @NotNull Rect2i getPosition() {
        return pos;
    }

    public void updatePos(int cornerX, int cornerY) {
        posAccessor.setCornerX(cornerX);
        posAccessor.setCornerY(cornerY);
        updateChildren();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        for (final var children : children()) {
            if (children.mouseClicked(pMouseX, pMouseY, pButton))
                return true;
        }
        if (!JeiHelper.isDraggingIngredient() &&
                posAccessor.getCornerX() <= pMouseX && pMouseX <= posAccessor.getCornerX() + screenWidth
                && posAccessor.getCornerY() <= pMouseY && pMouseY <= posAccessor.getCornerY() + screenHeight
                && pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            setDragging(true);
            draggingXDistance = (int) pMouseX - posAccessor.getCornerX();
            draggingYDistance = (int) pMouseY - posAccessor.getCornerY();
            return true;
        }
        return false;
    }

    /**
     * Call as the first thing in {@link #render(PoseStack, int, int, float)}.
     */
    public void handleDragging(int pMouseX, int pMouseY) {
        if (isDragging())
            updatePos(pMouseX - draggingXDistance, pMouseY - draggingYDistance);
    }

    public void updateChildren() {
        pos = new Rect2i(posAccessor.getCornerX() + 7, posAccessor.getCornerY() + 8, screenWidth, screenHeight);
    }

    protected interface PositionAccessor {
        void setCornerX(int cornerX);
        void setCornerY(int cornerY);

        int getCornerX();
        int getCornerY();
    }
}
