package pso;

import java.util.HashSet;
import java.util.Random;
import dp.Pattern;

public class CompleteUpdateStrategy implements UpdateStrategy {
    private static final Random random = new Random();
    private static final double VEL_MAX = 4.0;
    private static final double VEL_MIN = -4.0;
    private static final double C1 = 1.49445;
    private static final double C2 = 1.49445;

    @Override
    public void updateParticle(Particle particle, Pattern globalBest, double[] velocity, 
                             HashSet<Integer> novosItens, double w) {
        
        // Atualizar todos os itens (dimensões) do espaço de busca
        for (int d = 0; d < velocity.length; d++) {
            // Atualizar a velocidade usando a equação padrão do BPSO
            velocity[d] = w * velocity[d]
                    + C1 * random.nextDouble() * ((particle.getPbest().getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0))
                    + C2 * random.nextDouble() * ((globalBest.getItens().contains(d) ? 1 : 0) 
                        - (particle.getPattern().getItens().contains(d) ? 1 : 0));
            
            // Aplicar limitação de velocidade para evitar valores extremos
            velocity[d] = Math.max(Math.min(velocity[d], VEL_MAX), VEL_MIN);
            
            // Calcular probabilidade usando função sigmoid
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
}
