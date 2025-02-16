package org.kellen.particleSystem;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class lib {
    public static boolean Collides(Vector a, Block b) {
        return b.isCollidable() && 
      !(a.getX()-0.1f < b.getX() ||  a.getX()+0.1f > b.getX() + 1 ||  
        a.getY()-0.1f < b.getY() ||  a.getY()+0.1f > b.getY() + 1 ||  
        a.getZ()-0.1f < b.getZ() ||  a.getZ()+0.1f > b.getZ() + 1);
    }
}
