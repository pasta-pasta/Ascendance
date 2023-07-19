package com.pasta.ascendance.blocks.colonies;

import com.pasta.ascendance.core.reggers.BlockRegger;
import com.pasta.ascendance.core.reggers.TagRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class NanoColony extends Block {

    public static final boolean isStable = true;

    public NanoColony(Block.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide || this.isStable) return; // Only do this on the server side

        // All the possible directions a block can be relative to this one
        Direction[] directions = Direction.values();

        // Pick a random direction
        Direction randomDirection = directions[random.nextInt(directions.length)];

        // Get the position of the block in that direction
        BlockPos targetPos = pos.relative(randomDirection);

        // Check if the block at the target position isn't a blocker block
        BlockState targetState = level.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();

        if (canReplace(level, targetPos)) {
            // If it's not a blocker block, replace it with a copy of this block
            level.setBlock(targetPos, state, 3);
        }
    }

    public boolean canReplace(ServerLevel level, BlockPos pos){
        return !ForgeRegistries.BLOCKS.tags().getTag(TagRegger.naniteBlockers).contains(level.getBlockState(pos).getBlock());
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // Makes sure this block ticks randomly
    }


}
