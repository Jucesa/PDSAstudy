%Tamanho médios das DPs nas bases de dados
library(ggplot2)

ppi <- 300;
png("generation_west.png", width=6*ppi, height=4*ppi, res=ppi)
plot(ggplot(data=generation_west, aes(x=generation, y=size, group=Population)) + 
	geom_line(aes(linetype=Population), # Line type depends on cond
              size = 1.3) +       # Thicker line
    	geom_point(aes(shape=Population),   # Shape depends on cond
               size = 3)          # Large points
)
dev.off()

generation_west <- read.table(header=TRUE, text='
Population generation size
P 1 1
P 2 1.64
P 3 2.07
P 4 2.50
P 5 2.80
P 6 3.18
P 7 3.32
P 8 3.35
P 9 3.42
P 10 3.47
P 11 3.42
P 12 3.26
P 13 3.00
P 14 2.48
P 15 1.83
P 16 1.79
P 17 1.89
P 18 1.98
P 19 2.06
P 20 2.18
P 21 2.28
P 22 2.41
P 23 2.57
P 24 2.76
P 25 2.98
P 26 3.26
P 27 3.59
P 28 3.98
P 29 4.12
P 30 4.45
P 31 4.92
P 32 5.40
P 33 5.54
P 34 5.16
P 35 4.74
P 36 4.39
P 37 4.45
Pk 1 1
Pk 2 1.8
Pk 3 2.64
Pk 4 2.68
Pk 5 2.82
Pk 6 3.04
Pk 7 3.24
Pk 8 3.4
Pk 9 3.62
Pk 10 3.86
Pk 11 3.86
Pk 12 4
Pk 13 4.12
Pk 14 4.14
Pk 15 4.12
Pk 16 4.1
Pk 17 4.1
Pk 18 4.1
Pk 19 4.14
Pk 20 4.2
Pk 21 4.2
Pk 22 4.2
Pk 23 4.18
Pk 24 4.28
Pk 25 4.34
Pk 26 4.52
Pk 27 4.72
Pk 28 4.66
Pk 29 4.82
Pk 30 5.06
Pk 31 5.22
Pk 32 5.46
Pk 33 5.58
Pk 34 5.64
Pk 35 5.64
Pk 36 5.64
Pk 37 5.64
')
