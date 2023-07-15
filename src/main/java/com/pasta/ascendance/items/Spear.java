package com.pasta.ascendance.items;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.ParticleRegger;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;


import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import static com.pasta.ascendance.core.ASCFunctions.rayTrace;

public class Spear extends SwordItem {

    private Random rand = new Random();
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

        double boxSize = 20.0D;
        AABB box = new AABB(pos).inflate(boxSize);


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
                    for(int i = 0; i < volume*10; i++){
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
        return super.use(level, player, hand);
    }

}
