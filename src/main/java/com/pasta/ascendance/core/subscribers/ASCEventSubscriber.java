package com.pasta.ascendance.core.subscribers;

import com.pasta.ascendance.Ascendance;
import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfectionProvider;
import com.pasta.ascendance.client.armor.MeraliumArmorRenderer;
import com.pasta.ascendance.client.hud.InfectionHudOverlay;
import com.pasta.ascendance.core.ASCFunctions;
import com.pasta.ascendance.core.reggers.ItemRegger;
import com.pasta.ascendance.core.reggers.TagRegger;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.server.packets.InfectionCapabilityDataSyncS2CPacket;
import com.pasta.ascendance.items.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
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
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "ascendance")
public class ASCEventSubscriber {
    @SubscribeEvent
    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        LivingEntity livingEntity = event.getEntity();

        // Check if the entity is a player
        if (livingEntity instanceof Player player) {
            Item item = event.getFrom().getItem();
            if (item instanceof MeraliumArmorGecko) {
                if (!MeraliumArmorGecko.checkFullSet(player)) {
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
            if (MeraliumArmorItem.checkFullSet(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamageMeralium(LivingDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (MeraliumArmorItem.checkFullSet(player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamageGuardCurio(LivingDamageEvent event) {

        if (event.getEntity() instanceof Player player) {

            if (ASCFunctions.hasCurioItem(player, ItemRegger.GUARD_INJECTION.get())) {
                player.getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(inf -> {
                    int infection = inf.getInfection();
                    event.setAmount(event.getAmount()*((float) 10 /infection));
                } );
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamageAggressiveCurio(LivingDamageEvent event) {

        if (event.getSource().getEntity() instanceof Player player) {
            int countInjection = ASCFunctions.countCurioItem(player, ItemRegger.AGGRESSIVE_INJECTION.get());
            if (countInjection > 0) {
                player.getCapability(PlayerNaniteInfectionProvider.PLAYER_INFECTION).ifPresent(inf -> {
                    int infection = inf.getInfection();
                    event.setAmount(event.getAmount()*((float) infection /10)*countInjection);
                } );

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

        @SubscribeEvent
        public static void registerArmorRenderers(final EntityRenderersEvent.AddLayers event){
            GeoArmorRenderer.registerArmorRenderer(MeraliumArmorGecko.class, new MeraliumArmorRenderer());
        }

    }

}
