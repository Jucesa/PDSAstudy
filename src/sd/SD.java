/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sd;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import dp.RSS;
import evolucionario.SSDP;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import simulacoes.DPinfo;

/**
 *
 * @author Marianna
 */
public class SD {
    public Pattern[] run(double min_support, int beam_width, String tipoAvaliacao, int k, double maxTimeSegundos){
        long t0 = System.currentTimeMillis(); //Initial time
        Pattern[] beam = new Pattern[beam_width];
        Pattern[] newBeam = new Pattern[beam_width];
        Pattern[] Pk = new Pattern[k];
        for(int i = 0; i < beam_width; i++){
            beam[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
            newBeam[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
        }
        
        boolean houveMelhoria = true;
        int ciclo = 0;
        while(houveMelhoria){
            //System.out.println("\nCiclo: " + ciclo++);
            double qualidadePiorAntes = beam[beam_width-1].getQualidade();
            for(int i = 0; i < beam_width; i++){
                
                //Finalização por tempo.
                double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                    System.arraycopy(newBeam, 0, Pk, 0, Pk.length);
                    return Pk;
                }
                
                for(int j = 0; j < D.numeroItensUtilizados; j++){
                    
                    HashSet<Integer> itens = (HashSet<Integer>)beam[i].getItens().clone();
                    itens.add(D.itensUtilizados[j]);
                    Pattern p = new Pattern(itens, tipoAvaliacao);
                    double suporte = (double)p.getTP()/(double)D.numeroExemplos;
                    boolean ehRelevante = this.ehRelevante(p, newBeam);
                    double qualidade = p.getQualidade();
                    //Se valor de qualidade é menor que zero ou menor que a qualidade do pior Pattern
                    //ele não será incluido no newBean
                    if(qualidade > newBeam[beam_width-1].getQualidade() &&
                       suporte >= min_support &&
                       ehRelevante){
                            newBeam[beam_width-1] = p;
                            Arrays.sort(newBeam);                
                    }                    
                }                
            }
            if(newBeam[beam_width-1].getQualidade() == qualidadePiorAntes){
                houveMelhoria = false;
            }
            beam = newBeam.clone();
//            Avaliador.imprimir(beam, beam_width);
            //System.out.println("Qualidade média: " + Avaliador.avaliarMedia(beam,k));
//            System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(beam,k));
        }        
        //Arrays.sort(beam);
        
        System.arraycopy(newBeam, 0, Pk, 0, Pk.length); 
        
        return Pk;
    }

    private static boolean ehRelevante(Pattern p, Pattern[] newBeam) {
        for(int i = 0; i < newBeam.length; i++){
            if(newBeam[i].sobrescreve(p) != -1){
                return false;
            }
        }
        return true;
    }
    
    
    public static void main(String[] args) throws FileNotFoundException{
           
         //====================================================================
        //== CONFIGURATION ===================================================
        //====================================================================
        //CSV database path 
       String caminho = "C:\\Users\\Tarcisio  Lucas\\Documents\\NetBeansProjects\\DP4\\pastas\\bases\\"; 
       //String nomeBase = "zENEM2014-NOTA-100K.csv";
       
        //String nomeBase = "yelp-labelled.csv";
       String nomeBase = "breast-cancer-pn.CSV";
       //String nomeBase = "ENEM2014_RACA_1M.csv";
       //String nomeBase = "matrixBinaria-Global-1000-p.csv";
       //String nomeBase = "burczynski-pn-freq-2.CSV";//"chiaretti-pn-freq-2.CSV";
       //String nomeBase = "pns_pessoa_Diabetes10p.csv";
       //String nomeBase = "toy_problem.csv";
       String caminhoBase = caminho + nomeBase;
       
        D.SEPARADOR = ","; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed
        
        //Parameters of the algorithm
        int k = 20; //number of DPs
        String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_QG; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC_NORMALIZED; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC_OVER_SIZE; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_DIFF_SUPP; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_GROW_RATE; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_LIFT; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_CHI_QUAD; //Fitness
        
        //====================================================================
        //= END ==============================================================
        //====================================================================
              
        
        D.CarregarArquivo(caminhoBase, D.TIPO_CSV); //Loading database         
        D.GerarDpDn("p");
        //"6,80,104,116,134,145,151,153,156,256"; //target value
        //D.valorAlvo = "I-III";
        //D.valorAlvo = "IV-VII";
        
        
        //FILTRAR POR ATRIBUTO, VALORS E ITENS
        //Filtrar de atributos
        //String[] filtrarAtributos = {"C006"};
        String[] filtrarAtributos = null;
        //Filtrar de valores
        //String[] filtrarValores = {"NA"};
        String[] filtrarValores = null;
        //Filtrar itens
//        String[][] filtrarAtributosValores = new String[2][2];
//        filtrarAtributosValores[0][0] = "D001";
//        filtrarAtributosValores[0][1] = "2";
//        filtrarAtributosValores[1][0] = "E01002";
//        filtrarAtributosValores[1][1] = "8";
        String[][] filtrarAtributosValores = null;
                
        //Executar filtros        
        D.filtrar(filtrarAtributos, filtrarValores, filtrarAtributosValores);
                  
        
        Pattern.numeroIndividuosGerados = 0; //Initializing count of generated individuals
        //System.out.println("Rodando...");
        //Rodando SSDP
        long t0 = System.currentTimeMillis(); //Initial time
        double min_support = Math.sqrt(D.numeroExemplosPositivo)/D.numeroExemplos; 
        int beamWidth = 2*k; //Número de soluções capturadas por ciclo
        double maxTimeSecond =  60*60; //1 hora
        
        SD sd = new SD();
        Pattern[] p = sd.run(min_support, beamWidth, tipoAvaliacao, 2*k, maxTimeSecond);
        p = RSS.run(p, k);
        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
        System.out.println("Finalizado");
        
        
        //Informations about top-k DPs:  
        System.out.println("### Base:" + D.nomeBase + "|D|=" + D.numeroExemplos + ",|D+|=" + D.numeroExemplosPositivo +
                ",|D-|=" + D.numeroExemplosNegativo + ",|A|=" + D.numeroAtributos + ",|I|=" + D.numeroItensUtilizados); //database name
        System.out.println("Average " + tipoAvaliacao + ": " + Avaliador.avaliarMedia(p, k));
        System.out.println("Time(s): " + tempo);
        System.out.println("Average size: " + Avaliador.avaliarMediaDimensoes(p,k));        
        System.out.println("Coverage of all k DPs in relation to D+: " + Avaliador.coberturaPositivo(p, k)*100 + "%");
        
        System.out.println("Cover Redundancy D+: " + DPinfo.coverRedundancy(p, null));
        System.out.println("Description Redundancy Density (|itensUnicos|/|itens|): " + DPinfo.descritionRedundancyDensity(p));
        System.out.println("Description Redundancy Item Dominador (|itemDominador|/k): " + DPinfo.descritionRedundancyDominator(p));
         
        System.out.println("Number of individuals generated: " + Pattern.numeroIndividuosGerados);
        System.out.println("\n### Top-k DPs:");
        Avaliador.imprimirRegras(p, k);
        
        
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
