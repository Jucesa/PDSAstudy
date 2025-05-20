package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Threshold {

    //PBSD -> Problem Based Subgroup Discovery
    //TF -> Tournament = Fixed
    //TV -> Tournament = Variable

    //ST -> Sorting threshold = True
    //SF -> Sorting threshold = False

    protected static final int maxGeracoesSemMelhoraPk = 3;
    protected static final int maxReinicializacoes = 3;

    protected static final int maxTorneio = 100;

    /**Inicializa população da seguinte forma:
     * 90% dimensão 1,
     * 10% aleatório com número de itens igual a dimensão média dos top-k DPs e utilizando apenas os itens dos top-k DPs.
     *@author Júlio Limeira
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param Pk Pattern[] - k melhores DPs: referência para criar metade da população
     * @param tamanhoPopulacao int - tamanho da população
     * @param I Pattern[] - Array de Pattern D1 ordenados por avaliação
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorioD1_Pk(String tipoAvaliacao, int tamanhoPopulacao, Pattern[] Pk, Pattern[] I){
        int numeroDimensoes =  (int) Avaliador.avaliarMediaDimensoes(Pk, Pk.length);
        if(numeroDimensoes < 2){
            numeroDimensoes = 2;
        }

        //População que será retornada
        Pattern[] P0 = new Pattern[tamanhoPopulacao];

        //Adicionando os 90% melhores de D1
        int i = 0;
        for(; i < 9*tamanhoPopulacao/10; i++){
            P0[i] = I[i];
        }

        //Coletanto todos os itens distintos da população Pk.
        HashSet<Integer> itensPk = new HashSet<>();
        for (Pattern pattern : Pk) {
            itensPk.addAll(pattern.getItens());
        }

        int[] itensPkArray = new int[itensPk.size()];

        Iterator<Integer> iterator = itensPk.iterator();
        int n = 0;
        while(iterator.hasNext()){
            itensPkArray[n++] = iterator.next();
        }

        //Gerando parte da população utilizando os itens presentes em Pk
        for(int j = i; j < tamanhoPopulacao; j++){
            HashSet<Integer> itens = new HashSet<>();

            while(itens.size() < numeroDimensoes){
                if(itensPkArray.length > numeroDimensoes){
                    itens.add(itensPkArray[Const.random.nextInt(itensPkArray.length)]);
                }else{ //Caso especial: existem menos itens nas top-k do que o tamanho exigido para o invíduo
                    if(Const.random.nextBoolean()){
                        itens.add(itensPkArray[Const.random.nextInt(itensPkArray.length)]);
                    }else{
                        itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
                    }
                }

            }

            P0[j] = new Pattern(itens, tipoAvaliacao);
        }
        return P0;
    }

    protected static void ordenaP(Pattern[] P){
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));
    }

    /**Sorteia um pattern da seguinte forma:
     * (1) Compara um double aleatorio com a proporção threshold/P.length
     * (2) Caso o double seja menor que esta proporção, torneio de pattern do indice 0 até threshold-1
     *  caso contrário, torneio de pattern de hreshold até P.length-1
     * (3) Retorna o Pattern vencedor do torneio
     @param P Pattern[] -> população original de todos Pattern
     @param quantidadeTorneio int -> quantidade de individuos a disputar o torneio
     @param threshold int -> separação de P
     */
    protected static Pattern sortear(Pattern[] P, int quantidadeTorneio, int threshold) {
        Pattern aux;

        double aDouble = Const.random.nextDouble(0, 0.99);

        if (aDouble < (float) threshold / P.length) {
            if(threshold > 1){
                aux = P[SELECAO.torneioN(P, quantidadeTorneio, 0, threshold-1)];
            } else {
                aux = P[0];
            }
        } else {
            aux = P[SELECAO.torneioN(P, quantidadeTorneio, threshold, P.length - 1)];
        }

        return aux;
    }

    /**checa se um pattern paux substitui outro no indice threshold-1
     @param P Pattern[] -> população original de todos Pattern
     @param paux Pattern -> pattern candidato a substituição
     @param threshold int -> separação de P
     */
    protected static boolean substituirIndividuo(Pattern[] P, Pattern paux, int threshold) {
        if(paux.getQualidade() > P[threshold - 1].getQualidade()){
        //if (SELECAO.ehRelevante(paux, P)) {
            P[threshold - 1] = paux;
            return true;
        }
        return false;
    }

    protected static void avaliarPopulacao(Pattern[] P, int torneio, int threshold, int numeroIndividuos) {
//        System.out.println();
//
//
//        double melhorQualidade = Arrays.stream(P).mapToDouble(Pattern::getQualidade).max().getAsDouble();
//
//        double mediaQualidade = Arrays.stream(P)
//                .mapToDouble(Pattern::getQualidade)
//                .average()
//                .orElse(0.0);
//
//        double mediaTamanho =  Arrays.stream(P)
//                .mapToDouble(pattern -> pattern.getItens().size()).average().getAsDouble();
//
//        System.out.println("------ Avaliação da População ------");
//        logging(torneio, threshold, numeroIndividuos);
//        System.out.println("Melhor qualidade: " + melhorQualidade);
//        System.out.println("Qualidade média: " + mediaQualidade);
//        System.out.println("Tamanho médio dos indivíduos: " + mediaTamanho);
    }

    protected static void logging(int torneio, int threshold, int numeroIndividuos){
        System.out.println("Torneio: " + torneio);
        System.out.println("Threshold: " + threshold);
        System.out.println("Testes: " + numeroIndividuos);
    }

    /**retorna o conjunto ordenado de subgrupos melhorados que estão abaixo do threshold
     @param P Pattern[] -> população original de todos Pattern
     @param threshold int -> indice de divisão entre itens e subgrupos de P
    */
    protected static Pattern[]  modifiedSGs(Pattern[] P, int threshold){
        Pattern[] subgroups = new Pattern[P.length - threshold];
        System.arraycopy(P, threshold, subgroups, 0, P.length - threshold);
        ordenaP(subgroups);
        return subgroups;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Threshold.class.getName());

        String base = "pastas/bases/Bases BIO 10/zsun-pn-freq-2.CSV";
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
        String metricaAvaliacao = Const.METRICA_Qg;
        double similaridade = 0.5;
        int torneio = 20;
        int passo = 10;
        Pattern[] p;

//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nPBSD_TV5_SF");
//        p = PBSD_TV_SF.run(passo, similaridade, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//
//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nPBSD_TV5_ST");
//        p = PBSD_TV_ST.run(passo, similaridade, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);


        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nPBSD_TF5_ST");
        p = PBSD_TF_ST.run(5, similaridade, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);

        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nPBSD_TF10_ST");
        p = PBSD_TF_ST.run(10, similaridade, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);


        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nPBSD_TF20_ST");
        p = PBSD_TF_ST.run(20, similaridade, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);


        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nPBSD_TF50_ST");
        p = PBSD_TF_ST.run(50, similaridade, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);

        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nPBSD_TF100_ST");
        p = PBSD_TF_ST.run(100, similaridade, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);


//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nPBSD_TF20_SF");
//        p = PBSD_TF_SF.run(torneio, similaridade, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//
//        System.out.println("\n\nPBSD_Fibonacci_ST");
//        p = PBSD_Fibonacci_ST.run(similaridade, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//
//        System.out.println("\n\nPBSD_Fibonacci_SF");
//        p = PBSD_Fibonacci_SF.run(similaridade, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);

        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nSSDP+");
        p = SSDPmais.run(k, metricaAvaliacao, similaridade, 3600);
        Avaliador.imprimirRegras(p, k);
    }
}
