package newSD;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ExecLogger implements AutoCloseable {
    private static final String REPORT_DIR = "relatorios/";
    private static final String COUNTER_FILE = REPORT_DIR + "report_counter.txt";
    private static int counter = 0;

    private final List<String> buffer = new ArrayList<>();
    private final File logFile;

    static {
        inicializarCounter();
    }

    public ExecLogger(String algoritmoNome) {
        try {
            int numeroRelatorio = ++counter;
            salvarCounter();
            Files.createDirectories(Paths.get(REPORT_DIR));
            logFile = new File(REPORT_DIR + "relatorio_execucao_"+algoritmoNome+"_" + numeroRelatorio + ".txt");

            buffer.add("Algoritmo: " + algoritmoNome);
            buffer.add("Arquivo: " + logFile.getName());
            buffer.add("=========================================");
        } catch (IOException e) {
            throw new RuntimeException("Erro criando relatório", e);
        }
    }

    private static void inicializarCounter() {
        try {
            Path path = Paths.get(COUNTER_FILE);
            if (Files.exists(path)) {
                String valor = Files.readString(path).trim();
                counter = Integer.parseInt(valor);
            } else {
                Files.createDirectories(Paths.get(REPORT_DIR));
                Files.write(path, "0".getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
            counter = 0;
        }
    }

    private static void salvarCounter() {
        try {
            Files.writeString(Paths.get(COUNTER_FILE), String.valueOf(counter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarInicializacao(int numero) {
        buffer.add("");
        buffer.add("Inicialização " + numero);
        buffer.add("------------------------");
    }

    public void registrarProgresso(int individuosGerados,
                                   String metrica,
                                   double mediaQualidade,
                                   double tamanhoMedioSG,
                                   double suporteGlobal,
                                   int qtdDistintos,
                                   Map<Integer, EstatisticasPopulacao.ItemStats> freqItens,
                                   int limiar) {

        buffer.add("Indivíduos gerados: " + individuosGerados);
        buffer.add("Métrica: " + metrica);
        buffer.add(String.format("Média qualidade: %.6f", mediaQualidade));
        buffer.add(String.format("Tamanho médio SG: %.6f", tamanhoMedioSG));
        buffer.add(String.format("Suporte positivo global: %.6f", suporteGlobal));
        buffer.add("Qtd SG distintos: " + qtdDistintos);
        buffer.add("Limiar (índice threshold): " + limiar);
        buffer.add("Itens na população (item -> freq, médiaQualidade):");

        for (Map.Entry<Integer, EstatisticasPopulacao.ItemStats> e : freqItens.entrySet()) {
            buffer.add(String.format("  Item %d -> freq=%d, médiaQualidade=%.6f",
                    e.getKey(), e.getValue().frequencia, e.getValue().getMediaQualidade()));
        }
        buffer.add("-----");
    }

    @Override
    public void close() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            buffer.forEach(writer::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
