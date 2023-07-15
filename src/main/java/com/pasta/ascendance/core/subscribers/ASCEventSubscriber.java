package com.pasta.ascendance.core.subscribers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.client.hud.InfectionHudOverlay;
import com.pasta.ascendance.core.reggers.BlockRegger;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.reggers.ParticleRegger;
import com.pasta.ascendance.core.reggers.TagRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityDataSyncS2CPacket;
import com.pasta.ascendance.items.*;
import com.pasta.ascendance.particles.NarrativeErasureParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.pasta.ascendance.items.MeraliumChestplate;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.pasta.ascendance.Ascendance.LOGGER;

@Mod.EventBusSubscriber(modid = "ascendance")
public class ASCEventSubscriber {
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();

        // Check if the entity is a player
        if (livingEntity instanceof Player player) {
            Item item = event.getFrom().getItem();
            if (item instanceof MeraliumChestplate ||
                    item instanceof MeraliumBoots ||
                    item instanceof MeraliumLeggins ||
                    item instanceof MeraliumHelmet) {
                if (!MeraliumChestplate.checkFullSet(player)) {
                    LivingEntity entity = event.getEntity();
                    ServerPlayer serverPlayer = (ServerPlayer) player;

                    AttributeInstance maxHealthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
                    AttributeInstance flyingSpeedAttribute = player.getAttribute(Attributes.FLYING_SPEED);
                    AttributeInstance walkingSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);

                    if (maxHealthAttribute != null && maxHealthAttribute.getValue() > maxHealthAttribute.getAttribute().getDefaultValue()) {
                        maxHealthAttribute.setBaseValue(maxHealthAttribute.getAttribute().getDefaultValue());
                        entity.setHealth((float) maxHealthAttribute.getValue());
                    }
                    if (flyingSpeedAttribute != null){
                        flyingSpeedAttribute.setBaseValue(0.05F);
                    }
                    if (walkingSpeedAttribute != null){
                        walkingSpeedAttribute.setBaseValue(0.1F);
                    }
                    if (serverPlayer.gameMode.getGameModeForPlayer() != GameType.CREATIVE){
                        serverPlayer.getAbilities().mayfly = false;
                        serverPlayer.getAbilities().flying = false;
                        serverPlayer.getAbilities().invulnerable = false;
                        serverPlayer.onUpdateAbilities();
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public void onEntityAttacked(LivingAttackEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (MeraliumChestplate.checkFullSet(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (MeraliumChestplate.checkFullSet(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof Nanobreaker) {
            Block clickedBlock = event.getLevel().getBlockState(event.getPos()).getBlock();
            if (clickedBlock == Blocks.BEDROCK) {
                // Set the block to air
                player.level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3);

                // Create the item entity
                ItemEntity itemEntity = new ItemEntity(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new ItemStack(Blocks.BEDROCK));
                // Spawn the item entity
                event.getLevel().addFreshEntity(itemEntity);

                // Cancel the event so the default block breaking doesn't take place
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        ItemStack tool = event.getPlayer().getMainHandItem();

        if (tool.getItem() instanceof Nanobreaker) {
            ItemStack drop = new ItemStack(state.getBlock());
            Block.popResource((Level) event.getLevel(), event.getPos(), drop);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Block clickedBlock = event.getLevel().getBlockState(event.getPos()).getBlock();
        Player player = event.getEntity();
        Item heldItem = player.getMainHandItem().getItem();
        Random random = new Random();


        if (heldItem == ItemRegger.NANITE_EXTRACTOR.get() && ForgeRegistries.BLOCKS.tags().getTag(TagRegger.coloniesTag).contains(clickedBlock))
        {
            player.level.setBlock(event.getPos(), Blocks.AIR.defaultBlockState(), 3);

            ItemStack stack = player.getItemBySlot(EquipmentSlot.MAINHAND);
            stack.setDamageValue(stack.getDamageValue() + 1);
            if (stack.getDamageValue() >= stack.getMaxDamage()) stack.setCount(0);

            int randomAmount = random.nextInt(4)+1;
            ItemStack dropStack;
            switch (clickedBlock.getName().toString()) {
                case "translation{key='block.ascendance.nanocolony', args=[]}":
                    dropStack = new ItemStack(ItemRegger.SMALLCOLONY.get(), randomAmount);
                    break;
                case "translation{key='block.ascendance.guardcolonyblock', args=[]}":
                    dropStack = new ItemStack(ItemRegger.GUARD_COLONY.get(), randomAmount);
                    break;
                case "translation{key='block.ascendance.aggressivecolonyblock', args=[]}":
                    dropStack = new ItemStack(ItemRegger.AGGRESIVE_COLONY.get(), randomAmount);
                    break;
                default:
                    dropStack = ItemStack.EMPTY;
                    break;

            }

            if (!dropStack.isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(event.getLevel(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), dropStack);
                event.getLevel().addFreshEntity(itemEntity);
            }
        }

    }

    //Lower there are some utility and networking shit

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player){
            if (!event.getObject().getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).isPresent()){
                event.addCapability(new ResourceLocation(Ascendance.MOD_ID, "properties"), new PlayerNaniteInfectionProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        if(event.isWasDeath()){
            event.getOriginal().getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinWorld(EntityJoinLevelEvent event){
        if(!event.getLevel().isClientSide && event.getEntity() instanceof ServerPlayer player){
            player.getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(infection -> {
                ASCServerSideHandler.sendToPlayer(new InfectionCapabilityDataSyncS2CPacket(infection.getInfection()), player);
            });
        }
    }

    //Client subscribing shit

    @Mod.EventBusSubscriber(modid = Ascendance.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ASCClientEventSubscriber {

        @SubscribeEvent
        public static void registerGuiOverlays(RegisterGuiOverlaysEvent event){
            event.registerAboveAll("infection", InfectionHudOverlay.HUD_INFECTION);
        }

    }

}
