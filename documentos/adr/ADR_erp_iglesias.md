# ADR: Mejoras arquitectónicas propuestas para ERP_Iglesias

Con base en el análisis del código backend y frontend y los problemas funcionales detectados, este ADR presenta 10 decisiones arquitectónicas concretas orientadas a aplicar patrones de diseño y principios SOLID para mejorar mantenibilidad, escalabilidad y calidad del software.

---

# ADR-001: Implementación de una capa de servicios (Service Layer)

## Estado

- Implementado

## Contexto

Durante la exploración del sistema se identificó que algunos controladores manejan directamente operaciones relacionadas con la lógica de negocio y la persistencia de datos. Este enfoque puede generar controladores demasiado grandes y difíciles de mantener, ya que concentran múltiples responsabilidades dentro de una misma clase.

En arquitecturas basadas en frameworks como Spring Boot, es recomendable separar claramente las responsabilidades entre las diferentes capas del sistema. Los controladores deberían encargarse principalmente de gestionar las solicitudes HTTP, mientras que la lógica de negocio debe ubicarse en una capa intermedia especializada.

## Decisión

Se decidió introducir formalmente una **capa de servicios (Service Layer)** dentro de la arquitectura del sistema.

Esta capa se ubicará entre los controladores y los repositorios, y tendrá como responsabilidad principal encapsular la lógica de negocio de la aplicación. Los controladores delegarán las operaciones al servicio correspondiente, y los servicios interactuarán con los repositorios para acceder a la base de datos.

De esta forma, el flujo de interacción quedará estructurado de la siguiente manera:

**Controller → Service → Repository → Base de datos**

## Justificación

La introducción de una capa de servicios permite separar claramente las responsabilidades dentro de la arquitectura del sistema, lo que facilita la organización del código y mejora su mantenibilidad.

Este enfoque también permite reutilizar la lógica de negocio desde distintos controladores o componentes del sistema sin duplicar código. Además, al centralizar las reglas de negocio en una capa específica, se facilita la implementación de pruebas unitarias, ya que los servicios pueden probarse de forma independiente de la capa web.

Asimismo, esta decisión sigue el principio **Single Responsibility Principle (SRP)** del modelo SOLID, que establece que cada componente del sistema debe tener una única responsabilidad bien definida.

## Consecuencias

**Consecuencias positivas**

* Mejora la organización general de la arquitectura del sistema
* Reduce la complejidad de los controladores
* Facilita la reutilización de lógica de negocio
* Permite implementar pruebas unitarias de forma más sencilla

**Consecuencias negativas o riesgos**

* Implica reorganizar parte del código existente para trasladar lógica desde los controladores hacia los servicios
* Puede aumentar ligeramente el número de clases en el proyecto, aunque esto contribuye a una mejor separación de responsabilidades

---

# ADR-002: Uso del patrón DTO (Data Transfer Object)

## Estado

- Propuesto

## Contexto

En aplicaciones web es común que las entidades utilizadas para persistencia en la base de datos se expongan directamente en las respuestas de la API. Sin embargo, este enfoque puede generar problemas de acoplamiento entre la estructura interna del sistema y la interfaz pública que consumen los clientes.

Además, las entidades de persistencia pueden contener información que no debería exponerse a los usuarios o que no es necesaria para ciertas operaciones de la API.

## Decisión

Se decidió implementar el **patrón DTO (Data Transfer Object)** para gestionar el intercambio de datos entre la API y los clientes del sistema.

Los DTOs se utilizarán para representar la información que entra y sale de los controladores, mientras que las entidades continuarán utilizándose exclusivamente para la persistencia en la base de datos.

Esto implica que las operaciones del sistema incluirán procesos de conversión entre entidades y DTOs.

## Justificación

El uso de DTOs permite desacoplar la estructura interna del modelo de datos de la representación utilizada en la API. Esto facilita la evolución del sistema, ya que cambios en la base de datos no necesariamente afectarán a los contratos de la API.

Además, permite controlar de forma precisa qué información se expone a los clientes, evitando la exposición innecesaria de atributos sensibles o irrelevantes.

Este patrón también facilita la validación de datos de entrada, ya que los DTOs pueden incorporar anotaciones de validación específicas para cada operación.

## Consecuencias

**Consecuencias positivas**

* Reduce el acoplamiento entre la capa de persistencia y la API
* Permite controlar la información expuesta en las respuestas
* Facilita la evolución del modelo de datos sin afectar a los clientes
* Mejora la claridad de las operaciones del sistema

**Consecuencias negativas o riesgos**

* Introduce una capa adicional de conversión entre entidades y DTOs
* Requiere mantener clases adicionales dentro del proyecto

---

# ADR-003: Implementación de validación de datos mediante Bean Validation

## Estado

- Implementado

## Contexto

Durante la exploración del sistema se identificó que algunas operaciones reciben datos provenientes del cliente sin aplicar mecanismos de validación consistentes antes de procesarlos o almacenarlos en la base de datos.

La ausencia de validación puede provocar inconsistencias en los datos almacenados, errores en tiempo de ejecución o comportamientos inesperados dentro del sistema.

## Decisión

Se decidió implementar un mecanismo de validación de datos utilizando **Bean Validation**, a través de anotaciones estándar como:

* `@NotNull`
* `@NotBlank`
* `@Email`
* `@Size`

Estas validaciones se aplicarán principalmente sobre los DTOs que representan los datos de entrada en los endpoints de la API.

## Justificación

La validación temprana de datos permite detectar errores antes de que la lógica de negocio procese información incorrecta o incompleta.

Además, el uso de anotaciones declarativas permite centralizar las reglas de validación directamente en los modelos de datos, lo que mejora la legibilidad del código y reduce la necesidad de implementar validaciones manuales repetitivas en los controladores.

Este enfoque también mejora la calidad general de los datos almacenados en la base de datos.

## Consecuencias

**Consecuencias positivas**

* Mejora la integridad de los datos manejados por el sistema
* Reduce errores causados por información inválida
* Centraliza las reglas de validación dentro del modelo de datos
* Simplifica la lógica dentro de los controladores

**Consecuencias negativas o riesgos**

* Puede requerir ajustes en algunos endpoints existentes para adaptarse a las nuevas reglas de validación

---

# ADR-004: Implementación de manejo global de excepciones

## Estado

- Implementado

## Contexto

En aplicaciones que exponen APIs REST, los errores pueden generarse en diferentes capas del sistema, como controladores, servicios o repositorios. Si estos errores no se manejan de manera consistente, pueden producir respuestas poco claras para los clientes o incluso exponer información interna del sistema.

## Decisión

Se decidió implementar un **mecanismo global de manejo de excepciones** utilizando las capacidades proporcionadas por el framework, mediante componentes como `@ControllerAdvice`.

Este mecanismo permitirá capturar excepciones comunes del sistema y transformarlas en respuestas HTTP estructuradas y consistentes.

## Justificación

Centralizar el manejo de errores permite estandarizar la forma en que el sistema comunica problemas a los clientes de la API.

Además, evita duplicar bloques de manejo de excepciones en múltiples controladores, lo que simplifica el código y facilita el mantenimiento del sistema.

Este enfoque también permite mapear diferentes tipos de errores a códigos HTTP apropiados, mejorando la claridad de la API.

## Consecuencias

**Consecuencias positivas**

* Respuestas de error más claras y consistentes
* Reducción de duplicación de código en los controladores
* Mejora la experiencia de los consumidores de la API

**Consecuencias negativas o riesgos**

* Requiere definir una estructura estándar para las respuestas de error
* Implica identificar y mapear correctamente las excepciones del sistema

---

# ADR-005: Uso del patrón Strategy para la gestión de pagos

## Estado

- Propuesto

## Contexto

El sistema incluye funcionalidades relacionadas con el procesamiento de pagos. Dependiendo del método de pago utilizado, el proceso puede variar en términos de validaciones, lógica de cálculo o integración con servicios externos.

Si todas estas variaciones se manejan mediante estructuras condicionales dentro de una misma clase, el código puede volverse difícil de mantener y extender.

## Decisión

Se decidió aplicar el **patrón de diseño Strategy** para gestionar las diferentes formas de procesamiento de pagos.

Cada método de pago será implementado como una estrategia independiente que seguirá una interfaz común. El sistema seleccionará la estrategia adecuada en tiempo de ejecución según el tipo de pago solicitado.

## Justificación

El patrón Strategy permite encapsular diferentes algoritmos o comportamientos dentro de clases separadas que comparten una interfaz común.

Esto facilita la incorporación de nuevos métodos de pago sin modificar el código existente, lo cual sigue el principio **Open/Closed Principle (OCP)** de SOLID.

Además, mejora la claridad del código al evitar estructuras condicionales extensas.

## Consecuencias

**Consecuencias positivas**

* Facilita la extensión del sistema con nuevos métodos de pago
* Mejora la organización del código
* Reduce la complejidad de las clases responsables del procesamiento de pagos

**Consecuencias negativas o riesgos**

* Incrementa el número de clases dentro del sistema
* Requiere implementar una lógica de selección de estrategia

---

# ADR-006: Uso de inyección de dependencias por constructor

## Estado

- Implementado

## Contexto

El sistema utiliza un framework que permite la inyección de dependencias para gestionar la creación y provisión de componentes dentro de la aplicación. Sin embargo, existen diferentes formas de implementar esta inyección, como la inyección por campo, por métodos setter o por constructor.

La inyección por campo suele ser una práctica común en proyectos pequeños, pero presenta algunas desventajas en términos de pruebas, claridad de dependencias y control sobre la inicialización de los objetos.

## Decisión

Se decidió adoptar como práctica estándar del proyecto la **inyección de dependencias mediante constructores** para todos los componentes del sistema, especialmente en controladores, servicios y otros componentes gestionados por el contenedor de Spring.

Esto implica que todas las dependencias necesarias para el funcionamiento de una clase se declararán explícitamente como parámetros de su constructor.

## Justificación

La inyección por constructor permite expresar de manera explícita cuáles son las dependencias necesarias para que una clase funcione correctamente. Esto mejora la legibilidad del código y facilita la comprensión de las relaciones entre los distintos componentes del sistema.

Además, esta forma de inyección facilita la realización de pruebas unitarias, ya que permite crear instancias de las clases proporcionando implementaciones simuladas (mocks) de sus dependencias.

Esta decisión también contribuye a aplicar el principio **Dependency Inversion Principle (DIP)**, promoviendo una arquitectura más desacoplada y flexible.

## Consecuencias

**Consecuencias positivas**

* Las dependencias de cada clase se vuelven explícitas y más fáciles de comprender
* Facilita la implementación de pruebas unitarias
* Promueve una arquitectura menos acoplada
* Mejora la seguridad en la inicialización de los objetos

**Consecuencias negativas o riesgos**

* Los constructores pueden volverse extensos si una clase depende de muchos componentes, lo cual puede indicar que la clase tiene demasiadas responsabilidades
* Puede requerir refactorizar clases existentes que utilizan otros tipos de inyección

---

# ADR-007: Uso de interfaces para la definición de servicios

## Estado

- Propuesto

## Contexto

En la capa de servicios del sistema se implementa la lógica de negocio que coordina las operaciones entre los controladores y los repositorios. En algunos casos, los servicios pueden implementarse directamente como clases concretas sin una abstracción intermedia.

Sin embargo, cuando las clases de servicio no utilizan interfaces, los componentes que dependen de ellas quedan acoplados directamente a sus implementaciones concretas, lo que reduce la flexibilidad del sistema.

## Decisión

Se decidió definir **interfaces para todos los servicios del sistema**, de modo que cada servicio tenga una interfaz que describa sus operaciones y una clase que implemente dicha interfaz.

Por ejemplo:

```
UserService
UserServiceImpl
```

Los controladores y otros componentes dependerán de las interfaces en lugar de depender directamente de las implementaciones concretas.

## Justificación

El uso de interfaces permite desacoplar las dependencias dentro del sistema, lo que facilita la modificación o sustitución de implementaciones sin afectar a los componentes que las utilizan.

Además, este enfoque mejora la capacidad de realizar pruebas unitarias, ya que permite sustituir fácilmente las implementaciones reales por versiones simuladas durante las pruebas.

Esta práctica también refuerza el principio **Dependency Inversion Principle (DIP)**, que promueve depender de abstracciones en lugar de implementaciones concretas.

## Consecuencias

**Consecuencias positivas**

* Reduce el acoplamiento entre componentes del sistema
* Facilita la sustitución o evolución de implementaciones de servicios
* Mejora la capacidad de realizar pruebas unitarias

**Consecuencias negativas o riesgos**

* Introduce clases adicionales en el proyecto
* Puede considerarse innecesario en servicios muy simples, aunque contribuye a mantener consistencia arquitectónica

---

# ADR-008: Introducción del patrón Factory para la creación de objetos complejos

## Estado

- Propuesto

## Contexto

En algunos casos dentro del sistema pueden existir procesos de creación de objetos que requieren múltiples parámetros, validaciones o configuraciones específicas. Cuando esta lógica se implementa directamente mediante constructores dentro de los controladores o servicios, puede generar código repetitivo y difícil de mantener.

Además, el uso directo de constructores puede generar acoplamiento entre las clases consumidoras y la forma específica en que se crean los objetos.

## Decisión

Se decidió introducir el **patrón de diseño Factory** para encapsular la lógica de creación de objetos complejos dentro de componentes especializados encargados de instanciarlos.

Las fábricas se encargarán de construir los objetos necesarios a partir de los datos recibidos, manteniendo centralizada la lógica de inicialización.

## Justificación

El patrón Factory permite separar la lógica de creación de objetos de la lógica de negocio que los utiliza. Esto facilita la reutilización de procesos de creación y reduce la duplicación de código en diferentes partes del sistema.

Además, si en el futuro se requiere modificar la forma en que se crean ciertos objetos, estos cambios podrán realizarse dentro de la fábrica sin afectar a las clases que dependen de dichos objetos.

Este enfoque contribuye a mejorar la organización del código y a reducir el acoplamiento entre componentes.

## Consecuencias

**Consecuencias positivas**

* Centraliza la lógica de creación de objetos complejos
* Reduce duplicación de código
* Disminuye el acoplamiento entre componentes del sistema
* Facilita modificaciones futuras en el proceso de creación de objetos

**Consecuencias negativas o riesgos**

* Introduce clases adicionales dentro del proyecto
* Puede resultar innecesario si los objetos a crear son demasiado simples

---

# ADR-009: Uso de un formato estándar de respuesta para la API (Response Wrapper)

## Estado

- Implementado

## Contexto

Las APIs REST pueden devolver respuestas con diferentes estructuras dependiendo del endpoint o del tipo de operación realizada. Cuando no existe un formato estandarizado, los clientes que consumen la API deben adaptarse a múltiples estructuras de respuesta, lo que dificulta su integración y mantenimiento.

Además, la falta de estandarización complica la inclusión de metadatos adicionales como mensajes, estados de operación o información de errores.

## Decisión

Se decidió adoptar un **formato estándar para todas las respuestas de la API**, utilizando un objeto contenedor que encapsule los datos devueltos por el sistema.

Este objeto incluirá campos como:

* estado de la operación
* mensaje descriptivo
* datos devueltos por la operación

De esta manera, todas las respuestas de la API seguirán una estructura uniforme.

## Justificación

Estandarizar el formato de las respuestas facilita el consumo de la API por parte de clientes externos, ya que estos pueden esperar siempre una estructura consistente.

Además, permite incluir información adicional sobre el resultado de las operaciones sin modificar la estructura principal de los datos devueltos.

Este enfoque también mejora la claridad y la mantenibilidad del diseño de la API.

## Consecuencias

**Consecuencias positivas**

* Mayor consistencia en las respuestas de la API
* Facilita el consumo de la API por parte de clientes externos
* permite incluir información adicional de manera estructurada

**Consecuencias negativas o riesgos**

* Requiere adaptar los endpoints existentes para cumplir con el nuevo formato
* Introduce una capa adicional de encapsulamiento en las respuestas

---

# ADR-010: Organización del código por módulos funcionales (Package-by-Feature)

## Estado

- Propuesto

## Contexto

Una forma común de organizar proyectos es agrupar las clases por tipo técnico, por ejemplo separando controladores, servicios y repositorios en paquetes independientes. Aunque este enfoque es sencillo, puede dificultar la comprensión del sistema cuando crece en tamaño, ya que las clases relacionadas con una misma funcionalidad quedan distribuidas en diferentes paquetes.

Esto puede dificultar la navegación del código y aumentar el acoplamiento entre módulos funcionales.

## Decisión

Se decidió reorganizar la estructura del proyecto siguiendo un enfoque **package-by-feature**, en el cual las clases se agrupan según la funcionalidad del sistema a la que pertenecen.

Por ejemplo:

```
user
 ├ controller
 ├ service
 ├ repository
 ├ dto

payment
 ├ controller
 ├ service
 ├ repository
 ├ dto
```

Cada módulo funcional contendrá sus propios controladores, servicios, repositorios y modelos relacionados.

## Justificación

Organizar el código por funcionalidades permite comprender más fácilmente las responsabilidades de cada módulo del sistema. También facilita el mantenimiento del proyecto, ya que todos los componentes relacionados con una misma funcionalidad se encuentran agrupados en un mismo lugar.

Este enfoque también favorece una evolución más ordenada del sistema, ya que nuevas funcionalidades pueden añadirse como módulos independientes sin afectar a la organización existente.

## Consecuencias

**Consecuencias positivas**

* Mejora la organización y navegación del código
* Facilita la comprensión de la arquitectura del sistema
* Permite modularizar funcionalidades del sistema

**Consecuencias negativas o riesgos**

* Puede requerir reorganizar paquetes existentes dentro del proyecto
* Algunos desarrolladores pueden estar más acostumbrados a estructuras tradicionales basadas en capas técnicas

---

**Nota:** De los 10 ADR propuestos, se implementaron los siguientes:
ADR-001, ADR-003, ADR-004, ADR-006 y ADR-009.
