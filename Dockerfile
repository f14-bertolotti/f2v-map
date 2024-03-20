FROM ubuntu:latest
ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update

# INSTALL SYSTEM DEPENDENCIES 
RUN apt-get install -y wget python3 python3-pip openjdk-11-jdk make git gcc
# you can use also a later openjdk, if you do, remember to set the JAVA_HOME accordingly

# SET UP JAVA_HOME, REQUIRED FOR JEP 
ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64/

# INSTALL REQUIREMENTS
RUN pip3 install \
    torch \
    jep==4.1.0 \
    jsonlines \
    numpy \
    git+https://github.com/casics/spiral.git
RUN pip3 install bidict tqdm

ADD method-extractor /home/method-extractor
ADD spiral_fix.py /usr/local/lib/python3.10/dist-packages/spiral/utils.py
ADD f2v.pt /home
ADD f2v.py /home
ADD f2v-map.py /home
ADD info.pkl /home
ADD restructure.py /home

# SET UP JEP LIBRARY PATH, REQUIRED FOR THE FEATURE_EXTRACTOR 
ENV LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib/python3.10/dist-packages/jep

WORKDIR /home
RUN make -C method-extractor/ clean
RUN make -C method-extractor/ jars/code-parser.jar
