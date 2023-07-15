package com.pasta.ascendance.core.reggers;

import com.pasta.ascendance.particles.NarrativeErasureParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import static com.pasta.ascendance.Ascendance.LOGGER;

public class ParticleFactoryRegger {

    public static void register(){
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        engine.register(ParticleRegger.NARRATIVE_ERASURE_PARTICLE.get(), NarrativeErasureParticle.Provider::new);
        LOGGER.info("Registered factories!");
    }
}
