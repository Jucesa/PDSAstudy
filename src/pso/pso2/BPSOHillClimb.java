
package pso.pso2;

import java.util.HashSet;
import java.util.ArrayList;

import dp.Pattern;

// Variação: BPSO apenas com busca local (hill climbing)
public class BPSOHillClimb extends BPSO {
    public BPSOHillClimb(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        // Apenas mecanismo de busca local (hill climbing)
        initializeSwarm();
        for (int iter = 0; iter < numIterations; iter++) {
            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                // Atualização padrão do PSO (sem adaptativos, sem janelas, sem diversidade)
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
                // Busca local (hill climbing): tenta melhorar o padrão atual
                Pattern melhor = new Pattern(newPattern);
                double melhorQualidade = melhor.getQualidade();
                for (int d = 0; d < dimension; d++) {
                    HashSet<Integer> vizinho = new HashSet<>(itens);
                    if (vizinho.contains(d)) {
                        vizinho.remove(d);
                    } else {
                        vizinho.add(d);
                    }
                    Pattern vizinhoPattern = new Pattern(vizinho, metrica);
                    if (vizinhoPattern.getQualidade() > melhorQualidade) {
                        melhor = vizinhoPattern;
                        melhorQualidade = vizinhoPattern.getQualidade();
                    }
                }
                particle.setPattern(melhor);
                particle.updatePbest();
                updateGbest(melhor);
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
