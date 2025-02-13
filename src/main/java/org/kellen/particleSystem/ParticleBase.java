package org.kellen.particleSystem;

import java.util.ArrayList;
import java.util.Random;

public class ParticleBase {
    public ArrayList<Object> children = new ArrayList<>();
    public ParticleBase parent;
    public static final Random random = new Random();

    public ParticleBase(ParticleBase nparent) {
        this.parent = nparent;
    }
}
