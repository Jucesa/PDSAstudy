package newSD.logging;
import dp.Pattern;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;


public class PatternInfo implements Serializable {

    private static long contador = 0;
    private final long id;
    private final Pattern pattern;
    private final long timestamp;
    private final String itensComRanking;
    private final String categoria;
    private final String operador;

    public PatternInfo(Pattern pattern, String operador, Set<Integer> triviais, Map<Integer, Integer> ranking) {
        this.id = ++contador;
        this.operador = operador;
        this.pattern = pattern;
        this.timestamp = Instant.now().toEpochMilli();
        this.categoria = categorizar(pattern, triviais);
        this.itensComRanking = construirDescricaoItens(pattern.getItens(), ranking);
    }


    public static void resetContador() {
        contador = 0;
    }

    private String construirDescricaoItens(Set<Integer> itens, Map<Integer, Integer> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (int item : itens) {
            if (!first) sb.append("; ");
            int rank = ranking.getOrDefault(item, -1);
            sb.append(item).append("(").append(rank).append(")");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }



    private String categorizar(Pattern pattern,  Set<Integer> triviais) {
        Set<Integer> itens = pattern.getItens();
        if (itens.isEmpty()) {
            return "Vazio";
        }

        // Mapeia rapidamente os itens triviais com base no limite

        boolean temTrivial = false;
        boolean temNaoTrivial = false;

        for (int item : itens) {
            if (triviais.contains(item)) {
                temTrivial = true;
            } else {
                temNaoTrivial = true;
            }
        }

        // Classificação final
        if (pattern.getItens().size() == 1) {
            return triviais.containsAll(itens) ? "Trivial (1D)" : "Não-trivial (1D)";
        }

        if(temNaoTrivial && temTrivial) {
            return "Não-trivial";
        }
        if(temTrivial) {
            return "Trivial";
        }

        return "Raro";
    }

    public long getId() { return id; }

    public Pattern getPattern() { return pattern; }

    public long getTimestamp() { return timestamp; }

    public String getCategoria() { return categoria; }

    public String getOperador(){ return operador; }
    public String getItensComRanking() { return itensComRanking; }

    @Override
    public String toString() {
        return "PatternInfo{" +
                "id=" + id +
                ", qualidade=" + pattern.getQualidade() +
                ", itens=" + pattern.getItens() +
                ", categoria='" + categoria + '\'' +
                ", operador=" + operador +
                ", timestamp=" + timestamp +
                '}';
    }
}