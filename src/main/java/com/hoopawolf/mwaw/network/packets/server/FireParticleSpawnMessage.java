package com.hoopawolf.mwaw.network.packets.server;

import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.Vec3d;

public class FireParticleSpawnMessage extends MessageToServer
{
    private Vec3d targetCoordinates, targetSpeed;
    private int iteration;

    public FireParticleSpawnMessage(Vec3d i_targetCoordinates, Vec3d i_targetSpeed, int _iteration)
    {
        messageIsValid = true;
        messageType = 1;
        targetCoordinates = i_targetCoordinates;
        targetSpeed = i_targetSpeed;
        iteration = _iteration;
    }

    // for use by the message handler only.
    public FireParticleSpawnMessage()
    {
        messageIsValid = false;
    }

    public static FireParticleSpawnMessage decode(PacketBuffer buf)
    {
        int iterationAmount;
        double x;
        double y;
        double z;
        double speedx;
        double speedy;
        double speedz;

        try
        {
            iterationAmount = buf.readInt();
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
            speedx = buf.readDouble();
            speedy = buf.readDouble();
            speedz = buf.readDouble();

            // these methods may also be of use for your code:
            // for Itemstacks - ByteBufUtils.readItemStack()
            // for NBT tags ByteBufUtils.readTag();
            // for Strings: ByteBufUtils.readUTF8String();
            // NB that PacketBuffer is a derived class of ByteBuf

        } catch (IllegalArgumentException | IndexOutOfBoundsException e)
        {
            Reference.LOGGER.warn("Exception while reading FireParticleSpawnMessageToServer: " + e);
            return new FireParticleSpawnMessage();
        }

        return new FireParticleSpawnMessage(new Vec3d(x, y, z), new Vec3d(speedx, speedy, speedz), iterationAmount);
    }

    public Vec3d getTargetCoordinates()
    {
        return targetCoordinates;
    }

    public Vec3d getTargetSpeed()
    {
        return targetSpeed;
    }

    public int getIteration()
    {
        return iteration;
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        if (!messageIsValid) return;
        buf.writeInt(iteration);
        buf.writeDouble(targetCoordinates.x);
        buf.writeDouble(targetCoordinates.y);
        buf.writeDouble(targetCoordinates.z);
        buf.writeDouble(targetSpeed.z);
        buf.writeDouble(targetSpeed.z);
        buf.writeDouble(targetSpeed.z);
    }

    @Override
    public String toString()
    {
        return "FireParticleSpawnMessageToServer[targetCoordinates=" + targetCoordinates + "]";
    }
}
