package org.kellen.particleSystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ParticlesCMD implements CommandExecutor, TabCompleter {

    ParticleSystem plugin;

    public ParticlesCMD(ParticleSystem plugin) {
        this.plugin = plugin;
    }

    private boolean debug(@NotNull Player sender) {
        float x, y, z;
        x = (float) sender.getX();
        y = (float) sender.getY();
        z = (float) sender.getZ();
        World world = sender.getWorld();
        for (int p = 0; p < 3; p++) {  // p = 0, 1, 2
            for (float s : new float[]{1F, 2 / 3F, 1 / 3F}) {  // s = 1, 2/3, 1/3
                plugin.generators.add(new FireGenerator(plugin.toParticleBase(),world, x + s * 3F, y, z + (float) p, s, p, false,0));
                plugin.generators.add(new FireGenerator(plugin.toParticleBase(),world, x + s * 3F, y + 3, z + (float) p, s, p, true,0));
                plugin.generators.add(new FireGenerator(plugin.toParticleBase(),world, x + s * 3F, y + 7, z + (float) p, s, p, false,1));
                plugin.generators.add(new FireGenerator(plugin.toParticleBase(),world, x + s * 3F, y + 10, z + (float) p, s, p, true,1));
            }
        }

        plugin.generators.add(new WindGenerator(plugin.toParticleBase(),sender,150));

        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        if (Objects.equals(args[0], "debug")) {
            return debug((Player) sender);
        } else if (Objects.equals(args[0], "clear")) {
            for (ParticleGenerator gen : plugin.generators) {
                gen.marker.remove();
            }
            return true;
        } else if (Objects.equals(args[0], "wind")) {
            if (args.length <= 2) {
                sender.sendMessage("Usage: /particles wind [player] <?particle count>");
            }
            Player player = plugin.getServer().getPlayer(args[1]);

            if (player == null || !player.isOnline()) {
                sender.sendMessage(args[1] +" isn't online or doesn't exist");
                return false;
            }
            int parsedInt = 150;
            try {
                parsedInt = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {
            }
            plugin.generators.add(new WindGenerator(plugin.toParticleBase(),player,parsedInt));
            return true;
        } else if (Objects.equals(args[0], "fire")) {
            if (args.length <= 4) {
                sender.sendMessage("Usage: /particles fire [x] [y] [z] <?scale> <?palatte> <?collide>");
            }
            float x, y, z, scale;
            int palatte;
            try {
                x = Float.parseFloat(args[1]);
                y = Float.parseFloat(args[2]);
                z = Float.parseFloat(args[3]);
                scale = Float.parseFloat(args[4]);
                palatte = Integer.parseInt(args[5]);
            } catch (RuntimeException e) {
                sender.sendMessage("Position must be 3 numbers");
                sender.sendMessage("Usage: /particles fire [x] [y] [z] [scale] [palatte] <?collide> <?wind multiplier>");
                return false;
            }
            float wind = 0F;
            boolean collide = true;

            for (int i = 6; i < args.length; i++) {
                String arg = args[i];

                try {
                    wind = Float.parseFloat(arg);
                    continue;
                } catch (NumberFormatException ignored) {
                }

                if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) {
                    collide = Boolean.parseBoolean(arg); // Boolean must be last
                    continue;
                }

                sender.sendMessage("Invalid argument: " + arg + " (expected float, int, or true/false)");
                return false;
            }

            // Call the correct constructor based on provided values
            plugin.generators.add(new FireGenerator(plugin.toParticleBase(),((Player) sender).getWorld(), x, y, z, scale, palatte, collide, wind));

            return true;
        }
        return true; // Return true to indicate command was handled
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return Collections.emptyList(); // No suggestions for non-players
        }

        if (args.length == 1) {
            return Arrays.asList("debug", "clear", "fire", "wind");
        } else if (args[0].equalsIgnoreCase("fire")) {
            switch (args.length) {
                case 2 -> {
                    // Suggest the player's current coordinates for x
                    return List.of(String.valueOf(player.getLocation().getX()));
                }
                case 3 -> {
                    // Suggest the player's current coordinates for y
                    return List.of(String.valueOf(player.getLocation().getY()));
                }
                case 4 -> {
                    // Suggest the player's current coordinates for z
                    return List.of(String.valueOf(player.getLocation().getZ()));
                }
                case 5 -> {
                    // Suggest a default scale for fire particles
                    return Arrays.asList("0.5", "1", "2");
                }
                case 6 -> {
                    // Suggest a default scale for fire particles
                    return Arrays.asList("0", "1", "2");
                }
                default -> {
                }
            }
        } else if (args[0].equalsIgnoreCase("wind")) {
            if (args.length == 2) {
                ArrayList <String> players = new ArrayList<>();
                for (Player p:plugin.getServer().getOnlinePlayers()) {
                    players.add(p.name().toString());
                }
                // Suggest the player's current coordinates for x
                return players;
            } else if (args.length == 3) {
                // Suggest the player's current coordinates for y
                return Arrays.asList("150","300","600");
            }
        }

        return Collections.emptyList(); // No suggestions for invalid cases
    }
}
