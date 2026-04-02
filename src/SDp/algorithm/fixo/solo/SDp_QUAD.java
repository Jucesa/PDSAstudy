package SDp.algorithm.fixo.solo;

import SDp.algorithm.SDp;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

import static SDp.algorithm.SelectionManager.selecionarPaisD1DnQuad;

public class SDp_QUAD extends SDp {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio) {
        return tamanhoTorneio;
    }
    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);

        // 1. Inicialização Modular (Pk e P)
        Pattern[] P = setupInitialState(k, Pk, I, tipoAvaliacao);

        int tamanhoPopulacao = P.length;
        int indexUltimaAval = tamanhoPopulacao;
        int limiar = tamanhoPopulacao;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        // Intervalo de manutenção baseado no tamanho da população
        INTERVALO_MANUTENCAO = tamanhoPopulacao;

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

                // 3. Seleção Quadrática Modular
                // Nota: Usamos o mét0do selecionarPaisD1DnQuad que definimos na classe base SDp
                Pattern[] pais = selecionarPaisD1DnQuad(P, tamanhoTorneio, limiar);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                Pattern paux = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 4. Verificação de Aceitação (Usando ganho zero para manter comportamento original)
                if (pTemGanho(paux, qualidadeMelhorPai, 0.0) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                // 5. Manutenção Periódica Modular
                if (Pattern.numeroIndividuosGerados % INTERVALO_MANUTENCAO == 0 && limiar < tamanhoPopulacao) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, INTERVALO_MANUTENCAO,
                            0.0, // Entropia desativada nesta variante
                            tamanhoTorneio, paramTorneio
                    );

                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    diversidadeSuficiente = m.diversidadeSuficiente();
                    tamanhoTorneio = m.tamanhoTorneio();
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