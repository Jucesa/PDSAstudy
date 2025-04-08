package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

public class VarSortNaoAceita extends Threshold {

    public static Pattern[] run(int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {

        int quantidadeTorneio = 1;

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);
        ordenaP(P);

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > 1) {

            Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao-1)];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux;

                Pattern pai2 = sortear(P, quantidadeTorneio, particao);

                paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
                gerou++;

                if(filhoPiorQuePais(pai1, pai2, paux)) break;

                if(gerou % P.length == 0){
                    quantidadeTorneio++;
                    //avaliarPopulacao(P);
                }
                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
            }
        }

        return topK(P, k);
    }


}
