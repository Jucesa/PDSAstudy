package newSD;

public class PBSD_VAR extends PBSD_Base {

    @Override
    protected int calcularTamanhoTorneio(int tamanhoTorneio, int saltoTorneio) {
        return tamanhoTorneio + saltoTorneio;
    }
}
