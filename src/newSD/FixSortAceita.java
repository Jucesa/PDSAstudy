package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;

public class FixSortAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > 0 && particao > quantidadeTorneio) {

            Pattern pai1 = sortear(P, quantidadeTorneio, particao);

            for (int i = 0; i < tentativasMelhoria; i++) {

                Pattern pai2 = sortear(P, quantidadeTorneio, particao);

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
