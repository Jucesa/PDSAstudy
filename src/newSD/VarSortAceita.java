package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;

public class VarSortAceita extends Threshold{
    public static Pattern[] run(int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {

        int quantidadeTorneio = 1;

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);
        ordenaP(P);

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > 0) {

            Pattern pai1 = sortear(P, quantidadeTorneio, particao);

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux;

                Pattern pai2 = sortear(P, quantidadeTorneio, particao);

                paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
                gerou++;

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
