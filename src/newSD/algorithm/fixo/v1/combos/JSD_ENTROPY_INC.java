package newSD.algorithm.fixo.v1.combos;

import dp.Const;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import newSD.algorithm.JSD;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class JSD_ENTROPY_INC extends JSD {
    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        return tamanhoTorneio;
    }
    @Override
    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] P;
        Pattern[] Pk = new Pattern[k];

        // Inicialização segura de Pk
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I); // Ordenação crescente (pior -> melhor) ou decrescente dependendo da sua implementação de CompareTo

        // Preenchimento inicial
        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) P[i] = (i < I.length) ? I[i] : I[Const.random.nextInt(I.length - 1)];
        } else {
            P = I;
        }

        int limiar;
        int tamanhoPopulacao = P.length;
        int numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        // --- PARÂMETROS DE MELHORIA ---
        double entropiaMinima = 0.5; // Abaixo disso, consideramos convergência prematura

        // Loop de Reinicializações (Meta-épocas)
        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {

            // Lógica de Reinicialização
            if (numeroReinicializacoes > 0) {
                // Reinicializa mantendo Pk (conhecimento adquirido) e introduzindo aleatoriedade
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));
            } else {
                limiar = P.length;
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
            boolean diversidadeSuficiente = true;

            // Loop Evolutivo
            while (diversidadeSuficiente && numeroGeracoesSemMelhoraPk < 1000 && limiar > 0) {
                double r = Const.random.nextDouble();
                double Pth = (double) limiar / P.length;

                // Seleção
                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2 = (r < Pth) ? P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)]
                        : P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];

                // Cruzamento
                Pattern filho = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                // --- MELHORIA 1: GANHO DE QUALIDADE ESTRITO ---
                // O filho precisa ser melhor que o melhor dos pais para justificar a substituição
                double qualidadeMelhorPai = Math.max(pai1.getQualidade(), pai2.getQualidade());
                boolean houveGanhoGenetico = filho.getQualidade() > qualidadeMelhorPai;

                // Se houve ganho, verificamos se ele merece entrar na população (substituindo o pior da fronteira 'limiar')
                if (houveGanhoGenetico) {
                    // Verifica se é melhor que o indivíduo na posição de corte (limiar-1)
                    if (filho.getQualidade() > P[limiar - 1].getQualidade()) {
                        if (limiar > 1) {
                            P[limiar - 1] = filho;
                            limiar--; // Move a fronteira da população
                        }
                    }
                }

                // Manutenção Periódica (a cada 'P.length' gerações)
                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    Arrays.sort(P, limiar, P.length);

                    // --- MELHORIA 2: VERIFICAÇÃO DE ENTROPIA ---
                    double entropiaAtual = calcularEntropiaPopulacao(P, limiar);
                    if (entropiaAtual < entropiaMinima) {
                        diversidadeSuficiente = false; // Isso quebrará o while e forçará a reinicialização (próximo loop do 'for')
                    }

                    // Atualiza Top-K Global
                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);

                    if (novosK == 0) numeroGeracoesSemMelhoraPk++;
                    else numeroGeracoesSemMelhoraPk = 0;

                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
                }
            }

            // Finalização da Época
            Arrays.sort(P, limiar, P.length);
            SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, P.length), similaridade);
        }

        // Finalização Global
        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
        return Pk;
    }
}
