package com.pasta.ascendance.compacted.items;

import com.pasta.ascendance.compacted.blocks.entities.CompactedBlockEntity;
import com.pasta.ascendance.compacted.blocks.entities.CompactedDimBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SideStateChanger extends Item {
    public SideStateChanger(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();

        if (!world.isClientSide()) { // This logic should only be run on the server
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CompactedDimBlockEntity) {
                CompactedDimBlockEntity cbe = (CompactedDimBlockEntity) be;

                context.getPlayer().sendSystemMessage(Component.literal(cbe.cycleSideMode(side)));


                return InteractionResult.SUCCESS;
            }
            else if (be instanceof  CompactedBlockEntity){
                CompactedBlockEntity cbe = (CompactedBlockEntity) be;

                context.getPlayer().sendSystemMessage(Component.literal(cbe.cycleSideMode(side)));


                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS; // Fall back to default item usage if not on one of your blocks
    }
}

