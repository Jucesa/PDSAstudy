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
public class Resultado implements Serializable{
    private Pattern[] DPs;
    private double tempoExecucao;
    private int numeroTestes;
    private long seed;

    public Resultado(Pattern[] DPs) {
        this.DPs = DPs;
    }

    public Resultado(Pattern[] DPs, double tempoExecucao, int numeroTestes, long seed) {
        this.DPs = DPs;
        this.tempoExecucao = tempoExecucao;
        this.numeroTestes = numeroTestes;
        this.seed = seed;
    }
    
    public long getSeed(){
        return this.seed;
    }

    public Pattern[] getDPs() {
        return DPs;
    }

    public double getTempoExecucao() {
        return tempoExecucao;
    }

    public int getNumeroTestes() {
        return numeroTestes;
    }   
}
