/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sd;

import dp.Avaliador;
import dp.Const;
import dp.D;
import dp.Pattern;
import evolucionario.SELECAO;

import java.util.*;

/**
 *
 * @author tarcisio_pontes
 */
public class Aleatorio {


    public static Pattern[] run(String tipoAvaliacao, int k, double similaridade, int maximoTentativas, double p){
        int[] itens = D.itensUtilizados;
        Pattern[] Pk =  new Pattern[k];

        // Inicializa Pk com padrões vazios
        for (int i = 0; i < Pk.length; i++) {
            Pk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }

        // 1. Buffer reutilizável (evita criar milhões de HashSets)
        HashSet<Integer> bufferCandidato = new HashSet<>();
        double r;

        for(int i = 0; i < maximoTentativas; i++){
            // Limpa o buffer para reuso (operação barata)
            bufferCandidato.clear();

            // Preenche o buffer
            for (int iten : itens) {
                r = Const.random.nextDouble();
                if (r < p) {
                    bufferCandidato.add(iten);
                }
            }

            // Evita processar vazio
            if(bufferCandidato.isEmpty()) continue;

            // 2. Cria a "Sonda": Um Pattern temporário que aponta para o buffer.
            // O objetivo dele é apenas calcular a qualidade (getQualidade).
            // Nota: Assumimos que o construtor do Pattern calcula a qualidade imediatamente.
            Pattern sonda = new Pattern(bufferCandidato, tipoAvaliacao);

            // 3. OTIMIZAÇÃO DE MEMÓRIA (Gatekeeper):
            // Verificamos se vale a pena tentar salvar.
            // Só entramos no IF se a sonda for melhor que o pior elemento atual de Pk.
            // (Assumindo que Pk está ordenado do melhor para o pior e o último é o pior)
            if (sonda.getQualidade() > Pk[Pk.length - 1].getQualidade()) {

                // 4. ALOCAÇÃO TARDIA (Lazy Allocation):
                // Agora sim, gastamos memória. Criamos um novo HashSet independente do buffer.
                HashSet<Integer> itensPermanentes = new HashSet<>(bufferCandidato);

                // Criamos o candidato oficial com os itens clonados
                Pattern candidatoOficial = new Pattern(itensPermanentes, tipoAvaliacao);

                // Chama o mét0do de salvamento original (sem alterações nele)
                SELECAO.salvandoRelevanteDPmaisSingle(Pk, candidatoOficial, similaridade);

                // Nota: O mét0do SELECAO deve reordenar Pk para que Pk[Pk.length-1]
                // continue sendo o pior na próxima iteração.
            }

            // Se não entrou no IF, 'sonda' é descartada e 'bufferCandidato' é limpo na próxima volta.
            // Nenhuma memória permanente foi criada para o candidato ruim.
        }

        return Pk;
    }
    //Máximo até dimensão 3
    public static Pattern [] run(String tipoAvaliacao, int k, int tempoMaximoMinutos){
        Pattern[] DP1k = new Pattern[k];
        Pattern[] DP2k = new Pattern[k];
        Pattern[] DP3k = new Pattern[k];
        
        //Itens de 1D
        int quantidadeTestes = 0;
        long t0Simulacao = System.currentTimeMillis(); //Utilizado no critério de parada.
        
        long t0Dimensao1 = System.currentTimeMillis();          
        for(int i = 0; i < D.numeroItensUtilizados; i++){
            HashSet<Integer> itens = new HashSet<>();
            itens.add(i);
            Pattern p = new Pattern(itens, tipoAvaliacao);
            quantidadeTestes++;
            if(i == k){
                Arrays.sort(DP1k);
            }
            if(i < k){
                DP1k[i] = p;
            }            
            else if(p.getQualidade() > DP1k[DP1k.length-1].getQualidade()){
                if(SELECAO.ehRelevante(p, DP1k)){
                    DP1k[DP1k.length-1] = p;
                    Arrays.sort(DP1k);
                }
            }
        }
        double tempoDimensao1 = (double) (System.currentTimeMillis() - t0Dimensao1)/1000.0;
        Avaliador.imprimir(DP1k, k);
        System.out.println("\n1D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP1k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP1k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao1);

        


        //Itens de 2D
        long t0Dimensao2 = System.currentTimeMillis();
        quantidadeTestes = 0;
        int indiceKprimeiros = 0;
        System.out.print("\nD2-i: ");
        for(int i = 0; i < D.numeroItensUtilizados; i++){            
            if(i % 100 == 0){
                System.out.print(i+",");
            }
            
            for(int j = i+1; j < D.numeroItensUtilizados; j++){
                HashSet<Integer> itens = new HashSet<>();
                itens.add(i);
                itens.add(j);
                Pattern p = new Pattern(itens, tipoAvaliacao);
                quantidadeTestes++;
                if(indiceKprimeiros <= k){
                        if(indiceKprimeiros < k){
                            DP2k[indiceKprimeiros] = p;
                        }else{
                            Arrays.sort(DP2k);
                        }  
                        indiceKprimeiros++;
                }else if(p.getQualidade() > DP2k[DP2k.length-1].getQualidade()){
                    if(SELECAO.ehRelevante(p, DP2k)){
                        DP2k[DP2k.length-1] = p; 
                        Arrays.sort(DP2k);                 
                    }
                }               
            }             
        }        
        double tempoDimensao2 = (double) (System.currentTimeMillis() - t0Dimensao2)/1000.0;
        Avaliador.imprimir(DP2k, k);
        System.out.println("\n2D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP2k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP2k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao2);
        
        
        
        //Itens de 3D
        long t0Dimensao3 = System.currentTimeMillis();
        quantidadeTestes = 0;
        indiceKprimeiros = 0;
        System.out.print("\nD3-i: ");        
        for(int i = 0; i < D.numeroItensUtilizados; i++){            
            System.out.print(i+",");
            
            //Critério de tempo de simulação total. Não pode ser maior que tempoMaximoMinutos
            double tempoTotal = (double) (System.currentTimeMillis() - t0Simulacao)/(1000.0*60.0);
            if(tempoTotal > tempoMaximoMinutos){
                //Caso não tenha inicializado a busca na dimensão 3, retornar os melhores entre DP1 e DP2. Mas ao mesmo tempo garantir que pelo menos o DP2 será realizado até o fim!
                if(DP3k[DP3k.length-1] == null){
                    return SELECAO.selecionarMelhoresDistintos(DP1k, DP2k);
                }
                else{
                    break;
                }                
            }
            for(int j = i+1; j < D.numeroItensUtilizados; j++){
                for(int l = j+1; l < D.numeroItensUtilizados; l++){                    
                    HashSet<Integer> itens = new HashSet<>();
                    itens.add(i);            
                    itens.add(j);
                    itens.add(l);
                    Pattern p = new Pattern(itens, tipoAvaliacao);
                    quantidadeTestes++;
                    if(indiceKprimeiros <= k){
                        if(indiceKprimeiros < k){
                            DP3k[indiceKprimeiros] = p;
                        }else{
                            Arrays.sort(DP3k);
                        }  
                        indiceKprimeiros++;
                    }else if(p.getQualidade() > DP3k[DP3k.length-1].getQualidade()){
                        if(SELECAO.ehRelevante(p, DP3k)){
                            DP3k[DP3k.length-1] = p;
                            Arrays.sort(DP3k);
                        }
                    }        
                }               
            }
        }       
        
        double tempoDimensao3 = (double) (System.currentTimeMillis() - t0Dimensao3)/1000.0;        
        Avaliador.imprimir(DP3k, k);
        System.out.println("3D");
        System.out.println("Qualidade média: " + Avaliador.avaliarMedia(DP3k, k));
        System.out.println("Cobertura +: " + Avaliador.coberturaPositivo(DP3k, k));
        System.out.println("Tentativas: " + quantidadeTestes);
        System.out.println("Tempo: " + tempoDimensao3);

        return SELECAO.selecionarMelhoresDistintos(DP1k, DP2k, DP3k);
    }

    public static Pattern [] runNtentativas(String tipoAvaliacao, int k, int numeroTentativas, int numeroMaximoDimensoes){
        Pattern[] DPk = new Pattern[k];
        Pattern[] DPtentativas = new Pattern[numeroTentativas];
        Random random = new Random();
        //Itens de 1D        
        for(int i = 0; i < numeroTentativas; i++){
            HashSet<Integer> itens = new HashSet<>();
            int numeroDimensoes = random.nextInt(numeroMaximoDimensoes) + 1;
            while(itens.size() < numeroDimensoes){
                itens.add(D.itensUtilizados[ random.nextInt(D.numeroItensUtilizados) ]);
            }
            Pattern p = new Pattern(itens, tipoAvaliacao);            
            DPtentativas[i] = p;            
        }        
        Arrays.sort(DPtentativas);
                
        for(int i = 0; i < k; i++){
            DPk[i] = new Pattern(new HashSet<>(), tipoAvaliacao);
        }
        
        int indiceDPk = 0;
        for(int i = 0; i < DPtentativas.length && indiceDPk < k; i++){
            Pattern p = DPtentativas[i];
            if(p.getQualidade() > DPk[k-1].getQualidade()){
                if(SELECAO.ehRelevante(p, DPk)){
                    DPk[k-1] = p; 
                    Arrays.sort(DPk);                 
                    indiceDPk++;
                } 
            }
        }            
        
        return DPk;
    }

}
