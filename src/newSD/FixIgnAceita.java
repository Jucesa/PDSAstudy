package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

public class FixIgnAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > quantidadeTorneio) {
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern pai1 = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                index = SELECAO.torneioN(P, quantidadeTorneio);
                Pattern pai2 = P[index];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
                gerou++;

                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
            }
        }

        return topK(P, k);
    }
}
