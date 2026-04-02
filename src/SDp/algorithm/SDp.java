package SDp.algorithm;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import sd.Aleatorio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public abstract class SDp {
    protected DiversityManager diversity = new DiversityManager();

    protected Pattern[] setupInitialState(int k, Pattern[] Pk, Pattern[] I, String tipoAvaliacao) {
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        Arrays.sort(I);

        if (I.length < k) {
            Pattern[] P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                P[i] = (i < I.length) ? I[i] : I[Const.random.nextInt(I.length)];
            }
            return P;
        }
        return I;
    }
    protected abstract int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio);
    protected boolean pTemGanho(Pattern filho, double qualidadeMelhorPai, double MIN_GANHO_RELATIVO) {
        return filho.getQualidade() > (qualidadeMelhorPai * (1.0 + MIN_GANHO_RELATIVO));
    }

    //records para comunicacao em diferentes modulos
    protected record ReinicializacaoResult(
            Pattern[] P,
            int tamanhoTorneio,
            int limiar,
            int indexUltimaAval
    ) {}

    protected ReinicializacaoResult realizarReinicializacao(
            String tipoAvaliacao,
            int tamanhoPopulacao,
            Pattern[] I,
            Pattern[] Pk,
            int paramTorneio
    ) {
        // 1. Gera a nova população com reinjeção da elite (Pk)
        Pattern[] novoP = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);

        // 2. Define o novo limiar (90% da população)
        int novoLimiar = Math.max(1, (int) (novoP.length * 0.9));

        // 3. Retorna o pacote completo de variáveis resetadas
        return new ReinicializacaoResult(
                novoP,
                paramTorneio,
                novoLimiar,
                novoLimiar // indexUltimaAval recebe o mesmo valor do limiar inicial
        );
    }

    protected record ManutencaoResult(
            double numeroGeracoesSemMelhoraPk,
            int indexUltimaAval,
            boolean diversidadeSuficiente,
            int tamanhoTorneio
    ) {}

    protected ManutencaoResult processarManutencao(
            int limiar, int indexUltimaAval, Pattern[] P, Pattern[] Pk,
            double similaridade, double stagnation, int intervalo,
            double entropiaMin, int tamTorneio, int paramTorneio
    ) {
        boolean diversidade = true;
        int novoIndexUltimaAval = indexUltimaAval;
        double novaStagnation = stagnation;

        // 1. Atualização da Elite (Pk)
        if (limiar < indexUltimaAval) {
            Arrays.sort(P, limiar, indexUltimaAval);
            int novosK = SELECAO.salvandoRelevantesDPmais(Pk, Arrays.copyOfRange(P, limiar, indexUltimaAval), similaridade);

            // Se novos padrões entraram em P mas nenhum era relevante para Pk, conta estagnação
            novaStagnation = (novosK == 0) ? (novaStagnation + ((double) intervalo / 100)) : 0;
            novoIndexUltimaAval = limiar;
        } else {
            // Ninguém novo entrou em P, estagnação aumenta
            novaStagnation += ((double) intervalo / 100);
        }

        // 2. Verificação de Diversidade (Entropia)
        if (diversity.calcularEntropiaPopulacao(P, limiar) <= entropiaMin) {
            diversidade = false;
        }

        // 3. Ajuste de Parâmetros de Seleção
        int novoTamanhoTorneio = calcularTamanhoTorneio(tamTorneio, paramTorneio);

        return new ManutencaoResult(novaStagnation, novoIndexUltimaAval, diversidade, novoTamanhoTorneio);
    }

    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {
        Pattern[] Pk = new Pattern[k];
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Pattern[] P = setupInitialState(k, Pk, I ,tipoAvaliacao);

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

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);

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

                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio);
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

    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(SDp.class.getName());

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
