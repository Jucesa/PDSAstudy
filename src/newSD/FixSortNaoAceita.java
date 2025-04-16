package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;

public class FixSortNaoAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        int particao = P.length;

        while (Pattern.numeroIndividuosGerados < maxIndividuosGerados && particao > 1) {

            Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao-1)];

            for (int i = 0; i < tentativasMelhoria; i++) {

                Pattern pai2 = sortear(P, quantidadeTorneio, particao);

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if(filhoPiorQuePais(pai1, pai2, paux)){
                    i = tentativasMelhoria;
                } else{
                    if (substituirIndividuo(P, paux, particao)) {
                        particao--;
                        break;
                    }
                    if(Pattern.numeroIndividuosGerados % P.length == 0){
                        avaliarPopulacao(P, quantidadeTorneio, particao, Pattern.numeroIndividuosGerados);
                    }
                }

            }

        }

        return topK(P, k);
    }
}
