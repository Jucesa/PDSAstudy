package SDp.algorithm.fixo.solo;

import SDp.algorithm.SDp;
import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

import static SDp.algorithm.SelectionManager.selecionarPaisD1DnLinear;

public class SDp_INC extends SDp {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio) {
        return tamanhoTorneio;
    }
    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        // 1. Inicialização Modular
        Pattern[] P = setupInitialState(k, Pk, I, tipoAvaliacao);

        int tamanhoPopulacao = P.length;
        int indexUltimaAval = tamanhoPopulacao;
        int limiar = tamanhoPopulacao;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        // Intervalo de manutenção fixo no tamanho da população para este algoritmo
        int intervaloManutencao = tamanhoPopulacao;
        final double MIN_GANHO_RELATIVO = 0.0001;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
                // 2. Reinicialização Modular
                ReinicializacaoResult result = realizarReinicializacao(
                        tipoAvaliacao, tamanhoPopulacao, I, Pk, paramTorneio
                );
                P = result.P();
                tamanhoTorneio = result.tamanhoTorneio();
                limiar = result.limiar();
                indexUltimaAval = result.indexUltimaAval();
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);
            boolean diversidadeSuficiente = true;

            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 3 && limiar > 0) {
                // 3. Seleção Linear (Podemos usar o mét0do da base ou manter local se for exclusivo)
                Pattern[] pais = selecionarPaisD1DnLinear(P, tamanhoTorneio, limiar);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                Pattern paux = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 4. Verificação de Ganho Modular
                if (pTemGanho(paux, qualidadeMelhorPai, MIN_GANHO_RELATIVO) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                // 5. Manutenção Periódica Modular
                if (Pattern.numeroIndividuosGerados % intervaloManutencao == 0 && limiar < tamanhoPopulacao) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, intervaloManutencao,
                            0.0, // Entropia desativada para este algoritmo (0.0)
                            tamanhoTorneio, paramTorneio
                    );

                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    diversidadeSuficiente = m.diversidadeSuficiente();
                    tamanhoTorneio = m.tamanhoTorneio();
                }
            }
            // Salvamento final de cada reinicialização
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }

        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }


}