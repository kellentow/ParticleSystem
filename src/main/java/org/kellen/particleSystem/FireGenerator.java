package org.kellen.particleSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class FireGenerator extends ParticleGenerator {

    private final float scale,windmult;
    private final ArrayList<Color> colors = new ArrayList<>();
    private final boolean testCollisions;
    private final int palatte;
    private Vector wind = new Vector(0,0,0);

    public FireGenerator(ParticleBase nparent, World nworld, float nx, float ny, float nz, float nscale, int palateindex, boolean collide, float windscale) {
        super(nparent, nworld, nx, ny, nz);
        windmult = windscale;
        scale = nscale;

        testCollisions = collide;

        for (int i = 0; i < (int) (150 * scale); i++) {
            CustomParticle part = new CustomParticle(this, nworld, x, y, z);
            part.vx = random.nextFloat(-0.1F, 0.1F);
            part.vy = random.nextFloat(-0.1F, 0.1F);
            part.vz = random.nextFloat(-0.1F, 0.1F);
            part.age = random.nextInt(0, 30);
            children.add(part);
        }

        palatte = palateindex;

        switch (palateindex) {
            case 0 -> {
                colors.add(Color.WHITE);
                colors.add(Color.fromRGB(64, 188, 255));
                colors.add(Color.fromRGB(0, 17, 201));
                colors.add(Color.YELLOW);
                colors.add(Color.RED);
                colors.add(Color.BLACK);
                colors.add(Color.fromARGB(0, 0, 0, 0));
            }
            case 1 -> {
                colors.add(Color.WHITE);
                colors.add(Color.WHITE);
                colors.add(Color.YELLOW);
                colors.add(Color.YELLOW);
                colors.add(Color.RED);
                colors.add(Color.BLACK);
                colors.add(Color.fromARGB(0, 0, 0, 0));
            }
            case 2 -> {
                colors.add(Color.WHITE);
                colors.add(Color.WHITE);
                colors.add(Color.WHITE);
                colors.add(Color.BLACK);
                colors.add(Color.BLACK);
                colors.add(Color.BLACK);
                colors.add(Color.fromARGB(0, 0, 0, 0));
            }
            default -> {
            }
        }
    }

    public static float clamp(float val, float min, float max) {
        return Math.min(max, Math.max(min, val));
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "fire");
        map.put("world", world.getName());
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("scale", scale);
        map.put("palatte", palatte);
        map.put("collide", testCollisions);
        map.put("wind", windmult);
        return map;
    }

    public static FireGenerator fromMap(Map<String, Object> map, ParticleBase parent) {
        ParticleSystem plugin = ParticleSystem.getPlugin(ParticleSystem.class);
        float x, y, z, scale, wind;
        int palatte;
        boolean collide;
        x = ((Double) (map.get("x"))).floatValue();
        y = ((Double) (map.get("y"))).floatValue();
        z = ((Double) (map.get("z"))).floatValue();
        scale = ((Double) (map.get("scale"))).floatValue();
        palatte = ((Double) map.get("palatte")).intValue();
        collide = (Boolean) map.get("collide");
        wind = ((Double) (map.get("wind"))).floatValue();

        return new FireGenerator(parent, plugin.getServer().getWorld((String) map.get("world")), x, y, z, scale, palatte, collide,wind);
    }

    @Override
    public void tick() {
        super.tick();
        Location center = marker.getLocation();

        if (!marker.isTicking()) {
            return;
        }

        wind = ParticleSystem.getPlugin(ParticleSystem.class).getWindVector();

        for (Object part : children) {
            if (part instanceof CustomParticle customParticle) {
                float dx, dz, len, velX, velY, velZ;

                dx = (float) (center.x() - customParticle.x);
                dz = (float) (center.z() - customParticle.z);
                len = (float) Math.sqrt(dx * dx + dz * dz);

                if (len > 0) {
                    velX = (dx / len) * 0.05F+(float) wind.getX()*windmult*0.05f;  // Velocity toward center
                    velZ = (dz / len) * 0.05F+(float) wind.getZ()*windmult*0.05f;
                    velY = 0.01F             +(float) wind.getY()*windmult*0.05f;  // Fixed upward velocity

                    if (testCollisions) {
                        Vector newpos = new Vector(customParticle.x+customParticle.vx+velX,
                        customParticle.y+customParticle.vy+velY,
                        customParticle.z+customParticle.vz+velZ);

                        int blockX = newpos.getBlockX();
                        int blockY = newpos.getBlockY();
                        int blockZ = newpos.getBlockZ();

                        if (lib.Collides(newpos, world.getBlockAt(blockX, blockY, blockZ))) {
                            customParticle.addVel(-velX * 1.1F, (customParticle.vy+velY) * -1.05F, -velZ * 1.1F);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX+1, blockY, blockZ))) {
                            customParticle.addVel(-0.2f, (customParticle.vy+velY) * -1.05F, -velZ * 1.1F);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX, blockY+1, blockZ))) {
                            customParticle.addVel(-velX * 1.1F, (customParticle.vy+velY) * -1F -0.2f, -velZ * 1.1F);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX, blockY, blockZ+1))) {
                            customParticle.addVel(-velX * 1.1F, (customParticle.vy+velY) * -1.05F, -0.2f);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX-1, blockY, blockZ))) {
                            customParticle.addVel(0.2f, (customParticle.vy+velY) * -1.05F, -velZ * 1.1F);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX, blockY-1, blockZ))) {
                            customParticle.addVel(-velX * 1.1F, 0.2F, -velZ * 1.1F);
                            customParticle.age -= 1;
                        } else if (lib.Collides(newpos, world.getBlockAt(blockX, blockY, blockZ-1))) {
                            customParticle.addVel(-velX * 1.1F, (customParticle.vy+velY) * -1.05F, 0.2f);
                            customParticle.age -= 1;
                        }
                    }

                    customParticle.addVel(velX, velY, velZ);
                }

                customParticle.tick();

                // Particle reset logic
                if (customParticle.age > 15) {
                    customParticle.age = random.nextInt(0, 1);
                    // Add a slight random offset to prevent clustering
                    customParticle.x = x;
                    customParticle.y = y;
                    customParticle.z = z;

                    // Give more noticeable initial velocity
                    customParticle.vx = (random.nextFloat() - 0.5F) * 0.2F * scale;
                    customParticle.vy = (random.nextFloat() - 0.3F) * 0.2F * scale;
                    customParticle.vz = (random.nextFloat() - 0.5F) * 0.2F * scale;
                }
            }
        }
    }

    private static Color lerp(Color start, Color end, float t) {
        // Clamp t to ensure it stays between 0.0 and 1.0
        t = Math.max(0.0f, Math.min(1.0f, t));

        // Interpolate each color component (Red, Green, Blue)
        int red = (int) (start.getRed() + t * (end.getRed() - start.getRed()));
        int green = (int) (start.getGreen() + t * (end.getGreen() - start.getGreen()));
        int blue = (int) (start.getBlue() + t * (end.getBlue() - start.getBlue()));
        int alpha = (int) (start.getAlpha() + t * (end.getAlpha() - start.getAlpha()));

        // Return the resulting color
        return Color.fromARGB(alpha, red, green, blue);
    }

    private Color getPalate(int age) {
        float t = age / 15F;  // Normalized age value

        float[] stops = {0.0f, 0.1f, 0.3f, 0.5f, 0.7f, 0.8f, 1.0f, 2.0f};

        // Find the correct segment
        for (int i = 0; i < stops.length - 1; i++) {
            if (t < stops[i + 1]) {
                try {
                    float localT = (t - stops[i]) / (stops[i + 1] - stops[i]);  // Normalize within segment
                    return lerp(colors.get(i), colors.get(i + 1), localT);
                } catch (Exception ignore) {
                }
            }
        }
        return colors.getLast();  // Fallback
    }

    @Override
    public void draw() {
        int r, g, b, a;
        for (Object part : children) {
            if (part instanceof CustomParticle customParticle) {
                Color color = getPalate(customParticle.age);
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                a = color.getAlpha();
                customParticle.draw(
                        (int) clamp(r, 0, 255),
                        (int) clamp(g, 0, 255),
                        (int) clamp(b, 0, 255),
                        (int) clamp(a, 0, 255),
                        scale / 3F);
            }
        }
    }
}
