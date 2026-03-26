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
import newSD.algorithm.fixo.last.JSD_v1;
import newSD.algorithm.fixo.v2.JSD_V2;
import sd.Aleatorio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public abstract class JSD {

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

                int limiteEstagnacao = 3;

                while (numeroGeracoesSemMelhoraPk < limiteEstagnacao || limiar > 0) {

                    // --- Lógica RARM ---
                    double r = Const.random.nextDouble();

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

    public static Pattern[] inicializarPorTorneio(String tipoAvaliacao) {
        Pattern[] candidatos = INICIALIZAR.D1(tipoAvaliacao);

        // Proteção contra quebras caso a base de itens esteja vazia
        if (candidatos == null || candidatos.length == 0) return new Pattern[0];

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
        if (candidatos == null || candidatos.length == 0) return new Pattern[0];

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
        if (candidatos == null || candidatos.length == 0) return new Pattern[0];

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

    private final Map<Integer, Integer> bufferFrequencia = new HashMap<>();
    private static final double LOG2 = Math.log(2);

    protected double calcularEntropiaPopulacao(Pattern[] populacao, int inicio) {
        if (populacao == null || populacao.length == 0) return 0.0;

        // LIMPA o mapa existente em vez de usar 'new HashMap<>()'
        bufferFrequencia.clear();
        int totalItens = 0;

        // Contabiliza a frequência de cada item na população ativa
        for (int i = inicio; i < populacao.length; i++) {
            if (populacao[i] != null) {
                for (Integer item : populacao[i].getItens()) {
                    // Mantemos o autoboxing do map, mas não recriamos a estrutura do mapa
                    bufferFrequencia.put(item, bufferFrequencia.getOrDefault(item, 0) + 1);
                    totalItens++;
                }
            }
        }

        if (totalItens == 0) return 0.0;

        double entropia = 0.0;

        for (Integer count : bufferFrequencia.values()) {
            double p = (double) count / totalItens;
            // Usa a constante pré-calculada LOG2
            entropia -= p * (Math.log(p) / LOG2);
        }

        return entropia;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(JSD.class.getName());

        String base = "pastas/Bases BIO 10/alon-pn-freq-2.CSV";
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
//        Pattern[] pks = SSDPmais.run(k, metricaAvaliacao, 0.5, 120);
//        Avaliador.imprimirRegras(pks, k);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);

//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\n\n\ntoreneio1");
//        JSD_V2 fixo = new JSD_V2();
//        Pattern[] pk = fixo.run(1, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(pk, k);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
//
//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\n\n\ntorneio2");
//        JSD_V2 fixo2 = new JSD_V2();
//        pk = fixo2.run(2, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(pk, k);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
//
//        Pattern.numeroIndividuosGerados = 0;
//        System.out.println("\n\n\n\ntorneio50");
//        JSD_V2 fixo50 = new JSD_V2();
//        pk = fixo50.run(50, 0.5, metricaAvaliacao, k);
//        Avaliador.imprimirRegras(pk, k);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);


//        System.out.println("\n\n\n\n1%");
        Pattern.numeroIndividuosGerados = 0;
        Pattern[] pk = Aleatorio.runDnp(metricaAvaliacao, k, 0.5,1000000, 0.5);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
//
//        System.out.println("\n\n\n\n10%");
//        Pattern.numeroIndividuosGerados = 0;
//
//        pk = Aleatorio.run(metricaAvaliacao, k, 0.5,1000000, 0.1);
//        Avaliador.imprimirRegras(pk, k);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
//
//
//        System.out.println("\n\n\n\n50%");
//        Pattern.numeroIndividuosGerados = 0;
//
//        pk = Aleatorio.run(metricaAvaliacao, k, 0.5,1000000, 0.5);
//        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);
//        Avaliador.imprimirRegras(pk, k);
    }
}
