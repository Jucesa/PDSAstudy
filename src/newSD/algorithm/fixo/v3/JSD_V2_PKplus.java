package newSD.algorithm.fixo.v3;

import dp.Avaliador;
import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.algorithm.JSD;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class JSD_V2_PKplus extends JSD {

    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }

    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] P;
        Pattern[] Pk = new Pattern[k];
        for (int i = 0; i < Pk.length; i++) Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);

        // INICIALIZAÇÃO PADRÃO
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I);

        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) P[i] = (i < I.length) ? I[i] : I[Const.random.nextInt(I.length - 1)];
        } else {
            P = I;
        }

        int ultimaAval = P.length;
        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        double numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        double entropiaMinima = 0.4;
        final double MIN_GANHO_RELATIVO = 0.0001;
        int limiteInferiorStop = (int) (P.length * 0.10); // Floor de 10%

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));

                ultimaAval = limiar;
            }
            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
            int limiteEstagnacao = Math.max(50, (int) ((tamanhoPopulacao * 0.01) / (numeroReinicializacoes + 1)));


            boolean diversidadeSuficiente = true; // Controle da Entropia
            int intervaloManutencao = Math.max(100, P.length / 5);

            // O while agora usa a variável adaptativa em vez de um número mágico
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 3 && limiar > 0) {
                double r = Const.random.nextDouble();
                double razaoLimiar = (double) (limiar - limiteInferiorStop) / (P.length - limiteInferiorStop);
                double Pth = Math.pow(razaoLimiar, 2);

                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2 = (r < Pth) ? P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)]
                        : P[SELECAO.torneioN(P, tamanhoTorneio, limiar, tamanhoPopulacao)];

                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                // =========================================================
                // NOVA LÓGICA: SMART DROP (Poda Inteligente) + ELITE SWAP
                // =========================================================
                double chanceMutacao = Const.random.nextDouble();

                // 20% de chance de aplicar o Smart Drop (Busca Local)
                if (chanceMutacao < 0.05 && paux.getItens().size() > 2) {
                    boolean melhorou = true;

                    // Continua cortando itens até a regra chegar em 2 itens ou não dar mais para cortar
                    while (melhorou && paux.getItens().size() > 2) {
                        melhorou = false;
                        Object itemParaRemover = null;
                        // Tolera uma perda minúscula (2%) para incentivar regras menores e mais gerais
                        double melhorQualidade = paux.getQualidade() * 0.98;
                        Pattern melhorPadrao = paux;

                        // Testa remover cada um dos itens um por vez
                        for (Integer item : paux.getItens()) {
                            HashSet<Integer> tempItens = new HashSet<>(paux.getItens());
                            tempItens.remove(item);
                            Pattern testePadrao = new Pattern(tempItens, tipoAvaliacao);

                            // Se sem esse item a qualidade se mantiver (ou subir!), ele é o alvo
                            if (testePadrao.getQualidade() >= melhorQualidade) {
                                melhorQualidade = testePadrao.getQualidade();
                                melhorPadrao = testePadrao;
                                itemParaRemover = item;
                            }
                        }

                        // Se achou um item inútil, efetiva o corte e roda o while de novo!
                        if (itemParaRemover != null) {
                            paux = melhorPadrao;
                            melhorou = true;
                        }
                    }
                }
                // 10% de chance de Elite Swap (Troca Direcionada)
                else if (chanceMutacao >= 0.85 && chanceMutacao <= 0.95) {
                    HashSet<Integer> itensAtuais = new HashSet<>(paux.getItens());
                    if (itensAtuais.size() >= 2) {
                        // 1. Remove um item aleatoriamente
                        int itemRemoverIndex = Const.random.nextInt(itensAtuais.size());
                        int currentIndex = 0;
                        for (Iterator<Integer> it = itensAtuais.iterator(); it.hasNext(); ) {
                            it.next();
                            if (currentIndex == itemRemoverIndex) {
                                it.remove();
                                break;
                            }
                            currentIndex++;
                        }

                        // 2. ROUBO DE GENE DA ELITE: Pega um indivíduo aleatório da zona de exploração/explotação
                        int indiceDoador = Const.random.nextInt(Math.max(1, limiar));
                        Pattern doador = P[indiceDoador];

                        // Sorteia um dos genes desse doador
                        Integer geneNovo = null;
                        int geneDoadorIndex = Const.random.nextInt(doador.getItens().size());
                        int idx = 0;
                        for (Integer g : doador.getItens()) {
                            if (idx == geneDoadorIndex) {
                                geneNovo = g;
                                break;
                            }
                            idx++;
                        }

                        if (geneNovo != null) {
                            itensAtuais.add(geneNovo);
                            Pattern pauxSwap = new Pattern(itensAtuais, tipoAvaliacao);

                            if (pauxSwap.getQualidade() >= paux.getQualidade() * 0.9) {
                                paux = pauxSwap;
                            }
                        }
                    }
                }
                // =========================================================


                double qualidadeMelhorPai = Math.max(pai1.getQualidade(), pai2.getQualidade());
                boolean ganhoSignificativo = paux.getQualidade() > (qualidadeMelhorPai * (1.0 + MIN_GANHO_RELATIVO));

                // Se o filho for bom o suficiente para entrar na competição
                if (ganhoSignificativo && paux.getQualidade() >= P[limiar - 1].getQualidade()) {

                    // Se ainda temos espaço para expandir (não comeu todos os triviais)
                    if (limiar > 1) {

                        boolean substituiuPorSimilaridade = false;
                        double piorQualidadePmais = Double.MAX_VALUE;

                        // 1. Varredura na região "Pmais" (de limiar até o fim)
                        // Objetivo: Manter diversidade (Similaridade) e encontrar o pior para substituição se necessário
                        for (int i = limiar; i < P.length; i++) {

                            if (Avaliador.similaridade(paux, P[i], Const.SIMILARIDADE_JACCARD) >= similaridade) {
                                // Achou alguém muito parecido! Competição direta.
                                if (paux.getQualidade() > P[i].getQualidade()) {
                                    P[i] = paux; // Substitui o similar mais fraco
                                    // Reduz 1 geração inteira de estagnação como recompensa, travando no zero
                                    numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                                }
                                substituiuPorSimilaridade = true;
                                break; // Encontrou o "clone", resolveu, sai do loop.
                            }

                            // Rastreia o pior indivíduo desta região (caso precisemos dele futuramente,
                            // embora aqui a gente prefira expandir o limiar)
                            if (P[i].getQualidade() < piorQualidadePmais) {
                                piorQualidadePmais = P[i].getQualidade();
                            }
                        }

                        // 2. Se NÃO substituiu ninguém por similaridade (é um padrão Distinto)
                        if (!substituiuPorSimilaridade) {
                            // Estratégia: O padrão é novo e distinto.
                            // Expandimos a fronteira do Pmais "comendo" um item trivial.

                            P[limiar - 1] = paux; // Coloca na fronteira
                            limiar--; // Expande a região Pmais (empurra a fronteira para cima)

                            // Reduz 1 geração inteira de estagnação como recompensa, travando no zero
                            numeroGeracoesSemMelhoraPk = Math.max(0.0, numeroGeracoesSemMelhoraPk - 1.0);
                        }
                    }
                }

                if (Pattern.numeroIndividuosGerados % intervaloManutencao == 0) {

                    // TRAVA DE SEGURANÇA: Só faz a lógica pesada se entraram novatos
                    if (limiar < ultimaAval) {
                        Arrays.sort(P, limiar, ultimaAval);

                        int novosK = SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, ultimaAval), similaridade);

                        if (novosK == 0) {
                            numeroGeracoesSemMelhoraPk ++;
                        } else {
                            numeroGeracoesSemMelhoraPk = 0;
                        }

                        // Atualiza o ponteiro para o próximo ciclo
                        ultimaAval = limiar;

                    } else {
                        // Ninguém novo entrou na elite nesse ciclo, estagnou.
                        numeroGeracoesSemMelhoraPk ++;
                    }

                    // CHECAGEM DE ENTROPIA
                    if (calcularEntropiaPopulacao(P, limiar) <= entropiaMinima) {
                        diversidadeSuficiente = false; // Força parada e reinicialização
                    }

                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
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