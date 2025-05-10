

#RELATÓRIO - Pior desempenho dado pela regra vazia
#Tabelão atribunindo regra vazia sempre que resultado não retornar regras.
#local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-10-TabelaoSemKzero.csv";
local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/UCI-20-TabelaoSemKzero.csv";

local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-8-126-Tabelao - k10.csv";

local = "C:/Users/Tarcisio  Lucas/Documents/NetBeansProjects/DP2_Driver/pastas/relatorios/UCI-20-TabelaoSemKzero.csv"

tabelao <- read.csv(local);

library(gplots);
library(ggplot2);

#Redundância
#supP e SUPP
textplot(
  aggregate(tabelao[, c("WRAcc", "suppP","overoll.suppP")], list(tabelao$Algoritmo), mean)
  )


#Qg
ggplot(tabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Qg")

pdf("BIO-8-126-R.pdf", paper="a4r")
#Média dos resultados
textplot(aggregate(tabelao[, c("Qg","Tempo","Testes","size")], list(tabelao$Algoritmo), mean))
textplot(aggregate(tabelao[, c("Qg","suppP","conf","SUPP")], list(tabelao$Algoritmo), mean))

#WRAcc
#ggplot(tabelao, aes(x=Algoritmo, y=WRAcc, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + ggtitle("WRAcc")

#Qg
ggplot(tabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Qg")

#suppP
ggplot(tabelao, aes(x=Algoritmo, y=suppP, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("suppP")

#conf
ggplot(tabelao, aes(x=Algoritmo, y=conf, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("conf")


#Qg-limit
#ggplot(tabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + scale_y_continuous(limits=c(0,20))  + ggtitle("Qg-limit")

#Tempo(s)
ggplot(tabelao, aes(x=Algoritmo, y=Tempo, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Tempo")

#Testes
ggplot(tabelao, aes(x=Algoritmo, y=Testes, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Testes")

#K
#ggplot(tabelao, aes(x=Algoritmo, y=K, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + ggtitle("K")
#K-limit
#ggplot(tabelao, aes(x=Algoritmo, y=K, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + scale_y_continuous(limits=c(0,6)) + ggtitle("K-limit")

#size
ggplot(tabelao, aes(x=Algoritmo, y=size, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("size")
#size-limit
#ggplot(tabelao, aes(x=Algoritmo, y=size, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + scale_y_continuous(limits=c(0,5)) + ggtitle("size-limit (UCI20)")

#SUPP
ggplot(tabelao, aes(x=Algoritmo, y=SUPP, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("SUPP")
dev.off()































local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/RelatorioUCI.csv";

%Atributo
tabelao$algoritmo

%Resumo das informações
summary(tabelao)

%Nomes dos atributos
names(tabelao)

%Selecionando subconjuntos de dados em R (também pode ser feito via índices!)
%Linhas onde algoritmo é NMEEF-SD
subset(tabelao, Algoritmo == "NMEEF-SD")
%Duas condições
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5)
%Seleção em linha e coluna! Sensacional!
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5, select = c(Algoritmo, WRAcc, Qg, size, K,  overoll.suppP))
%Seleção em linha e coluna intervalo de colunas
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5, select = Atributos:Qg)

%Agregando valores!
%Agregando valor médio dos algoritmos em determinados atributos para todos os registros
aggregate(tabelao[, 12:13], list(tabelao$Algoritmo), mean)
%Agregando valor médio das bases de dados em determinados atributos para todos os registros
aggregate(tabelao[, 12:13], list(tabelao$Base), mean)
