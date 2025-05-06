package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class PBSD_TV_SF extends Threshold {
    public static Pattern[] run(int passo, double similaridade, String tipoAvaliacao, int k) {

        Pattern[] Pk = new Pattern[k];
        Pattern[] P = null;

        int quantidadeTorneio = 1;
        int numeroGeracoesSemMelhoraPk = 0;

        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        Pattern[] Paux = P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

        int tamanhoP = P.length;
        int particao = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            System.out.println("Reinicializações: "+numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorio1_D_Pk(tipoAvaliacao, tamanhoP, Pk);
                particao = 9*P.length/10;
            }

            while (numeroGeracoesSemMelhoraPk < 3 && particao > 1) {
                int novosK = 0;


                int index = SELECAO.torneioN(P, quantidadeTorneio);

                Pattern pai1 = P[index];

                index = SELECAO.torneioN(P, quantidadeTorneio);
                Pattern pai2 = P[index];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    //break;
                }
                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    quantidadeTorneio += passo;

                    novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                    if (novosK == 0) {
                        numeroGeracoesSemMelhoraPk++;
                    } else {
                        System.out.println("NovosK:"+novosK);
                        numeroGeracoesSemMelhoraPk = 0;
                    }
                    //avaliarPopulacao(P, quantidadeTorneio, particao, Pattern.numeroIndividuosGerados);
                }

            }
        }

        return Pk;
    }
}
