package com.pasta.ascendance.items;

import com.pasta.ascendance.core.ASCTab;
import com.pasta.ascendance.core.reggers.ItemRegger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class MeraliumChestplate extends ArmorItem {

    private static final Map<Player, Vec3> lastPosition = new HashMap<>();

    public MeraliumChestplate(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Properties().tab(ASCTab.instance));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (slot == EquipmentSlot.LEGS) {
            return "ascendance:textures/models/armor/meralium_layer_2.png";
        } else {
            // This applies for the helmet, chestplate and boots
            return "ascendance:textures/models/armor/meralium_layer_1.png";
        }
    }

    public static boolean checkFullSet(Player player){
        return  player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ItemRegger.MERALIUM_HELMET.get() &&
                player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ItemRegger.MERALIUM_CHESTPLATE.get() &&
                player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ItemRegger.MERALIUM_LEGGINS.get() &&
                player.getItemBySlot(EquipmentSlot.FEET).getItem() == ItemRegger.MERALIUM_BOOTS.get();
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (checkFullSet(player)) {
            AttributeInstance flyingSpeedAttribute = null;
            AttributeInstance walkingSpeedAttribute = null;
            if (!world.isClientSide()) {
                AttributeInstance maxHealthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
                flyingSpeedAttribute = player.getAttribute(Attributes.FLYING_SPEED);
                walkingSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
                Vec3 playerPos = player.position();

                if (maxHealthAttribute != null) {
                    maxHealthAttribute.setBaseValue(maxHealthAttribute.getAttribute().getDefaultValue() * 4);
                    player.setHealth((float) maxHealthAttribute.getValue());
                }
                // if (player.getAbilities().flying){
                // if (!ASCKeySubscriber.isPlayerMoving()) {
                //     Vec3 lastPos = lastPosition.get(player);
                //    if (lastPos != null && (playerPos.x != lastPos.x || playerPos.y != lastPos.y || playerPos.z != lastPos.z)){
                //        ASCServerSideHandler.sendToServer(new IsMovingC2SPacket(lastPos));
                //      }
            //}
            //  else{
            //      lastPosition.put(player, player.position());
            //  }
            // }
                if (flyingSpeedAttribute != null) {
                    flyingSpeedAttribute.setBaseValue(1.2F);
                }
                if (walkingSpeedAttribute != null) {
                    walkingSpeedAttribute.setBaseValue(walkingSpeedAttribute.getAttribute().getDefaultValue() * 1.5);
                }


                if (player instanceof ServerPlayer serverPlayer) { // make sure it's a server-side player
                    serverPlayer.getAbilities().mayfly = true; // gives the player the ability to fly
                    serverPlayer.clearFire();
                    serverPlayer.getAbilities().invulnerable = true;
                    serverPlayer.onUpdateAbilities(); // sends the updated abilities to the client
                }

        }
        }
    }

}
