package SDp.algorithm.fixo.combined;

import SDp.algorithm.SDp;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

import static SDp.algorithm.SelectionManager.selecionarPaisD1DnQuad;

public class SDp_INC_QUAD extends SDp {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
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

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
            boolean diversidadeSuficiente = true;

            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 1000 && limiar > 0) {

                // 3. Seleção Quadrática Modular
                Pattern[] pais = selecionarPaisD1DnQuad(P, tamanhoTorneio, limiar);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                Pattern paux = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 4. Verificação de Ganho Significativo e Identidade
                boolean ehNovo = !paux.ehIgual(pais[0]) && !paux.ehIgual(pais[1]);
                if (ehNovo && pTemGanho(paux, qualidadeMelhorPai, MIN_GANHO_RELATIVO) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                // 5. Manutenção Periódica Modular
                if (Pattern.numeroIndividuosGerados % intervaloManutencao == 0) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, intervaloManutencao,
                            0.0, // Entropia desativada
                            tamanhoTorneio, paramTorneio
                    );

                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    diversidadeSuficiente = m.diversidadeSuficiente();
                    tamanhoTorneio = m.tamanhoTorneio();
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