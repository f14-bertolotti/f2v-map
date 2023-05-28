
f2v.pt:
	wget --no-check-certificate 'https://docs.google.com/uc?export=download&id=1yY306b7hZ3Pm92fmZBOJkRSwipkkdriI' -O f2v.pt

info.pkl:
	wget --no-check-certificate 'https://docs.google.com/uc?export=download&id=1Eslovi9MvGghIZVwVtrkmgR9zT6LLETA' -O info.pkl

data/.docker-build: Dockerfile restructure.py f2v.py f2v-map.py info.pkl f2v.pt spiral_fix.py
	docker build --rm . -t f14:f2v -f ./Dockerfile
	touch data/.docker-build

data/data.jsonl: data/.docker-build
	docker run \
		--mount type=bind,source=./java-files,target=/home/java-files \
		--mount type=bind,source=./data,target=/home/data \
		--user "$(shell id -u):$(shell id -g)" \
		-it f14:f2v \
		java -cp method-extractor/jars/code-parser.jar src.main.java.Client \
		-input_path java-files \
		-output_path data/data.jsonl \
		-verbose

data/f2vmap.jsonl: data/data.jsonl data/.docker-build
	docker run \
		--mount type=bind,source=./java-files,target=/home/java-files \
		--mount type=bind,source=./data,target=/home/data \
		--user "$(shell id -u):$(shell id -g)" \
		-it f14:f2v \
		python3 f2v-map.py

data/java_encoding_files: data/f2vmap.jsonl data/.docker-build 
	mkdir -p data/java_encoding_files
	touch data/java_encoding_files
	docker run \
		--mount type=bind,source=./java-files,target=/home/java-files \
		--mount type=bind,source=./data,target=/home/data \
		--user "$(shell id -u):$(shell id -g)" \
		-it f14:f2v \
		python3 restructure.py

clean:
	make -C method-extractor clean
	rm -rf __pycache__
	rm -rf data/*
	rm -f f2v.pt
	rm -f info.pkl
	rm data/.docker-build
	docker image rm -f f14:f2v
