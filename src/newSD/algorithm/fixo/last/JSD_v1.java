package newSD.algorithm.fixo.last;

import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.algorithm.JSD;

import java.io.IOException;
import java.util.*;

public class JSD_v1 extends JSD {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }

    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k, double limiteLPercentual) throws IOException {
        Pattern[] P;
        Pattern[] Pk = new Pattern[k];

        // Inicializa Pk
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // População inicial
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I); // Ordena do Melhor para o Pior

        // Limita o tamanho da população se a base for gigantesca
        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < I.length) P[i] = I[i];
                else P[i] = I[Const.random.nextInt(I.length - 1)];
            }
        } else {
            P = I;
        }

        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        int numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);

            // Controle para garantir que a varredura do limite L aconteça apenas uma vez por ciclo
            boolean varreduraLRealizada = false;

            // Calcula o limite absoluto baseado no percentual passado por parâmetro
            int limiteL = Math.max(1, (int) (P.length * limiteLPercentual));

            int limiteEstagnacao = 3;

            while (numeroGeracoesSemMelhoraPk < limiteEstagnacao || limiar > 0) {

                // =========================================================================
                // BLOCO SUPER OTIMIZADO: Varredura por Agrupamento de Qualidade
                // =========================================================================
                if (limiteLPercentual > 0 && !varreduraLRealizada && limiar <= limiteL) {

                    // 1. Garantimos que a zona abaixo do limiar esteja perfeitamente ordenada
                    Arrays.sort(P, limiar, P.length);

                    List<Pattern> unicos = new ArrayList<>();

                    // Varre os padrões abaixo do limiar
                    for (int i = limiar; i < P.length; i++) {
                        Pattern atual = P[i];
                        boolean ehDuplicado = false;

                        // 2. Compara apenas com os últimos únicos validados
                        // Como o vetor está ordenado por qualidade, olhamos de trás para frente
                        for (int j = unicos.size() - 1; j >= 0; j--) {
                            Pattern u = unicos.get(j);

                            // FILTRO 1: Se a qualidade for diferente, NÃO tem como ser o mesmo padrão.
                            // Usamos uma margem (epsilon) pois qualidade geralmente é double.
                            if (Math.abs(atual.getQualidade() - u.getQualidade()) > 0.000001) {
                                break; // Interrompe o laço. Bypassa 99% das comparações lentas!
                            }

                            // FILTRO 2: Qualidade é igual. Os tamanhos dos conjuntos de itens são iguais?
                            Set<?> itensAtual = (Set<?>) atual.getItens();
                            Set<?> itensU = (Set<?>) u.getItens();

                            if (itensAtual.size() == itensU.size()) {
                                // CUSTO REAL: Só chega aqui se Qualidade E Tamanho forem idênticos.
                                if (itensAtual.equals(itensU)) {
                                    ehDuplicado = true;
                                    break;
                                }
                            }
                        }

                        if (!ehDuplicado) {
                            unicos.add(atual);
                        }
                    }

                    int vagasAbertas = (P.length - limiar) - unicos.size();

                    if (vagasAbertas > 0) {
                        // 3. Reagrupa os únicos e injeta diversidade no fundo
                        int index = limiar;
                        for (Pattern p : unicos) {
                            P[index++] = p;
                        }
                        for (int i = index; i < P.length; i++) {
                            P[i] = I[Const.random.nextInt(I.length)];
                        }
                        // Mantém a zona ordenada após a injeção
                        Arrays.sort(P, limiar, P.length);
                    }
                    varreduraLRealizada = true;
                }
                // =========================================================================

                // --- Lógica RARM ---
                double r = Const.random.nextDouble();
                double Pth = (double) limiar / P.length;

                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2;

                if (r < Pth) {
                    // Exploração: Trivial x Trivial
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                } else {
                    // Explotação: Trivial x Raro/Subgrupo
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
                }

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                // Substituição
                if (paux.getQualidade() >= P[limiar - 1].getQualidade()) {
                    if (limiar > 1) {
                        P[limiar - 1] = paux;
                        limiar--;
                    }
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    Arrays.sort(P, limiar, P.length);

                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk,
                            Arrays.copyOfRange(P, limiar, P.length),
                            similaridade);

                    if (novosK == 0) numeroGeracoesSemMelhoraPk += 1;
                    else numeroGeracoesSemMelhoraPk = 0;

                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
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
