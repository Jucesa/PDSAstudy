package sd;

import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class TPSD {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, String tipoAvaliacao){
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        int pioras = 0;
        while(pioras < 3){
            //1. seleciona o melhor index entre quantidadeTorneio indexes
            Pattern p = P[SELECAO.torneioN(P, quantidadeTorneio)];
            //2. tenta melhorar
            for(int i = 0; i < tentativasMelhoria; i++){
                HashSet<Integer> itemNovo = new HashSet<>(p.getItens()); //adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // procura um indice novo pelo torneio pra ser adicionado

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);


            }
        }
        return P;
    }
    public static void main(String[] args){

    }
}
