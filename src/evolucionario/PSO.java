package evolucionario;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Particle;
import dp.Pattern;


import java.util.ArrayList;
import java.util.Random;

public class PSO {
    private static int NUM_PARTICULAS;
    private static final int ITERACOES_MAX = 100;



    public static Pattern run(int dimensao, String tipoAvaliacao) {
        ArrayList<Particle> particulas = new ArrayList<>();
        Pattern globalBest = null;
        NUM_PARTICULAS = dimensao;
        // Inicialização das partículas
        for (int i = 0; i < NUM_PARTICULAS; i++) {
            Particle particula = new Particle(dimensao, tipoAvaliacao, i);
            particulas.add(particula);
            if (globalBest == null || particula.getQualidade() > globalBest.getQualidade()) {
                globalBest = particula.getPattern();
            }
        }

        // Loop principal do PSO binário
        for (int iteracao = 0; iteracao < ITERACOES_MAX; iteracao++) {
            for (Particle particula : particulas) {
                particula.updateVelocityAndPosition(globalBest);

                if (particula.getQualidade() > globalBest.getQualidade()) {
                    globalBest = particula.getPattern();
                }
            }
        }

        return globalBest;
    }


    public static void main(String[] args) {
        String diretorioBases = Const.CAMINHO_BASES;
        String base = diretorioBases + "/sua-base.csv";

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        D.SEPARADOR = ",";
        Const.random = new Random(Const.SEEDS[0]);

        int dimensao = D.numeroItensUtilizados;
        String tipoAvaliacao = Const.METRICA_Qg;

        Pattern melhorSubgrupo = run(dimensao, tipoAvaliacao);
        Avaliador.imprimirRegras(new Pattern[]{melhorSubgrupo}, 1);
    }
}
