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
        
        String separadorBase = ",";
        String separadorRelatorio = ",";
        
        //Tabelão
        String[] metricas = {
            Const.METRICA_WRACC,          
            Const.METRICA_WRACC_NORMALIZED,
            //Const.METRICA_TIME,
            Const.METRICA_SIZE,            
            Const.METRICA_TP,
            Const.METRICA_FP,   
            Const.METRICA_OVERALL_SUPP_POSITIVO,    
            Const.METRICA_COVER_REDUNDANCY_POSITIVO,
            Const.METRICA_DESCRIPTION_REDUNDANCY_DENSITY,
            Const.METRICA_DESCRIPTION_REDUNDANCY_DOMINATOR,
            Const.METRICA_Qg,                        
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
            Const.METRICA_SUPP_NEGATIVO            
        };        
        Relatorio.gerarTabelaoCSV(metricas, separadorBase, separadorRelatorio);
        
        System.out.println("Tabelão concluído");
        
        //Arquivo para teste de hipótese
        String[] nomeAlgoritmos = {
            //"DSSD-k20-foWRAcc"
//            Const.ALGORITMO_SSDP + "-k10-foWRAccN",
//            Const.ALGORITMO_SSDPmaisS10 + "-k10-foWRAccN",
//            Const.ALGORITMO_SSDPmaisS50 + "-k10-foWRAccN",
//            Const.ALGORITMO_SSDPmaisS90 + "-k10-foWRAccN"
//            Const.ALGORITMO_SD + "-k20-foWRAcc",
//            Const.ALGORITMO_SD_RSS + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS00 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS10 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS20 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS30 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS40 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS50 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS60 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS70 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS80 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS90 + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmaisS100 + "-k20-foWRAcc"
            
//            Const.ALGORITMO_SSDPmais10Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais10Prb + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais50Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais50Prb + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais90Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais90Prb + "-k20-foWRAcc"
            
            
           // Const.ALGORITMO_SSDP + "-k10-foQg",
//            Const.ALGORITMO_SSDPmais10Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais10Prb + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais50Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais50Prb + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais90Pbest + "-k20-foWRAcc",
//            Const.ALGORITMO_SSDPmais90Prb + "-k20-foWRAcc"
                
//            Const.ALGORITMO_SD + "-k20-foQg",
//            Const.ALGORITMO_SD_RSS + "-k20-foQg",
//            Const.ALGORITMO_SSDP + "-k20-foQg",
//            Const.ALGORITMO_SSDPmais80 + "-k20-foQg",
//            Const.ALGORITMO_SSDPmais50 + "-k20-foQg",
//            Const.ALGORITMO_SSDPmais20 + "-k20-foQg"
//            Const.ALGORITMO_SSDP + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisPbest + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2k + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2kselection + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best50 + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502k + "-k5-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502kselection + "-k5-foQg",
//            
//            Const.ALGORITMO_SSDP + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisPbest + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2k + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2kselection + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best50 + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502k + "-k10-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502kselection + "-k10-foQg",
//            
//            Const.ALGORITMO_SSDP + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisPbest + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2k + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2kselection + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best50 + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502k + "-k20-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502kselection + "-k20-foQg",
            
//            Const.ALGORITMO_SSDP + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisPbest + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2k + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisPbest2kselection + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best50 + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502k + "-k50-foQg",
//            Const.ALGORITMO_SSDPmaisRandom50Best502kselection + "-k50-foQg"
//            
            
            //Const.ALGORITMO_SSDPmais70NA + "-k10-foQg",
            //Const.ALGORITMO_SSDPmais10NA + "-k10-foQg"
            
            //Const.ALGORITMO_SSDP + "-k10-foQg",
            //Const.ALGORITMO_SD + "-k10-foQg"                            
        };
        
        //String metrica = Const.METRICA_DESCRIPTION_REDUNDANCY_DOMINATOR;
        //String metrica = Const.METRICA_COVER_REDUNDANCY_POSITIVO;
        //String metrica = Const.METRICA_WRACC_NORMALIZED;
        //String metrica = Const.METRICA_Qg;
//        Relatorio.gerarArquivoTestesHipotese(nomeAlgoritmos, metrica, separadorBase);
    }
}