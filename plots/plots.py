import matplotlib.pyplot as plt
from matplotlib.ticker import StrMethodFormatter

plt.rcParams["font.family"] = "Times New Roman"

x = ['9','99','999','9999','99999']
y = [0.10188,0.01024,0.0009,0.00008,0.00002]

plt.figure(figsize=(5.5,4))
plt.title('False Positive Ratio / m')
plt.xlabel('m')
plt.ylabel('False Positive Ratio')
plt.plot(x,y,'k--')
plt.savefig('falsepositive.png')