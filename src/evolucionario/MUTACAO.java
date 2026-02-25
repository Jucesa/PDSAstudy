/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolucionario;

import dp.Const;
import dp.D;
import dp.Pattern;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Marianna
 */
public class MUTACAO {
            
    /**Adiciona ou troca gene de forma aleatória
     * 33% das vezes troca (objetivo: explorar aleatoriamente espaço D)
     * 33% das vezes adiciona (objetivo: explorar aleatoriamente espaço D+1)
     * 33% das vezes exclui (objetivo: explorar aleatoriamente espaço D-1)
     *@author Tarcísio Pontes
     * @param p Pattern - indivíduo a ser mutado
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern - indivíduo mutado
     */
    public static Pattern unGeneTrocaOuAdicionaOuExclui(Pattern p, String tipoAvaliacao){
        HashSet<Integer> itens = p.getItens();
        
        if(itens.isEmpty()){//Se indivíduo não tiver gene, retorne um novo aleatório de 1D
            itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);            
            return new Pattern(itens, tipoAvaliacao);
        }
        
        HashSet<Integer> novoItens = new HashSet<>();
        double r = Const.random.nextDouble();
        if(r < 0.33){//Excluir gene
            
            int indiceExcluir = Const.random.nextInt(itens.size());
            Iterator iterator = itens.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(i != indiceExcluir){
                    novoItens.add((Integer)iterator.next());
                }else{
                    iterator.next();
                }
            }
            
        }else if(r > 0.66){//Troca gene por outro aleatório
            //Excluir gene
            int indiceExcluir = Const.random.nextInt(itens.size());
            Iterator iterator = itens.iterator();
            for(int i = 0; iterator.hasNext(); i++){
                if(i != indiceExcluir){
                    novoItens.add((Integer)iterator.next());
                }else{
                    iterator.next();
                }
            }
                   
            //Adiciona novo gene
            while(novoItens.size() < itens.size()){
                novoItens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);            
            }
        }else{//Adiciona gene aleatoriamente
            //Adiciona novo gene
            novoItens.addAll(itens);
            while(novoItens.size() < itens.size() + 1){
                novoItens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);            
            }
        }                          
              
        
        Pattern pNovo = new Pattern(novoItens, tipoAvaliacao);
        
        //Imprimir itens nos idivíduos gerados via cruzamento
        //DPinfo.imprimirItens(p);
        //System.out.print(r + "->");
        //DPinfo.imprimirItens(pNovo);
        //System.out.println();
        
        return pNovo;
    }

    public static Pattern unGeneTrocaOuAdicionaOuExcluiE(Pattern p, String tipoAvaliacao) {
        // 1. CLONAGEM IMEDIATA (Segurança)
        // Nunca modifique o set original com p.getItens().add(), pois pode afetar o pai na população
        HashSet<Integer> novoItens = new HashSet<>(p.getItens());

        // 2. PROTEÇÃO CONTRA ENTRADA VAZIA
        // Se por algum milagre chegou vazio, conserta agora
        if (novoItens.isEmpty()) {
            novoItens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            return new Pattern(novoItens, tipoAvaliacao);
        }

        double r = Const.random.nextDouble();

        // --- LÓGICA DE MUTAÇÃO SEGURA ---

        if (r < 0.33) {
            // CASO 1: EXCLUSÃO (Com proteção)
            // Só podemos excluir se sobrar pelo menos 1 item depois (ou seja, tamanho atual > 1)
            if (novoItens.size() > 1) {
                // Converte para array/lista para remover aleatório eficientemente
                Integer[] arrayItens = novoItens.toArray(new Integer[0]);
                int indiceExcluir = Const.random.nextInt(arrayItens.length);
                novoItens.remove(arrayItens[indiceExcluir]);
            } else {
                // Se tem apenas 1 item, NÃO EXCLUI.
                // Forçamos uma Troca ou Adição para não devolver vazio.
                // Aqui escolhi Adicionar para variar.
                adicionarItemAleatorio(novoItens);
            }

        } else if (r > 0.66) {
            // CASO 2: TROCA (Swap)
            // Remove um e adiciona outro. Seguro para tamanho 1 (fica tamanho 1).

            // Remove um aleatório
            Integer[] arrayItens = novoItens.toArray(new Integer[0]);
            int indiceExcluir = Const.random.nextInt(arrayItens.length);
            novoItens.remove(arrayItens[indiceExcluir]);

            // Adiciona um novo diferente do que já tem
            adicionarItemAleatorio(novoItens);

        } else {
            // CASO 3: ADIÇÃO
            adicionarItemAleatorio(novoItens);
        }

        return new Pattern(novoItens, tipoAvaliacao);
    }

    // Método auxiliar para evitar repetição de código e loops infinitos
    private static void adicionarItemAleatorio(HashSet<Integer> itens) {
        // Proteção contra Loop Infinito:
        // Se o padrão já tiver TODOS os itens possíveis, não dá pra adicionar.
        if (itens.size() >= D.numeroItensUtilizados) return;

        int tamanhoOriginal = itens.size();
        // Tenta adicionar até conseguir (HashSet não aceita duplicados)
        // Limite de 100 tentativas para não travar o sistema
        for (int i = 0; i < 100; i++) {
            int itemCandidato = D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)];
            itens.add(itemCandidato);
            if (itens.size() > tamanhoOriginal) break; // Sucesso
        }
    }
 
    /**Gerar uma população a partir de mutações unGeneTrocaOuAdicionaOuExclui
     * 33% das vezes troca (objetivo: explorar aleatoriamente espaço D)
     * 33% das vezes adiciona (objetivo: explorar aleatoriamente espaço D+1)
     * 33% das vezes exclui (objetivo: explorar aleatoriamente espaço D-1)
    *@author Tarcísio Pontes
     * @param P Pattern[] - população
     * @param tamanhoPopulacao - número de indivíduoes a serem gerados
     * @param tipoAvaliacao int - tipo de função de avaliação utilizada
     * @return Pattern - indivíduo mutado
     */
    public static Pattern[] unGeneTrocaOuAdicionaOuExcluiPop(Pattern[] P, int tamanhoPopulacao, String tipoAvaliacao){
        Pattern[] Pm = new Pattern[tamanhoPopulacao];       
        
        for(int i = 0; i < Pm.length; i++){
            Pm[i] = MUTACAO.unGeneTrocaOuAdicionaOuExclui(P[i], tipoAvaliacao);
        }
        return Pm;
    }
    
    
    
    /**
     * Mutação em um gene garantindo que indivíduos terá mesmo tamanho.
     * Utilizado em abordagens que devem garantir que os indivíduos gerados devem ser do mesmo tamanho do original.
     * @param p
     * @param tipoAvaliacao
     * @return 
     */
    public static Pattern unGeneD(Pattern p, String tipoAvaliacao){
        HashSet<Integer> itens = (HashSet<Integer>) p.getItens().clone();
        HashSet<Integer> novoItens = new HashSet<>();
                    
        int indiceExcluir = Const.random.nextInt(itens.size());
        Iterator iterator = itens.iterator();
        for(int i = 0; iterator.hasNext(); i++){
            if(i != indiceExcluir){
                novoItens.add((Integer)iterator.next());
            }
        }          
        
        while(novoItens.size() < itens.size()){
            novoItens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);            
        }        
        return new Pattern(novoItens, tipoAvaliacao);
    }

}
