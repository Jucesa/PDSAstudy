package SDp.algorithm.fixo.combined;

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


public class SDp_Total extends SDp {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio) {
        return tamanhoTorneio;
    }


    public Pattern[] run(int estagnacao, int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Pattern[] P = setupInitialState(k, Pk, I, tipoAvaliacao);

        int tamanhoPopulacao = P.length;
        int indexUltimaAval = tamanhoPopulacao;
        int limiar = tamanhoPopulacao;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        double entropiaMinima = 0.25;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
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
            INTERVALO_MANUTENCAO = Math.max(100, P.length / 5);
            int contadorManutencao = INTERVALO_MANUTENCAO;
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < estagnacao && limiar > 0) {

                Pattern[] pais = SelectionManager.selecionarPaisD1DnQuad(P, tamanhoTorneio, limiar);
                Pattern pai1 = pais[0];
                Pattern pai2 = pais[1];

                double qualidadeMelhorPai = Math.max(pai1.getQualidade(), pai2.getQualidade());

                Pattern paux;
                // Se os pais forem idênticos, cruzá-los é desperdício de banco de dados. Forçamos a mutação!
                if (pai1 == pai2 || pai1.getItens().equals(pai2.getItens())) {
                    paux = MutationManager.applySmartDrop(pai1, tipoAvaliacao);
                } else {
                    paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);
                }

                double r = Const.random.nextDouble();

                if(paux.getItens().size() > 2) {
                    if (r >= 0.90) {
                        // Range [0.9 - 1.0]: 10% chance for Smart Drop
                        paux = MutationManager.applySmartDrop(paux, tipoAvaliacao);
                    }
                    else if (r >= 0.80) {
                        // Range [0.8 - 0.9]: 10% chance for Elite Swap
                        paux = MutationManager.applyEliteSwap(paux, Pk, tipoAvaliacao);
                    }
                }

                //checa se o novo Pattern eh melhor MIN_GANHO_RELATIVO vezes mais que o melhor pai
                //alem disso ele precisa efetivamente substituir o pior item por qualidade absoluta
                if (pTemGanho(paux, qualidadeMelhorPai, 0) && paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        // Reduz 1 geração inteira de estagnação como recompensa, travando no zero
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

                contadorManutencao--;
                if (contadorManutencao <= 0) {
                    ManutencaoResult m = processarManutencao(
                            limiar, indexUltimaAval, P, Pk, similaridade,
                            numeroGeracoesSemMelhoraPk, INTERVALO_MANUTENCAO,
                            entropiaMinima, tamanhoTorneio, paramTorneio
                    );

                    // Atualiza o estado do loop com os resultados
                    numeroGeracoesSemMelhoraPk = m.numeroGeracoesSemMelhoraPk();
                    indexUltimaAval = m.indexUltimaAval();
                    diversidadeSuficiente = m.diversidadeSuficiente();
                    tamanhoTorneio = m.tamanhoTorneio();
                    contadorManutencao = INTERVALO_MANUTENCAO;
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
