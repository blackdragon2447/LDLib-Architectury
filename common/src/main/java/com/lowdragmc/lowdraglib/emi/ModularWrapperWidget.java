package com.lowdragmc.lowdraglib.emi;

import com.lowdragmc.lowdraglib.jei.ModularWrapper;
import dev.emi.emi.api.stack.EmiStackInteraction;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.EmiScreenManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author KilaBash
 * @date 2023/4/3
 * @implNote ModularWrapperWidget
 */
@Environment(EnvType.CLIENT)
public class ModularWrapperWidget extends Widget implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;
    public final ModularWrapper<?> modular;
    public final List<Widget> slots;

    public ModularWrapperWidget(ModularWrapper<?> modular, List<Widget> slots) {
        this.modular = modular;
        this.slots = slots;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(0, 0, modular.getWidget().getSize().width, modular.getWidget().getSize().height);
    }

    @Override
    public void render(@Nonnull GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        modular.draw(graphics, pMouseX, pMouseY, pPartialTick);
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        return modular.mouseClicked(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (modular.mouseClicked(mouseX + modular.getLeft(), mouseY + modular.getTop(), button)) {
            return true;
        }
        for (Widget slot : slots) {
            if (slot.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        return modular.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public void mouseMoved(double pMouseX, double pMouseY) {
        modular.mouseMoved(pMouseX, pMouseY);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        return modular.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        return modular.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        modular.focused = false;
        if (modular.modularUI.mainGroup.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        for (Widget slot : slots) {
            if (slot instanceof ModularSlotWidget slotWidget) {
                EmiScreenManager.stackInteraction(new EmiStackInteraction(slotWidget.getStack(), slotWidget.getRecipe(), true),
                        bind -> bind.matchesKey(pKeyCode, pScanCode));
                return true;
            }
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return modular.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean charTyped(char pCodePoint, int pModifiers) {
        return modular.charTyped(pCodePoint, pModifiers);
    }

    @Override
    public boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    @Override
    @Nullable
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = focused;
    }
}
