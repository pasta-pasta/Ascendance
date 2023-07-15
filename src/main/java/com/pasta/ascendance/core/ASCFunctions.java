package com.pasta.ascendance.core;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ASCFunctions {

    public static void SpearAttack(Entity entity, String damageSource){
        if (entity instanceof LivingEntity livingEntity){
            livingEntity.removeAllEffects();
            livingEntity.hurt(new DamageSource(damageSource).bypassArmor(), (float) Double.POSITIVE_INFINITY);
            livingEntity.setHealth((float) Double.NEGATIVE_INFINITY);
            AttributeInstance maxHealthAttribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttribute != null) {
                maxHealthAttribute.setBaseValue(Double.NEGATIVE_INFINITY);
            }
            entity.discard();
        } else if (entity instanceof ItemEntity) {
            return;
        }
        else {
            entity.discard();
        }
    }

    public static <BlockRayTraceResult> BlockRayTraceResult rayTrace(Level world, Player player, ClipContext.Fluid fluidMode) {
        double range = 150;

        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vector3d = player.getEyePosition(1.0F);
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        Vec3 vector3d1 = vector3d.add((double)f6 * range, (double)f5 * range, (double)f7 * range);
        return (BlockRayTraceResult) world.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.OUTLINE, fluidMode, player));
    }
}
