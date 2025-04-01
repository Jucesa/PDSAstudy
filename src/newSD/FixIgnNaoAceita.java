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

                if(filhoPiorQuePais(pai1, pai2, paux)) break;

                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
                //checa se individuos suficientes foram gerados para avaliar P
//                if(gerou % P.length == 0){
//                    //System.out.println("Partição: "+ particao);
//                    avaliarPopulacao(P);
//                }
            }
        }

        ordenaP(P);

//        double overallConfidence = calculateOverallConfidence(P, k);
//        if (overallConfidence < 0.8) {
//            System.out.println("Warning: Overall confidence in top-k is below threshold! " + overallConfidence);
//        }
//        System.out.println("Overall Confidence: " + overallConfidence);
//        System.out.println("Threshold: " +particao);
//        System.out.println("Population Size: " + P.length);
//        System.out.println("Gerou: " + gerou);
        return P;
    }
}
