# -------------------------------
# Carregar bibliotecas
# -------------------------------
usar_pacote <- function(pacote) {
  if (!require(pacote, character.only = TRUE)) {
    install.packages(pacote, dependencies = TRUE)
    library(pacote, character.only = TRUE)
  }
}

usar_pacote("ggplot2")
usar_pacote("gridExtra")
usar_pacote("dplyr")

# -------------------------------
# Carregar dados
# -------------------------------
local <- file.choose()
tabelao <- read.csv(local, fileEncoding = "UTF-8")

# -------------------------------
# Filtrar algoritmos com sufixo "foQg"
# -------------------------------
tabelao <- tabelao %>% filter(grepl("foQg$", Algoritmo))

# -------------------------------
# Colunas métricas numéricas
# -------------------------------
colunas_metrica <- c("size", "Tempo", "Testes", "WRAcc", "Qg", "overollSuppP", "suppP", "conf",
                     "CoverRedundancyP","DescripRDensity","DescripRDominator",
                     "Chi_Quad","p_value","Lift","DiffSup","GrowthRate","OddsRatio",
                     "cov","supp","suppN")

# -------------------------------
# Função de boxplot
# -------------------------------
gerar_boxplot <- function(coluna) {
  ggplot(tabelao, aes_string(x = "Algoritmo", y = coluna, fill = "Algoritmo")) +
    geom_boxplot(outlier.shape = 21, outlier.fill = "white", outlier.color = "black") +
    guides(fill = FALSE) +
    coord_flip() +
    ggtitle(paste("Boxplot -", coluna)) +
    theme_minimal(base_size = 12)
}

# -------------------------------
# Gerar PDF geral com todos os boxplots
# -------------------------------
lista_plots <- lapply(colunas_metrica, gerar_boxplot)

pdf("Relatorio_Boxplots.pdf", paper = "a4r", width = 11, height = 8.5)
for (p in lista_plots) {
  print(p)
}
dev.off()

# -------------------------------
# Gerar PDF individual por base
# -------------------------------
bases_unicas <- unique(tabelao$Base)

for (base in bases_unicas) {
  dados_base <- subset(tabelao, Base == base)
  nome_base_limpa <- gsub("[^A-Za-z0-9]", "_", base)
  nome_pdf <- paste0("Relatorio_", nome_base_limpa, ".pdf")
  
  pdf(nome_pdf, paper = "a4r", width = 11, height = 8.5)
  
  # -------------------------------
  # Tabela de médias por algoritmo
  # -------------------------------
  tabela_medias <- dados_base %>%
    group_by(Algoritmo) %>%
    summarise(across(all_of(colunas_metrica), mean, na.rm = TRUE)) %>%
    as.data.frame()
  
  # Arredondar apenas colunas numéricas
  colunas_validas <- intersect(colnames(tabela_medias), colunas_metrica)
  tabela_medias_arredondada <- tabela_medias
  tabela_medias_arredondada[colunas_validas] <- round(tabela_medias_arredondada[colunas_validas], 4)
  
  grid.table(tabela_medias_arredondada)
  
  # -------------------------------
  # Boxplots para cada métrica
  # -------------------------------
  for (metrica in colunas_metrica) {
    if (metrica %in% colnames(dados_base)) {
      p <- ggplot(dados_base, aes_string(x = "Algoritmo", y = metrica, fill = "Algoritmo")) +
        geom_boxplot(outlier.shape = 21, outlier.fill = "white", outlier.color = "black") +
        guides(fill = FALSE) +
        coord_flip() +
        ggtitle(paste("Boxplot -", metrica, "\nBase:", base)) +
        theme_minimal(base_size = 12)
      print(p)
    }
  }
  
  dev.off()
}
