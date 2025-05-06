package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class VarSortAceitamais extends Threshold {
    public static Pattern[] run(int tentativasMelhoria, double similaridade, String tipoAvaliacao, int k) {

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
        int threshold = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            //System.out.println("Reinicializações: "+numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            if (numeroReinicializacoes > 0) {
                P =  aleatorioD1_Pk(tipoAvaliacao, tamanhoP, Pk, Paux);
                threshold = 9*P.length/10;
            }

            while (numeroGeracoesSemMelhoraPk < tamanhoP && threshold > 1) {
                int novosK = 0;


                Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, threshold-1)];

                for (int i = 0; i < tentativasMelhoria; i++) {

                    Pattern pai2 = sortear(P, quantidadeTorneio, threshold);

                    Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                    if (substituirIndividuo(P, paux, threshold)) {


                        threshold--;
                        break;
                    }
                    if (Pattern.numeroIndividuosGerados % P.length == 0) {
                        quantidadeTorneio++;
                        //avaliarPopulacao(P, quantidadeTorneio, threshold, Pattern.numeroIndividuosGerados);
                    }
                }
                novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                if (novosK == 0) {
                    numeroGeracoesSemMelhoraPk++;
                } else {
                    //System.out.println("NovosK:"+novosK);
                    numeroGeracoesSemMelhoraPk = 0;
                }

            }
        }
        System.out.println("Gerou: "+Pattern.numeroIndividuosGerados);
        return Pk;
    }

}

