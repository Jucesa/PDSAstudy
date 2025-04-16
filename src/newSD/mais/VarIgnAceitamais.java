package newSD.mais;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.Threshold;

import java.util.HashSet;

public class VarIgnAceitamais extends Threshold {
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
        int particao = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            //System.out.println("Reinicializações: "+numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoP, Pk, Paux);
                particao = 9*P.length/10;
            }

            while (numeroGeracoesSemMelhoraPk < tamanhoP && particao > 1) {
                int novosK = 0;


                int index = SELECAO.torneioN(P, quantidadeTorneio);

                Pattern pai1 = P[index];

                for (int i = 0; i < tentativasMelhoria; i++) {

                    index = SELECAO.torneioN(P, quantidadeTorneio);
                    Pattern pai2 = P[index];

                    Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                    if (substituirIndividuo(P, paux, particao)) {

                        novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                        particao--;
                        break;
                    }
                    if (Pattern.numeroIndividuosGerados % P.length == 0) {
                        quantidadeTorneio++;
                        //avaliarPopulacao(P, quantidadeTorneio, particao, Pattern.numeroIndividuosGerados);
                    }
                }

                if (novosK == 0) {
                    numeroGeracoesSemMelhoraPk++;
                } else {
                    //System.out.println("NovosK:"+novosK);
                    numeroGeracoesSemMelhoraPk = 0;
                }

            }
        }

        return Pk;
    }
}
