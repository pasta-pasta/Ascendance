package com.pasta.ascendance.compacted;


import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.NaniteDamagerEntity;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.BlockRegger;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import com.pasta.ascendance.core.reggers.ItemRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class CompactedBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public CompactedBlock(Properties properties) {
        super(properties);
    }

    private int id;

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!worldIn.isClientSide()) {
            ServerLevel serverWorld = (ServerLevel) worldIn;
            IDManager idManager = IDManager.get(serverWorld);
            this.id = idManager.getNextID();
            BlockEntity be = worldIn.getBlockEntity(pos);
            if (be instanceof CompactedBlockEntity){
                ((CompactedBlockEntity) be).setId((CompactedBlockEntity) be, this.id);
            }



            // Make sure we're in the compacted dimension
            ServerLevel compactedWorld = serverWorld.getServer().getLevel(DimensionRegger.ASCDIM_KEY);

            Ascendance.LOGGER.info("Creating compact dimension for block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            if (compactedWorld != null) {
                ASCCompactedFunctions.generateCubeBoundaries(compactedWorld, ASCCompactedFunctions.getValidBoxPos(id), BlockRegger.COMPACTED_DIMBLOCK.get());
                Ascendance.LOGGER.info("Compact dimension created successfully!");
            }
            else{
                Ascendance.LOGGER.info("ServerLevel is null!");
            }
        }
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }



    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegger.COMPACTEDBLOCKENTITY.get().create(pos,state);
    }

    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type){
        return level.isClientSide() ? null : ($0, $1, $2, blockEntity) -> {
            if(blockEntity instanceof CompactedBlockEntity compactedBlockEntity){
                compactedBlockEntity.tick(level, compactedBlockEntity.getBlockPos(), state, compactedBlockEntity);
            }
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide){
            if(level.getBlockEntity(pos) instanceof CompactedBlockEntity compactedBlockEntity){
                if(player.getItemInHand(hand).getItem() == ItemRegger.COMPACT_TELEPORTER.get()){

                    ItemStack teleporter = player.getItemInHand(hand);

                    CompoundTag tag = teleporter.getOrCreateTag();
                    tag.putDouble("x", player.position().x);
                    tag.putDouble("y", player.position().y);
                    tag.putDouble("z", player.position().z);

                    ServerLevel serverWorld = (ServerLevel) level;
                    ServerLevel compactedWorld = serverWorld.getServer().getLevel(DimensionRegger.ASCDIM_KEY);
                    ASCCompactedFunctions.preGenerateChunks(compactedWorld, pos, 2);

                    BlockPos tpblockpos = new BlockPos(ASCCompactedFunctions.getValidBoxPos(compactedBlockEntity.getId(compactedBlockEntity)));
                    Vec3 tppos = new Vec3(tpblockpos.getX(), tpblockpos.getY(), tpblockpos.getZ());

                    ASCCompactedFunctions.visit(tppos, compactedWorld, player);
                }
            }

        }

        return InteractionResult.SUCCESS;
    }
}
