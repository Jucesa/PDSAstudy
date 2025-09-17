package newSD;

import dp.Avaliador;
import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;
import java.util.HashSet;

public class PBSD_VAR extends Threshold {
    /**
     * Execução do algoritmo PBSD com torneio variável.
     * O tamanho do torneio aumenta em "saltoTorneio" a cada P.length indivíduos gerados.
     *
     * @param saltoTorneio int - incremento do torneio a cada ciclo da população
     * @param similaridade double - limite de similaridade para filtragem de padrões relevantes
     * @param tipoAvaliacao String - tipo de avaliação do indivíduo
     * @param k int - número de melhores padrões a serem retornados
     * @param benchmark boolean - se true, não registra logs
     * @return Pattern[] - top-k padrões encontrados
     */
    public static Pattern[] run(int saltoTorneio, double similaridade, String tipoAvaliacao, int k, boolean benchmark) {
        ExecLogger logger = benchmark ? null : new ExecLogger("PBSD_VAR_" + saltoTorneio);

        try {
            Pattern[] Pk = new Pattern[k];
            Pattern[] P;

            // Inicializa Pk com indivíduos vazios
            for (int i = 0; i < Pk.length; i++) {
                Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
            }

            // População inicial de itens unidimensionais
            Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
            if (I.length < k) {
                P = new Pattern[k];
                for (int i = 0; i < k; i++) {
                    if (i < I.length) {
                        P[i] = I[i];
                    } else {
                        P[i] = I[Const.random.nextInt(I.length - 1)];
                    }
                }
            } else {
                P = I;
            }
            Arrays.sort(P);
            SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

            int limiar = P.length;
            int tamanhoPopulacao = P.length;
            int numeroGeracoesSemMelhoraPk = 0;

            // tamanho do torneio começa em 2 por padrão (mínimo)
            int tamanhoTorneio = 2;

            for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
                if (!benchmark) logger.registrarInicializacao(numeroReinicializacoes + 1);

                if (numeroReinicializacoes > 0) {
                    P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                    limiar = Math.max(1, (int) (P.length * 0.9));
                }

                while (numeroGeracoesSemMelhoraPk < 20) {
                    Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                    Pattern pai2 = sortear(P, tamanhoTorneio, limiar);

                    Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                    if (paux.getQualidade() >= P[limiar - 1].getQualidade() && limiar > 1) {
                        limiar--;
                        P[limiar] = paux;
                    }

                    // a cada ciclo de |P| indivíduos, atualiza Pk e ajusta torneio
                    if (Pattern.numeroIndividuosGerados % P.length == 0) {
                        int novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                        if (novosK == 0) {
                            numeroGeracoesSemMelhoraPk++;
                        } else {
                            numeroGeracoesSemMelhoraPk = 0;
                        }

                        // aumenta o tamanho do torneio
                        tamanhoTorneio = Math.min(tamanhoTorneio + saltoTorneio, P.length);

                        if (!benchmark) {
                            // registrar progresso usando os utilitários
                            logger.registrarProgresso(
                                    Pattern.numeroIndividuosGerados,
                                    tipoAvaliacao,
                                    EstatisticasPopulacao.mediaQualidade(P),
                                    Avaliador.avaliarMediaDimensoes(P, P.length),
                                    EstatisticasPopulacao.overallSuppPositive(P),
                                    EstatisticasPopulacao.contarDistintos(P),
                                    EstatisticasPopulacao.calcularFrequenciaItens(Pk),
                                    limiar
                            );
                        }
                    }
                }
            }

            return Pk;
        } finally {
            if (!benchmark && logger != null) {
                logger.close();
            }
        }
    }
}
