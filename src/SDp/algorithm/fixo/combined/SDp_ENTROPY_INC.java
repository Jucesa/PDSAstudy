package SDp.algorithm.fixo.combined;

import SDp.algorithm.SDp;
import SDp.algorithm.SelectionManager;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

public class SDp_ENTROPY_INC extends SDp {
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

        // Parâmetros específicos deste combo
        INTERVALO_MANUTENCAO = tamanhoPopulacao;
        double entropiaMinima = 0.5;

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

            // O limite de 1000 gerações sem melhora é generoso para combos
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 1000 && limiar > 0) {

                // 3. Seleção Linear (Como na SDp_INC)
                Pattern[] pais = SelectionManager.selecionarPaisD1DnLinear(P, tamanhoTorneio, limiar);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                Pattern filho = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 4. Lógica de Ganho Estrito (Ganho Relativo = 0.0)
                // Isso garante que 'filho.qualidade > melhorPai.qualidade'
                if (pTemGanho(filho, qualidadeMelhorPai, 0.0) && filho.getQualidade() > P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = filho;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                // 5. Manutenção Periódica (Controle de Entropia + Top-K)
                if (Pattern.numeroIndividuosGerados % INTERVALO_MANUTENCAO == 0) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, INTERVALO_MANUTENCAO,
                            entropiaMinima, tamanhoTorneio, paramTorneio
                    );

                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    diversidadeSuficiente = m.diversidadeSuficiente();
                    tamanhoTorneio = m.tamanhoTorneio();
                }
            }

            // Sincronização final da época
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }

        // Finalização Global
        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }
}