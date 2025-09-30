package pso.pso2;

import java.util.HashSet;
import java.util.ArrayList;

import dp.Pattern;

// Variação: BPSO apenas com mecanismos de diversidade
public class BPSODiversidade extends BPSO {
    public BPSODiversidade(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        // Apenas mecanismo de diversidade
        initializeSwarm();
        double diversityThreshold = 0.9; // 90% dos itens iguais ao gbest
        for (int iter = 0; iter < numIterations; iter++) {
            HashSet<Integer> gbestItensForDiversity = gbestPattern != null ? gbestPattern.getItens() : new HashSet<>();
            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                HashSet<Integer> itens = new HashSet<>(particle.getPattern().getItens());
                int iguais = 0;
                for (Integer att : itens) {
                    if (gbestItensForDiversity.contains(att)) iguais++;
                }
                double similaridade = itens.size() > 0 ? (double) iguais / itens.size() : 0.0;
                if (similaridade > diversityThreshold && itens.size() > 0) {
                    int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
                    int[] janela = sorteiaJanela(tamJanela);
                    janelasAtributos.set(idx, janela);
                    HashSet<Integer> novosItens = new HashSet<>();
                    int numItens = 1 + rand.nextInt(Math.min(5, janela.length));
                    while (novosItens.size() < numItens) {
                        int val = janela[rand.nextInt(janela.length)];
                        if (val >= 0 && val < dimension) {
                            novosItens.add(val);
                        }
                    }
                    Pattern pattern = new Pattern(novosItens, metrica);
                    double[] velocity = new double[dimension];
                    for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
                    particle.setPattern(pattern);
                    particle.setVelocity(velocity);
                    particle.updatePbest();
                    updateGbest(pattern);
                }
                // Atualização padrão PSO
                double[] velocity = particle.getVelocity();
                HashSet<Integer> pbestItens = particle.getPbestPattern().getItens();
                HashSet<Integer> gbestItensSet = gbestPattern != null ? gbestPattern.getItens() : new HashSet<>();
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
