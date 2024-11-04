/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;

/**
 *
 * @author Marianna
 */
public class Estatistica {
    public static double getMedia(double[] numeros){
        double total = 0;
        int i;
        for(i = 0; i < numeros.length; i++){
            total = total + numeros[i];
        }
        return total/i;
    }
    
    
    
    public static double getMediana(double[] numeros){
        int n = numeros.length;
        //System.out.println(n);
        Arrays.sort(numeros);
                
        if(n % 2 == 0){//Se quantidade de número for par
            return (numeros[n/2] + numeros[(n/2)-1]) / 2.0; 
        }else{
            return numeros[n/2];
        }        
    }
    
    public static double getMaior(double[] numeros){
        double maior = numeros[0];
        for(int i = 1; i < numeros.length; i++){
            if(maior < numeros[i]){
                maior = numeros[i];
            }
        }
        return maior;
    }
    
    
    public static double getMenor(double[] numeros){
        double menor = numeros[0];
        for(int i = 1; i < numeros.length; i++){
            if(menor > numeros[i]){
                menor = numeros[i];
            }
        }
        return menor;
    }
    
    
    
}
