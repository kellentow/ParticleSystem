package org.kellen.particleSystem;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

public class ParticleGenerator extends ParticleBase{
    public Entity marker;
    public World world;
    public float x,y,z;

    public ParticleGenerator(ParticleBase nparent, World nworld, float nx, float ny, float nz) {
        super(nparent);
        this.x = nx;
        this.y = ny;
        this.z = nz;
        this.world = nworld;
        Location location = new Location(world,x,y,z);
        this.marker = world.spawn(location,TextDisplay.class);
        marker.setInvisible(true);
        marker.setInvulnerable(true);
        marker.setGravity(false);
        marker.setNoPhysics(true);
    }

    public Map<String, Object> toMap() {
        Map <String, Object> map = new HashMap<>();
        map.put("type","base");
        map.put("x",x);
        map.put("y",y);
        map.put("z",z);
        return map;
    }

    public static ParticleGenerator fromMap(Map<String,Object> map,ParticleBase parent) {
        ParticleSystem plugin = ParticleSystem.getPlugin(ParticleSystem.class);
        float x,y,z;
        x = Float.parseFloat((String) map.get("x"));
        y = Float.parseFloat((String) map.get("y"));
        z = Float.parseFloat((String) map.get("z"));
        return new ParticleGenerator(parent,plugin.getServer().getWorld((String) map.get("world")),x,y,z);
    }

    public void tick() {
        if (marker.isDead()) {
            for (Object part:children) {
                if (part instanceof CustomParticle customParticle) {
                    customParticle.destroy();
                }
            }
        } else if (!marker.isValid()){
            return;
        }
        x = (float) marker.getX();
        y = (float) marker.getY();
        z = (float) marker.getZ();
    }

    public void draw() {
        for (Object part:children) {
            if (part instanceof CustomParticle customParticle) {
                customParticle.draw(255,255,255,255,1F);
            }
        }
    }
}
