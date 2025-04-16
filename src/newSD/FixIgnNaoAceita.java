package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;

public class FixIgnNaoAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        int particao = P.length;

        while (Pattern.numeroIndividuosGerados < maxIndividuosGerados && particao > quantidadeTorneio) {
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern pai1 = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                index = SELECAO.torneioN(P, quantidadeTorneio);
                Pattern pai2 = P[index];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if (filhoPiorQuePais(pai1, pai2, paux)) {
                    i = tentativasMelhoria;
                } else {
                    if (substituirIndividuo(P, paux, particao)) {
                        particao--;
                        break;
                    }
                    if (Pattern.numeroIndividuosGerados % P.length == 0) {
                        avaliarPopulacao(P, quantidadeTorneio, particao, Pattern.numeroIndividuosGerados);
                    }

                }
            }

        }
        return topK(P, k);
    }
}