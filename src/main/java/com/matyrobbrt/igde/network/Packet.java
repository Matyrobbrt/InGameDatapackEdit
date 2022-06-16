package com.matyrobbrt.igde.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public interface Packet {
    void handle(Context context);
    void encode(FriendlyByteBuf buf);

    interface Context {
        static Context wrap(NetworkEvent.Context context) {
            return new Context() {
                @Override
                public @Nullable ServerPlayer getSender() {
                    return context.getSender();
                }

                @Override
                public void reply(Packet packet) {
                    if (getSender() != null) {
                        IGDENetwork.CHANNEL.send(PacketDistributor.PLAYER.with(this::getSender), packet);
                    }
                }
            };
        }

        @Nullable ServerPlayer getSender();
        void reply(Packet packet);
    }
}
