package com.hoopawolf.mwaw.blocks;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;

public class FairyMushroomBlock extends MushroomBlock
{
    public FairyMushroomBlock(Block.Properties properties)
    {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        super.animateTick(stateIn, worldIn, pos, rand);

        if (worldIn.rand.nextInt(100) < 20)
        {
            int _iteration = worldIn.rand.nextInt(10);
            Vec3d _vec = new Vec3d(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3d(0, -0.1f, 0), _iteration, 0, 0.5F);
            MWAWPacketHandler.INSTANCE.send(PacketDistributor.DIMENSION.with(() -> worldIn.getDimension().getType()), spawnParticleMessage);
        }
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state)
    {
        return false;
    }

    public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.down();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (block == Blocks.DIRT || block == Blocks.GRASS_BLOCK)
        {
            return true;
        }

        return false;
    }
}
