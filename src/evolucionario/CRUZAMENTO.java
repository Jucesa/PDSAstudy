/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolucionario;

import dp.Const;
import dp.Pattern;

import java.util.*;

/**
 *
 * @author Tarcísio Lucas
 * @since 27/01/2013
 * @version 2.0 26/06/2017
 */
public class CRUZAMENTO {
    
    /////////////////////////////////////////////////////////////
    //UNIFORME TRADICIONAL //////////////////////////////////////
    /////////////////////////////////////////////////////////////
        
    /**Cruzamento gera população a partir de cruzamentos do tipo uniforme e de mutações
     *@author Tarcísio Pontes
     * @param P Pattern[] - população antiga 
     * @param taxaMutacao double - taxa de indivúduos que terão um gene modificado
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern[] - nova população
     */
    public static Pattern[] uniforme2Pop(Pattern[] P, double taxaMutacao, String tipoAvaliacao) {
        int tamanhoPopulacao = P.length;
        Pattern[] Pnovo = new Pattern[tamanhoPopulacao];

        // Garante que temos pais suficientes para gerar a nova população 1 para 1
        int[] selecao = SELECAO.torneioBinario(tamanhoPopulacao, P);

        int indiceSelecao = 0;
        int indicePnovo = 0;

        // Loop principal
        while (indicePnovo < tamanhoPopulacao) {

            // Verifica se ainda cabem 2 filhos E se há 2 pais disponíveis para cruzamento
            boolean podeCruzar = (indicePnovo < tamanhoPopulacao - 1) && (indiceSelecao < selecao.length - 1);

            // Lógica de decisão: Cruzamento vs Mutação
            // Se random > mutação, tentamos cruzar. Caso contrário, ou se não der pra cruzar, mutamos.
            if (podeCruzar && Const.random.nextDouble() > taxaMutacao) {
                Pattern p1 = P[selecao[indiceSelecao]];
                Pattern p2 = P[selecao[indiceSelecao + 1]];

                Pattern[] novos = CRUZAMENTO.uniforme2Individuos(p1, p2, tipoAvaliacao);

                Pnovo[indicePnovo++] = novos[0];
                // Verificação redundante mas segura
                if (indicePnovo < tamanhoPopulacao) {
                    Pnovo[indicePnovo++] = novos[1];
                }
                indiceSelecao += 2; // Consumimos 2 pais

            } else {
                // Mutação (ou fallback se só sobrou 1 espaço)
                if (indiceSelecao < selecao.length) {
                    Pattern pai = P[selecao[indiceSelecao]];
                    Pattern filho = MUTACAO.unGeneTrocaOuAdicionaOuExclui(pai, tipoAvaliacao);
                    Pnovo[indicePnovo++] = filho;
                    indiceSelecao++; // Consumimos 1 pai
                } else {
                    // Caso extremo de segurança: acabaram os pais selecionados?
                    // Clona ou gera aleatório, ou break.
                    break;
                }
            }
        }

        return Pnovo;
    }

        public static Pattern[] uniforme2PopE(Pattern[] P, double taxaMutacao, String tipoAvaliacao) {
            int tamanhoPopulacao = P.length;
            Pattern[] Pnovo = new Pattern[tamanhoPopulacao];

            // Garante que temos pais suficientes para gerar a nova população 1 para 1
            int[] selecao = SELECAO.torneioBinario(tamanhoPopulacao, P);

            int indiceSelecao = 0;
            int indicePnovo = 0;

            // Loop principal
            while (indicePnovo < tamanhoPopulacao) {

                // Verifica se ainda cabem 2 filhos E se há 2 pais disponíveis para cruzamento
                boolean podeCruzar = (indicePnovo < tamanhoPopulacao - 1) && (indiceSelecao < selecao.length - 1);

                // Lógica de decisão: Cruzamento vs Mutação
                // Se random > mutação, tentamos cruzar. Caso contrário, ou se não der pra cruzar, mutamos.
                if (podeCruzar && Const.random.nextDouble() > taxaMutacao) {
                    Pattern p1 = P[selecao[indiceSelecao]];
                    Pattern p2 = P[selecao[indiceSelecao + 1]];

                    Pattern[] novos = CRUZAMENTO.uniforme2IndividuosE(p1, p2, tipoAvaliacao);

                    Pnovo[indicePnovo++] = novos[0];
                    // Verificação redundante mas segura
                    if (indicePnovo < tamanhoPopulacao) {
                        Pnovo[indicePnovo++] = novos[1];
                    }
                    indiceSelecao += 2; // Consumimos 2 pais

                } else {
                    // Mutação (ou fallback se só sobrou 1 espaço)
                    if (indiceSelecao < selecao.length) {
                        Pattern pai = P[selecao[indiceSelecao]];
                        Pattern filho = MUTACAO.unGeneTrocaOuAdicionaOuExcluiE(pai, tipoAvaliacao);
                        Pnovo[indicePnovo++] = filho;
                        indiceSelecao++; // Consumimos 1 pai
                    } else {
                        // Caso extremo de segurança: acabaram os pais selecionados?
                        // Clona ou gera aleatório, ou break.
                        break;
                    }
                }
            }

            return Pnovo;
        }
        
    /**Cruzamento gera dois indivíduos a partir do método uniforme
     *@author Tarcísio Pontes
     * @param p1 Pattern[] - indivíduo 1 
     * @param p2 Pattern[] - indivíduo 2
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern[] - vetor com dois novos indivíduos
     */
    public static Pattern[] uniforme2Individuos(Pattern p1, Pattern p2, String tipoAvaliacao){
        Pattern[] novosPattern = new Pattern[2];
        HashSet<Integer> novoItens1 = new HashSet<>();
        HashSet<Integer> novoItens2 = new HashSet<>();

        Iterator<Integer> iterator = p1.getItens().iterator();
        while(iterator.hasNext()){
            if(Const.random.nextBoolean()){
                novoItens1.add(iterator.next());
            }else{          
                novoItens2.add(iterator.next());
            }
        }
        iterator = p2.getItens().iterator();
        while(iterator.hasNext()){
            if(Const.random.nextBoolean()){
                novoItens1.add(iterator.next());
            }else{          
                novoItens2.add(iterator.next());
            }
        }
        novosPattern[0] = new Pattern(novoItens1, tipoAvaliacao);
        novosPattern[1] = new Pattern(novoItens2, tipoAvaliacao);
        return novosPattern;           
    }

    public static Pattern[] uniforme2IndividuosE(Pattern p1, Pattern p2, String tipoAvaliacao) {
        Pattern[] novosPattern = new Pattern[2];
        HashSet<Integer> novoItens1 = new HashSet<>();
        HashSet<Integer> novoItens2 = new HashSet<>();

        // --- FASE 1: Distribuição Probabilística (Cruzamento Uniforme) ---

        // Distribui genes do Pai 1
        for (Integer item : p1.getItens()) {
            if (Const.random.nextBoolean()) {
                novoItens1.add(item);
            } else {
                novoItens2.add(item);
            }
        }

        // Distribui genes do Pai 2
        for (Integer item : p2.getItens()) {
            if (Const.random.nextBoolean()) {
                novoItens1.add(item);
            } else {
                novoItens2.add(item);
            }
        }

        // --- FASE 2: Reparação Genética (Herança de Emergência) ---

        // Correção Filho 1
        if (novoItens1.isEmpty()) {
            // 1. Sorteia qual pai vai doar o gene de salvamento (P1 ou P2)
            Pattern doador = Const.random.nextBoolean() ? p1 : p2;

            // 2. Pega um item aleatório desse pai
            Integer[] itensDoador = doador.getItens().toArray(new Integer[0]);
            if (itensDoador.length > 0) {
                novoItens1.add(itensDoador[Const.random.nextInt(itensDoador.length)]);
            }
        }

        // Correção Filho 2
        if (novoItens2.isEmpty()) {
            // 1. Sorteia qual pai vai doar
            Pattern doador = Const.random.nextBoolean() ? p1 : p2;

            // 2. Pega item aleatório
            Integer[] itensDoador = doador.getItens().toArray(new Integer[0]);
            if (itensDoador.length > 0) {
                novoItens2.add(itensDoador[Const.random.nextInt(itensDoador.length)]);
            }
        }

        novosPattern[0] = new Pattern(novoItens1, tipoAvaliacao);
        novosPattern[1] = new Pattern(novoItens2, tipoAvaliacao);

        return novosPattern;
    }
    
    /////////////////////////////////////////////////////////////
    // AND                 //////////////////////////////////////
    /////////////////////////////////////////////////////////////
      
    /**Reliza cruzamento do tipo AND entre indivíduos de duas populações distintas
     *@author Tarcísio Pontes
     * @param P1 Pattern[] - população 1 
     * @param P2 Pattern[] - população 2
     * @param tipoAvaliacao int - tipo de função de avaliação
     * @return Pattern[] - nova população
     */
    public static Pattern[] ANDduasPopulacoes(Pattern[] P1, Pattern[] P2, String tipoAvaliacao){
        int tamanhoPopulacao = P1.length;
        Pattern[] Pnovo = new Pattern[tamanhoPopulacao];
        int[] indicesP1 = SELECAO.torneioBinario(tamanhoPopulacao, P1);
        int[] indicesP2 = SELECAO.torneioBinario(tamanhoPopulacao, P2);

        for(int i = 0; i < tamanhoPopulacao; i++){
            Pattern p1 = P1[indicesP1[i]];
            Pattern p2 = P2[indicesP2[i]];
            Pattern filho = CRUZAMENTO.AND(p1, p2, tipoAvaliacao);
            Pnovo[i] = filho;
        }
        return Pnovo;
    }
    
    /**Reliza cruzamento do tipo AND entre dois indivíduos
     *@author Tarcísio Pontes
     * @param p1 Pattern - indivíduo 1 
     * @param p2 Pattern - indivíduo 2
     * @param tipoAvaliacao int - tipo de função de avaliação
     * @return Pattern - novo indivíduo
     * @since 27/01/2016     * 
     */
//    public static Pattern AND(Pattern p1, Pattern p2, String tipoAvaliacao){
//        HashSet<Integer> novoitens = new HashSet<>();
//        novoitens.addAll(p1.getItens());
//        novoitens.addAll(p2.getItens());
//
//        return new Pattern(novoitens, tipoAvaliacao);
//    }

    public static Pattern AND(Pattern p1, Pattern p2, String tipoAvaliacao){
        HashSet<Integer> itensP1 = (HashSet<Integer>) p1.getItens();
        HashSet<Integer> itensP2 = (HashSet<Integer>) p2.getItens();

        // 1. OTIMIZAÇÃO DE OURO (Early Exit):
        // Se um pai já contém todos os itens do outro, a união será idêntica ao pai maior.
        // Retornamos a referência do pai diretamente. Isso EVITA acionar o construtor
        // do Pattern e a leitura do banco de dados! O seu "ehNovo" vai dar false e ignorá-lo.
        if (itensP1.containsAll(itensP2)) return p1;
        if (itensP2.containsAll(itensP1)) return p2;

        // 2. OTIMIZAÇÃO DE MEMÓRIA:
        // Já sabemos o tamanho máximo que o novo set terá.
        // Inicializar com o tamanho certo e load factor 1.0 evita redimensionamentos pesados.
        int capacidadeMxima = itensP1.size() + itensP2.size();
        HashSet<Integer> novoitens = new HashSet<>(capacidadeMxima, 1.0f);

        novoitens.addAll(itensP1);
        novoitens.addAll(itensP2);

        // A avaliação pesada (banco de dados) acontece aqui dentro:
        return new Pattern(novoitens, tipoAvaliacao);
    }
    

    /////////////////////////////////////////////////////////////
    //UNIFORME RESTRITO A TAMANHO FIXO ///// ////////////////////
    /////////////////////////////////////////////////////////////
            
    /**Dois indivíduos de tamanho d geram outros dois do mesmo tamanho d
     * pelo método uniforme
     *@author Tarcísio Pontes
     * @param p1 Pattern[] - indivíduo 1 
     * @param p2 Pattern[] - indivíduo 2
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern[] - novos indivíduos
     */
    public static Pattern[] uniforme2D(Pattern p1, Pattern p2, String tipoAvaliacao){
        Pattern[] p = new Pattern[2];
        int d = p1.getItens().size();
        ArrayList<Integer> itensTodos = new ArrayList<>();
        itensTodos.addAll(p1.getItens());
        itensTodos.addAll(p2.getItens());
        
        HashSet<Integer> itens = new HashSet<>();
        while(itens.size() < d){
            itens.add(itensTodos.get(Const.random.nextInt(itensTodos.size())));
        }
        p[0] = new Pattern(itens, tipoAvaliacao);
        
        itens = new HashSet<>();
        while(itens.size() < d){
            itens.add(itensTodos.get(Const.random.nextInt(itensTodos.size())));
        }
        p[1] = new Pattern(itens, tipoAvaliacao);       
        
        return p;
    }
    
    
    /**Cruzamento gera população a partir de cruzamentos do tipo uniforme2D 
     * e mutações
     *@author Tarcísio Pontes
     * @param P Pattern[] - população antiga 
     * @param taxaMutacao double - taxa de indivúduos que terão um gene modificado
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern[] - nova população
     */
    public static Pattern[] uniforme2DPop(Pattern[] P, double taxaMutacao, String tipoAvaliacao){
        int tamanhoPopulacao = P.length;        
        Pattern[] Pnovo = new Pattern[tamanhoPopulacao];
               
        int indicePnovo = 0;
        int indiceP1;
        int indiceP2;
        while(indicePnovo < Pnovo.length){//Cuidado para não acessar índices maiores que o tamanho do array                     
            if(Const.random.nextDouble() > taxaMutacao){                                    
                //Obtendo índices do indivíduos que serão cruzados
//                if(random.nextDouble() > 0.75){//75% de chanses de ser selecionado um dos 25% mais relevantes
//                    indiceP1 = random.nextInt(P.length*1/4);
//                    indiceP2 = random.nextInt(P.length*1/4);
//                }else{//25% de chanses de ser selecionado totalmente aleatório
//                    indiceP1 = random.nextInt(P.length);
//                    indiceP2 = random.nextInt(P.length);
//                }
                indiceP1 = SELECAO.torneioBinario(P);
                indiceP2 = SELECAO.torneioBinario(P);
                
                Pattern[] novos = CRUZAMENTO.uniforme2D(P[indiceP1], P[indiceP2], tipoAvaliacao);
                Pnovo[indicePnovo++] = novos[0];
                if(indicePnovo < Pnovo.length){
                    Pnovo[indicePnovo++] = novos[1];                    
                }                                                                      
            }else{
//                if(random.nextDouble() > 0.75){//75% de chanses de ser selecionado um dos 25% mais relevantes
//                    indiceP1 = random.nextInt(P.length*1/4);                    
//                }else{//25% de chanses de ser selecionado totalmente aleatório
//                    indiceP1 = random.nextInt(P.length);                    
//                }
                indiceP1 = SELECAO.torneioBinario(P);                    
                Pnovo[indicePnovo++] = MUTACAO.unGeneD(P[indiceP1], tipoAvaliacao);                                                       
            }
        }

        return Pnovo;
    }  

    
    /////////////////////////////////////////////////////////////
    //OUTROS               //////////////////////////////////////
    /////////////////////////////////////////////////////////////
    
    /**Reliza cruzamento do uniforme, gerando 2 indivíduos e AND gernado mais um
     *@author Tarcísio Pontes
     * @param p1 Pattern[] - indivíduo 1 
     * @param p2 Pattern[] - indivíduo 2 
     * @param tipoAvaliacao int - tipo de função de avaliação
     * @return Pattern[] - novos indivíduos
     */
    public static Pattern[] uniforme2AND(Pattern p1, Pattern p2, String tipoAvaliacao){
        Pattern[] novosPattern = new Pattern[3];
        HashSet<Integer> novoitens1 = new HashSet<>();
        HashSet<Integer> novoitens2 = new HashSet<>();
        HashSet<Integer> novoitens3 = new HashSet<>();
        
        novoitens3.addAll(p1.getItens());
        novoitens3.addAll(p2.getItens());
        
        Iterator<Integer> iterator = novoitens3.iterator();
        while(iterator.hasNext()){
            if(Const.random.nextBoolean()){
                novoitens1.add(iterator.next());
            }else{          
                novoitens2.add(iterator.next());
            }
        }
               
        novosPattern[0] = new Pattern(novoitens1, tipoAvaliacao);
        novosPattern[1] = new Pattern(novoitens2, tipoAvaliacao);
        novosPattern[2] = new Pattern(novoitens3, tipoAvaliacao);
        return novosPattern;           
    }
    
    /**Cruzamento gera um indivíduo a partir do método uniforme
     *@author Tarcísio Pontes
     * @param p1 Pattern - indivíduo 1 
     * @param p2 Pattern - indivíduo 2
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern - novo indivíduo
     */
    public static Pattern uniforme1(Pattern p1, Pattern p2, String tipoAvaliacao){

        HashSet<Integer> itens = new HashSet<>();
        itens.addAll(p1.getItens());
        itens.addAll(p2.getItens());

        HashSet<Integer> novoItens = new HashSet<>();                  

        while(novoItens.isEmpty()){
            Iterator<Integer> iterator = itens.iterator();
            while(iterator.hasNext()){
                if(Const.random.nextBoolean()){
                    novoItens.add(iterator.next());
                }
            }
        }     

        return new Pattern(novoItens, tipoAvaliacao);
    }
  
    
        
    /**Reliza cruzamento do tipo AND entre indivíduos de duas populações de dimensões P1 e PD garantindo
     * que cada indivíduo terá dimensão D+1
     * Adiciona indivíduos de 1D aleatoriamente caso o cruzamento não gere um indivíduo de dimensão desejada.
    *@author Tarcísio Pontes
     * @param P1 Pattern[] - população dimensão 1 
     * @param PD Pattern[] - população dimensão D
     * @param tipoAvaliacao int - tipo de função de avaliação
     * @param  tamanhoPopulacao int - quantos indivíduos se deseja
     * @return Pattern[] - nova população
     */
    public static Pattern[] ANDP1PD(Pattern[] P1, Pattern[] PD, String tipoAvaliacao, int tamanhoPopulacao){
        int dimensaoExigida = PD[0].getItens().size()+1;
        Pattern[] Pnovo = new Pattern[tamanhoPopulacao];
        int indicePNovo = 0;
        int indiceP1;
        int indicePD;
        while(indicePNovo < Pnovo.length){
            if(Const.random.nextDouble() > 0.75){//75% de chanses de ser selecionado um dos 25% mais relevantes
                indiceP1 = Const.random.nextInt(P1.length /4);
                indicePD = Const.random.nextInt(PD.length /4);
            }else{//25% de chanses de ser selecionado totalmente aleatório
                indiceP1 = Const.random.nextInt(P1.length);
                indicePD = Const.random.nextInt(PD.length);
            }
            HashSet<Integer> itensNovo = new HashSet<>();
            itensNovo.addAll(P1[indiceP1].getItens());
            itensNovo.addAll(PD[indicePD].getItens());
            if(itensNovo.size() == dimensaoExigida){
                Pnovo[indicePNovo++] = new Pattern(itensNovo, tipoAvaliacao);
            }
        }
        return Pnovo;
    }

}
