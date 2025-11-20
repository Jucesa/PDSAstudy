package newSD.algorithm;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;
import newSD.logging.PatternTracker;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.OptionalDouble;

public abstract class PBSD_Base extends Threshold {

    private void header(Pattern[] P, int k, double similaridade, String tipoAvaliacao, int paramTorneio) {
        String className = this.getClass().getSimpleName();

        System.out.println("\n================= EXECUÇÃO =================");
        System.out.printf("%-32s: %s%n", "Base de dados", D.nomeBase);
        System.out.printf("%-32s: %s%n", "Métrica de avaliação", tipoAvaliacao);
        System.out.printf("%-32s: %d%n", "k (Top-K)", k);
        System.out.printf("%-32s: %.4f%n", "Similaridade", similaridade);
        System.out.printf("%-32s: %d%n", "Torneio inicial", 2);
        System.out.printf("%-32s: %s%n", "Classe", className);
        System.out.printf("%-32s: %d%n", "Parâmetro torneio", paramTorneio);
        System.out.printf("%-32s: %d%n", "População inicial", P.length);
        System.out.printf("%-32s: %d%n", "Máx. reinicializações", 3);
        System.out.printf("%-32s: %d%n", "Máx. gerações sem melhora Pk", 3);
        System.out.println("============================================");
    }

    private void body(Pattern[] P, Pattern[] Pk){
        logPk(Pk);
        System.out.println();
        System.out.println("-----------------------------");
        logP(P);
        System.out.println("-----------------------------");
    }

    private void footer(Pattern[] P, Pattern[] Pk){
        System.out.println("Distintos Pk");
        logDistintosPk(Pk);
        System.out.println("-----------------------------");
    }

    private void logP(Pattern[] P) {

        if (P == null || P.length == 0) {
            System.out.println(" - População vazia.");
            return;
        }

        double avgFitness = Avaliador.avaliarMedia(P, P.length);
        double avgSize = Avaliador.avaliarMediaDimensoes(P, P.length);
        OptionalDouble bestOpt = Arrays.stream(P)
                .mapToDouble(Pattern::getQualidade)
                .max();
        double bestFitness = bestOpt.getAsDouble();
        int distinctItemsCount = Avaliador.itensDistintosPk(P, P.length).size();

        System.out.println("População P:");
        System.out.println(" - Média de qualidade: " + avgFitness);
        System.out.println(" - Melhor qualidade: " + bestFitness);
        System.out.println(" - Tamanho médio: " + avgSize);
        System.out.println(" - Itens distintos: " + distinctItemsCount);

        logTopIndividuals(P, 3);
    }

    private void logTopIndividuals(Pattern[] P, int topN) {
        if (P == null || P.length == 0) {
            System.out.println("Nenhum indivíduo para mostrar.");
            return;
        }

        Pattern[] copy = Arrays.copyOf(P, P.length);

        Comparator<Pattern> comparatorDesc = Comparator
                .comparingDouble(Pattern::getQualidade)
                .reversed();

        Arrays.sort(copy, comparatorDesc);

        int limit = Math.min(topN, copy.length);
        System.out.println("Top " + limit + " indivíduos:");
        for (int i = 0; i < limit; i++) {
            Pattern pat = copy[i];
            double fitness = pat.getQualidade();
            System.out.println("  #" + (i + 1) + ": " + pat.getItens() + " fitness=" + fitness);
        }
    }


    private void logPk(Pattern[] Pk){

        double qualidadeMediaPk = Avaliador.avaliarMedia(Pk, Pk.length);
        double tamMedioPk = Avaliador.avaliarMediaDimensoes(Pk, Pk.length);
        System.out.println("População Pk:");
        System.out.println("Média de qualidade Pk: " + qualidadeMediaPk);
        System.out.println("Tamanho médio de Pk: " + tamMedioPk);
    }

    private void logDistintosPk(Pattern[] Pk) {
        HashSet<Integer> distintosPk = Avaliador.itensDistintosPk(Pk, Pk.length);

        // Transforma cada item em Pattern
        ArrayList<Pattern> itens = new ArrayList<>();
        for (Integer i : distintosPk) {
            HashSet<Integer> singleton = new HashSet<>();
            singleton.add(i);

            Pattern p = new Pattern(singleton, Pk[0].getTipoAvaliacao());
            itens.add(p);
        }

        // Ordena por qualidade decrescente
        itens.sort(Comparator.comparingDouble(Pattern::getQualidade).reversed());

        // Log ordenado
        for (Pattern p : itens) {
            Integer item = p.getItens().iterator().next(); // pega o único item do conjunto
            System.out.print("| item: " + item);
            System.out.print(" qualidade: " + p.getQualidade());
            System.out.print(" |\n");
        }
    }

    //mapear eventos
    //Evento
    // 1 - AND_ITEMxITEM_MELHORA
    // 2 - AND_ITEMxSUBGRUPO_MELHORA
    // 3 - AND_ITEMxITEM
    // 4 - AND_ITEMxSUBGRUPO

    /**
     * Mét0d0 abstrato para determinar o tamanho do torneio na geração atual.
     * Cada implementação (FIXO ou VARIÁVEL) define sua lógica.
     */
    protected abstract int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio);

    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) {

        Pattern[] P;

        Pattern[] Pk = new Pattern[k];

        // Inicializa Pk com indivíduos vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // População inicial
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        PatternTracker tracker = new PatternTracker(I, I.length/100, k);

        Arrays.sort(I);

        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < I.length) P[i] = I[i];
                else P[i] = I[Const.random.nextInt(I.length - 1)];
            }
        } else {
            P = I;
        }

        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        int numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        //header(P, k, similaridade, tipoAvaliacao, paramTorneio);
        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);

            while (numeroGeracoesSemMelhoraPk < 1000 || limiar == 0) {
                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2;
                String operador;

                // Sorteio r ~ U(0,1)
                double r = Const.random.nextDouble();

                // Probabilidade teórica
                double Pth = (double) limiar / (P.length);

                if (r < Pth) {
                    // Seleção acima do limiar: faixa 0..limiar-1
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                    operador = "AND_ITEMxITEM";
                } else {
                    // Seleção abaixo do limiar: faixa limiar..P.length-1
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
                    operador = "AND_ITEMxPATTERN";
                }
                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);
                ArrayList<HashSet<Integer>> gene = new ArrayList<>();
                gene.add(pai1.getItens());
                gene.add(pai2.getItens());

                tracker.registrar(paux, operador, gene);


                if (paux.getQualidade() >= P[limiar - 1].getQualidade() && limiar > 1) {
                    P[limiar - 1] = paux;
                    limiar--;
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    Arrays.sort(P, limiar, P.length);

                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk,
                            Arrays.copyOfRange(P, limiar, P.length),
                            similaridade, tracker);
                    //System.out.println(novosK);
                    if (novosK == 0) numeroGeracoesSemMelhoraPk++;
                    else numeroGeracoesSemMelhoraPk = 0;

                    // Atualiza tamanho do torneio usando mét0do polimórfico
                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);
                    //body(P, Pk);
                }
            }
        }
        //footer(P, Pk);

        tracker.exportarCSV("C:/Users/jc160/IdeaProjects/PDSAstudy/pastas/logRelatorioK",D.nomeBase, this.getClass().getSimpleName(), k);
        return Pk;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Threshold.class.getName());

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

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_WRACC;
        int quantidadeTorneio = 50;
        int passoTorneio = 5;

        System.out.println("\n\n\n\nFIXO");
        PBSD_FIXO fixo = new PBSD_FIXO();
        Pattern[] pk = fixo.run(quantidadeTorneio, 0.5, metricaAvaliacao, k);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);

    }
}
