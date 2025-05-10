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
    private static int NUM_PARTICULAS = 400; // Alterado para variável não-final para permitir configuração
    private static int idCounter = 1; // IDs únicos
    private static FileWriter fileWriter = new FileWriter(); // Instância do FileWriter
    private static Random randomGenerator = new Random(); // Gerador de números aleatórios  

    // Método para configurar o número de partículas
    public static void setNumParticulas(int numParticulas) {
        NUM_PARTICULAS = numParticulas;
    }

    // Método modificado para retornar um array de Pattern (top-k)
    public static Pattern[] run(int k, String tipoAvaliacao, double tempoMaximoSegundos, String nomeBase, String updateStrategy) {
        System.out.println("Iniciando PSO...");
        long tempoInicio = System.currentTimeMillis();
        StringBuilder log = new StringBuilder();
        ArrayList<Particle> particulas = new ArrayList<>();
        Particle globalBest = null; // Alterar o tipo para Particle
        // HashSet<HashSet<Integer>> combinacoesDistintas = new HashSet<>(); // Para rastrear combinações distintas, ESSA ESTRATÉGIA ESTÁ QUEBRADA, DÁ ERRO DE HEAP SPACE

        System.out.println("Inicializando partículas...");
        // Inicialização: Cada partícula começa com um único item
        log.append("Inicializando as partículas abaixo:\n");
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            HashSet<Integer> itens = new HashSet<>();
            int randomItem = randomGenerator.nextInt(D.numeroItens);
            itens.add(randomItem);
            Particle particula = null;
            switch (updateStrategy) {
                case "Random":
                    particula = new Particle(idCounter++, itens, tipoAvaliacao, D.numeroItens, ITERACOES_MAX, "Random");
                    break;
                case "Complete":
                    particula = new Particle(idCounter++, itens, tipoAvaliacao, D.numeroItens, ITERACOES_MAX, "Complete");
                    break;
                case "BestFromN10":
                    particula = new Particle(idCounter++, itens, tipoAvaliacao, D.numeroItens, ITERACOES_MAX, "BestFromN10");
                    break;
                case "BestFromN100":
                    particula = new Particle(idCounter++, itens, tipoAvaliacao, D.numeroItens, ITERACOES_MAX, "BestFromN100");
                    break;
                case "BestFromNScalingFrom1":
                    particula = new Particle(idCounter++, itens, tipoAvaliacao, D.numeroItens, ITERACOES_MAX, "BestFromNScalingFrom1");
                    break;
                default:
                    break;
            }
            particulas.add(particula);
            log.append("Partícula ").append(particula.getId())
               .append(" inicializada com itens: ").append(itens).append("\n");
            // combinacoesDistintas.add(itens);
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
        log.append("====== Número de Top K Partículas Selecionadas: ").append(k).append(" ======").append("\n");
        log.append("====== Dimensão do Problema: ").append(D.numeroItens).append(" ======").append("\n");
        
        // Contador de iterações para exibição
        int iteracaoCount = 1;
        
        for (int iteracao = 0; iteracao < ITERACOES_MAX; iteracao++) {
            // Verificar se atingiu o tempo máximo
            if ((System.currentTimeMillis() - tempoInicio) / 1000.0 > tempoMaximoSegundos) {
                System.out.println("Tempo máximo atingido. Finalizando PSO após " + iteracao + " iterações.");
                break;
            }
            
            System.out.println("Executando iteração " + (iteracao + 1) + " de " + ITERACOES_MAX);
            iteracaoCount++;
            
            log.append("\n=== Iteração: ").append(iteracao).append(" ===\n");

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
                // combinacoesDistintas.add(new HashSet<>(particula.getPattern().getItens()));
            }

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
            // log.append("  - Número de Subgrupos Distintos ao Final Desta Iteração: ").append(combinacoesDistintas.size()).append("\n");

            // Ordenar partículas pela qualidade e selecionar as top K
            Collections.sort(particulas, Comparator.comparingDouble(Particle::getQualidade).reversed());
            ArrayList<Particle> topKParticles = new ArrayList<>(particulas.subList(0, Math.min(k, particulas.size())));
            log.append("  - Top K ").append(k).append(" Partículas:\n");
            for (int i = 0; i < topKParticles.size(); i++) {
                Particle p = topKParticles.get(i);
                String regras = Avaliador.getRegrasString(new Pattern[]{p.getPattern()}, 1);
                log.append("    * Posição: ").append(i + 1)
                   .append(" | Itens: ").append(p.getPattern().getItens().toString())
                   .append(" | Qualidade: ").append(String.valueOf(p.getQualidade()))
                   .append(" | Subgrupo: ").append(regras)
                   .append("\n");
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

        // Preparar o array de retorno com os top-k patterns
        Pattern[] resultado = new Pattern[k];
        
        // Ordenar as partículas por qualidade
        Collections.sort(particulas, Comparator.comparingDouble(Particle::getQualidade).reversed());
        
        // Pegar os top-k patterns das partículas
        for (int i = 0; i < k && i < particulas.size(); i++) {
            resultado[i] = particulas.get(i).getPattern();
        }
        
        // Se tiver menos partículas que k, preencher o restante com patterns vazios
        for (int i = particulas.size(); i < k; i++) {
            resultado[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }
        
        return resultado; // Retornar o array de top-k patterns
    }

    // O método antigo agora redireciona para o novo
    public static Pattern run(int dimensao, String tipoAvaliacao, String nomeBase, String updateStrategy) {
        Pattern[] patterns = run(1, tipoAvaliacao, 3600, nomeBase, updateStrategy); // Padrão 1 hora
        return patterns[0];
    }

    public static void main(String[] args) {
        String caminho = "pastas/bases/";
        String nomebase = "alon-pn-freq-2.CSV";
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
        Pattern melhorSubgrupo = run(dimensao, tipoAvaliacao, nomebase, "Complete"); // Executando o PSO
    }
}
