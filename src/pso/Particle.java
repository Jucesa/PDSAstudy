package pso;

import dp.Pattern;
import java.util.HashSet;
import java.util.Random;

public class Particle {
    private int id; // Novo campo para identificar a partícula
    private Pattern pattern;
    private double[] velocity;
    private Pattern best;
    private static final double VEL_MAX = 1.0;
    private static final double VEL_MIN = -1.0; // Novo limite inferior
    private static final double W_INITIAL = 0.9; // Peso de inércia inicial
    private static final double W_FINAL = 0.4;   // Peso de inércia final
    private static final double C1 = 2.0; // Aumentar peso do melhor local
    private static final double C2 = 2.0; // Aumentar peso do melhor global
    private static Random random = new Random();
    private int iteracao;
    private int maxIteracoes;

    public Particle(int id, HashSet<Integer> posicaoInicial, String tipoAvaliacao, int dimensao, int maxIteracoes) {
        this.id = id; // Atribuir o ID
        this.pattern = new Pattern(posicaoInicial, tipoAvaliacao);
        this.velocity = new double[dimensao];
        for (int i = 0; i < dimensao; i++) {
            this.velocity[i] = (random.nextDouble() * 2 - 1) * VEL_MAX;
        }
        this.best = pattern;
        this.iteracao = 0;
        this.maxIteracoes = maxIteracoes;
    }

    public void updateVelocityAndPosition(Pattern globalBest) {
        // Adaptar o peso de inércia
        double w = W_INITIAL - ((W_INITIAL - W_FINAL) * iteracao) / maxIteracoes;
    
        // Copiar os itens atuais
        HashSet<Integer> novosItens = new HashSet<>(pattern.getItens());
    
        // Selecionar aleatoriamente um índice para atualização
        int d = random.nextInt(velocity.length);
    
        // Atualizar a velocidade do índice escolhido
        velocity[d] = w * velocity[d]
                + C1 * random.nextDouble() * ((best.getItens().contains(d) ? 1 : 0) - (pattern.getItens().contains(d) ? 1 : 0))
                + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0) - (pattern.getItens().contains(d) ? 1 : 0));
    
        // Aplicar limitação de velocidade
        if (velocity[d] > VEL_MAX) {
            velocity[d] = VEL_MAX;
        } else if (velocity[d] < VEL_MIN) {
            velocity[d] = VEL_MIN;
        }
    
        // Determinar a probabilidade de mudar o estado com base na função sigmoid
        double prob = sigmoid(velocity[d]);
        if (random.nextDouble() < prob) {
            // Inverter o estado binário do índice selecionado
            if (novosItens.contains(d)) {
                novosItens.remove(d);
            } else {
                novosItens.add(d);
            }
        }
    
        // Atualizar o padrão (pattern) com base nos novos itens
        Pattern novoPattern = new Pattern(novosItens, pattern.getTipoAvaliacao());
        if (novoPattern.getQualidade() > pattern.getQualidade()) {
            pattern = novoPattern;
            if (pattern.getQualidade() > best.getQualidade()) {
                best = pattern;
            }
        }
    
        // Incrementar a iteração
        iteracao++;
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


