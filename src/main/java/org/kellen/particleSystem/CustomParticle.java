package org.kellen.particleSystem;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.kyori.adventure.text.Component;

public class CustomParticle extends ParticleBase {
    public float x, y, z;
    public float vx, vy, vz;
    public int age;

    private final World world;
    private final TextDisplay disp;
    private final Vector3f position;
    private final Vector3f velocity = new Vector3f();
    private final Vector3f origin;

    public CustomParticle(ParticleBase nparent, World nworld, float nx, float ny, float nz) {
        super(nparent);
        this.world = nworld;
        this.x = nx;
        this.y = ny;
        this.z = nz;
        this.position = new Vector3f(nx, ny, nz);

        // Spawn at exact location
        Location location = new Location(world, x, y, z);
        this.disp = world.spawn(location, TextDisplay.class);
        disp.text(Component.text("  "));
        disp.setDisplayHeight((float) disp.getWidth());
        disp.setBillboard(Display.Billboard.VERTICAL);

        // Store the original spawn position
        this.origin = new Vector3f((float) location.getX(), (float) location.getY(), (float) location.getZ());

        updateTransform();
    }

    public void goTo(float nx, float ny, float nz) {
        x = nx;
        y = ny;
        z = nz;
        position.set(nx, ny, nz);
        updateTransform();
    }

    public void destroy() {
        disp.remove();
    }

    public void addVel(float nx, float ny, float nz) {
        vx += nx;
        vy += ny;
        vz += nz;
        velocity.add(nx, ny, nz);
    }

    public void tick() {
        x += vx;
        y += vy;
        z += vz;
        position.set(x, y, z);

        vx *= 0.99F;
        vy *= 0.99F;
        vz *= 0.99F;
        velocity.mul(0.99F);

        updateTransform();
        age++;
    }

    public void draw(int r, int g, int b, int a, float scale) {
        Color color = Color.fromARGB(a, r, g, b);
        disp.setBackgroundColor(color);

        Vector3f scaleVec = new Vector3f(scale);
        Quaternionf identity = new Quaternionf(0, 0, 0, 1);

        disp.setTransformation(new Transformation(position.sub(origin, new Vector3f()), identity, scaleVec, identity));
    }

    private void updateTransform() {
        Transformation current = disp.getTransformation();
        disp.setTransformation(new Transformation(position.sub(origin, new Vector3f()), current.getLeftRotation(), current.getScale(), current.getRightRotation()));
    }
}
