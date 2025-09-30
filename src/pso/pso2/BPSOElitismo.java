package pso.pso2;

import java.util.HashSet;
import java.util.ArrayList;

import dp.Pattern;

// Variação: BPSO apenas com elitismo
public class BPSOElitismo extends BPSO {
    public BPSOElitismo(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        // Apenas mecanismo de elitismo
        initializeSwarm();
        Pattern elite = null;
        for (int iter = 0; iter < numIterations; iter++) {
            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                double[] velocity = particle.getVelocity();
                HashSet<Integer> itens = new HashSet<>(particle.getPattern().getItens());
                HashSet<Integer> pbestItens = particle.getPbestPattern().getItens();
                HashSet<Integer> gbestItensSet = gbestPattern != null ? gbestPattern.getItens() : new HashSet<>();
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
                updateGbest(newPattern);
            }
            // Elitismo: preserva o melhor indivíduo da população
            Pattern melhor = gbestPattern;
            if (melhor != null && (elite == null || melhor.getQualidade() > elite.getQualidade())) {
                elite = new Pattern(melhor);
            }
        }
        // Retorna os melhores pbests, incluindo o elite
        ArrayList<Pattern> allPbests = new ArrayList<>();
        for (Particle p : swarm) {
            allPbests.add(p.getPbestPattern());
        }
        if (elite != null) {
            allPbests.add(elite);
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
