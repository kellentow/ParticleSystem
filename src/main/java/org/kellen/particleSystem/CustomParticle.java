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
// Main class for handling particles

public class CustomParticle extends ParticleBase {
    float x,y,z,vx,vy,vz;
    public int age;
    private World world;
    private final TextDisplay disp;

    public CustomParticle(ParticleBase nparent, World nworld,float nx,float ny,float nz) {
        super(nparent);
        this.x = nx;
        this.y = ny;
        this.z = nz;
        this.world = nworld;
        Location location = new Location(world,x,y,z);
        this.disp = world.spawn(location, TextDisplay.class);
        disp.text(Component.text("  "));
        disp.setDisplayHeight((float) disp.getWidth());
        disp.setBillboard(Display.Billboard.CENTER);
    }

    public void goTo(World nworld,float nx,float ny,float nz) {
        x = nx;
        y = ny;
        z = nz;
        world = nworld;
        disp.teleport(new Location(world,x,y,z));
    }

    public void destroy() {
        disp.remove();
    }

    public void addVel(float nx,float ny,float nz) {
        vx += nx;
        vy += ny;
        vz += nz;
    }

    public void tick() {
        x += vx;
        y += vy;
        z += vz;

        vx *= 0.99F;
        vy *= 0.99F;
        vz *= 0.99F;

        disp.teleport(new Location(world,x,y,z));
        age+=1;
    }

    public void draw(int r, int g, int b, int a, float scale) {
        Color color = Color.fromARGB(a, r, g, b);
        disp.setBackgroundColor(color);

        // Retrieve the current transformation
        Transformation current = disp.getTransformation();
        // Create a new scale vector; default is (1, 1, 1), so we set it to (scale, scale, scale)
        Vector3f newScale = new Vector3f(scale);
        Quaternionf zeroed = new Quaternionf(0,0,0,1);

        // Create and set the new transformation with the updated scale
        disp.setTransformation(new Transformation(current.getTranslation(),  zeroed, newScale, zeroed));

    }
}
