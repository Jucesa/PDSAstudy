package newSD.logging;

import dp.Pattern;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

public class PatternTracker implements Closeable {

    private final Set<Integer> triviais;
    private final Map<Integer, Integer> rankingItems = new HashMap<>();

    // Pattern → id único (por identidade)
    private final Map<Pattern, Integer> ids = new IdentityHashMap<>();
    private int proxId = 0;

    // Escrita direta
    private final PrintWriter writer;

    // Reutilização de buffers (ThreadLocal)
    // TL_SB: Usado exclusivamente pelo método pai (writeCsvLine)
    private static final ThreadLocal<StringBuilder> TL_SB =
            ThreadLocal.withInitial(StringBuilder::new);

    // TL_GENE: Usado exclusivamente pelo método auxiliar de genealogia
    private static final ThreadLocal<StringBuilder> TL_GENE =
            ThreadLocal.withInitial(StringBuilder::new);

    public PatternTracker(Pattern[] I, int limiteTrivial, String dir, String base, String algoritmo, int k) throws IOException {

        this.triviais = new HashSet<>();
        construirRanking(I);

        // Marca triviais (baseado na frequência ou critério definido)
        for (int i = 0; i < Math.min(limiteTrivial, I.length); i++) {
            triviais.addAll(I[i].getItens());
        }

        // Prepara nome do arquivo com timestamp
        String nomeArquivo = String.format(
                "%s/%s_%s_top-%d_%s.csv",
                dir,
                algoritmo,
                base,
                k,
                LocalDateTime.now().toString().replace(":", "-")
        );
        File out = new File(nomeArquivo);
        // Garante que o diretório exista
        if (out.getParentFile() != null) {
            out.getParentFile().mkdirs();
        }

        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(out),
                        StandardCharsets.UTF_8
                ),
                32 * 1024 // Buffer de 32KB para escrita eficiente
        );

        this.writer = new PrintWriter(bw, false);

        writeHeader();

        // Registra população inicial
        for (Pattern p : I) {
            registrar(p, "INICIALIZAR", Collections.emptyList());
        }
    }

    private String categorizar(Pattern pattern) {
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

    private void writeHeader() {
        writer.write("id,timestamp,categoria,operador,qualidade,itens_rank,genealogia,is_pk");
        writer.println();
    }

    private void construirRanking(Pattern[] I) {
        for (int pos = 0; pos < I.length; pos++) {
            for (int item : I[pos].getItens()) {
                rankingItems.putIfAbsent(item, pos + 1);
            }
        }
    }

    private synchronized int ensureId(Pattern p) {
        Integer id = ids.get(p);
        if (id == null) {
            id = proxId++;
            ids.put(p, id);
        }
        return id;
    }

    // ----------------------------------------------------------
    // MÉTODOS DE REGISTRO
    // ----------------------------------------------------------

    public void registrar(Pattern p, String operador, List<Pattern> pais) {
        int id = ensureId(p);

        // Serializar genealogia (Pattern → id)
        int[] idsPais = new int[pais.size()];
        for (int i = 0; i < pais.size(); i++) {
            idsPais[i] = ensureId(pais.get(i));
        }

        try {
            writeCsvLine(id, p, operador, idsPais, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrar(Pattern p, String operador) {
        registrar(p, operador, Collections.emptyList());
    }

    public void registrarK(Pattern p, String operador, List<Pattern> pais) {
        int id = ensureId(p);

        // Serializar genealogia (Pattern → id)
        int[] idsPais = new int[pais.size()];
        for (int i = 0; i < pais.size(); i++) {
            idsPais[i] = ensureId(pais.get(i));
        }

        try {
            writeCsvLine(id, p, operador, idsPais, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarK(Pattern p, String operador) {
        registrarK(p, operador, Collections.emptyList());
    }

    // ----------------------------------------------------------
    // Escrita direta no CSV
    // ----------------------------------------------------------
    private synchronized void writeCsvLine(
            int id,
            Pattern p,
            String operador,
            int[] idsPais,
            int isPk
    ) throws IOException {

        // Pega o buffer principal (ThreadLocal) e limpa
        StringBuilder sb = TL_SB.get();
        sb.setLength(0);

        // 1. ID
        sb.append(id).append(',');

        // 2. Timestamp (segundos)
        sb.append(System.currentTimeMillis() / 1000).append(',');

        // 3. Categoria
        String categoria = categorizar(p);
        appendEscaped(sb, categoria).append(',');

        // 4. Operador
        appendEscaped(sb, operador).append(',');

        // 5. Qualidade
        sb.append(String.format(Locale.US, "%.4f", p.getQualidade())).append(',');

        // 6. Itens com Ranking
        // IMPORTANTE: Este método agora usa seu próprio StringBuilder interno,
        String itensRank = serializarItensComRanking(p);
        appendEscaped(sb, itensRank).append(',');

        // 7. Genealogia
        String gene = serializarGenealogia(idsPais);
        appendEscaped(sb, gene).append(',');

        // 8. is_pk
        sb.append(isPk);

        writer.write(sb.toString());
        writer.println();
    }

    private String serializarItensComRanking(Pattern p) {
        // CORREÇÃO APLICADA: Instanciar um novo StringBuilder localmente.
        // Isso isola este método do buffer principal usado em writeCsvLine.
        StringBuilder sb = new StringBuilder();

        List<Integer> itens = new ArrayList<>(p.getItens());
        Collections.sort(itens);

        boolean first = true;
        for (int item : itens) {
            int rank = rankingItems.getOrDefault(item, -1);
            if (!first) sb.append('|');
            first = false;
            sb.append(rank);
        }

        return sb.toString();
    }

    private String serializarGenealogia(int[] idsPais) {
        if (idsPais == null || idsPais.length == 0) return "";

        // Uso seguro: TL_GENE é diferente de TL_SB
        StringBuilder sb = TL_GENE.get();
        sb.setLength(0);

        for (int i = 0; i < idsPais.length; i++) {
            if (i > 0) sb.append('|');
            sb.append(idsPais[i]);
        }

        return sb.toString();
    }

    private StringBuilder appendEscaped(StringBuilder sb, String v) {
        if (v == null || v.isEmpty()) {
            return sb;
        }
        boolean needQuote = v.contains(",") || v.contains("\"") || v.contains("\n");
        if (!needQuote) {
            sb.append(v);
            return sb;
        }
        sb.append('"');
        sb.append(v.replace("\"", "\"\""));
        sb.append('"');
        return sb;
    }

    @Override
    public synchronized void close() throws IOException {
        writer.flush();
        writer.close();
    }
}