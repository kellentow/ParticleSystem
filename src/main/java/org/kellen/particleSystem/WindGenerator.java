package org.kellen.particleSystem;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class WindGenerator extends ParticleGenerator {

    private Vector wind;

    public WindGenerator(ParticleBase nparent, Player player, int particleCount) {
        super(nparent, ((Entity) player).getWorld(), 0F, 0F, 0F);
        marker = (Entity) player;
        wind = new Vector(1, 1, 1);

        for (int i = 0; particleCount >= i; i++) {
            CustomParticle part = new CustomParticle(this, ((Entity) player).getWorld(), x, y, z);
            part.vx = random.nextFloat(-0.1F, 0.1F);
            part.vy = random.nextFloat(-0.1F, 0.1F);
            part.vz = random.nextFloat(-0.1F, 0.1F);
            part.age = random.nextInt(0, 15);
            children.add(part);
        }
    }

    public static WindGenerator fromMap(Map<String, Object> map) {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        Location center = marker.getLocation();
        wind = ParticleSystem.getPlugin(ParticleSystem.class).getWindVector();

        if (!marker.isTicking()) {
            return;
        }
        float dx, dz, len;

        for (Object part : children) {
            if (part instanceof CustomParticle customParticle) {

                dx = (float) (center.x() - customParticle.x);
                dz = (float) (center.z() - customParticle.z);
                len = (float) Math.sqrt(dx * dx + dz * dz);

                customParticle.tick();

                // Particle reset logic
                if (len > 15) {
                    customParticle.x = x + random.nextFloat(-15, 15);
                    customParticle.y = y + random.nextFloat(-15, 15);
                    customParticle.z = z + random.nextFloat(-15, 15);

                    customParticle.vx = (float) wind.getX();
                    customParticle.vy = (float) wind.getY();
                    customParticle.vz = (float) wind.getZ();
                }
            }
        }
    }

    @Override
    public void draw() {
        int s, a;
        s = 200;
        a = 150;
        for (Object part : children) {
            if (part instanceof CustomParticle customParticle) {
                customParticle.draw(s, s, s, a, 0.66F);
            }
        }
    }
}
