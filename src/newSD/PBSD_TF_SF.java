package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class PBSD_TF_SF extends Threshold {
    public static Pattern[] run(int quantidadeTorneio, double similaridade, String tipoAvaliacao, int k) {

        Pattern[] Pk = new Pattern[k]; //array de k melhores indivíduos
        Pattern[] P; // array da população manipulada pelo algoritmo
        Pattern[] I; // array que salva os Pattern de 1D ordenados
        int numeroGeracoesSemMelhoraPk;

        //inicializa Pk com Pattern vazio
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        //inicializa e ordena I e salva os pattern 1D em Pk de acordo com similaridade
        I = INICIALIZAR.D1(tipoAvaliacao);
        ordenaP(I);
        SELECAO.salvandoRelevantesDPmais(Pk, I, similaridade);

        //atribuições iniciais
        P = I;
        int tamanhoP = P.length;
        int threshold = P.length;


        for (int numeroReinicializacoes = 0; numeroReinicializacoes < maxReinicializacoes; numeroReinicializacoes++) {
//            System.out.println("Reinicializações: " + numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            //reinicializa P para ter 90% pattern 1D e 10% pattern parecidos com Pk,
            // threshold settado para começar em 90% de P, ou seja, exatamente como o comportamento
            // original do algoritmo de separar pattern 1D de nD
            if (numeroReinicializacoes > 0) {
                P = aleatorioD1_Pk(tipoAvaliacao, tamanhoP, Pk, I);
                threshold = 9*P.length/10;
            }

            //tenta melhorar P.length individuos até reinicializar
            while (numeroGeracoesSemMelhoraPk < maxGeracoesSemMelhoraPk && threshold > 1) {
                int novosK;

                Pattern pai1 = P[SELECAO.torneioN(P, quantidadeTorneio)];
                Pattern pai2 = P[SELECAO.torneioN(P, quantidadeTorneio)];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if (substituirIndividuo(P, paux, threshold)) {
                    threshold--;
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                   // System.out.println("Gerou: " + Pattern.numeroIndividuosGerados);
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
