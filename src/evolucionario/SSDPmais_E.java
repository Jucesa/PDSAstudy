package evolucionario;

import dp.Const;
import dp.Pattern;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

public class SSDPmais_E {

    public static Pattern[] run(int k, String tipoAvaliacao, double similaridade, double maxTimeSegundos) throws IOException {
        long t0 = System.currentTimeMillis(); //Initial time

        Pattern[] Pk = new Pattern[k];
        Pattern[] P;

        //Inicializa Pk com indivíduos vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        //Inicializa garantindo que P maior que Pk sempre! em bases pequenas isso nem sempre ocorre
        Pattern[] Paux = INICIALIZAR.D1(tipoAvaliacao); //P recebe população inicial

        if (Paux.length < k) {
            P = new Pattern[k];
            for (int i = 0; i < k; i++) {
                if (i < Paux.length) {
                    P[i] = Paux[i];
                } else {
                    P[i] = Paux[Const.random.nextInt(Paux.length - 1)];
                }
            }
        } else {
            P = Paux;
        }

        Arrays.sort(P);


        SELECAO.salvandoRelevantesDPmais(Pk, P, similaridade);

        int numeroGeracoesSemMelhoraPk = 0;
        int indiceGeracoes = 1;

        //Laço do AG
        Pattern[] Pnovo;
        Pattern[] PAsterisco;

        int tamanhoPopulacao = P.length;

        for (int numeroReinicializacoes = 0; numeroReinicializacoes < 3; numeroReinicializacoes++) {//Controle número de reinicializações

            if (numeroReinicializacoes > 0) {
                P = INICIALIZAR.aleatorio1_D_Pk(tipoAvaliacao, tamanhoPopulacao, Pk);
            }

            double mutationTax = 0.4; //Mutação inicia em 0.4. Crossover é sempre 1-mutationTax.

            while (numeroGeracoesSemMelhoraPk < 3) {

                if (indiceGeracoes == 1) {
                    Pnovo = CRUZAMENTO.ANDduasPopulacoes(P, P, tipoAvaliacao);
                    indiceGeracoes++;
                } else {
                    Pnovo = CRUZAMENTO.uniforme2PopE(P, mutationTax, tipoAvaliacao);
                }

                PAsterisco = SELECAO.selecionarMelhores(P, Pnovo);
                P = PAsterisco;

                int novosK = SELECAO.salvandoRelevantesDPmais(Pk, PAsterisco, similaridade);//Atualizando Pk e coletando número de indivíduos substituídos
                // Registrar Pk atualizado

                double tempo = (System.currentTimeMillis() - t0) / 1000.0; //time
                if (maxTimeSegundos > 0 && tempo > maxTimeSegundos) {
                    return Pk;
                }


                //Definição automática de mutação de crossover
                if (novosK > 0 && mutationTax > 0.0) {//Aumenta cruzamento se Pk estiver evoluindo e se mutação não não for a menos possível.
                    mutationTax -= 0.2;
                } else if (novosK == 0 && mutationTax < 1.0) {//Aumenta mutação caso Pk não tenha evoluido e mutação não seja maior que o limite máximo.
                    mutationTax += 0.2;
                }
                //Critério de parada: 3x sem evoluir Pk com taxa de mutação 1.0
                if (novosK == 0 && mutationTax == 1.0) {
                    numeroGeracoesSemMelhoraPk++;

                } else {
                    numeroGeracoesSemMelhoraPk = 0;
                }
            }

            numeroGeracoesSemMelhoraPk = 0;
        }
        return Pk;
    }

}
