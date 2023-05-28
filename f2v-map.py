import jsonlines, torch, tqdm, pickle
from f2v import Model

def tokens2ids(stmt,info,unk):
    return [[info["train"]["tokens"][nonterm] if nonterm in info["train"]["tokens"] else unk for nonterm in stmt[0] if nonterm != ""],
            [info["train"]["tokens"][   term] if    term in info["train"]["tokens"] else unk for    term in stmt[1] if    term != ""]]

with open("info.pkl", "rb") as file: info = pickle.load(file)
data = jsonlines.open("data/data.jsonl")

configuration = {
    "GA_hidden"                  : 512,
    "LSTM_hidden"                : 100,
    "embedding_size"             : 100,
    "nheads"                     : 4,
    "<PAD>"                      : 10002,
    "<UNK>"                      : 10001,
    "preprocess_max_code_length" : 50,
    "no.vocabs"                  : 10003,
    "device"                     : "cpu",
    "training_max_code_length"   : 15,
}

model = Model(configuration)
model.load_state_dict(torch.load("f2v.pt"))
model.eval()

writer = jsonlines.open('data/f2vmap.jsonl', mode='w')
for line in tqdm.tqdm(data):
    ids = tokens2ids(line["f2v"],info, configuration["<UNK>"])
    ids[0] = ids[0][:configuration["training_max_code_length"]] + [configuration["<PAD>"]] * max(0,configuration["training_max_code_length"]-len(ids[0]))
    ids[1] = ids[1][:configuration["training_max_code_length"]] + [configuration["<PAD>"]] * max(0,configuration["training_max_code_length"]-len(ids[1]))
    emb = model.encode(torch.tensor(ids).unsqueeze(0))
    writer.write({"path":line["path"], "raw":line["raw"], "f2v":line["f2v"], "emb":emb.squeeze(0).tolist()})
