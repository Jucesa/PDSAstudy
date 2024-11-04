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
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Tarcisio Lucas
 */
public class JARrelatorioDiscriminante {
        public static void main(String args[]) throws IOException, FileNotFoundException{
        //Passar: o nome do arquivo, valor de k e o valor de atributo que quero desprezar        
        //args[0]: caminho da base 
        //args[1]: nome da base 
        //args[2]: k
        //args[3]: tipo de avaliação
        //args[4]: valor a filtrar
        //args[5]: rótulos
        //args[6]: AND ou OR
        
        Pattern.medidaSimilaridade = Const.SIMILARIDADE_JACCARD;
               
        System.out.println("Caminho base...");
        //Base de dados
        String caminho = args[0];
        String nomeBase = args[1];  
        String caminhoBase = caminho + nomeBase;
        String separadorBase = ",";  
        int tipoBase = D.TIPO_CSV;
          
        System.out.println("k...");
        //k
        int k = Integer.parseInt(args[2]);
        int kItens = k;
        
        //Métrica de avaliação
        System.out.println("Métrica de avaliação...");
        String tipoAvaliacao = args[3];
        
        //Relatório Grupos
        System.out.println("Filtro...");
        boolean relatorioGrupos = true;
        long seed = Const.SEEDS[0];
        String[] filtrarAtributos = null;       
        String[] filtrarValores = {args[4]};       
        String[][] filtrarAtributosValores = null;
                        
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
        
        System.out.println("Rótulo...");
        String[] rotulos = null;
        if(args[5].contains(",")){
            rotulos = args[5].split(",");            
        }else{
            rotulos = new String[1];
            rotulos[0] = args[5];
        }
        //String[] rotulos = {"p"};
        //String[] rotulos = {"6","80","104","116","134","145","151","153","156","256"};
                
        //Relatório Caracterísitcas
        boolean relatorioItens = true;        
        
        System.out.println("ANDouOR...");
        int ANDouOR = Integer.parseInt( args[6] );
        
        System.out.println("Gerando relatório...");
        RelatorioDiscriminante.TXT(caminhoBase, separadorBase, tipoBase, 
                tipoAvaliacao, metricas, rotulos, ANDouOR, 
                relatorioItens, kItens, 
                relatorioGrupos, k, seed, filtrarAtributos, filtrarValores, filtrarAtributosValores);
     
    }
}
