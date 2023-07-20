package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.blocks.entities.NaniteDamagerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.function.Supplier;

public class NaniteDamagerSyncS2CPacket {


    public final int change;
    public final BlockPos pos;
    private ResourceKey<Level> dim;


    public NaniteDamagerSyncS2CPacket(int change, BlockPos pos, ResourceKey<Level> dim) {
        this.change = change;
        this.pos = pos;
        this.dim = dim;

    }

    public NaniteDamagerSyncS2CPacket(FriendlyByteBuf buf) {
        this.change = buf.readInt();
        this.pos = buf.readBlockPos();
        this.dim = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(change);
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(dim.location());
    }



    public static boolean handle(NaniteDamagerSyncS2CPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Run this on the main server thread
        ctx.get().enqueueWork(() -> {

            if (Minecraft.getInstance().level.getBlockEntity(packet.pos) instanceof NaniteDamagerEntity entity){
                entity.getInventory().getStackInSlot(0).setDamageValue(packet.change);
            }
                });
        return true;

    }
}
