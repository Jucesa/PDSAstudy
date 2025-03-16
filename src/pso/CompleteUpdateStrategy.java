package pso;

import java.util.HashSet;
import java.util.Random;
import dp.Pattern;

public class CompleteUpdateStrategy implements UpdateStrategy {
    private static final Random random = new Random();
    private static final double VEL_MAX = 1.0;
    private static final double VEL_MIN = -1.0;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;

    @Override
    public void updateParticle(Particle particle, Pattern globalBest, double[] velocity, 
                             HashSet<Integer> novosItens, double w) {
        // Percorrer todas as dimensões
        for (int d = 0; d < velocity.length; d++) {
            // Atualizar a velocidade para cada dimensão
            velocity[d] = w * velocity[d]
                    + C1 * random.nextDouble() * ((particle.getPbest().getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0))
                    + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0));

            // Aplicar limitação de velocidade
            velocity[d] = Math.max(Math.min(velocity[d], VEL_MAX), VEL_MIN);

            // Determinar a probabilidade de mudar o estado com base na função sigmoid
            double prob = 1.0 / (1.0 + Math.exp(-velocity[d]));
            if (random.nextDouble() < prob) {
                novosItens.add(d);
            } else {
                if (novosItens.contains(d)) {
                    novosItens.remove(d);
                }
            }
        }
    }
}
