package com.pasta.ascendance.core.server;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.server.packets.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ASCServerSideHandler {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id(){
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Ascendance.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(InfectionCapabilityC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(InfectionCapabilityC2SPacket::new)
                .encoder(InfectionCapabilityC2SPacket::toBytes)
                .consumerMainThread(InfectionCapabilityC2SPacket::handle)
                .add();

        net.messageBuilder(NaniteDamagerC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(NaniteDamagerC2SPacket::new)
                .encoder(NaniteDamagerC2SPacket::toBytes)
                .consumerMainThread(NaniteDamagerC2SPacket::handle)
                .add();

        net.messageBuilder(InfectionCapabilityDataSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(InfectionCapabilityDataSyncS2CPacket::new)
                .encoder(InfectionCapabilityDataSyncS2CPacket::toBytes)
                .consumerMainThread(InfectionCapabilityDataSyncS2CPacket::handle)
                .add();

        net.messageBuilder(EnergySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(EnergySyncS2CPacket::new)
                .encoder(EnergySyncS2CPacket::toBytes)
                .consumerMainThread(EnergySyncS2CPacket::handle)
                .add();

        net.messageBuilder(NaniteDamagerSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(NaniteDamagerSyncS2CPacket::new)
                .encoder(NaniteDamagerSyncS2CPacket::toBytes)
                .consumerMainThread(NaniteDamagerSyncS2CPacket::handle)
                .add();

        net.messageBuilder(FuelSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(FuelSyncS2CPacket::new)
                .encoder(FuelSyncS2CPacket::toBytes)
                .consumerMainThread(FuelSyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message){
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }

}
