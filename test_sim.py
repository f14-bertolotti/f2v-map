import numpy, json

from numpy import dot
from numpy.linalg import norm

with open("data/f2vmap.jsonl") as file:
    data = [json.loads(line) for line in file]

for line1 in data:
    for line2 in data:
        a, b = numpy.array(line1["emb"]), numpy.array(line2["emb"])
        sim = dot(a, b)/(norm(a)*norm(b))
        print(line1["f2v"])
        print(line2["f2v"])
        print(line1["raw"])
        print(line2["raw"])
        print(sim)
        print()

