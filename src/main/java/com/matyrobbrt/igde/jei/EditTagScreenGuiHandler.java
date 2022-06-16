package com.matyrobbrt.igde.jei;

import com.matyrobbrt.igde.client.screen.EditTagScreen;
import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.api.gui.handlers.IScreenHandler;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("rawtypes")
class EditTagScreenGuiHandler implements IScreenHandler<EditTagScreen> {
    @Override
    public @Nullable IGuiProperties apply(EditTagScreen guiScreen) {
        // Very hacky workaround, see IngredientListOverlay#updateNewScreen, which if the properties changed
        // (because of the stacked GUIs), stops any dragging
        return guiScreen.getChild() == null ? null : MutateEntriesScreenGuiHandler.getProperties(guiScreen.getChild());
    }
}
