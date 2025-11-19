package newSD.logging;

import dp.Pattern;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PatternTracker {

    private final Set<Integer> triviais;
    private final Map<Integer, Integer> rankingItems = new HashMap<>();

    private void construirRanking(Pattern[] I) {
        for (int pos = 0; pos < I.length; pos++) {
            for (int item : I[pos].getItens()) {
                rankingItems.put(item, pos + 1); // rank começa em 1
            }
        }
    }


    public PatternTracker(Pattern[] I, int limiteTrivial) {
        PatternInfo.resetContador();
        this.triviais = new HashSet<>();
        for (int i = 0; i < Math.min(limiteTrivial, I.length); i++) {
            triviais.addAll(I[i].getItens());
        }
        construirRanking(I);
    }

    public PatternTracker(Pattern[] I, int limiteTrivial, int k) {
        PatternInfo.resetContador();
        this.triviais = new HashSet<>();
        for (int i = 0; i < Math.min(limiteTrivial, I.length); i++) {
            triviais.addAll(I[i].getItens());
        }
        construirRanking(I);
    }

    private final List<PatternInfo> historico = new ArrayList<>();

    /**
     * Registra um novo PatternInfo no histórico.
     */
    public PatternInfo registrar(Pattern pattern, String operador) {
        PatternInfo info = new PatternInfo(pattern, operador, triviais, rankingItems);
        historico.add(info);
        return info;
    }

    /**
     * Retorna o histórico completo dos padrões criados.
     */
    public List<PatternInfo> getHistorico() {
        return historico;
    }


    /**
     * Exporta o histórico como CSV para análise temporal.
     * Exemplo de saída:
     * id,timestamp,categoria,itens
     * 1,1731162001000,Trivial,[1,2,3]
     */
    public void exportarCSV(String diretorioBase, String algoritmo) {
        String nomeArquivo = String.format(
                "%s/patterns_evolucao_%s_%s.csv",
                diretorioBase,
                algoritmo,
                java.time.LocalDateTime.now().toString().replace(":", "-")
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("id,timestamp,categoria,operador,qualidade,itens_rank\n");

            for (PatternInfo info : historico) {
                writer.write(String.format(Locale.US,"%d,%d,%s,%s,%.5f,%s\n",
                        info.getId(),
                        info.getTimestamp(),
                        info.getCategoria(),
                        info.getOperador(),
                        info.getPattern().getQualidade(),
                        info.getItensComRanking()
                ));
            }

            //System.out.println("📄 Histórico exportado para: " + nomeArquivo);
        } catch (IOException e) {
            //System.err.println("Erro ao exportar histórico: " + e.getMessage());
        }
    }

    public void exportarCSV(String diretorioBase, String algoritmo, int k) {
        String nomeArquivo = String.format(
                "%s/patterns_evolucaoTop-%d_%s_%s.csv",
                diretorioBase,
                k,
                algoritmo,
                java.time.LocalDateTime.now().toString().replace(":", "-")
        );

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("id,timestamp,categoria,operador,qualidade,itens_rank\n");

            for (PatternInfo info : historico) {
                writer.write(String.format(Locale.US,"%d,%d,%s,%s,%.5f,%s\n",
                        info.getId(),
                        info.getTimestamp(),
                        info.getCategoria(),
                        info.getOperador(),
                        info.getPattern().getQualidade(),
                        info.getItensComRanking()
                ));
            }

            //System.out.println("📄 Histórico exportado para: " + nomeArquivo);
        } catch (IOException e) {
            //System.err.println("Erro ao exportar histórico: " + e.getMessage());
        }
    }
    //[and, mutacao, mutacao]
    //armazenar a primeira informação de criação
    //imprimir item como ranking relativo
}
