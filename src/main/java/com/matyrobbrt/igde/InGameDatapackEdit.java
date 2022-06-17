package com.matyrobbrt.igde;

import com.matyrobbrt.igde.api.IGDEApi;
import com.matyrobbrt.igde.api.RegistryData;
import com.matyrobbrt.igde.api.client.ObjectRenderer;
import com.matyrobbrt.igde.api.jei.JeiInfo;
import com.matyrobbrt.igde.client.renderer.FluidObjectRenderer;
import com.matyrobbrt.igde.client.renderer.ItemObjectRenderer;
import com.matyrobbrt.igde.jei.info.FluidJeiInfo;
import com.matyrobbrt.igde.jei.info.ItemJeiInfo;
import com.matyrobbrt.igde.network.IGDENetwork;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InGameDatapackEdit.MOD_ID)
@MethodsReturnNonnullByDefault
public class InGameDatapackEdit {

    public static final String MOD_ID = IGDEApi.MOD_ID;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean jeiLoaded;

    public InGameDatapackEdit() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

        MinecraftForge.EVENT_BUS.addListener(IGDECommands::registerCommands);

        final var bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener((final FMLCommonSetupEvent event) -> IGDENetwork.register());

        IGDEApi.INSTANCE.registerData(Registry.ITEM_REGISTRY, new RegistryData<>() {
            final LazyOptional<ObjectRenderer<Item>> renderer = LazyOptional.of(() -> new ItemObjectRenderer<>(Item::getDefaultInstance));
            final ItemJeiInfo<Item> jeiInfo = new ItemJeiInfo<>(ItemStack::getItem);

            @Override
            public ObjectRenderer<Item> getRenderer() {
                return renderer.orElseThrow(RuntimeException::new);
            }

            @Override
            public @NotNull JeiInfo<Item, ?> getJeiInfo() {
                return jeiInfo;
            }
        });
        IGDEApi.INSTANCE.registerData(Registry.BLOCK_REGISTRY, new RegistryData<>() {
            final LazyOptional<ObjectRenderer<Block>> renderer = LazyOptional.of(() -> new ItemObjectRenderer<Block>(b -> b.asItem().getDefaultInstance()));
            final ItemJeiInfo<Block> jeiInfo = new ItemJeiInfo<>(i -> Block.byItem(i.getItem())) {
                @Override
                public boolean isValid(ItemStack ingredient) {
                    return ingredient.getItem() instanceof BlockItem;
                }
            };

            @Override
            public ObjectRenderer<Block> getRenderer() {
                return renderer.orElseThrow(RuntimeException::new);
            }

            @Override
            public @NotNull JeiInfo<Block, ?> getJeiInfo() {
                return jeiInfo;
            }
        });
        IGDEApi.INSTANCE.registerData(Registry.FLUID_REGISTRY, new RegistryData<>() {
            final LazyOptional<ObjectRenderer<Fluid>> renderer = LazyOptional.of(FluidObjectRenderer::new);
            final FluidJeiInfo jeiInfo = new FluidJeiInfo();

            @Override
            public ObjectRenderer<Fluid> getRenderer() {
                return renderer.orElseThrow(RuntimeException::new);
            }

            @Override
            public @NotNull JeiInfo<Fluid, ?> getJeiInfo() {
                return jeiInfo;
            }
        });

        jeiLoaded = ModList.get().isLoaded("jei");
    }

}
