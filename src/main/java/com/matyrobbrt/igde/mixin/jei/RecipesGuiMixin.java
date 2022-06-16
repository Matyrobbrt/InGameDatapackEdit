package com.matyrobbrt.igde.mixin.jei;

import com.matyrobbrt.igde.client.screen.EditTagScreen;
import mezz.jei.gui.recipes.RecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipesGui.class)
public abstract class RecipesGuiMixin extends Screen {
    protected RecipesGuiMixin(Component pTitle) {
        super(pTitle);
    }

    @Nullable
    @Shadow(remap = false)
    private Screen parentScreen;

    @Inject(
            method = "Lmezz/jei/gui/recipes/RecipesGui;onClose()V",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = true
    )
    private void igde$useGuiStacking(CallbackInfo ci) {
        if (parentScreen instanceof EditTagScreen.MutateEntriesScreen mutateEntriesScreen) {
            minecraft.setScreen(mutateEntriesScreen.getParent());
            minecraft.pushGuiLayer(mutateEntriesScreen);
            ci.cancel();
        }
    }
}
