package com.matyrobbrt.igde.client.screen;

import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.util.Translations;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public final class TagEntryRenderer<T> {
    private final ObjectRenderer<T> renderer;
    private final Registry<T> registry;

    private String value;
    private List<FormattedCharSequence> tooltip = List.of();

    @Nullable
    private TagKey<T> tag;

    private List<T> tagValues;
    private int currentTagValue;
    private int renderTicks;

    public TagEntryRenderer(ObjectRenderer<T> objectRenderer, Registry<T> registry) {
        this.renderer = objectRenderer;
        this.registry = registry;
    }

    public void updateValue(@Nullable String valueIn) {
        if (valueIn == null) {
            value = null;
            return;
        }
        this.value = valueIn.trim().toLowerCase(Locale.ROOT);
        if (value.startsWith("#")) {
            try {
                this.tag = TagKey.create(registry.key(), new ResourceLocation(value.substring(1)));
                tagValues = registry.getOrCreateTag(tag)
                        .stream().map(Holder::value).toList();
                tooltip = List.of(
                        Translations.TAG_HOLDS_VALUES.create(tagValues.size()).getVisualOrderText()
                );
            } catch (ResourceLocationException ignored) {}
        } else {
            tag = null;
            tagValues = List.of();
        }
    }

    public List<FormattedCharSequence> getTooltip() {
        return tooltip;
    }

    public void render(Screen screen, @NotNull PoseStack pPoseStack, int pTop, int pLeft) {
        renderTicks++;
        renderer.renderBackground(screen, pPoseStack, pLeft, pTop);
        if (this.tag != null) {
            if (tagValues == null || tagValues.isEmpty()) {
                renderer.renderUnknownObject(this.tag.location(), true, screen, pPoseStack, pLeft, pTop);
            } else {
                final var object = tagValues.get(currentTagValue);
                renderer.render(object, screen, pPoseStack, pLeft, pTop);
                if (renderTicks % 60 == 0 && !Screen.hasShiftDown()) {
                    currentTagValue++;
                    if (tagValues.size() <= currentTagValue)
                        currentTagValue = 0;
                    renderTicks = 0;
                }
            }
        } else {
            if (value != null) {
                try {
                    final var location = new ResourceLocation(value);
                    final var object = getValue(location);
                    if (object != null) {
                        renderer.render(object, screen, pPoseStack, pLeft, pTop);
                    } else {
                        renderer.renderUnknownObject(location, false, screen, pPoseStack, pLeft, pTop);
                    }
                } catch (ResourceLocationException ignored) {
                    renderer.renderUnknownObject(null, false, screen, pPoseStack, pLeft, pTop);
                }
            }
        }
    }

    public boolean isValid() {
        return (tag != null && !tagValues.isEmpty()) || (value != null && getValue(new ResourceLocation(value)) != null);
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @Nullable
    public T getValue(ResourceLocation id) {
        final var value = registry.get(id);
        if (registry instanceof DefaultedRegistry<T> def) {
            if (def.getDefaultKey().equals(id))
                return null;
            else if (def.get(def.getDefaultKey()) == value)
                return null;
        }
        return value;
    }
}
