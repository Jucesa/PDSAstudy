/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacoes;


import dp.Const;
import dp.Pattern;
import dp.RSS;
import evolucionario.INICIALIZAR;
import evolucionario.*;
import exatos.GulosoD;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import sd.SD;
/**
 *
 * @author Tarcisio Lucas
 */
public class SimulacaoGeralJAR {
        public static void main(String[] args) throws IOException, InterruptedException {
                
         
        Pattern.ITENS_OPERATOR = Const.PATTERN_AND;
        Pattern.medidaSimilaridade = Const.SIMILARIDADE_JACCARD;
        //String caminhoPastaArquivos = args[0];
        //int[] K = {5,10,20,50};
        //int[] K = {20};
        //int[] K = {1,5,10,20};
        //int[] K = {5,10};
        
//        args = new String[5];
//        args[0] = "5,10,20";
//        args[1] = "Qg";
//        args[2] = "10";
//        args[3] = "10";
//        args[4] = "SSDP,SSDPplusS10,SSDPplusS50";
//        
        
        
        System.out.print("K");
        int[] K = null;
        if(args[0].contains(",")){
            String[] Kstr = args[0].split(",");
            K = new int[Kstr.length];
            for(int i = 0; i < K.length; i++){
                K[i] = Integer.parseInt( Kstr[i] );
            }
        }else{
            K = new int[1];
            K[0] = Integer.parseInt(args[0]);
        }
        
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_QG;
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC;
        String tipoAvaliacao = args[1];
                
        //int numeroRepeticoes = 1;
        int numeroRepeticoes = Integer.parseInt( args[2] );
        
        //int numeroRepeticoes = 30;
        //double  tempoMaximoSegundosAlgoritmos = 60*60*3; //max 3h
        double tempoMaximoSegundosAlgoritmos = (double) Integer.parseInt( args[3] ); //max 3h
        
//        String[] algoritmos = {//Const.ALGORITMO_AG,
//            //Const.ALGORITMO_SD,                
//            
//            Const.ALGORITMO_SSDP,
//            Const.ALGORITMO_SSDPmaisS90,
//            Const.ALGORITMO_SSDPmaisS50,
//            Const.ALGORITMO_SSDPmaisS10,
            //Const.ALGORITMO_SD,
            //Const.ALGORITMO_SD_RSS
                
            //Const.ALGORITMO_SSDPmais,
            //Const.ALGORITMO_GulosoDplus,
                            //Const.ALGORITMO_SSDPmais10Pbest,
                            //Const.ALGORITMO_SSDPmais10Prb,
//                            Const.ALGORITMO_SSDPmais50Pbest,
//                            Const.ALGORITMO_SSDPmais50Prb,
//                            Const.ALGORITMO_SSDPmais90Pbest,
//                            Const.ALGORITMO_SSDPmais90Prb,
//                            };
           
        String[] algoritmos = null;
        if(args[4].contains(",")){
            algoritmos = args[4].split(",");
        }else{
            algoritmos = new String[1];
            algoritmos[0] = args[4];
        }
        
        SimulacaoGeral sg = new SimulacaoGeral(new File(Const.CAMINHO_INDICE));
        
        
        sg.run(K, numeroRepeticoes, algoritmos, ",", tipoAvaliacao, tempoMaximoSegundosAlgoritmos);       
        
     
    }
    
}
