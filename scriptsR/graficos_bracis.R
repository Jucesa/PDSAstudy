library(ggplot2)

%Barras com quantidade méida de DPs
sizeDP <- read.table(header=TRUE, text='
size count
[1;1]  18
(1;2]  54
(2;3]  39
(3;4]  10
(4;5]  1
')

# Very basic bar graph
ggplot(data=sizeDP, aes(x=size, y=count)) +
    geom_bar(stat="identity")






Qg <- read.table(header=TRUE, text='
Algorithm k Qg
SSDP 5.0 33.11
SSDP 10.0 32.82
SSDP 20.0 31.88
SSDP 50.0 30.30
SD 5.0 26.73
SD 10.0 26.70
SD 20.0 27.02
SD 50.0 27.62
ExaustiveK 5.0 22.56
ExaustiveK 10.0 22.74
ExaustiveK 20.0 22.31
ExaustiveK 50.0 21.79
Random1M 5.0 24.48
Random1M 10.0 22.87
Random1M 20.0 21.07
Random1M 50.0 18.76
Random2M 5.0 25.16
Random2M 10.0 23.60
Random2M 20.0 21.71
Random2M 50.0 19.52
')

ppi <- 300;
png("Qg.png", width=6*ppi, height=4*ppi, res=ppi)
plot(ggplot(data=Qg, aes(x=k, y=Qg, group=Algorithm)) + 
	geom_line(aes(linetype=Algorithm), # Line type depends on cond
              size = 1.3) +       # Thicker line
    	geom_point(aes(shape=Algorithm),   # Shape depends on cond
               size = 3)          # Large points
)
dev.off()







time <- read.table(header=TRUE, text='
Algorithm k time
SSDP 5.0 9.39
SSDP 10.0 11.05
SSDP 20.0 13.33
SSDP 50.0 14.54
SD 5.0 3.34
SD 10.0 7.25
SD 20.0 16.97
SD 50.0 51.81
ExaustivoK 5.0 0.14
ExaustivoK 10.0 0.14
ExaustivoK 20.0 0.16
ExaustivoK 50.0 0.81
Aleatorio1M 5.0 4.87
Aleatorio1M 10.0 4.80
Aleatorio1M 20.0 5.28
Aleatorio1M 50.0 5.35
Aleatorio2M 5.0 11.32
Aleatorio2M 10.0 11.22
Aleatorio2M 20.0 12.53
Aleatorio2M 50.0 12.46
')

ppi <- 300;
png("time.png", width=6*ppi, height=4*ppi, res=ppi)
plot(ggplot(data=time, aes(x=k, y=time, group=Algorithm)) + 
	geom_line(aes(linetype=Algorithm), # Line type depends on cond
              size = 1.3) +       # Thicker line
    	geom_point(aes(shape=Algorithm),   # Shape depends on cond
               size = 3)          # Large points
)
dev.off()




size <- read.table(header=TRUE, text='
Algorithm k size
SSDP 5.0 2.04
SSDP 10.0 2.17
SSDP 20.0 2.26
SSDP 50.0 2.31
SD 5.0 1.4
SD 10.0 1.51
SD 20.0 1.65
SD 50.0 1.79
ExaustivoK 5.0 1.35
ExaustivoK 10.0 1.43
ExaustivoK 20.0 1.53
ExaustivoK 50.0 1.73
Aleatorio1M 5.0 1.64
Aleatorio1M 10.0 1.67
Aleatorio1M 20.0 1.71
Aleatorio1M 50.0 1.8
Aleatorio2M 5.0 1.66
Aleatorio2M 10.0 1.70
Aleatorio2M 20.0 1.74
Aleatorio2M 50.0 1.81
')

ppi <- 300;
png("size.png", width=6*ppi, height=4*ppi, res=ppi)
plot(ggplot(data=size, aes(x=k, y=size, group=Algorithm)) + 
	geom_line(aes(linetype=Algorithm), # Line type depends on cond
              size = 1.3) +       # Thicker line
    	geom_point(aes(shape=Algorithm),   # Shape depends on cond
               size = 3)          # Large points
)
dev.off()





SUPP <- read.table(header=TRUE, text='
Algorithm k SUPP
SSDP 5.0 0.83
SSDP 10.0 0.89
SSDP 20.0 0.93
SSDP 50.0 0.96
SD 5.0 0.78
SD 10.0 0.85
SD 20.0 0.89
SD 50.0 0.93
ExaustivoK 5.0 0.77
ExaustivoK 10.0 0.87
ExaustivoK 20.0 0.92
ExaustivoK 50.0 0.95
Aleatorio1M 5.0 0.82
Aleatorio1M 10.0 0.91
Aleatorio1M 20.0 0.96
Aleatorio1M 50.0 0.98
Aleatorio2M 5.0 0.82
Aleatorio2M 10.0 0.91
Aleatorio2M 20.0 0.96
Aleatorio2M 50.0 0.98
')

ppi <- 300;
png("SUPP.png", width=6*ppi, height=4*ppi, res=ppi)
plot(ggplot(data=SUPP, aes(x=k, y=SUPP, group=Algorithm)) + 
	geom_line(aes(linetype=Algorithm), # Line type depends on cond
              size = 1.3) +       # Thicker line
    	geom_point(aes(shape=Algorithm),   # Shape depends on cond
               size = 3)          # Large points
)
dev.off()

