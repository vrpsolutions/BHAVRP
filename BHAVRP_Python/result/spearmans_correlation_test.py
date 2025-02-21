import scipy.stats as stats
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# Datos de los tiempos de ejecución
python_times = [9.050, 9.921, 2022.662, 106.005, 9.481, 421.012, 534.659, 9.118, 8.223, 3526.039, 11.192, 4.308, 6.014, 9.526, 12.238, 13.165, 294.793, 143.467, 
                8.936, 10.368, 2069.586, 107.051, 9.155, 406.529, 511.032, 9.452, 8.306, 3599.406, 10.655, 4.510, 6.214, 9.252, 11.849, 12.748, 289.022, 143.822]
java_times = [48.35, 1.05, 2665.45, 1.55, 0.4, 87.1, 47.05, 0.3, 0.5, 468.2, 0.55, 0.2, 1.9, 0.2, 0.1, 0.2, 0.2, 6.05,
              48.02, 1.03, 2695.26, 1.49, 0.39, 85.94, 46.84, 0.31, 0.49, 468.87, 0.56, 0.19, 1.92, 0.19, 0.1, 0.21, 0.21, 6.24]

spearman_corr, p_value = stats.spearmanr(python_times, java_times)
print(f"Resultado: Spearman's Corr: {spearman_corr}, P Value: {p_value}")

# Crear el gráfico
plt.figure(figsize=(8,6))
sns.regplot(x=python_times, y=java_times, scatter_kws={"s": 50}) 

# Etiquetas
plt.xlabel("Tiempo en Python (ms)")
plt.ylabel("Tiempo en Java (ms)")
plt.title("Comparación de tiempos de ejecución en Python vs Java")
plt.show()
