package com.matyrobbrt.igde.client.screen;

import com.google.common.collect.ImmutableList;
import com.matyrobbrt.igde.util.Translations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class ListMenuScreen extends Screen {

    protected final int itemHeight;
    protected EntryList list;
    protected List<Item> entries;
    protected List<FormattedCharSequence> activeTooltip;
    protected FocusedEditBox activeTextField;
    protected FocusedEditBox searchTextField;
    protected boolean inJei;

    protected ListMenuScreen(Component title, int itemHeight) {
        super(title);
        this.itemHeight = itemHeight;
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        this.minecraft = pMinecraft;
        this.itemRenderer = pMinecraft.getItemRenderer();
        this.font = pMinecraft.font;
        this.width = pWidth;
        this.height = pHeight;
        setFocused(null);
    }

    @Override
    protected void init() {
        if (!inJei) {
            inJei = true;
        } else
            return; // Stop JEI from re-initing our stuff

        this.entries = new ArrayList<>();
        this.constructEntries(entries);
        this.list = new EntryList(this.entries);
        this.list.setRenderBackground(!isPlayingGame());
        this.addWidget(this.list);

        this.searchTextField = new FocusedEditBox(this.font, this.width / 2 - 110, 22, 220, 20, Translations.SEARCH.create());
        this.searchTextField.setResponder(s -> {
            ScreenUtil.updateSearchTextFieldSuggestion(this.searchTextField, s, this.entries);
            this.list.replaceEntries(s.isEmpty() ? this.entries : this.entries.stream().filter(item -> !(item instanceof IgnoreSearch) && item.getLabel().toLowerCase(Locale.ENGLISH).contains(s.toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));
            if (!s.isEmpty()) {
                this.list.setScrollAmount(0);
            }
        });
        this.addWidget(this.searchTextField);
        ScreenUtil.updateSearchTextFieldSuggestion(this.searchTextField, "", this.entries);
    }

    protected abstract void constructEntries(List<Item> entries);

    public void setActiveTooltip(List<FormattedCharSequence> tooltip) {
        this.activeTooltip = tooltip;
    }

    protected void updateTooltip(int mouseX, int mouseY) {
    }

    public void removeEntry(Item entry) {
        entries.remove(entry);
        list.removeEntry(entry);
    }

    public void addEntry(Item entry) {
        entries.add(entry);
        list.addEntry(entry);
    }

    public void addEntry(int index, Item entry) {
        entries.add(index, entry);
        list.addEntry(index, entry);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.activeTooltip = null;
        renderBackground(poseStack);
        this.list.render(poseStack, mouseX, mouseY, partialTicks);
        this.searchTextField.render(poseStack, mouseX, mouseY, partialTicks);

        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 7, 0xFFFFFF);
        super.render(poseStack, mouseX, mouseY, partialTicks);

        this.updateTooltip(mouseX, mouseY);
        if (this.activeTooltip != null) {
            this.renderTooltip(poseStack, this.activeTooltip, mouseX, mouseY);
        } else {
            ScreenUtil.drawWidgetTooltips(this, poseStack, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        final var entry = list.getEntryAtPos(mouseX, mouseY);
        if (entry != null)
            return entry.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static boolean isPlayingGame() {
        return Minecraft.getInstance().player != null;
    }

    protected class EntryList extends ContainerObjectSelectionList<Item> {
        public EntryList(List<Item> entries) {
            super(ListMenuScreen.this.minecraft, ListMenuScreen.this.width, ListMenuScreen.this.height, 50, ListMenuScreen.this.height - 36, ListMenuScreen.this.itemHeight);
            entries.forEach(this::addEntry);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 144;
        }

        @Override
        public int getRowWidth() {
            return 260;
        }

        // Overridden simply to make it public
        @Override
        public void replaceEntries(Collection<Item> entries) {
            super.replaceEntries(entries);
        }
        @Nullable
        public Item getEntryAtPos(double mouseX, double mouseY) {
            return getEntryAtPosition(mouseX, mouseY);
        }
        @Override
        public boolean removeEntry(Item pEntry) {
            return super.removeEntry(pEntry);
        }
        @Override
        public int addEntry(Item pEntry) {
            return super.addEntry(pEntry);
        }
        public void addEntry(int index, Item entry) {
            children().add(index, entry);
        }

        @Override
        public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
            super.render(poseStack, mouseX, mouseY, partialTicks);
            this.renderToolTips(poseStack, mouseX, mouseY);
        }

        private void renderToolTips(PoseStack poseStack, int mouseX, int mouseY) {
            if (this.isMouseOver(mouseX, mouseY) && mouseX < ListMenuScreen.this.list.getRowLeft() + ListMenuScreen.this.list.getRowWidth() - 67) {
                Item item = this.getEntryAtPosition(mouseX, mouseY);
                if (item != null) {
                    ListMenuScreen.this.setActiveTooltip(item.tooltip);
                }
            }
            this.children().forEach(item -> ScreenUtil.drawWidgetTooltips(item.children(), ListMenuScreen.this, poseStack, mouseX, mouseY));
        }
    }

    protected abstract static class Item extends ContainerObjectSelectionList.Entry<Item> implements LabelProvider {
        protected final Component label;
        protected List<FormattedCharSequence> tooltip;
        protected final List<GuiEventListener> children = new ArrayList<>();

        public Item(Component label) {
            this.label = label;
        }

        @Override
        public String getLabel() {
            return this.label.getString();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return children;
        }

        public <T extends GuiEventListener> T addChildren(T child) {
            children.add(child);
            return child;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(new NarratableEntry() {
                @Override
                public NarratableEntry.NarrationPriority narrationPriority() {
                    return NarratableEntry.NarrationPriority.HOVERED;
                }

                @Override
                public void updateNarration(NarrationElementOutput output) {
                    output.add(NarratedElementType.TITLE, label);
                }
            });
        }
    }

    protected class TitleItem extends Item implements IgnoreSearch {
        public TitleItem(Component title) {
            super(title);
        }

        @Override
        public void render(PoseStack poseStack, int x, int top, int left, int width, int height, int mouseX, int mouseY, boolean selected, float partialTicks) {
            Screen.drawCenteredString(poseStack, font, this.label, left + width / 2, top + 5, 0xFFFFFF);
        }
    }

    protected class FocusedEditBox extends EditBox {
        public FocusedEditBox(Font font, int x, int y, int width, int height, Component label) {
            super(font, x, y, width, height, label);
        }

        @Override
        protected void onFocusedChanged(boolean focused) {
            super.onFocusedChanged(focused);
            if (focused) {
                if (ListMenuScreen.this.activeTextField != null && ListMenuScreen.this.activeTextField != this) {
                    ListMenuScreen.this.activeTextField.setFocused(false);
                }
                ListMenuScreen.this.activeTextField = this;
            }
        }
    }

    protected interface IgnoreSearch {
    }
}