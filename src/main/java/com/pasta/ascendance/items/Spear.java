package com.pasta.ascendance.items;

import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.ParticleRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.pasta.ascendance.core.ASCFunctions.rayTrace;

public class Spear extends SwordItem {

    private final Random rand = new Random();
    public Spear(Properties properties){

        super(ASCTiers.MERALIUM_TIER, Integer.MAX_VALUE, Integer.MAX_VALUE,  properties);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.literal("§5The Overweapon"));

        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        BlockHitResult ray = rayTrace(level, player, ClipContext.Fluid.NONE);
        BlockPos pos = ray.getBlockPos();

        Vec3 start = player.getEyePosition();


        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        Vec3 direction = end.subtract(start).normalize();
        double step = 10;


        for (double i = 0; i < start.distanceTo(end); i += step) {
            Vec3 currentPos = start.add(direction.scale(i));
            BlockPos currentBlockPos = new BlockPos(currentPos);
            double boxSize = 20.0D;
            AABB box = new AABB(currentBlockPos).inflate(boxSize);


            List<Entity> entities = level.getEntitiesOfClass(Entity.class, box);


            for (Entity entity : entities) {
                if (entity != player){
                    if (entity instanceof LivingEntity livingEntity){
                        AABB bb = entity.getBoundingBox();
                        double width = bb.maxX - bb.minX;
                        double height = bb.maxY - bb.minY;
                        double depth = bb.maxZ - bb.minZ;
                        double volume = width * height * depth * 50;
                        double speed = (1.0/10)/30;
                        if (volume < 50){
                            volume *= 100;
                        }
                        if (volume >= 2050){
                            volume /= 50;
                        }
                        for(int j = 0; j < volume*10; j++){
                            if (rand.nextFloat()>0.2){
                                double x = bb.minX + rand.nextDouble() * (bb.maxX - bb.minX);
                                double y = bb.minY + rand.nextDouble() * (bb.maxY - bb.minY);
                                double z = bb.minZ + rand.nextDouble() * (bb.maxZ - bb.minZ);
                                level.addParticle(ParticleRegger.NARRATIVE_ERASURE_PARTICLE.get(),
                                        x, y,z,
                                        (rand.nextFloat() * 2 - 1) * speed,
                                        (rand.nextFloat() * 2 - 1) * speed,
                                        (rand.nextFloat() * 2 - 1) * speed);

                            }
                        }
                        ASCFunctions.SpearAttack(entity, "spear");
                    } else if (entity instanceof ItemEntity) {
                    }
                    else {
                        entity.discard();
                    }
                }
            }
        }

        return super.use(level, player, hand);
    }

}
