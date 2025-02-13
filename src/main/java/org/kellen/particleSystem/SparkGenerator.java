package org.kellen.particleSystem;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class SparkGenerator extends ParticleGenerator {

    public SparkGenerator(ParticleBase nparent, World world, float x, float y, float z) {
        super(nparent, world, x, y, z);

        children.add(new CustomParticle(this, world, x, y, z));
    }

    @Override
    public void tick() {
        super.tick();

        if (!marker.isTicking()) {
            return;
        }

        for (Object part : children) {
            if (part instanceof CustomParticle customParticle) {
                boolean kill = false;
                float newx = customParticle.x + customParticle.vx;
                float newy = customParticle.y + customParticle.vy;
                float newz = customParticle.z + customParticle.vz;

                BoundingBox bounding = new BoundingBox(
                        newx - 0.3f / 2,
                        newy,
                        newz - 0.3f / 2,
                        newx + 0.3f / 2,
                        newy + 0.3f,
                        newz + 0.3f / 2);

                int blockX = (int) Math.floor(newx);
                int checkY = (int) Math.floor(newy + 1);  // Ensure we're checking the exact block above
                int blockZ = (int) Math.floor(newz);

                if (world.getBlockAt(blockX, checkY, blockZ).getCollisionShape().overlaps(bounding)) {
                    kill = true;
                }

                customParticle.tick();

                // Particle reset logic
                if (kill) {
                    customParticle.x = x;
                    customParticle.y = y;
                    customParticle.z = z;

                    // Give more noticeable initial velocity
                    customParticle.vx = (random.nextFloat() - 0.5F) * 0.2F;
                    customParticle.vy = (random.nextFloat() - 0.5F) * 0.2F;
                    customParticle.vz = (random.nextFloat() - 0.5F) * 0.2F;
                }
            }
        }
    }
}
