package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDP;
import simulacoes.DPinfo;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Partition {

    /**Algoritmo baseado em torneio e partição da população com metodo AND combinando os individuos
     *@author Júlio Limeira
     * @param quantidadeTorneio
     * @param tentativasMelhoria
     * @param maxIndividuosGerados
     * @param tipoAvaliacao
     * @param k
     * @return Pattern[] - a população final
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, int maxIndividuosGerados, String tipoAvaliacao, int k) {

        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao);

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        int gerou = 0;
        int particao = P.length;

        while (gerou < maxIndividuosGerados && particao > quantidadeTorneio) {
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern individuo = P[index];

            for (int i = 0; i < tentativasMelhoria; i++) {
                Pattern paux = melhorarIndividuo(individuo, P, quantidadeTorneio, particao);
                gerou++;
                if (substituirIndividuo(P, paux, particao)) {
                    particao--;
                    break;
                }
                if(gerou % P.length == 0){
                    System.out.println("Partição: "+ particao);
                    avaliarPopulacao(P);
                }
            }
        }

        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));

        double overallConfidence = calculateOverallConfidence(P, k);
        if (overallConfidence < 0.8) {
            System.out.println("Warning: Overall confidence in top-k is below threshold! " + overallConfidence);
        }
        System.out.println("Overall Confidence: " + overallConfidence);
        System.out.println("Population Size: " + P.length);
        System.out.println("Gerou: " + gerou);
        return P;
    }

    private static Pattern melhorarIndividuo(Pattern individuo, Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern novoIndividuo;
        double aDouble = Const.random.nextDouble(0, 1);
        if (aDouble < (float) particao / P.length) {
            novoIndividuo = CRUZAMENTO.AND(individuo, P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao - 1)], individuo.getTipoAvaliacao());
        } else {
            novoIndividuo = CRUZAMENTO.AND(individuo, P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)], individuo.getTipoAvaliacao());
        }
        return novoIndividuo;
    }

    private static boolean substituirIndividuo(Pattern[] P, Pattern paux, int particao) {
        //if(paux.getQualidade() >= P[particao - 1].getQualidade()){
        if (SELECAO.ehRelevante(paux, P)) {
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

    private static void avaliarPopulacao(Pattern[] P) {
        double melhorQualidade = Arrays.stream(P).mapToDouble(Pattern::getQualidade).max().getAsDouble();

        double mediaQualidade = Arrays.stream(P)
                .mapToDouble(Pattern::getQualidade)
                .average()
                .orElse(0.0);

        double mediaTamanho =  Arrays.stream(P)
                .mapToDouble(pattern -> pattern.getItens().size()).average().getAsDouble();

        HashSet<Pattern> distintos = new HashSet<>(Arrays.asList(P));

        System.out.println("------ Avaliação da População ------");
        System.out.println("Melhor qualidade: " + melhorQualidade);
        System.out.println("Qualidade média: " + mediaQualidade);
        System.out.println("Tamanho médio dos indivíduos: " + mediaTamanho);
        System.out.println("Quantidade de individuos distintos: " + distintos.size());
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

        String base = bases[0];
        D.SEPARADOR = ",";

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
        int maxIndividuosGerados = 100000000;
        int quantidadeTorneio = 5;

        System.out.println("Algoritmo com Torneio: " + quantidadeTorneio);
        Pattern[] p = run(quantidadeTorneio, tentativasMelhoria, maxIndividuosGerados, metricaAvaliacao, k);

        System.out.println("Partition");
        Avaliador.imprimirRegras(p, k);

        System.out.println("-------------------------");

        System.out.println("SSDP");
        Pattern[] pS = SSDP.run(k, metricaAvaliacao, 3600);
        Avaliador.imprimirRegras(pS, k);
    }
}
