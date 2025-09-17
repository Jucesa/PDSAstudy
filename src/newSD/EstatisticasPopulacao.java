package newSD;

import dp.Pattern;
import java.util.*;

public class EstatisticasPopulacao {

    public static class ItemStats {
        int frequencia;
        double somaQualidade;

        public void adicionar(double qualidade) {
            frequencia++;
            somaQualidade += qualidade;
        }

        public double getMediaQualidade() {
            return frequencia == 0 ? 0.0 : somaQualidade / frequencia;
        }
    }

    public static Map<Integer, ItemStats> calcularFrequenciaItens(Pattern[] P) {
        Map<Integer, ItemStats> estatisticas = new HashMap<>();
        if (P == null) return estatisticas;

        for (Pattern padrao : P) {
            if (padrao == null) continue;
            double qualidade = padrao.getQualidade();
            for (Integer item : padrao.getItens()) {
                estatisticas
                        .computeIfAbsent(item, k -> new ItemStats())
                        .adicionar(qualidade);
            }
        }
        return estatisticas;
    }

    public static int contarDistintos(Pattern[] P) {
        if (P == null) return 0;
        Set<Pattern> set = new HashSet<>();
        for (Pattern p : P) {
            if (p != null) set.add(p);
        }
        return set.size();
    }

    public static double mediaQualidade(Pattern[] P) {
        if (P == null || P.length == 0) return 0.0;
        double soma = 0.0;
        int cont = 0;
        for (Pattern p : P) {
            if (p != null) {
                soma += p.getQualidade();
                cont++;
            }
        }
        return cont == 0 ? 0.0 : soma / cont;
    }

    public static double overallSuppPositive(Pattern[] P) {
        if (P == null || P.length == 0) return 0.0;
        boolean[] vrpGrupo = new boolean[dp.D.numeroExemplosPositivo];
        Arrays.fill(vrpGrupo, false);

        for (Pattern pattern : P) {
            if (pattern == null) continue;
            boolean[] vrpItem = pattern.getVrP();
            if (vrpItem == null) continue;
            for (int j = 0; j < Math.min(vrpItem.length, vrpGrupo.length); j++) {
                if (vrpItem[j]) {
                    vrpGrupo[j] = true;
                }
            }
        }

        double TPgrupo = 0;
        for (boolean b : vrpGrupo) {
            if (b) TPgrupo++;
        }

        return vrpGrupo.length == 0 ? 0.0 : TPgrupo / vrpGrupo.length;
    }

    /**
     * 👉 Função central: transforma estatísticas em um array de Strings
     */
    public static String[] gerarResumo(Pattern[] populacao, String metrica, int individuosGerados, int limiar) {
        double mediaQualidade = mediaQualidade(populacao);
        double tamanhoMedio = populacao == null || populacao.length == 0 ? 0.0 :
                Arrays.stream(populacao).filter(Objects::nonNull).mapToInt(p -> p.getItens().size()).average().orElse(0.0);
        double suporteGlobal = overallSuppPositive(populacao);
        int distintos = contarDistintos(populacao);
        Map<Integer, ItemStats> freqItens = calcularFrequenciaItens(populacao);

        List<String> linhas = new ArrayList<>();
        linhas.add("Indivíduos gerados: " + individuosGerados);
        linhas.add("Métrica: " + metrica);
        linhas.add(String.format("Média qualidade: %.6f", mediaQualidade));
        linhas.add(String.format("Tamanho médio SG: %.6f", tamanhoMedio));
        linhas.add(String.format("Suporte positivo global: %.6f", suporteGlobal));
        linhas.add("Qtd SG distintos: " + distintos);
        linhas.add("Limiar (índice threshold): " + limiar);
        linhas.add("Itens na população (item -> freq, médiaQualidade):");

        for (Map.Entry<Integer, ItemStats> e : freqItens.entrySet()) {
            linhas.add(String.format("  Item %d -> freq=%d, médiaQualidade=%.6f",
                    e.getKey(), e.getValue().frequencia, e.getValue().getMediaQualidade()));
        }
        linhas.add("-----");

        return linhas.toArray(new String[0]);
    }
}
