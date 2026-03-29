package SDp.algorithm;

import dp.Pattern;

import java.util.HashMap;
import java.util.Map;

public class DiversityManager {

    private final Map<Integer, Integer> bufferFrequencia = new HashMap<>();
    private static final double LOG2 = Math.log(2);

    public double calcularEntropiaPopulacao(Pattern[] populacao, int inicio) {
        if (populacao == null || populacao.length == 0) return 0.0;

        bufferFrequencia.clear();
        int totalItensAcrossPopulation = 0;

        // 1. Contagem de Frequências (Mantendo sua lógica de buffer)
        for (int i = inicio; i < populacao.length; i++) {
            Pattern p = populacao[i];
            if (p != null) {
                for (Integer item : p.getItens()) {
                    // Otimização de busca: usa 'put' com o retorno de 'get' para evitar 2 lookups
                    bufferFrequencia.compute(item, (k, count) -> (count == null) ? 1 : count + 1);
                    totalItensAcrossPopulation++;
                }
            }
        }

        if (totalItensAcrossPopulation == 0) return 0.0;

        // 2. Cálculo da Entropia (Otimizado matematicamente)
        double somaCountLogCount = 0.0;

        for (Integer count : bufferFrequencia.values()) {
            // soma += count * log2(count)
            somaCountLogCount += count * (Math.log(count) / LOG2);
        }

        // H = log2(total) - (soma / total)
        double logTotal = Math.log(totalItensAcrossPopulation) / LOG2;
        return logTotal - (somaCountLogCount / totalItensAcrossPopulation);
    }

}
