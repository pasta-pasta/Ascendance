package com.pasta.ascendance.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import static com.pasta.ascendance.core.ASCFunctions.rayTrace;

public class BlockstateChecker extends Item {

    public BlockstateChecker(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        BlockHitResult ray = rayTrace(level, player, ClipContext.Fluid.NONE);
        BlockPos pos = ray.getBlockPos();

        player.displayClientMessage(Component.translatable(level.getBlockState(pos).getBlock().getDescriptionId()), false);

        return super.use(level, player, pUsedHand);
    }
}
