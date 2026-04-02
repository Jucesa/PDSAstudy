package SDp.algorithm;

import dp.Const;
import dp.Pattern;
import evolucionario.SELECAO;

public class SelectionManager {
    public static Pattern[] selecionarPaisD1DnQuad(Pattern[] P, int tamanhoTorneio, int limiar, int limiteInferiorStop){
        double razaoLimiar = (double) (limiar - limiteInferiorStop) / (P.length - limiteInferiorStop);
        double Pth = Math.pow(razaoLimiar, 2);
        double r = Const.random.nextDouble();

        Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
        Pattern pai2 = (r < Pth) ? P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)]
                : P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
        Pattern[] pais = new Pattern[2];
        pais[0] = pai1;
        pais[1] = pai2;
        return pais;
    }

    public static Pattern[] selecionarPaisD1DnLinear(Pattern[] P, int tamanhoTorneio, int limiar) {
        double Pth = (double) limiar / P.length;
        Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
        Pattern pai2 = (Const.random.nextDouble() < Pth) ?
                P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)] :
                P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
        return new Pattern[]{pai1, pai2};
    }

    public static Pattern[] selecionarPaisD1DnQuad(Pattern[] P, int tamanhoTorneio, int limiar){
        double razaoLimiar = (double) (limiar) / (P.length);
        double Pth = Math.pow(razaoLimiar, 2);
        double r = Const.random.nextDouble();

        Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];
        Pattern pai2 = (r < Pth) ? P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)]
                : P[SELECAO.torneioN(P, tamanhoTorneio, limiar, P.length)];
        Pattern[] pais = new Pattern[2];
        pais[0] = pai1;
        pais[1] = pai2;
        return pais;
    }
}
