package com.pasta.ascendance.blocks.colonies;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.reggers.BlockRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class GuardColony extends NanoColony{
    public static boolean isStable = false;

    Random rand = new Random();

    public GuardColony(Block.Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.isClientSide || isStable) return; // Only do this on the server side

        if (rand.nextFloat()>0.95){
            Direction[] directions = Direction.values();


            Direction randomDirection = directions[random.nextInt(directions.length)];

            BlockPos targetPos = pos.relative(randomDirection);

            BlockState targetState = level.getBlockState(targetPos);
            Block targetBlock = targetState.getBlock();

            if (targetBlock != BlockRegger.GUARD_COLONY_BLOCK.get()) {

                level.setBlock(targetPos, state, 3);
            }
        }

    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true; // Makes sure this block ticks randomly
    }
}
