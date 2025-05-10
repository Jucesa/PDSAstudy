package pso;

import dp.Pattern;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class BestFromNScalingStrategy implements UpdateStrategy {
    private static final Random random = new Random();
    private static final double VEL_MAX = 4.0; // Valor ajustado conforme CompleteUpdateStrategy
    private static final double VEL_MIN = -4.0; // Valor ajustado conforme CompleteUpdateStrategy
    private static final double C1 = 1.49445; // Valor ajustado conforme CompleteUpdateStrategy
    private static final double C2 = 1.49445; // Valor ajustado conforme CompleteUpdateStrategy
    
    private int currentN;
    private final int maxN;
    private final double incrementRate;
    private int iteration;

    /**
     * Cria uma estratégia onde N aumenta progressivamente a cada iteração
     * @param initialN valor inicial de N
     * @param maxN valor máximo de N (normalmente o tamanho da base de dados)
     * @param incrementRate taxa de incremento de N por iteração (por exemplo, 1.0 para incremento linear)
     */
    public BestFromNScalingStrategy(int initialN, int maxN, double incrementRate) {
        this.currentN = initialN;
        this.maxN = maxN;
        this.incrementRate = incrementRate;
        this.iteration = 0;
    }

    /**
     * Construtor alternativo que assume incremento linear (1.0)
     */
    public BestFromNScalingStrategy(int initialN, int maxN) {
        this(initialN, maxN, 1.0);
    }

    @Override
    public void updateParticle(Particle particle, Pattern globalBest, double[] velocity,
                              HashSet<Integer> novosItens, double w) {
        // Criar lista de índices para seleção aleatória
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < velocity.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);

        // Determinar quantos índices processar (limitado pelo currentN atual)
        int dimensionsToProcess = Math.min(currentN, velocity.length);
        
        // Manter referência à melhor solução encontrada
        HashSet<Integer> bestSolution = new HashSet<>(novosItens);
        double bestQuality = particle.getPattern().getQualidade();
        
        // Processar as dimensões selecionadas
        for (int i = 0; i < dimensionsToProcess; i++) {
            int d = indices.get(i);
            
            // Atualizar a velocidade usando a equação padrão do BPSO com parâmetros ajustados
            velocity[d] = w * velocity[d]
                    + C1 * random.nextDouble() * ((particle.getPbest().getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0))
                    + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0));
            
            // Aplicar limitação de velocidade
            velocity[d] = Math.max(Math.min(velocity[d], VEL_MAX), VEL_MIN);
            
            // Calcular probabilidade usando função sigmoid
            double prob = 1.0 / (1.0 + Math.exp(-velocity[d]));
            
            // Testar a mudança antes de aplicá-la
            HashSet<Integer> testItens = new HashSet<>(bestSolution);
            if (random.nextDouble() < prob) {
                testItens.add(d);
            } else {
                testItens.remove(d);
            }

            // Verificar se a mudança melhora a qualidade
            Pattern testPattern = new Pattern(testItens, particle.getPattern().getTipoAvaliacao());
            if (testPattern.getQualidade() > bestQuality) {
                // Se melhorou, guarda esta como a melhor solução até agora
                bestSolution = new HashSet<>(testItens);
                bestQuality = testPattern.getQualidade();
            }
        }
        
        // Atualizar solução da partícula com a melhor encontrada
        novosItens.clear();
        novosItens.addAll(bestSolution);
        
        // Atualizar o valor de N para a próxima iteração
        updateN();
        iteration++;
    }
    
    private void updateN() {
        // Calcula o novo valor de N com base na taxa de incremento
        currentN = Math.min(maxN, (int)(currentN + incrementRate));
    }
    
    public int getCurrentN() {
        return currentN;
    }
    
    public int getIteration() {
        return iteration;
    }
    
    public void resetScaling() {
        this.iteration = 0;
        this.currentN = getInitialN();
    }
    
    public int getInitialN() {
        return currentN - (int)(incrementRate * iteration);
    }
}
