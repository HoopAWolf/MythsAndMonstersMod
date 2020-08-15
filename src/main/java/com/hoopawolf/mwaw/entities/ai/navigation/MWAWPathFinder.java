package com.hoopawolf.mwaw.entities.ai.navigation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Region;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MWAWPathFinder extends PathFinder
{
    private final PathHeap path = new PathHeap();
    private final Set<PathPoint> closedSet = Sets.newHashSet();
    private final PathPoint[] pathOptions = new PathPoint[32];
    private final NodeProcessor nodeProcessor;
    private final int field_215751_d;

    public MWAWPathFinder(NodeProcessor processor, int p_i51280_2_)
    {
        super(processor, p_i51280_2_);
        this.nodeProcessor = processor;
        this.field_215751_d = p_i51280_2_;
    }

    @Override
    @Nullable
    public Path func_227478_a_(Region p_227478_1_, MobEntity p_227478_2_, Set<BlockPos> p_227478_3_, float p_227478_4_, int p_227478_5_, float p_227478_6_)
    {
        this.path.clearPath();
        this.nodeProcessor.func_225578_a_(p_227478_1_, p_227478_2_);
        PathPoint pathpoint = this.nodeProcessor.getStart();
        Map<FlaggedPathPoint, BlockPos> map = p_227478_3_.stream().collect(Collectors.toMap((p_224782_1_) ->
        {
            return this.nodeProcessor.func_224768_a(p_224782_1_.getX(), p_224782_1_.getY(), p_224782_1_.getZ());
        }, Function.identity()));
        Path path = this.findPath(pathpoint, map, p_227478_4_, p_227478_5_, p_227478_6_);
        this.nodeProcessor.postProcess();
        return path;
    }

    @Nullable
    private Path findPath(PathPoint p_227479_1_, Map<FlaggedPathPoint, BlockPos> p_227479_2_, float p_227479_3_, int p_227479_4_, float p_227479_5_)
    {
        Set<FlaggedPathPoint> set = p_227479_2_.keySet();
        p_227479_1_.totalPathDistance = 0.0F;
        p_227479_1_.distanceToNext = this.func_224776_a(p_227479_1_, set);
        p_227479_1_.distanceToTarget = p_227479_1_.distanceToNext;
        this.path.clearPath();
        this.closedSet.clear();
        this.path.addPoint(p_227479_1_);
        Set<FlaggedPathPoint> set2 = Sets.newHashSetWithExpectedSize(set.size());
        int i = 0;
        int j = (int) ((float) this.field_215751_d * p_227479_5_);

        while (!this.path.isPathEmpty())
        {
            ++i;
            if (i >= j)
            {
                break;
            }

            PathPoint pathpoint = this.path.dequeue();
            pathpoint.visited = true;
            for (FlaggedPathPoint flaggedpathpoint : set)
            {
                if (pathpoint.func_224757_c(flaggedpathpoint) <= (float) p_227479_4_)
                {
                    flaggedpathpoint.func_224764_e();
                    set2.add(flaggedpathpoint);
                }
            }

            if (!set2.isEmpty())
            {
                break;
            }

            if (!(pathpoint.distanceTo(p_227479_1_) >= p_227479_3_))
            {
                int k = this.nodeProcessor.func_222859_a(this.pathOptions, pathpoint);

                for (int l = 0; l < k; ++l)
                {
                    PathPoint pathpoint1 = this.pathOptions[l];
                    float f = pathpoint.distanceTo(pathpoint1);
                    pathpoint1.field_222861_j = pathpoint.field_222861_j + f;
                    float f1 = pathpoint.totalPathDistance + f + pathpoint1.costMalus;
                    if (pathpoint1.field_222861_j < p_227479_3_ && (!pathpoint1.isAssigned() || f1 < pathpoint1.totalPathDistance))
                    {
                        pathpoint1.previous = pathpoint;
                        pathpoint1.totalPathDistance = f1;
                        pathpoint1.distanceToNext = this.func_224776_a(pathpoint1, set) * 1.5F;
                        if (pathpoint1.isAssigned())
                        {
                            this.path.changeDistance(pathpoint1, pathpoint1.totalPathDistance + pathpoint1.distanceToNext);
                        } else
                        {
                            pathpoint1.distanceToTarget = pathpoint1.totalPathDistance + pathpoint1.distanceToNext;
                            this.path.addPoint(pathpoint1);
                        }
                    }
                }
            }
        }

        Optional<Path> optional = !set2.isEmpty() ? set2.stream().map((p_224778_2_) ->
        {
            return this.createPath(p_224778_2_.func_224763_d(), p_227479_2_.get(p_224778_2_), true);
        }).min(Comparator.comparingInt(Path::getCurrentPathLength)) : set.stream().map((p_224777_2_) ->
        {
            return this.createPath(p_224777_2_.func_224763_d(), p_227479_2_.get(p_224777_2_), false);
        }).min(Comparator.comparingDouble(Path::func_224769_l).thenComparingInt(Path::getCurrentPathLength));
        return !optional.isPresent() ? null : optional.get();
    }

    private float func_224776_a(PathPoint p_224776_1_, Set<FlaggedPathPoint> p_224776_2_)
    {
        float f = Float.MAX_VALUE;

        for (FlaggedPathPoint flaggedpathpoint : p_224776_2_)
        {
            float f1 = p_224776_1_.distanceTo(flaggedpathpoint);
            flaggedpathpoint.func_224761_a(f1, p_224776_1_);
            f = Math.min(f1, f);
        }

        return f;
    }

    private Path createPath(PathPoint p_224780_1_, BlockPos p_224780_2_, boolean p_224780_3_)
    {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint = p_224780_1_;
        list.add(0, p_224780_1_);

        while (pathpoint.previous != null)
        {
            pathpoint = pathpoint.previous;
            list.add(0, pathpoint);
        }

        return new PatchedPath(list, p_224780_2_, p_224780_3_);
    }

    static class PatchedPath extends Path
    {

        public PatchedPath(List<PathPoint> p_i51804_1_, BlockPos p_i51804_2_, boolean p_i51804_3_)
        {
            super(p_i51804_1_, p_i51804_2_, p_i51804_3_);
        }

        @Override
        public Vector3d getVectorFromIndex(Entity entity, int index)
        {
            PathPoint point = this.getPathPointFromIndex(index);
            double d0 = point.x + MathHelper.floor(entity.getWidth() + 1.0F) * 0.5D;
            double d1 = point.y;
            double d2 = point.z + MathHelper.floor(entity.getWidth() + 1.0F) * 0.5D;
            return new Vector3d(d0, d1, d2);
        }
    }
}
