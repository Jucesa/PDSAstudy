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

import java.util.*;

/**
 *
 * @author Tarcísio Pontes
 * @version 2.0
 * @since 26/06/17
 */
public class INICIALIZAR {
       
    /**Inicializa população com todas as possibilidades de indivíduos com apenas uma dimensão
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @return Pattern[] - nova população
     */
    public static Pattern[] D1(String tipoAvaliacao){
        Pattern[] P0 = new Pattern[D.numeroItensUtilizados];
        
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            HashSet<Integer> itens = new HashSet<>();
            itens.add(D.itensUtilizados[i]);
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
    
    /**Inicializa população com todas as possibilidades de indivíduos com apenas uma dimensão
     * mas antes juntanto itens considerados similares entre si com relação ao índice adotado
     * objetivo é diminuir volume de itens correlacionados sem perda de informação.
     * Ao mesmo tempo itens similares não aumentam o fitness de um subgrupo!
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param similarity double - grau que define quando dois itens são similares
     * @return Pattern[] - nova população
     */
    public static Pattern[] D1joinSimilarItens(String tipoAvaliacao, double similarity){
        Pattern[] P0 = new Pattern[D.numeroItensUtilizados];
        
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            HashSet<Integer> itens = new HashSet<>();
            itens.add(D.itensUtilizados[i]);
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
    
        
    
    /**Inicializa população de indivíduos aleatório com dimensão e tamanho especificados
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param numeroDimensoes int - tamanho fixo da dimensão de cada indivíduo
     * @param tamanhoPopulacao int - tamanho da população
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorioD(String tipoAvaliacao, int numeroDimensoes, int tamanhoPopulacao){
        Pattern[] P0 = new Pattern[tamanhoPopulacao];
        
        for(int i = 0; i < tamanhoPopulacao; i++){
            HashSet<Integer> itens = new HashSet<Integer>();
            while(itens.size() < numeroDimensoes){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
    
    
    
    /**Inicializa população da seguinte forma:
     * 90% aleatório com número de itens igual a dimensão média dos top-k DPs
     * 10% aleatório com número de itens igual a dimensão média dos top-k DPs e utilizando apenas os itens dos top-k DPs.
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param Pk Pattern[] - k melhores DPs: referência para criar metade da população
     * @param tamanhoPopulacao int - tamanho da população
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorio1_D_Pk(String tipoAvaliacao, int tamanhoPopulacao, Pattern[] Pk){
        //Ajeitar isso!!!
        int numeroDimensoes =  (int) Avaliador.avaliarMediaDimensoes(Pk, Pk.length);
        if(numeroDimensoes < 2){
            numeroDimensoes = 2;
        }
        
        //População que será retornada        
        Pattern[] P0 = new Pattern[tamanhoPopulacao];
        
        //Adicionando aleatoriamente com até numeroDimensoes itens
        int i = 0;
        for(; i < 9*tamanhoPopulacao/10; i++){
            HashSet<Integer> itens = new HashSet<Integer>();
            
            while(itens.size() < numeroDimensoes){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        
        
        //Coletanto todos os itens distintos da população Pk.
        HashSet<Integer> itensPk = new HashSet<>();
        for(int n = 0; n < Pk.length; n++){
            itensPk.addAll(Pk[n].getItens());
        }
        int[] itensPkArray = new int[itensPk.size()];
        
        Iterator iterator = itensPk.iterator();
        int n = 0;        
        while(iterator.hasNext()){
            itensPkArray[n++] = (int)iterator.next();
        }
        
        //Gerando parte da população utilizando os itens presentes em Pk        
        for(int j = i; j < tamanhoPopulacao; j++){
            HashSet<Integer> itens = new HashSet<Integer>();
            
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

    /**
     * Inicializa população da seguinte forma:
     * 90% vêm diretamente do vetor I (padrões de 1 dimensão)
     * 10% são gerados aleatoriamente usando os itens presentes nos top-k DPs (Pk).
     *
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo
     * @param tamanhoPopulacao int - tamanho da população
     * @param I Pattern[] - vetor de padrões unitários (1 item cada)
     * @param Pk Pattern[] - k melhores DPs
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorioD1_Pk(String tipoAvaliacao, int tamanhoPopulacao, Pattern[] I, Pattern[] Pk) {
        Pattern[] P0 = new Pattern[tamanhoPopulacao];

        // --- 1) Define quantidade de indivíduos a partir de I (90%) ---
        int qtdI = (9 * tamanhoPopulacao) / 10;
        qtdI = Math.min(qtdI, I.length); // não ultrapassa tamanho de I

        // Copia padrões unitários de I
        if (qtdI > 0) System.arraycopy(I, 0, P0, 0, qtdI);

        // --- 2) Coleta itens distintos de Pk ---
        HashSet<Integer> itensPkSet = new HashSet<>();
        for (Pattern p : Pk) {
            itensPkSet.addAll(p.getItens());
        }
        Integer[] itensPkArray = itensPkSet.toArray(new Integer[0]);

        // --- 3) Define número médio de dimensões ---
        int numeroDimensoes = (int) Avaliador.avaliarMediaDimensoes(Pk, Pk.length);
        numeroDimensoes = Math.max(numeroDimensoes, 2); // mínimo 2 dimensões

        // --- 4) Ajusta número de dimensões para não exceder itens disponíveis ---
        if (itensPkArray.length > 0 && numeroDimensoes > itensPkArray.length) {
            numeroDimensoes = itensPkArray.length;
        }

        // --- 5) Gera 10% restantes de forma aleatória ---
        for (int j = qtdI; j < tamanhoPopulacao; j++) {
            HashSet<Integer> itens = new HashSet<>();

            // Se houver itens em Pk, sorteia sem repetição dentro do padrão
            if (itensPkArray.length > 0) {
                List<Integer> listaItens = new ArrayList<>(Arrays.asList(itensPkArray));
                Collections.shuffle(listaItens, Const.random);
                itens.addAll(listaItens.subList(0, numeroDimensoes));
            } else {
                // fallback: sorteia itens globalmente
                while (itens.size() < numeroDimensoes) {
                    itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
                }
            }

            P0[j] = new Pattern(itens, tipoAvaliacao);
        }

        return P0;
    }




    /**Inicializa população de indivíduos aleatório com entre 1D e nD
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param limiteDimensoes int - indivíduos de dimensões entre 1 e nD
     * @param tamanhoPopulacao int - tamanho da população
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorio1_D(String tipoAvaliacao, int limiteDimensoes, int tamanhoPopulacao){
        Pattern[] P0 = new Pattern[tamanhoPopulacao];
        
        for(int i = 0; i < tamanhoPopulacao; i++){
            int d = Const.random.nextInt(limiteDimensoes) + 1;
            HashSet<Integer> itens = new HashSet<Integer>();                        
                        
            while(itens.size() < d){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
    
    
    /**Inicializa população de indivíduos aleatório utilizando entre 1 e 25% dos genes!
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param tamanhoPopulacao int - tamanho da população
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorio_1_25(String tipoAvaliacao, int tamanhoPopulacao){
        Pattern[] P0 = new Pattern[tamanhoPopulacao];
        int dimensaoMaxima = (int) (D.numeroItensUtilizados * 0.25);
        for(int i = 0; i < tamanhoPopulacao; i++){
            int d = Const.random.nextInt(dimensaoMaxima);
            HashSet<Integer> itens = new HashSet<Integer>();                        
                        
            while(itens.size() < d){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
   
    
    /**Inicializa população de indivíduos aleatório utilizando percentual fixo de genes!
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @param tamanhoPopulacao int - tamanho da população
     * @param percentualGenes double - percentual dos itens utilizados na formação dos indivíduos
     * @return Pattern[] - nova população
     */
    public static Pattern[] aleatorioPercentualSize(String tipoAvaliacao, int tamanhoPopulacao, double percentualGenes){
        Pattern[] P0 = new Pattern[tamanhoPopulacao];
        
        //int dimensao1p = 40;
        int dimensao = (int) (percentualGenes * D.numeroItensUtilizados);
        //System.out.println("População Inicial: size=" + dimensao + "(|I|=" + D.numeroItensUtilizados + "," + (percentualGenes*100) + "%)");
        for(int i = 0; i < tamanhoPopulacao; i++){
            HashSet<Integer> itens = new HashSet<Integer>();                        
           
            while(itens.size() < dimensao){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
   
        
}
