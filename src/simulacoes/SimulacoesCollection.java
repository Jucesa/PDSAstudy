/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacoes;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;



/**
 *
 * @author Marianna
 */
public class SimulacoesCollection {
    public ArrayList<Simulacao> simulacoes;

    public SimulacoesCollection() {
        this.simulacoes = new ArrayList<>();
    }
    
    //Retorna Simulação com nome de base e algoritmo específico
    public Simulacao getSimulacao(String algoritmo, String base){
        for(int i = 0; i < this.simulacoes.size(); i++){
            Simulacao s = this.simulacoes.get(i);
            if(s.getAlgoritmo().equals(algoritmo) && s.getNomeBase().equals(base)){
                return s;
            }
        }
        return null;
    }
     
    //Retorna array com todas os objetos Simulacao
    public Simulacao[] getTodas(){
        Simulacao[] simulacoesArray = new Simulacao[this.simulacoes.size()];
        for(int i = 0; i < simulacoesArray.length; i++){
            simulacoesArray[i] = this.simulacoes.get(i);
        }
        return simulacoesArray;
    }
    
   
    //Carrega Objetos Simulacao a partir de arquivos de resultados mínimos
    //É preciso o caminho das bases por gambiarra também! Não deveria ser necessário! Está assim por causa da forma de inidicalizadação das DPs e da base D tipo static
    public void carregarSimulacoesFromObjTextTipo1(String caminhoResultados, String caminhoBases, String separadorBases) throws FileNotFoundException, IOException{
        if(caminhoResultados == null){
            caminhoResultados = "../DP2/pastas/resultados";
        }
        if(caminhoBases == null){
            caminhoBases = "../DP2/pastas/bases";
        }
        D.SEPARADOR = separadorBases;
        String funcaoObjetivo  = Avaliador.METRICA_AVALIACAO_WRACC; //Gambiarra pq classe Pattens exige tipo de função objetivo quando é inicializada
        
        File diretorio = new File(caminhoResultados);
        File arquivos[] = diretorio.listFiles();
        System.out.println(arquivos.length);        
        for(int i = 0; i < arquivos.length; i++){
            
            String nomeArquivo = arquivos[i].getName();
            
            //Apagar possíveis ruídos
            String nomeArquivoClean = nomeArquivo.replace("_pn", "").replace(".txt", "");
            
            
            //Extrair palavras com informações relevantes
            String[] palavras = nomeArquivoClean.split("_");
            
            String nomeAlgoritmo = palavras[0];           
            String nomeBase = palavras[1] + "-pn";
                        
            System.out.println("[" + i + "/" + (arquivos.length-1) + "]: " + nomeAlgoritmo + "_" +  nomeBase);
            
            D.CarregarArquivo(caminhoBases + "/" + nomeBase + ".CSV", D.TIPO_CSV);
            
            Scanner scanner = new Scanner(new FileReader(arquivos[i].getAbsolutePath()))
                       .useDelimiter("\\n");
            ArrayList<Pattern> DPs = new ArrayList<>();
            while (scanner.hasNext()) {
                String[] itensStr = scanner.next().split(", ");
                HashSet<Integer> itens = new HashSet<>();
                for(int j = 0; j < itensStr.length; j++){
                    itens.add( Integer.parseInt( itensStr[j] ) );
                }
                Pattern p = new Pattern(itens, funcaoObjetivo);
                DPs.add(p);
            }
            Pattern[] DPsArray = new Pattern[DPs.size()];
            for(int l = 0; l < DPsArray.length; l++){
                DPsArray[l] = DPs.get(l);
            }
            
            Resultado[] r = new Resultado[1];//Temoas apenas um resultado por simulação
            r[0] = new Resultado(DPsArray, -1, -1, -1); //Não temos os valores do tempo, número de tentativas e seed
            Simulacao simulacao = new Simulacao(nomeAlgoritmo, nomeBase, r);
            
            this.simulacoes.add(simulacao);         
        }
    }  
    
    
    //Carrega Objetos Simulacao a partir de arquivos de resultados completos
    //É preciso o caminho das bases por gambiarra também! Não deveria ser necessário! Está assim por causa da forma de inidicalizadação das DPs e da base D tipo static
    public void carregarSimulacoesFromText(String separadorBases, String separadorResultadoDPs) throws IOException {
        String caminhoResultados = Const.CAMINHO_RESULTADOS;
        String caminhoBases = Const.CAMINHO_BASES;
        D.SEPARADOR = separadorBases;
        String funcaoObjetivo  = Avaliador.METRICA_AVALIACAO_WRACC; // Gambiarra pq classe Pattens exige tipo de função objetivo quando é inicializada

        File diretorio = new File(caminhoResultados);
        File[] arquivos = diretorio.listFiles();
        assert arquivos != null;
        System.out.println(arquivos.length);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "^(?<algoritmo>.+)_(?<base>.+)$"
        );
        // Regex para capturar as partes do nome do arquivo

        for (File arquivo : arquivos) {
            String nomeArquivo = arquivo.getName();
            String nomeArquivoClean = nomeArquivo.replace(".txt", "");

            java.util.regex.Matcher matcher = pattern.matcher(nomeArquivoClean);

            if (!matcher.matches()) {
                System.err.println("Arquivo fora do padrão esperado: " + nomeArquivo);
                continue; // pula este arquivo
            }
            String nomeAlgoritmo = matcher.group("algoritmo"); // inclui -kXX-foYYY
            String base = matcher.group("base");           // nome completo da base

            System.out.println("Algoritmo=" + nomeAlgoritmo +
                    " | Base=" + base);

            D.CarregarArquivo(caminhoBases + "/" + base + ".CSV", D.TIPO_CSV);
            D.GerarDpDn("p");
            Scanner scanner = new Scanner(new FileReader(arquivo.getAbsolutePath()))
                    .useDelimiter("\\n");

            ArrayList<Resultado> resultadosArrayList = new ArrayList<>();
            ArrayList<Pattern> DPsArrayLis = new ArrayList<>();
            int rep = -1;
            double time = -1;
            int trys = -1;
            long seed = -1L;

            String linha = "";
            String palavra = "";

            if (!scanner.hasNext()) { // Caso específico de arquivo de resultado sem DPs dentro.
                Resultado[] rs = new Resultado[1];
                rs[0] = new Resultado(null, time, trys, seed);
                this.simulacoes.add(new Simulacao(nomeAlgoritmo, base, rs));
                continue;
            }

            linha = scanner.next();
            linha = linha.replaceFirst("\r", "");
            if (linha.contains("@rep")) { // Arquivo de resultado: padrão completo.
                palavra = linha.split(":")[1];
                if (!palavra.equals("?")) {
                    rep = Integer.parseInt(palavra);
                }
                while (scanner.hasNext()) {
                    linha = scanner.next();
                    linha = linha.replaceFirst("\r", "");
                    if (linha.contains("@rep")) {
                        palavra = linha.split(":")[1];
                        if (!palavra.equals("?")) {
                            rep = Integer.parseInt(palavra);
                        }
                    } else if (linha.contains("@time")) {
                        palavra = linha.split(":")[1];
                        if (!palavra.equals("?")) {
                            time = Double.parseDouble(palavra);
                        }
                    } else if (linha.contains("@trys")) {
                        palavra = linha.split(":")[1];
                        if (!palavra.equals("?")) {
                            trys = Integer.parseInt(palavra);
                        }
                    } else if (linha.contains("@seed")) {
                        palavra = linha.split(":")[1];
                        if (!palavra.equals("?")) {
                            seed = Long.parseLong(palavra);
                        }
                    } else if (linha.contains("@dps-begin")) {
                        DPsArrayLis = new ArrayList<>();
                        while (!linha.contains("@dps-end")) {
                            linha = scanner.next();
                            linha = linha.replace("\r", "");
                            if (linha.contains("@dps-end") || linha.isEmpty()) {
                                continue;
                            }
                            String[] itensStr = linha.split(separadorResultadoDPs);
                            HashSet<Integer> itens = new HashSet<>();
                            for (String s : itensStr) {
                                itens.add(Integer.parseInt(s));
                            }
                            Pattern p = new Pattern(itens, funcaoObjetivo);
                            DPsArrayLis.add(p);
                        }
                        Pattern[] dps = new Pattern[DPsArrayLis.size()];
                        for (int j = 0; j < dps.length; j++) {
                            dps[j] = DPsArrayLis.get(j);
                        }
                        Resultado r = new Resultado(dps, time, trys, seed);
                        resultadosArrayList.add(r);
                    } else if (linha.isEmpty()) {
                        continue;
                    }
                }

            } else { // Arquivo de resultado: padrão simples
                DPsArrayLis = new ArrayList<>();
                if (!linha.isEmpty()) {
                    String[] itensStr = linha.split(separadorResultadoDPs);
                    HashSet<Integer> itens = new HashSet<>();
                    for (int j = 0; j < itensStr.length; j++) {
                        itens.add(Integer.parseInt(itensStr[j].replace(" ", "")));
                    }
                    Pattern p = new Pattern(itens, funcaoObjetivo);
                    DPsArrayLis.add(p);
                }
                while (scanner.hasNext()) {
                    linha = scanner.next();
                    linha = linha.replaceFirst("\r", "");
                    if (linha.isEmpty()) {
                        continue;
                    }
                    String[] itensStr = linha.split(separadorResultadoDPs);
                    HashSet<Integer> itens = new HashSet<>();
                    for (String s : itensStr) {
                        itens.add(Integer.parseInt(s.replace(" ", "")));
                    }
                    Pattern p = new Pattern(itens, funcaoObjetivo);
                    DPsArrayLis.add(p);
                }

                Pattern[] dps = new Pattern[DPsArrayLis.size()];
                for (int j = 0; j < dps.length; j++) {
                    dps[j] = DPsArrayLis.get(j);
                }
                Resultado r = new Resultado(dps, time, trys, seed);
                resultadosArrayList.add(r);
            }

            Resultado[] resultados = new Resultado[resultadosArrayList.size()];
            for (int j = 0; j < resultados.length; j++) {
                resultados[j] = resultadosArrayList.get(j);
            }

            Simulacao simulacao = new Simulacao(nomeAlgoritmo, base, resultados);
            this.simulacoes.add(simulacao);
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException{
        SimulacoesCollection simulacoes = new SimulacoesCollection();
        simulacoes.carregarSimulacoesFromText(",", ",");
        System.out.println();
    }
}
