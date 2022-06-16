package com.matyrobbrt.igde.client.screen;

import static com.matyrobbrt.igde.client.screen.EditTagScreen.ICONS;
import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.client.screen.base.LabelProvider;
import com.matyrobbrt.igde.util.Translations;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class ScreenUtil {

    public static boolean isMouseWithin(int x, int y, int width, int height, int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static Button.OnTooltip createButtonTooltip(Screen screen, Component message, int maxWidth) {
        return createButtonTooltip(screen, message, maxWidth, button -> button.active && button.isHoveredOrFocused());
    }

    public static Button.OnTooltip createButtonTooltip(Screen screen, Component message, int maxWidth, Predicate<Button> predicate) {
        return createButtonTooltip(screen, message, maxWidth, predicate, null);
    }

    public static Button.OnTooltip createButtonTooltip(Screen screen, Component message, int maxWidth, Predicate<Button> predicate, @Nullable Component orElse) {
        return (button, poseStack, mouseX, mouseY) -> {
            if (predicate.test(button)) {
                screen.renderTooltip(poseStack, Minecraft.getInstance().font.split(message, maxWidth), mouseX, mouseY);
            } else if (orElse != null) {
                screen.renderTooltip(poseStack, Minecraft.getInstance().font.split(orElse, maxWidth), mouseX, mouseY);
            }
        };
    }

    public static void updateSearchTextFieldSuggestion(EditBox editBox, String value, List<? extends LabelProvider> entries) {
        if (!value.isEmpty()) {
            Optional<? extends LabelProvider> optional = entries.stream().filter(info -> info.getLabel().toLowerCase(Locale.ROOT).startsWith(value.toLowerCase(Locale.ROOT))).min(Comparator.comparing(LabelProvider::getLabel));
            if (optional.isPresent()) {
                int length = value.length();
                String displayName = optional.get().getLabel();
                editBox.setSuggestion(displayName.substring(length));
            } else {
                editBox.setSuggestion("");
            }
        } else {
            editBox.setSuggestion(Translations.SEARCH.create().getString());
        }
    }

    public static void renderIcon(PoseStack poseStack, int x, int y, int blitOffset, int u, int v, ObjectRenderer<?> renderer) {
        poseStack.pushPose();
        poseStack.scale(renderer.getItemWidth() / 16, renderer.getItemHeight() / 16, 1.0f);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, ICONS);
        Screen.blit(poseStack, x, y, blitOffset, u, v, 16, 16, 256, 256);
        poseStack.popPose();
    }

    public static void drawWidgetTooltips(List<? extends GuiEventListener> children, Screen screen, PoseStack poseStack, int mouseX, int mouseY) {
        for (GuiEventListener widget : children) {
            if (widget instanceof AbstractWidget abstractWidget && abstractWidget.isHoveredOrFocused()) {
                if (abstractWidget instanceof TooltipAccessor tooltipAccessor) {
                    screen.renderTooltip(poseStack, tooltipAccessor.getTooltip(), mouseX, mouseY);
                } else {
                    abstractWidget.renderToolTip(poseStack, mouseX, mouseY);
                }
                break;
            }
        }
    }

    public static void drawWidgetTooltips(Screen screen, PoseStack poseStack, int mouseX, int mouseY) {
        drawWidgetTooltips(screen.children(), screen, poseStack, mouseX, mouseY);
    }

}
