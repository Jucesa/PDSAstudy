package pso;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class PSO {
    private static final int NUM_PARTICULAS = 30;
    private static final int ITERACOES_MAX = 100;

    private static Random random = new Random();
    private static int idCounter = 1; // Novo campo para gerenciar IDs únicos

    public static Pattern run(int dimensao, String tipoAvaliacao) {
        ArrayList<Particle> particulas = new ArrayList<>();
        Particle globalBest = null; // Alterar o tipo para Particle

        // Inicialização: Cada partícula começa com uma combinação aleatória de itens
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            HashSet<Integer> itens = new HashSet<>();
            int numeroItensInicial = random.nextInt(dimensao / 2) + 1; // Inicia com 1 a metade dos itens
            while (itens.size() < numeroItensInicial) {
                itens.add(random.nextInt(dimensao));
            }
            Particle particula = new Particle(idCounter++, itens, tipoAvaliacao, dimensao, ITERACOES_MAX);
            particulas.add(particula);
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

                // Imprimir ID, itens e qualidade da partícula
                System.out.println("  Partícula ID " + particula.getId() + ": " + particula.getPattern().getItens() + " | Qualidade: " + particula.getQualidade());
            }

            // Calcular e imprimir qualidade média
            double qualidadeMedia = somaQualidade / particulas.size();
            System.out.println("Qualidade Média das Partículas: " + qualidadeMedia);

            // Log do melhor global após atualizar todas as partículas
            System.out.println("\nGlobalBest após a iteração " + iteracao + ":");
            System.out.println("  - ID: " + globalBest.getId());
            System.out.println("  - Itens: " + globalBest.getPattern().getItens());
            System.out.println("  - Qualidade: " + globalBest.getQualidade());

            // Implementar Diversificação Periódica (opcional)
            if (iteracao % 20 == 0 && iteracao != 0) { // A cada 20 iterações
                diversificar(particulas, dimensao, tipoAvaliacao);
                System.out.println("Diversificação aplicada nas partículas.");
            }
        }

        return globalBest.getPattern(); // Retornar o Pattern da melhor partícula
    }

    private static void diversificar(ArrayList<Particle> particulas, int dimensao, String tipoAvaliacao) {
        for (int i = 0; i < particulas.size(); i++) {
            if (random.nextDouble() < 0.1) { // 10% das partículas recebem diversificação
                HashSet<Integer> novosItens = new HashSet<>();
                int numeroItensInicial = random.nextInt(dimensao / 2) + 1;
                while (novosItens.size() < numeroItensInicial) {
                    novosItens.add(random.nextInt(dimensao));
                }
                // Reutilizar o ID da partícula sendo substituída
                int idAtual = particulas.get(i).getId();
                Particle novaParticula = new Particle(idAtual, novosItens, tipoAvaliacao, dimensao, ITERACOES_MAX);
                particulas.set(i, novaParticula);
            }
        }
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
        String tipoAvaliacao = Const.METRICA_Qg;

        Pattern melhorSubgrupo = run(dimensao, tipoAvaliacao);
        System.out.println("Melhor Subgrupo: " + melhorSubgrupo.toString2());
    }
}
