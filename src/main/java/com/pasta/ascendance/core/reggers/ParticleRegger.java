package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.Ascendance;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ParticleRegger {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Ascendance.MOD_ID);

     public static final RegistryObject<SimpleParticleType> NARRATIVE_ERASURE_PARTICLE =
            PARTICLE_TYPES.register("narrative_erasure_particles", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus){
        Ascendance.LOGGER.info("[Ascendance] Registering particles...");
        PARTICLE_TYPES.register(eventBus);
    }
}
