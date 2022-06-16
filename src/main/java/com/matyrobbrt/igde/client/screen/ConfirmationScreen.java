package com.matyrobbrt.igde.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConfirmationScreen extends Screen {
    private final Component message;
    private Component positiveText = CommonComponents.GUI_YES;
    private Component negativeText = CommonComponents.GUI_NO;

    public static void push(Component message) {
        Minecraft.getInstance().pushGuiLayer(new ConfirmationScreen(message));
    }

    public ConfirmationScreen(Component message) {
        super(message);
        this.message = message;
    }

    @Override
    protected void init() {
        List<FormattedCharSequence> lines = this.font.split(this.message, 300);
        int messageOffset = (lines.size() * (this.font.lineHeight + 2)) / 2;
        this.addRenderableWidget(new Button(this.width / 2 - 105, this.height / 2 + messageOffset, 100, 20, this.positiveText, button -> {
            this.minecraft.popGuiLayer();
            this.minecraft.popGuiLayer();
        }));
        this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 2 + messageOffset, 100, 20, this.negativeText, button -> this.minecraft.popGuiLayer()));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        List<FormattedCharSequence> lines = this.font.split(this.message, 300);
        for (int i = 0; i < lines.size(); i++) {
            int lineWidth = this.font.width(lines.get(i));
            this.font.draw(poseStack, lines.get(i), this.width / 2 - lineWidth / 2, this.height / 2 - 20 - (lines.size() * (this.font.lineHeight + 2)) / 2 + i * (this.font.lineHeight + 2), 0xFFFFFF);
        }
    }

    public void setPositiveText(Component positiveText) {
        this.positiveText = positiveText;
    }
    public void setNegativeText(Component negativeText) {
        this.negativeText = negativeText;
    }

}