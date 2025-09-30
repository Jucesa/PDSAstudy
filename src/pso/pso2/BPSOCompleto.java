package pso.pso2;

import dp.Pattern;

// Variação: BPSO com todos os mecanismos (original)
public class BPSOCompleto extends BPSO {
    public BPSOCompleto(int numParticles, int numIterations, int dimension, double w, double c1, double c2) {
        super(numParticles, numIterations, dimension, w, c1, c2);
    }

    @Override
    public Pattern[] run(int k) {
        return super.run(k); // Executa o BPSO completo
        // Todas as chamadas a Particle.getGbestPattern() e Particle.updateGbest() já são herdadas de BPSO, pois herdamos updateGbest e gbestPattern.
        // Se houver chamadas diretas, substitua por métodos/variáveis locais.
        // ...existing code...
    }
}
