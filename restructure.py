import os, jsonlines

with jsonlines.open("data/f2vmap.jsonl","r") as f2vmap:
    for line in f2vmap:
        path = line["path"]
        path_components = os.path.normpath(path).split(os.sep)
        directory = path_components[path_components.index("java-files")+1]
        newdir = os.path.join(os.path.join("data","java_encoding_files"),directory)
        newpath = os.path.join(newdir,"f2v.jsonl")


        if not os.path.isdir(newdir): os.mkdir(newdir)
        if os.path.isfile(newpath): 
            with jsonlines.open(newpath,mode="a") as f2vmap: 
                f2vmap.write(line)
        else: 
            with jsonlines.open(newpath,mode="w") as f2vmap: 
                f2vmap.write(line)


