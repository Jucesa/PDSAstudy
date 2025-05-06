package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;
import simulacoes.DPinfo;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Threshold {

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
                }else{//Caso especial: existem menos itens nas top-k do que o tamanho exigido para o invíduo
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

    protected static Pattern[] topK(Pattern[] P, int k){
        Pattern[] Pk = new Pattern[k];
        ordenaP(P);
        System.arraycopy(P, 0, Pk, 0, k);
        return Pk;
    }

    protected static boolean filhoPiorQuePais(Pattern pai1, Pattern pai2, Pattern filho){
        return filho.getQualidade() < pai1.getQualidade() && filho.getQualidade() < pai2.getQualidade();
    }

    protected static void ordenaP(Pattern[] P){
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));
    }

    protected static Pattern melhorarIndividuo(Pattern pai1, Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern novoIndividuo;
        Pattern pai2;

        double aDouble = Const.random.nextDouble(0, 1);

        if (aDouble < (float) particao / P.length) {
            if(particao > quantidadeTorneio){
                pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao - 1)];
            } else {
                pai2 = P[SELECAO.torneioN(P, particao-1, 0, particao - 1)];
            }
        } else {
            pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)];
        }

        novoIndividuo = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

        return novoIndividuo;
    }

    protected static Pattern sortear(Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern aux;

        double aDouble = Const.random.nextDouble(0, 0.99);

        if (aDouble < (float) particao / P.length) {
            if(particao > 1){
                aux = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao-1)];
            } else {
                aux = P[0];
            }
        } else {
            aux = P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)];
        }

        return aux;
    }


    protected static Pattern melhorarIndividuo(Pattern pai1, Pattern[] P, int quantidadeTorneio) {
        Pattern pai2 = P[SELECAO.torneioN(P, quantidadeTorneio)];
        return CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
    }

    protected static boolean substituirIndividuo(Pattern[] P, Pattern paux, int particao) {
        if(paux.getQualidade() > P[particao - 1].getQualidade()){
        //if (SELECAO.ehRelevante(paux, P)) {
            P[particao - 1] = paux;
            return true;
        }
        return false;
    }

    protected static double calculateOverallConfidence(Pattern[] P, int k) {
        double totalConfidence = 0;
        for (int i = 0; i < k && i < P.length; i++) {
            totalConfidence += DPinfo.conf(P[i]);
        }
        return totalConfidence / k;
    }

    protected static void avaliarPopulacao(Pattern[] P, int torneio, int threshold, int numeroIndividuos) {
        System.out.println();


        double melhorQualidade = Arrays.stream(P).mapToDouble(Pattern::getQualidade).max().getAsDouble();

        double mediaQualidade = Arrays.stream(P)
                .mapToDouble(Pattern::getQualidade)
                .average()
                .orElse(0.0);

        double mediaTamanho =  Arrays.stream(P)
                .mapToDouble(pattern -> pattern.getItens().size()).average().getAsDouble();

        System.out.println("------ Avaliação da População ------");
        logging(torneio, threshold, numeroIndividuos);
        System.out.println("Melhor qualidade: " + melhorQualidade);
        System.out.println("Qualidade média: " + mediaQualidade);
        System.out.println("Tamanho médio dos indivíduos: " + mediaTamanho);
    }

    protected static void logging(int torneio, int threshold, int numeroIndividuos){
        System.out.println("Torneio: " + torneio);
        System.out.println("Threshold: " + threshold);
        System.out.println("Testes: " + numeroIndividuos);
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Threshold.class.getName());

        String diretorioBases = Const.CAMINHO_BASES;
        String social = "/Humanitie and ssocial sciences";
        String bio140 = "/Bases BIO 140";
        String bioinformatica = "/Bioinformatic";
        String texto = "/Text mining";

        String[] bases = {diretorioBases+bioinformatica+"/alon-clean50-pn-width-2.csv",
                diretorioBases+social+"/ENEM2014-NOTA-100K.csv",
                diretorioBases+"/matrixBinaria-Global-100-p.csv",
                diretorioBases+texto+"/matrixBinaria-ALL-TERMS-59730-p.csv"
        };

        String base = "pastas/bases/Bases BIO 10/burczynski-pn-freq-2.CSV";
        D.SEPARADOR = ",";

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        Const.random = new Random(Const.SEEDS[5]); //Seed
        D.GerarDpDn("p");

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_WRACC;
        Pattern[] p = null;

//        System.out.println("\n\nFix20IgnAceitamais");
//        p = FixIgnAceitamais.run(20, 50, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);


//        System.out.println("\n\nFix20SortAceitamais");
//        p = FixSortAceitamais.run(10, 5, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
        Pattern.numeroIndividuosGerados = 0;
        System.out.println("\n\nFix50SortAceitamais");
        p = FixSortAceitamais.run(10, 100, 0.5, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);

//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nFix50SortAceitamais");
//        p = FixSortAceitamais.run(10, 1, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//
//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nFix50SortAceitamais");
//        p = FixSortAceitamais.run(10, 1, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);


//        System.out.println("\n\nFix100SortAceitamais");
//        p = FixSortAceitamais.run(10, 100, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);


//        System.out.println("\n\nVarIgnAceitamais");
//        p = VarIgnAceitamais.run(50, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);


//        System.out.println("\n\nVarSortAceitamais");
//        p = VarSortAceitamais.run(5, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//
//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\nVarSortAceitamais");
//        p = VarSortAceitamais.run(1, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(p, k);
//        Pattern.numeroIndividuosGerados = 0;

//        p = SSDPmais.run(k, metricaAvaliacao, 0.5, 1200);
//        System.out.println("\n\nSSDP+");
//        Avaliador.imprimirRegras(p, k);
    }
}
