package dp;

import java.util.HashSet;
import java.util.Random;

// Classe Particle representa uma partícula no PSO
public class Particle {
    private Pattern pattern;
    private double[] velocity;
    private Pattern best;
    private static Random random = new Random();

    private static final double W = 0.5;
    private static final double C1 = 1.5;
    private static final double C2 = 1.5;
    private static final double VEL_MAX = 6.0;

    public Particle(int dimensao, String tipoAvaliacao) {
        HashSet<Integer> itens = new HashSet<>();
        for (int i = 0; i < dimensao; i++) {
            if (random.nextBoolean()) itens.add(i);
        }
        this.pattern = new Pattern(itens, tipoAvaliacao);
        this.velocity = new double[dimensao];
        this.best = new Pattern(pattern);
    } 

    //Aqui é para inicializar 1 para algum item e 0 para os outros
    //Perguntar sobre isso daqui
    public Particle(int dimensao, String tipoAvaliacao, int itemDoMomento) {
        HashSet<Integer> itens = new HashSet<>();
        for (int i = 0; i < dimensao; i++) {
            if (i == itemDoMomento) itens.add(D.itensUtilizados[i]);
        }
        this.pattern = new Pattern(itens, tipoAvaliacao);
        this.velocity = new double[dimensao];
        this.best = new Pattern(pattern);
    }


    public void updateVelocityAndPosition(Pattern globalBest) {
        HashSet<Integer> novosItens = new HashSet<>(pattern.getItens());
        for (int d = 0; d < velocity.length; d++) {
            velocity[d] = W * velocity[d]
                        + C1 * random.nextDouble() * (best.getItens().contains(d) ? 1 : 0 - (novosItens.contains(d) ? 1 : 0))
                        + C2 * random.nextDouble() * (globalBest.getItens().contains(d) ? 1 : 0 - (novosItens.contains(d) ? 1 : 0));
            
            velocity[d] = Math.max(-VEL_MAX, Math.min(VEL_MAX, velocity[d]));

            if (sigmoid(velocity[d]) > random.nextDouble()) {
                if (novosItens.contains(d)) {
                    novosItens.remove(d);
                } else {
                    novosItens.add(d);
                }
            }
        }

        Pattern novoPattern = new Pattern(novosItens, pattern.getTipoAvaliacao());
        if (novoPattern.getQualidade() > pattern.getQualidade()) {
            pattern = novoPattern;
            if (pattern.getQualidade() > best.getQualidade()) {
                best = new Pattern(pattern);
            }
        }
    }

    public double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    public Pattern getPattern() {
        return pattern;
    }

    public double getQualidade() {
        return pattern.getQualidade();
    }
}