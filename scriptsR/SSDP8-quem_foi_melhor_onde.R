library(ggplot2)

#Carregando base de dados
caminho <- "C:\\Users\\Tarcisio  Lucas\\Desktop\\ASCO-2\\SSDP8\\"
nomeBase <- "BIO-126-8SSDP-Qg-Tabelao-nomeClean.csv"
nomeCompletoBase <- paste(caminho, nomeBase, sep = "")

data = read.csv(nomeCompletoBase)
summary(data)

#Editando nomes dos algoritmos
data$Algoritmo <- as.character(data$Algoritmo)
data$Algoritmo[data$Algoritmo == "SSDP"] <- "SSDP_100x100"
data$Algoritmo[data$Algoritmo == "SSDP3x3"] <- "SSDP_100x100_3x3"
data$Algoritmo[data$Algoritmo == "SSDPm10Xc90"] <- "SSDP_90x10"
data$Algoritmo[data$Algoritmo == "SSDPm10Xc903x3"] <- "SSDP_90x10_3x3"
data$Algoritmo[data$Algoritmo == "SSDPm50Xc50"] <- "SSDP_50x50"
data$Algoritmo[data$Algoritmo == "SSDPm50Xc503x3"] <- "SSDP_50x50_3x3"
data$Algoritmo[data$Algoritmo == "SSDPmXcAuto"] <- "SSDP_Auto"
data$Algoritmo[data$Algoritmo == "SSDPmXcAuto3x3"] <- "SSDP_Auto_3x3"
data$Algoritmo <- as.factor(data$Algoritmo)
summary(data)

#Transformando campo "Base-discretização" em "Base" e "discretização" 
data$Base <- as.character(data$Base)
split <- strsplit(data$Base, "-pn-")

data$Base <- sapply(split, function(x) x[1])
data$Base <- as.factor(data$Base)

discretizacao <- sapply(split, function(x) x[2])
discretizacao <- as.factor(discretizacao)
data[, "discretizacao"] <- discretizacao


#Gráfico para cada base de dados
dataSub <- aggregate(Qg ~ Algoritmo + K + Base, data = data, mean)
ggplot(data=dataSub, aes(x=K, y=Qg, group=Algoritmo)) + 
  geom_line(aes(color=Algoritmo), # Line type depends on cond
            size = 1.3) +       # Thicker line
  geom_point(aes(shape=Algoritmo),   # Shape depends on cond
             size = 1) +          # Large points
  #facet_grid(. ~ Base)
  facet_wrap( ~ Base, ncol = 11, scales = "free" )
#facet_grid(sex ~ day)


#Gráfico para cada tipo de discretização
dataSub <- aggregate(Qg ~ Algoritmo + K + discretizacao, data = data, mean)
ggplot(data=dataSub, aes(x=K, y=Qg, group=Algoritmo)) + 
  geom_line(aes(color=Algoritmo), # Line type depends on cond
            size = 1.3) +       # Thicker line
  geom_point(aes(shape=Algoritmo),   # Shape depends on cond
             size = 1) +          # Large points
  #facet_grid(. ~ discretizacao)
  facet_wrap( ~ discretizacao, scales = "free" )




###
#   Tabela para investigar desempenho de cada versão SSDP8 vs. tipo de base de dados
###
#install.packages("magrittr")
library(magrittr) # %>%
library(dplyr) # group_by

#Filtrando colunas
dataSub <- data[, c("Algoritmo", "Base", "discretizacao", "D", "Dp", "Dn", "Atributos",
                     "I", "Qg","K")]
#summary(dataSub)

dataSub <- aggregate(Qg ~ Algoritmo + Base + K + discretizacao + D + Dp + Atributos, data = data, mean)
dim(dataSub)
#summary(dataSub)

#Obtendo algoritmos de melhor desempenho por base X k X discretizaçao (incluindo repetidamente quando empate)
result <- dataSub %>% 
  group_by(Base, K, discretizacao) %>%
  filter(Qg == max(Qg)) %>%
  arrange(Base, K, discretizacao, D, Dp, Atributos, Algoritmo)
#summary(result)

meta_base <- as.data.frame(result)
summary(meta_base)

#Criando: Dp passa a armezenar o valor em percentual
meta_base$Dp <- meta_base$Dp/meta_base$D
#summary(meta_base$Dp)


### Adicionando colunas com rótulo binário
#Coluna com SSDP_Auto_3x3
SSDP_Auto_3x3vs <- meta_base$Algoritmo
summary(SSDP_Auto_3x3vs)
SSDP_Auto_3x3vs <- as.character(SSDP_Auto_3x3vs)
SSDP_Auto_3x3vs[SSDP_Auto_3x3vs != "SSDP_Auto_3x3"]  <- "n"
SSDP_Auto_3x3vs <- as.factor(SSDP_Auto_3x3vs)
summary(SSDP_Auto_3x3vs)

#Coluna com SSDP_100x100_3x3
SSDP_100x100_3x3vs <- meta_base$Algoritmo
summary(SSDP_100x100_3x3vs)
SSDP_100x100_3x3vs <- as.character(SSDP_100x100_3x3vs)
SSDP_100x100_3x3vs[SSDP_100x100_3x3vs != "SSDP_100x100_3x3"]  <- "n"
SSDP_100x100_3x3vs <- as.factor(SSDP_100x100_3x3vs)
summary(SSDP_100x100_3x3vs)

#Coluna com SSDP_90x10_3x3
SSDP_90x10_3x3vs <- meta_base$Algoritmo
summary(SSDP_90x10_3x3vs)
SSDP_90x10_3x3vs <- as.character(SSDP_90x10_3x3vs)
SSDP_90x10_3x3vs[SSDP_90x10_3x3vs != "SSDP_90x10_3x3"]  <- "n"
SSDP_90x10_3x3vs <- as.factor(SSDP_90x10_3x3vs)
summary(SSDP_90x10_3x3vs)

#Coluna com SSDP_50x50_3x3
SSDP_50x50_3x3vs <- meta_base$Algoritmo
summary(SSDP_50x50_3x3vs)
SSDP_50x50_3x3vs <- as.character(SSDP_50x50_3x3vs)
SSDP_50x50_3x3vs[SSDP_50x50_3x3vs != "SSDP_50x50_3x3"]  <- "n"
SSDP_50x50_3x3vs <- as.factor(SSDP_50x50_3x3vs)
summary(SSDP_50x50_3x3vs)

#Adicionado colunas criadas
meta_base[, "SSDP_Auto_3x3vs"] <- SSDP_Auto_3x3vs
meta_base[, "SSDP_100x100_3x3vs"] <- SSDP_100x100_3x3vs
meta_base[, "SSDP_50x50_3x3vs"] <- SSDP_50x50_3x3vs
meta_base[, "SSDP_90x10_3x3vs"] <- SSDP_90x10_3x3vs
summary(meta_base)

###Discretizando atributos numéricos
summary(meta_base)
meta_base$Dp[meta_base$Dp <= 0.5] <- "<=0.5"
meta_base$Dp[meta_base$Dp > 0.5] <- ">0.5"
meta_base$Dp <- as.factor(meta_base$Dp)
summary(meta_base$Dp)

summary(meta_base$D)
meta_base$D[meta_base$D <= 100] <- "<=100"
meta_base$D[meta_base$D > 100 & meta_base$D < 200] <- "(100-200)"
meta_base$D[meta_base$D >= 200] <- ">=200"
meta_base$D <- as.factor(meta_base$D)
summary(meta_base$D)

summary(meta_base$Atributos)
meta_base$Atributos[meta_base$Atributos <= 10000] <- "<=10000"
meta_base$Atributos[meta_base$Atributos > 10000 & meta_base$Atributos < 20000] <- "(10000-20000)"
meta_base$Atributos[meta_base$Atributos >= 20000] <- ">=20000"
meta_base$Atributos <- as.factor(meta_base$Atributos)
summary(meta_base$Atributos)

meta_base$K <- as.factor(meta_base$K)


meta_base <- meta_base[meta_base$discretizacao == "width-2" | meta_base$discretizacao == "width-4" | meta_base$discretizacao == "width-8", ] 

summary(meta_base$Algoritmo)
dim(meta_base)

#Salvando bases
#SSDP_Auto_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_Auto_3x3vs")]
nomeFile <- paste(caminho, "SSDP_Auto_3x3vs.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)

#Salvando bases
#SSDP_100x100_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_100x100_3x3vs")]
nomeFile <- paste(caminho, "SSDP_100x100_3x3vs.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)

#Salvando bases
#SSDP_50x50_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_50x50_3x3vs")]
nomeFile <- paste(caminho, "SSDP_50x50_3x3vs.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)


#Salvando bases
#SSDP_90x10_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_90x10_3x3vs")]
nomeFile <- paste(caminho, "SSDP_90x10_3x3vs.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)





### Bases considerando apenas discretização com relação a WIDTH
#Salvando bases
#SSDP_Auto_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_Auto_3x3vs")]
base <- base[base$discretizacao == "width-2" | base$discretizacao == "width-4" | base$discretizacao == "width-8", ] 
nomeFile <- paste(caminho, "SSDP_Auto_3x3vsWidth.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)

#Salvando bases
#SSDP_100x100_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_100x100_3x3vs")]
base <- base[base$discretizacao == "width-2" | base$discretizacao == "width-4" | base$discretizacao == "width-8", ] 
nomeFile <- paste(caminho, "SSDP_100x100_3x3vsWidth.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)

#Salvando bases
#SSDP_50x50_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_50x50_3x3vs")]
base <- base[base$discretizacao == "width-2" | base$discretizacao == "width-4" | base$discretizacao == "width-8", ] 
nomeFile <- paste(caminho, "SSDP_50x50_3x3vsWidth.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)


#Salvando bases
#SSDP_90x10_3x3vs
base <- meta_base[, c("Dp", "D", "Atributos", "K", "discretizacao", "SSDP_90x10_3x3vs")]
base <- base[base$discretizacao == "width-2" | base$discretizacao == "width-4" | base$discretizacao == "width-8", ] 
nomeFile <- paste(caminho, "SSDP_90x10_3x3vsWidth.csv", sep = "")
write.csv2(base, nomeFile, row.names=FALSE)

