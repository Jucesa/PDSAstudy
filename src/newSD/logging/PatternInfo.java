package newSD.logging;
import dp.Pattern;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;


public class PatternInfo implements Serializable {
    private static long contador = 0;

    private final long id;
    private final Pattern pattern;
    private final long timestamp;
    private final String categoria;
    private final String operador;

    public PatternInfo(Pattern pattern, String operador, Set<Integer> triviais) {
        this.id = ++contador;
        this.operador = operador;
        this.pattern = pattern;
        this.timestamp = Instant.now().toEpochMilli();
        this.categoria = categorizar(pattern, triviais, 50);
    }

    private String categorizar(Pattern pattern,  Set<Integer> triviais, int limiteTrivial) {
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
    @Override
    public String toString() {
        return "PatternInfo{" +
                "id=" + id +
                ", itens=" + pattern.getItens() +
                ", categoria='" + categoria + '\'' +
                ", operador=" + operador +
                ", timestamp=" + timestamp +
                '}';
    }
}