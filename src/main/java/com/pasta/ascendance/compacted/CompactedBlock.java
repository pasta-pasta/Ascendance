package com.pasta.ascendance.compacted;


import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.blocks.entities.NaniteDamagerEntity;
import com.pasta.ascendance.core.reggers.BlockEntityRegger;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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


            // Convert the overworld coordinates to compacted dimension coordinates
            int compactedX = pos.getX() * 16;
            int compactedY = pos.getY() + 16;
            int compactedZ = pos.getZ() * 16;

            BlockPos compactedPos = new BlockPos(compactedX, compactedY, compactedZ);

            // Make sure we're in the compacted dimension
            ServerLevel compactedWorld = serverWorld.getServer().getLevel(DimensionRegger.ASCDIM_KEY);

            Ascendance.LOGGER.info("Creating compact dimension for block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
            if (compactedWorld != null) {
                ASCCompactedFunctions.generateCubeBoundaries(compactedWorld, compactedPos, Blocks.BEDROCK);
                Ascendance.LOGGER.info("Compact dimension created successfully!");
            }
            else{
                Ascendance.LOGGER.info("ServerLevel is null!");
            }
        }
        super.onPlace(state, worldIn, pos, oldState, isMoving);
    }

    public Vec3 getCompactedPos(BlockPos pos){
          return new Vec3 (pos.getX()*16, pos.getY()+16, pos.getZ()*16);
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
                ServerLevel serverWorld = (ServerLevel) level;
                ServerLevel compactedWorld = serverWorld.getServer().getLevel(DimensionRegger.ASCDIM_KEY);
                ASCCompactedFunctions.preGenerateChunks(compactedWorld, pos, 2);

                //thx the AE2 repo for this code
                PortalInfo portalInfo = new PortalInfo(getCompactedPos(pos), Vec3.ZERO, player.getYRot(),
                        player.getXRot());
                player.changeDimension(compactedWorld, new ITeleporter() {
                    @Override
                    public Entity placeEntity(Entity entity, ServerLevel currentLevel, ServerLevel destLevel, float yaw,
                                              Function<Boolean, Entity> repositionEntity) {
                        return repositionEntity.apply(false);
                    }

                    @Override
                    public PortalInfo getPortalInfo(Entity entity, ServerLevel destLevel,
                                                    Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                        return portalInfo;
                    }
                });
            }
        }

        return InteractionResult.SUCCESS;
    }
}
