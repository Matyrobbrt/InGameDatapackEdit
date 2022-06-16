package com.matyrobbrt.igde;

import static com.matyrobbrt.igde.InGameDatapackEdit.MOD_ID;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import com.matyrobbrt.igde.api.IGDEApi;
import com.matyrobbrt.igde.network.IGDENetwork;
import com.matyrobbrt.igde.network.message.tag.OpenScreenPacket;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class IGDECommands {
    static void registerCommands(final RegisterCommandsEvent event) {
        final ResourceKey<Registry<Registry<?>>> rootReg = ResourceKey.createRegistryKey(new ResourceLocation("root"));
        event.getDispatcher().register(Commands.literal(MOD_ID)
                .then(literal("tag")
                        .requires(cs -> cs.hasPermission(2))
                        .then(Commands.argument("registry", ResourceKeyArgument.key(rootReg))
                                .suggests(IGDECommands::suggestRegistries)
                                .then(argument("tag", ResourceLocationArgument.id())
                                        .executes(ctx -> {
                                            final var sender = ctx.getSource().getPlayerOrException();
                                            IGDENetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender),
                                                    OpenScreenPacket.make(
                                                            ctx.getSource().getServer(), getResourceKey(ctx, "registry", rootReg)
                                                                    .orElseThrow(),
                                                            ResourceLocationArgument.getId(ctx, "tag")
                                                    ));
                                            return Command.SINGLE_SUCCESS;
                                        })))));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T> Optional<ResourceKey<T>> getResourceKey(final CommandContext<CommandSourceStack> ctx,
                                                               final String name,
                                                               final ResourceKey<Registry<T>> registryKey)
    {
        // Don't inline to avoid an unchecked cast warning due to raw types
        final ResourceKey<?> key = ctx.getArgument(name, ResourceKey.class);
        return key.cast(registryKey);
    }

    private static CompletableFuture<Suggestions> suggestRegistries(final CommandContext<CommandSourceStack> ctx,
                                                                    final SuggestionsBuilder builder)
    {
        SharedSuggestionProvider.suggestResource(
                IGDEApi.INSTANCE.getAllData().keySet().stream().map(ResourceKey::location),
                builder
        );
        return builder.buildFuture();
    }
}
