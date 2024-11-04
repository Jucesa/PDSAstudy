/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulacoes;

import dp.D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 *
 * @author tarcisio_pontes
 */
public class BasesArrayList {
    private Base[] bases;
    
    public BasesArrayList(String caminho, String separador) throws FileNotFoundException{
        D.SEPARADOR = separador;
        this.carragarBases(caminho, separador);
    }
    
    private void carragarBases(String caminho, String separador) throws FileNotFoundException{        
        D.SEPARADOR = separador;
        File diretorio = new File(caminho);
        File arquivos[] = diretorio.listFiles();
        this.bases = new Base[arquivos.length];
        for(int i = 0; i < bases.length; i++){
            this.bases[i] = new Base(arquivos[i].getAbsolutePath(), D.TIPO_CSV);
        }   
    }
    
    
    public Base getBase(String nome){        
        for(int i = 0; i < this.bases.length; i++){
            Base b = this.bases[i];
            if(b.getNome().equals(nome))
                return b;
        }
        return null;
    }
    
    public String[] getNomeBases(){        
        String[] nomeBases = new String[this.bases.length];
        for(int i = 0; i < this.bases.length; i++){
            nomeBases[i] = this.bases[i].getNome();
        }        
        return nomeBases;
    }
}
