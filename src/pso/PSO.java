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

            for (Particle particula : particulas) {
                // Atualiza a velocidade e a posição da partícula
                particula.updateVelocityAndPosition(globalBest.getPattern());

                // Atualiza o melhor global se necessário
                if (particula.getQualidade() > globalBest.getQualidade()) {
                    globalBest = particula;
                }

                // Acumular para a qualidade média
                somaQualidade += particula.getQualidade();

                // Adicionar nova combinação distinta se ainda não apareceu
                combinacoesDistintas.add(new HashSet<>(particula.getPattern().getItens()));

                //Descomentar se quiser ver o ID, itens e qualidade de cada partícula separadamente
                // Imprimir ID, itens e qualidade da partícula
/*                 System.out.println("\nPartícula da vez: ");
                System.out.println("  - ID: " + particula.getId());
                System.out.println("  - Itens: " + particula.getPattern().getItens());
                System.out.println("  - Qualidade: " + particula.getQualidade()); */
            }

            // Calcular e imprimir qualidade média
            double qualidadeMedia = somaQualidade / particulas.size();
            System.out.println("Quantidade de Partículas: " + particulas.size());
            System.out.println("Qualidade Média das Partículas: " + qualidadeMedia);

            // Imprimir número de subgrupos distintos
            System.out.println("Número de Subgrupos Distintos ao Final Desta Iteração: " + combinacoesDistintas.size());

            // Log do melhor global após atualizar todas as partículas
            System.out.println("\nGlobalBest após a iteração: ");
            System.out.println("  - ID: " + globalBest.getId());
            System.out.println("  - Itens: " + globalBest.getPattern().getItens());
            System.out.println("  - Qualidade: " + globalBest.getQualidade());

        }

        return globalBest.getPattern(); // Retornar o Pattern da melhor partícula
    }

    public static void main(String[] args) {
        String caminho = "pastas/bases/";
        String nomebase = "matrixBinaria-Global-100-p.csv";
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
