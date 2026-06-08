# Requisito de execução/deploy no Tomcat 11.x via Maven

Este projeto atende à opção **(b)** do enunciado:

```bash
mvn cargo:run
```

A configuração está no `pom.xml`, usando:

```xml
<packaging>war</packaging>
```

```xml
<plugin>
    <groupId>org.codehaus.cargo</groupId>
    <artifactId>cargo-maven3-plugin</artifactId>
    <version>${cargo.maven3.plugin.version}</version>
    ...
    <containerId>tomcat11x</containerId>
    ...
</plugin>
```

O projeto também gera o arquivo WAR com:

```bash
mvn clean package
```

Arquivo gerado:

```text
target/candidatura-estagio-t5.war
```

A classe principal estende `SpringBootServletInitializer`, permitindo deploy em Tomcat externo.
