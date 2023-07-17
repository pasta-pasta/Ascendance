package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.blocks.entities.NanoInjectorEntity;
import com.pasta.ascendance.containers.NanoInjectorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FuelSyncS2CPacket {


    private final int fuel;
    private final BlockPos pos;

    public FuelSyncS2CPacket(int energy, BlockPos pos) {
        this.fuel = energy;
        this.pos = pos;
    }

    public FuelSyncS2CPacket(FriendlyByteBuf buf) {
        this.fuel = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(fuel);
        buf.writeBlockPos(pos);
    }



    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        ctx.get().enqueueWork(() -> {
            if(Minecraft.getInstance().level.getBlockEntity(pos) instanceof NanoInjectorEntity entity){
                entity.setFuel(fuel);

                if (Minecraft.getInstance().player.containerMenu instanceof NanoInjectorMenu menu
                    && menu.getBlockEntity().getBlockPos().equals(pos)){
                    entity.setFuel(fuel);
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;

    }
}
