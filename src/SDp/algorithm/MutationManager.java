package SDp.algorithm;

import dp.Const;
import dp.Pattern;
import java.util.HashSet;
import java.util.Iterator;

public class MutationManager {

    // --- PUBLIC "TRY" METHODS (Check probability then call logic) ---

    public static Pattern trySmartDrop(Pattern p, double probability, String tipo) {
        // Run only if roll is WITHIN probability AND size is valid
        if (Const.random.nextDouble() < probability && p.getItens().size() > 2) {
            return applySmartDrop(p, tipo);
        }
        return p;
    }

    public static Pattern tryEliteSwap(Pattern p, Pattern[] P, int limiar, double probability, String tipo) {
        if (Const.random.nextDouble() < probability && p.getItens().size() >= 2) {
            return applyEliteSwap(p, P, limiar, tipo);
        }
        return p;
    }

    /**
     * Range Dispatcher: 0.9-1.0 (Smart Drop), 0.8-0.9 (Elite Swap)
     */
    public static Pattern applyStrategicMutation(Pattern p, Pattern[] P, int limiar, String tipo) {
        double r = Const.random.nextDouble();

        if (r >= 0.90) { // Top 10%
            return p.getItens().size() > 2 ? applySmartDrop(p, tipo) : p;
        } else if (r >= 0.80) { // Next 10%
            return p.getItens().size() >= 2 ? applyEliteSwap(p, P, limiar, tipo) : p;
        }
        return p;
    }

    public static Pattern applySmartDrop(Pattern paux, String tipo) {
        boolean melhorou = true;
        while (melhorou && paux.getItens().size() > 2) {
            melhorou = false;
            Integer itemParaRemover = null;
            double melhorQualidade = paux.getQualidade() * 0.98;
            Pattern melhorPadrao = paux;

            for (Integer item : paux.getItens()) {
                HashSet<Integer> tempItens = new HashSet<>(paux.getItens());
                tempItens.remove(item);
                Pattern testePadrao = new Pattern(tempItens, tipo);

                if (testePadrao.getQualidade() >= melhorQualidade) {
                    melhorQualidade = testePadrao.getQualidade();
                    melhorPadrao = testePadrao;
                    itemParaRemover = item;
                }
            }
            if (itemParaRemover != null) {
                paux = melhorPadrao;
                melhorou = true;
            }
        }
        return paux;
    }

    public static Pattern applyEliteSwap(Pattern p, Pattern[] P, int limiar, String tipo) {
        HashSet<Integer> itensAtuais = new HashSet<>(p.getItens());

        // 1. Remove random item
        int itemRemoverIndex = Const.random.nextInt(itensAtuais.size());
        int currentIndex = 0;
        for (Iterator<Integer> it = itensAtuais.iterator(); it.hasNext(); ) {
            it.next();
            if (currentIndex == itemRemoverIndex) {
                it.remove();
                break;
            }
            currentIndex++;
        }

        // 2. Gene theft
        int indiceDoador = Const.random.nextInt(Math.max(1, limiar));
        Pattern doador = P[indiceDoador];

        if (!doador.getItens().isEmpty()) {
            int geneDoadorIndex = Const.random.nextInt(doador.getItens().size());
            Integer geneNovo = null;
            int idx = 0;
            for (Integer g : doador.getItens()) {
                if (idx == geneDoadorIndex) { geneNovo = g; break; }
                idx++;
            }

            if (geneNovo != null) {
                itensAtuais.add(geneNovo);
                Pattern pauxSwap = new Pattern(itensAtuais, tipo);
                if (pauxSwap.getQualidade() >= p.getQualidade() * 0.9) {
                    return pauxSwap;
                }
            }
        }
        return p;
    }
}