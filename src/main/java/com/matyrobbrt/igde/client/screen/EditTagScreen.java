package com.matyrobbrt.igde.client.screen;

import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.api.RegistryData;
import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.jei.DraggableScreen;
import com.matyrobbrt.igde.jei.JeiHelper;
import com.matyrobbrt.igde.network.IGDENetwork;
import com.matyrobbrt.igde.network.message.tag.TagData;
import com.matyrobbrt.igde.network.message.tag.TagEntry;
import com.matyrobbrt.igde.network.message.tag.UpdateTagPacket;
import com.matyrobbrt.igde.util.Translations;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class EditTagScreen<T> extends ListMenuScreen implements DraggableScreen {
    public static final ResourceLocation ICONS = new ResourceLocation(InGameDatapackEdit.MOD_ID, "textures/gui/icons.png");

    private final ResourceKey<Registry<T>> resourceKey;
    private final Registry<T> registry;
    private final ResourceLocation tag;
    private final RegistryData<T> registryData;

    private final TagData original;
    private final TagData newData;
    private final ObjectRenderer<T> renderer;

    private final TitleItem addedObjectsItem;
    private final TitleItem removedObjectsItem;

    @Nullable
    private MutateEntriesScreen child;

    @SuppressWarnings("unchecked")
    public EditTagScreen(ResourceKey<Registry<T>> resourceKey, Registry<T> registry, ResourceLocation tag, List<TagEntry> added, List<TagEntry> removed, boolean replace, RegistryData<T> registryData) {
        super(Translations.TAG_EDITOR.create(tag), registryData.getRenderer().getItemWidth() + 15);
        this.resourceKey = resourceKey;
        this.registry = registry;
        this.tag = tag;
        this.original = TagData.immutable(replace, added, removed);
        this.newData = original.copy();
        this.registryData = registryData;
        this.renderer = registryData.getRenderer();

        this.addedObjectsItem = new TitleItem(Translations.ADDED_OBJECTS.create());
        this.removedObjectsItem = new TitleItem(Translations.REMOVED_OBJECTS.create());
    }

    @Nullable
    public MutateEntriesScreen getChild() {
        return child;
    }

    @Override
    public Rect2i getPosition() {
        return child == null ? null : child.getPosition();
    }

    @Override
    protected void init() {
        super.init();
        Button additionButton = new MutationButton(-23, 0, ActionType.ADDITION);
        addRenderableWidget(additionButton);
        Button removalButton = new MutationButton(searchTextField.getWidth() + 3, 20, ActionType.REMOVAL);
        addRenderableWidget(removalButton);

        this.addRenderableWidget(new Button(this.width / 2 - 130, this.height - 29, 60, 20, CommonComponents.GUI_DONE, (p_97691_) -> this.onDone()));
        this.addRenderableWidget(new Button(this.width / 2 + 70, this.height - 29, 60, 20, CommonComponents.GUI_CANCEL, (p_97687_) -> this.onClose()));
        addRenderableWidget(CycleButton.booleanBuilder(Translations.REPLACE_ON.create(), Translations.REPLACE_OFF.create())
                .withTooltip(value -> List.of(Translations.REPLACE_VALUES_DESCRIPTION.create().getVisualOrderText()))
                .displayOnlyValue()
                .withInitialValue(this.original.replace().get())
                .create(this.width / 2 - 50, this.height - 29, 100, 20, Translations.REPLACE.create(),
                        (button, val) -> newData.replace().set(val)));
    }

    private void onDone() {
        if (!original.equals(newData)) {
            IGDENetwork.sendToServer(new UpdateTagPacket<>(
                    resourceKey,
                    tag,
                    newData.added(),
                    newData.removed(),
                    newData.replace().get()
            ));
        }
        super.onClose();
    }

    @Override
    public void onClose() {
        if (newData.equals(original))
            super.onClose();
        else
            ConfirmationScreen.push(Translations.UNSAVED_CHANGES.create());
    }

    private final class MutationButton extends Button {

        private final int u;
        private final ActionType actionType;

        public MutationButton(int xOffset, int u, ActionType actionType) {
            super(
                    searchTextField.x + xOffset, searchTextField.y,
                    20, 20, TextComponent.EMPTY,
                    e -> {
                    },
                    ScreenUtil.createButtonTooltip(EditTagScreen.this, actionType.createComponent(), 120)
            );
            this.u = u;
            this.actionType = actionType;
        }

        @Override
        public void onPress() {
            minecraft.pushGuiLayer(new MutateEntriesScreen(EditTagScreen.this.width / 3, EditTagScreen.this.height / 2, actionType));
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            pPoseStack.pushPose();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, ICONS);
            Screen.blit(pPoseStack, x, y, EditTagScreen.this.getBlitOffset(), u, 16, 20, 20, 256, 256);
            pPoseStack.popPose();
        }
    }

    public final class MutateEntriesScreen extends Screen implements DraggableScreen {

        public static final int WIDTH = 176;
        public static final int HEIGHT = 60;

        public static final ResourceLocation BACKGROUND_LOCATION = new ResourceLocation(InGameDatapackEdit.MOD_ID, "textures/gui/tag_mutation.png");
        private EditBox editBox;
        public static int cornerX;
        public static int cornerY;
        private CycleButton<Boolean> required;
        private Button doneButton;
        private Button cancelButton;
        private Rect2i objectPos;

        private final TagEntryRenderer<T> entryRenderer;
        private final ActionType actionType;

        private MutateEntriesScreen(int cornerX, int cornerY, ActionType actionType) {
            super(NarratorChatListener.NO_TITLE);
            // Only set the corners if not previously set
            if (MutateEntriesScreen.cornerX == 0)
                MutateEntriesScreen.cornerX = cornerX;
            if (MutateEntriesScreen.cornerY == 0)
                MutateEntriesScreen.cornerY = cornerY;
            this.entryRenderer = new TagEntryRenderer<>(renderer, registry);
            this.actionType = actionType;
        }

        @Override
        protected void init() {
            EditTagScreen.this.child = this;
            entryRenderer.updateValue(null);
            editBox = new EditBox(minecraft.font, 0, 0, 140, 20, Translations.VALUE.create());
            editBox.setSuggestion(Translations.VALUE.create().getString());
            editBox.setResponder(this::onEdited);
            addWidget(editBox);
            setFocused(editBox);
            required = CycleButton.booleanBuilder(Translations.REQUIRED_ON.create(), Translations.REQUIRED_OFF.create())
                    .withInitialValue(true)
                    .displayOnlyValue()
                    .withTooltip(val -> List.of(Translations.REQUIRED_DESCRIPTION.create().getVisualOrderText()))
                    .create(0, 0, 50, 20, Translations.REQUIRED.create());
            addRenderableWidget(required);
            doneButton = this.addRenderableWidget(new Button(0, 0, 50, 20,
                    CommonComponents.GUI_DONE, (p_97691_) -> this.onDone(), (pButton, pPoseStack, pMouseX, pMouseY) -> {
                if (!entryRenderer.isValid() && required.getValue()) {
                    EditTagScreen.this.renderTooltip(pPoseStack, Translations.INVALID_VALUE.create(), pMouseX, pMouseY);
                } else if (entryRenderer.getValue() != null && hasValue(actionType == ActionType.ADDITION, entryRenderer.getValue())) {
                    EditTagScreen.this.renderTooltip(pPoseStack, Translations.DUPLICATE_VALUE.create(), pMouseX, pMouseY);
                }
            }));
            cancelButton = this.addRenderableWidget(new Button(0, 0, 50, 20, CommonComponents.GUI_CANCEL, (p_97687_) -> this.onClose()));
            updateChildren();
        }

        public void updatePos(int cornerX, int cornerY) {
            MutateEntriesScreen.cornerX = cornerX;
            MutateEntriesScreen.cornerY = cornerY;
            updateChildren();
        }

        public EditTagScreen<T> getParent() {
            return EditTagScreen.this;
        }

        public RegistryData<T> getRegistryData() {
            return registryData;
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        public void onDone() {
            if (entryRenderer.getValue() == null || entryRenderer.getValue().isEmpty())
                doClose();
            if ((!entryRenderer.isValid() && required.getValue()) || hasValue(actionType == ActionType.ADDITION, entryRenderer.getValue()))
                return;
            doClose();
            addEntry(actionType == ActionType.ADDITION, new TagEntry(entryRenderer.getValue(), required.getValue()));
        }

        private void doClose() {
            EditTagScreen.this.child = null;
            super.onClose();
        }

        @Override
        public void removed() {
            EditTagScreen.this.child = null;
        }

        @Override
        public void onClose() {
            if (entryRenderer.getValue() == null || entryRenderer.getValue().isEmpty())
                doClose();
            else
                ConfirmationScreen.push(Translations.UNSAVED_CHANGES.create());
        }

        public void onEdited(String newValue) {
            entryRenderer.updateValue(newValue);
            if (newValue.isBlank())
                editBox.setSuggestion(Translations.VALUE.create().getString());
            else
                editBox.setSuggestion("");
        }

        public void updateChildren() {
            editBox.x = cornerX + 12 + renderer.getItemWidth();
            editBox.y = cornerY + 8 + (renderer.getItemHeight() - 20) / 2;
            final var secondLayerY = cornerY + 16 + renderer.getItemHeight();
            cancelButton.y = secondLayerY;
            cancelButton.x = cornerX + 5;
            required.x = cornerX + 5 + 50 + 7;
            required.y = secondLayerY;
            doneButton.y = secondLayerY;
            doneButton.x = cornerX + 5 + 2 * (50 + 7);

            objectPos = new Rect2i(cornerX + 7, cornerY + 8, renderer.getItemWidth(), renderer.getItemHeight());
        }

        public void updateValue(String value) {
            this.editBox.setValue(value);
            entryRenderer.updateValue(value);
        }

        public Rect2i getObjectPos() {
            return objectPos;
        }

        private static int draggingXDistance;
        private static int draggingYDistance;

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            for (final var children : children()) {
                if (children.mouseClicked(pMouseX, pMouseY, pButton))
                    return true;
            }
            if (!JeiHelper.isDraggingIngredient() &&
                    cornerX <= pMouseX && pMouseX <= cornerX + WIDTH && cornerY <= pMouseY && cornerY <= cornerY + HEIGHT
                    && pButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                setDragging(true);
                draggingXDistance = (int) pMouseX - cornerX;
                draggingYDistance = (int) pMouseY - cornerY;
                return true;
            }
            return false;
        }

        @Override
        public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
            super.resize(pMinecraft, pWidth, pHeight);
            updatePos(pWidth / 3, pHeight / 2);
        }

        @Override
        public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (isDragging()) {
                updatePos(pMouseX - draggingXDistance, pMouseY - draggingYDistance);
            }

            this.renderBackground(pPoseStack);
            editBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            entryRenderer.render(this, pPoseStack, objectPos.getY(), objectPos.getX());
            ScreenUtil.drawWidgetTooltips(this, pPoseStack, pMouseX, pMouseY);
        }

        @Override
        public void renderBackground(PoseStack pPoseStack) {
            pPoseStack.pushPose();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            Screen.blit(pPoseStack, cornerX, cornerY, getBlitOffset(), 0, 0, 176, 60, 256, 256);
            pPoseStack.popPose();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, pPoseStack));
        }

        @Override
        public Rect2i getPosition() {
            return new Rect2i(cornerX, cornerY, WIDTH, HEIGHT);
        }
    }

    @Override
    protected void constructEntries(List<Item> entries) {
        if (!original.added().isEmpty()) {
            entries.add(addedObjectsItem);
            original.added().forEach(tag -> entries.add(new TagItem(tag, true)));
        }
        if (!original.removed().isEmpty()) {
            entries.add(removedObjectsItem);
            original.removed().forEach(tag -> entries.add(new TagItem(tag, false)));
        }
    }

    protected void removeEntry(boolean added, TagItem item) {
        removeEntry(item);
        if (added) {
            this.newData.added().remove(item.entry);
            if (this.newData.added().isEmpty())
                removeEntry(addedObjectsItem);
        } else {
            this.newData.removed().remove(item.entry);
            if (this.newData.removed().isEmpty())
                removeEntry(removedObjectsItem);
        }
    }

    protected void addEntry(boolean added, TagEntry entry) {
        final var item = new TagItem(entry, added);
        if (added) {
            final var objIndex = entries.indexOf(addedObjectsItem);
            if (objIndex == -1) {
                addEntry(addedObjectsItem);
                addEntry(item);
            } else {
                addEntry(objIndex + 1, item);
            }
            this.newData.added().add(entry);
        } else {
            final var objIndex = entries.indexOf(removedObjectsItem);
            if (objIndex == -1) {
                addEntry(removedObjectsItem);
                addEntry(item);
            } else {
                addEntry(objIndex + 1, item);
            }
            this.newData.removed().add(entry);
        }
    }

    protected boolean hasValue(boolean added, String name) {
        if (added)
            return this.newData.added().stream().anyMatch(e -> e.id().equals(name));
        else
            return this.newData.removed().stream().anyMatch(e -> e.id().equals(name));
    }

    public class TagItem extends Item {
        private final TagEntry entry;
        private final Button deleteButton;
        @Nullable
        private final Button optionalButton;
        private final int buttonCount;

        private final TagEntryRenderer<T> entryRenderer;

        public TagItem(TagEntry entry, boolean added) {
            super(new TextComponent(entry.id()));
            this.entry = entry;
            this.entryRenderer = new TagEntryRenderer<>(renderer, registry);
            entryRenderer.updateValue(entry.id());
            tooltip = entryRenderer.getTooltip();
            buttonCount = entry.required() ? 1 : 2; // We need the delete button as well as the "optional" one if the tag is not required
            deleteButton = addChildren(new Button(
                    0, 0,
                    renderer.getItemWidth(),
                    renderer.getItemHeight(),
                    TextComponent.EMPTY,
                    pButton -> removeEntry(added, this),
                    ScreenUtil.createButtonTooltip(EditTagScreen.this, Translations.DELETE.create(), 120)
            ) {
                @Override
                public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
                    ScreenUtil.renderIcon(
                            pPoseStack,
                            x, y,
                            EditTagScreen.this.getBlitOffset(),
                            0, 0,
                            renderer
                    );
                }
            });
            if (entry.required())
                optionalButton = null;
            else
                optionalButton = addChildren(new Button(
                        0, 0,
                        renderer.getItemWidth(),
                        renderer.getItemHeight(),
                        TextComponent.EMPTY,
                        b -> {
                        },
                        ScreenUtil.createButtonTooltip(EditTagScreen.this, Translations.OPTIONAL_ENTRY.create(), 120)
                ) {
                    @Override
                    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
                        pPoseStack.pushPose();
                        pPoseStack.scale(renderer.getItemWidth() / 16, renderer.getItemHeight() / 16, 1.0f);
                        RenderSystem.setShaderTexture(0, ICONS);
                        Screen.blit(pPoseStack, x, y, EditTagScreen.this.getBlitOffset(), 16, 0, 16, 16, 256, 256);
                        pPoseStack.popPose();
                    }

                    @Override
                    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
                        return false;
                    }
                });

        }

        @Override
        public void render(@NotNull PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int width, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            final var trimmedLabel = this.getTrimmedLabel(width - renderer.getItemWidth() * (buttonCount + 1) - 5);
            font.draw(pPoseStack, trimmedLabel,
                    pLeft + renderer.getItemWidth() + 4, pTop + (renderer.getItemHeight() - font.lineHeight) / 2, 0xFFFFFF);
            entryRenderer.render(EditTagScreen.this, pPoseStack, pTop, pLeft);
            this.deleteButton.x = pLeft + width - renderer.getItemWidth() - 3;
            this.deleteButton.y = pTop;
            deleteButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            if (optionalButton != null) {
                this.optionalButton.x = pLeft + width - 2 * renderer.getItemWidth() - 6;
                this.optionalButton.y = pTop;
                this.optionalButton.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }
        }

        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        private Component getTrimmedLabel(int maxWidth) {
            if (font.width(this.label) > maxWidth) {
                return new TextComponent(font.substrByWidth(this.label, maxWidth).getString() + "...");
            }
            return this.label;
        }

    }

    public enum ActionType {
        ADDITION(Translations.NEW_ADDITION), REMOVAL(Translations.NEW_REMOVAL);
        private final Translations translations;

        ActionType(Translations translations) {
            this.translations = translations;
        }

        public Component createComponent() {
            return translations.create();
        }
    }
}
