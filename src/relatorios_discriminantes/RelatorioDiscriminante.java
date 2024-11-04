/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package relatorios_discriminantes;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.PDSA;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

/**
 *
 * @author Tarcisio Lucas
 */
public class RelatorioDiscriminante {
    public static DecimalFormat df = new DecimalFormat("#.##");
    
       
    public static void TXT(
            //Base de dados
            String caminhoBase, String separadorBase, int tipoBase, 
            //Perfil descrição
            String tipoAvaliacao, String[] metricas, String[] rotulos, int ANDouORentreItens,
            //Relatório Caracterísitcas
            boolean relatorioItens, int kItens, 
            //Relatório Grupos
            boolean relatorioGrupos, int k, long seed, String[] filtrarAtributos, String[] filtrarValores, String[][] filtrarAtributosValores 
        ) throws FileNotFoundException{
        
        //Configurando arredondamento casas decimais
        df.setRoundingMode(RoundingMode.HALF_UP);
        
        //AND ou OR
        Pattern.ITENS_OPERATOR = ANDouORentreItens;
            
        //Carregando base de dados       
        D.SEPARADOR = separadorBase; //separator database
        Const.random = new Random(seed); //Seed
        D.CarregarArquivo(caminhoBase, tipoBase); //Loading database         
        
        //Gerando arquivo TXT para salvar tudo que seria impresso no console!
        Calendar cal = GregorianCalendar.getInstance();
        String nomeRelatorioTXT = caminhoBase +
                "RID_" + D.nomeBase + "_" + tipoAvaliacao + ((ANDouORentreItens==Const.PATTERN_AND)?"AND_":"OR_") +
                cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH)  + 
                "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.getTimeInMillis() +
                ".txt";

        PrintStream out = new PrintStream(new FileOutputStream(nomeRelatorioTXT, true));
        System.setOut(out);


        //Informações gerais da base de dados
        System.out.println("################################");
        System.out.println("BASE DE DADOS ##################");
        System.out.println("################################");

        System.out.println("Nome: " + D.nomeBase);
        System.out.println("Total Colunas (atributos): " + D.numeroAtributos);
        System.out.println("Total de Linhas (exemplos/amostras): " + D.numeroExemplos);
        String nomesClasses = "CLASSES: ";
        for(int j = 0; j < D.valoresAlvo.length; j++){
            nomesClasses = nomesClasses + D.valoresAlvo[j];
            if(j < D.valoresAlvo.length-1){
                nomesClasses = nomesClasses + ", ";                    
            }
        }
        System.out.println(nomesClasses);
       
        System.out.println();
        System.out.println("################################");
        System.out.println("INFORMAÇÕES TÉCNICAS ###########");
        System.out.println("################################");
        System.out.println("Métrica de avaliação: " + tipoAvaliacao);
        if(ANDouORentreItens == Const.PATTERN_AND){
            System.out.println("Operador entre características: AND");            
        }else{
            System.out.println("Operador entre características: OR");
        }
            
        
        //Para cada classe
        for (String rotulo : rotulos) {
            D.GerarDpDn(rotulo);

            System.out.println("\n");
            System.out.println("################################");
            System.out.println("INVESTIGANDO: CLASSE = " + rotulo);
            System.out.println("################################");
            double percentualPositivos = 100.0 * (double) D.numeroExemplosPositivo / (double) D.numeroExemplos;
            double percentualNegativos = 100.0 * (double) D.numeroExemplosNegativo / (double) D.numeroExemplos;
            System.out.println("CLASSE = " + rotulo + ": " + D.numeroExemplosPositivo + " linhas (" + df.format(percentualPositivos) + "%)");
            System.out.println("DEMAIS CLASSES: " + D.numeroExemplosNegativo + " linhas (" + df.format(percentualNegativos) + "%)");


            //Relatório de itens
            if (relatorioItens) {
                System.out.println();
                System.out.println("### TOP-" + kItens + " catacterísticas:");
                Pattern[] itens = INICIALIZAR.D1(tipoAvaliacao);
                Arrays.sort(itens);
                for (int j = 0; j < kItens; j++) {
                    System.out.println(itens[j].toString(metricas, false, false, false));
                }
            }


            //Relatórios de grupos
            Pattern[] pk = null;
            if (relatorioGrupos) {
                System.out.println("\n");
                System.out.println("### TOP-" + k + " grupos");

                //Executar filtros
                if (filtrarAtributos != null || filtrarValores != null || filtrarAtributosValores != null) {
                    D.filtrar(filtrarAtributos, filtrarValores, filtrarAtributosValores);
                    String descricao = "";
                    if (filtrarAtributos != null) {
                        for (int j = 0; j < filtrarAtributos.length; j++) {
                            descricao = descricao + filtrarAtributos[j];
                            if (j < filtrarAtributos.length - 1) {
                                descricao = descricao + ", ";
                            }
                        }
                        System.out.println("Atributos filtrados: {" + descricao + "}");
                    }
                    if (filtrarValores != null) {
                        descricao = "";
                        for (int j = 0; j < filtrarValores.length; j++) {
                            descricao = descricao + filtrarValores[j];
                            if (j < filtrarValores.length - 1) {
                                descricao = descricao + ", ";
                            }
                        }
                        System.out.println("Valores filtrados: {" + descricao + "}");
                    }
                    if (filtrarAtributosValores != null) {
                        descricao = "";
                        for (int j = 0; j < filtrarAtributosValores.length; j++) {
                            descricao = descricao + filtrarAtributosValores[j][0] + "->" + filtrarAtributosValores[j][1];
                            if (j < filtrarAtributosValores.length - 1) {
                                descricao = descricao + ", ";
                            }
                        }
                        System.out.println("Características filtradas: {" + descricao + "}");
                    }
                }

                //Minerando grupos
                pk = PDSA.run(k, tipoAvaliacao, -1); //run PDSA
//                SD sd = new SD();
//                pk = sd.run(0, 2*k, tipoAvaliacao, k); //run PDSA

                System.out.println("Cobertura total CLASSE = " + rotulo + ": " + df.format(Avaliador.coberturaPositivo(pk, k) * 100) + "%");
                //Impriminto top-k grupos
                for (int j = 0; j < k; j++) {
                    System.out.println(pk[j].toString(metricas, false, false, false));
                }

                //Itens utilizados

                //Obtendo todos os itens
                HashSet<Integer> allItensPk = new HashSet<Integer>();
                for (Pattern pattern : pk) {
                    allItensPk.addAll(pattern.getItens());
                }

                //Gerando um Pattern para cada item utilizado e salvando em itensUtilizados
                Pattern[] itensUtilizados = new Pattern[allItensPk.size()];
                Iterator<Integer> iterator = allItensPk.iterator();
                int indice = 0;
                while (iterator.hasNext()) {
                    HashSet<Integer> item = new HashSet<Integer>();
                    item.add((Integer) iterator.next());
                    itensUtilizados[indice++] = new Pattern(item, tipoAvaliacao);
                }
                Arrays.sort(itensUtilizados);
                System.out.println("\n");
                System.out.println("OBS: Características utilizadas nos grupos:");
                for (Pattern itensUtilizado : itensUtilizados) {
                    System.out.println(itensUtilizado.toString(metricas, false, false, false));
                }
            }
        }       
    }
        
    public static void main(String[] args) throws FileNotFoundException{
                 
        //Base de dados
        String caminho = "C:\\Users\\jc160\\IdeaProjects\\subgroupAlgorithms\\pastas\\bases\\";
        String nomeBase = "matrixBinaria-Global-100-p.csv";
        String caminhoBase = caminho + nomeBase;
        String separadorBase = ",";  
        int tipoBase = D.TIPO_CSV;
        
        //Perfil descrição
        String tipoAvaliacao = Avaliador.METRICA_AVALIACAO_WRACC;
        
        String[] metricas = {
            Const.METRICA_QUALIDADE,
            Const.METRICA_SIZE,
            Const.METRICA_WRACC,
            Const.METRICA_Qg,
            Const.METRICA_DIFF_SUP,
            Const.METRICA_LIFT,
            Const.METRICA_CONF,
            Const.METRICA_COV,
            Const.METRICA_CHI_QUAD,
            Const.METRICA_P_VALUE,
            Const.METRICA_SUPP_POSITIVO,
            Const.METRICA_SUPP_NEGATIVO                      
        };

        String[] rotulos = {"Y"};
        int ANDouORentreItens = Const.PATTERN_AND;
        
        
        //Relatório Caracterísitcas
        boolean relatorioItens = true;
        int kItens = 10;
        
        //Relatório Grupos
        boolean relatorioGrupos = true;
        int k = 10;
        long seed = Const.SEEDS[0];
        String[] filtrarValores = {""};

               
        RelatorioDiscriminante.TXT(caminhoBase, separadorBase, tipoBase, 
                tipoAvaliacao, metricas, rotulos, ANDouORentreItens, 
                relatorioItens, kItens, 
                relatorioGrupos, k, seed, null, filtrarValores, null);
    }       
}
