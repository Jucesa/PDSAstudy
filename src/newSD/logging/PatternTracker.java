package newSD.logging;

import dp.Pattern;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PatternTracker {

    private final Set<Integer> triviais;
    private final int k;
    private final Map<Integer, Integer> rankingItems = new HashMap<>();

    // ID único por objeto Pattern
    private final Map<Pattern, Integer> ids = new IdentityHashMap<>();
    private int nextId = 1;

    // Banco de PatternInfo únicos
    private final Map<Integer, PatternInfo> banco = new HashMap<>();

    // Históricos leves
    private final List<Integer> historicoIds = new ArrayList<>();
    private final List<Integer> historicoKIds = new ArrayList<>();


    public PatternTracker(Pattern[] I, int limiteTrivial, int k) {
        this.triviais = new HashSet<>();
        this.k = k;

        // Marcar itens triviais pelos primeiros padrões
        for (int i = 0; i < Math.min(limiteTrivial, I.length); i++) {
            triviais.addAll(I[i].getItens());
        }

        construirRanking(I);
    }

    private void construirRanking(Pattern[] I) {
        for (int pos = 0; pos < I.length; pos++) {
            for (int item : I[pos].getItens()) {
                rankingItems.put(item, pos + 1);
            }
        }
    }

    // =====================
    //   Registro interno
    // =====================
    private PatternInfo registrarInterno(Pattern pattern,
                                         String operador,
                                         ArrayList<HashSet<Integer>> genealogia) {

        Integer id = ids.get(pattern);
        if (id == null) {
            id = nextId++;
            ids.put(pattern, id);
        }

        PatternInfo info = banco.get(id);

        if (info != null) return info;

        info = new PatternInfo(id, pattern, operador, genealogia, triviais, rankingItems);
        banco.put(id, info);

        return info;
    }

    // =====================
    //   Registro padrão
    // =====================
    public void registrar(Pattern pattern, String operador, ArrayList<HashSet<Integer>> genealogia) {
        PatternInfo info = registrarInterno(pattern, operador, genealogia);
        historicoIds.add(info.getId());
    }

    public void registrarK(Pattern pattern, String operador, ArrayList<HashSet<Integer>> genealogia) {
        PatternInfo info = registrarInterno(pattern, operador, genealogia);
        historicoKIds.add(info.getId());
    }

    // Sobrecarregados sem genealogia
    public void registrar(Pattern pattern, String operador) {
        registrar(pattern, operador, new ArrayList<>());
    }

    public void registrarK(Pattern pattern, String operador) {
        registrarK(pattern, operador, new ArrayList<>());
    }

    // =====================
    //     Exportação
    // =====================
    public void exportarCSV(String diretorioBase, String base, String algoritmo, int k) {

        String nomeArquivo = String.format(
                "%s/%s_%s_%s.csv",
                diretorioBase,
                base,
                algoritmo,
                java.time.LocalDateTime.now().toString().replace(":", "-")
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            //algoritmo,base,k,
            writer.write("id,timestamp,categoria,operador,qualidade,itens_rank,genealogia,is_pk\n");

            for (int id : historicoIds) {
                PatternInfo info = banco.get(id);

                writer.write(String.format(Locale.US,
                        "%d,%d,%s,%s,%.5f,%s,%s,%d\n",
//%s,%s,%d,
//                        algoritmo,
//                        base,
//                        k,
                        info.getId(),
                        info.getTimestamp(),
                        info.getCategoria(),
                        info.getOperador(),
                        info.getPattern().getQualidade(),
                        info.getItensComRanking(),
                        info.getGenealogiaSerializada().isBlank() ? "[]" : info.getGenealogiaSerializada(),
                        0
                ));
            }

            for (int id : historicoKIds) {
                PatternInfo info = banco.get(id);

                writer.write(String.format(Locale.US,
                        "%s,%s,%d,%d,%d,%s,%s,%.5f,%s,%s,%d\n",
                        algoritmo,
                        base,
                        k,
                        info.getId(),
                        info.getTimestamp(),
                        info.getCategoria(),
                        info.getOperador(),
                        info.getPattern().getQualidade(),
                        info.getItensComRanking(),
                        info.getGenealogiaSerializada(),
                        1
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
