package evolucionario;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Random;
import simulacoes.Base;
import simulacoes.DPinfo;

/**
 *
 * @author Jucesa
 */
//Poorly Developed Subgroup-discovery algorithm
public class PDSA {
    public static Pattern[] run(int k, String tipoAvaliacao, double maxTimeSegundos){
        long t0 = System.currentTimeMillis(); //Initial time

        Pattern[] Pk = new Pattern[k];
        Pattern[] P = null;

        //Inicializa garantindo que P maior que Pk sempre! em bases pequenas isso nem sempre ocorre
        Pattern[] Paux = INICIALIZAR.D1(tipoAvaliacao);//P recebe população inicial
        if(Paux.length < k){
            P = new Pattern[k];
            for(int i = 0; i < k; i++){
                if(i < Paux.length){
                    P[i] = Paux[i];
                }else{
                    P[i] = Paux[Const.random.nextInt(Paux.length-1)];
                }
            }
        }else{
            P = Paux;
        }


        Arrays.sort(P);
        System.arraycopy(P, 0, Pk, 0, k); //Inicializa Pk com os melhores indivíduos da população inicial


        int numeroGeracoesSemMelhoraPk = 0;
        int indiceGeracoes = 1;

        //Laço do AG
        Pattern[] Pnovo = null;
        Pattern[] PAsterisco = null;

        int tamanhoPopulacao = P.length;

        for(int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++){//Controle número de reinicializações
            if(numeroReinicializacoes > 0){
                P = INICIALIZAR.aleatorio1_D(tipoAvaliacao, D.numeroAtributos, tamanhoPopulacao);
            }

            double mutationTax = 0.4; //Mutação inicia em 0.1. Crossover é sempre 1-mutationTax.
            while(numeroGeracoesSemMelhoraPk < 3){

                if(indiceGeracoes == 1){
                    Pnovo = CRUZAMENTO.ANDduasPopulacoes(P, P, tipoAvaliacao);
                    indiceGeracoes++;
                }else{
                    Pnovo = CRUZAMENTO.uniforme2Pop(P, mutationTax, tipoAvaliacao);
                }
                PAsterisco = SELECAO.selecionarMelhores(P, Pnovo);
                P = PAsterisco;

                int novosK = SELECAO.salvandoRelevantes(Pk, PAsterisco);//Atualizando Pk e coletando número de indivíduos substituídos

                double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                    return Pk;
                }

                if(novosK > 0 && mutationTax > 0.0){//Aumenta cruzamento se Pk estiver evoluindo e se mutação não não for a menos possível.
                    mutationTax -= 0.2;
                }else if(novosK == 0 && mutationTax < 1.0){//Aumenta mutação caso Pk não tenha evoluido e mutação não seja maior que o limite máximo.
                    mutationTax += 0.2;
                }
                //Critério de parada: com taxa de mutação 1.0
                if(novosK == 0 && mutationTax == 1.0){
                    numeroGeracoesSemMelhoraPk++;
                }else{
                    numeroGeracoesSemMelhoraPk = 0;
                }

            }

            numeroGeracoesSemMelhoraPk = 0;
        }
        return Pk;
    }


    public static void main(String[] args) throws FileNotFoundException{
        //====================================================================
        //== CONFIGURATION ===================================================
        //====================================================================
        //CSV database path
        String caminho = "C:\\Users\\jc160\\IdeaProjects\\subgroupAlgorithms\\pastas\\bases\\";
        String nomeBase = "matrixBinaria-Global-100-p.csv";

        String caminhoBase = caminho + nomeBase;

        D.SEPARADOR = ","; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed

        //Parameters of the algorithm
        int k = 10; //number of DPs
        String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC; //Fitness


        double maxTimeSecond =  60*1; //1 minutos

        Pattern.ITENS_OPERATOR = Const.PATTERN_AND;
        //Pattern.ITENS_OPERATOR = Const.PATTERN_OR;

        //====================================================================
        //= END ==============================================================
        //====================================================================


        D.CarregarArquivo(caminhoBase, D.TIPO_CSV); //Loading database
        D.GerarDpDn("Y");

        //FILTRAR POR ATRIBUTO, VALORS E ITENS
        //Filtrar de atributos
        //String[] filtrarAtributos = {"C006"};
        String[] filtrarAtributos = {};
        //String[] filtrarAtributos = null;
        //Filtrar de valores
        String[] filtrarValores = {"", "NA"};
        //String[] filtrarValores = null;
        //Filtrar itens
//        String[][] filtrarAtributosValores = new String[2][2];
//        filtrarAtributosValores[0][0] = "D001";
//        filtrarAtributosValores[0][1] = "2";
//        filtrarAtributosValores[1][0] = "E01002";
//        filtrarAtributosValores[1][1] = "8";
        //String[][] filtrarAtributosValores = null;

        //Executar filtros
        D.filtrar(filtrarAtributos, filtrarValores, null);


        Pattern.numeroIndividuosGerados = 0; //Initializing count of generated individuals
        System.out.println("Rodando...");
        //Rodando PDSA
        long t0 = System.currentTimeMillis(); //Initial time
        Pattern[] p = PDSA.run(k, tipoAvaliacao, maxTimeSecond); //run PDSA
        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
        System.out.println("Finalizado");


        //Informations about top-k DPs:
//        System.out.println("### Base:" + D.nomeBase + "|D|=" + D.numeroExemplos + ",|D+|=" + D.numeroExemplosPositivo +
//                ",|D-|=" + D.numeroExemplosNegativo + ",|A|=" + D.numeroAtributos + ",|I|=" + D.numeroItensUtilizados); //database name
//        System.out.println("Average " + tipoAvaliacao + ": " + Avaliador.avaliarMedia(p, k));
//        System.out.println("Time(s): " + tempo);
//        System.out.println("Average size: " + Avaliador.avaliarMediaDimensoes(p,k));
//        System.out.println("Coverage of all k DPs in relation to D+: " + Avaliador.coberturaPositivo(p, k)*100 + "%");
//
//        System.out.println("Cover Redundancy D+: " + DPinfo.coverRedundancy(p, null));
//        System.out.println("Description Redundancy Density (|itensUnicos|/|itens|): " + DPinfo.descritionRedundancyDensity(p));
//        System.out.println("Description Redundancy Item Dominador (|itemDominador|/k): " + DPinfo.descritionRedundancyDominator(p));
//
//        System.out.println("Number of individuals generated: " + Pattern.numeroIndividuosGerados);
//        System.out.println("\n### Top-k DPs:");
        //Avaliador.imprimirRegras(p, k);


        //Informations about top-k DPs:
//        System.out.println("### Base:" + D.nomeBase + "(|D|=" + D.numeroExemplos + ",|D+|=" + D.numeroExemplosPositivo +
//                ",|D-|=" + D.numeroExemplosNegativo + ",|A|=" + D.numeroAtributos + ",|I|=" + D.numeroItensUtilizados + ")"); //database name
//        System.out.println("Average " + tipoAvaliacao + ": " + Avaliador.avaliarMedia(p, k));
//        System.out.println("Time(s): " + tempo);
//        System.out.println("Average size: " + Avaliador.avaliarMediaDimensoes(p,k));
//        System.out.println("Coverage of all Pk DPs in relation to D+: " + Avaliador.coberturaPositivo(p, k)*100 + "%");
//        System.out.println("Number of individuals generated: " + Pattern.numeroIndividuosGerados);
//        System.out.println("\n### Top-k DPs:");
//        //Avaliador.imprimirRegras(p, k);
//        String[] metricas = {
//            Const.METRICA_QUALIDADE,
//            //Const.METRICA_SIZE,
//            //Const.METRICA_WRACC,
//            //Const.METRICA_Qg,
//            //Const.METRICA_DIFF_SUP,
//            //Const.METRICA_LIFT,
//            //Const.METRICA_CHI_QUAD,
//            //Const.METRICA_P_VALUE,
//            //Const.METRICA_SUPP_POSITIVO,
//            //Const.METRICA_SUPP_NEGATIVO,
//            //Const.METRICA_COV,
//            //Const.METRICA_CONF
//        };
//        Avaliador.imprimirRegras(p, k, metricas, true, true, true);
//
//        System.out.println("### Universo de itens:");
//        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
//        Arrays.sort(I);
//        Avaliador.imprimirRegras(I, I.length, metricas, true, true, true);
//
    }


}

