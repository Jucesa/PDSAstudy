package newSD;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.SELECAO;
import evolucionario.SSDP;
import simulacoes.DPinfo;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Threshold {


    protected static Pattern[] topK(Pattern[] P, int k){
        Pattern[] Pk = new Pattern[k];
        ordenaP(P);
        System.arraycopy(P, 0, Pk, 0, k);
        return Pk;
    }
    //filho tem que ser melhor que pior p acima do threshold
    //e melhor q os dois pais
    protected static boolean filhoPiorQuePais(Pattern pai1, Pattern pai2, Pattern filho){
        if (filho.getQualidade() < pai1.getQualidade() && filho.getQualidade() < pai2.getQualidade()) return true;
        return false;
    }

    protected static void ordenaP(Pattern[] P){
        Arrays.sort(P, (p1, p2) -> Double.compare(p2.getQualidade(), p1.getQualidade()));
    }

    protected static Pattern melhorarIndividuo(Pattern pai1, Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern novoIndividuo;
        Pattern pai2;

        double aDouble = Const.random.nextDouble(0, 1);

        if (aDouble < (float) particao / P.length) {
            if(particao > quantidadeTorneio){
                pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao - 1)];
            } else {
                pai2 = P[SELECAO.torneioN(P, particao-1, 0, particao - 1)];
            }
        } else {
            pai2 = P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)];
        }

        novoIndividuo = CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());

        return novoIndividuo;
    }

    protected static Pattern sortear(Pattern[] P, int quantidadeTorneio, int particao) {
        Pattern aux;

        double aDouble = Const.random.nextDouble(0, 0.99);

        if (aDouble < (float) particao / P.length) {
            if(particao > quantidadeTorneio && particao > 0){
                aux = P[SELECAO.torneioN(P, quantidadeTorneio, 0, particao-1)];
            } else {
                aux = P[0];
            }
        } else {
            aux = P[SELECAO.torneioN(P, quantidadeTorneio, particao, P.length - 1)];
        }

        return aux;
    }


    protected static Pattern melhorarIndividuo(Pattern pai1, Pattern[] P, int quantidadeTorneio) {
        Pattern pai2 = P[SELECAO.torneioN(P, quantidadeTorneio)];
        return CRUZAMENTO.AND(pai1, pai2, pai1.getTipoAvaliacao());
    }

    protected static boolean substituirIndividuo(Pattern[] P, Pattern paux, int particao) {
        if(paux.getQualidade() > P[particao - 1].getQualidade()){
        //if (SELECAO.ehRelevante(paux, P)) {
            P[particao - 1] = paux;
            return true;
        }
        return false;
    }

    protected static double calculateOverallConfidence(Pattern[] P, int k) {
        double totalConfidence = 0;
        for (int i = 0; i < k && i < P.length; i++) {
            totalConfidence += DPinfo.conf(P[i]);
        }
        return totalConfidence / k;
    }

    protected static void avaliarPopulacao(Pattern[] P) {
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

        String base = "pastas/bases/Bases BIO 10/alon-pn-freq-2.CSV";
        D.SEPARADOR = ",";

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }

        Const.random = new Random(Const.SEEDS[9]); //Seed
        D.GerarDpDn("p");

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_WRACC;
        int tentativasMelhoria = 20;
        int maxIndividuosGerados = 10000000;
        int quantidadeTorneio = 5;

        System.out.println("\n\n\n\nVarSortAceita");

        Pattern[] p = VarSortAceita.run(tentativasMelhoria, maxIndividuosGerados, metricaAvaliacao, k);

        Avaliador.imprimirRegras(p, k);

        System.out.println("\n\n\n\nVarSortNaoAceita");
        p = VarSortNaoAceita.run(tentativasMelhoria, maxIndividuosGerados, metricaAvaliacao, k);

        Avaliador.imprimirRegras(p, k);
    }
}
