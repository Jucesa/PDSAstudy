package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

public class VarIgnAceita extends Threshold {
    public static Pattern[] run(int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        //var
        int quantidadeTorneio = 1;

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);
        ordenaP(P);

        int particao = P.length;

        while (Pattern.numeroIndividuosGerados < maxIndividuosGerados && particao > 0) {
            int index = SELECAO.torneioN(P, quantidadeTorneio);

            Pattern pai1 = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux;

                index = SELECAO.torneioN(P, quantidadeTorneio);
                Pattern pai2 = P[index];

                paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if(Pattern.numeroIndividuosGerados % P.length == 0){
                    quantidadeTorneio++;
                    avaliarPopulacao(P, quantidadeTorneio, particao, Pattern.numeroIndividuosGerados);

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
