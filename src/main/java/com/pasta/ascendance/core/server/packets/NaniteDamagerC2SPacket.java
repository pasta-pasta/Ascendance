package com.pasta.ascendance.core.server.packets;

import com.pasta.ascendance.blocks.entities.NaniteDamagerEntity;
import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class NaniteDamagerC2SPacket {


    public final int change;
    public final BlockPos pos;
    private ResourceKey<Level> dim;


    public NaniteDamagerC2SPacket(int change, BlockPos pos, ResourceKey<Level> dim) {
        this.change = change;
        this.pos = pos;
        this.dim = dim;

    }

    public NaniteDamagerC2SPacket(FriendlyByteBuf buf) {
        this.change = buf.readInt();
        this.pos = buf.readBlockPos();
        this.dim = ResourceKey.create(Registry.DIMENSION_REGISTRY, buf.readResourceLocation());
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeInt(change);
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(dim.location());
    }



    public static boolean handle(NaniteDamagerC2SPacket packet, Supplier<NetworkEvent.Context> ctx) {
        // Run this on the main server thread
        ctx.get().enqueueWork(() -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            BlockEntity entity = Objects.requireNonNull(server.getLevel(packet.dim)).getBlockEntity(packet.pos);
            if (entity instanceof NaniteDamagerEntity naniteDamager){
                naniteDamager.damage(naniteDamager, packet.change);
                ASCServerSideHandler.sendToClients(new NaniteDamagerSyncS2CPacket(naniteDamager.getInventory().getStackInSlot(0).getDamageValue(), packet.pos, packet.dim));
            }
                });
        return true;

    }
}
