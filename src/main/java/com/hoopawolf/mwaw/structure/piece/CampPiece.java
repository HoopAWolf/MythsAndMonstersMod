package com.hoopawolf.mwaw.structure.piece;

import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.StructureRegistryHandler;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class CampPiece
{
    public static final ResourceLocation HUNTER_CAMP_LOC = new ResourceLocation(Reference.MOD_ID, "huntercamp");

    public static void start(TemplateManager templateManager, BlockPos pos, Rotation rotation, List<StructurePiece> pieceList, Random random)
    {
        int x = pos.getX();
        int z = pos.getZ();

        //This is how we factor in rotation for multi-piece structures.
        //
        //I would recommend using the OFFSET map above to have each piece at correct height relative of each other
        //and keep the X and Z equal to 0. And then in rotations, have the centermost piece have a rotation
        //of 0, 0, 0 and then have all other pieces' rotation be based off of the bottommost left corner of
        //that piece (the corner that is smallest in X and Z).
        //
        //Lots of trial and error may be needed to get this right for your structure.
        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
        BlockPos blockpos = rotationOffSet.add(x, pos.getY(), z);
        pieceList.add(new CampPiece.Piece(templateManager, HUNTER_CAMP_LOC, blockpos, rotation));
    }

    public static class Piece extends TemplateStructurePiece
    {
        private final ResourceLocation resourceLocation;
        private final Rotation rotation;
        private boolean isSpawned;

        public Piece(TemplateManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotationIn)
        {
            super(StructureRegistryHandler.HUNTER_CAMP_FEATURE, 0);
            this.resourceLocation = resourceLocationIn;
            BlockPos blockpos = new BlockPos(0, 1, 0);
            this.templatePosition = pos.add(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            this.rotation = rotationIn;
            this.setupPiece(templateManagerIn);
        }


        public Piece(TemplateManager templateManagerIn, CompoundNBT tagCompound)
        {
            super(StructureRegistryHandler.HUNTER_CAMP_FEATURE, tagCompound);
            this.resourceLocation = new ResourceLocation(tagCompound.getString("Template"));
            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
            this.isSpawned = tagCompound.getBoolean("Spawned");
            this.setupPiece(templateManagerIn);
        }


        private void setupPiece(TemplateManager templateManager)
        {
            Template template = templateManager.getTemplateDefaulted(this.resourceLocation);
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            this.setup(template, this.templatePosition, placementsettings);
        }

        @Override
        protected void readAdditional(CompoundNBT tagCompound)
        {
            super.readAdditional(tagCompound);
            tagCompound.putString("Template", this.resourceLocation.toString());
            tagCompound.putString("Rot", this.rotation.name());
            tagCompound.putBoolean("Spawned", this.isSpawned);
        }

        @Override
        protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb)
        {
        }

        @Override
        public boolean create(IWorld worldIn, ChunkGenerator<?> p_225577_2_, Random randomIn, MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos)
        {
            PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
            BlockPos blockpos = new BlockPos(0, 1, 0);
            this.templatePosition.add(Template.transformedBlockPos(placementsettings, new BlockPos(-blockpos.getX(), 0, -blockpos.getZ())));

            if (!isSpawned)
            {
                int i = randomIn.nextInt(2) + 1;

                for (int j = 0; j < i; ++j)
                {
                    int l = this.getXWithOffset(2, 2);
                    int i1 = this.getYWithOffset(1);
                    int k = this.getZWithOffset(2, 2);

                    if (structureBoundingBoxIn.isVecInside(new BlockPos(l, i1, k)) && !worldIn.getBlockState(new BlockPos(l, i1, k)).isSolid())
                    {
                        HunterEntity hunter = EntityRegistryHandler.HUNTER_ENTITY.get().create(worldIn.getWorld());
                        hunter.enablePersistence();
                        hunter.setLocationAndAngles((double) l + 0.5D, i1, (double) k + 0.5D, 0.0F, 0.0F);
                        hunter.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(l, i1, k)), SpawnReason.STRUCTURE, null, null);
                        worldIn.addEntity(hunter);
                    }
                }

                this.isSpawned = true;
            }

            return super.create(worldIn, p_225577_2_, randomIn, structureBoundingBoxIn, chunkPos);
        }
    }
}
