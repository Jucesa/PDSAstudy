library(gplots);
library(ggplot2);


#Dados iniciais
#local = "C:/Users/Mari/Documents/R/BIO-126-SSDP8_Tabelao2.csv";
#local = "C:/Users/Mari/Documents/R/BIO-126-SSDP8_Tabelao.csv";
#local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-126-8SSDP-Qg-Tabelao-nomeClean.csv"
local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-126-8SSDP-Qg-Tabelao-nomeClean2.csv"

#local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-126-SSDPauto3x3xSDxTrivialxRamdon3M-Qg-Tabelao-nomeClean.csv"
local = "C:/Users/tarcisio_pontes/Documents/NetBeansProjects/DP2/pastas/relatorios/BIO-126-SSDPauto3x3xSDxTrivialxRamdon3M-Qg-Tabelao-nomeClean.csv"

tabelao = read.csv(local);
attach(tabelao)








pdf("RelatórioK-BIO-126-SSDP8.pdf", paper="a4r")
#pdf("RelatórioK-BIO-126-SSDPxSDxAleatorioxTrivial.pdf", paper="a4r")



#Resultado médio
tabela1 <- aggregate(tabelao[, c("Qg","Tempo","Testes","size")], by=list(Algoritmo,K), FUN=mean, na.rm=TRUE);
textplot(tabela1)
write.xls(tabela1, "c:/arquivo.xls") 

tabela2 <- aggregate(tabelao[, c("supp", "conf", "SUPP")], by=list(Algoritmo,K), FUN=mean, na.rm=TRUE)
textplot(tabela2)

#Resultado variância
tabela1 <- aggregate(tabelao[, c("Qg","Tempo","Testes","size")], by=list(Algoritmo,K), FUN=var, na.rm=TRUE);
textplot(tabela1)

tabela2 <- aggregate(tabelao[, c("supp", "conf", "SUPP")], by=list(Algoritmo,K), FUN=var, na.rm=TRUE)
textplot(tabela2)




ppi <- 300;
png("SSDP8.png", width=6*ppi, height=4*ppi, res=ppi)

tabelaoSub <- aggregate(Qg ~ Algoritmo + K, data = tabelao, mean)
p1 <- ggplot(data=tabelaoSub, aes(x=K, y=Qg, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points


tabelaoSub <- aggregate(Tempo ~ Algoritmo + K, data = tabelao, mean)
p2 <- ggplot(data=tabelaoSub, aes(x=K, y=Tempo, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points


multiplot(p1, p2, cols=1)

dev.off()
source("http://peterhaschke.com/Code/multiplot.R")
source("multiplot.R")


#Gráfico: Qg
ppi <- 300;
png("SSDP8-Qg.png", width=6*ppi, height=4*ppi, res=ppi)
tabelaoSub <- aggregate(Qg ~ Algoritmo + K, data = tabelao, mean)

plot(ggplot(data=tabelaoSub, aes(x=K, y=Qg, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points
)

dev.off()


ppi <- 300;
png("SSDP8-Tempo.png", width=6*ppi, height=4*ppi, res=ppi)
#Gráfico: Tempo
tabelaoSub <- aggregate(Tempo ~ Algoritmo + K, data = tabelao, mean)
plot(ggplot(data=tabelaoSub, aes(x=K, y=Tempo, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points
)
dev.off()


ppi <- 300;
png("SSDP8-Testes.png", width=6*ppi, height=4*ppi, res=ppi)
#Gráfico: Testes
tabelaoSub <- aggregate(Testes ~ Algoritmo + K, data = tabelao, mean)
plot(ggplot(data=tabelaoSub, aes(x=K, y=Testes, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points
)
dev.off()


ppi <- 300;
png("SSDP8-Size.png", width=6*ppi, height=4*ppi, res=ppi)
#Gráfico: size
tabelaoSub <- aggregate(size ~ Algoritmo + K, data = tabelao, mean)
plot(ggplot(data=tabelaoSub, aes(x=K, y=size, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points
)
dev.off()


ppi <- 300;
png("SSDP8-SUPP.png", width=6*ppi, height=4*ppi, res=ppi)
#Gráfico: SUPP
tabelaoSub <- aggregate(SUPP ~ Algoritmo + K, data = tabelao, mean)
plot(ggplot(data=tabelaoSub, aes(x=K, y=SUPP, group=Algoritmo)) + 
       geom_line(aes(linetype=Algoritmo), # Line type depends on cond
                 size = 1.3) +       # Thicker line
       geom_point(aes(shape=Algoritmo),   # Shape depends on cond
                  size = 3)          # Large points
)
dev.off()

dev.off()











#Tabela K=5
#tabelaoSub <- subset(tabelao, K == 5, select = c(Algoritmo, Qg, Tempo, Testes, size))
#aggregate(tabelaoSub[, c("Qg","Tempo","Testes","size")],list(tabelaoSub$Algoritmo), mean)

#tabelaoSub <- subset(tabelao, K == 5, select = c(Algoritmo, supp, conf, SUPP))
#aggregate(tabelaoSub[, c("supp", "conf", "SUPP")],list(tabelaoSub$Algoritmo), mean)

#Desvio padrão e variância
#aggregate(tabelaoSub[, c("Qg","Tempo","Testes","size")],list(tabelaoSub$Algoritmo), sd)
#aggregate(tabelaoSub[, c("Qg","Tempo","Testes","size")],list(tabelaoSub$Algoritmo), var)









