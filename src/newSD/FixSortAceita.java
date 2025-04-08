package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

public class FixSortAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > 1) {
            Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao-1)];

            for (int i = 0; i < tentativasMelhoria; i++) {

                Pattern pai2 = sortear(P, quantidadeTorneio, particao);

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
                gerou++;

                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
//                if(gerou == gerou % P.length){
//                    avaliarPopulacao(P);
//                }
            }
        }

        return topK(P, k);
    }
}
