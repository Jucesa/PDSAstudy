package pso.pso2;

import java.util.HashSet;
import java.util.Set;

import dp.Pattern;

public class Particle {
    private Pattern pattern;
    private double[] velocity;
    private Pattern pbestPattern;
    private double pbestQuality;

    public Particle(Pattern pattern, double[] velocity) {
        this.pattern = pattern;
        this.velocity = velocity;
        this.pbestPattern = new Pattern(pattern);
        this.pbestQuality = pattern.getQualidade();
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Set<Integer> getItens() {
        return pattern.getItens();
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    public Pattern getPbestPattern() {
        return pbestPattern;
    }

    public double getPbestQuality() {
        return pbestQuality;
    }

    public void updatePbest() {
        if (pattern.getQualidade() > pbestQuality) {
            pbestPattern = new Pattern(pattern);
            pbestQuality = pattern.getQualidade();
        }
    }
    // Removido gbest estático. Agora o gbest deve ser gerenciado por cada instância do BPSO.
}
