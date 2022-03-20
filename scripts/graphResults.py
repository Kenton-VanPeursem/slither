import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv('QLearningPlayer.csv');

print(df.describe())

y = df['Score'].values
x = [i for i in range(len(y))]
print(x[:5])
print(y[:5])
plt.scatter(x, y)
plt.show()