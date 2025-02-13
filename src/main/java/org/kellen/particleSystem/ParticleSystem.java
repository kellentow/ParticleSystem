package org.kellen.particleSystem;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class ParticleSystem extends JavaPlugin {

    private Vector wind = new Vector(0,0,0);
    private final Random random = new Random();
    private final ParticleBase base = new ParticleBase(null);
    final ArrayList<ParticleGenerator> generators = new ArrayList<>();

    public Vector getWindVector() {
        if (random.nextFloat() < 0.01F) {
            float x,y,z;
            x = random.nextFloat(-0.01F, 0.01F);
            y = random.nextFloat(-0.01F, 0.01F);
            z = random.nextFloat(-0.01F, 0.01F);
            wind = wind.add(new Vector(x,y,z));
        }
        return wind;
    }

    public ParticleBase toParticleBase() {
        return base;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        int tickInterval = 1; // Runs every tick (20 ticks = 1 second)

        ParticlesCMD particlesCommand = new ParticlesCMD(this);
        PluginCommand cmdobj = getCommand("particles");
        if (cmdobj == null) {
            return;
        }
        cmdobj.setExecutor(particlesCommand);
        cmdobj.setTabCompleter(particlesCommand);

        Gson gson = new Gson();

        Type listType = new TypeToken<List<Map<String, Object>>>() {
        }.getType(); // List of maps with key as String and value as Person

        try (FileReader reader = new FileReader("data.json")) {
            List<Map<String, Object>> data = gson.fromJson(reader, listType);

            // Print the parsed data
            for (Map<String, Object> entry : data) {
                String type = (String) entry.getOrDefault("type", "");
                switch (type) {
                    case "fire" ->
                        generators.add(FireGenerator.fromMap(entry, base));
                    case "wind" ->
                        generators.add(WindGenerator.fromMap(entry, base));
                }

            }
        } catch (IOException ignored) {
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                ParticleGenerator toRemove = null;
                for (ParticleGenerator gen : generators) {
                    if (gen.marker.isDead()) {
                        toRemove = gen;
                    } else if (gen.marker.isValid()) {
                        gen.draw();
                        gen.tick();
                    }
                }
                if (toRemove != null) {
                    generators.remove(toRemove);
                    for (Object part : toRemove.children) {
                        if (part instanceof CustomParticle customParticle) {
                            customParticle.destroy();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, tickInterval);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void onDisable() {
        // Plugin shutdown logic
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ArrayList<Object> data = new ArrayList<>();
        for (ParticleGenerator gen : generators) {
            data.add(gen.toMap());
            gen.marker.remove();
            for (Object part : gen.children) {
                if (part instanceof CustomParticle customParticle) {
                    customParticle.destroy();
                }
            }
        }
        try (FileWriter writer = new FileWriter("data.json")) {
            gson.toJson(data, writer);
            System.out.println("JSON file created successfully!");
        } catch (IOException e) {
            e.printStackTrace
            ();
        }
    }
}
