package SDp.algorithm.fixo;

import SDp.algorithm.SDp;
import SDp.algorithm.SelectionManager;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

public class SDp_FIXO_classic extends SDp {

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
        int intervaloManutencao = tamanhoPopulacao;
        int limiteEstagnacao = 3;

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

            while (numeroGeracoesSemMelhoraPk < limiteEstagnacao && limiar > 0) {

                // 3. Seleção Linear Modular (Pth = limiar / P.length)
                Pattern[] pais = SelectionManager.selecionarPaisD1DnLinear(P, tamanhoTorneio, limiar);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                Pattern paux = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 4. Aceitação (Ganho 0.0 para manter qualidade >= P[limiar-1])
                if (pTemGanho(paux, qualidadeMelhorPai, 0.0) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                    }
                }

                // 5. Manutenção Periódica Modular
                if (Pattern.numeroIndividuosGerados % intervaloManutencao == 0) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, intervaloManutencao,
                            0.0, // Sem checagem de entropia nesta versão
                            tamanhoTorneio, paramTorneio
                    );

                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    tamanhoTorneio = m.tamanhoTorneio();
                }
            }

            // Sincronização final da rodada
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }

        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }
}