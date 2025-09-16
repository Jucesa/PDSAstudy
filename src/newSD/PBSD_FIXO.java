    package newSD;

    import dp.Avaliador;
    import dp.Const;
    import dp.Pattern;
    import evolucionario.CRUZAMENTO;
    import evolucionario.INICIALIZAR;
    import evolucionario.SELECAO;

    import java.util.Arrays;
    import java.util.HashSet;

    public class PBSD_FIXO extends Threshold {
        public static Pattern[] run(int tamanhoTorneio, double similaridade, String tipoAvaliacao, int k) {

            Pattern[] Pk = new Pattern[k];
            Pattern[] P = null;

            //Inicializa Pk com indivíduos vazios
            for (int i = 0; i < Pk.length; i++) {
                Pk[i] = new Pattern(new HashSet<Integer>(), tipoAvaliacao);
            }

            //System.out.println("Inicializando população...");
            //Inicializa garantindo que P maior que Pk sempre! em bases pequenas isso nem sempre ocorre
            Pattern[] I = INICIALIZAR.D1(tipoAvaliacao);//P recebe população inicial
            if (I.length < k) {
                P = new Pattern[k];
                for (int i = 0; i < k; i++) {
                    if (i < I.length) {
                        P[i] = I[i];
                    } else {
                        P[i] = I[Const.random.nextInt(I.length - 1)];
                    }
                }
            } else {
                P = I;
            }
            Arrays.sort(P);
            SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);


            int limiar = P.length;
            int tamanhoPopulacao = P.length;
            int numeroGeracoesSemMelhoraPk = 0;

            for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {
                if (numeroReinicializacoes > 0) {
                    //System.out.println("\n\n\n\n\n-----Reiniciou: " + numeroReinicializacoes +"\n\n\n\n\n");
                    Avaliador.imprimirRegras(Pk, k);
                    P = INICIALIZAR.aleatorioD1_Pk(tipoAvaliacao, tamanhoPopulacao, I, Pk);

                    limiar = Math.max(1, (int) (P.length * 0.9));
                }

                while (numeroGeracoesSemMelhoraPk < 20) {

                    Pattern pai1 = P[SELECAO.torneioN(P, tamanhoTorneio, 0, limiar)];

                    Pattern pai2 = sortear(P, tamanhoTorneio, limiar);

                    Pattern paux = CRUZAMENTO.AND(pai1, pai2, tipoAvaliacao);

                    if (paux.getQualidade() >= P[limiar-1].getQualidade() && limiar > 1) {
                        limiar--;
                        P[limiar] = paux;
                    }

                    if(Pattern.numeroIndividuosGerados % P.length == 0){
                        int novosK = SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);
                        if(novosK == 0){
                            numeroGeracoesSemMelhoraPk++;
                        } else {
                            //System.out.println("Novos k: " + novosK);
                            numeroGeracoesSemMelhoraPk = 0;
                        }
                    }
                }
            }

            return Pk;
        }
    }
