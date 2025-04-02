package pso;

import dp.Pattern;
import java.util.HashSet;
import java.util.Random;

public class Particle {
    private int id; // Novo campo para identificar a partícula
    private Pattern pattern;
    private double[] velocity;
    private Pattern pbest;
    private static final double VEL_MAX = 1.0;
    private static final double W_INITIAL = 0.9; // Peso de inércia inicial
    private static final double W_FINAL = 0.4;   // Peso de inércia final
    private static Random random = new Random();
    private int iteracao;
    private int maxIteracoes;
    private UpdateStrategy updateStrategy;
    private HashSet<Integer> reusableItems = new HashSet<>();

    public Particle(int id, HashSet<Integer> posicaoInicial, String tipoAvaliacao, int dimensao, int maxIteracoes) {
        this.id = id; 
        this.pattern = new Pattern(posicaoInicial, tipoAvaliacao);
        this.velocity = new double[dimensao];
        for (int i = 0; i < dimensao; i++) {
            this.velocity[i] = (random.nextDouble() * 2 - 1) * VEL_MAX;
        }
        this.pbest = pattern;
        this.iteracao = 0;
        this.maxIteracoes = maxIteracoes;

        // É SÓ MUDAR AQUI A ESTRATÉGIA DE ATUALIZAÇÃO
        this.updateStrategy = new BestFromNScalingStrategy(1, dimensao ,1); 
    }

    public void updateVelocityAndPosition(Pattern globalBest) {
        // Adaptar o peso de inércia
        double w = W_INITIAL - ((W_INITIAL - W_FINAL) * iteracao) / maxIteracoes;
    
        // Copiar os itens atuais
        reusableItems.clear();
        reusableItems.addAll(pattern.getItens());
    
        // Usar a estratégia de atualização
        updateStrategy.updateParticle(this, globalBest, velocity, reusableItems, w);
    
        // Atualizar o padrão com base nos novos itens
        Pattern novoPattern = new Pattern(reusableItems, pattern.getTipoAvaliacao());
        pattern = novoPattern;
        if (pattern.getQualidade() > pbest.getQualidade()) {
            pbest = pattern;
        }
        iteracao++;
    }
    
    public Pattern getPbest() {
        return pbest;
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

    public int getId() {
        return id;
    }
}


