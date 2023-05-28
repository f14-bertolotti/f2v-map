# f2v-map
This repository contains the scripts to generate statement-level fold2vec encodings from java source files.

## Requirements
- The only requirement is having [docker](https://www.docker.com/) installed.
  In particular, you need to be able to use docker as [non-root user](https://docs.docker.com/engine/install/linux-postinstall/).
- [wget](https://linux.die.net/man/1/wget) to download the torch model.
- [makefile](https://www.gnu.org/software/make/) to run commands.

## Usage
Put your java files inside the `java-files` directory.
The `makefile` contains the commands to run the scripts inside the container.
- `make f2v.pt` and `make info.pkl` download the model necessary to generate the encodings.
- `make data/.docker-build` builds the docker image.
- `make data/data.jsonl` runs the docker image to parse the java files inside the `java-files` directory. All the feature are stored in `data/data.jsonl`.
- `make data/f2vmap.jsonl` runs the docker image to generate encodings from the fold2vec model. Encodings alongside with features are stored in `data/f2vmap.
- `make clean` cleans the repository.

## Data
Encodings are stored `data/f2vmap.jsonl` using [jsonl](https://jsonlines.org/) format (one json object for line). 


## Citation
If you find this code to be useful for your research, please consider citing:
```
@article{fold2vec,
  title={Fold2Vec: Towards a Statement-Based Representation of Code for Code Comprehension},
  author={Bertolotti, Francesco and Cazzola, Walter},
  journal={ACM Transactions on Software Engineering and Methodology},
  volume={32},
  number={1},
  pages={1--31},
  year={2023},
  publisher={ACM New York, NY}
}
```
