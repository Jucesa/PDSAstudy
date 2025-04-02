package pso;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;

public class PSO {
    // Caso vá usar um número fixo de partículas, pode descomentar a linha abaixo
    // private static final int NUM_PARTICULAS = 30;
    private static final int ITERACOES_MAX = 100;
    private static final int K = 10;
    private static final int NUM_PARTICULAS = 400; // Número de partículas
    private static int idCounter = 1; // IDs únicos
    private static FileWriter fileWriter = new FileWriter(); // Instância do FileWriter
    private static Random randomGenerator = new Random(); // Gerador de números aleatórios  


    public static Pattern run(int dimensao, String tipoAvaliacao, String nomeBase) {
        System.out.println("Iniciando PSO...");
        StringBuilder log = new StringBuilder();
        ArrayList<Particle> particulas = new ArrayList<>();
        Particle globalBest = null; // Alterar o tipo para Particle
        HashSet<HashSet<Integer>> combinacoesDistintas = new HashSet<>(); // Para rastrear combinações distintas

        System.out.println("Inicializando partículas...");
        // Inicialização: Cada partícula começa com um único item
        log.append("Inicializando as partículas abaixo:\n");
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            HashSet<Integer> itens = new HashSet<>();
            int randomItem = randomGenerator.nextInt(dimensao);
            itens.add(randomItem);
            Particle particula = new Particle(idCounter++, itens, tipoAvaliacao, dimensao, ITERACOES_MAX);
            particulas.add(particula);
            log.append("Partícula ").append(particula.getId())
               .append(" inicializada com itens: ").append(itens).append("\n");
            combinacoesDistintas.add(itens);
            if (globalBest == null || particula.getQualidade() > globalBest.getQualidade()) {
                globalBest = particula; 
            }
        }
        System.out.println("Inicialização completa. Total de partículas: " + particulas.size());

        // Loop principal do BPSO
        System.out.println("\nIniciando loop principal do PSO...");
        log.append("\n====== Nome da Base Analisada: ").append(nomeBase).append(" ======").append("\n");
        log.append("====== Tipo de Avaliação: ").append(tipoAvaliacao).append(" ======").append("\n");
        log.append("====== Número de Partículas: ").append(NUM_PARTICULAS).append(" ======").append("\n");
        log.append("====== Número de Iterações Máximas: ").append(ITERACOES_MAX).append(" ======").append("\n");
        log.append("====== Número de Top K Partículas Selecionadas: ").append(K).append(" ======").append("\n");
        log.append("====== Dimensão do Problema: ").append(dimensao).append(" ======").append("\n");
        for (int iteracao = 0; iteracao < ITERACOES_MAX; iteracao++) {
            System.out.println("Executando iteração " + (iteracao + 1) + " de " + ITERACOES_MAX);
            log.append("\n=== Iteração: ").append(iteracao).append(" ===\n");

            double somaQualidade = 0.0;
            double somaTamanho = 0.0;
            double maiorQualidade = Double.NEGATIVE_INFINITY;
            double menorQualidadeNaoZero = Double.POSITIVE_INFINITY;
            Pattern melhorIndividuo = null;
            Pattern piorIndividuo = null;

            System.out.println("Atualizando partículas...");
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

            System.out.println("Calculando estatísticas da iteração...");
            double qualidadeMedia = somaQualidade / particulas.size();
            double tamanhoMedio = somaTamanho / particulas.size();

            // Adicionar estatísticas dos indivíduos ao log
            log.append("\n=== Estatísticas dos Indivíduos após a iteração ").append(iteracao).append(" ===\n");
            log.append("  - Qualidade Média: ").append(qualidadeMedia).append("\n");
            log.append("  - Tamanho Médio: ").append(tamanhoMedio).append("\n");
            log.append("  - Maior Qualidade: ").append(maiorQualidade).append("\n");
            log.append("  - Menor Qualidade (não zero): ").append(menorQualidadeNaoZero == Double.POSITIVE_INFINITY ? "N/A" : menorQualidadeNaoZero).append("\n");
            log.append("  - Melhor Indivíduo: ").append(melhorIndividuo != null ? melhorIndividuo.getItens() : "N/A").append("\n");
            log.append("  - Pior Indivíduo: ").append(piorIndividuo != null ? piorIndividuo.getItens() : "N/A").append("\n");
            log.append("  - Número de Subgrupos Distintos ao Final Desta Iteração: ").append(combinacoesDistintas.size()).append("\n");

            System.out.println("Ordenando e selecionando top " + K + " partículas...");
            // Ordenar partículas pela qualidade e selecionar as top K
            Collections.sort(particulas, Comparator.comparingDouble(Particle::getQualidade).reversed());
            ArrayList<Particle> topKParticles = new ArrayList<>(particulas.subList(0, Math.min(K, particulas.size())));
            log.append("  - Top K ").append(K).append(" Partículas:\n");
            for (int i = 0; i < topKParticles.size(); i++) {
                Particle p = topKParticles.get(i);
                String regras = Avaliador.getRegrasString(new Pattern[]{p.getPattern()}, 1);
                log.append("    * Posição: ").append(i + 1)
                   .append(" | Itens: ").append(p.getPattern().getItens().toString())
                   .append(" | Qualidade: ").append(String.valueOf(p.getQualidade()))
                   .append(" | Subgrupo: ").append(regras)
                   .append("\n");
                // Imprime também no console
            }

            // Adicionar log do melhor global após atualizar todas as partículas
            log.append("\n=== GlobalBest após a iteração ").append(iteracao).append(" ===\n");
            log.append("  - ID: ").append(globalBest.getId()).append("\n");
            log.append("  - Itens: ").append(globalBest.getPattern().getItens()).append("\n");
            log.append("  - Qualidade: ").append(globalBest.getQualidade()).append("\n");
            log.append("===================================\n");
        }

        System.out.println("\nPSO finalizado!");
        System.out.println("Escrevendo logs no arquivo...");
        fileWriter.writeLines(new String[]{log.toString()});

        return globalBest.getPattern(); // Retornar o Pattern da melhor partícula
    }

    public static void main(String[] args) {
        String caminho = "pastas/bases/";
        String nomebase = "alon-clean50-pn-width-2.CSV";
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
        Pattern melhorSubgrupo = run(dimensao, tipoAvaliacao, nomebase);
    }
}
