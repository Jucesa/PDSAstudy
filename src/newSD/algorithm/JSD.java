package newSD.algorithm;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;
import newSD.algorithm.fixo.PBSD_FIXO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public abstract class JSD {

    // P = d1 com item de torneio de 5
    // P = roleta
    // P = roleta com faixa

    /**
     * Mét0d0 abstrato para determinar o tamanho do torneio na geração atual.
     * Cada implementação (FIXO ou VARIÁVEL) define sua lógica.
     */
    protected abstract int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP);

        public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
            Pattern[] P;
            Pattern[] Pk = new Pattern[k];

            // Inicializa Pk
            for (int i = 0; i < Pk.length; i++) {
                Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
            }

            // População inicial
            Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
            Arrays.sort(I); // Ordena do Melhor para o Pior

            // Limita o tamanho da população se a base for gigantesca (Otimização de Memória/Tempo)
            // Se tiver 50.000 atributos, usar todos é inviável. Pegamos os Top 5000 + alguns aleatórios?
            // Por enquanto, mantemos a lógica original
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
                    // Reinicia limiar de forma mais agressiva para economizar tempo
                    limiar = Math.max(1, (int) (P.length * 0.9));
                }

                tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
                boolean diversidadeSuficiente = true;

                // OTIMIZAÇÃO 2: Redução de ciclos de estagnação (de 1000 para 500 ou dinâmico)
                int limiteEstagnacao = 500;

                while (numeroGeracoesSemMelhoraPk < limiteEstagnacao || limiar > 0) {

                    // --- Lógica RARM ---
                    double r = Const.random.nextDouble();


                    // Curva Sigmoide Suave em vez de Quadrática Abrupta?
                    // Mantendo quadrática pois funcionou bem para separar fases.
                    double Pth = (double) limiar /P.length;

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

    public static void executarHillClimbing(Pattern p, Pattern[] melhoresItens, Pattern[] Pk, double similaridade) {
        // 1. Otimização Rápida: Se o padrão já for vazio, aborta.
        if (p.getItens().isEmpty()) return;

        Pattern melhorVersao = p;
        HashSet<Integer> itensAtuais = new HashSet<>(p.getItens());

        // CONFIGURAÇÃO DE DESEMPENHO
        // Testar apenas 15 itens aleatórios
        // Isso reduz o custo de Swap/Add em ~70%.
        int MAX_TENTATIVAS = 15;

        // Preparação dos Candidatos:
        // Filtra apenas itens que NÃO estão no padrão e embaralha para garantir aleatoriedade
        List<Integer> candidatos = new ArrayList<>();
        int limiteColeta = Math.min(melhoresItens.length, 50); // Usa o Top 50 como base

        for (int i = 0; i < limiteColeta; i++) {
            if (melhoresItens[i] != null && !melhoresItens[i].getItens().isEmpty()) {
                Integer item = melhoresItens[i].getItens().iterator().next();
                if (!itensAtuais.contains(item)) {
                    candidatos.add(item);
                }
            }
        }
        // O 'shuffle' garante que não estamos sempre testando os mesmos itens "triviais" do topo
        Collections.shuffle(candidatos, Const.random);

        // =================================================================
        // FASE 1: REMOÇÃO (Pruning) - Custo Baixo O(N)
        // Geralmente vale a pena testar exaustivamente pois N (tamanho do padrão) é pequeno.
        // =================================================================
        if (itensAtuais.size() > 1) {
            for (Integer item : itensAtuais) {
                HashSet<Integer> tentativaSet = new HashSet<>(itensAtuais);
                tentativaSet.remove(item);

                // Instancia apenas se necessário (algumas implementações calculam qualidade no construtor)
                Pattern tentativa = new Pattern(tentativaSet, p.getTipoAvaliacao());

                if (tentativa.getQualidade() > melhorVersao.getQualidade()) {
                    // FIRST IMPROVEMENT: Achou? Salva e sai!
                    // Não perde tempo procurando se existe outra remoção marginalmente melhor.
                    SELECAO.salvandoRelevantesDPmais(Pk, new Pattern[]{tentativa}, similaridade);
                    return;
                }
            }
        }

        // =================================================================
        // FASE 2: SUBSTITUIÇÃO (Swap) & ADIÇÃO - Custo Controlado O(MAX_TENTATIVAS)
        // Priorizamos Swap pois é o que corrige o problema de "parceiro errado" (X1614 vs X964)
        // =================================================================

        int tentativasRealizadas = 0;

        for (Integer itemCandidato : candidatos) {
            if (tentativasRealizadas >= MAX_TENTATIVAS) break; // Limite rígido de custo
            tentativasRealizadas++;

            // 2.1 TENTATIVA DE SUBSTITUIÇÃO (Swap)
            // Para cada item original, tenta trocar pelo candidato sorteado
            for (Integer itemOriginal : itensAtuais) {
                HashSet<Integer> tentativaSwapSet = new HashSet<>(itensAtuais);
                tentativaSwapSet.remove(itemOriginal);
                tentativaSwapSet.add(itemCandidato);

                Pattern tentativaSwap = new Pattern(tentativaSwapSet, p.getTipoAvaliacao());

                if (tentativaSwap.getQualidade() > melhorVersao.getQualidade()) {
                    SELECAO.salvandoRelevantesDPmais(Pk, new Pattern[]{tentativaSwap}, similaridade);
                    return; // FIRST IMPROVEMENT: Sai imediatamente
                }
            }

            // 2.2 TENTATIVA DE ADIÇÃO (Specialization)
            // Só tenta adicionar se o padrão for pequeno (ex: < 5 itens) para evitar overfitting
            if (itensAtuais.size() < 5) {
                HashSet<Integer> tentativaAddSet = new HashSet<>(itensAtuais);
                tentativaAddSet.add(itemCandidato);
                Pattern tentativaAdd = new Pattern(tentativaAddSet, p.getTipoAvaliacao());

                if (tentativaAdd.getQualidade() > melhorVersao.getQualidade()) {
                    SELECAO.salvandoRelevantesDPmais(Pk, new Pattern[]{tentativaAdd}, similaridade);
                    return; // FIRST IMPROVEMENT: Sai imediatamente
                }
            }
        }

    }
    public static Pattern[] inicializarPorTorneio(String tipoAvaliacao) {
        // 1. Gera o "pool" de todos os candidatos possíveis (itens unitários)
        // Isso é necessário para ter de onde sortear.
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);

        Pattern[] P = new Pattern[D.numeroItensUtilizados];
        int tamanhoTorneio = 5;

        for (int i = 0; i < P.length; i++) {
            // --- Início do Torneio ---
            Pattern vencedor = null;
            double melhorQualidade = Double.NEGATIVE_INFINITY;

            for (int j = 0; j < tamanhoTorneio; j++) {
                // Sorteia um índice aleatório do pool de candidatos
                int indexSorteado = Const.random.nextInt(candidatos.length);
                Pattern desafiante = candidatos[indexSorteado];

                // Verifica se este desafiante é o melhor deste torneio
                if (desafiante.getQualidade() > melhorQualidade) {
                    melhorQualidade = desafiante.getQualidade();
                    vencedor = desafiante;
                }
            }
            // --- Fim do Torneio ---

            // Adiciona o vencedor à nova população.
            // É CRÍTICO criar uma nova instância (new Pattern) se o seu algoritmo
            // modifica os padrões (mutação) posteriormente. Se você apenas passar a referência,
            // alterar P[0] afetaria P[10] se eles fossem o mesmo objeto.
            if (vencedor != null) {
                P[i] = new Pattern(new HashSet<>(vencedor.getItens()), tipoAvaliacao);
            }
        }

        return P;
    }
    /**
     * Inicializa a população selecionando itens unitários via Roleta (Fitness Proportionate Selection).
     * A probabilidade de um item ser escolhido é proporcional à sua qualidade relativa.
     */
    public static Pattern[] inicializarPorRoleta(String tipoAvaliacao) {
        // 1. Gera o "pool" de todos os candidatos possíveis (itens unitários)
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);

        // 2. Calcula o total das qualidades (Necessário para a roleta)
        double somaQualidades = 0.0;
        double[] qualidadesAcumuladas = new double[candidatos.length];

        for (int i = 0; i < candidatos.length; i++) {
            // Proteção: Roleta não funciona bem com qualidade negativa. Assumimos >= 0.
            // Se sua métrica gerar negativos, você deve normalizar antes.
            double q = Math.max(0.0, candidatos[i].getQualidade());

            somaQualidades += q;
            qualidadesAcumuladas[i] = somaQualidades;
        }

        Pattern[] P = new Pattern[D.numeroItensUtilizados];

        // Fallback: Se todas as qualidades forem 0, seleciona aleatoriamente para evitar travar
        if (somaQualidades == 0) {
            for (int i = 0; i < P.length; i++) {
                int indexAleatorio = Const.random.nextInt(candidatos.length);
                P[i] = new Pattern(new HashSet<>(candidatos[indexAleatorio].getItens()), tipoAvaliacao);
            }
            return P;
        }

        // 3. Seleção (Gira a roleta N vezes)
        for (int i = 0; i < P.length; i++) {
            // Sorteia um valor entre 0 e a SomaTotal
            double valorSorteado = Const.random.nextDouble() * somaQualidades;

            // Encontra onde a "agulha" da roleta parou
            // Busca linear é suficiente aqui, mas para N muito grande, busca binária seria melhor.
            int indexSelecionado = -1;
            for (int j = 0; j < qualidadesAcumuladas.length; j++) {
                if (qualidadesAcumuladas[j] >= valorSorteado) {
                    indexSelecionado = j;
                    break;
                }
            }

            // Se por erro de arredondamento double não achar, pega o último
            if (indexSelecionado == -1) indexSelecionado = candidatos.length - 1;

            // Cria uma NOVA instância (Clone) para garantir independência
            P[i] = new Pattern(new HashSet<>(candidatos[indexSelecionado].getItens()), tipoAvaliacao);
        }

        return P;
    }

    /**
     * Inicialização por Roleta Estratificada (Buckets).
     * Divide os itens em faixas de qualidade para balancear performance e diversidade.
     */
    public static Pattern[] inicializarPorRoletaEmFaixas(String tipoAvaliacao) {
        // 1. Gera todos os itens candidatos
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);

        // É necessário ordenar para facilitar a visualização, mas a lógica de faixas funcionaria sem.
        // Vamos ordenar para garantir que achamos Min e Max rápido.
        Arrays.sort(candidatos); // Ordem Crescente (Pior -> Melhor) pelo Comparable padrão

        // Captura Min e Max (assumindo array ordenado crescente)
        double minQ = Math.max(0, candidatos[0].getQualidade());
        double maxQ = candidatos[candidatos.length - 1].getQualidade();

        // Se a base for toda igual, retorna aleatório simples
        if (maxQ == minQ) return inicializarPorRoleta(tipoAvaliacao);

        // CONFIGURAÇÃO: Número de Faixas (Ex: 10 faixas de qualidade)
        int NUM_FAIXAS = 10;
        double tamanhoFaixa = (maxQ - minQ) / NUM_FAIXAS;

        // Estruturas das Faixas
        List<List<Pattern>> faixas = new ArrayList<>();
        double[] pesoFaixas = new double[NUM_FAIXAS];

        for (int i = 0; i < NUM_FAIXAS; i++) faixas.add(new ArrayList<>());

        // 2. Distribui os itens nas faixas e calcula pesos
        double somaTotalPesos = 0.0;

        for (Pattern p : candidatos) {
            double q = Math.max(0, p.getQualidade());

            // Descobre o índice da faixa: (Valor - Min) / Tamanho
            int indexFaixa = (int) ((q - minQ) / tamanhoFaixa);
            if (indexFaixa >= NUM_FAIXAS) indexFaixa = NUM_FAIXAS - 1; // Proteção para o valor Max exato

            faixas.get(indexFaixa).add(p);

            // O peso da faixa é a soma das qualidades dos itens nela contidos.
            // Isso resolve sua dúvida: Faixas com muitos itens ruins ganham peso pelo volume,
            // Faixas com poucos itens bons ganham peso pela qualidade individual.
            pesoFaixas[indexFaixa] += q;
            somaTotalPesos += q;
        }

        Pattern[] P = new Pattern[D.numeroItensUtilizados];

        // 3. Seleção
        for (int i = 0; i < P.length; i++) {
            // --- Passo A: Escolhe a Faixa (Roleta) ---
            double roleta = Const.random.nextDouble() * somaTotalPesos;
            int faixaEscolhida = -1;
            double acumulado = 0.0;

            for (int f = 0; f < NUM_FAIXAS; f++) {
                acumulado += pesoFaixas[f];
                if (acumulado >= roleta) {
                    faixaEscolhida = f;
                    break;
                }
            }
            if (faixaEscolhida == -1) faixaEscolhida = NUM_FAIXAS - 1;

            // Proteção: Se a faixa sorteada estiver vazia (pode acontecer em distribuições esparsas)
            // Tenta pegar da vizinha ou aleatória global
            List<Pattern> itensDaFaixa = faixas.get(faixaEscolhida);
            if (itensDaFaixa.isEmpty()) {
                P[i] = new Pattern(new HashSet<>(candidatos[Const.random.nextInt(candidatos.length)].getItens()), tipoAvaliacao);
            } else {
                // --- Passo B: Escolhe o Item dentro da Faixa (Aleatório Uniforme) ---
                // Aqui garantimos diversidade! Não pegamos sempre o melhor da faixa.
                int indexNoBucket = Const.random.nextInt(itensDaFaixa.size());
                Pattern escolhido = itensDaFaixa.get(indexNoBucket);

                P[i] = new Pattern(new HashSet<>(escolhido.getItens()), tipoAvaliacao);
            }
        }

        return P;
    }

    protected double calcularEntropiaPopulacao(Pattern[] populacao, int inicio) {
        if (populacao == null || populacao.length == 0) return 0.0;

        HashMap<Integer, Integer> frequenciaItens = new HashMap<>();
        int totalItens = 0;

        // Contabiliza a frequência de cada item na população ativa
        for (int i = inicio; i < populacao.length; i++) {
            if (populacao[i] != null) {
                for (Integer item : populacao[i].getItens()) {
                    frequenciaItens.put(item, frequenciaItens.getOrDefault(item, 0) + 1);
                    totalItens++;
                }
            }
        }

        if (totalItens == 0) return 0.0;

        double entropia = 0.0;
        double log2 = Math.log(2);

        for (Integer count : frequenciaItens.values()) {
            double p = (double) count / totalItens;
            entropia -= p * (Math.log(p) / log2);
        }

        return entropia;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(JSD.class.getName());

        String base = "pastas/Bases BIO 10/yeoh-pn-freq-2.CSV";
        D.SEPARADOR = ",";

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        Const.random = new Random(Const.SEEDS[0]); //Seed
        D.GerarDpDn("p");

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_Qg;
        int quantidadeTorneio = 50;


//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\n\n\nSSDP+");
//
        Pattern[] pks = SSDPmais.run(k, metricaAvaliacao, 0.5, 120);
        Avaliador.imprimirRegras(pks, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
        Pattern.numeroIndividuosGerados = 0;

        System.out.println("\n\n\n\nFIXO");
        PBSD_FIXO fixo = new PBSD_FIXO();
        Pattern[] pk = fixo.run(quantidadeTorneio, 0.5, metricaAvaliacao, k);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
    }
}
