package com.matyrobbrt.igde.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * A renderer for objects inside the datapack editor.
 *
 * @param <T> the type of the object to register
 */
public interface ObjectRenderer<T> {
    Component UNKNOWN_OBJECT = new TextComponent("?");

    /**
     * Renders an object.
     *
     * @param object    the object to register
     * @param screen    the screen to register inside
     * @param poseStack the pose stack
     * @param x         the x position to render
     * @param y         the y position to render
     */
    void render(T object, Screen screen, PoseStack poseStack, int x, int y);

    /**
     * Renders an unknown object.
     *
     * @param id        the ID of the object to register. May be {@code null}
     * @param tag       if this object is a tag
     * @param screen    the screen to register inside
     * @param poseStack the pose stack
     * @param x         the x position to render
     * @param y         the y position to render
     */
    default void renderUnknownObject(@Nullable ResourceLocation id, boolean tag, Screen screen, PoseStack poseStack, int x, int y) {
        Screen.drawCenteredString(poseStack, Minecraft.getInstance().font, UNKNOWN_OBJECT, (x + getItemWidth() / 2), y + (getItemHeight() - Minecraft.getInstance().font.lineHeight) / 2, 0x0000ff);
    }

    /**
     * Renders the background of an object.
     *
     * @param screen    the screen to register inside
     * @param poseStack the pose stack
     * @param x         the x position to render
     * @param y         the y position to render
     */
    default void renderBackground(Screen screen, PoseStack poseStack, int x, int y) {

    }

    /**
     * Gets the height of an object to render.
     *
     * @return the height of an object to render
     */
    default int getItemHeight() {
        return 16;
    }

    /**
     * Gets the width of an object to render.
     *
     * @return the width of an object to render
     */
    default int getItemWidth() {
        return 16;
    }
}
