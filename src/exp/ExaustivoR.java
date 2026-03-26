package exp;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public class ExaustivoR {

    public static Pattern[] run(int dimension, int k, String tipoAvaliacao, int dimInicio) {
        long inicioGeral = System.currentTimeMillis();

        // Base inicial (1D): todos os itens avaliados e ordenados
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);

        // Inicializa o top-k
        Pattern[] Pk = new Pattern[k];
        for (int i = 0; i < k; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // --- Dimensão 1 ---
        if (dimInicio <= 1) { // só executa se for para começar da dimensão 1
            long inicioDim = System.currentTimeMillis();
            System.out.println("Dimensão 1 -> total: " + I.length);

            for (Pattern p : I) {
                SELECAO.salvandoRelevanteDPmaisSingle(Pk, p, 0.5);
            }

            Arrays.sort(Pk);
            double tempoDim = (System.currentTimeMillis() - inicioDim) / 1000.0;
            System.out.println("Melhor padrão (1D): " + Pk[0].getQualidade());
            System.out.printf("Tempo 1D: %.3f s%n", tempoDim);
        } else {
            System.out.println("Pulando dimensões até " + dimInicio + "D...");
        }

        // --- Dimensões superiores ---
        for (int d = 2; d <= dimension; d++) {
            // só processa se d >= dimInicio
            if (d < dimInicio) continue;

            long inicioDim = System.currentTimeMillis();
            double frac = 1.0 / Math.pow(2, d - 1);
            int qtd = Math.max(1, (int) (I.length * frac));

            // Seleciona apenas os melhores padrões 1D para formar combinações
            Pattern[] melhores = Arrays.copyOfRange(I, 0, qtd);
            int[] itensMelhores = new int[melhores.length];
            for (int i = 0; i < melhores.length; i++) {
                itensMelhores[i] = melhores[i].getItens().iterator().next();
            }

            System.out.println("\nDimensão " + d + " -> combinando " + qtd + " melhores itens");
            combinar(itensMelhores, d, 0, new HashSet<>(), tipoAvaliacao, Pk);

            Arrays.sort(Pk);
            double tempoDim = (System.currentTimeMillis() - inicioDim) / 1000.0;
            System.out.println("Melhor padrão (" + d + "D): " + Pk[0].getQualidade());
            System.out.printf("Tempo %dD: %.3f s%n", d, tempoDim);
        }

        double tempoTotalSegundos = (System.currentTimeMillis() - inicioGeral) / 1000.0;
        double tempoTotalMinutos = tempoTotalSegundos / 60.0;

        System.out.println("\n=== RESUMO FINAL ===");
        System.out.printf("Tempo total: %.3f s (%.2f min)%n", tempoTotalSegundos, tempoTotalMinutos);
        System.out.println("Melhor padrão final: " + Pk[0].getQualidade());

        return Pk;
    }



    /**
     * Gera todas as combinações de 'tamanho' elementos do vetor 'itens',
     * testando cada combinação via salvandoRelevanteDPmaisSingle.
     */
    private static void combinar(int[] itens, int tamanho, int inicio, HashSet<Integer> atual,
                                 String tipoAvaliacao, Pattern[] Pk) {

        if (atual.size() == tamanho) {
            Pattern pnovo = new Pattern(new HashSet<>(atual), tipoAvaliacao);
            SELECAO.salvandoRelevanteDPmaisSingle(Pk, pnovo, 0.5);
            return;
        }

        for (int i = inicio; i < itens.length; i++) {
            atual.add(itens[i]);
            combinar(itens, tamanho, i + 1, atual, tipoAvaliacao, Pk);
            atual.remove(itens[i]);
        }
    }


    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(ExaustivoR.class.getName());

        String base = "pastas/Bases BIO 10/alon-pn-freq-2.CSV";
        D.SEPARADOR = ",";

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        Const.random = new Random(Const.SEEDS[0]); //Seed
        D.GerarDpDn("p");


        int k = 10;
        String tipoAvaliacao = Const.METRICA_WRACC;
        Pattern[] pk  = run(4, k, tipoAvaliacao, 3);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("\n\nSSDP");
        Pattern[] ssdp = SSDPmais.run(k, tipoAvaliacao, 0.5, 1000);
        Avaliador.imprimirRegras(ssdp, k);
    }
}
