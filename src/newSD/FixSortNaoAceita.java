package newSD;

import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;

public class FixSortNaoAceita extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));
        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > quantidadeTorneio) {
            int index = SELECAO.torneioN(P, quantidadeTorneio, 0, particao);
            Pattern individuo = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux = melhorarIndividuo(individuo, P, quantidadeTorneio, particao);
                gerou++;
                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
                //checa se individuos suficientes foram gerados para avaliar P
                if(gerou % P.length == 0){
                    System.out.println("Partição: "+ particao);
                    avaliarPopulacao(P);
                }
            }
        }

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        double overallConfidence = calculateOverallConfidence(P, k);
        if (overallConfidence < 0.8) {
            System.out.println("Warning: Overall confidence in top-k is below threshold! " + overallConfidence);
        }
        System.out.println("Overall Confidence: " + overallConfidence);
        System.out.println("Threshold: " +particao);
        System.out.println("Population Size: " + P.length);
        System.out.println("Gerou: " + gerou);
        return P;
    }
}
