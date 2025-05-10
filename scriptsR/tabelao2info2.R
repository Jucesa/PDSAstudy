#RELATÓRIO - Pior desempenho dado pela regra vazia
#Tabelão atribunindo regra vazia sempre que resultado não retornar regras.
#local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-10-TabelaoSemKzero.csv";
local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/UCI-20-TabelaoSemKzero.csv";

local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-8-126-Tabelao - k10.csv";

local = "C:/Users/Tarcisio  Lucas/Documents/NetBeansProjects/DP2_Driver/pastas/relatorios/UCI-20-TabelaoSemKzero.csv"

local = "C:/Users/Tarcisio  Lucas/Documents/NetBeansProjects/DP2_Driver/pastas/relatorios/BIO-126-Qg-Tabelao.csv"

tabelao <- read.csv(local);

library(gplots);
library(ggplot2);

pdf("teste.pdf", paper="a4r")
#########################
### GERAL ###############
#########################

#Bases a serem consideradas
bases <- c("yeoh-pn-width-2", 
           #"west-pn-width-2",
           #"tian-pn-width-2",
           #"sun-pn-width-2",
           #"subramanian-pn-width-2",
           #"sorlie-pn-width-2",
           #"singh-pn-width-2",
           #"shipp-pn-width-2",
           #"pomeroy-pn-width-2",
           #"nakayama-pn-width-2",
           #"khan-pn-width-2",
           #"gravier-pn-width-2",
           #"gordon-pn-width-2",
           #"golub-pn-width-2",
           #"christensen-pn-width-2",
           #"chowdary-pn-width-2",
           #"chin-pn-width-2",
           #"chiaretti-pn-width-2",
           #"burczynski-pn-width-2",
           #"borovecki-pn-width-2",
           "alon-pn-width-2"
           )
#filtrando resultados com bases especificadas
subTabelao <- tabelao[tabelao$Base %in% bases,]
dim(subTabelao)
#Confirmando bases consideradas
unique(subTabelao$Base)
subTabelao[1:45, c(1,2,3)]


#TABELAS
textplot("Bases:")

textplot(aggregate(subTabelao[, c("Qg", "WRAcc", "Tempo","overoll.suppP")], list(subTabelao$Algoritmo), mean))
textplot(aggregate(subTabelao[, c("Testes","size","TP","FP")], list(subTabelao$Algoritmo), mean))

#BOX_PLOT
ggplot(subTabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("Qg")

ggplot(subTabelao, aes(x=Algoritmo, y=WRAcc, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("WRAcc")

ggplot(subTabelao, aes(x=Algoritmo, y=Tempo, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("Tempo")

ggplot(subTabelao, aes(x=Algoritmo, y=overoll.suppP, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("SUPP+")

ggplot(subTabelao, aes(x=Algoritmo, y=Testes, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("Testes")

ggplot(subTabelao, aes(x=Algoritmo, y=size, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("size")

ggplot(subTabelao, aes(x=Algoritmo, y=TP, fill=Algoritmo)) + geom_boxplot() + 
  guides(fill=FALSE) + coord_flip() + ggtitle("TP")

#Qg-limit
#ggplot(tabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
#    guides(fill=FALSE) + coord_flip() + scale_y_continuous(limits=c(0,20))  + ggtitle("Qg-limit")






#########################
### POR BASE ############
#########################

for(i in 1:length(bases)){
  base <- bases[2]
  
  baseText <- paste("Base: ", base, "\n(|D|=", subTabelao[1,4], ",|D+|=", subTabelao[1,5]
                    ,",|D-|=", subTabelao[1,6], ",|A|=", subTabelao[1,7], ",|I|=", subTabelao[1,8], ")"  
  )
  textplot(baseText)
  
  subTabelao <- tabelao[tabelao$Base == base ,]
  unique(subTabelao$Base)
  subTabelao[1:45, c(1,2,3,17)]
  #TABELAS
  textplot(aggregate(subTabelao[, c("Qg", "WRAcc", "Tempo","overoll.suppP")], list(subTabelao$Algoritmo), mean))
  textplot(aggregate(subTabelao[, c("Testes","size","TP","FP")], list(subTabelao$Algoritmo), mean))
  
  #BOX_PLOT
  print(
  ggplot(subTabelao, aes(x=Algoritmo, y=Qg, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Qg")
  )
  
  print(
  ggplot(subTabelao, aes(x=Algoritmo, y=WRAcc, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("WRAcc")
  )
  
  print(
  ggplot(subTabelao, aes(x=Algoritmo, y=Tempo, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("Tempo")
  )
  
  print(
  ggplot(subTabelao, aes(x=Algoritmo, y=overoll.suppP, fill=Algoritmo)) + geom_boxplot() + 
    guides(fill=FALSE) + coord_flip() + ggtitle("SUPP+")
  )
}


dev.off()

















#Atributo
tabelao$algoritmo

#Resumo das informações
summary(tabelao)

#Nomes dos atributos
names(tabelao)

#Selecionando subconjuntos de dados em R (também pode ser feito via índices!)
#Linhas onde algoritmo é NMEEF-SD
subset(tabelao, Algoritmo == "NMEEF-SD")
#Duas condições
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5)
#Seleção em linha e coluna! Sensacional!
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5, select = c(Algoritmo, WRAcc, Qg, size, K,  overoll.suppP))
#Seleção em linha e coluna intervalo de colunas
subset(tabelao, Algoritmo == "NMEEF-SD" & K <= 5, select = Atributos:Qg)

#Agregando valores!
#Agregando valor médio dos algoritmos em determinados atributos para todos os registros
aggregate(tabelao[, 12:13], list(tabelao$Algoritmo), mean)
#Agregando valor médio das bases de dados em determinados atributos para todos os registros
aggregate(tabelao[, 12:13], list(tabelao$Base), mean)
