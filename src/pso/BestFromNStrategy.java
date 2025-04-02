package pso;

import dp.Pattern;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class BestFromNStrategy implements UpdateStrategy {
    private static final Random random = new Random();
    private static final double VEL_MAX = 1.0;
    private static final double VEL_MIN = -1.0;
    private static final double C1 = 2.0;
    private static final double C2 = 2.0;
    private int numItemsToTest;

    public BestFromNStrategy(int n) {
        this.numItemsToTest = n;
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

        // Selecionar os N primeiros índices
        for (int i = 0; i < Math.min(numItemsToTest, velocity.length); i++) {
            int d = indices.get(i);
            
            // Calcular nova velocidade
            velocity[d] = w * velocity[d]
                + C1 * random.nextDouble() * ((particle.getPbest().getItens().contains(d) ? 1 : 0)
                    - (particle.getPattern().getItens().contains(d) ? 1 : 0))
                + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0)
                    - (particle.getPattern().getItens().contains(d) ? 1 : 0));
            
            velocity[d] = Math.max(Math.min(velocity[d], VEL_MAX), VEL_MIN);
            double prob = 1.0 / (1.0 + Math.exp(-velocity[d]));

            // Testar a mudança antes de aplicá-la
            HashSet<Integer> testItens = new HashSet<>(novosItens);
            if (random.nextDouble() < prob) {
                testItens.add(d);
            } else {
                testItens.remove(d);
            }

            // Verificar se a mudança melhora a qualidade
            Pattern testPattern = new Pattern(testItens, particle.getPattern().getTipoAvaliacao());
            if (testPattern.getQualidade() > particle.getPattern().getQualidade()) {
                // Se melhorou, aplica a mudança
                novosItens.clear();
                novosItens.addAll(testItens);
            }
        }
    }
    
    // Métodos de acesso ao numItemsToTest para permitir modificação nas subclasses
    protected int getNumItemsToTest() {
        return numItemsToTest;
    }
    
    protected void setNumItemsToTest(int n) {
        this.numItemsToTest = n;
    }
}