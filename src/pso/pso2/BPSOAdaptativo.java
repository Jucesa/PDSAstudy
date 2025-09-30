package pso.pso2;

import java.util.HashSet;
import java.util.ArrayList;

import dp.Pattern;

// Variação: BPSO apenas com parâmetros adaptativos
public class BPSOAdaptativo extends BPSO {
    public BPSOAdaptativo(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        // Apenas BPSO com parâmetros adaptativos: sem elitismo, sem mecanismos extras
        initializeSwarm();
        for (int iter = 0; iter < numIterations; iter++) {
            double frac = (double) iter / (double) numIterations;
            w = wMax - (wMax - wMin) * frac;
            c1 = c1Max - (c1Max - c1Min) * frac;
            c2 = c2Min + (c2Max - c2Min) * frac;

            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                double[] velocity = particle.getVelocity();
                HashSet<Integer> itens = new HashSet<>(particle.getPattern().getItens());
                HashSet<Integer> pbestItens = particle.getPbestPattern().getItens();
                    HashSet<Integer> gbestItensSet = gbestPattern.getItens(); // Use local variable
                int dimension = velocity.length;
                for (int d = 0; d < dimension; d++) {
                    int x = itens.contains(d) ? 1 : 0;
                    int pbest = pbestItens.contains(d) ? 1 : 0;
                    int gbestVal = gbestItensSet.contains(d) ? 1 : 0;
                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();
                    velocity[d] = w * velocity[d]
                        + c1 * r1 * (pbest - x)
                        + c2 * r2 * (gbestVal - x);
                    double prob = 1.0 / (1.0 + Math.exp(-velocity[d]));
                    if (rand.nextDouble() < prob) {
                        itens.add(d);
                    } else {
                        itens.remove(d);
                    }
                }
                particle.setVelocity(velocity);
                Pattern newPattern = new Pattern(itens, metrica);
                particle.setPattern(newPattern);
                particle.updatePbest();
                    updateGbest(newPattern); // Use local method
            }
        }
        // Retorna os melhores pbests
        ArrayList<Pattern> allPbests = new ArrayList<>();
        for (Particle p : swarm) {
            allPbests.add(p.getPbestPattern());
        }
        allPbests.sort((a, b) -> Double.compare(b.getQualidade(), a.getQualidade()));
        ArrayList<Pattern> unique = new ArrayList<>();
        for (Pattern p : allPbests) {
            boolean found = false;
            for (Pattern u : unique) {
                if (u.equals(p)) {
                    found = true;
                    break;
                }
            }
            if (!found) unique.add(p);
        }
        int numRetornar = Math.min(k, unique.size());
        Pattern[] result = new Pattern[numRetornar];
        for (int i = 0; i < numRetornar; i++) {
            result[i] = unique.get(i);
        }
        return result;
    }
}
