package pso;

import dp.Const;
import dp.D;
import dp.Pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class PSO {
    // Caso vá usar um número fixo de partículas, pode descomentar a linha abaixo
    // private static final int NUM_PARTICULAS = 30;
    private static final int ITERACOES_MAX = 100;

    private static int idCounter = 1; // Novo campo para gerenciar IDs únicos

    public static Pattern run(int dimensao, String tipoAvaliacao) {
        ArrayList<Particle> particulas = new ArrayList<>();
        Particle globalBest = null; // Alterar o tipo para Particle
        HashSet<HashSet<Integer>> combinacoesDistintas = new HashSet<>(); // Para rastrear combinações distintas

        // Inicialização: Cada partícula começa com um único item
        for (int i = 0; i < dimensao; i++) {
            HashSet<Integer> itens = new HashSet<>();
            itens.add(i); // Cada partícula começa com um item único
            Particle particula = new Particle(idCounter++, itens, tipoAvaliacao, dimensao, ITERACOES_MAX);
            particulas.add(particula);
            combinacoesDistintas.add(itens); // Adicionar combinação inicial
            if (globalBest == null || particula.getQualidade() > globalBest.getQualidade()) {
                globalBest = particula; // Atribuir a partícula, não o pattern
            }
        }

        // Loop principal do BPSO
        for (int iteracao = 0; iteracao < ITERACOES_MAX; iteracao++) {
            System.out.println("\n=== Iteração: " + iteracao + " ===");

            double somaQualidade = 0.0;
            double somaTamanho = 0.0;
            double maiorQualidade = Double.NEGATIVE_INFINITY;
            double menorQualidadeNaoZero = Double.POSITIVE_INFINITY;
            Pattern melhorIndividuo = null;
            Pattern piorIndividuo = null;

            for (Particle particula : particulas) {
                // Atualiza a velocidade e a posição da partícula
                particula.updateVelocityAndPosition(globalBest.getPattern());

                // Atualiza o melhor global se necessário
                if (particula.getQualidade() > globalBest.getQualidade()) {
                    globalBest = particula;
                }

                // Acumular para a qualidade média
                double qualidade = particula.getPattern().getQualidade();
                int tamanho = particula.getPattern().getItens().size();

                somaQualidade += qualidade;
                somaTamanho += tamanho;

                if (qualidade > maiorQualidade) {
                    maiorQualidade = qualidade;
                    melhorIndividuo = particula.getPattern();
                }

                if (qualidade > 0 && qualidade < menorQualidadeNaoZero) {
                    menorQualidadeNaoZero = qualidade;
                    piorIndividuo = particula.getPattern();
                }

                // Adicionar nova combinação distinta se ainda não apareceu
                combinacoesDistintas.add(new HashSet<>(particula.getPattern().getItens()));
            }

            double qualidadeMedia = somaQualidade / particulas.size();
            double tamanhoMedio = somaTamanho / particulas.size();

            // Imprimir estatísticas dos indivíduos
            System.out.println("\n=== Estatísticas dos Indivíduos após a iteração " + iteracao + " ===");
            System.out.println("  - Qualidade Média: " + qualidadeMedia);
            System.out.println("  - Tamanho Médio: " + tamanhoMedio);
            System.out.println("  - Maior Qualidade: " + maiorQualidade);
            System.out.println("  - Menor Qualidade (não zero): " + (menorQualidadeNaoZero == Double.POSITIVE_INFINITY ? "N/A" : menorQualidadeNaoZero));
            System.out.println("  - Melhor Indivíduo: " + melhorIndividuo.getItens());
            System.out.println("  - Pior Indivíduo: " + piorIndividuo.getItens());
            System.out.println("===================================");

            // Imprimir número de subgrupos distintos
            System.out.println("Número de Subgrupos Distintos ao Final Desta Iteração: " + combinacoesDistintas.size());

            // Log do melhor global após atualizar todas as partículas
            System.out.println("\n=== GlobalBest após a iteração " + iteracao + " ===");
            System.out.println("  - ID: " + globalBest.getId());
            System.out.println("  - Itens: " + globalBest.getPattern().getItens());
            System.out.println("  - Qualidade: " + globalBest.getQualidade());
            System.out.println("===================================");

        }

        return globalBest.getPattern(); // Retornar o Pattern da melhor partícula
    }

    public static void main(String[] args) {
        String caminho = "pastas/bases/";
        String nomebase = "alon-clean50-pn-width-2.csv";
        String caminhoBase = caminho + nomebase;
        D.SEPARADOR = ",";
        Const.random = new Random(Const.SEEDS[0]);

        try {
            D.CarregarArquivo(caminhoBase, D.TIPO_CSV); // Carregando base de dados
            D.GerarDpDn("p"); // Gerando D+ e D-
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int dimensao = D.numeroItens;
        String tipoAvaliacao = Const.METRICA_WRACC;

        Pattern melhorSubgrupo = run(dimensao, tipoAvaliacao);
        System.out.println("Melhor Subgrupo: " + melhorSubgrupo.toString2());
    }

}
