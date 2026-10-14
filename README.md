[![Build and deploy](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/build-and-deploy.yml/badge.svg)](https://github.com/EvaristeGalois11/UniBlock/actions/workflows/build-and-deploy.yml)

# UniBlock
University themed blockchain

### Build from source
`./mvnw verify`

**_To compile the project, you need at least the Java version set in [`.java-version`](.java-version)_**

### Run with podman
`podman run --rm ghcr.io/evaristegalois11/uniblock`

### Usage
```
usage: uniblock [-d <arg>] [-h] [-p]
 -d,--difficulty <arg>   Choose the difficulty of the mining
 -h,--help               Print this message
 -p,--progress           Show the progress of the mining
```
