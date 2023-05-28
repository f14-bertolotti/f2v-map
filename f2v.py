import torch.nn.functional as F
import torch

class GlobaAttention(torch.nn.Module):
    def __init__(self, configuration):
        super(GlobaAttention, self).__init__()
        self.configuration = configuration
        self.qlinear = torch.nn.Linear(configuration["embedding_size"], configuration["GA_hidden"]).to(configuration["device"])
        self.vlinear = torch.nn.Linear(configuration["embedding_size"], configuration["embedding_size"]).to(configuration["device"]) 
        self.alpha   = torch.nn.Linear(configuration["GA_hidden"], 1).to(configuration["device"])
    def forward(self, x, mask):
        h = self.qlinear(x)
        v = self.vlinear(x)
        s = self.alpha(h).masked_fill(mask.unsqueeze(-1),float("-inf"))
        a = torch.nn.functional.softmax(s,-2)
        z = (v*a).sum(-2)
        return z,a

class Model(torch.nn.Module):

    def __init__(self, configuration):
        super(Model, self).__init__()
        self.configuration = configuration

        self.src_embedding = torch.nn.Embedding(self.configuration["no.vocabs"], self.configuration["embedding_size"], padding_idx=self.configuration["<PAD>"]).to(device=self.configuration["device"])
        self.src_positions = torch.nn.Embedding(self.configuration["training_max_code_length"], self.configuration["embedding_size"], padding_idx=None                            ).to(device=self.configuration["device"])

        self.srcencoder = torch.nn.TransformerEncoder(torch.nn.TransformerEncoderLayer(d_model=self.configuration["embedding_size"],activation="gelu", nhead=self.configuration["nheads"], dropout=0.1, dim_feedforward=248), num_layers=3).to(self.configuration["device"])
        self.ga = GlobaAttention(self.configuration)

        self.normGA = torch.nn.LayerNorm(self.configuration["embedding_size"]).to(self.configuration["device"])
        
        self.fc0 = torch.nn.Linear(self.configuration["embedding_size"]*2,256).to(self.configuration["device"])
        self.ln0 = torch.nn.LayerNorm(256) .to(self.configuration["device"])
        self.dp0 = torch.nn.Dropout  (0.2) .to(self.configuration["device"])
        self.fc1 = torch.nn.Linear(256,self.configuration["embedding_size"]*2).to(self.configuration["device"])
        self.ln1 = torch.nn.LayerNorm(self.configuration["embedding_size"]*2) .to(self.configuration["device"])

        self.optimizer     = torch.optim.Adam(self.parameters(),lr=0.0001)
        self.loss          = torch.nn.TripletMarginLoss(reduction="sum")

    def encode(self, sequences):
        with torch.no_grad():
            sequences                = sequences[:,:,:self.configuration["training_max_code_length"]].to(self.configuration["device"])
            sequencesmask            = sequences   == self.configuration["<PAD>"]
            sequences_nonterm_mask   = sequencesmask[:,0,:]
            sequences_term_mask      = sequencesmask[:,1,:] 
            sequences_term_mask[:,0] = False
        
       
        sequencesembs     = self.src_embedding(sequences) + self.src_positions.weight.unsqueeze(0)
        sequencesembs     = self.srcencoder(sequencesembs.view(sequences.size(0),sequences.size(2)*2,self.configuration["embedding_size"]).transpose(0,1)).transpose(0,1).view(sequences.size(0),sequences.size(1),sequences.size(2),self.configuration["embedding_size"])
        sequencesnonterms = sequencesembs[:,0,:,:]
        sequencesterms    = sequencesembs[:,1,:,:]

        ##### TERMINALS ENCODE PATH ###
        sequencesGAterms = self.normGA(self.ga(sequencesterms, sequences_term_mask)[0])

        # NON TERMINALS ENCODE PATH ###
        sequencesGAnonterms = self.normGA(self.ga(sequencesnonterms, sequences_nonterm_mask)[0])
        
        # TERMINALS + NON TERMINALS ###
        sequencesenc = torch.cat([sequencesGAnonterms, sequencesGAterms],dim=-1)
        sequencesenc = self.ln1(sequencesenc + self.fc1(self.ln0(self.dp0(F.relu(self.fc0(sequencesenc))))))
        return sequencesenc


    def forward(self, anchors, positives, negatives):
        return (self.encode(  anchors),
                self.encode(positives),
                self.encode(negatives))

