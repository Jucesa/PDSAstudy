package pso;

import dp.Pattern;
import java.util.HashSet;

public class BestFromNScalingStrategy extends BestFromNStrategy {
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
        super(initialN);
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
        // Atualizar o valor de N antes de chamar o método da classe pai
        updateN();
        
        // Atualiza o valor de N na classe pai antes de chamar updateParticle
        setNumItemsToTest(currentN);
        
        // Chama o método da classe pai com o novo valor de N
        super.updateParticle(particle, globalBest, velocity, novosItens, w);
        
        // Incrementa o contador de iterações
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
        return super.getNumItemsToTest();
    }
}
