import matplotlib.pyplot as plt
from matplotlib.ticker import StrMethodFormatter

plt.rcParams["font.family"] = "Times New Roman"

graphs = {
    'memory': {
        'nnaps': ['10','100','1000'],
        'bloomsize_10k': [9491,949,95],
        'bloomsize_64k': [60744,6074,607],
        'bitmap_10k': [10000,10000,10000],
        'bitmap_64k': [64000,64000,64000]
    },
}

fig, (ax1,ax2) = plt.subplots(2,figsize=(5,5))
fig.suptitle('Memory Requirements per NAP')
ax1.set(ylabel='bits/NAP')
ax2.set(xlabel='N_NAPs', ylabel='bits/NAP')
ax1.set_title('N_items = 10,000')
ax1.plot(graphs['memory']['nnaps'], graphs['memory']['bloomsize_10k'],'--k',label='Bloom Filter')
ax1.plot(graphs['memory']['nnaps'], graphs['memory']['bitmap_10k'],'r',label='Bitmap')
ax1.set_xticks(graphs['memory']['nnaps'])

ax2.set_title('N_items = 64,000')
ax2.plot(graphs['memory']['nnaps'], graphs['memory']['bloomsize_64k'],'--k')
ax2.plot(graphs['memory']['nnaps'], graphs['memory']['bitmap_64k'],'r')
ax2.set_xticks(graphs['memory']['nnaps'])

fig.legend()
fig.tight_layout()
plt.savefig('plots/figs/memory.png')


# x = ['9','99','999','9999','99999']
# y = [0.10188,0.01024,0.0009,0.00008,0.00002]

# plt.figure(figsize=(5.5,4))
# plt.title('False Positive Ratio / m')
# plt.xlabel('m')
# plt.ylabel('False Positive Ratio')
# plt.plot(x,y,'k--')
# plt.savefig('falsepositive.png')