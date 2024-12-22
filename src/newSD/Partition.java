package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDP;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Partition {
    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param maxIndividuosGerados int -criterio de parada
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduos
     * @return Pattern[] - conjunto final de individuos
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        int gerou = 0;
        int tamanhoP = P.length;
        int particao = tamanhoP;
        float pFinal = 1;
        //criterio parada: individuos gerados
        while (gerou < maxIndividuosGerados && particao > k) {
            float p = (float) particao/tamanhoP;
            System.out.println("Probabilidade acima da partição: " + p);

            System.out.println("Partição: " + particao);
            //Seleciona um indice acima da particao
            int index = SELECAO.torneioN(P, quantidadeTorneio);

            Pattern individuo = P[index];
            System.out.println("Individuo para melhorar: " + individuo.getItens());
            System.out.println("Qualidade individuo: " + individuo.getQualidade());

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(individuo.getItens()); // Adiciona itens existentes de p a ser melhorado

                double aDouble = Const.random.nextDouble(0, 1);
                if(aDouble < p) {
                    System.out.println("Melhorando acima da partição");
                    itemNovo.add(SELECAO.torneioNparticao(P, quantidadeTorneio, 0, particao-1));
                } else {
                    System.out.println("Melhorando abaixo da partição");
                    itemNovo.add(SELECAO.torneioNparticao(P, quantidadeTorneio, particao, tamanhoP-1));
                }

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                System.out.println("\nNovo individuo gerado: " + paux.getItens());
                System.out.println("Qualidade individuo: " + paux.getQualidade());

                System.out.println("\nTentando substituir por individuo: " + P[particao-1].getItens());
                System.out.println("Qualidade individuo: " + P[particao-1].getQualidade());
                //if (paux.getQualidade() > P[particao-1].getQualidade()){
                if (SELECAO.ehRelevante(paux, P)) {
                    particao--;
                    P[particao] = paux;
                    System.out.println("\nSubstituiu! Partição: " + particao);
                    break;
                } else {
                    System.out.println("\nNão substituiu. Partição: " + particao);
                }
                gerou++;
                System.out.println("Individuos gerados: " + gerou);
            }
            System.out.println("Tentativas de melhoria encerradas\n");
            pFinal = p;
        }
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        System.out.println("\nAlgoritmo Finalizado\n");
        System.out.println("Individuos gerados: " + gerou);
        System.out.println("Probabilidade final: " + pFinal);

        return P;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Partition.class.getName());

        String diretorioBases = Const.CAMINHO_BASES;

        String[] bases = {diretorioBases+"/alon-clean50-pn-width-2.csv",
                diretorioBases+"/ENEM2014.csv",
                diretorioBases+"/matrixBinaria-Global-100-p.csv"};

        String base = bases[1];
        D.SEPARADOR = ","; //separator database
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
        int tentativasMelhoria = 10;
        int maxIndividuosGerados = 100000;
        int quantidadeTorneio = 10;
        System.out.println("Algoritmo com Torneio: " + quantidadeTorneio);
        Pattern[] p = run(quantidadeTorneio, tentativasMelhoria, maxIndividuosGerados, metricaAvaliacao, k);

        //String[] metricas = {metricaAvaliacao};
        Avaliador.imprimirRegras(p, k);
        System.out.println("-------------------------");

        Pattern[] pS = SSDP.run(k, metricaAvaliacao, 3600);
        Avaliador.imprimirRegras(pS, k);
    }
}

//tenta melhorar todos individuos de P0 - Italan
//        int piorIndividuo = P.length-1;
//        for(int i = 0; i < P.length; i++){
//            System.out.println(P[i].getItens());
//            System.out.println(P[i].getQualidade());
//            System.out.println(piorIndividuo);
//            Pattern p = P0[i];
//
//            for(int j = 0; j < tentativasMelhoria; j++){
//
//                HashSet<Integer> itemNovo = new HashSet<>(p.getItens());
//                itemNovo.add(SELECAO.torneioN(P0, quantidadeTorneio));
//
//                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
//
//                if (paux.getQualidade() > P[piorIndividuo].getQualidade()) {
//                    P[piorIndividuo] = paux;
//                    piorIndividuo--;
//                }
//                gerou++;
//            }
//        }

//        for (Pattern p : P) {
//            if (p.getItens().size() > 1) {
//                int temp = Arrays.stream(P).toList().indexOf(p) - 1;
//                if (temp >= 0) {
//                    particao = temp;
//                }
//            }
//        }
