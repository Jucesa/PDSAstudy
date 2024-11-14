package sd;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.INICIALIZAR;
import evolucionario.SELECAO;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TPSD {
    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo (WRacc valoriza subgrupos menores enquanto Qg, maiores
     * @return Pattern[] - conjunto final de individuos melhorados
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, String tipoAvaliacao) {
        Pattern[] P = INICIALIZAR.D1(tipoAvaliacao); // inicializa P com todos os indivíduos de 1D

        int naoMudou = 0;
        while (naoMudou < 3) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // Atualiza o indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    /**
     *@author Jucesa
     * @param quantidadeTorneio int - quantidade de individos selecionados no torneio
     * @param tentativasMelhoria int - quantidade de vezes que o algoritmo tenta melhorar um individuo
     * @param tipoAvaliacao String - tipo de avaliação utilizado para qualificar indivíduo (WRacc valoriza subgrupos menores enquanto Qg, maiores
     * @param dimensaoMaxInicial int - dimensao maxima dos itens inicializados em P0
     * @return Pattern[] - conjunto final de individuos melhorados
     */
    public static Pattern[] run(int quantidadeTorneio, int tentativasMelhoria, String tipoAvaliacao, int dimensaoMaxInicial) {
        Pattern[] P = INICIALIZAR.aleatorio1_D(tipoAvaliacao, dimensaoMaxInicial, D.numeroItensUtilizados); // inicializa P com todos os indivíduos de 1D

        int naoMudou = 0;
        while (naoMudou < 3) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // Atualiza o indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    public static Pattern[] runKillTheWeek(int quantidadeTorneio, int tentativasMelhoria, int falhasAteParada, int dimensaoMaxInicial, String tipoAvaliacao) {
        Pattern[] P = INICIALIZAR.aleatorio1_D(tipoAvaliacao, dimensaoMaxInicial, D.numeroItensUtilizados); // inicializa P com todos os indivíduos de 1D

        int naoMudou = 0;
        while (naoMudou < falhasAteParada) {
            // Seleciona o melhor índice entre quantidadeTorneio índices
            int index = SELECAO.torneioN(P, quantidadeTorneio);
            Pattern melhorP = P[index];

            boolean melhorou = false; // Flag para verificar se houve melhoria

            // Tenta melhorar o indivíduo selecionado
            for (int i = 0; i < tentativasMelhoria; i++) {
                HashSet<Integer> itemNovo = new HashSet<>(melhorP.getItens()); // Adiciona itens existentes de p
                itemNovo.add(SELECAO.torneioN(P, quantidadeTorneio)); // Adiciona um novo índice aleatório

                Pattern paux = new Pattern(itemNovo, tipoAvaliacao);
                if (paux.getQualidade() > melhorP.getQualidade()) {
                    melhorP = paux;
                    melhorou = true; // Marca que houve uma melhoria
                }
            }

            // atualiza o indivíduo na população
            P[index] = melhorP;

            // Verifica se houve alguma melhoria neste ciclo
            if (melhorou) {
                naoMudou = 0; // Reseta o contador de tentativas sem melhoria
            } else {
                naoMudou++; // Incrementa se o indivíduo não foi melhorado
            }
        }
        return P;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Logger logger = Logger.getLogger(TPSD.class.getName());

        String diretorioBases = Const.CAMINHO_BASES;
        String[] bases = {diretorioBases+"/alon-clean50-pn-width-2.csv",
                diretorioBases+"/ENEM2014.csv",
                diretorioBases+"/matrixBinaria-Global-100-p.csv"};
        String base = bases[0];

        try {
            D.CarregarArquivo(base, D.TIPO_CSV);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, e.getMessage());
            return;
        }
        D.SEPARADOR = ","; //separator database
        Const.random = new Random(Const.SEEDS[0]); //Seed

        //Parameters of the algorithm
        int k = 10;
        String metricaAvaliacao = Const.METRICA_Qg;
        D.GerarDpDn("p");
        Pattern[] p = runKillTheWeek(20, 5, 10,2, metricaAvaliacao);
        Avaliador.imprimirRegras(p, k);
    }
}
