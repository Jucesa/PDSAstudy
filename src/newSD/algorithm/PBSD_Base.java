package newSD.algorithm;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.CRUZAMENTO;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;
import evolucionario.SSDPmais;
import newSD.logging.PatternTracker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public abstract class PBSD_Base {


    //mapear eventos
    //Evento
    // 1 - AND_ITEMxITEM_MELHORA
    // 2 - AND_ITEMxSUBGRUPO_MELHORA
    // 3 - AND_ITEMxITEM
    // 4 - AND_ITEMxSUBGRUPO

    /**
     * Mét0d0 abstrato para determinar o tamanho do torneio na geração atual.
     * Cada implementação (FIXO ou VARIÁVEL) define sua lógica.
     */
    protected abstract int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP);

    public Pattern[] run(int paramTorneio, double similaridade, String tipoAvaliacao, int k) throws IOException {

        Pattern[] P;

        Pattern[] Pk = new Pattern[k];

        // Inicializa Pk com indivíduos vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // População inicial
        Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);
        Arrays.sort(I);
        PatternTracker tracker = new PatternTracker(I, I.length/100, Const.SAIDA_LOG, D.nomeBase, "JSD", k);


        if (I.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < I.length) P[i] = I[i];
                else P[i] = I[Const.random.nextInt(I.length - 1)];
            }
        } else {
            P = I;
        }

        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade, tracker);

        int limiar = P.length;
        int tamanhoPopulacao = P.length;
        int numeroGeracoesSemMelhoraPk = 0;
        int tamanhoTorneio = 2;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);
                tamanhoTorneio = paramTorneio;
                limiar = Math.max(1, (int) (P.length * 0.9));
            }

            tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);

            while (numeroGeracoesSemMelhoraPk < 1000 && limiar > 0) {
                Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                Pattern pai2;
                String operador;

                // Sorteio r ~ U(0,1)
                double r = Const.random.nextDouble();

                // Probabilidade teórica
                double Pth = (double) limiar / (P.length);
                if (r < Pth) {
                    // Seleção acima do limiar: faixa 0..limiar-1
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
                    operador = "AND_ITEMxITEM";
                } else {
                    // Seleção abaixo do limiar: faixa limiar..P.length-1
                    pai2 = P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
                    operador = "AND_ITEMxPATTERN";
                }

                //operador de cruzamento
                Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                //tracking da genealogia
                List<Pattern> idsPais = Arrays.asList(pai1, pai2);

                //se paux melhor que o pior item, substitui
                if (paux.getQualidade() >= P[limiar - 1].getQualidade() && limiar > 1) {
                    tracker.registrar(paux, operador, idsPais);
                    P[limiar - 1] = paux;
                    limiar--;
                } else if (Pattern.numeroIndividuosGerados % 50 == 0) { //se foram criados "lixos" suficientes, registra
                    tracker.registrar(paux, operador+"_lixo", idsPais);
                }

                if (Pattern.numeroIndividuosGerados % P.length == 0) {
                    Arrays.sort(P, limiar, P.length);

                    int novosK = SELECAO.salvandoRelevantesDPmais(Pk,
                            Arrays.copyOfRange(P, limiar, P.length),
                            similaridade, tracker);
                    //System.out.println(novosK);
                    if (novosK == 0) numeroGeracoesSemMelhoraPk++;
                    else numeroGeracoesSemMelhoraPk = 0;

                    // Atualiza tamanho do torneio usando mét0do polimórfico
                    tamanhoTorneio = calcularTamanhoTorneio(tamanhoTorneio, paramTorneio, tamanhoPopulacao);
                }
            }
            Arrays.sort(P, limiar, P.length);

            int novosK = SELECAO.salvandoRelevantesDPmais(Pk,
                    Arrays.copyOfRange(P, limiar, P.length),
                    similaridade, tracker);
        }

        Arrays.sort(P);
        SELECAO.salvandoRelevantesDPmais(Pk,
                P,
                similaridade, tracker);

        tracker.close();
        return Pk;
    }

    public static void main(String[] args) throws IOException {
        Logger logger = Logger.getLogger(PBSD_Base.class.getName());

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
        int passoTorneio = 5;

        System.out.println("\n\n\n\nFIXO");
        PBSD_FIXO fixo = new PBSD_FIXO();
        Pattern[] pk = fixo.run(quantidadeTorneio, 0.5, metricaAvaliacao, k);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);

        System.out.println("\n\n\n\nVAR");
        PBSD_VAR var = new PBSD_VAR();
        pk = fixo.run(quantidadeTorneio, 0.5, metricaAvaliacao, k);
        Avaliador.imprimirRegras(pk, k);
        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);

        System.out.println("\nSSDP");
        pk = SSDPmais.run(k, metricaAvaliacao, 0.5, 120);
        Avaliador.imprimirRegras(pk, k);

        System.out.println("Testes: " + Pattern.numeroIndividuosGerados);

    }
}
