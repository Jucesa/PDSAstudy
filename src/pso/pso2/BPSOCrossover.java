package pso.pso2;

import java.util.HashSet;
import java.util.ArrayList;

import dp.Pattern;

// Variação: BPSO apenas com crossover
public class BPSOCrossover extends BPSO {
    public BPSOCrossover(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        // Apenas mecanismo de crossover
        initializeSwarm();
        double crossoverRate = 0.3; // taxa de crossover
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
                // Crossover: combina com outro indivíduo aleatório
                if (rand.nextDouble() < crossoverRate) {
                    int outroIdx = rand.nextInt(swarm.size());
                    if (outroIdx != idx) {
                        Particle outro = swarm.get(outroIdx);
                        HashSet<Integer> outrosItens = outro.getPattern().getItens();
                        HashSet<Integer> novosItens = new HashSet<>();
                        for (int d = 0; d < dimension; d++) {
                            if (rand.nextBoolean()) {
                                if (itens.contains(d)) novosItens.add(d);
                            } else {
                                if (outrosItens.contains(d)) novosItens.add(d);
                            }
                        }
                        Pattern novoPattern = new Pattern(novosItens, metrica);
                        particle.setPattern(novoPattern);
                        particle.updatePbest();
                        updateGbest(novoPattern);
                    }
                } else {
                    Pattern newPattern = new Pattern(itens, metrica);
                    particle.setPattern(newPattern);
                    particle.updatePbest();
                    updateGbest(newPattern);
                }
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
