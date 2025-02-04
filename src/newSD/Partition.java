package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDP;
import simulacoes.DPinfo;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Partition {

    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k, Pattern[] P0, int recursionDepth) {
        int maxRecursionDepth = 100;
        if (recursionDepth >= maxRecursionDepth) {
            System.out.println("Max recursion depth reached. Returning current population.");
            return P0;
        }

        Pattern[] P = inicializarPopulacao(tipoAvaliacao, P0, recursionDepth);
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        int gerou = 0;
        int tamanhoP = P.length;
        int particao = tamanhoP;

        while (gerou < maxIndividuosGerados && particao > quantidadeTorneio) {
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern individuo = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux = melhorarIndividuo(individuo, P, quantidadeTorneio, particao, tamanhoP, tipoAvaliacao);
                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
                gerou++;
            }
        }

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        double overallConfidence = calculateOverallConfidence(P, k);
        if (overallConfidence < 0.8) {
            System.out.println("Warning: Overall confidence in top-k is below threshold! " + overallConfidence);
            return run(quantidadeTorneio, tentativasMelhoria, maxIndividuosGerados, tipoAvaliacao, k, P, recursionDepth + 1);
        }
        System.out.println("Recursion Depth: " + recursionDepth);
        System.out.println("Overall Confidence: " + overallConfidence);
        System.out.println("Population Size: " + P.length);
        return P;
    }

    private static Pattern[] inicializarPopulacao(String tipoAvaliacao, Pattern[] P0, int recursionDepth) {
        return (recursionDepth == 0) ? INICIALIZAR.D1(tipoAvaliacao) : P0;
    }

    private static Pattern melhorarIndividuo(Pattern individuo, Pattern[] P, int quantidadeTorneio, int particao, int tamanhoP, String tipoAvaliacao) {
        HashSet<Integer> itemNovo = new HashSet<>(individuo.getItens());
        double aDouble = Const.random.nextDouble(0, 1);
        if (aDouble < (float) particao / tamanhoP) {
            itemNovo.add(SELECAO.torneioNparticao(P, quantidadeTorneio, 0, particao - 1));
        } else {
            itemNovo.add(SELECAO.torneioNparticao(P, quantidadeTorneio, particao, tamanhoP - 1));
        }
        return new Pattern(itemNovo, tipoAvaliacao);
    }

    private static boolean substituirIndividuo(Pattern[] P, Pattern paux, int particao) {
        if(paux.getQualidade() >= P[particao - 1].getQualidade()){
        //if (SELECAO.ehRelevante(paux, P)) {
            P[particao - 1] = paux;
            return true;
        }
        return false;
    }

    private static double calculateOverallConfidence(Pattern[] P, int k) {
        double totalConfidence = 0;
        for (int i = 0; i < k && i < P.length; i++) {
            totalConfidence += DPinfo.conf(P[i]);
        }
        return totalConfidence / k;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(Partition.class.getName());

        String diretorioBases = Const.CAMINHO_BASES;
        String social = "/Humanitie and ssocial sciences";
        String bio140 = "/Bases BIO 140";
        String bioinformatica = "/Bioinformatic";
        String texto = "/Text mining";

        String[] bases = {diretorioBases+bioinformatica+"/alon-clean50-pn-width-2.csv",
                diretorioBases+social+"/ENEM2014-NOTA-100K.csv",
                diretorioBases+"/matrixBinaria-Global-100-p.csv",
                diretorioBases+texto+"/matrixBinaria-ALL-TERMS-59730-p.csv"
        };

        String base = "pastas/bases/Text mining/matrixBinaria-Global-100-p.csv";
        D.SEPARADOR = ","; //separator database
        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        Const.random = new Random(Const.SEEDS[3]); //Seed
        D.GerarDpDn("p");

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_WRACC;
        int tentativasMelhoria = 20;
        int maxIndividuosGerados = 100000;
        int quantidadeTorneio = 5;

        System.out.println("Algoritmo com Torneio: " + quantidadeTorneio);
        Pattern[] P = new Pattern[0];
        Pattern[] p = run(quantidadeTorneio, tentativasMelhoria, maxIndividuosGerados, metricaAvaliacao, k, P, 0);

        System.out.println("Partition");
        Avaliador.imprimirRegras(p, k);

        System.out.println("-------------------------");

        System.out.println("SSDP");
        Pattern[] pS = SSDP.run(k, metricaAvaliacao, 3600);
        Avaliador.imprimirRegras(pS, k);
    }
}
