package com.pasta.ascendance.items;

import com.pasta.ascendance.core.ASCTab;
import com.pasta.ascendance.core.reggers.ItemRegger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.item.GeoArmorItem;

public class MeraliumArmorGecko extends GeoArmorItem implements IAnimatable {

    private final AnimationFactory factory = new AnimationFactory(this);
    public MeraliumArmorGecko(ArmorMaterial material, EquipmentSlot slot) {
        super(material, slot, new Properties().tab(ASCTab.instance));
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<MeraliumArmorGecko>(this, "controller", 20, this::predicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    private <P extends IAnimatable>PlayState predicate(AnimationEvent<P> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", true));
        return PlayState.CONTINUE;
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


                if (maxHealthAttribute != null) {
                    maxHealthAttribute.setBaseValue(maxHealthAttribute.getAttribute().getDefaultValue() * 4);
                    player.setHealth((float) maxHealthAttribute.getValue());
                }
                if (flyingSpeedAttribute != null) {
                    flyingSpeedAttribute.setBaseValue(1.2F);
                }
                if (walkingSpeedAttribute != null) {
                    //walkingSpeedAttribute.setBaseValue(walkingSpeedAttribute.getAttribute().getDefaultValue() * 1.5);
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
