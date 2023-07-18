package com.pasta.ascendance.blocks.colonies;

import com.pasta.ascendance.core.reggers.BlockRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ControllingColony extends NanoColony{
    public static final boolean isStable = false;

    public ControllingColony(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide || isStable) return; // Only do this on the server side

        tickAction(state,level,pos,random);

    }

    private void tickAction(BlockState state, ServerLevel level, BlockPos pos, RandomSource random){
        Direction[] directions = Direction.values();

        for(Direction dir : directions){
            BlockPos targetPos = pos.relative(dir);

            // Check if the block at the target position isn't a blocker block
            BlockState targetState = level.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (targetBlock != BlockRegger.GUARD_COLONY_BLOCK.get() && targetBlock != BlockRegger.NANITE_DOOR.get()) {
                // If it's not a blocker block, replace it with a copy of this block
                level.setBlock(targetPos, state, 3);
            }
        }

    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // Makes sure this block ticks randomly
    }
}
