/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exatos;


import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.SELECAO;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import simulacoes.DPinfo;

/**
 *
 * @author Tarcisio Lucas
 */
public class GulosoD {
    public static Pattern[] run(int k, int pWidth, String tipoAvaliacao, double similaridade, double maxTimeSegundos, int maxDimensao){
        long t0 = System.currentTimeMillis(); //Initial time
        Pattern[] Pk = new Pattern[k];                
        Pattern[] P = new Pattern[pWidth];
        int indiceP = 0;
        
        //Inicializa Pk com indivíduos vazios
        for(int i = 0; i < Pk.length;i++){
            Pk[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
        }
        
        
        ///////////////////////////////////////////////////////////
        // D1
        ///////////////////////////////////////////////////////////
        System.out.println("### Dimensão 1:");
        for(int i = 0; i < D.numeroItensUtilizados; i++){            
            //Gerando DPs e atribuindo a Pk (critério DP+)
            HashSet<Integer> itens = new HashSet<>();
            itens.add(D.itensUtilizados[i]);
            Pattern p = new Pattern(itens, tipoAvaliacao);
            P[indiceP++] = p;
            if(indiceP == P.length){
                Arrays.sort(P);
                SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                //Testa finalização por tempo.
                double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                    return Pk;
                }                
                indiceP = 0;
                P = new Pattern[pWidth];               
            }                         
        }      
       
        double qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        ///////////////////////////////////////////////////////////
        // D2
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 2){
            System.out.println("### Dimensão 2:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1+1; i2 < D.numeroItensUtilizados; i2++){
                    //Gerando DPs e atribuindo a Pk (critério DP+)
                    HashSet<Integer> itens = new HashSet<>();
                    itens.add(D.itensUtilizados[i1]);
                    itens.add(D.itensUtilizados[i2]);
                    Pattern p = new Pattern(itens, tipoAvaliacao);
                    P[indiceP++] = p;
                    if(indiceP == P.length){
                        Arrays.sort(P);
                        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                        //Testa finalização por tempo.
                        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                        if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                            return Pk;
                        }
                        indiceP = 0;
                        P = new Pattern[pWidth];                 
                    }                      
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        
        ///////////////////////////////////////////////////////////
        // D3
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 3){
            System.out.println("### Dimensão 3:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        //Gerando DPs e atribuindo a Pk (critério DP+)
                        HashSet<Integer> itens = new HashSet<>();
                        itens.add(D.itensUtilizados[i1]);
                        itens.add(D.itensUtilizados[i2]);
                        itens.add(D.itensUtilizados[i3]);
                        Pattern p = new Pattern(itens, tipoAvaliacao);
                        P[indiceP++] = p;
                        if(indiceP == P.length){
                            Arrays.sort(P);
                            SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                            //Testa finalização por tempo.
                            double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                            if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                return Pk;
                            }
                            indiceP = 0;
                            P = new Pattern[pWidth];                    
                        }
                        
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
              
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D4
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 4){
            System.out.println("### Dimensão 4:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            //Gerando DPs e atribuindo a Pk (critério DP+)
                            HashSet<Integer> itens = new HashSet<>();
                            itens.add(D.itensUtilizados[i1]);
                            itens.add(D.itensUtilizados[i2]);
                            itens.add(D.itensUtilizados[i3]);
                            itens.add(D.itensUtilizados[i4]);
                            Pattern p = new Pattern(itens, tipoAvaliacao);
                            P[indiceP++] = p;
                            if(indiceP == P.length){
                                Arrays.sort(P);
                                SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                //Testa finalização por tempo.
                                double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                    return Pk;
                                }
                                indiceP = 0;
                                P = new Pattern[pWidth];                    
                            }
                            
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
            
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D5
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 5){
            System.out.println("### Dimensão 5:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                //Gerando DPs e atribuindo a Pk (critério DP+)
                                HashSet<Integer> itens = new HashSet<>();
                                itens.add(D.itensUtilizados[i1]);
                                itens.add(D.itensUtilizados[i2]);
                                itens.add(D.itensUtilizados[i3]);
                                itens.add(D.itensUtilizados[i4]);
                                itens.add(D.itensUtilizados[i5]);
                                Pattern p = new Pattern(itens, tipoAvaliacao);
                                P[indiceP++] = p;
                                if(indiceP == P.length){
                                    Arrays.sort(P);
                                    SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                    //Testa finalização por tempo.
                                    double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                    if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                        return Pk;
                                    }
                                    indiceP = 0;
                                    P = new Pattern[pWidth];                    
                                }
                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }

        ///////////////////////////////////////////////////////////
        // D6
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 6){
            System.out.println("### Dimensão 6:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                for(int i6 = i5 + 1; i6 < D.numeroItensUtilizados; i6++){
                                    //Gerando DPs e atribuindo a Pk (critério DP+)
                                    HashSet<Integer> itens = new HashSet<>();
                                    itens.add(D.itensUtilizados[i1]);
                                    itens.add(D.itensUtilizados[i2]);
                                    itens.add(D.itensUtilizados[i3]);
                                    itens.add(D.itensUtilizados[i4]);
                                    itens.add(D.itensUtilizados[i5]);
                                    itens.add(D.itensUtilizados[i6]);
                                    Pattern p = new Pattern(itens, tipoAvaliacao);
                                    P[indiceP++] = p;
                                    if(indiceP == P.length){
                                        Arrays.sort(P);
                                        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                        //Testa finalização por tempo.
                                        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                        if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                            return Pk;
                                        }
                                        indiceP = 0;
                                        P = new Pattern[pWidth];                    
                                    }
                                   
                                }                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D7
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 7){
            System.out.println("### Dimensão 7:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                for(int i6 = i5 + 1; i6 < D.numeroItensUtilizados; i6++){
                                    for(int i7 = i6 + 1; i7 < D.numeroItensUtilizados; i7++){
                                        //Gerando DPs e atribuindo a Pk (critério DP+)
                                        HashSet<Integer> itens = new HashSet<>();
                                        itens.add(D.itensUtilizados[i1]);
                                        itens.add(D.itensUtilizados[i2]);
                                        itens.add(D.itensUtilizados[i3]);
                                        itens.add(D.itensUtilizados[i4]);
                                        itens.add(D.itensUtilizados[i5]);
                                        itens.add(D.itensUtilizados[i6]);
                                        itens.add(D.itensUtilizados[i7]);
                                        Pattern p = new Pattern(itens, tipoAvaliacao);
                                        P[indiceP++] = p;
                                        if(indiceP == P.length){
                                            Arrays.sort(P);
                                            SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                            //Testa finalização por tempo.
                                            double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                            if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                                return Pk;
                                            }
                                            indiceP = 0;
                                            P = new Pattern[pWidth];                    
                                        }
                                        
                                    }
                                }                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D8
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 8){
            System.out.println("### Dimensão 8:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                for(int i6 = i5 + 1; i6 < D.numeroItensUtilizados; i6++){
                                    for(int i7 = i6 + 1; i7 < D.numeroItensUtilizados; i7++){
                                        for(int i8 = i7 + 1; i8 < D.numeroItensUtilizados; i8++){
                                            //Gerando DPs e atribuindo a Pk (critério DP+)
                                            HashSet<Integer> itens = new HashSet<>();
                                            itens.add(D.itensUtilizados[i1]);
                                            itens.add(D.itensUtilizados[i2]);
                                            itens.add(D.itensUtilizados[i3]);
                                            itens.add(D.itensUtilizados[i4]);
                                            itens.add(D.itensUtilizados[i5]);
                                            itens.add(D.itensUtilizados[i6]);
                                            itens.add(D.itensUtilizados[i7]);
                                            itens.add(D.itensUtilizados[i8]);
                                            Pattern p = new Pattern(itens, tipoAvaliacao);
                                            P[indiceP++] = p;
                                            if(indiceP == P.length){
                                                Arrays.sort(P);
                                                SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                                //Testa finalização por tempo.
                                                double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                                if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                                    return Pk;
                                                }
                                                indiceP = 0;
                                                P = new Pattern[pWidth];                    
                                            }
                                            
                                        }
                                    }
                                }                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D9
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 9){
            System.out.println("### Dimensão 9:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                for(int i6 = i5 + 1; i6 < D.numeroItensUtilizados; i6++){
                                    for(int i7 = i6 + 1; i7 < D.numeroItensUtilizados; i7++){
                                        for(int i8 = i7 + 1; i8 < D.numeroItensUtilizados; i8++){
                                            for(int i9 = i8 + 1; i9 < D.numeroItensUtilizados; i9++){                                    
                                                //Gerando DPs e atribuindo a Pk (critério DP+)
                                                HashSet<Integer> itens = new HashSet<>();
                                                itens.add(D.itensUtilizados[i1]);
                                                itens.add(D.itensUtilizados[i2]);
                                                itens.add(D.itensUtilizados[i3]);
                                                itens.add(D.itensUtilizados[i4]);
                                                itens.add(D.itensUtilizados[i5]);
                                                itens.add(D.itensUtilizados[i6]);
                                                itens.add(D.itensUtilizados[i7]);
                                                itens.add(D.itensUtilizados[i8]);
                                                itens.add(D.itensUtilizados[i9]);
                                                Pattern p = new Pattern(itens, tipoAvaliacao);
                                                P[indiceP++] = p;
                                                if(indiceP == P.length){
                                                    Arrays.sort(P);
                                                    SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                                    //Testa finalização por tempo.
                                                    double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                                    if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                                        return Pk;
                                                    }
                                                    indiceP = 0;
                                                    P = new Pattern[pWidth];                    
                                                }
                                               
                                            }
                                        }
                                    }
                                }                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
        
        //Se Pk não evoluiu de uma dimensão para outra, parar a busca.
        if(Pk[Pk.length-1].getQualidade() <= qualidadePiorIndividuoTopK){
            return Pk;
        }else{
            qualidadePiorIndividuoTopK = Pk[Pk.length-1].getQualidade();
        }
        
        ///////////////////////////////////////////////////////////
        // D10
        ///////////////////////////////////////////////////////////
        if(maxDimensao >= 10){
            System.out.println("### Dimensão 10:");
            for(int i1 = 0; i1 < D.numeroItensUtilizados; i1++){            
                for(int i2 = i1 + 1; i2 < D.numeroItensUtilizados; i2++){
                    for(int i3 = i2 + 1; i3 < D.numeroItensUtilizados; i3++){
                        for(int i4 = i3 + 1; i4 < D.numeroItensUtilizados; i4++){
                            for(int i5 = i4 + 1; i5 < D.numeroItensUtilizados; i5++){
                                for(int i6 = i5 + 1; i6 < D.numeroItensUtilizados; i6++){
                                    for(int i7 = i6 + 1; i7 < D.numeroItensUtilizados; i7++){
                                        for(int i8 = i7 + 1; i8 < D.numeroItensUtilizados; i8++){
                                            for(int i9 = i8 + 1; i9 < D.numeroItensUtilizados; i9++){ 
                                                for(int i10 = i9 + 1; i10 < D.numeroItensUtilizados; i10++){    
                                                    //Gerando DPs e atribuindo a Pk (critério DP+)
                                                    HashSet<Integer> itens = new HashSet<>();
                                                    itens.add(D.itensUtilizados[i1]);
                                                    itens.add(D.itensUtilizados[i2]);
                                                    itens.add(D.itensUtilizados[i3]);
                                                    itens.add(D.itensUtilizados[i4]);
                                                    itens.add(D.itensUtilizados[i5]);
                                                    itens.add(D.itensUtilizados[i6]);
                                                    itens.add(D.itensUtilizados[i7]);
                                                    itens.add(D.itensUtilizados[i8]);
                                                    itens.add(D.itensUtilizados[i9]);
                                                    itens.add(D.itensUtilizados[i10]);
                                                    Pattern p = new Pattern(itens, tipoAvaliacao);
                                                    P[indiceP++] = p;
                                                    if(indiceP == P.length){
                                                        Arrays.sort(P);
                                                        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                                                        //Testa finalização por tempo.
                                                        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
                                                        if(maxTimeSegundos > 0 && tempo > maxTimeSegundos){
                                                            return Pk;
                                                        }
                                                        indiceP = 0;
                                                        P = new Pattern[pWidth];                    
                                                    }
                                                    
                                                }
                                            }
                                        }
                                    }
                                }                                
                            }                        
                        }                    
                    }
                }                              
            }
        }else{
            return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
        }
               
        return GulosoD.finalizarBusca(Pk, P, indiceP, tipoAvaliacao, similaridade);
    }
    
    private static Pattern[] finalizarBusca(Pattern[] Pk, Pattern[] P, int indiceP, String tipoAvaliacao, double similaridade){
        for(int i = indiceP; i < P.length; i++){
            P[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
        }
        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;       
    }
    
    
    
    public static void main(String args[]) throws FileNotFoundException{
        //====================================================================
        //== CONFIGURATION ===================================================
        //====================================================================
        //CSV database path
        
        String caminho = "C:\\Users\\Tarcisio  Lucas\\Documents\\NetBeansProjects\\DP4\\pastas\\bases\\"; 
        String nomeBase = "alon-clean50-pn-width-2.csv";
        //String nomeBase = "matrixBinaria-Global-1000-p.csv";
        //String nomeBase = "ENEM2014_RACA.csv";
        //String nomeBase = "ENEM2014_81_NOTA_1M.csv";
        //String nomeBase = "pns_pessoa_AVC_P.csv";
        //String nomeBase = "pns_pessoa_Cancer_P.csv";
        //String nomeBase = "pns_pessoa_Coracao_P.csv";
        //String nomeBase = "pns_pessoa_Depressao_P.csv";
        //String nomeBase = "pns_pessoa_Diabetes_P.csv";
        //String nomeBase = "pns_pessoa_uf_melhor_sRaca.csv";
        //String nomeBase = "yelp_labelled.csv";
        //String nomeBase = "imdb_labelled.csv";
        //String nomeBase = "amazon_cells_labelled.csv";
        //String nomeBase = "evasao.csv";
        String caminhoBase = caminho + nomeBase;
       
        D.SEPARADOR = ","; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed
        
        //Parameters of the algorithm
        int k = 10; //number of DPs
        Pattern.maxSimulares = k;
        Pattern.medidaSimilaridade = Const.SIMILARIDADE_JACCARD;
        //Pattern.medidaSimilaridade = Const.SIMILARIDADE_SOKAL_MICHENER;
        String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_QG; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC_OVER_SIZE; //Fitness
        //String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC; //Fitness
        
        Pattern.ITENS_OPERATOR = Const.PATTERN_AND;
        //Pattern.ITENS_OPERATOR = Const.PATTERN_OR;
        
        double maxTimeSecond =  60*1; //1 minutos
        int maxDimensao = 5;
        double similaridade = 0.90;
        //====================================================================
        //= END ==============================================================
        //====================================================================
        
        
        System.out.println("Carregando base de dados...");
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
        //String[] filtrarValores = {"0"};
        String[] filtrarValores = null;
        //Filtrar itens
        //String[][] filtrarAtributosValores = new String[2][2];
        //filtrarAtributosValores[0][0] = "D001";
        //filtrarAtributosValores[0][1] = "2";
        //filtrarAtributosValores[1][0] = "E01002";
        //filtrarAtributosValores[1][1] = "8";
        String[][] filtrarAtributosValores = null;
                
        //Executar filtros        
        D.filtrar(filtrarAtributos, filtrarValores, filtrarAtributosValores);
        
        
        
        Pattern.numeroIndividuosGerados = 0; //Initializing count of generated individuals
        System.out.println("### Base:" + D.nomeBase + "(|I|=" + D.numeroItens + 
                "; |A|=" + D.numeroAtributos +
                "; |D|=" + D.numeroExemplos +
                "; |D+|=" + D.numeroExemplosPositivo +
                "; |D-|=" + D.numeroExemplosNegativo +
                 ")"); //database name
        
        
        
        System.out.println("SSDPmais executando...");
        //Rodando SSDP
        long t0 = System.currentTimeMillis(); //Initial time
        //Pattern[] p = SSDPmais.run(k, tipoAvaliacao, similaridade);
        Pattern[] p = GulosoD.run(k, D.numeroItensUtilizados, tipoAvaliacao, similaridade, maxTimeSecond, maxDimensao);
        double tempo = (System.currentTimeMillis() - t0)/1000.0; //time
        
        System.out.println("\n### Top-k DPs:");
        Avaliador.imprimirRegras(p, k); 
        
        //Informations about top-k DPs:  
        System.out.println("### Base:" + D.nomeBase + "(|I|=" + D.numeroItens + 
                "; |A|=" + D.numeroAtributos +
                "; |D+|=" + D.numeroExemplosPositivo +
                "; |D-|=" + D.numeroExemplosNegativo +
                 ")"); //database name
        System.out.println("Average " + tipoAvaliacao + ": " + Avaliador.avaliarMedia(p, k));
        System.out.println("Time(s): " + tempo);
        System.out.println("Average size: " + Avaliador.avaliarMediaDimensoes(p,k));        
        System.out.println("Coverage of all Pk DPs in relation to D+: " + Avaliador.coberturaPositivo(p, k)*100 + "%");
        System.out.println("Cover Redundancy D+: " + DPinfo.coverRedundancy(p, null));
        System.out.println("Description Redundancy Density (|itensUnicos|/|itens|): " + DPinfo.descritionRedundancyDensity(p));
        System.out.println("Description Redundancy Item Dominador (|itemDominador|/k): " + DPinfo.descritionRedundancyDominator(p));
        
        System.out.println("Number of individuals generated: " + Pattern.numeroIndividuosGerados);
        
        System.out.println("\n### Top-k DPs - SIMILARES:");
        //Avaliador.imprimirRegrasSimilares(p, k); 
        String[] metricas = {
            Const.METRICA_QUALIDADE,
            //Const.METRICA_SIZE,
            //Const.METRICA_WRACC,
            //Const.METRICA_Qg,
            //Const.METRICA_DIFF_SUP,
            //Const.METRICA_LIFT,
            //Const.METRICA_CHI_QUAD,
            //Const.METRICA_P_VALUE,
            //Const.METRICA_SUPP_POSITIVO,
            //Const.METRICA_SUPP_NEGATIVO,
            //Const.METRICA_COV,
            Const.METRICA_CONF            
        };
        Avaliador.imprimirRegras(p, k, metricas, false, false, true);
              
    }
}
