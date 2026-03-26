package newSD.algorithm.fixo;

import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.algorithm.JSD;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class PBSD_FIXO extends JSD {

    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] P;
        Pattern[] Pk = new Pattern[k];

        // Inicializa Pk
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // População inicial
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I); // Ordena do Melhor para o Pior

        // Limita o tamanho da população se a base for gigantesca (Otimização de Memória/Tempo)
        // Se tiver 50.000 atributos, usar todos é inviável. Pegamos os Top 5000 + alguns aleatórios?
        // Por enquanto, mantemos a lógica original
        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < I.length) P[i] = I[i];
                else P[i] = I[Const.random.nextInt(I.length - 1)];
            }
        } else {
            P = I;
        }

        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        int numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;


        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                // Reinicia limiar de forma mais agressiva para economizar tempo
                limiar = Math.max(1, (int) (P.length * 0.9));
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);

            int limiteEstagnacao = 3;

            while (numeroGeracoesSemMelhoraPk < limiteEstagnacao && limiar > 0) {

                double r = Const.random.nextDouble();

                double Pth = (double) limiar /P.length;

                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2;

                if (r < Pth) {
                    // Exploração: Trivial x Trivial
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                } else {
                    // Explotação: Trivial x Raro/Subgrupo
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
                }

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                double qualidadeMelhorPai = Math.max(pai1.getQualidade(), pai2.getQualidade());
                boolean ganhoSignificativo = paux.getQualidade() > (qualidadeMelhorPai * (1));

                if (ganhoSignificativo && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        // Reduz 1 geração inteira de estagnação como recompensa, travando no zero
                    }
                }


                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    Arrays.sort(P, limiar, P.length);

                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk,
                            Arrays.copyOfRange(P, limiar, P.length),
                            similaridade);

                    if (novosK == 0) numeroGeracoesSemMelhoraPk += 1;
                    else numeroGeracoesSemMelhoraPk = 0;

                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
                }
            }

            // Salvamento final da rodada
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }

        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }
}
