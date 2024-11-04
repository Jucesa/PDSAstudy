/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulacoes;

import dp.Pattern;
import java.io.Serializable;

/**
 *
 * @author tarcisio_pontes
 */
public class Simulacao implements Serializable{
    private String algoritmo;    
    private String nomeBase;
    private Resultado[] resultados;

    public Simulacao(String algoritmo, String nomeBase, Resultado[] resultados) {
        this.algoritmo = algoritmo;
        this.nomeBase = nomeBase;
        this.resultados = resultados;
    }
    
    public String getAlgoritmo() {
        return algoritmo;
    }

    public String getNomeBase() {
        return nomeBase;
    }
    
    public Resultado[] getResultados(){
        return this.resultados;
    }
    
}
