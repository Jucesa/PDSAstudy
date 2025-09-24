package newSD;

public class PBSD_FIXO extends PBSD_Base {

    @Override
    protected int calcularTamanhoTorneio(int geracoes, int tamanhoPopulacao, int tamanhoFixo) {
        // sempre retorna o valor fixo
        return tamanhoFixo;
    }
}
