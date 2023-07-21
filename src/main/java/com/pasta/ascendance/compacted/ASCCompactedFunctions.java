package com.pasta.ascendance.compacted;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.DimensionRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
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

    public static void generateCubeBoundaries(ServerLevel compactedDim, BlockPos center, Block blockToFill) {
        // Get the start and end coordinates of the cube
        BlockPos startPos = center.offset(-CUBE_RADIUS, -CUBE_RADIUS, -CUBE_RADIUS);
        BlockPos endPos = center.offset(CUBE_RADIUS, CUBE_RADIUS, CUBE_RADIUS);
        preGenerateChunks(compactedDim, center, 2);

        // Iterate over each block position in the cube
        for (int x = startPos.getX(); x <= endPos.getX(); x++) {
            for (int y = startPos.getY(); y <= endPos.getY(); y++) {
                for (int z = startPos.getZ(); z <= endPos.getZ(); z++) {
                    // Check if the block is on the boundary of the cube
                    if (x == startPos.getX() || x == endPos.getX() ||
                            y == startPos.getY() || y == endPos.getY() ||
                            z == startPos.getZ() || z == endPos.getZ()) {
                        // Set the block at this position to the given block
                        compactedDim.setBlockAndUpdate(new BlockPos(x, y, z), blockToFill.defaultBlockState());
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

    public static BlockPos getValidBoxPos(int id){
        int row_num = id/MAX_ROW_ID > 0 ? id/MAX_ROW_ID : 1;
        int column_num = row_num > 1 ? id-(MAX_ROW_ID*(row_num-1)) : id;
        int vert = 72;

        Vec3 vec = new Vec3(row_num*32, vert, column_num*32);
        BlockPos pos = new BlockPos(vec.x, vec.y, vec.z);
        Ascendance.LOGGER.info("Getting valid compact dimension from ID: " + id);
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
