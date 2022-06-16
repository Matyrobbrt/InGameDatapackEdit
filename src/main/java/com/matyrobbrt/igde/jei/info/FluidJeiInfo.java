package com.matyrobbrt.igde.jei.info;

import com.matyrobbrt.igde.api.jei.JeiInfo;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidJeiInfo implements JeiInfo<Fluid, Object> {
    @Override
    public @NotNull Class<Object> getIngredientClass() {
        return Object.class;
    }

    @Override
    public @Nullable Fluid convert(Object ingredient) {
        if (ingredient instanceof FluidStack fStack) {
            return fStack.getFluid();
        } else if (ingredient instanceof ItemStack stack) {
            final var capOpt = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
            return capOpt.filter(handler -> handler.getTanks() > 0)
                    .map(cap -> cap.getFluidInTank(0).getFluid())
                    .orElse(null);
        }
        return null;
    }

    @Override
    public boolean isValid(Object ingredient) {
        if (ingredient instanceof FluidStack)
            return true;
        else if (ingredient instanceof ItemStack stack)
            return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
        return false;
    }
}
