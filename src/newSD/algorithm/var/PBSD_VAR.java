package newSD.algorithm.var;

import newSD.algorithm.JSD;

public class PBSD_VAR extends JSD {

    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio, int tamanhoP) {
        int novo = tamanhoTorneio + saltoTorneio;
        int max = Math.max(2, tamanhoP / 10);
        return Math.min(novo, max);
    }
}
