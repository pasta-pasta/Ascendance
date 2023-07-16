package com.pasta.ascendance.items;
import com.pasta.ascendance.core.reggers.TagRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import static com.pasta.ascendance.core.ASCFunctions.rayTrace;

public class NaniteKiller extends Item{

    public NaniteKiller(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockHitResult ray = rayTrace(level, player, ClipContext.Fluid.NONE);
        BlockPos pos = ray.getBlockPos();
        BlockState originalState = level.getBlockState(pos);

        if(!level.isClientSide && ForgeRegistries.BLOCKS.tags().getTag(TagRegger.coloniesTag).contains(originalState.getBlock())) {

            Set<BlockPos> visited = new HashSet<>();

            Deque<BlockPos> stack = new ArrayDeque<>();
            stack.push(pos);

            while(!stack.isEmpty()) {
                BlockPos currentPos = stack.pop();
                BlockState currentState = level.getBlockState(currentPos);

                if(currentState.getBlock() == originalState.getBlock() && !visited.contains(currentPos)) {
                    level.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
                    visited.add(currentPos);


                    for(Direction direction : Direction.values()) {
                        stack.push(currentPos.relative(direction));
                    }
                }
            }

        }


        return super.use(level, player, hand);
    }
}
