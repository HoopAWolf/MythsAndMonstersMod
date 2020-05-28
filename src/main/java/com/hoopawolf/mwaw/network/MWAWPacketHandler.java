package com.hoopawolf.mwaw.network;

import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.network.packets.server.FireParticleSpawnMessage;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MWAWPacketHandler
{
    public static final MWAWPacketHandler packetHandler = new MWAWPacketHandler();
    public static SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Reference.MOD_ID, "mwaw_main_channel"))
            .clientAcceptedVersions(MessageHandlerOnClient::isThisProtocolAcceptedByClient)
            .serverAcceptedVersions(MessageHandlerOnServer::isThisProtocolAcceptedByServer)
            .networkProtocolVersion(() -> Reference.MESSAGE_PROTOCOL_VERSION)
            .simpleChannel();
    private static int id;

    public static void init()
    {
        id = 0;
        channel.messageBuilder(FireParticleSpawnMessage.class, id++).encoder(FireParticleSpawnMessage::encode).decoder(FireParticleSpawnMessage::decode).consumer(MessageHandlerOnServer::onMessageReceived).add();
        channel.messageBuilder(SpawnParticleMessage.class, id++).encoder(SpawnParticleMessage::encode).decoder(SpawnParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        channel.messageBuilder(SpawnOrbitingParticleMessage.class, id++).encoder(SpawnOrbitingParticleMessage::encode).decoder(SpawnOrbitingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        channel.messageBuilder(SpawnSuckingParticleMessage.class, id++).encoder(SpawnSuckingParticleMessage::encode).decoder(SpawnSuckingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
    }

    public void send(PacketTarget target, Object message)
    {
        channel.send(target, message);
    }

    public void sendToPlayer(ServerPlayerEntity player, Object message)
    {
        this.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public void sendToDimension(DimensionType dimension, Object message)
    {
        this.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public void sendToNearbyPlayers(double x, double y, double z, double radius, DimensionType dimension, Object message)
    {
        this.sendToNearbyPlayers(new TargetPoint(x, y, z, radius, dimension), message);
    }

    public void sendToNearbyPlayers(TargetPoint point, Object message)
    {
        this.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    public void sendToAllPlayers(Object message)
    {
        this.send(PacketDistributor.ALL.noArg(), message);
    }

    public void sendToChunk(Chunk chunk, Object message)
    {
        this.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }
}
