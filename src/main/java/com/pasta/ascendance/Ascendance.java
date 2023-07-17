package com.pasta.ascendance;

import com.pasta.ascendance.capabilities.nanites.infection.PlayerNaniteInfection;
import com.pasta.ascendance.containers.NanoInjectorScreen;
import com.pasta.ascendance.core.server.ASCServerSideHandler;
import com.pasta.ascendance.core.subscribers.ASCEventSubscriber;
import com.pasta.ascendance.core.reggers.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod("ascendance")
public class Ascendance {
    public static final Logger LOGGER = LogManager.getLogger();


    public static final String MOD_ID = "ascendance";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Ascendance.MOD_ID);


    public Ascendance () {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerParticleFactories);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::textureStitch);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegisterCapabilities);
        modEventBus.addListener(this::setup);





        ItemRegger.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ASCEventSubscriber());
        BlockRegger.BLOCKS.register(modEventBus);
        FeatureRegger.CONFIGURED_FEATURES.register(modEventBus);
        FeatureRegger.PLACED_FEATURES.register(modEventBus);
        BlockEntityRegger.BLOCK_ENTITIES.register(modEventBus);
        MenuRegger.MENUS.register(modEventBus);
        RecipesRegger.SERIALIZERS.register(modEventBus);
        ParticleRegger.register(modEventBus);

        Ascendance.LOGGER.info("Ascendance on-line! Who's onto me?!");
    }



    private void setup(final FMLCommonSetupEvent event) {

    }

    private void textureStitch(final TextureStitchEvent.Pre event){
        event.addSprite(new ResourceLocation(Ascendance.MOD_ID, "items/curio_icon"));

    }

    private void enqueueIMC(final InterModEnqueueEvent event){
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.CURIO.getMessageBuilder()
                        .size(4)
                        .icon(new ResourceLocation(Ascendance.MOD_ID, "items/curio_icon"))
                        .build());

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(MenuRegger.NANOINJECTOR_MENU.get(), NanoInjectorScreen::new);
        ASCServerSideHandler.register();
    }

    public void registerParticleFactories(final RegisterParticleProvidersEvent event) {
        ParticleFactoryRegger.register();
    }

    public void onRegisterCapabilities(RegisterCapabilitiesEvent event){
        event.register(PlayerNaniteInfection.class);
    }





 
}
