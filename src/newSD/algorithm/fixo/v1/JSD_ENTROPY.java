package newSD.algorithm.fixo.v1;

import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.algorithm.JSD;
import java.io.IOException;
import java.util.*;
import java.util.Arrays;

public class JSD_ENTROPY extends JSD {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }
    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] P;
        Pattern[] Pk = new Pattern[k];
        for (int i = 0; i < Pk.length; i++) Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);

        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I);

        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) P[i] = (i < I.length) ? I[i] : I[Const.random.nextInt(I.length - 1)];
        } else {
            P = I;
        }

        int ultimaAval = P.length;
        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        // MELHORIA ISOLADA: Entropia
        double entropiaMinima = 0.4;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));


                ultimaAval = limiar;
            }
            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);

            boolean diversidadeSuficiente = true; // Controle da Entropia
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 3 && limiar > 0) {
                double r = Const.random.nextDouble();
                double Pth = (double) limiar / P.length; // Pth Linear (Padrão)

                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2 = (r < Pth) ? P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)]
                        : P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                if (paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        // Reduz 1 geração inteira de estagnação como recompensa, travando no zero
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0 && limiar < tamanhoPopulacao) {
                    Arrays.sort(P, limiar, ultimaAval);


                    // CHECAGEM DE ENTROPIA
                    if (calcularEntropiaPopulacao(P, limiar) <= entropiaMinima) {
                        diversidadeSuficiente = false; // Força parada e reinicialização
                    }

                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, ultimaAval), similaridade);
                    if (novosK == 0) {
                        numeroGeracoesSemMelhoraPk++;
                    } else {
                        numeroGeracoesSemMelhoraPk = 0;
                    }

                    ultimaAval = limiar;
                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
                }
            }
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }
        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }
}
