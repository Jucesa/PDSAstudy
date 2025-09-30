import pandas as pd
from tkinter import Tk
from tkinter.filedialog import askopenfilenames
import os

Tk().withdraw()
arquivos = askopenfilenames(
    title="Selecione os arquivos CSV das bases",
    filetypes=[("CSV files", "*.csv"), ("Todos os arquivos", "*.*")]
)

if arquivos:
    dfs = []
    for caminho in arquivos:
        df = pd.read_csv(caminho)
        dfs.append(df)
    df_total = pd.concat(dfs, ignore_index=True)
    
    medias_gerais = df_total.groupby('Algoritmo').mean(numeric_only=True)
    
    # Pasta do primeiro arquivo selecionado
    pasta_saida = os.path.dirname(arquivos[0])
    nome_saida = "medias_gerais_por_algoritmo.csv"
    caminho_saida = os.path.join(pasta_saida, nome_saida)
    
    medias_gerais.to_csv(caminho_saida)
    
    print(f"Médias gerais por algoritmo salvas em: {caminho_saida}")
else:
    print("Nenhum arquivo selecionado.")
