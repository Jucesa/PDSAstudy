package newSD;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.HashSet;

public class PBSD_Fibonacci_SF extends Threshold {
    public static Pattern[] run(double similaridade, String tipoAvaliacao, int k) {

        int fib0 = 0;
        int fib1 = 1;

        Pattern[] Pk = new Pattern[k];
        Pattern[] P;

        int quantidadeTorneio = 1;
        int numeroGeracoesSemMelhoraPk;

        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        Pattern[] I = P = INICIALIZAR.D1(tipoAvaliacao);

        ordenaP(P);

        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

        int tamanhoP = P.length;
        int threshold = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
//            System.out.println("Reinicializações: "+numeroReinicializacoes);
            numeroGeracoesSemMelhoraPk = 0;

            if (numeroReinicializacoes > 0) {
                P = aleatorioD1_Pk(tipoAvaliacao, tamanhoP, Pk, I);
                threshold = 9*P.length/10;
            }

//            System.out.println("\n------------Pk------------");
//            Avaliador.imprimirRegras(Pk, k);
//            System.out.println("--------------------------\n");

            while (numeroGeracoesSemMelhoraPk < maxGeracoesSemMelhoraPk && threshold > 1) {
                int novosK;

                int index = SELECAO.torneioN(P, quantidadeTorneio);

                Pattern pai1 = P[index];

                index = SELECAO.torneioN(P, quantidadeTorneio);
                Pattern pai2 = P[index];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

                if (substituirIndividuo(P, paux, threshold)) {
                    threshold--;
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0) {

                    if(quantidadeTorneio >= maxTorneio) {
                        quantidadeTorneio = maxTorneio;
                    } else {
                        int temp = fib1;
                        fib1 = fib0 + fib1;
                        fib0 = temp;
                        quantidadeTorneio = fib1;
                    }

//                    System.out.println("TorneioFib:"+quantidadeTorneio);
                    novosK = SELECAO.salvandoRelevantesDPmais(Pk, modifiedSGs(P, threshold), similaridade);
                    if (novosK == 0) {
                        numeroGeracoesSemMelhoraPk++;
                    } else {
//                        System.out.println("NovosK:"+novosK);
                        numeroGeracoesSemMelhoraPk = 0;
                    }
                    //avaliarPopulacao(P, quantidadeTorneio, threshold, Pattern.numeroIndividuosGerados);
                }
            }
        }

        return Pk;
    }
}
