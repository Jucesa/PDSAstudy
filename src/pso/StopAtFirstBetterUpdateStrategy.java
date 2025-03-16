package pso;

import dp.Pattern;
import java.util.Random;
import java.util.HashSet;

public class StopAtFirstBetterUpdateStrategy implements UpdateStrategy {
    private static final Random random = new Random();
    private static final double VEL_MAX = 1.0;
    private static final double VEL_MIN = -1.0;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;

    @Override
    public void updateParticle(Particle particle, Pattern globalBest, double[] velocity,
                               HashSet<Integer> novosItens, double w) {
        double currentQuality = particle.getPattern().getQualidade();

        for (int d = 0; d < velocity.length; d++) {
            velocity[d] = w * velocity[d]
                    + C1 * random.nextDouble() * ((particle.getPbest().getItens().contains(d) ? 1 : 0)
                      - (particle.getPattern().getItens().contains(d) ? 1 : 0))
                    + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0)
                      - (particle.getPattern().getItens().contains(d) ? 1 : 0));
            velocity[d] = Math.max(Math.min(velocity[d], VEL_MAX), VEL_MIN);

            double prob = 1.0 / (1.0 + Math.exp(-velocity[d]));
            if (random.nextDouble() < prob) {
                novosItens.add(d);
            } else {
                if (novosItens.contains(d)) {
                    novosItens.remove(d);
                }
            }
            Pattern testePattern = new Pattern(novosItens, particle.getPattern().getTipoAvaliacao());
            double newQuality = testePattern.getQualidade();
            if (newQuality > currentQuality) {
                break; // Manter mudança
            } else {
                if (novosItens.contains(d)) {
                    novosItens.add(d);
                } else {
                    novosItens.remove(d);
                }
            }
        }
    }
}
