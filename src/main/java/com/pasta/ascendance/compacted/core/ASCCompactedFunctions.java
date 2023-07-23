package com.pasta.ascendance.compacted.core;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.compacted.blocks.entities.CompactedDimBlockEntity;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ASCCompactedFunctions {
    public static final int CUBE_RADIUS = 8;

    public static final int MAX_ROW_ID = 1800000;



    public static void generateCubeBoundaries(ServerLevel compactedDim, BlockPos center, Block blockToFill, int id) {
        // Get the start and end coordinates of the cube
        BlockPos startPos = center.offset(-CUBE_RADIUS, -CUBE_RADIUS, -CUBE_RADIUS);
        BlockPos endPos = center.offset(CUBE_RADIUS, CUBE_RADIUS, CUBE_RADIUS);
        preGenerateChunks(compactedDim, center, 2);

        // Iterate over each block position in the cube
        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {
                    boolean isBoundaryX = (x == startPos.getX() || x == endPos.getX());
                    boolean isBoundaryY = (y == startPos.getY() || y == endPos.getY());
                    boolean isBoundaryZ = (z == startPos.getZ() || z == endPos.getZ());

                    // Exclude corner blocks
                    boolean isCorner = ((x == startPos.getX() || x == endPos.getX()) &&
                            (y == startPos.getY() || y == endPos.getY())) ||
                            ((x == startPos.getX() || x == endPos.getX()) &&
                                    (z == startPos.getZ() || z == endPos.getZ())) ||
                            ((z == startPos.getZ() || z == endPos.getZ()) &&
                                    (y == startPos.getY() || y == endPos.getY()));

                    // Only place a block if it's a boundary block and not a corner block
                    if ((isBoundaryX || isBoundaryY || isBoundaryZ) && !isCorner) {
                        compactedDim.setBlockAndUpdate(new BlockPos(x, y, z), blockToFill.defaultBlockState());
                        BlockEntity newBe = compactedDim.getBlockEntity(new BlockPos(x, y, z));
                        if (newBe instanceof CompactedDimBlockEntity cde) {
                            cde.setOverworldId(id);
                            Direction direction;
                            if (x == startPos.getX()) direction = Direction.WEST;
                            else if (x == endPos.getX()) direction = Direction.EAST;
                            else if (y == startPos.getY()) direction = Direction.DOWN;
                            else if (y == endPos.getY()) direction = Direction.UP;
                            else if (z == startPos.getZ()) direction = Direction.NORTH;
                            else direction = Direction.SOUTH;
                            // Assign a slot ID based on this block's position within the direction's grid
                            int slotId = direction.get3DDataValue() * 196 + ((y - startPos.getY()) * 14 + (z - startPos.getZ()));
                            cde.setSlotId(slotId);
                        }
                    }
                }
            }
        }
    }

    public static void preGenerateChunks(ServerLevel targetDimension, BlockPos centerPos, int radiusInChunks) {
        ChunkSource chunkManager = targetDimension.getChunkSource();
        for (int x = centerPos.getX() - radiusInChunks; x <= centerPos.getX() + radiusInChunks; x++) {
            for (int z = centerPos.getZ() - radiusInChunks; z <= centerPos.getZ() + radiusInChunks; z++) {
                chunkManager.getChunk(x, z, ChunkStatus.FULL, true);
            }
        }
    }

    public static void updateChunks(ServerLevel targetDimension, BlockPos centerPos, int radiusInChunks) {
        ChunkSource chunkManager = targetDimension.getChunkSource();
        ChunkPos centerChunkPos = new ChunkPos(centerPos);
        for (int x = centerChunkPos.x - radiusInChunks; x <= centerChunkPos.x + radiusInChunks; x++) {
            for (int z = centerChunkPos.z - radiusInChunks; z <= centerChunkPos.z + radiusInChunks; z++) {
                chunkManager.getChunk(x, z, ChunkStatus.FULL, true);
                targetDimension.setChunkForced(x, z, true);
            }
        }
    }

    public static BlockPos getValidBoxPos(int id, boolean update){
        int row_num = id/MAX_ROW_ID > 0 ? id/MAX_ROW_ID : 1;
        int column_num = row_num > 1 ? id-(MAX_ROW_ID*(row_num-1)) : id;
        int vert = 72;

        Vec3 vec = new Vec3(row_num*32, vert, column_num*32);
        BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
        if (update){
            Ascendance.LOGGER.info("Getting valid box pos for ID: " + id);
        }

        return pos;

    }


    //thx the AE2 repo for this code
    public static void visit(Vec3 tppos, ServerLevel dimension, Player player){

        PortalInfo portalInfo = new PortalInfo((tppos), Vec3.ZERO, player.getYRot(),
                player.getXRot());
        player.changeDimension(dimension, new ITeleporter() {
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
