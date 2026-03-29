package SDp.algorithm;

import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InitializationManager {
    public static Pattern[] inicializarPorTorneio(String tipoAvaliacao) {
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);

        // Proteção contra quebras caso a base de itens esteja vazia
        if (candidatos.length == 0) return new Pattern[0];

        Pattern[] P = new Pattern[D.numeroItensUtilizados];
        int tamanhoTorneio = 5;

        for (int i = 0; i < P.length; i++) {
            Pattern vencedor = null;
            double melhorQualidade = Double.NEGATIVE_INFINITY;

            // --- Início do Torneio ---
            for (int j = 0; j < tamanhoTorneio; j++) {
                Pattern desafiante = candidatos[Const.random.nextInt(candidatos.length)];

                // Lendo a qualidade apenas uma vez por loop para poupar processamento
                double q = desafiante.getQualidade();

                if (q > melhorQualidade) {
                    melhorQualidade = q;
                    vencedor = desafiante;
                }
            }
            // --- Fim do Torneio ---

            // Otimização de Memória: HashSet inicializado com capacidade 2 e load factor 1.0
            P[i] = new Pattern(new HashSet<>(2, 1.0f), tipoAvaliacao);

            if (vencedor != null) {
                P[i].getItens().addAll(vencedor.getItens());
            }
        }

        return P;
    }

    /**
     * Inicializa a população selecionando itens unitários via Roleta (Fitness Proportionate Selection).
     * A probabilidade de um item ser escolhido é proporcional à sua qualidade relativa.
     */
    public static Pattern[] inicializarPorRoleta(String tipoAvaliacao) {
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);
        if (candidatos.length == 0) return new Pattern[0];

        double somaQualidades = 0.0;
        double[] qualidadesAcumuladas = new double[candidatos.length];

        for (int i = 0; i < candidatos.length; i++) {
            double q = Math.max(0.0, candidatos[i].getQualidade());
            somaQualidades += q;
            qualidadesAcumuladas[i] = somaQualidades;
        }

        Pattern[] P = new Pattern[D.numeroItensUtilizados];

        // Fallback: Se todas as qualidades forem 0
        if (somaQualidades == 0) {
            for (int i = 0; i < P.length; i++) {
                int indexAleatorio = Const.random.nextInt(candidatos.length);
                P[i] = new Pattern(new HashSet<>(candidatos[indexAleatorio].getItens()), tipoAvaliacao);
            }
            return P;
        }

        // 3. Seleção Otimizada com Busca Binária O(K log C)
        for (int i = 0; i < P.length; i++) {
            double valorSorteado = Const.random.nextDouble() * somaQualidades;

            // Arrays.binarySearch retorna (-(ponto de inserção) - 1) se o valor não for encontrado.
            // O ponto de inserção é exatamente o índice do primeiro elemento maior que o valorSorteado!
            int indexSelecionado = Arrays.binarySearch(qualidadesAcumuladas, valorSorteado);

            if (indexSelecionado < 0) {
                // Converte o retorno negativo no índice exato onde a roleta parou
                indexSelecionado = Math.abs(indexSelecionado + 1);
            }

            // Proteção contra arredondamento de double no limite superior do array
            if (indexSelecionado >= candidatos.length) {
                indexSelecionado = candidatos.length - 1;
            }

            // Cria uma NOVA instância. Assumindo que itens de D1 têm tamanho pequeno (ex: 1 item),
            // inicializar o HashSet com capacidade para 2 evita realocações internas de memória.
            P[i] = new Pattern(new HashSet<>(2), tipoAvaliacao);
            P[i].getItens().addAll(candidatos[indexSelecionado].getItens());
        }

        return P;
    }
    /**
     * Inicialização por Roleta Estratificada (Buckets).
     * Divide os itens em faixas de qualidade para balancear performance e diversidade.
     */
    public static Pattern[] inicializarPorRoletaEmFaixas(String tipoAvaliacao) {
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);
        if (candidatos.length == 0) return new Pattern[0];

        // 1. OTIMIZAÇÃO: Busca Linear O(N) para Min e Max (Substitui o Arrays.sort)
        double minQ = Double.MAX_VALUE;
        double maxQ = Double.MIN_VALUE;

        for (Pattern p : candidatos) {
            double q = p.getQualidade();
            if (q < minQ) minQ = q;
            if (q > maxQ) maxQ = q;
        }

        // Garante que a qualidade mínima avaliada não seja negativa
        minQ = Math.max(0, minQ);

        // Se a base for toda igual, retorna aleatório simples
        if (maxQ <= minQ) return inicializarPorRoleta(tipoAvaliacao);

        // CONFIGURAÇÃO
        int NUM_FAIXAS = 10;
        double tamanhoFaixa = (maxQ - minQ) / NUM_FAIXAS;

        List<List<Pattern>> faixas = new ArrayList<>(NUM_FAIXAS);
        double[] pesoFaixas = new double[NUM_FAIXAS];

        for (int i = 0; i < NUM_FAIXAS; i++) {
            faixas.add(new ArrayList<>());
        }

        // 2. Distribui os itens nas faixas e calcula pesos O(N)
        double somaTotalPesos = 0.0;

        for (Pattern p : candidatos) {
            double q = Math.max(0, p.getQualidade());

            int indexFaixa = (int) ((q - minQ) / tamanhoFaixa);
            if (indexFaixa >= NUM_FAIXAS) indexFaixa = NUM_FAIXAS - 1;

            faixas.get(indexFaixa).add(p);
            pesoFaixas[indexFaixa] += q;
            somaTotalPesos += q;
        }

        Pattern[] P = new Pattern[D.numeroItensUtilizados];

        // 3. Seleção
        for (int i = 0; i < P.length; i++) {
            double roleta = Const.random.nextDouble() * somaTotalPesos;
            int faixaEscolhida = NUM_FAIXAS - 1; // Fallback padrão
            double acumulado = 0.0;

            for (int f = 0; f < NUM_FAIXAS; f++) {
                acumulado += pesoFaixas[f];
                if (acumulado >= roleta) {
                    faixaEscolhida = f;
                    break;
                }
            }

            List<Pattern> itensDaFaixa = faixas.get(faixaEscolhida);

            // Defesa mantida para imprecisões de ponto flutuante
            if (itensDaFaixa.isEmpty()) {
                P[i] = new Pattern(new HashSet<>(candidatos[Const.random.nextInt(candidatos.length)].getItens()), tipoAvaliacao);
            } else {
                Pattern escolhido = itensDaFaixa.get(Const.random.nextInt(itensDaFaixa.size()));
                P[i] = new Pattern(new HashSet<>(escolhido.getItens()), tipoAvaliacao);
            }
        }

        return P;
    }
}
