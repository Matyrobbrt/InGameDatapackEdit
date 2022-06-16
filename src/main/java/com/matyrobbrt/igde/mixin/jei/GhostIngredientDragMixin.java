package com.matyrobbrt.igde.mixin.jei;

import com.matyrobbrt.igde.client.screen.EditTagScreen;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.gui.ghost.GhostIngredientDrag;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GhostIngredientDrag.class)
public class GhostIngredientDragMixin<T> {
    @Inject(
            at = @At("HEAD"),
            method = "drawItem",
            cancellable = true,
            remap = false
    )
    private void igde$dontDrawForStackedGuis(Minecraft minecraft, PoseStack poseStack, int mouseX, int mouseY, CallbackInfo ci) {
        if (minecraft.screen instanceof EditTagScreen.MutateEntriesScreen && (mouseX == Integer.MAX_VALUE || mouseY == Integer.MAX_VALUE)) {
            ci.cancel(); // The event JEI listens for is fired when for the sub GUIs, but with max value mouse pos, causing
            // the line renderer to go nuts
        }
    }
}
