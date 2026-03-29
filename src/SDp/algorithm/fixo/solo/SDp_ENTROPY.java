package SDp.algorithm.fixo.solo;

import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import SDp.algorithm.SDp;
import java.io.IOException;
import java.util.*;

import static SDp.algorithm.SelectionManager.selecionarPaisD1DnQuad;

public class SDp_ENTROPY extends SDp {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio) {
        return tamanhoTorneio;
    }

    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Pattern[] P = setupInitialState(k, Pk, I, tipoAvaliacao);

        // Ajuste: padronizar nome para indexUltimaAval como nos métodos modulares
        int indexUltimaAval = P.length;
        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;
        int intervaloManutencao = Math.max(100, P.length / 5);

        double entropiaMinima = 0.4;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
                ReinicializacaoResult result = realizarReinicializacao(
                        tipoAvaliacao, tamanhoPopulacao, I, Pk, paramTorneio
                );

                P = result.P();
                tamanhoTorneio = result.tamanhoTorneio();
                limiar = result.limiar();
                // IMPORTANTE: Resetar o index de controle na reinicialização
                indexUltimaAval = result.indexUltimaAval();
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);
            boolean diversidadeSuficiente = true;

            // Removida a variável estagnacao fixa (3) para fins de exemplo,
            // mas você pode passá-la por parâmetro se desejar
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 3 && limiar > 0) {

                Pattern[] pais = selecionarPaisD1DnQuad(P, tamanhoTorneio, limiar);
                Pattern pai1 = pais[0];
                Pattern pai2 = pais[1];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                // Lógica de aceitação simplificada para a SDp_ENTROPY
                if (paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                        numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                    }
                }

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