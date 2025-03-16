package pso;

import java.util.HashSet;
import dp.Pattern;

public interface UpdateStrategy {
    void updateParticle(Particle particle, Pattern globalBest, double[] velocity, 
                       HashSet<Integer> novosItens, double w);
}
