package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;
import java.util.function.Supplier;

public class InfectionCapabilityC2SPacket {


    public final int change;
    public final UUID player;

    public InfectionCapabilityC2SPacket(int change, UUID player) {
        this.change = change;
        this.player = player;
    }

    public InfectionCapabilityC2SPacket(FriendlyByteBuf buf) {
        this.change = buf.readInt();
        this.player = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(change);
        buf.writeUUID(player);
    }



    public static boolean handle(InfectionCapabilityC2SPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Run this on the main server thread
        ctx.get().enqueueWork(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            ServerPlayer player = server.getPlayerList().getPlayer(packet.player);
            if (player != null) {
                player.getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(infection -> {
                    infection.changeInfection(packet.change);
                    ASCServerSideHandler.sendToPlayer(new InfectionCapabilityDataSyncS2CPacket(infection.getInfection()), player);
                    if (infection.getInfection() == infection.getMAX_INFECTION()){
                        if (!ASCFunctions.hasCurioItem(player, ItemRegger.CONTROLLING_INJECTION.get())){
                            ASCFunctions.SpearAttack(player, "nanites");
                            infection.changeInfection(-infection.getInfection());
                            ASCServerSideHandler.sendToPlayer(new InfectionCapabilityDataSyncS2CPacket(infection.getInfection()), player);
                        }
                    }
                });
            }
        });
        return true;

    }
}
