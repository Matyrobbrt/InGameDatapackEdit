package com.matyrobbrt.igde.jei;

import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.client.screen.EditTagScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.runtime.JeiRuntime;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
@MethodsReturnNonnullByDefault
public class IGDEJeiPlugin implements IModPlugin {
    public static final ResourceLocation ID = new ResourceLocation(InGameDatapackEdit.MOD_ID, "jei");
    public static JeiRuntime runtime;
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiScreenHandler(EditTagScreen.MutateEntriesScreen.class, new MutateEntriesScreenGuiHandler());
        registration.addGuiScreenHandler(EditTagScreen.class, new EditTagScreenGuiHandler()); // Very hacky workaround for JEI not liking stacked GUIs

        registration.addGhostIngredientHandler(EditTagScreen.MutateEntriesScreen.class, new MutateEntriesGhostIngredientHandler());
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = (JeiRuntime) jeiRuntime;
    }
}
