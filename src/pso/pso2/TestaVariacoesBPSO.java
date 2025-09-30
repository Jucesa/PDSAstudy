package pso.pso2;

import dp.Pattern;

public class TestaVariacoesBPSO {
    public static void main(String[] args) {
        // Carregar base de dados antes de rodar qualquer PSO
        String caminhoBase = "pastas/basesBrunoExperimento/alon-clean50-pn-width-2.CSV";
        try {
            dp.D.CarregarArquivo(caminhoBase, dp.D.TIPO_CSV);
            dp.D.GerarDpDn("p");
        } catch (Exception e) {
            System.err.println("Erro ao carregar base: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Parâmetros de exemplo
        int numParticles = 30;
        int numIterations = 100;
        int dimension = dp.D.numeroAtributos; // agora pega da base
        double w = 0.7;
        double c1 = 2.0;
        double c2 = 2.0;
        int k = 10;

        // Chama cada variação
        System.out.println("BPSO Completo");
        BPSOCompleto completo = new BPSOCompleto(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resCompleto = completo.run(k);
        System.out.println("Melhor qualidade: " + resCompleto[0].getQualidade());

        System.out.println("BPSO Adaptativo");
        BPSOAdaptativo adapt = new BPSOAdaptativo(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resAdapt = adapt.run(k);
        System.out.println("Melhor qualidade: " + resAdapt[0].getQualidade());

        System.out.println("BPSO Janela");
        BPSOJanela janela = new BPSOJanela(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resJanela = janela.run(k);
        System.out.println("Melhor qualidade: " + resJanela[0].getQualidade());

        System.out.println("BPSO Diversidade");
        BPSODiversidade diversidade = new BPSODiversidade(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resDiversidade = diversidade.run(k);
        System.out.println("Melhor qualidade: " + resDiversidade[0].getQualidade());

        System.out.println("BPSO HillClimb");
        BPSOHillClimb hill = new BPSOHillClimb(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resHill = hill.run(k);
        System.out.println("Melhor qualidade: " + resHill[0].getQualidade());

        System.out.println("BPSO Elitismo");
        BPSOElitismo elitismo = new BPSOElitismo(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resElitismo = elitismo.run(k);
        System.out.println("Melhor qualidade: " + resElitismo[0].getQualidade());

        System.out.println("BPSO Crossover");
        BPSOCrossover crossover = new BPSOCrossover(numParticles, numIterations, dimension, w, c1, c2);
        Pattern[] resCrossover = crossover.run(k);
        System.out.println("Melhor qualidade: " + resCrossover[0].getQualidade());
    }
}
