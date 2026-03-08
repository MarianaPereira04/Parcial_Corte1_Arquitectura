# stack tecnológico actual — ERP Iglesia

## Resumen ejecutivo

El presente documento contiene un diagnóstico arquitectónico del sistema **ERP Iglesia**, basado en el análisis del repositorio del proyecto. El objetivo es identificar el stack tecnológico utilizado, evaluar la organización del código y analizar aspectos clave de la arquitectura como la separación de responsabilidades, el nivel de acoplamiento entre módulos, así como la escalabilidad y mantenibilidad de la solución.

A partir de esta evaluación se presentan observaciones sobre la calidad arquitectónica del sistema, riesgos detectados durante el análisis funcional y técnico, y un conjunto de recomendaciones priorizadas orientadas a mejorar la robustez, mantenibilidad y evolución futura del software.

---

# Stack tecnológico actual

El sistema está compuesto por una arquitectura web con separación entre **frontend y backend**, desplegada mediante contenedores.

## Backend

* **Lenguaje:** Java
* **Framework:** Spring Boot
* **Gestión de dependencias:** Maven (`pom.xml`)
* **Persistencia:** Spring Data JPA (repositorios `*Repository`)
* **Seguridad:** Spring Security con autenticación basada en JWT
* **Componentes de seguridad detectados:**

  * `SecurityConfig`
  * `JwtService`
  * `JwtAuthFilter`
  * `AuthUserDetailsService`
* **Contenerización:** `Dockerfile` para despliegue del backend

El backend expone una **API REST** mediante controladores (`*Controller.java`) y utiliza repositorios Spring Data para la interacción con la capa de persistencia.

---

## Frontend

* **Framework:** Angular (TypeScript)
* **Configuración y build:** `angular.json`, `package.json`
* **Arquitectura:** componentes organizados por funcionalidad (`church`, `courses`, `people`, etc.)
* **Servicios de comunicación:** `api.service.ts`, `auth.service.ts`
* **Contenerización y despliegue:** `Dockerfile` y configuración `nginx.conf`

El frontend implementa una estructura modular donde cada módulo contiene sus propios componentes y servicios, lo que favorece la organización del código y la separación entre lógica de presentación y acceso a datos.

---

## Orquestación

El sistema utiliza:

* **Docker**
* **Docker Compose**

Archivo principal:

```
docker-compose.yml
```

Este archivo permite orquestar los distintos contenedores de la aplicación (frontend y backend) facilitando su despliegue y ejecución en entornos de desarrollo o pruebas.

---

# Observaciones sobre la estructura del código

A partir del análisis del repositorio se identifican las siguientes características estructurales:

* El backend sigue un patrón similar a **MVC simplificado**, donde se distinguen claramente:

  * controladores REST (`*Controller`)
  * entidades de dominio
  * repositorios (`*Repository`)

* La seguridad se encuentra centralizada mediante **Spring Security** y filtros JWT, lo cual es una práctica adecuada para aplicaciones API REST.

* Sin embargo, **no se observa una capa de servicios claramente definida**. En varios casos la lógica de negocio parece estar implementada directamente en los controladores, lo cual introduce riesgos de acoplamiento y dificulta la evolución del sistema.

En el frontend, la organización modular de Angular facilita la separación entre componentes de interfaz y servicios encargados de consumir la API del backend.

---

# Separación de responsabilidades

## Aspectos positivos

Se identifican varias decisiones correctas desde el punto de vista arquitectónico:

* Separación clara entre **frontend y backend**.
* Uso de **servicios Angular** para centralizar las llamadas a la API.
* Persistencia desacoplada mediante **Spring Data Repositories**.
* Configuración de seguridad centralizada mediante **Spring Security** y filtros JWT.

Estas decisiones contribuyen a mantener un grado aceptable de organización del sistema.

---

## Debilidades identificadas

A pesar de los puntos positivos, se detectan algunas debilidades arquitectónicas:

### Ausencia de capa de servicios

La falta de una capa `service` consistente provoca que:

* Parte de la lógica de negocio esté distribuida en los controladores
* Las validaciones no estén centralizadas
* Se incremente el acoplamiento entre API y persistencia

Una arquitectura más mantenible debería seguir el patrón:

```
Controller
   ↓
Service
   ↓
Repository
```

### Validaciones insuficientes

Durante la exploración funcional se identificó que el sistema permite:

* Ingreso de datos inválidos
* Registros duplicados
* Ausencia de restricciones en ciertos campos

Esto sugiere que faltan:

* Validaciones a nivel de **DTOs o entidades**
* Restricciones a nivel de **base de datos**

---

# Acoplamiento entre módulos

En el **frontend**, el acoplamiento entre componentes es relativamente bajo gracias al uso de servicios compartidos para la comunicación con la API.

En el **backend**, en cambio, se observa que los controladores dependen directamente de los repositorios. Esto genera un **acoplamiento fuerte entre la capa de presentación y la capa de persistencia**.

Esta dependencia directa puede dificultar:

* La implementación de pruebas unitarias
* La evolución del modelo de datos
* El cambio de mecanismos de persistencia

La introducción de una capa de servicios ayudaría a reducir este acoplamiento.

---

# Escalabilidad

El sistema presenta algunas características favorables para su escalabilidad:

* Uso de **contenedores Docker**
* Orquestación mediante **Docker Compose**

Esto facilita la replicación de instancias y el despliegue en diferentes entornos.

No obstante, la aplicación sigue una **arquitectura monolítica**, donde todos los módulos funcionales se ejecutan dentro del mismo backend. En este modelo, el escalado se limita principalmente a la replicación de la aplicación completa detrás de un balanceador de carga.

Adicionalmente, no se identificó el uso de herramientas de migración de base de datos como **Flyway** o **Liquibase**, lo cual sería recomendable para gestionar la evolución del esquema de datos en entornos productivos.

---

# Mantenibilidad

## Fortalezas

* Organización del código con nombres claros (`PersonController`, `OfferingRepository`, etc.).
* Estructura modular del frontend basada en funcionalidades.
* Uso de frameworks ampliamente adoptados (Spring Boot y Angular).

## Oportunidades de mejora

Para mejorar la mantenibilidad del sistema se recomienda:

* Introducir una capa de servicios que desacople controladores y repositorios
* Fortalecer las validaciones de datos
* Mejorar el manejo de errores de la API
* Incorporar pruebas automatizadas

---

# Riesgos funcionales detectados en el flujo admin y usuario

Durante las pruebas exploratorias del sistema se identificaron los siguientes riesgos:

1. El **dashboard presenta fallos en la carga inicial**, lo que puede afectar la experiencia de usuario.
2. Los **usuarios creados no siempre aparecen en el listado**, lo que sugiere problemas en persistencia o mapeo de datos.
3. Existen **validaciones débiles**, permitiendo el ingreso de datos incorrectos o incompletos.
4. Se detecta posibilidad de **registros duplicados** en módulos como inscripciones y ofrendas.
5. Las **acciones relacionadas con pagos** presentan errores o comportamientos inconsistentes.

---

# Conclusión

El sistema ERP Iglesia presenta una base tecnológica sólida al utilizar **Spring Boot en el backend y Angular en el frontend**, junto con un esquema de autenticación basado en JWT y despliegue mediante contenedores Docker.

No obstante, el análisis arquitectónico evidencia oportunidades de mejora, particularmente en la **separación de responsabilidades**, la **gestión de validaciones**, y la **reducción del acoplamiento entre capas**.

La implementación de las recomendaciones propuestas permitirá mejorar la mantenibilidad, robustez y capacidad de evolución del sistema, facilitando su crecimiento futuro y su adaptación a nuevos requerimientos.

