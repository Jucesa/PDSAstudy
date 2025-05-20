/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulacoes;

import dp.Const;
import dp.D;
import dp.Pattern;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;


/**
 *
 * @author Tarcísio Pontes
 */
public class Relatorio{
        
    public static void gerarTabelaoCSV(String[] metricas, String separadorBases, String separadorRelatorio) throws IOException {
        String caminhoBases = Const.CAMINHO_BASES;
        String caminhoResultados = Const.CAMINHO_RESULTADOS;
        String caminhoSalvarRelatorio = Const.CAMINHO_RELATORIO;
        String nomeRelatorio = "Tabelao.csv";
               
        D.SEPARADOR = separadorBases;     
        
        //Capturando simulações
        System.out.println("Carregando Simulações...");
        SimulacoesCollection simulacoesArrayList = new SimulacoesCollection();       
        simulacoesArrayList.carregarSimulacoesFromText(separadorBases, separadorRelatorio);
        Simulacao[] simulacoes = simulacoesArrayList.getTodas();
        
        //Carregando bases
        System.out.println("Carregando bases...");
        BasesArrayList bases = new BasesArrayList(caminhoBases, separadorBases);
        
        System.out.println("Escrevendo resultados...");
        //Abrindo arquivo para gravação de tabelão
        File file = new File(caminhoSalvarRelatorio + nomeRelatorio);
        file.createNewFile();
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file); 
        
        //Inserindo nomes das métricas
        StringBuilder labels = new StringBuilder();
        labels.append("Algoritmo" + separadorRelatorio + "Base" + separadorRelatorio + "Repeticao" + separadorRelatorio + "|D|" + separadorRelatorio +
                "|D+|" + separadorRelatorio + "|D-|" + separadorRelatorio + "Atributos" + separadorRelatorio + "|I|" + separadorRelatorio +
                "Tempo" + separadorRelatorio + "Testes" + separadorRelatorio + "Seed" + separadorRelatorio);
        for (String metrica : metricas) {
            labels.append(metrica);
            labels.append(separadorRelatorio);
        }
        writer.write(labels + "\n"); 
        
        
        //Inserindo simulações dados simulações     
        for (Simulacao s : simulacoes) {
            String nomeAlgoritmo = s.getAlgoritmo();
            String nomeBase = s.getNomeBase();
            Resultado[] resulratos = s.getResultados();
            for (int j = 0; j < resulratos.length; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(nomeAlgoritmo);
                sb.append(separadorRelatorio);
                sb.append(nomeBase);
                sb.append(separadorRelatorio);
                sb.append(j + 1);//Índice de repetição do experimento
                sb.append(separadorRelatorio);
                Resultado r = resulratos[j];
                double tempo = r.getTempoExecucao();
                long seed = r.getSeed();
                int numeroTestes = r.getNumeroTestes();
                Pattern[] dps = r.getDPs();

                //Informações da base de dados
                Base b = bases.getBase(nomeBase);
                sb.append(b.getNumeroExemplos());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroExemplosPositivo());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroExemplosNegativo());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroAtributos());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroItens());
                sb.append(separadorRelatorio);


                sb.append(tempo);
                sb.append(separadorRelatorio);

                sb.append(numeroTestes);
                sb.append(separadorRelatorio);

                sb.append(seed);
                sb.append(separadorRelatorio);

                for (int k = 0; k < metricas.length; k++) {
                    sb.append(DPinfo.metricaMedia(dps, b, metricas[k]));
                    if (k != metricas.length - 1) {
                        sb.append(separadorRelatorio);
                    }
                }

                writer.write(sb + "\n");
                writer.flush();

            }
        }     
        
        writer.close();
        
    }
    
    //Gera tabelão subistituindo valores NaN por desempenhos em regras vazias
    public static void gerarTabelaoCSVRegrasVaziasParaKzero(String[] metricas, String separadorBases, String separadorRelatorio) throws FileNotFoundException, IOException, ClassNotFoundException{
        String caminhoBases = Const.CAMINHO_BASES;
        String caminhoResultados = Const.CAMINHO_RESULTADOS;
        String caminhoSalvarRelatorio = Const.CAMINHO_RELATORIO;
        String nomeRelatorio = "TabelaoSemKzero.csv";
               
        D.SEPARADOR = separadorBases;     
        
        //Capturando simulações
        System.out.println("Carregando Simulações...");
        SimulacoesCollection simulacoesArrayList = new SimulacoesCollection();       
        simulacoesArrayList.carregarSimulacoesFromText(separadorBases, separadorRelatorio);
        Simulacao[] simulacoes = simulacoesArrayList.getTodas();
        
        //Carregando bases
        System.out.println("Carregando bases...");
        BasesArrayList bases = new BasesArrayList(caminhoBases, separadorBases);
        
        System.out.println("Escrevendo resultados...");
        //Abrindo arquivo para gravação de tabelão
        File file = new File(caminhoSalvarRelatorio + nomeRelatorio);
        file.createNewFile();
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file); 
        
        //Inserindo nomes das métricas
        StringBuilder labels = new StringBuilder();
        labels.append("Algoritmo").append(separadorRelatorio).append("Base").append(separadorRelatorio).append("Repeticao").append(separadorRelatorio).append("|D|").append(separadorRelatorio).append("|D+|").append(separadorRelatorio).append("|D-|").append(separadorRelatorio).append("Atributos").append(separadorRelatorio).append("|I|").append(separadorRelatorio).append("Tempo").append(separadorRelatorio).append("Testes").append(separadorRelatorio).append("Seed").append(separadorRelatorio);
        for (String metrica : metricas) {
            labels.append(metrica);
            labels.append(separadorRelatorio);
        }
        writer.write(labels + "\n"); 
        
        
        //Inserindo simulações dados simulações     
        for (Simulacao s : simulacoes) {
            String nomeAlgoritmo = s.getAlgoritmo();
            String nomeBase = s.getNomeBase();
            Resultado[] resulratos = s.getResultados();
            for (int j = 0; j < resulratos.length; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(nomeAlgoritmo);
                sb.append(separadorRelatorio);
                sb.append(nomeBase);
                sb.append(separadorRelatorio);
                sb.append(j + 1);//Índice de repetição do experimento
                sb.append(separadorRelatorio);
                Resultado r = resulratos[j];
                double tempo = r.getTempoExecucao();
                long seed = r.getSeed();
                int numeroTestes = r.getNumeroTestes();
                Pattern[] dps = r.getDPs();

                //Informações da base de dados
                Base b = bases.getBase(nomeBase);
                sb.append(b.getNumeroExemplos());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroExemplosPositivo());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroExemplosNegativo());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroAtributos());
                sb.append(separadorRelatorio);
                sb.append(b.getNumeroItens());
                sb.append(separadorRelatorio);


                sb.append(tempo);
                sb.append(separadorRelatorio);

                sb.append(numeroTestes);
                sb.append(separadorRelatorio);

                sb.append(seed);
                sb.append(separadorRelatorio);

                boolean kZero = false;
                if (dps == null) {
                    //Criar resultado onde não existe regra! Esse será o valor mínimo de todos os resultados!!!
                    b.carregarBaseEmD();
                    dps = new Pattern[1];
                    dps[0] = new Pattern(new HashSet<Integer>(), Const.METRICA_WRACC); //Gera DPs sem regra. Nesse caso tanto faz a métrica utlizada!                    
                    kZero = true;
                }

                for (int k = 0; k < metricas.length; k++) {
                    double valor = DPinfo.metricaMedia(dps, b, metricas[k]);
                    if (kZero && metricas[k].endsWith(Const.METRICA_K)) {
                        valor = 0;
                    }
                    sb.append(valor);

                    if (k != metricas.length - 1) {
                        sb.append(separadorRelatorio);
                    }
                }

                writer.write(sb + "\n");
                writer.flush();

            }
        }     
        
        writer.close();
        
    }
    
    
    public static void gerarArquivoTestesHipotese(String[] nomesAlgoritmo, String metrica, String separadorBases) throws FileNotFoundException, IOException, ClassNotFoundException{
        String separadorRelatorio = ",";
        String caminhoBases = Const.CAMINHO_BASES;
        String caminhoSalvarRelatorio = Const.CAMINHO_RELATORIO;
        String nomeRelatorio = "teste-hipotese-" + metrica + ".csv";
               
        D.SEPARADOR = separadorBases;     
        
        //Capturando simulações
        System.out.println("Carregando Simulações...");
        SimulacoesCollection simulacoes = new SimulacoesCollection();       
        simulacoes.carregarSimulacoesFromText(separadorBases, separadorRelatorio);
                
        //Carregando bases
        System.out.println("Carregando bases...");
        BasesArrayList bases = new BasesArrayList(caminhoBases, separadorBases);
        String[] nomesBase = bases.getNomeBases();
        
        System.out.println("Escrevendo resultados...");
        //Abrindo arquivo para gravação de tabelão
        File file = new File(caminhoSalvarRelatorio + nomeRelatorio);
        file.createNewFile();
        // creates a FileWriter Object
        FileWriter writer = new FileWriter(file); 
        
        //Inserindo cabeçalho conforme padrão
        StringBuilder labels = new StringBuilder();
        labels.append("Dataset,");
        for(int i = 0; i < nomesAlgoritmo.length-1; i++){            
            labels.append(nomesAlgoritmo[i]).append(",");
        }        
        labels.append(nomesAlgoritmo[nomesAlgoritmo.length-1]);
        writer.write(labels + "\n"); 
                
        //Inserindo simulações dados simulações     
        for (String s : nomesBase) {
            Base b = bases.getBase(s);
            b.carregarBaseEmD();
            StringBuilder linha = new StringBuilder();
            linha.append(b.getNome()).append(",");
            for (int j = 0; j < nomesAlgoritmo.length; j++) {
                Simulacao simulacao = simulacoes.getSimulacao(nomesAlgoritmo[j], s);
                if (simulacao == null) {
                    linha.append("null");
                } else {
                    Resultado[] resultados = simulacao.getResultados();
                    double valor = DPinfo.metricaMedia(resultados, b, metrica);
                    if (Double.isNaN(valor)) {
                        Pattern[] p = new Pattern[1];
                        p[0] = new Pattern(new HashSet<Integer>(), Const.METRICA_WRACC);
                        resultados[0] = new Resultado(p);
                        valor = DPinfo.metricaMedia(resultados, b, metrica);
                    }
                    linha.append(valor);

                }
                if (j != nomesAlgoritmo.length - 1) {
                    linha.append(",");
                }
            }
            writer.write(linha + "\n");
            writer.flush();
        }    
        writer.close();        
    }
    
    
    
    public static void main(String[] args) throws IOException{
        //Tabelão
        String[] metricas = {
                Const.METRICA_WRACC,
                Const.METRICA_Qg,
                Const.METRICA_OVERALL_SUPP_POSITIVO,
                Const.METRICA_COVER_REDUNDANCY_POSITIVO,
                Const.METRICA_DESCRIPTION_REDUNDANCY_DENSITY,
                Const.METRICA_DESCRIPTION_REDUNDANCY_DOMINATOR,
                Const.METRICA_CHI_QUAD,
                Const.METRICA_P_VALUE,
                Const.METRICA_LIFT,
                Const.METRICA_DIFF_SUP,
                Const.METRICA_K,
                Const.METRICA_GROWTH_RATE,
                Const.METRICA_ODDS_RATIO,
                Const.METRICA_COV,
                Const.METRICA_CONF,
                Const.METRICA_SUPP,
                Const.METRICA_SUPP_POSITIVO,
                Const.METRICA_SUPP_NEGATIVO,
                Const.METRICA_SIZE
        };
        String separadorBase = ",";
        String separadorRelatorio = ",";
        Relatorio.gerarTabelaoCSV(metricas, separadorBase, separadorRelatorio);

        System.out.println("Tabelão concluído");
    }
}