package com.hoopawolf.mwaw.network;

import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.network.packets.server.FireParticleSpawnMessage;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MWAWPacketHandler
{
    private static int id;
    public static SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Reference.MOD_ID, "mwaw_main_channel"))
            .clientAcceptedVersions(MessageHandlerOnClient::isThisProtocolAcceptedByClient)
            .serverAcceptedVersions(MessageHandlerOnServer::isThisProtocolAcceptedByServer)
            .networkProtocolVersion(() -> Reference.MESSAGE_PROTOCOL_VERSION)
            .simpleChannel();

    public static void init()
    {
        id = 0;
        INSTANCE.messageBuilder(FireParticleSpawnMessage.class, id++).encoder(FireParticleSpawnMessage::encode).decoder(FireParticleSpawnMessage::decode).consumer(MessageHandlerOnServer::onMessageReceived).add();
        INSTANCE.messageBuilder(SpawnParticleMessage.class, id++).encoder(SpawnParticleMessage::encode).decoder(SpawnParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        INSTANCE.messageBuilder(SpawnOrbitingParticleMessage.class, id++).encoder(SpawnOrbitingParticleMessage::encode).decoder(SpawnOrbitingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        INSTANCE.messageBuilder(SpawnSuckingParticleMessage.class, id++).encoder(SpawnSuckingParticleMessage::encode).decoder(SpawnSuckingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
    }
}
