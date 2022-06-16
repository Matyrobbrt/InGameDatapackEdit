package com.matyrobbrt.igde.jei;

import com.matyrobbrt.igde.client.screen.EditTagScreen;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
class MutateEntriesScreenGuiHandler implements IScreenHandler<EditTagScreen.MutateEntriesScreen> {

    public static @NotNull IGuiProperties getProperties(EditTagScreen.@NotNull MutateEntriesScreen guiScreen) {
        return new IGuiProperties() {
            @Override
            public @NotNull Class<? extends Screen> getScreenClass() {
                return EditTagScreen.MutateEntriesScreen.class;
            }

            @Override
            public int getGuiLeft() {
                return EditTagScreen.MutateEntriesScreen.cornerX;
            }

            @Override
            public int getGuiTop() {
                return EditTagScreen.MutateEntriesScreen.cornerY;
            }

            @Override
            public int getGuiXSize() {
                return 0; // The mixin will handle wrapping around the tab
            }

            @Override
            public int getGuiYSize() {
                return 0;
            }

            @Override
            public int getScreenWidth() {
                return guiScreen.width;
            }

            @Override
            public int getScreenHeight() {
                return guiScreen.height;
            }
        };
    }

    @Override
    public @Nullable IGuiProperties apply(EditTagScreen.@NotNull MutateEntriesScreen guiScreen) {
        return getProperties(guiScreen);
    }
}
