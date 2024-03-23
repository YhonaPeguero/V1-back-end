# Proyecto de Aplicación Backend

## Tabla de Contenidos

- [Proyecto de Aplicación Backend](#proyecto-de-aplicación-backend)
  - [Tabla de Contenidos](#tabla-de-contenidos)
  - [Resumen](#resumen)
  - [Configuración y Ejecución](#configuración-y-ejecución)
    - [Requisitos Previos](#requisitos-previos)
    - [Pasos para la Configuración](#pasos-para-la-configuración)
    - [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
  - [Construido con:](#construido-con)
  - [Funcionalidades Clave](#funcionalidades-clave)

## Resumen

Esta herramienta de línea de comando backend está diseñada para analizar archivos Java, describiendo atributos y métodos de clases de manera dinámica. Este documento proporciona instrucciones detalladas sobre cómo instalar y ejecutar la aplicación backend.

## Configuración y Ejecución

### Requisitos Previos

Antes de ejecutar esta aplicación, asegúrate de tener instalados los siguientes componentes:

- Java Development Kit (JDK) 1.8.0
- Apache Maven 3.8.5
- Un IDE compatible con proyectos Maven, como IntelliJ IDEA

### Pasos para la Configuración

1. Clona el repositorio del proyecto backend en tu máquina local usando:

```bash
git clone https://github.com/YhonaPeguero/V1-back-end
```

2. Navega al directorio del proyecto:

```bash
cd V1-back-end
```

3. Asegúrate de que la configuración de tu IDE apunte a la versión correcta del JDK (1.8.0) y Maven (3.8.5).

### Ejecución de la Aplicación

#### Configuración con Intellij IDEA :

![](./src/assets/img/image1.webp)

Una vez configurado el proyecto, puedes compilar y empaquetar la aplicación con el siguiente comando Maven:

```bash
mvn clean package
```

Para ejecutar la aplicación y analizar un archivo de clase Java, usa:

```bash
java -jar target/backend-1.0-SNAPSHOT.jar path/a/Person.txt
```

Reemplaza `path/a/Person.txt` con la ruta al archivo de texto que contiene la definición de la clase Java que deseas analizar.

## Construido con:

- **Back-end**:
  - Java (1.8.0)
  - Maven (3.8.5)
  - IntelliJ IDEA

## Funcionalidades Clave

- Análisis de archivos de clase Java para extraer y describir información de atributos y métodos.
- Salida formateada que muestra detalles de la clase de forma estructurada y legible.
