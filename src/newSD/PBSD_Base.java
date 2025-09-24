package newSD;

import dp.Avaliador;
import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.util.Arrays;
import java.util.HashSet;

public abstract class PBSD_Base extends Threshold {

    /**
     * Mét0d0 abstrato para determinar o tamanho do torneio na geração atual.
     * Cada implementação (FIXO ou VARIÁVEL) define sua lógica.
     */
    protected abstract int calcularTamanhoTorneio(int geracoes, int tamanhoPopulacao, int saltoTorneio);

    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k, boolean benchmark) {
        ExecLogger logger = benchmark ? null : new ExecLogger(this.getClass().getSimpleName() + "_" + paramTorneio);

        try {
            Pattern[] Pk = new Pattern[k];
            Pattern[] P;

            // Inicializa Pk com indivíduos vazios
            for (int i = 0; i < Pk.length; i++) {
                Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
            }

            // População inicial
            Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
            Arrays.sort(I);

            if (I.length < k) {
                P = new Pattern[k];
                for (int i = 0; i < k; i++) {
                    if (i < I.length) P[i] = I[i];
                    else P[i] = I[Const.random.nextInt(I.length - 1)];
                }
            } else {
                P = I;
            }

            SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

            int limiar = P.length;
            int tamanhoPopulacao = P.length;
            int numeroGeracoesSemMelhoraPk = 0;

            for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
                if (!benchmark) logger.registrarInicializacao(numeroReinicializacoes + 1);

                if (numeroReinicializacoes > 0) {
                    P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                    limiar = Math.max(1, (int) (P.length * 0.9));
                }

                int geracoes = 0;
                int tamanhoTorneio = calcularTamanhoTorneio(geracoes, P.length, paramTorneio);

                while (numeroGeracoesSemMelhoraPk < 3) {
                    Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                    Pattern pai2 = sortear(P, tamanhoTorneio, limiar);

                    Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                    if (paux.getQualidade() >= P[limiar - 1].getQualidade() && limiar > 1) {
                        limiar--;
                        P[limiar] = paux;
                    }

                    geracoes++;

                    if (Pattern.numeroIndividuosGerados % P.length == 0) {
                        int novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                        if (novosK == 0) numeroGeracoesSemMelhoraPk++;
                        else numeroGeracoesSemMelhoraPk = 0;

                        // Atualiza tamanho do torneio usando mét0do polimórfico
                        tamanhoTorneio = calcularTamanhoTorneio(geracoes, P.length, paramTorneio);

                        if (!benchmark) {
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
            if (!benchmark && logger != null) logger.close();
        }
    }
}
