/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulacoes;

import dp.Const;
import dp.D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author tarcisio_pontes
 */
public class Arquivo {
    
    public void replaceNomeArquivo(String caminhoArquivos, String textoAntigo, String textoNovo){
        File diretorio = new File(caminhoArquivos);
        File arquivos[] = diretorio.listFiles();
        for(int i = 0; i < arquivos.length; i++){
            String caminhoCompletoAntigo = arquivos[i].getPath();
            String nomeArquivoAntigo = caminhoCompletoAntigo.split("/")[0];
            String nomeArquivoNovo = nomeArquivoAntigo.replaceAll(textoAntigo, textoNovo);
            String caminhoCompletoNovo = caminhoCompletoAntigo.replace(nomeArquivoAntigo, nomeArquivoNovo);           
            
            File file1 = new File(caminhoCompletoAntigo);
            File file2 = new File(caminhoCompletoNovo);
            if(file1.renameTo(file2)){
                System.out.println("Ok");
            }
        }
    }
    
    public void replaceConteudoArquivo(String[] textoAntigo, String[] textoNovo) throws IOException{
        
        File diretorio = new File(Const.CAMINHO_BASES);
        File arquivos[] = diretorio.listFiles();
        for(int i = 0; i < arquivos.length; i++){
            //Abrindo arquivo para leitura
            System.out.println("[" + (i+1) + "/" + arquivos.length + "]:" + arquivos[i].getName());
            String caminhoArquivo = arquivos[i].getPath();
            BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo));
            //Criando novo arquivo
            String caminhoNovoArquivo = caminhoArquivo.replace("bases", "bases_editadas");
            File novoArquivo = new File(caminhoNovoArquivo);
            novoArquivo.createNewFile();
            FileWriter fw = new FileWriter(novoArquivo);
            BufferedWriter bw = new BufferedWriter(fw);

            //Criando conteúdo de novo arquivo a partir do primeiro
            String linha = "";
            while ((linha = br.readLine()) != null) {
                String novaLinha = linha;
                for(int j = 0; j < textoAntigo.length; j++){
                    novaLinha = novaLinha.replace(textoAntigo[j], textoNovo[j]);                                        
                }
                bw.write(novaLinha);
                bw.newLine();
            }          
            bw.close();
            br.close();
        }    
    }
    
    public static void main(String[] args) throws IOException{
        String caminhoArquivos = Const.CAMINHO_BASES;
        
        //Arquivo arq = new Arquivo();
        //arq.replaceNomeArquivo(caminhoArquivos, "_", "-");
    

   
        
        Arquivo arq = new Arquivo();
        //String[] antigo = {",", ";","*@$"," "};
        //String[] novo = {"*@$", ",",";",""};        
        //String[] antigo = {"\"\""};
        //String[] novo = {"NA"};        
        String[] antigo = {"\"p\"", "\"n\"", "\"!@#\""};
        String[] novo = {"\"!@#\"", "\"p\"", "\"n\""};        
                
        arq.replaceConteudoArquivo(antigo, novo);
     }
}
