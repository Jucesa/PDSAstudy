package newSD;

public class PBSD_VAR extends PBSD_Base {

    @Override
    protected int calcularTamanhoTorneio(int geracoes, int tamanhoPopulacao, int saltoTorneio) {
        // começa em 2 e aumenta a cada ciclo em saltoTorneio
        int tamanho = 2 + (geracoes / tamanhoPopulacao) * saltoTorneio;
        return Math.min(tamanho, tamanhoPopulacao);
    }
}
