package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InfectionCapabilityC2SPacket {


    public final int change;

    public InfectionCapabilityC2SPacket(int change) {
        this.change = change;

    }

    public InfectionCapabilityC2SPacket(FriendlyByteBuf buf) {
        this.change = buf.readInt();

    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(change);
    }



    public static boolean handle(InfectionCapabilityC2SPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Run this on the main server thread
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(infection -> {
                    infection.changeInfection(packet.change);
                    ASCServerSideHandler.sendToPlayer(new InfectionCapabilityDataSyncS2CPacket(infection.getInfection()), player);
                    if (infection.getInfection() == infection.getMAX_INFECTION()){
                        ASCFunctions.SpearAttack(player, "nanites");
                        infection.changeInfection(-infection.getInfection());
                    }
                });

            }
        });
        ctx.get().setPacketHandled(true);
        return true;

    }
}
