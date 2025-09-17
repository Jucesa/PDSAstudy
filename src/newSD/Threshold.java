package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;
import sd.Aleatorio;
import sd.SD;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Threshold {

    /**
     * Mede a diversidade de um conjunto de padrões.
     *
     * Diversidade = 1 - média das similaridades entre os pares de padrões
     *
     * @param P - conjunto de padrões
     * @param k - número de padrões considerados
     * @param metricaSimilaridade - métrica usada (Const.SIMILARIDADE_JACCARD ou SOKAL_MICHENER)
     * @return valor da diversidade (0 = nada diverso, 1 = totalmente diverso)
     */
    public static double avaliarDiversidade(Pattern[] P, int k, String metricaSimilaridade) {
        if (k <= 1) {
            return 0.0; // não existe diversidade com menos de 2 padrões
        }

        double somaSimilaridades = 0.0;
        int numPares = 0;

        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k; j++) {
                somaSimilaridades += Avaliador.similaridade(P[i], P[j], metricaSimilaridade);
                numPares++;
            }
        }

        if (numPares == 0) return 0.0;

        double mediaSimilaridade = somaSimilaridades / numPares;
        return 1.0 - mediaSimilaridade; // diversidade é o inverso da similaridade média
    }

    protected static Pattern sortear(Pattern[] P, int quantidadeTorneio, int limiar) {
        Pattern aux;

        // Sorteio r ~ U(0,1)
        double r = Const.random.nextDouble();

        // Probabilidade teórica
        double Pth = (double) limiar / (P.length);

        if (r < Pth) {
            // Seleção acima do limiar: faixa 0..limiar-1
            aux = P[SELECAO.torneioN(P, quantidadeTorneio, 0, limiar - 1)];
        } else {
            // Seleção abaixo do limiar: faixa limiar..P.length-1
            aux = P[SELECAO.torneioN(P, quantidadeTorneio, limiar, P.length - 1)];
        }

        return aux;
    }
    protected static double mediaQualidade(Pattern[] P){
        return Arrays.stream(P)
                .mapToDouble(Pattern::getQualidade)
                .average()
                .orElse(0.0);
    }

    public static Map<Integer, Integer> calcularFrequenciaItens(Pattern[] P) {
        Map<Integer, Integer> frequencia = new HashMap<>();
        if (P == null) return frequencia;

        for (Pattern padrao : P) {
            for (Integer item : padrao.getItens()) { // assumindo que Pattern tem getItens()
                frequencia.put(item, frequencia.getOrDefault(item, 0) + 1);
            }
        }
        return frequencia;
    }

    protected static void avaliarPopulacao(Pattern[] P, int torneio, int threshold, int numeroIndividuos) {
        System.out.println();


        double melhorQualidade = Arrays.stream(P).mapToDouble(Pattern::getQualidade).max().getAsDouble();

        double mediaQualidade = Arrays.stream(P)
                .mapToDouble(Pattern::getQualidade)
                .average()
                .orElse(0.0);

        double mediaTamanho =  Arrays.stream(P)
                .mapToDouble(pattern -> pattern.getItens().size()).average().getAsDouble();

        System.out.println("------ Avaliação da População ------");
        System.out.println("Melhor qualidade: " + melhorQualidade);
        System.out.println("Qualidade média: " + mediaQualidade);
        System.out.println("Tamanho médio dos indivíduos: " + mediaTamanho);
    }



    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Threshold.class.getName());

        String base = "pastas/bases/alon-pn-freq-2.CSV";
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
        String metricaAvaliacao = Const.METRICA_WRACC;
        int quantidadeTorneio = 5;
        int passoTorneio = 5;

        System.out.println("\n\n\n\nFIXO");
        Pattern[] pk = PBSD_FIXO.run(quantidadeTorneio, 0.5, metricaAvaliacao, k, false);
        Avaliador.imprimirRegras(pk, k);


        System.out.println("\n\n\nSSDP+");
        pk = SSDPmais.run(k, metricaAvaliacao, 0.5, 600);
        Avaliador.imprimirRegras(pk, k);

        System.out.println("\n\n\nSD");
        SD sd = new SD();
        double min_suport = Math.sqrt(D.numeroExemplosPositivo) / D.numeroExemplos;
        pk = sd.run(min_suport, 2*k, metricaAvaliacao, k, 60);
        Avaliador.imprimirRegras(pk, k);

        System.out.println("\n\n\nAleatorio");
        pk = Aleatorio.runNtentativas(metricaAvaliacao, k, 1000000, 10);
        Avaliador.imprimirRegras(pk, k);
    }
}
