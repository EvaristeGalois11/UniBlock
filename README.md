[![Build and deploy](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/build-and-deploy.yml/badge.svg)](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/build-and-deploy.yml)
[![CodeQL](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/codeql.yml/badge.svg)](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/codeql.yml)

# UniBlock
University themed blockchain

### Build from source
`./mvnw verify`

**_The project needs Java 20 to compile_**

### Run with podman
`podman run --rm ghcr.io/evaristegalois11/uniblock`

### Usage
```
usage: uniblock [-d <arg>] [-h] [-p]
 -d,--difficulty <arg>   Choose the difficulty of the mining
 -h,--help               Print this message
 -p,--progress           Show the progress of the mining
```
