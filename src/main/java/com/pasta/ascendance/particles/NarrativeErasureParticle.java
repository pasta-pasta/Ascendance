package com.pasta.ascendance.particles;

import com.pasta.ascendance.Ascendance;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class NarrativeErasureParticle extends TextureSheetParticle {

    final Random rand = new Random();
    protected NarrativeErasureParticle(ClientLevel level, double xCoord, double yCoord, double zCoord,
                                       SpriteSet spriteSet, double dx, double dy, double dz) {
        super(level, xCoord, yCoord, zCoord, dx, dy, dz);

        this.friction = 0.8F;
        this.xd = dx;
        this.yd = dy;
        this.zd = dz;
        this.quadSize *= 1.0F;
        this.lifetime = (int) (20 + rand.nextFloat() * (45-20));
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
        if (this.zd == 0 || this.yd == 0 || this.xd == 0){
            this.zd += this.zd == 0 ? 1 : 0;
            this.yd += this.yd == 0 ? 1 : 0;
            this.xd += this.xd == 0 ? 1 : 0;
        }
        if (this.age >= this.lifetime-8){
            this.zd *= 5;
            this.yd *= 5;
            this.xd *= 5;
        }
    }

    private void fadeOut(){
        this.alpha = (-(1/(float)lifetime)*age + rand.nextFloat());
        this.quadSize *= 0.99;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet sprites;

        public Provider(SpriteSet spriteSet){
            this.sprites = spriteSet;
        }

        public Particle createParticle(SimpleParticleType particleType, ClientLevel level,double x, double y, double z,
                                        double dx, double dy, double dz){
            Ascendance.LOGGER.info("Spawned particle!");
            return new NarrativeErasureParticle(level, x, y, z, this.sprites, dx, dy, dz);
        }

    }
}
