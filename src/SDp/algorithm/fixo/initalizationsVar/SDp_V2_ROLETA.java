package SDp.algorithm.fixo.initalizationsVar;

import SDp.algorithm.InitializationManager;
import SDp.algorithm.MutationManager;
import SDp.algorithm.SDp;
import SDp.algorithm.SelectionManager;
import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import java.io.IOException;
import java.util.Arrays;

public class SDp_V2_ROLETA extends SDp {

    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }

    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];

        // Inicialização específica por Roleta
        Pattern[] I = InitializationManager.inicializarPorRoleta(tipoAvaliacao);
        Pattern[] P = setupInitialState(k, Pk, I, tipoAvaliacao);

        int tamanhoPopulacao = P.length;
        int indexUltimaAval = tamanhoPopulacao;
        int limiar = tamanhoPopulacao;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        double entropiaMinima = 0.4;
        final double MIN_GANHO_RELATIVO = 0.0001;
        int limiteInferiorStop = (int) (P.length * 0.10);

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
                // Reinicialização com parâmetro estratégico '2'
                P = INICIALIZAR.aleatorioD1_Pk_Estrategico(tipoAvaliacao, tamanhoPopulacao, I, Pk, 2);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));
                indexUltimaAval = limiar;
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
            int limiteEstagnacao = Math.max(50, (int) ((tamanhoPopulacao * 0.01) / (numeroReinicializacoes + 1)));
            int intervaloManutencao = Math.max(100, P.length / 5);
            boolean diversidadeSuficiente = true;

            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < limiteEstagnacao && limiar > 0) {

                // 1. Seleção Quadrática
                Pattern[] pais = SelectionManager.selecionarPaisD1DnQuad(P, tamanhoTorneio, limiar, limiteInferiorStop);
                double qualidadeMelhorPai = Math.max(pais[0].getQualidade(), pais[1].getQualidade());

                // 2. Cruzamento
                Pattern paux = CRUZAMENTO.AND(pais[0], pais[1], tipoAvaliacao);

                // 3. Mutação Estratégica
                double r = Const.random.nextDouble();
                if (paux.getItens().size() > 2) {
                    if (r < 0.05) {
                        paux = MutationManager.applySmartDrop(paux, tipoAvaliacao);
                    } else if (r >= 0.85 && r <= 0.95) {
                        paux = MutationManager.applyEliteSwap(paux, P, limiar, tipoAvaliacao);
                    }
                }

                // 4. Aceitação
                boolean ehNovo = !paux.ehIgual(pais[0]) && !paux.ehIgual(pais[1]);
                if (ehNovo && pTemGanho(paux, qualidadeMelhorPai, MIN_GANHO_RELATIVO) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                // 5. Manutenção Periódica
                if (Pattern.numeroIndividuosGerados % intervaloManutencao == 0) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, intervaloManutencao,
                            entropiaMinima, tamanhoTorneio, paramTorneio
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