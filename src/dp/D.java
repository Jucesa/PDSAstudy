package dp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.io.BufferedReader;
import java.util.*;
/**
 *
 * @author Tarcisio Lucas
 */
public class D {
   
    public static String nomeBase;
    public static String caminho;
    public static int numeroExemplos;
    public static int numeroExemplosPositivo;
    public static int numeroExemplosNegativo;
    public static int numeroAtributos;
    public static int numeroItens;
    
    public static String SEPARADOR = ","; 
    
    public static String[] nomeVariaveis;   
    
    public static int[] itemAtributo;
    public static int[] itemValor;
    public static String[] itemAtributoStr;
    public static String[] itemValorStr;
    
    public static int[][] Dp;
    public static int[][] Dn;
    
    public static int[] itensUtilizados;
    public static int numeroItensUtilizados;
    
    public static final int TIPO_CSV = 0;
    public static final int TIPO_ARFF = 1;
    public static final int TIPO_EXCEL = 2;
    
    public static String[][] dadosStr;
        
    public static String tipoDiscretizacao;
    
    public static String valorAlvo = "";
    public static String[] valoresAlvo;

    /** Recebe caminho para base de dados e tipo de formato e carrega base de dados na classe D 
     * @param caminho - caminho do arquivo completo
     * @param tipoArquivo - tipodo arquivo: CSV, ARFF, EXCEL, etc.
     * @throws FileNotFoundException 
     */    
    public static void CarregarArquivo(String caminho, int tipoArquivo) throws FileNotFoundException{
          
        //Passa dados do formato específico para um formato padrão definido por nós: String[][] dadosStr 
        D.dadosStr = null;
        switch(tipoArquivo){
            case D.TIPO_CSV:
                dadosStr = D.CVStoDadosStr(caminho);
                break;
            case D.TIPO_ARFF:
                //não implementado
                break;
            case D.TIPO_EXCEL:
                //não implementado
                break;
        }           
    }
    
    
    /** Gera D, Dp, Dn e itnes a partir da base salva no formato de matriz de String: dadosStr: String[][]  
     * @param rotulo: String valor de referência para dividir Dp e Dn 
     */    
    public static void GerarDpDn(String rotulo) throws FileNotFoundException{
        //Atribuindo alvo
        D.valorAlvo = rotulo;
        
        //Carrega a partir do nosso formato em D
        D.dadosStrToD(dadosStr); 
        
        //Filtro determina os itens que serão considerados pelos algoritmos
        //Por padrão todos são aceitos
        D.numeroItensUtilizados = D.numeroItens;
        D.itensUtilizados = new int[D.numeroItensUtilizados];
        for(int l = 0; l < D.numeroItensUtilizados; l++){
            D.itensUtilizados[l] = l;
        }
    }



    public static void carregarBaseVertical(String caminho, String valorAlvoDesejado) throws IOException {
        System.out.println("Carregando base para memória vertical (BitSets)...");
        D.caminho = caminho;

        // 1. Mapeamento de "Atributo=Valor" para um ID Inteiro único
        // Ex: "Idade=30" -> ID 0, "Sexo=M" -> ID 1
        Map<String, Integer> mapaItemParaId = new HashMap<>();
        List<BitSet> listaBitSets = new ArrayList<>();
        List<String> listaNomesItens = new ArrayList<>();

        // Configuração do Leitor (BufferedReader é mais rápido que Scanner para arquivos grandes)
        BufferedReader br = new BufferedReader(new FileReader(caminho));

        String linha = br.readLine();
        if (linha == null) return;

        // Tratamento do cabeçalho
        String separador = D.SEPARADOR; // Garanta que D.SEPARADOR esteja definido (ex: "," ou ";")
        String[] cabecalho = linha.split(separador);
        for(int i=0; i<cabecalho.length; i++) cabecalho[i] = cabecalho[i].replaceAll("[\"\r']", "").trim();

        D.nomeVariaveis = cabecalho;
        D.numeroAtributos = cabecalho.length - 1; // Último é o alvo

        // Inicializa BitSet do Target
        BitSet targetBits = new BitSet();
        int numeroLinha = 0;

        // 2. Leitura Linha a Linha (Streaming)
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;

            String[] colunas = linha.split(separador);

            // Garante que a linha tem o tamanho certo
            if (colunas.length < cabecalho.length) continue;

            // Processa Atributos (do 0 até penúltimo)
            for (int i = 0; i < D.numeroAtributos; i++) {
                String valor = colunas[i].replaceAll("[\"\r']", "").trim();

                // Constrói chave única: "NomeAtributo=Valor"
                // Se for numérico contínuo, você precisaria discretizar ANTES ou aqui.
                // Assumindo que já está discretizado ou é categórico:
                String chaveItem = cabecalho[i] + "=" + valor;

                // Verifica se este item já tem ID
                int idItem;
                if (!mapaItemParaId.containsKey(chaveItem)) {
                    idItem = mapaItemParaId.size();
                    mapaItemParaId.put(chaveItem, idItem);
                    listaNomesItens.add(chaveItem);
                    listaBitSets.add(new BitSet()); // Cria novo vetor vertical
                } else {
                    idItem = mapaItemParaId.get(chaveItem);
                }

                // MARCA O BIT: O exemplo 'numeroLinha' possui o item 'idItem'
                listaBitSets.get(idItem).set(numeroLinha);
            }

            // Processa Classe (última coluna)
            String valorClasse = colunas[D.numeroAtributos].replaceAll("[\"\r']", "").trim();
            // Se a classe for igual ao alvo que estamos procurando (ex: "sim", "doente", "1")
            if (valorClasse.equals(valorAlvoDesejado)) {
                targetBits.set(numeroLinha);
            }

            numeroLinha++;
        }
        br.close();

        // 3. Finalização e Atualização das Estruturas Estáticas
        D.numeroExemplos = numeroLinha;
        D.numeroItensUtilizados = listaBitSets.size();

        // Converte listas para arrays estáticos (acesso O(1) muito rápido)
        CacheVertical.cacheItens = listaBitSets.toArray(new BitSet[0]);
        CacheVertical.targetBitSet = targetBits;
        CacheVertical.nomeItens = listaNomesItens.toArray(new String[0]);

        // Atualiza D.itensUtilizados para o algoritmo genético saber quais IDs existem
        D.itensUtilizados = new int[D.numeroItensUtilizados];
        for(int i=0; i<D.numeroItensUtilizados; i++) D.itensUtilizados[i] = i;

        System.out.println("Base carregada com sucesso!");
        System.out.println("Exemplos: " + D.numeroExemplos);
        System.out.println("Itens únicos (Atributo=Valor): " + D.numeroItensUtilizados);
    }

    public static void carregarBaseOtimizada(String caminho, String rotuloAlvo) throws IOException {
        System.out.println("--- Iniciando Carregamento Otimizado (BitSets) ---");
        CacheVertical.reset(); // Limpa lixo anterior

        D.caminho = caminho;
        D.valorAlvo = rotuloAlvo;

        // Mapas temporários para criar IDs únicos para cada "Atributo=Valor"
        Map<String, Integer> mapaItemParaId = new HashMap<>();
        List<BitSet> listaBitSets = new ArrayList<>();
        List<String> listaNomes = new ArrayList<>();

        // Leitor de alta performance (BufferedReader)
        BufferedReader br = new BufferedReader(new FileReader(caminho));

        // 1. Ler Cabeçalho
        String linha = br.readLine();
        if (linha == null) throw new IOException("Arquivo vazio!");

        // Define separador (assume ; ou ,)
        String separador = D.SEPARADOR != null ? D.SEPARADOR : (linha.contains(";") ? ";" : ",");

        String[] cabecalho = linha.split(separador);
        for(int i=0; i<cabecalho.length; i++) {
            cabecalho[i] = cabecalho[i].replaceAll("[\"\r']", "").trim();
        }

        D.nomeVariaveis = cabecalho;
        D.numeroAtributos = cabecalho.length - 1; // Último coluna é o alvo

        BitSet targetBits = new BitSet();
        int rowId = 0;

        // 2. Loop de Leitura (Streaming)
        while ((linha = br.readLine()) != null) {
            if (linha.trim().isEmpty()) continue;

            String[] colunas = linha.split(separador);
            if (colunas.length < cabecalho.length) continue; // Ignora linhas quebradas

            // --- Processa a Classe (Target) ---
            String valorClasse = colunas[D.numeroAtributos].replaceAll("[\"\r']", "").trim();
            if (valorClasse.equals(rotuloAlvo)) {
                targetBits.set(rowId);
            }

            // --- Processa Atributos ---
            for (int col = 0; col < D.numeroAtributos; col++) {
                String valor = colunas[col].replaceAll("[\"\r']", "").trim();

                // Cria a chave única do item: "NomeAtributo=Valor"
                // IMPORTANTE: Se seus dados forem contínuos (números quebrados),
                // você deve discretizar AQUI ou arredondar, senão criará infinitos itens.
                String chaveItem = cabecalho[col] + "=" + valor;

                // Busca ou Cria ID para o Item
                int itemId;
                if (mapaItemParaId.containsKey(chaveItem)) {
                    itemId = mapaItemParaId.get(chaveItem);
                } else {
                    itemId = mapaItemParaId.size();
                    mapaItemParaId.put(chaveItem, itemId);
                    listaNomes.add(chaveItem);
                    listaBitSets.add(new BitSet()); // Cria a coluna vertical
                }

                // Marca que este exemplo (rowId) tem este item (itemId)
                listaBitSets.get(itemId).set(rowId);
            }
            rowId++;
        }
        br.close();

        // 3. Finalização e Vínculo com classe D
        D.numeroExemplos = rowId;
        D.numeroItens = listaBitSets.size();
        D.numeroItensUtilizados = D.numeroItens;

        // Transfere para Arrays Estáticos (Acesso O(1))
        CacheVertical.cacheItens = listaBitSets.toArray(new BitSet[0]);
        CacheVertical.nomeItens = listaNomes.toArray(new String[0]);
        CacheVertical.targetBitSet = targetBits;

        // Popula vetor de itens utilizados (exigido pelo seu Genético)
        D.itensUtilizados = new int[D.numeroItens];
        for(int i=0; i<D.numeroItens; i++) {
            D.itensUtilizados[i] = i;
        }

        System.out.println("Base Carregada!");
        System.out.println("Exemplos: " + D.numeroExemplos);
        System.out.println("Itens Únicos: " + D.numeroItens);
        System.out.println("Alvos Positivos: " + targetBits.cardinality());
    }
    
    //Densidade é a quantidade
    public static double densidade(){        
        return 0.0;
    }
    

    
    /**Recebe caminho para arquivo .CSV ou .csv e retorna matriz de strings onde dadosStr:String[numeroExemplo][numeroAtributos].
     * Além disso: 
     * (1) Salva nome da base em D.nomeBase
     * (2) Salva os nomes dos atributos e do rótulo em D.nomeVariaveis
     * (3) Salva número de exemplos e de atributos
     * (4) Salva caminho da base em D.caminho
     * @param caminho - caminho do arquivo csv
     * @return String[][] - String[numeroExemplo][numeroAtributos]
     * @throws FileNotFoundException - joga erro se arq nao encontrado
     */
    private static String[][] CVStoDadosStr(String caminho) throws FileNotFoundException{
        //Lendo arquivo no formato padrão
        D.caminho = caminho;
        Scanner scanner = new Scanner(new FileReader(D.caminho))
                       .useDelimiter("\\n");
        ArrayList<String[]> dadosString = new ArrayList<>();        
              
        
        String[] palavras = D.caminho.split("\\\\");
        if(palavras.length == 1){
            palavras = D.caminho.split("/");//Caso separador de pastas seja / e  não \\
        }
        
        D.nomeBase = palavras[palavras.length-1].replace(".CSV", "");//Nome do arquivo é a última palavra (caso .CSV)
        D.nomeBase = D.nomeBase.replace(".csv", "");//(caso .csv)
                
        D.nomeVariaveis = scanner.next().split(D.SEPARADOR); //1º linha: nome das variáveis
        //Lipando nomes dos atributos
        for(int i = 0; i < D.nomeVariaveis.length; i++){
            D.nomeVariaveis[i] = D.nomeVariaveis[i].replaceAll("[\"\r']", "");
        }
        
        D.numeroAtributos = D.nomeVariaveis.length-1; //último atributo é o rótulo
        while (scanner.hasNext()) {
            dadosString.add(scanner.next().split(D.SEPARADOR));
        }
        D.numeroExemplos = dadosString.size();
        
        HashSet<String> valoresAlvoHasSet = new HashSet<>();
        String[][] dadosStr = new String[D.numeroExemplos][D.numeroAtributos+1];
        for(int i = 0; i < dadosString.size(); i++){
            String[] exemploBase = dadosString.get(i);//recebe linha de dados
            for(int j = 0; j < exemploBase.length; j++){
                dadosStr[i][j] = exemploBase[j].replaceAll("[\"\r']", "");
            }
            //valoresAlvoHasSet.add(exemploBase[D.numeroAtributos]);
            valoresAlvoHasSet.add(dadosStr[i][D.numeroAtributos]);           
        }       
        
        //Coletanto valores distintos do atributo alvo
        D.valoresAlvo = new String[valoresAlvoHasSet.size()];
        Iterator<String> iterator = valoresAlvoHasSet.iterator();
        int indice = 0;
        while(iterator.hasNext()){
            D.valoresAlvo[indice++] = iterator.next();
        }
        Arrays.sort(D.valoresAlvo);
        
        return dadosStr;
    }
        
    /**Recebe dados no formato de String e preenche classe D com o universo de itens e exemplos positivos e negativos
     * (1) Gera universos de itens (atributo, valores) carregando em itemAtributoStr(String[]) e itemValorStr(String[])
     * (2) Mapeia universo de itens no formato original para inteiros: itemAtributo(int[]) e itemValor(int[]) 
     * (3) Mapeia base de dados para o formato de inteiros
     * OBS: a posição do array é o Item no problema de Grupos Discriminativos. 
     * Posição i, por exemplo é um item que representa o atributo itemAtributoStr[i] com valor itemValorStr[i].
     * Tais valores são mapeados nos inteiros itemAtributo[i] e itemValor[i], formato final da base de dadosutilizadas pelos algoritmos.  
     * @param dadosStr - conjunto de dados em string
     */
    private static void dadosStrToD(String[][] dadosStr){
                
        //Capturando os valores distintos de cada atributo
        ArrayList<HashSet<String>> valoresDistintosAtributos = new ArrayList<>(); //Amazena os valores distintos de cada atributo em um linha
        D.numeroItens = 0;
        for(int i = 0; i < D.numeroAtributos; i++){
            HashSet<String> valoresDistintosAtributo = new HashSet<>(); //Armazena valores distintos de apenas um atributo. Criar HashSet para armezenar valores distintos de um atributo. Não admite valores repetidos!
            for(int j = 0; j < D.numeroExemplos; j++){
                valoresDistintosAtributo.add(dadosStr[j][i]); //Coleção não admite valores repetidos a baixo custo computacional.
            }
            D.numeroItens += valoresDistintosAtributo.size();
            
            valoresDistintosAtributos.add(valoresDistintosAtributo); //Adiciona lista de valores distintos do atributo de índice i na posição i do atributo atributosEvalores
        }
        
        //Gera 4 arrays para armazenar o universo deatributos e valores no formato original (String) e mapeado para inteiro.
        D.itemAtributoStr = new String[D.numeroItens];
        D.itemValorStr = new String[D.numeroItens];
        D.itemAtributo = new int[D.numeroItens];
        D.itemValor = new int[D.numeroItens];
            
        //Carrega arrays com universos de itens com valores reais e respectivos inteiros mapeados
        int[][] dadosInt = new int[D.numeroExemplos][D.numeroAtributos]; //dados no formato inteiro: mais rápido compararinteiros que strings
        int indiceItem = 0; //Indice vai de zero ao número de itens total
        for(int indiceAtributo = 0; indiceAtributo < valoresDistintosAtributos.size(); indiceAtributo++){
            Iterator<String> valoresDistintosAtributoIterator = valoresDistintosAtributos.get(indiceAtributo).iterator(); //Capturando valores distintos do atributo de indice.txt i
            int indiceValor = 0; //vai mapear um inteiro distinto para cada valor distinto de cada variável
            
            //Para cada atributo: 
            //Atribui inteiro para atributo e a cada valor do atributo.  
            //Realizar mapeamento na matriz de dados no formato inteiro
            while(valoresDistintosAtributoIterator.hasNext()){
                D.itemAtributoStr[indiceItem] = D.nomeVariaveis[indiceAtributo]; //
                D.itemValorStr[indiceItem] = valoresDistintosAtributoIterator.next();

                D.itemAtributo[indiceItem] = indiceAtributo;
                D.itemValor[indiceItem] = indiceValor;               
                
                //Preenche respectivo item (atributo, Valor) na matrix dadosInt com inteiro que mapeia valor categórico da base
                for(int m = 0; m < D.numeroExemplos; m++){
                    if(dadosStr[m][indiceAtributo].equals(D.itemValorStr[indiceItem])){
                        dadosInt[m][indiceAtributo] = D.itemValor[indiceItem];
                    }
                }
                indiceValor++;
                indiceItem++;
            }     
        } 
        
        //Gera Bases de exemplos positivos (D+) e negativos (D-)
        D.geraDpDn(dadosStr, dadosInt);
    }
    
    /**
     * Gerar bases D+ (ou Dp) e D- (ou Dn) no formato numérico considerando D.valorAlvo como classe alvo
     * @param dadosStr - conjunto de dados em string
     * @param dadosInt - conjunto de dados em int
     */
    private static void geraDpDn(String[][] dadosStr, int[][] dadosInt){
        //Capturar número de exemplo positivos (y="p") e negativos (y="n")
        int indiceRotulo = D.numeroAtributos;
        D.numeroExemplosPositivo = 0;
        D.numeroExemplosNegativo = 0;
        //Contanto número de exemplos positivos e negativos
        for(int i = 0; i < D.numeroExemplos; i++){
            String y = dadosStr[i][indiceRotulo];
            //if(y.equals(D.valorAlvo) || y.equals("\"" + D.valorAlvo + "\"\r") || y.equals("\'" + D.valorAlvo + "\'\r") || y.equals(D.valorAlvo + "\r")){
            if(y.equals(D.valorAlvo)){
                D.numeroExemplosPositivo++;
            }else{
                D.numeroExemplosNegativo++;
            }
        }
        
        //inicializando Dp e Dn
        D.Dp = new int[D.numeroExemplosPositivo][D.numeroAtributos];
        D.Dn = new int[D.numeroExemplosNegativo][D.numeroAtributos];
        
        int indiceDp = 0;
        int indiceDn = 0;
        for(int i = 0; i < D.numeroExemplos; i++){
            String yValue = dadosStr[i][indiceRotulo];
            //if(yValue.equals(D.valorAlvo) || yValue.equals("\"" + D.valorAlvo + "\"\r") || yValue.equals("\'" + D.valorAlvo + "\'\r") || yValue.equals(D.valorAlvo + "\r")){
            if(yValue.equals(D.valorAlvo)){
                if (D.numeroAtributos >= 0) System.arraycopy(dadosInt[i], 0, Dp[indiceDp], 0, D.numeroAtributos);
                indiceDp++;
            }else{
                if (D.numeroAtributos >= 0) System.arraycopy(dadosInt[i], 0, Dn[indiceDn], 0, D.numeroAtributos);
                indiceDn++;            
            }
        }
        System.out.println();
    }
    
    /**
     * Gera arquivo de dicionário .txt imprimindo valores de atributo e valor original e respectivos inteiros aos quais forma mapeados 
     * @param caminhoPastaSalvar - onde será salvo o arquivo com o dicionário
     * @throws IOException -
     */
    public static void recordDicionario(String caminhoPastaSalvar) throws IOException{
        String nomeArquivo = caminhoPastaSalvar + "\\" + D.nomeBase + "Dic.txt";
        String separadorDicionario = ",";
        File file = new File(nomeArquivo);
        // creates the file
        file.createNewFile();
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file); 
        // Writes the content to the file
        
        writer.write("@Nome: " + D.nomeBase + "\r\n"); 
        writer.write("@Info: Atributos=" + D.numeroAtributos +  separadorDicionario + "|D|=" +  D.numeroExemplos + separadorDicionario + "|Dp|=" + D.numeroExemplosPositivo + separadorDicionario + "|Dn|=" + D.numeroExemplosNegativo
            + separadorDicionario + "|I|=" + D.numeroItensUtilizados + "\r\n"); 
        //writer.write(); 
        writer.write("@Dicionario:Item,Atributo,Valor" + "\r\n"); 
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            writer.write(i + separadorDicionario + D.itemAtributoStr[i] + separadorDicionario + itemValorStr[i] + "\r\n");           
        }      
        writer.flush();
        writer.close();
    }
    
    /**
     * Imprime dicionário no console. É um alternativa ao mét0do recordDicionario que salva em arquivo.
     * @deprecated 
     * OBS: esse mét0do pode estar defasado!
     */
    public static void imprimirDicionario(){        
        System.out.println("@Nome:" + D.nomeBase);
        System.out.println("@Info:Atributos=" + D.numeroAtributos + " ; |D|=" +  D.numeroExemplos + " ; |Dp|=" + D.numeroExemplosPositivo + " ; |Dn|=" + D.numeroExemplosNegativo
            + "; |I|=" + D.numeroItensUtilizados);
        //System.out.println("@Dicionario: Item;atributoOriginal;valorOriginal;atributoInt;valorInt");
        System.out.println("@Dicionario: Item;Atributo;Valor");
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            //System.out.println(i + ";" + D.itemAtributoStr[i] + ";" + itemValorStr[i] + ";" + D.itemAtributo[i] + ";" + D.itemValor[i]);
            System.out.println(i + ";" + D.itemAtributoStr[i] + ";" + itemValorStr[i]);
        }        
    }

    /**
     * Filtra atributos, valores e itens (atributo, valor) passados como parâmetros.
     * Os itens filtrados não serão consideraodos pelos algoritmos nas buscas.
     * @param atributos
     * @param valores
     * @param atributosValores 
     */
    public static void filtrar(String[] atributos, String[] valores, String[][] atributosValores){
        ArrayList<Integer> itensPosFiltro = new ArrayList<>();
        for(int i = 0; i < D.numeroItens; i++){
            if(!(D.filtroAtributoContempla(atributos, i) ||
                    D.filtroValorContempla(valores, i) || 
                    D.filtroAtributoValorContempla(atributosValores, i)))
            {
                itensPosFiltro.add(i); //Adicione caso não perteça a nenhum filtro
            }

        }
        
        D.numeroItensUtilizados = itensPosFiltro.size();
        D.itensUtilizados = new int[D.numeroItensUtilizados];
        for(int i = 0; i < D.itensUtilizados.length; i++){
            D.itensUtilizados[i] = itensPosFiltro.get(i);
        }        
    }
    
    /**
     * Mét0do retorna se item passado como parâmetro pertence ao grupo de atributos que devem ser desconsiderados na busca
     * @param atributos - String[] com valores de atributos que devem ser filtrados
     * @param item - item que deve ou não ser filtrado com base no filtro
     * @return boolean
     */
    private static boolean filtroAtributoContempla(String[] atributos, int item){
        if(atributos == null){
            return false;
        }else{
            for (String atributo : atributos) {
                //if(D.comparaStrVar(atributos[j], D.itemAtributoStr[item])){
                if (atributo.equals(itemAtributoStr[item])) {
                    return true;
                }
            }            
        }    
        return false;
    }
            
    
    /**
     * Mét0do retorna se item passado como parâmetro pertence ao grupo de VALORES que devem ser desconsiderados na busca
     * @param valores - String[] com valores de atributos que devem ser filtrados
     * @param item - item que deve ou não ser filtrado com base no filtro
     * @return boolean
     */
    private static boolean filtroValorContempla(String[] valores, int item){
        if(valores == null){
            return false;
        }else{
            for (String valore : valores) {
                //if( D.comparaStrVar(valores[j], D.itemValorStr[item]) ){
                if (valore.equals(D.itemValorStr[item])) {
                    return true;
                }
            }            
        }    
        return false;
    }
    
    
    /**
     * Método retorna se item passado como parâmetro pertence ao grupo de intens (atributo, valor) que devem ser desconsiderados na busca
     * @param atributosValores - String[][]
     * @param item - item que deve ou não ser filtrado com base no filtro
     * @return boolean
     */
    private static boolean filtroAtributoValorContempla(String[][] atributosValores, int item){
        if(atributosValores == null){
            return false;
        }else{
            for (String[] atributosValore : atributosValores) {
                //if(D.comparaStrVar(atributosValores[j][0], D.itemAtributoStr[item]) &&
                //   D.comparaStrVar(atributosValores[j][1], D.itemValorStr[item]) ){
                if (atributosValore[0].equals(D.itemAtributoStr[item]) &&
                        atributosValore[1].equals(D.itemValorStr[item])) {

                    return true;
                }
            }            
        }    
        return false;
    }
    
    /**
     * Compara duas strings com variações de formatos provavelemnte devido a fomatação do testo (ISO, ANSI, etc.) Não sei se estácobrindo todoas as possibilidades.
     * Deve ter uma forma mais elegante de lidar com esse problema!!!
     * @param palavra -
     * @param palavraVariacoes -
     * @return -
     */
    private static boolean comparaStrVar(String palavraVariacoes, String palavra){
        return (palavra.equals(palavraVariacoes) //|| 
                //palavra.equals( "\"" + palavraVariacoes  + "\"") || 
                //palavra.equals( "\"" + palavraVariacoes  + "\"\r") || 
                //palavra.equals("\'" + palavraVariacoes  + "\'\r") || 
                //palavra.equals(palavraVariacoes  + "\r")
                );       
    } 
    
    
    
    public static void main(String[] args) throws IOException{
        
//        String caminho = Const.CAMINHO_BASES + "amazon_cells_labelled.csv";
//        
//        D.CarregarArquivo(caminho, D.TIPO_CSV);
//              
//        System.out.println();
        
        String caminhoPastaArquivos = Const.CAMINHO_BASES;
        
        File diretorio = new File(caminhoPastaArquivos);
        File[] arquivos = diretorio.listFiles();
        D.SEPARADOR = ",";
        for(int i = 0; i < Objects.requireNonNull(arquivos).length; i++){
        //for(int i = 0; i < 2; i++){  
                String caminhoBase = arquivos[i].getAbsolutePath();
                D.CarregarArquivo(caminhoBase, D.TIPO_CSV);
                D.GerarDpDn("p");
                System.out.println("[" + i + "]");
                //D.imprimirDicionario();
                D.recordDicionario(Const.CAMINHO_DICIONARIOS);                
        }
    }

}
