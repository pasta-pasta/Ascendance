package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.client.ClientInfectionData;
import com.pasta.ascendance.core.ASCFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InfectionCapabilityDataSyncS2CPacket {


    private final int infection;

    public InfectionCapabilityDataSyncS2CPacket(int infection) {
        this.infection = infection;
    }

    public InfectionCapabilityDataSyncS2CPacket(FriendlyByteBuf buf) {
        this.infection = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(infection);
    }



    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ctx.get().enqueueWork(() -> {
            ClientInfectionData.set(infection);
        });
        ctx.get().setPacketHandled(true);
        return true;

    }
}
