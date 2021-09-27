# Java Election RMI

Este programa tem como finalidade criar um sistema de voto eleitoral utilizando o Java RMI.

## Setup

Você pode compilar manualmente ou baixar os artefatos já processados pelo github neste repositorio, através dos artefatos armazenados no [Github Actions](https://github.com/LDAMD/eleicoes-com-java-rmi-luis_gustavo_vaz/actions).

Caso você queira compilar manualmente, você pode utilizar o Ubuntu LTS e apenas utilizar o comando abaixo: 
```
make
```
Este comando vai baixar automaticamente a LTS do Java, compilar os binarios de cliente e de servidor e armazenar em dois arquivos .JAR dentro da pasta ```/bin/```.

É importante que na mesma pasta de execução desses arquivos .JAR você tenha uma pasta ```/data/```, ela deve conter um arquivo com o nome ```senadores.csv```, de acordo com o especificado pelo professor nesta tarefa, conforme a imagem abaixo:

<img src="https://i.imgur.com/eBL8pDS.png" alt="Imagem da Aplicação"/>

Após essas etapas, basta executar os arquivos .JAR, primeiramente o servidor, e depois a quantidade de clientes que desejar utilizar.