package newSD.logging;

import dp.Pattern;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class PatternInfo implements Serializable {

    private final int id;
    private final Pattern pattern;
    private final long timestamp;
    private final String itensComRanking;
    private final String categoria;
    private final String operador;
    private final String genealogiaSerializada;

    public PatternInfo(
            int id,
            Pattern pattern,
            String operador,
            ArrayList<HashSet<Integer>> genealogia,
            Set<Integer> triviais,
            Map<Integer, Integer> ranking
    ) {
        this.id = id;
        this.pattern = pattern;
        this.operador = operador;
        this.timestamp = Instant.now().toEpochMilli();
        this.categoria = categorizar(pattern, triviais);
        this.itensComRanking = serializarItens(pattern.getItens(), ranking);
        this.genealogiaSerializada = serializarGenealogia(genealogia, ranking);
    }

    // ============================
    //   Serializador dos itens
    // ============================
    private String serializarItens(Set<Integer> itens, Map<Integer, Integer> ranking) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        boolean first = true;

        for (int item : itens) {
            if (!first) sb.append("; ");
            first = false;

            int rank = ranking.getOrDefault(item, -1);
            sb.append(item).append("(").append(rank).append(")");
        }

        sb.append("]");
        return sb.toString();
    }

    // ============================
    //   Serializador da genealogia
    // ============================
    private String serializarGenealogia(ArrayList<HashSet<Integer>> genealogia,
                                        Map<Integer, Integer> ranking) {

        if (genealogia == null || genealogia.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();
        boolean firstConjunto = true;

        for (HashSet<Integer> conjunto : genealogia) {

            if (!firstConjunto) sb.append("+"); // separa conjuntos
            firstConjunto = false;

            List<Integer> ordenados = new ArrayList<>(conjunto);
            Collections.sort(ordenados);

            sb.append("[");
            boolean firstItem = true;

            for (int item : ordenados) {
                if (!firstItem) sb.append(";");
                firstItem = false;

                int rank = ranking.getOrDefault(item, -1);
                sb.append(item).append("(").append(rank).append(")");
            }

            sb.append("]");
        }

        return sb.toString();
    }

    private String categorizar(Pattern pattern, Set<Integer> triviais) {
        Set<Integer> itens = pattern.getItens();

        if (itens.isEmpty()) return "Vazio";

        boolean temTrivial = false;
        boolean temNaoTrivial = false;

        for (int item : itens) {
            if (triviais.contains(item)) temTrivial = true;
            else temNaoTrivial = true;
        }

        if (itens.size() == 1)
            return triviais.containsAll(itens) ? "Trivial (1D)" : "Não-trivial (1D)";

        if (temTrivial && temNaoTrivial) return "Não-trivial";
        if (temTrivial) return "Trivial";

        return "Raro";
    }

    // ======================
    //   GETTERS
    // ======================
    public int getId() { return id; }
    public Pattern getPattern() { return pattern; }
    public long getTimestamp() { return timestamp; }
    public String getCategoria() { return categoria; }
    public String getOperador() { return operador; }
    public String getItensComRanking() { return itensComRanking; }
    public String getGenealogiaSerializada() { return genealogiaSerializada; }
}
