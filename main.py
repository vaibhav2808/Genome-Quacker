def frequencyMap(genome,k):
    table=dict()
    for i in range(0,len(genome)-k+1):
        temp=genome[i:i+k]
        table[temp]=table.get(temp,0)+1
    sortedList=sorted(table.items(),key=lambda t:(t[1],t[0]))


