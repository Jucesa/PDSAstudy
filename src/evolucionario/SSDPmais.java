/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolucionario;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import newSD.logging.PatternTracker;
import simulacoes.DPinfo;

/**
 *
 * @author TARCISIO
 */
public class SSDPmais {

    public static Pattern[] run(int k, String tipoAvaliacao, double similaridade, double maxTimeSegundos) throws IOException {
        long t0 = System.currentTimeMillis(); //Initial time

        Pattern[] Pk = new Pattern[k];
        Pattern[] P = null;

        //Inicializa Pk com indivíduos vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
        }

        //Inicializa garantindo que P maior que Pk sempre! em bases pequenas isso nem sempre ocorre
        Pattern[] Paux = INICIALIZAR.D1(tipoAvaliacao); //P recebe população inicial

        if (Paux.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < Paux.length) {
                    P[i] = Paux[i];
                } else {
                    P[i] = Paux[Const.random.nextInt(Paux.length - 1)];
                }
            }
        } else {
            P = Paux;
        }

        Arrays.sort(P);


        PatternTracker tracker = new PatternTracker(P, P.length/100, Const.SAIDA_LOG, D.nomeBase, "SSDP+", k);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade, tracker);

        int numeroGeracoesSemMelhoraPk = 0;
        int indiceGeracoes = 1;

        //Laço do AG
        Pattern[] Pnovo;
        Pattern[] PAsterisco;

        int tamanhoPopulacao = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {//Controle número de reinicializações

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorio1_D_Pk(tipoAvaliacao, tamanhoPopulacao, Pk, tracker);
            }

            double mutationTax = 0.4; //Mutação inicia em 0.4. Crossover é sempre 1-mutationTax.

            while (numeroGeracoesSemMelhoraPk < 3) {

                if (indiceGeracoes == 1) {
                    Pnovo = CRUZAMENTO.ANDduasPopulacoes(P, P, tipoAvaliacao, tracker);
                    indiceGeracoes++;
                } else {
                    Pnovo = CRUZAMENTO.uniforme2Pop(P, mutationTax, tipoAvaliacao, tracker);
                }

                PAsterisco = SELECAO.selecionarMelhores(P, Pnovo);
                P = PAsterisco;

                int novosK = SELECAO.salvandoRelevantesDPmais(Pk, PAsterisco, similaridade, tracker);//Atualizando Pk e coletando número de indivíduos substituídos
                // Registrar Pk atualizado

                double tempo = (System.currentTimeMillis() - t0) / 1000.0; //time
                if (maxTimeSegundos > 0 && tempo > maxTimeSegundos) {
                    return Pk;
                }


                //Definição automática de mutação de crossover
                if (novosK > 0 && mutationTax > 0.0) {//Aumenta cruzamento se Pk estiver evoluindo e se mutação não não for a menos possível.
                    mutationTax -= 0.2;
                } else if (novosK == 0 && mutationTax < 1.0) {//Aumenta mutação caso Pk não tenha evoluido e mutação não seja maior que o limite máximo.
                    mutationTax += 0.2;
                }
                //Critério de parada: 3x sem evoluir Pk com taxa de mutação 1.0
                if (novosK == 0 && mutationTax == 1.0) {
                    numeroGeracoesSemMelhoraPk++;

                } else {
                    numeroGeracoesSemMelhoraPk = 0;
                }
            }

            numeroGeracoesSemMelhoraPk = 0;
        }
        tracker.close();
        return Pk;
    }

    public static void main(String[] args) throws IOException {
        //====================================================================
        //== CONFIGURATION ===================================================
        //====================================================================
        //CSV database path

        String caminho = "C:\\Users\\CoffeeLake_01\\Documents\\NetBeansProjects\\DP4_2019-09-06\\pastas\\bases\\";
        //String nomeBase = "pns-pessoa-Diabetes-P.CSV";
        //String nomeBase = "evasao.CSV";
        String nomeBase = "fibrose_10snips - F0F1HCC.csv";
        //String nomeBase = "ENEM2014_81_NOTA_10k.csv";
        //String nomeBase = "pns_pessoa_AVC_P.csv";
        //String nomeBase = "pns_pessoa_Cancer_P.csv";
        //String nomeBase = "pns_pessoa_Coracao_P.csv";
        //String nomeBase = "pns_pessoa_Depressao_P.csv";
        //String nomeBase = "pns_pessoa_Diabetes_P.csv";
        //String nomeBase = "pns-pessoa-uf-melhor.csv";
        //String nomeBase = "yelp_labelled.csv";
        //String nomeBase = "imdb_labelled.csv";
        //String nomeBase = "amazon_cells_labelled.csv";
        //String nomeBase = "evasao.csv";
        String caminhoBase = caminho + nomeBase;

        D.SEPARADOR = ";"; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed

        //Parameters of the algorithm
        int k = 10; //number of DPs
        Pattern.maxSimulares = 3;
        Pattern.medidaSimilaridade = Const.SIMILARIDADE_JACCARD;
        //Pattern.medidaSimilaridade = Const.SIMILARIDADE_SOKAL_MICHENER;

        String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_QG;
        Pattern.ITENS_OPERATOR = Const.PATTERN_AND;
        //Pattern.ITENS_OPERATOR = Const.PATTERN_OR;

        double maxTimeSecond = 60 * 60 * 24; //1 minutos 

        double similaridade = 0.90;
        //====================================================================
        //= END ==============================================================
        //====================================================================

        System.out.println("Carregando base de dados...");
        D.CarregarArquivo(caminhoBase, D.TIPO_CSV); //Loading database         
        D.GerarDpDn("HCC");
        //"6,80,104,116,134,145,151,153,156,256"; //target value
        //D.valorAlvo = "I-III";
        //D.valorAlvo = "IV-VII";

        //FILTRAR POR ATRIBUTO, VALORS E ITENS
        //Filtrar de atributos
        String[] filtrarAtributos = {"SEXO"};
        //String[] filtrarAtributos = null;
        //Filtrar de valores
        //String[] filtrarValores = {"", "NA"};
        String[] filtrarValores = null;
        //Filtrar itens
        String[][] filtrarAtributosValores = new String[2][2];
        filtrarAtributosValores[0][0] = "IL-10 -819";
        filtrarAtributosValores[0][1] = "CT";
        filtrarAtributosValores[1][0] = "IL-10 -592";
        filtrarAtributosValores[1][1] = "CA";
        //String[][] filtrarAtributosValores = null;

        //Executar filtros        
        D.filtrar(filtrarAtributos, filtrarValores, filtrarAtributosValores);

        Pattern.numeroIndividuosGerados = 0; //Initializing count of generated individuals
        System.out.println("### Base:" + D.nomeBase + "(|I|=" + D.numeroItens
                + "; |A|=" + D.numeroAtributos
                + "; |D|=" + D.numeroExemplos
                + "; |D+|=" + D.numeroExemplosPositivo
                + "; |D-|=" + D.numeroExemplosNegativo
                + ")"); //database name

        System.out.println("SSDPmais executando...");
        //Rodando SSDP
        long t0 = System.currentTimeMillis(); //Initial time
        //Pattern[] p = SSDPmais.run(k, tipoAvaliacao, similaridade);
        Pattern[] p = SSDPmais.run(k, tipoAvaliacao, similaridade, maxTimeSecond);
        double tempo = (System.currentTimeMillis() - t0) / 1000.0; //time

        System.out.println("\n### Top-k DPs:");
        Avaliador.imprimirRegras(p, k);

        //Informations about top-k DPs:  
        //System.out.println("### Base:" + D.nomeBase + "(|I|=" + D.numeroItens
        //        + "; |A|=" + D.numeroAtributos
        //        + "; |D+|=" + D.numeroExemplosPositivo
        //        + "; |D-|=" + D.numeroExemplosNegativo
        //        + ")"); //database name
        //System.out.println("Average " + tipoAvaliacao + ": " + Avaliador.avaliarMedia(p, k));
        //System.out.println("Time(s): " + tempo);
        //System.out.println("Average size: " + Avaliador.avaliarMediaDimensoes(p, k));
        System.out.println("Coverage of all Pk DPs in relation to D+: " + Avaliador.coberturaPositivo(p, k) * 100 + "%");
        //System.out.println("Cover Redundancy D+: " + DPinfo.coverRedundancy(p, null));
        //System.out.println("Description Redundancy Density (|itensUnicos|/|itens|): " + DPinfo.descritionRedundancyDensity(p));
        System.out.println("Description Redundancy Item Dominador (|itemDominador|/k): " + DPinfo.descritionRedundancyDominator(p));

        //System.out.println("Number of individuals generated: " + Pattern.numeroIndividuosGerados);

        System.out.println("\n### Top-k DPs - SIMILARES:");
        //Avaliador.imprimirRegrasSimilares(p, k); 
        String[] metricas = {
            Const.METRICA_QUALIDADE,
            Const.METRICA_SIZE,
            //Const.METRICA_WRACC,
            //Const.METRICA_Qg,
            //Const.METRICA_DIFF_SUP,
            //Const.METRICA_LIFT,
            //Const.METRICA_CHI_QUAD,
            Const.METRICA_P_VALUE,
            Const.METRICA_SUPP_POSITIVO,
            //Const.METRICA_SUPP_NEGATIVO,
            //Const.METRICA_COV,
            Const.METRICA_CONF
        };
        Avaliador.imprimirRegras(p, k, metricas, false, false, true);

    }
}
