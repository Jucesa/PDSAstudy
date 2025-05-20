package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class PBSD_TV_SF extends Threshold {

    public static Pattern[] run(int passo, double similaridade, String tipoAvaliacao, int k) {

        Pattern[] Pk = new Pattern[k];
        Pattern[] P;

        int quantidadeTorneio = 1;
        int numeroGeracoesSemMelhoraPk;

        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(I);
        P = I;

        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

        int tamanhoP = P.length;
        int threshold = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < maxReinicializacoes; numeroReinicializacoes++) {
//            System.out.println("Reinicializações: "+numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            if (numeroReinicializacoes > 0) {
                P = aleatorioD1_Pk(tipoAvaliacao, tamanhoP, Pk, I);
                threshold = 9*P.length/10;
            }

            while (numeroGeracoesSemMelhoraPk < maxGeracoesSemMelhoraPk && threshold > 1) {
                int novosK;

                Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio)];
                Pattern pai2 = P[SELECAO.torneioN(P, quantidadeTorneio)];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if (substituirIndividuo(P, paux, threshold)) {
                    threshold--;
                }
                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    //System.out.println("Gerou: " + Pattern.numeroIndividuosGerados);
                    quantidadeTorneio += passo;

                    novosK = SELECAO.salvandoRelevantesDPmais(Pk, modifiedSGs(P, threshold), similaridade);
                    if (novosK == 0) {
                        numeroGeracoesSemMelhoraPk++;
                    } else {
//                        System.out.println("NovosK:"+novosK);
                        numeroGeracoesSemMelhoraPk = 0;
                    }
//                    avaliarPopulacao(P, quantidadeTorneio, threshold, Pattern.numeroIndividuosGerados);
                }

            }
        }
        return Pk;
    }
}
