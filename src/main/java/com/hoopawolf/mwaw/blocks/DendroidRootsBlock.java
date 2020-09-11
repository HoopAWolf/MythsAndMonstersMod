package com.hoopawolf.mwaw.blocks;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.Random;

public class DendroidRootsBlock extends BushBlock
{
    public DendroidRootsBlock(AbstractBlock.Properties properties)
    {
        super(properties);
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!(entityIn instanceof DendroidElderEntity) && !(entityIn instanceof DendroidEntity))
        {
            entityIn.attackEntityFrom(DamageSource.MAGIC, 2.0F);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        super.animateTick(stateIn, worldIn, pos, rand);

        if (worldIn.rand.nextInt(100) < 20)
        {
            int _iteration = worldIn.rand.nextInt(10);
            Vector3d _vec = new Vector3d(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vector3d(0, -0.1f, 0), _iteration, 8, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(worldIn.func_234923_W_(), spawnParticleMessage);
        }
    }

    @Override
    public AbstractBlock.OffsetType getOffsetType()
    {
        return AbstractBlock.OffsetType.XYZ;
    }
}