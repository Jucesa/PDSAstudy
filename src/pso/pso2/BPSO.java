package pso.pso2;

import dp.Const;
import dp.D;
import dp.Pattern;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class BPSO {
    protected Pattern gbestPattern = null;
    protected double gbestQuality = Double.NEGATIVE_INFINITY;
    protected int numParticles;
    protected int numIterations;
    protected int dimension; // deve ser igual ao número de atributos dos exemplos
    protected double w; // inércia
    protected double c1; // coeficiente cognitivo
    protected double c2; // coeficiente social
    protected ArrayList<Particle> swarm;
    protected Random rand = new Random();

    // Parâmetros para PSO adaptativo
    protected double wMax = 0.9;
    protected double wMin = 0.4;
    protected double c1Max = 4;
    protected double c1Min = 0.5;
    protected double c2Max = 5;
    protected double c2Min = 0.5;

    // A MÉTRICA É DEFINIDA AQUI!!!!!!!!!!!!
    protected String metrica = Const.METRICA_WRACC;

    protected double probTrocaJanela = 0.05; // probabilidade de trocar a janela a cada iteração
    protected int windowMin = 5;  // tamanho mínimo da janela
    protected int windowMax = 10; // tamanho máximo da janela

    // Estruturas auxiliares para armazenar a janela e o tamanho de cada partícula
    protected ArrayList<int[]> janelasAtributos;
    protected ArrayList<Integer> tamanhosJanela;

    protected int mutationInterval = 10; // a cada X iterações
    protected double mutationRate = 0.15; // 15% das partículas

    public BPSO(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        this.numParticles = numParticles;
        this.numIterations = numIterations;
        // Corrige dimension para nunca exceder o número de atributos dos exemplos
        if (D.numeroAtributos > 0 && dimension > D.numeroAtributos) {
            this.dimension = D.numeroAtributos;
        } else {
            this.dimension = dimension;
        }
        this.w = w;
        this.c1 = c1;
        this.c2 = c2;
        this.swarm = new ArrayList<>();
    }



    public void setProbTrocaJanela(double prob) {
        this.probTrocaJanela = prob;
    }

    public void setWindowLimits(int min, int max) {
        this.windowMin = min;
        this.windowMax = max;
    }

    public void setMutationParams(int interval, double rate) {
        this.mutationInterval = interval;
        this.mutationRate = rate;
    }

    protected int[] sorteiaJanela(int tamanho) {
    // Sorteia windowSize índices únicos de atributos
    int maxAtributos = D.numeroAtributos > 0 ? D.numeroAtributos : dimension;
    ArrayList<Integer> indices = new ArrayList<>();
    for (int i = 0; i < maxAtributos; i++) indices.add(i);
    java.util.Collections.shuffle(indices, rand);
    int[] janela = new int[Math.min(tamanho, maxAtributos)];
    for (int i = 0; i < janela.length; i++) janela[i] = indices.get(i);
    // Garante que todos os índices sorteados são < maxAtributos
    for (int i = 0; i < janela.length; i++) {
        if (janela[i] >= maxAtributos) {
            janela[i] = maxAtributos - 1;
        }
    }
    return janela;
    }

    public void initializeSwarm() {
        swarm.clear();
        janelasAtributos = new ArrayList<>();
        tamanhosJanela = new ArrayList<>();
        int metade = numParticles / 2;
        int quarto = numParticles / 4;
        int resto = numParticles - metade - quarto;

        // Metade com 1 item
        for (int i = 0; i < metade; i++) {
            int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
            tamanhosJanela.add(tamJanela);
            int[] janela = sorteiaJanela(tamJanela);
            janelasAtributos.add(janela);
            HashSet<Integer> itens = new HashSet<>();
            int idx = rand.nextInt(janela.length);
            if (janela[idx] >= 0 && janela[idx] < D.numeroAtributos) {
                itens.add(janela[idx]);
            }
            Pattern pattern = new Pattern(itens, metrica);
            double[] velocity = new double[dimension];
            for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
            swarm.add(new Particle(pattern, velocity));
            updateGbest(pattern);
        }

        // Um quarto com 2 ou 3 itens
        for (int i = 0; i < quarto; i++) {
            int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
            tamanhosJanela.add(tamJanela);
            int[] janela = sorteiaJanela(tamJanela);
            janelasAtributos.add(janela);
            HashSet<Integer> itens = new HashSet<>();
            int numItens = 2 + rand.nextInt(2); // 2 ou 3
            while (itens.size() < numItens) {
                int idx = rand.nextInt(janela.length);
                if (janela[idx] >= 0 && janela[idx] < D.numeroAtributos) {
                    itens.add(janela[idx]);
                }
            }
            Pattern pattern = new Pattern(itens, metrica);
            double[] velocity = new double[dimension];
            for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
            swarm.add(new Particle(pattern, velocity));
            updateGbest(pattern);
        }

        // Restante com quantidade aleatória de itens (1 a 5)
        for (int i = 0; i < resto; i++) {
            int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
            tamanhosJanela.add(tamJanela);
            int[] janela = sorteiaJanela(tamJanela);
            janelasAtributos.add(janela);
            HashSet<Integer> itens = new HashSet<>();
            int numItens = 1 + rand.nextInt(Math.min(5, janela.length));
            while (itens.size() < numItens) {
                int idx = rand.nextInt(janela.length);
                if (janela[idx] >= 0 && janela[idx] < D.numeroAtributos) {
                    itens.add(janela[idx]);
                }
            }
            Pattern pattern = new Pattern(itens, metrica);
            double[] velocity = new double[dimension];
            for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
            swarm.add(new Particle(pattern, velocity));
            updateGbest(pattern);
        }
        // for (Particle p : swarm) {
        //     System.out.println(
        //         p.getPattern().toString2() + " Qualidade: " + p.getPattern().getQualidade()
        //     );
        // }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }


    public void setAdaptiveParams(double wMax, double wMin, double c1Max, double c1Min, double c2Max, double c2Min) {
        this.wMax = wMax;
        this.wMin = wMin;
        this.c1Max = c1Max;
        this.c1Min = c1Min;
        this.c2Max = c2Max;
        this.c2Min = c2Min;
    }

    public Pattern[] run(int k) {
        initializeSwarm();
        // Influência extra dos itens do gbest: crossover parcial
        Pattern gbestPatternForItens = gbestPattern;
        HashSet<Integer> gbestItensForItens = new HashSet<>();
        if (gbestPatternForItens != null) {
            gbestItensForItens.addAll(gbestPatternForItens.getItens());
        }
        double bestQuality = Double.NEGATIVE_INFINITY;
        Pattern elitePattern = null;
        int semMelhora = 0;
        int maxSemMelhora = Math.max(10, numIterations / 10); // critério para aumentar mutação
        double baseMutationRate = mutationRate;
        int[] stagnation = new int[swarm.size()]; // contador de estagnação por partícula

        // Parâmetros para melhorias
        int hillClimbInterval = 40;
        int hillClimbTop = 2;
        int minItens = 2;
        int maxItens = 8;
        double diversityThreshold = 0.9; // 90% dos itens iguais ao gbest

        for (int iter = 0; iter < numIterations; iter++) {
            // Diversidade: reinicializar partículas muito parecidas com o gbest
            HashSet<Integer> gbestItensForDiversity = gbestPattern != null ? gbestPattern.getItens() : new HashSet<>();
            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                // Só copia gbest para pbest se a partícula estiver estagnada por >=10 iterações e com 2% de chance
                Pattern gbestPatternLocal = gbestPattern;
                if (gbestPatternLocal != null && stagnation[idx] >= 10 && gbestPatternLocal.getQualidade() > particle.getPbestQuality() && rand.nextDouble() < 0.02) {
                    particle.setPattern(new Pattern(gbestPatternLocal));
                    particle.updatePbest();
                }

                // Influência extra dos itens do gbest: crossover parcial
                if (rand.nextDouble() < 0.10 && gbestPatternForItens != null) {
                    HashSet<Integer> meusItens = new HashSet<>(particle.getPattern().getItens());
                    ArrayList<Integer> faltantes = new ArrayList<>();
                    for (Integer it : gbestItensForItens) {
                        if (!meusItens.contains(it)) faltantes.add(it);
                    }
                    if (!faltantes.isEmpty()) {
                        int escolhido = faltantes.get(rand.nextInt(faltantes.size()));
                        meusItens.add(escolhido);
                        // Atualiza o padrão da partícula (mantém velocity)
                        Pattern novo = new Pattern(meusItens, metrica);
                        particle.setPattern(novo);
                    }
                }
                HashSet<Integer> itens = new HashSet<>(particle.getPattern().getItens());
                int iguais = 0;
                for (Integer att : itens) {
                    if (gbestItensForDiversity.contains(att)) iguais++;
                }
                double similaridade = itens.size() > 0 ? (double) iguais / itens.size() : 0.0;
                if (similaridade > diversityThreshold && itens.size() > 0) {
                    // Reinicializa
                    int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
                    int[] janela = sorteiaJanela(tamJanela);
                    janelasAtributos.set(idx, janela);
                    HashSet<Integer> novosItens = new HashSet<>();
                    int numItens = 1 + rand.nextInt(Math.min(5, janela.length));
                    while (novosItens.size() < numItens) {
                        int val = janela[rand.nextInt(janela.length)];
                        if (val >= 0 && val < D.numeroAtributos) {
                            novosItens.add(val);
                        }
                    }
                    Pattern pattern = new Pattern(novosItens, metrica);
                    double[] velocity = new double[dimension];
                    for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
                    particle.setPattern(pattern);
                    particle.setVelocity(velocity);
                    particle.updatePbest();
                    updateGbest(pattern);
                    stagnation[idx] = 0;
                }
            }
            // Ajuste dinâmico dos parâmetros
            double frac = (double) iter / (double) numIterations;
            w = wMax - (wMax - wMin) * frac;
            c1 = c1Max - (c1Max - c1Min) * frac;
            c2 = c2Min + (c2Max - c2Min) * frac;

            // Elitismo: salva o melhor padrão global
            Pattern gbest = gbestPattern;
            double gbestQ = gbestQuality;
            if (gbest != null && gbestQ > bestQuality) {
                bestQuality = gbestQ;
                elitePattern = new Pattern(gbest.getItens(), metrica);
                semMelhora = 0;
                mutationRate = baseMutationRate;
            } else {
                semMelhora++;
                // Mutação adaptativa: aumenta taxa se não melhora
                if (semMelhora > maxSemMelhora) {
                    mutationRate = Math.min(0.5, mutationRate * 1.2);
                }
            }

            // ... Mutação periódica removida ...

            for (int idx = 0; idx < swarm.size(); idx++) {
                Particle particle = swarm.get(idx);
                // Diversidade: reinicializa partícula estagnada
                if (stagnation[idx] > Math.max(10, numIterations / 20)) {
                    int tamJanela = windowMin + rand.nextInt(windowMax - windowMin + 1);
                    int[] janela = sorteiaJanela(tamJanela);
                    janelasAtributos.set(idx, janela);
                    HashSet<Integer> itens = new HashSet<>();
                    int numItens = 1 + rand.nextInt(Math.min(5, janela.length));
                    while (itens.size() < numItens) {
                        int val = janela[rand.nextInt(janela.length)];
                        if (val >= 0 && val < D.numeroAtributos) {
                            itens.add(val);
                        }
                    }
                    Pattern pattern = new Pattern(itens, metrica);
                    double[] velocity = new double[dimension];
                    for (int d = 0; d < dimension; d++) velocity[d] = rand.nextDouble() - 0.5;
                    particle.setPattern(pattern);
                    particle.setVelocity(velocity);
                    particle.updatePbest();
                    updateGbest(pattern);
                    stagnation[idx] = 0;
                }

                // ... Lógica dinâmica da janela ...
                int tamJanelaInicial = tamanhosJanela.get(idx);
                int tamJanelaDinamico = windowMin + (int) Math.round((windowMax - windowMin) * (1.0 - frac));
                tamJanelaDinamico = Math.min(tamJanelaDinamico, tamJanelaInicial);
                tamJanelaDinamico = Math.max(windowMin, tamJanelaDinamico);

                boolean trocarJanela = rand.nextDouble() < probTrocaJanela;
                int[] janela = janelasAtributos.get(idx);
                if (janela.length != tamJanelaDinamico || trocarJanela) {
                    janela = sorteiaJanela(tamJanelaDinamico);
                    janelasAtributos.set(idx, janela);
                }

                double[] velocity = particle.getVelocity();
                HashSet<Integer> itens = new HashSet<>(particle.getPattern().getItens());
                HashSet<Integer> pbestItens = particle.getPbestPattern().getItens();
                HashSet<Integer> gbestItensSet = gbestPattern != null ? gbestPattern.getItens() : new HashSet<>();

                double oldQual = particle.getPattern().getQualidade();

                for (int j = 0; j < janela.length; j++) {
                    int d = janela[j];
                    int x = itens.contains(d) ? 1 : 0;
                    int pbest = pbestItens.contains(d) ? 1 : 0;
                    int gbestVal = gbestItensSet.contains(d) ? 1 : 0;

                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();

                    velocity[d] = w * velocity[d]
                        + c1 * r1 * (pbest - x)
                        + c2 * r2 * (gbestVal - x);

                    double prob = sigmoid(velocity[d]);
                    if (rand.nextDouble() < prob) {
                        itens.add(d);
                    } else {
                        itens.remove(d);
                    }
                }
                particle.setVelocity(velocity);

                // Penalização para padrões muito pequenos ou grandes
                Pattern newPattern = new Pattern(itens, metrica);
                particle.setPattern(newPattern);
                particle.updatePbest();
                updateGbest(newPattern);

                // Atualiza estagnação
                double newQual = newPattern.getQualidade();
                if (Math.abs(newQual - oldQual) < 1e-8) {
                    stagnation[idx]++;
                } else {
                    stagnation[idx] = 0;
                }
            // Busca local (hill climbing) nos melhores pbests a cada N iterações
            if (hillClimbInterval > 0 && iter > 0 && iter % hillClimbInterval == 0) {
                ArrayList<Pattern> allPbestsHC = new ArrayList<>();
                for (Particle p : swarm) allPbestsHC.add(p.getPbestPattern());
                allPbestsHC.sort((a, b) -> Double.compare(b.getQualidade(), a.getQualidade()));
                for (int i = 0; i < Math.min(hillClimbTop, allPbestsHC.size()); i++) {
                    Pattern p = allPbestsHC.get(i);
                    HashSet<Integer> base = new HashSet<>(p.getItens());
                    double bestQ = p.getQualidade();
                    boolean improved = false;
                    int estagna = 0;
                    // Tenta até estagnar 10 vezes, mudei isso aqui já
                    while (estagna < 100) {
                        int d = rand.nextInt(D.numeroAtributos > 0 ? D.numeroAtributos : 1);
                        HashSet<Integer> vizinho = new HashSet<>(base);
                        if (vizinho.contains(d)) {
                            vizinho.remove(d);
                        } else {
                            vizinho.add(d);
                        }
                        if (vizinho.size() >= minItens && vizinho.size() <= maxItens) {
                            Pattern vizinhoPattern = new Pattern(vizinho, metrica);
                            double q = vizinhoPattern.getQualidade();
                            if (q > bestQ) {
                                bestQ = q;
                                base = vizinho;
                                improved = true;
                                estagna = 0; // reset estagnação
                            } else {
                                estagna++;
                            }
                        } else {
                            estagna++;
                        }
                    }
                    if (improved) {
                        Pattern melhorado = new Pattern(base, metrica);
                        updateGbest(melhorado);
                    }
                }
            }
            }

            // Elitismo: garante que o melhor padrão global não se perca
            if (elitePattern != null && gbestQuality < bestQuality) {
                updateGbest(elitePattern);
            }

            // Imprimir progresso
            System.out.println("Iteração " + iter + " - Melhor qualidade: " + gbestQuality);
        }
        // Coletar todos os pbests
        ArrayList<Pattern> allPbests = new ArrayList<>();
        for (Particle p : swarm) {
            allPbests.add(p.getPbestPattern());
        }
        // Adiciona o gbest explicitamente
        if (gbestPattern != null) {
            allPbests.add(gbestPattern);
        }
        // Ordenar por qualidade (decrescente)
        allPbests.sort((a, b) -> Double.compare(b.getQualidade(), a.getQualidade()));
        // Remover duplicatas (opcional, se quiser apenas padrões únicos)
        ArrayList<Pattern> unique = new ArrayList<>();
        for (Pattern p : allPbests) {
            boolean found = false;
            for (Pattern u : unique) {
                if (u.equals(p)) {
                    found = true;
                    break;
                }
            }
            if (!found) unique.add(p);
            // Não faz mais break aqui, pois queremos todos únicos possíveis
        }
        // Retornar até k padrões únicos (se houver menos, retorna só os disponíveis)
        int numRetornar = Math.min(k, unique.size());
        Pattern[] result = new Pattern[numRetornar];
        for (int i = 0; i < numRetornar; i++) {
            result[i] = unique.get(i);
        }
        return result;
    }

    public Pattern getBestPattern() {
        return gbestPattern;
    }
    // Novo método para atualizar o gbest local
    protected void updateGbest(Pattern candidate) {
        if (gbestPattern == null || candidate.getQualidade() > gbestQuality) {
            gbestPattern = new Pattern(candidate);
            gbestQuality = candidate.getQualidade();
        }
    }

     public static void main(String[] args) {
        String caminho = "pastas/basesBrunoExperimento/";
        String nomebase = "alon-clean50-pn-width-2.CSV";
        String caminhoBase = caminho + nomebase;
        D.SEPARADOR = ",";
        Const.random = new Random(Const.SEEDS[0]);

        try {
            D.CarregarArquivo(caminhoBase, D.TIPO_CSV); // Carregando base de dados
            D.GerarDpDn("p"); // Gerando D+ e D-
            double percentualAlvo = (D.numeroExemplosPositivo * 100.0) / D.numeroExemplos;
            System.out.println("Base: " + D.nomeBase);
            System.out.println("Nº Atributos: " + D.numeroAtributos);
            System.out.println("Nº Exemplos: " + D.numeroExemplos);
            System.out.printf("%% Classe Alvo: %.2f%%\n", percentualAlvo);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int dimensao = D.numeroItens;
        String tipoAvaliacao = Const.METRICA_WRACC;
        int numParticles = 1000;
        int numIterations = 100;
        double wMax = 0.9;
        double wMin = 0.4;
        double c1Max = 4.0;
        double c1Min = 0.5;
        double c2Max = 5.0;
        double c2Min = 0.5;
        int k = 10;

        // Inicializa com valores iniciais (wMax, c1Max, c2Min)
        BPSO bpso = new BPSO(numParticles, numIterations, dimensao, wMax, c1Max, c2Min);
        // Ajusta os parâmetros adaptativos
        bpso.setAdaptiveParams(wMax, wMin, c1Max, c1Min, c2Max, c2Min);
        Pattern[] melhoresSubgrupos = bpso.run(k); // Executando o BPSO

        // Exemplo de impressão dos melhores padrões
        for (Pattern p : melhoresSubgrupos) {
            System.out.println(p.toString2());
            System.out.println("Qualidade: " + p.getQualidade());
        }
    }
}



