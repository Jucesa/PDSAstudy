package sd;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TPSD {
    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo (WRacc valoriza subgrupos menores enquanto Qg, maiores
     * @return Pattern[] - conjunto final de individuos melhorados
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, String tipoAvaliacao) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao); // inicializa P com todos os indivíduos de 1D

        int naoMudou = 0;
        while (naoMudou < 3) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // Atualiza o indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo (WRacc valoriza subgrupos menores enquanto Qg, maiores
     * @param dimensaoMaxInicial int - dimensao maxima dos itens inicializados em P0
     * @return Pattern[] - conjunto final de individuos melhorados
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, String tipoAvaliacao, int dimensaoMaxInicial) {
        Pattern[] P = INICIALIZAR.aleatorio1_D(tipoAvaliacao, dimensaoMaxInicial, D.numeroItensUtilizados); // inicializa P com todos os indivíduos de 1D
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        int naoMudou = 0;
        while (naoMudou < 3) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // Atualiza o indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    public static Pattern[] runKillTheWeek(int quantidadeTorneio, int tentativasMelhoria, int falhasAteParada, int dimensaoMaxInicial, String tipoAvaliacao) {
        Pattern[] P = INICIALIZAR.aleatorio1_D(tipoAvaliacao, dimensaoMaxInicial, D.numeroItensUtilizados); // inicializa P com todos os indivíduos de 1D
        Arrays.sort(P);
        int naoMudou = 0;
        while (naoMudou < falhasAteParada) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // atualiza o pior indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param maxIndividuosGerados int -criterio de parada
     * @param p double - chance inicial de selecionar um item solo acima da particao
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo (WRacc valoriza subgrupos menores enquanto Qg, maiores
     * @return Pattern[] - conjunto final de individuos melhorados
     */
    public static Pattern[] runP(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, double p, String tipoAvaliacao, int k) {
        Pattern[] Pk = new Pattern[k];
        Pattern[] P = null;

        //Inicializa Pk com indivíduos vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        P = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade())); //sorteia para poder substituir os piores
        SELECAO.salvandoRelevantes(Pk, P);

        int gerou = 0;
        int particao = P.length - 1;

        //criterio parada: individuos gerados
        while (gerou < maxIndividuosGerados) {
            System.out.println("Partição: " + particao);
            // Seleciona ou um indice acima ou abaixo de particao
            double aDouble = Const.random.nextDouble(0, 1);
            int index;
            if(aDouble < p) {
                index =  SELECAO.torneioNparticao(P, quantidadeTorneio, 0, particao);
            } else {
                index = SELECAO.torneioNparticao(P, quantidadeTorneio, particao+1, P.length-1);
            }
            Pattern individuo = P[index];
            System.out.println("Individuo para melhorar: " + individuo.getItens());
            System.out.println("Qualidade individuo: " + individuo.getQualidade());

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(individuo.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                System.out.println("\nNovo individuo gerado: " + paux.getItens());
                System.out.println("Qualidade individuo: " + paux.getQualidade());

                System.out.println("\nTentando substituir por individuo: " + P[particao].getItens());
                System.out.println("Qualidade individuo: " + P[particao].getQualidade());
                if (paux.getQualidade() > P[particao].getQualidade()) {
                    P[particao] = paux;
                    particao--;
                    System.out.println("\nSubstituiu! Partição: " + particao);
                } else {
                    System.out.println("\nNão substituiu. Partição: " + particao);
                }
                gerou++;
                System.out.println("Individuos gerados: " +gerou);
            }
        }

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));
        return P;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(TPSD.class.getName());

        String diretorioBases = Const.CAMINHO_BASES;
        String[] bases = {diretorioBases+"/alon-clean50-pn-width-2.csv",
                diretorioBases+"/ENEM2014.csv",
                diretorioBases+"/matrixBinaria-Global-100-p.csv"};

        String base = bases[2];

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }
        D.SEPARADOR = ","; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed
        D.GerarDpDn("p");

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_Qg;
        int tentativasMelhoria = 10;
        int maxIndividuosGerados = 1000;

        System.out.println("Algoritmo com Torneio: 10");
        Pattern[] p = runP(10, tentativasMelhoria, maxIndividuosGerados, 0.9, metricaAvaliacao, k);
        Avaliador.imprimirRegras(p, k);
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
