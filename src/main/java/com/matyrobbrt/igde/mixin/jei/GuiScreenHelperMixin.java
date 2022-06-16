package com.matyrobbrt.igde.mixin.jei;

import com.matyrobbrt.igde.jei.DraggableScreen;
import mezz.jei.gui.GuiScreenHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Mixin(GuiScreenHelper.class)
public class GuiScreenHelperMixin {

    @Inject(
            at = @At("RETURN"),
            method = "getPluginsExclusionAreas",
            remap = false,
            cancellable = true
    )
    private void igde$addMutateScreenArea(Screen screen, CallbackInfoReturnable<Set<Rect2i>> cir) {
        if (screen instanceof DraggableScreen draggableScreen) {
            final var set = new HashSet<>(cir.getReturnValue());
            final var pos = draggableScreen.getPosition();
            if (pos != null)
                set.add(pos);
            cir.setReturnValue(set);
        }
    }

}
