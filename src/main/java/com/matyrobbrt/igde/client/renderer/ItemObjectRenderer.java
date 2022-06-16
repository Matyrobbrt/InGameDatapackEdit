package com.matyrobbrt.igde.client.renderer;

import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.client.screen.EditTagScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public record ItemObjectRenderer<T>(Function<T, ItemStack> itemGetter) implements ObjectRenderer<T> {

    @Override
    public void render(T object, Screen screen, PoseStack poseStack, int x, int y) {
        screen.setBlitOffset(100);
        Minecraft.getInstance().getItemRenderer().blitOffset = 100.0F;
        RenderSystem.enableDepthTest();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(Minecraft.getInstance().player, itemGetter.apply(object), x, y, x + y * screen.width);
        Minecraft.getInstance().getItemRenderer().blitOffset = 0.0F;
        screen.setBlitOffset(0);
    }

    @Override
    public void renderBackground(Screen scree, PoseStack poseStack, int x, int y) {
        if (scree instanceof EditTagScreen.MutateEntriesScreen) {
            GuiComponent.fill(poseStack, x, y, x + 16, y + 16, -2130706433);
        }
    }
}
