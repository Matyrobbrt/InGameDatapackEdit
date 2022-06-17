package com.matyrobbrt.igde.network;

import com.matyrobbrt.igde.InGameDatapackEdit;
import com.matyrobbrt.igde.network.message.tag.OpenScreenPacket;
import com.matyrobbrt.igde.network.message.tag.UpdateTagPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class IGDENetwork {
    public static final EventNetworkChannel EXISTENCE_CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(InGameDatapackEdit.MOD_ID, "existence"))
            .networkProtocolVersion(() -> "no.")
            .serverAcceptedVersions(e -> true)
            .clientAcceptedVersions(e -> true)
            .eventNetworkChannel();

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(InGameDatapackEdit.MOD_ID, "channel"))
            .networkProtocolVersion(() -> "yes")
            .serverAcceptedVersions(e -> true)
            .clientAcceptedVersions(e -> true)
            .simpleChannel();

    public static final List<PacketData<?>> PACKETS = List.of(
            new PacketData<>(OpenScreenPacket.class, OpenScreenPacket::decode),
            new PacketData<>(UpdateTagPacket.class, UpdateTagPacket::decode)
    );

    public static void register() {
        int i = 0;
        for (final var data : PACKETS) {
            data.register(CHANNEL, i++);
        }
    }

    public static void sendToServer(Packet packet) {
        CHANNEL.sendToServer(packet);
    }

    record PacketData<T extends Packet>(Class<T> clazz, Function<FriendlyByteBuf, T> decoder, @Nullable NetworkDirection direction) {
        public PacketData(Class<T> clazz, Function<FriendlyByteBuf, T> decoder) {
            this(clazz, decoder, null);
        }

        public void register(SimpleChannel channel, int id) {
            CHANNEL.messageBuilder(clazz(), id, direction)
                    .consumer((pkt, context) -> {
                        context.get().enqueueWork(() -> pkt.handle(Packet.Context.wrap(context.get())));
                        return true;
                    })
                    .decoder(decoder())
                    .encoder(Packet::encode)
                    .add();
        }
    }
}
