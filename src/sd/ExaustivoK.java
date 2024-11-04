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
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author tarcisio_pontes
 */
public class ExaustivoK {
    
    
    public static Pattern[] run(int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        Pattern[] DP1k = new Pattern[k];
        Pattern[] DP2k = new Pattern[k];
        Pattern[] DP3k = new Pattern[k];
        Pattern[] DP4k = new Pattern[k];
        Pattern[] DP5k = new Pattern[k];
        
        Pattern[] Paux = null;
        DP1k = ExaustivoK.runDP1k(k, tipoAvaliacao); 
        Pk = DP1k;//Os k melhores são os melhores de DP1 incialmente
        DP2k = ExaustivoK.runD2(DP1k, k, tipoAvaliacao);
        if(DP2k[0].getQualidade() > Pk[k-1].getQualidade()){
            Pk = SELECAO.selecionarMelhores(Pk, DP2k);
            DP3k = ExaustivoK.runD3(DP1k, k, tipoAvaliacao);
            if(DP3k[0].getQualidade() > Pk[k-1].getQualidade()){
                Pk = SELECAO.selecionarMelhores(Pk, DP3k);
                DP4k = ExaustivoK.runD4(DP1k, k, tipoAvaliacao);
                if(DP4k[0].getQualidade() > Pk[k-1].getQualidade()){
                    Pk = SELECAO.selecionarMelhores(Pk, DP4k);
                    DP5k = ExaustivoK.runD5(DP1k, k, tipoAvaliacao);
                    if(DP5k[0].getQualidade() > Pk[k-1].getQualidade()){
                        Pk = SELECAO.selecionarMelhores(Pk, DP5k);
                    }
                }
            }
        }
        
        return Pk;
        
    }
    
    
    public static Pattern[] runD1_D5(int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        Pattern[] DP1k = new Pattern[k];
        Pattern[] DP2k = new Pattern[k];
        Pattern[] DP3k = new Pattern[k];
        Pattern[] DP4k = new Pattern[k];
        Pattern[] DP5k = new Pattern[k];
        
        DP1k = ExaustivoK.runDP1k(k, tipoAvaliacao); 
//        System.out.println("DP1k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP1k, k));
//        Avaliador.imprimir(DP1k, k);
        
        DP2k = ExaustivoK.runD2(DP1k, k, tipoAvaliacao);
//        System.out.println("DP2k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP2k, k));
//        Avaliador.imprimir(DP2k, k);
        
        DP3k = ExaustivoK.runD3(DP1k, k, tipoAvaliacao);
//        System.out.println("DP3k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP3k, k));
//        Avaliador.imprimir(DP3k, k);
        
        DP4k = ExaustivoK.runD4(DP1k, k, tipoAvaliacao);
//        System.out.println("DP4 (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP4k, k));
//        Avaliador.imprimir(DP4k, k);
        
        DP5k = ExaustivoK.runD5(DP1k, k, tipoAvaliacao);        
//        System.out.println("DP5 (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP5k, k));
//        Avaliador.imprimir(DP5k, k);
        
        Pattern[] aux1 = SELECAO.selecionarMelhores(DP1k,DP2k, DP3k);
        Pattern[] aux2 = SELECAO.selecionarMelhores(DP4k,DP5k);
        Pk = SELECAO.selecionarMelhores(aux1, aux2);
        return Pk;
        
    }
    
    
    public static Pattern[] runD1_D4(int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        Pattern[] DP1k = new Pattern[k];
        Pattern[] DP2k = new Pattern[k];
        Pattern[] DP3k = new Pattern[k];
        Pattern[] DP4k = new Pattern[k];
        
        //System.out.println("\nAntes:" + Pattern.numeroIndividuosGerados);
        DP1k = ExaustivoK.runDP1k(k, tipoAvaliacao); 
        //System.out.println("DP1k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP1k, k));
        //Avaliador.imprimir(DP1k, k);
        //System.out.println("DP1:" + Pattern.numeroIndividuosGerados);
        DP2k = ExaustivoK.runD2(DP1k, k, tipoAvaliacao);
        //System.out.println("DP2k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP2k, k));
        //Avaliador.imprimir(DP2k, k);
        //System.out.println("DP2:" + Pattern.numeroIndividuosGerados);
        DP3k = ExaustivoK.runD3(DP1k, k, tipoAvaliacao);
        //System.out.println("DP3k (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP3k, k));
        //Avaliador.imprimir(DP3k, k);
        //System.out.println("DP3:" + Pattern.numeroIndividuosGerados);
        DP4k = ExaustivoK.runD4(DP1k, k, tipoAvaliacao);
        //System.out.println("DP4 (Qualidade/Suporte/Confiança): " + Avaliador.avaliarMedia(DP4k, k));
        //Avaliador.imprimir(DP4k, k);
        //System.out.println("DP4:" + Pattern.numeroIndividuosGerados);
        Pattern[] aux1 = SELECAO.selecionarMelhores(DP1k, DP2k, DP3k);
        Pk = SELECAO.selecionarMelhores(aux1, DP4k);
        return Pk;
        
    }
    
    /**Retorna as k melhores DP1
     *@author Tarcísio Pontes
     * @param k int - quantidade DP retornado.
     * @param tipoAvaliacao - int métrica de qualidade de uma DP
     * @return Pk - Pattern[] população com os k melhores DP1
     */
    public static Pattern[] runDP1k(int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        
        //Inicializando com os k primeiros D-1               
        int indicekPrimeiros = 0;
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            HashSet<Integer> itens = new HashSet<>();            
            itens.add(D.itensUtilizados[i]);
            Pattern p = new Pattern(itens, tipoAvaliacao);
            //Pattern.numeroIndividuosGerados++;
            if(indicekPrimeiros < k){                
                Pk[indicekPrimeiros++] = p;                
            }else{
                if(p.getQualidade() > Pk[k-1].getQualidade()){
                    if(SELECAO.ehRelevante(p, Pk)){
                        Pk[k-1] = p;
                        Arrays.sort(Pk);
                    }
                }
            }    
        }        
        return Pk;
    }
    
    /**Retorna as k melhores DP-2 buscando todas as possibilidades
     * do conjunto de DPs passado como parâmetro.
     *@author Tarcísio Pontes
     * @param P Pattern[] - quantidade DP retornado.
     * @param k Pattern - quantidade DP retornado.
     * @param tipoAvaliacao - métrica de qualidade de uma DP
     * @return Pk Pattern[] - população com os k melhores DP1
     */
    public static Pattern[] runD2(Pattern[] P, int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        
        //Inicializando com os k primeiros D-1               
        int indicekPrimeiros = 0;
        for(int i = 0; i < P.length; i++){
            for(int j = i+1; j < P.length; j++){
                HashSet<Integer> itens = new HashSet<>();            
                itens.addAll(P[i].getItens());
                itens.addAll(P[j].getItens());
                Pattern p = new Pattern(itens, tipoAvaliacao);
                //Pattern.numeroIndividuosGerados++;           
                
                if(indicekPrimeiros < k){                
                    Pk[indicekPrimeiros++] = p;                
                }else{
                    if(p.getQualidade() > Pk[k-1].getQualidade()){
                        if(SELECAO.ehRelevante(p, Pk)){
                            Pk[k-1] = p;
                            Arrays.sort(Pk);
                        }
                    }
                }
            }                
        }        
        return Pk;
    }
    
    
    
    /**Retorna as k melhores DP-3 buscando todas as possibilidades
     * do conjunto de DPs passado como parâmetro.
     *@author Tarcísio Pontes
     * @param P Pattern[] - quantidade DP retornado.
     * @param k Pattern - quantidade DP retornado.
     * @param tipoAvaliacao - métrica de qualidade de uma DP
     * @return Pk Pattern[] - população com os k melhores DP1
     */
    public static Pattern[] runD3(Pattern[] P, int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        
        //Inicializando com os k primeiros D-1               
        int indicekPrimeiros = 0;
        for(int i = 0; i < P.length; i++){
            for(int j = i+1; j < P.length; j++){
                for(int l = j+1; l < P.length; l++){                    
                    HashSet<Integer> itens = new HashSet<>();            
                    itens.addAll(P[i].getItens());
                    itens.addAll(P[j].getItens());
                    itens.addAll(P[l].getItens());
                    Pattern p = new Pattern(itens, tipoAvaliacao);
                    //Pattern.numeroIndividuosGerados++;           

                    if(indicekPrimeiros < k){                
                        Pk[indicekPrimeiros++] = p;                
                    }else{
                        if(p.getQualidade() > Pk[k-1].getQualidade()){
                            if(SELECAO.ehRelevante(p, Pk)){
                                Pk[k-1] = p;
                                Arrays.sort(Pk);
                            }
                        }
                    }
                }
            }                
        }        
        return Pk;
    }
    
    
    /**Retorna as k melhores DP-4 buscando todas as possibilidades
     * do conjunto de DPs passado como parâmetro.
     *@author Tarcísio Pontes
     * @param P Pattern[] - quantidade DP retornado.
     * @param k Pattern - quantidade DP retornado.
     * @param tipoAvaliacao - métrica de qualidade de uma DP
     * @return Pk Pattern[] - população com os k melhores DP1
     */
    public static Pattern[] runD4(Pattern[] P, int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        
        //Inicializando com os k primeiros D-1               
        int indicekPrimeiros = 0;
        for(int i = 0; i < P.length; i++){
            for(int j = i+1; j < P.length; j++){
                for(int l = j+1; l < P.length; l++){                    
                    for(int m = l + 1; m < P.length; m++){
                        HashSet<Integer> itens = new HashSet<>();            
                        itens.addAll(P[i].getItens());
                        itens.addAll(P[j].getItens());
                        itens.addAll(P[l].getItens());
                        itens.addAll(P[m].getItens());
                        Pattern p = new Pattern(itens, tipoAvaliacao);
                        //Pattern.numeroIndividuosGerados++;           

                        if(indicekPrimeiros < k){                
                            Pk[indicekPrimeiros++] = p;                
                        }else{
                            if(p.getQualidade() > Pk[k-1].getQualidade()){
                                if(SELECAO.ehRelevante(p, Pk)){
                                    Pk[k-1] = p;
                                    Arrays.sort(Pk);
                                }
                            }
                        }
                    }
                }
            }                
        }        
        return Pk;
    }
    
    
    
    
    /**Retorna as k melhores DP-5 buscando todas as possibilidades
     * do conjunto de DPs passado como parâmetro.
     *@author Tarcísio Pontes
     * @param P Pattern[] - quantidade DP retornado.
     * @param k Pattern - quantidade DP retornado.
     * @param tipoAvaliacao - métrica de qualidade de uma DP
     * @return Pk Pattern[] - população com os k melhores DP1
     */
    public static Pattern[] runD5(Pattern[] P, int k, String tipoAvaliacao){
        Pattern[] Pk = new Pattern[k];
        
        //Inicializando com os k primeiros D-1               
        int indicekPrimeiros = 0;
        for(int i = 0; i < P.length; i++){
            for(int j = i+1; j < P.length; j++){
                for(int l = j+1; l < P.length; l++){                    
                    for(int m = l+1; m < P.length; m++){
                        for(int n = m+1; n < P.length; n++){
                            HashSet<Integer> itens = new HashSet<>();            
                            itens.addAll(P[i].getItens());
                            itens.addAll(P[j].getItens());
                            itens.addAll(P[l].getItens());
                            itens.addAll(P[m].getItens());
                            itens.addAll(P[n].getItens());
                            Pattern p = new Pattern(itens, tipoAvaliacao);
                            //Pattern.numeroIndividuosGerados++;           

                            if(indicekPrimeiros < k){                
                                Pk[indicekPrimeiros++] = p;                
                            }else{
                                if(p.getQualidade() > Pk[k-1].getQualidade()){
                                    if(SELECAO.ehRelevante(p, Pk)){
                                        Pk[k-1] = p;
                                        Arrays.sort(Pk);
                                    }
                                }
                            }
                        }
                    }
                }
            }                
        }        
        return Pk;
    }
    
    
}
