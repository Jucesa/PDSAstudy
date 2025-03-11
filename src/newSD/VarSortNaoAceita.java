package newSD;

import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;

public class VarSortNaoAceita extends Threshold {

    public static Pattern[] run(int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {

        int quantidadeTorneio = 1;

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados) {

            int index = SELECAO.torneioN(P, quantidadeTorneio);

            Pattern individuo = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux = melhorarIndividuo2(individuo, P, quantidadeTorneio, particao);
                gerou++;
                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
                if(gerou % P.length == 0){
                    quantidadeTorneio++;
                    System.out.println("Torneio de: "+quantidadeTorneio);
                    System.out.println("Partição: "+ particao);
                    //avaliarPopulacao(P);
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

    private static Pattern melhorarIndividuo2(Pattern pai1, Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern novoIndividuo;
        Pattern pai2;

        double aDouble = Const.random.nextDouble(0, 1);

        if (aDouble < (float) particao / P.length) {
            if(particao > quantidadeTorneio){
                pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao - 1)];
            } else {
                pai2 = P[SELECAO.torneioN(P, particao-1, 0, particao - 1)];
            }
        } else {
            pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)];
        }

        novoIndividuo = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

        return novoIndividuo;
    }
}
