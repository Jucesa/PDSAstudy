/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sd;

import dp.Avaliador;
import dp.D;
import dp.Pattern;
import evolucionario.SELECAO;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import util.Estatistica;

/**
 *
 * @author tarcisio_pontes
 */
public class Exaustivo {
    //Máximo até dimensão 3
    public static Pattern [] run(String tipoAvaliacao, int k, int tempoMaximoMinutos){
        Pattern[] DP1k = new Pattern[k];
        Pattern[] DP2k = new Pattern[k];
        Pattern[] DP3k = new Pattern[k];
        
        //Itens de 1D
        int quantidadeTestes = 0;
        long t0Simulacao = System.currentTimeMillis(); //Utilizado no critério de parada.
        
        long t0Dimensao1 = System.currentTimeMillis();          
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            HashSet<Integer> itens = new HashSet<>();
            itens.add(D.itensUtilizados[i]);
            Pattern p = new Pattern(itens, tipoAvaliacao);
            quantidadeTestes++;
            if(i == k){
                Arrays.sort(DP1k);
            }
            if(i < k){
                DP1k[i] = p;
            }            
            else if(p.getQualidade() > DP1k[DP1k.length-1].getQualidade()){
                if(SELECAO.ehRelevante(p, DP1k)){
                    DP1k[DP1k.length-1] = p;
                    Arrays.sort(DP1k);
                }
            }
        }
        double tempoDimensao1 = (double) (System.currentTimeMillis() - t0Dimensao1)/1000.0;
        System.out.println("");
        Avaliador.imprimir(DP1k, k);
        System.out.println("\n1D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP1k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP1k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao1);

        


        //Itens de 2D
        long t0Dimensao2 = System.currentTimeMillis();
        quantidadeTestes = 0;
        int indiceKprimeiros = 0;
        System.out.print("\nD2-i: ");
        for(int i = 0; i < D.numeroItensUtilizados; i++){            
            if(i % 100 == 0){
                System.out.print(i+",");
            }
            
            for(int j = i+1; j < D.numeroItensUtilizados; j++){
                HashSet<Integer> itens = new HashSet<>();
                itens.add(D.itensUtilizados[i]);
                itens.add(D.itensUtilizados[j]);
                Pattern p = new Pattern(itens, tipoAvaliacao);
                quantidadeTestes++;
                if(indiceKprimeiros <= k){
                        if(indiceKprimeiros < k){
                            DP2k[indiceKprimeiros] = p;
                        }else{
                            Arrays.sort(DP2k);
                        }  
                        indiceKprimeiros++;
                }else if(p.getQualidade() > DP2k[DP2k.length-1].getQualidade()){
                    if(SELECAO.ehRelevante(p, DP2k)){
                        DP2k[DP2k.length-1] = p; 
                        Arrays.sort(DP2k);                 
                    }
                }               
            }             
        }        
        double tempoDimensao2 = (double) (System.currentTimeMillis() - t0Dimensao2)/1000.0;
        System.out.println("");     
        Avaliador.imprimir(DP2k, k);
        System.out.println("\n2D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP2k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP2k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao2);
        
        
        
        //Itens de 3D
        long t0Dimensao3 = System.currentTimeMillis();
        quantidadeTestes = 0;
        indiceKprimeiros = 0;
        System.out.print("\nD3-i: ");        
        for(int i = 0; i < D.numeroItensUtilizados; i++){            
            System.out.print(i+",");
            
            //Critério de tempo de simulação total. Não pode ser maior que tempoMaximoMinutos
            double tempoTotal = (double) (System.currentTimeMillis() - t0Simulacao)/(1000.0*60.0);
            if(tempoTotal > tempoMaximoMinutos){
                //Caso não tenha inicializado a busca na dimensão 3, retornar os melhores entre DP1 e DP2. Mas ao mesmo tempo garantir que pelo menos o DP2 será realizado até o fim!
                if(DP3k[DP3k.length-1] == null){
                    Pattern[] Pk = SELECAO.selecionarMelhoresDistintos(DP1k, DP2k);                       
                    return Pk;
                }
                else{
                    break;
                }                
            }
            for(int j = i+1; j < D.numeroItensUtilizados; j++){
                for(int l = j+1; l < D.numeroItensUtilizados; l++){                    
                    HashSet<Integer> itens = new HashSet<>();
                    itens.add(D.itensUtilizados[i]);            
                    itens.add(D.itensUtilizados[j]);
                    itens.add(D.itensUtilizados[l]);
                    Pattern p = new Pattern(itens, tipoAvaliacao);
                    quantidadeTestes++;
                    if(indiceKprimeiros <= k){
                        if(indiceKprimeiros < k){
                            DP3k[indiceKprimeiros] = p;
                        }else{
                            Arrays.sort(DP3k);
                        }  
                        indiceKprimeiros++;
                    }else if(p.getQualidade() > DP3k[DP3k.length-1].getQualidade()){
                        if(SELECAO.ehRelevante(p, DP3k)){
                            DP3k[DP3k.length-1] = p;
                            Arrays.sort(DP3k);
                        }
                    }        
                }               
            }
        }       
        
        double tempoDimensao3 = (double) (System.currentTimeMillis() - t0Dimensao3)/1000.0;        
        System.out.println("");
        Avaliador.imprimir(DP3k, k);
        System.out.println("3D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP3k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP3k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao3);

        Pattern[] Pk = SELECAO.selecionarMelhoresDistintos(DP1k, DP2k, DP3k);               
        
        return Pk;
    }


    public static void main(String[] args) throws FileNotFoundException{                   
        
//        //Bases
//        String[] caminhoBases = new String[11];
//        caminhoBases[0] = "C:\\BD\\alon_mediana_pn.txt";
//        caminhoBases[1] = "C:\\BD\\west_mediana.txt";      
//        caminhoBases[2] = "C:\\BD\\chiaretti_111_12625_2_74p54n_mediana.txt";      
//        caminhoBases[3] = "C:\\BD\\chowdary_104_22283_2_62p42n_mediana.txt";      
//        caminhoBases[4] = "C:\\BD\\golub_72_7129_2_47p25n_mediana.txt";      
//        caminhoBases[5] = "C:\\BD\\gordon_181_12533_150p31n_mediana.txt";      
//        caminhoBases[6] = "C:\\BD\\gravier_168_2905_2_111p57n_mediana.txt";      
//        caminhoBases[7] = "C:\\BD\\pomeroy_60_7128_2_21p39n_mediana.txt";      
//        caminhoBases[8] = "C:\\BD\\singh_102_12600_2_52p20n_mediana.txt";      
//        caminhoBases[9] = "C:\\BD\\sun_179_54613_2_156p23n_mediana.txt";      
//        caminhoBases[10] = "C:\\BD\\tian_173_12625_2_137p36n_mediana.txt";   
//        caminhoBases[11] = "C:\\BD\\yeoh_248_12625_2_79p169n_mediana.txt";   
        
        //Bases
        String[] caminhoBases = new String[5];
        
//        caminhoBases[0] = "C:\\BD\\alon_mediana_pn.txt";
//        caminhoBases[1] = "C:\\BD\\west_mediana.txt";      
//        caminhoBases[2] = "C:\\BD\\chiaretti_111_12625_2_74p54n_mediana.txt";      
//        caminhoBases[3] = "C:\\BD\\chowdary_104_22283_2_62p42n_mediana.txt";      
//        caminhoBases[4] = "C:\\BD\\golub_72_7129_2_47p25n_mediana.txt";      
//        caminhoBases[5] = "C:\\BD\\gordon_181_12533_150p31n_mediana.txt";     
        //Mudar tamanho do array e i = 0 para atualizado!
        caminhoBases[0] = "C:\\BD\\gravier_168_2905_2_111p57n_mediana.txt";      
        caminhoBases[1] = "C:\\BD\\pomeroy_60_7128_2_21p39n_mediana.txt";      
        caminhoBases[2] = "C:\\BD\\singh_102_12600_2_52p20n_mediana.txt";      
        caminhoBases[3] = "C:\\BD\\tian_173_12625_2_137p36n_mediana.txt";   
        caminhoBases[4] = "C:\\BD\\yeoh_248_12625_2_79p169n_mediana.txt";   
        
        //Utilizando todos os itens disponíveis.       
        for(int i = 0; i < caminhoBases.length; i++){            
            System.out.println("\n\n### Simulando em: " + caminhoBases[i]);
            Pattern.numeroIndividuosGerados = 0; //Zerando contagem de indivíduos gerados
            D.CarregarArquivo(caminhoBases[i], D.TIPO_CSV);
            
            D.numeroItensUtilizados = D.numeroItens;
            D.itensUtilizados = new int[D.numeroItensUtilizados];
            for(int j = 0; j < D.numeroItensUtilizados; j++){
                D.itensUtilizados[j] = j;
            }
            
            int k = 20;
            int tempoMaximoMinutos = 24*60;
            String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_QG;
            
            long t0 = System.currentTimeMillis();   
            Pattern[] p = Exaustivo.run(tipoAvaliacao, k, tempoMaximoMinutos);
            double tempo = (double) (System.currentTimeMillis() - t0);
            
            System.out.println("\n\n>>>>>>>> RESULTADO FINAL \n### Base:" + caminhoBases[i]);
            System.out.println("\nMelhores:\n");
            Avaliador.imprimir(p, k);
            System.out.println("Qualidade média: " + Avaliador.avaliarMedia(p, k));
            System.out.println("Dimensão média: " + Avaliador.avaliarMediaDimensoes(p,k));        
            System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(p, k));
            System.out.println("Tentativas: " + Pattern.numeroIndividuosGerados);
            System.out.println("Tempo: " + tempo);
            System.out.println("\n-----------------------------------------------------\n-----------------------------------------------------\n\n");
        }    
    }
}
