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

    /**
     * Inicializa a população selecionando itens unitários via Torneio.
     * Isso aumenta a frequência de bons genes na população inicial.
     */
    public static Pattern[] inicializarPorTorneio(String tipoAvaliacao) {
        // 1. Gera o "pool" de todos os candidatos possíveis (itens unitários)
        // Isso é necessário para ter de onde sortear.
        Pattern[] candidatos = D1(tipoAvaliacao);

        Pattern[] P = new Pattern[D.numeroItensUtilizados];
        int tamanhoTorneio = 5;

        for (int i = 0; i < P.length; i++) {
            // --- Início do Torneio ---
            Pattern vencedor = null;
            double melhorQualidade = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < tamanhoTorneio; j++) {
                // Sorteia um índice aleatório do pool de candidatos
                int indexSorteado = Const.random.nextInt(candidatos.length);
                Pattern desafiante = candidatos[indexSorteado];

                // Verifica se este desafiante é o melhor deste torneio
                if (desafiante.getQualidade() > melhorQualidade) {
                    melhorQualidade = desafiante.getQualidade();
                    vencedor = desafiante;
                }
            }
            // --- Fim do Torneio ---

            // Adiciona o vencedor à nova população.
            // É CRÍTICO criar uma nova instância (new Pattern) se o seu algoritmo
            // modifica os padrões (mutação) posteriormente. Se você apenas passar a referência,
            // alterar P[0] afetaria P[10] se eles fossem o mesmo objeto.
            if (vencedor != null) {
                P[i] = new Pattern(new HashSet<>(vencedor.getItens()), tipoAvaliacao);
            }
        }

        return P;
    }

    /**Inicializa população com todas as possibilidades de indivíduos com dimensão N
     *@author Julio Mota
     * @param N int - dimensao de populacao
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @return Pattern[] - nova população
     */
    public static Pattern[] DN(String tipoAvaliacao, int N) {
        List<Pattern> lista = new ArrayList<>();
        int[] itens = D.itensUtilizados;
        int total = D.numeroItensUtilizados;

        // Gera todas as combinações de tamanho N
        gerarCombinacoes(itens, total, N, 0, new ArrayList<>(), lista, tipoAvaliacao);

        // Converte lista para array
        return lista.toArray(new Pattern[0]);
    }

    // Função recursiva para gerar todas as combinações de tamanho N
    private static void gerarCombinacoes(int[] itens, int total, int N, int inicio,
                                         List<Integer> atual, List<Pattern> lista,
                                         String tipoAvaliacao) {
        if (atual.size() == N) {
            HashSet<Integer> conjunto = new HashSet<>(atual);
            lista.add(new Pattern(conjunto, tipoAvaliacao));
            return;
        }

        for (int i = inicio; i < total; i++) {
            atual.add(itens[i]);
            gerarCombinacoes(itens, total, N, i + 1, atual, lista, tipoAvaliacao);
            atual.removeLast();
        }
    }
    
    /**Inicializa população com todas as possibilidades de indivíduos com apenas uma dimensão
     * mas antes juntanto itens considerados similares entre si com relação ao índice adotado
     * objetivo é diminuir volume de itens correlacionados sem perda de informação.
     * Ao mesmo tempo itens similares não aumentam o fitness de um subgrupo!
     *@author Tarcísio Pontes
     * @param tipoAvaliacao int - tipo de avaliação utilizado para qualificar indivíduo
     * @return Pattern[] - nova população
     */
    public static Pattern[] D1joinSimilarItens(String tipoAvaliacao){
        return D1(tipoAvaliacao);
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
            HashSet<Integer> itens = new HashSet<>();
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
            HashSet<Integer> itens = new HashSet<>();
            
            while(itens.size() < numeroDimensoes){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }
        
        
        //Coletanto todos os itens distintos da população Pk.
        HashSet<Integer> itensPk = new HashSet<>();
        for (Pattern pattern : Pk) {
            itensPk.addAll(pattern.getItens());
        }
        int[] itensPkArray = new int[itensPk.size()];
        
        Iterator<Integer> iterator = itensPk.iterator();
        int n = 0;        
        while(iterator.hasNext()){
            itensPkArray[n++] = iterator.next();
        }
        
        //Gerando parte da população utilizando os itens presentes em Pk        
        for(int j = i; j < tamanhoPopulacao; j++){
            HashSet<Integer> itens = new HashSet<>();
            
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

    /**
     * Reinicializa a população combinando exploração (I) e explotação (Pk).
     * @param estrategia 0=Original(Elitismo), 1=Torneio, 2=Roleta, 3=Bucket
     */
    public static Pattern[] aleatorioD1_Pk_Estrategico(String tipoAvaliacao, int tamanhoPopulacao, Pattern[] I, Pattern[] Pk, int estrategia) {
        Pattern[] P0 = new Pattern[tamanhoPopulacao];

        // --- 1) Define quantidade de indivíduos a partir de I (90%) ---
        int qtdI = (9 * tamanhoPopulacao) / 10;
        qtdI = Math.min(qtdI, I.length);

        // AQUI APLICAMOS AS ESTRATÉGIAS ANTERIORES
        if (qtdI > 0) {
            switch (estrategia) {
                case 1: // Torneio (Seleciona bons, mas varia)
                    preencherPorTorneio(P0, I, qtdI, tipoAvaliacao);
                    break;
                case 2: // Roleta (Seleciona proporcional à qualidade)
                    preencherPorRoleta(P0, I, qtdI, tipoAvaliacao);
                    break;
                case 3: // Bucket / Faixas (Melhor para alta dimensionalidade)
                    preencherPorBucket(P0, I, qtdI, tipoAvaliacao);
                    break;
                default: // Original (Copia sempre os mesmos Top-N se I estiver ordenado)
                    System.arraycopy(I, 0, P0, 0, qtdI);
                    break;
            }
        }

        // --- 2 a 5) Mantém a lógica de aproveitar o conhecimento do Pk (10%) ---
        // Essa parte é boa! Ela funciona como uma "memória" do que funcionou antes.

        // Coleta itens distintos de Pk
        HashSet<Integer> itensPkSet = new HashSet<>();
        for (Pattern p : Pk) {
            if(p != null) itensPkSet.addAll(p.getItens());
        }
        Integer[] itensPkArray = itensPkSet.toArray(new Integer[0]);

        // Define dimensão média
        int numeroDimensoes = (int) Avaliador.avaliarMediaDimensoes(Pk, Pk.length);
        numeroDimensoes = Math.max(numeroDimensoes, 2);
        if (itensPkArray.length > 0 && numeroDimensoes > itensPkArray.length) {
            numeroDimensoes = itensPkArray.length;
        }

        // Preenche o restante (10%)
        for (int j = qtdI; j < tamanhoPopulacao; j++) {
            HashSet<Integer> itens = new HashSet<>();

            if (itensPkArray.length > 0) {
                List<Integer> listaItens = new ArrayList<>(Arrays.asList(itensPkArray));
                Collections.shuffle(listaItens, Const.random);
                // Proteção de índice
                int subListEnd = Math.min(numeroDimensoes, listaItens.size());
                itens.addAll(listaItens.subList(0, subListEnd));
            } else {
                // Fallback global
                while (itens.size() < numeroDimensoes) {
                    itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
                }
            }
            P0[j] = new Pattern(itens, tipoAvaliacao);
        }

        return P0;
    }

    // ESTRATÉGIA 1: TORNEIO
    private static void preencherPorTorneio(Pattern[] destino, Pattern[] fonte, int qtd, String tipoAvaliacao) {
        int tamanhoTorneio = 5; // Ajustável
        for (int i = 0; i < qtd; i++) {
            Pattern vencedor = null;
            double melhorQ = Double.NEGATIVE_INFINITY;

            for (int k = 0; k < tamanhoTorneio; k++) {
                Pattern desafiante = fonte[Const.random.nextInt(fonte.length)];
                if (desafiante.getQualidade() > melhorQ) {
                    melhorQ = desafiante.getQualidade();
                    vencedor = desafiante;
                }
            }
            // Importante: Clonar o HashSet para evitar referência cruzada
            if(vencedor != null)
                destino[i] = new Pattern(new HashSet<>(vencedor.getItens()), tipoAvaliacao);
        }
    }

    // ESTRATÉGIA 2: ROLETA SIMPLES
    private static void preencherPorRoleta(Pattern[] destino, Pattern[] fonte, int qtd, String tipoAvaliacao) {
        // Pré-calcula soma (pode ser otimizado calculando uma vez só fora, mas aqui fica modular)
        double somaTotal = 0;
        double[] acumulada = new double[fonte.length];
        for (int k = 0; k < fonte.length; k++) {
            double q = Math.max(0, fonte[k].getQualidade());
            somaTotal += q;
            acumulada[k] = somaTotal;
        }

        for (int i = 0; i < qtd; i++) {
            double r = Const.random.nextDouble() * somaTotal;
            // Busca Binária para performance em N grande
            int index = Arrays.binarySearch(acumulada, r);
            if (index < 0) index = (-index) - 1;
            if (index >= fonte.length) index = fonte.length - 1;

            destino[i] = new Pattern(new HashSet<>(fonte[index].getItens()), tipoAvaliacao);
        }
    }

    // ESTRATÉGIA 3: BUCKET / FAIXAS (Ideal para o seu caso)
    // ESTRATÉGIA 3: BUCKET / FAIXAS (CORRIGIDO)
    private static void preencherPorBucket(Pattern[] destino, Pattern[] fonte, int qtd, String tipoAvaliacao) {
        // 1. Encontrar Min e Max reais varrendo o array (Mais seguro que assumir ordenação)
        double minQ = Double.MAX_VALUE;
        double maxQ = Double.NEGATIVE_INFINITY;

        for (Pattern p : fonte) {
            double q = Math.max(0, p.getQualidade());
            if (q < minQ) minQ = q;
            if (q > maxQ) maxQ = q;
        }

        // Proteção se todos forem iguais ou min > max (erro lógico)
        if (minQ >= maxQ) {
            // Se não há variação, usa seleção aleatória simples ou roleta uniforme
            preencherPorRoleta(destino, fonte, qtd, tipoAvaliacao);
            return;
        }

        int NUM_FAIXAS = 10;
        double tamanhoFaixa = (maxQ - minQ) / NUM_FAIXAS;

        // Mapa: Índice da Faixa -> Lista de Índices no vetor fonte
        List<List<Integer>> indicesPorFaixa = new ArrayList<>();
        double[] pesoFaixas = new double[NUM_FAIXAS];
        for (int k = 0; k < NUM_FAIXAS; k++) indicesPorFaixa.add(new ArrayList<>());

        double somaPesos = 0;
        for (int k = 0; k < fonte.length; k++) {
            double q = Math.max(0, fonte[k].getQualidade());

            // CÁLCULO SEGURO DO ÍNDICE
            int f = (int) ((q - minQ) / tamanhoFaixa);

            // CORREÇÃO DO ERRO INDEX -15:
            // Força o índice a ficar entre 0 e 9 (Clamp)
            if (f < 0) f = 0;
            if (f >= NUM_FAIXAS) f = NUM_FAIXAS - 1;

            indicesPorFaixa.get(f).add(k);
            pesoFaixas[f] += q;
            somaPesos += q;
        }

        for (int i = 0; i < qtd; i++) {
            double r = Const.random.nextDouble() * somaPesos;
            int faixaSel = -1;
            double acc = 0;

            for (int f = 0; f < NUM_FAIXAS; f++) {
                acc += pesoFaixas[f];
                if (acc >= r) {
                    faixaSel = f;
                    break;
                }
            }

            // Proteção final para a roleta
            if (faixaSel == -1) faixaSel = NUM_FAIXAS - 1;

            List<Integer> indicesDisponiveis = indicesPorFaixa.get(faixaSel);

            // Se a faixa sorteada estiver vazia (raro, mas possível), pega um aleatório geral
            if (indicesDisponiveis.isEmpty()) {
                destino[i] = new Pattern(new HashSet<>(fonte[Const.random.nextInt(fonte.length)].getItens()), tipoAvaliacao);
            } else {
                // Escolhe item dentro da faixa
                int indexReal = indicesDisponiveis.get(Const.random.nextInt(indicesDisponiveis.size()));
                destino[i] = new Pattern(new HashSet<>(fonte[indexReal].getItens()), tipoAvaliacao);
            }
        }
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
            HashSet<Integer> itens = new HashSet<>();
                        
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
            HashSet<Integer> itens = new HashSet<>();
                        
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
            HashSet<Integer> itens = new HashSet<>();
           
            while(itens.size() < dimensao){
                itens.add(D.itensUtilizados[Const.random.nextInt(D.numeroItensUtilizados)]);
            }            
            
            P0[i] = new Pattern(itens, tipoAvaliacao);
        }        
        return P0;
    }
   
        
}
