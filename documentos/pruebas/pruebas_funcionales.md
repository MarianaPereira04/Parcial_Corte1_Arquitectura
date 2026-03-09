# Pruebas funcionales — ADR-001

**Implementación de la capa Service**

## 1. Archivos modificados

Para implementar la arquitectura **Controller → Service → Repository** se añadieron y refactorizaron los siguientes archivos.

### Servicios creados

* `AuthService.java`
* `ChurchService.java`
* `CourseService.java`
* `PersonService.java`
* `EnrollmentService.java`
* `OfferingService.java`
* `PaymentService.java`
* `UserService.java`

Estos servicios encapsulan la lógica de negocio que previamente se encontraba en los controladores.

### Controladores refactorizados

````markdown
# Pruebas funcionales — ADR-001

**Implementación de la capa Service**

## 1. Archivos modificados

Para implementar la arquitectura **Controller → Service → Repository** se añadieron y refactorizaron los siguientes archivos.

### Servicios creados

* `AuthService.java`
* `ChurchService.java`
* `CourseService.java`
* `PersonService.java`
* `EnrollmentService.java`
* `OfferingService.java`
* `PaymentService.java`
* `UserService.java`

Estos servicios encapsulan la lógica de negocio que previamente se encontraba en los controladores.

### Controladores refactorizados

Los siguientes controladores fueron modificados para delegar la lógica a la nueva capa de servicios:

* `AuthController.java`
* `ChurchController.java`
* `CourseController.java`
* `EnrollmentController.java`
* `OfferingController.java`
* `PaymentController.java`
* `PersonController.java`
* `UserController.java`

También se actualizó:

* `DataInitializer.java` para utilizar `UserService`.

No se modificaron:

* entidades
* repositorios
* rutas públicas de la API

---

# 2. Verificación funcional

Para verificar que la refactorización no afectó el comportamiento del sistema, se realizaron pruebas manuales de los endpoints utilizando Postman contra el backend desarrollado en Spring Boot ejecutándose en Docker.

Base URL utilizada:

```
http://localhost:8080
```
---

# 3. Pruebas realizadas

## 3.1 Autenticación de usuario

**Endpoint**

```
POST /api/auth/login
```

**Body**

```json
{
  "email": "admin@parroquia.com",
  "password": "Admin123!"
}
```

**Resultado obtenido**

* Respuesta `200 OK`
* Generación de token JWT
* Retorno de email y rol del usuario autenticado


![Login exitoso](../../imagenes/sistema%20mejorado/P1/Autenticación_exitosa.png)

**Figura 1.** Autenticación exitosa mediante el endpoint `/api/auth/login`.

Esta prueba confirma que la lógica de autenticación fue correctamente delegada al servicio `AuthService`.

---

## 3.2 Creación de usuario

**Endpoint**

```
POST /api/users
```

**Resultado esperado**

* Usuario creado correctamente
* Validación de email duplicado


![Login exitoso](../../imagenes/sistema%20mejorado/P1/Creación_usuario.png)

**Figura 2.** Creación de usuario mediante el endpoint `/api/users`.

Esta prueba confirma que el controlador delega la lógica al servicio `UserService`.

---

# 4. Resultado

Las pruebas realizadas demuestran que:

* Los endpoints mantienen su funcionamiento original.
* La refactorización hacia la arquitectura

```
Controller → Service → Repository
```

no alteró el comportamiento de la API.

---

# 5. Conclusión

La implementación de la capa **Service Layer** permitió mejorar la separación de responsabilidades del sistema, manteniendo intacta la funcionalidad existente.

---

# Pruebas funcionales — ADR-003

**Validación de datos mediante Bean Validation**

## 1. Archivos modificados

Para implementar la validación automática de los datos recibidos en las solicitudes HTTP se modificaron varios controladores del sistema agregando soporte para **Bean Validation**.

Entre ellos:

* `AuthController.java`
* `ChurchController.java`
* `CourseController.java`
* `EnrollmentController.java`
* `OfferingController.java`
* `PersonController.java`
* `UserController.java`

En estos controladores se añadió la anotación `@Valid` en los parámetros `@RequestBody`, lo cual permite que las validaciones definidas en los DTO se ejecuten automáticamente cuando el sistema recibe una solicitud.

### Ejemplo de validaciones aplicadas

En los DTO utilizados por los endpoints se emplean anotaciones como:

```java
@NotBlank
String email;

@NotBlank
String password;

@Email
String email;
```

Estas validaciones permiten asegurar que los datos recibidos cumplan ciertas reglas antes de que la solicitud llegue a la capa de servicios.

No se modificaron:

* entidades
* repositorios
* servicios

Esto mantiene la arquitectura definida en el ADR-001:

```
Controller → Service → Repository
```

---

# 2. Verificación funcional

Para verificar el funcionamiento de las validaciones se realizaron pruebas manuales utilizando **Postman** contra el backend ejecutándose en Docker.

Base URL utilizada:

```
http://localhost:8080
```

Las pruebas consistieron en enviar solicitudes de autenticación con datos inválidos para comprobar que el sistema detecta errores en la información recibida.

---

# 3. Pruebas realizadas

## 3.1 Prueba 1 — Email vacío en login

**Endpoint**

```
POST /api/auth/login
```

**Body enviado**

```json
{
  "email": "",
  "password": "Admin123!"
}
```

**Resultado esperado**

* El sistema rechaza la solicitud debido a que el campo `email` está vacío.
* La validación `@NotBlank` impide procesar la autenticación.

![Login exitoso](../../imagenes/sistema%20mejorado/P2/Email_vacio.png)
*(request enviado desde Postman y respuesta del backend)*

---

## 3.2 Prueba 2 — Email con formato inválido

**Endpoint**

```
POST /api/auth/login
```

**Body enviado**

```json
{
  "email": "correo-invalido",
  "password": "Admin123!"
}
```

**Resultado esperado**

* El sistema detecta que el campo `email` no cumple con el formato válido de correo electrónico.
* La validación `@Email` genera un error de validación.

![Login exitoso](../../imagenes/sistema%20mejorado/P2/Email_formato_invalido.png)
*(request enviado desde Postman y respuesta del backend)*

---

# 4. Resultado

Las pruebas realizadas permiten observar que el sistema identifica datos inválidos en las solicitudes de autenticación.

Las validaciones se ejecutan automáticamente antes de que la solicitud llegue a la lógica de negocio del sistema.

---

# 5. Conclusión

La implementación de **Bean Validation** permite garantizar que los datos recibidos por la API cumplen con reglas básicas de integridad antes de ser procesados.

Esto mejora la robustez del sistema y mantiene la separación de responsabilidades establecida en la arquitectura:

```
Controller → Service → Repository
```

---

# Pruebas funcionales — ADR-004

**Implementación de manejo global de excepciones**

---

# 1. Archivos modificados

Para implementar el manejo global de excepciones se creó un componente centralizado encargado de interceptar errores generados en diferentes capas del sistema.

### Clase creada

* `GlobalExceptionHandler.java`

Esta clase utiliza la anotación:

```
@RestControllerAdvice
```

para interceptar excepciones generadas en controladores, servicios o repositorios y convertirlas en respuestas HTTP estructuradas y consistentes.

La estructura estándar de error devuelta por la API es:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción del error",
  "timestamp": "2026-03-09T10:00:00Z"
}
```

### Excepciones manejadas

El `GlobalExceptionHandler` implementa controladores para las siguientes excepciones:

| Excepción                         | Código HTTP                     |
| --------------------------------- | ------------------------------- |
| `MethodArgumentNotValidException` | 400 Bad Request                 |
| `ResponseStatusException`         | Según el status de la excepción |
| `EntityNotFoundException`         | 404 Not Found                   |
| `IllegalArgumentException`        | 400 Bad Request                 |
| `RuntimeException`                | 500 Internal Server Error       |

No se modificaron:

* entidades
* repositorios
* rutas de la API

Los controladores y servicios continúan lanzando excepciones normalmente, mientras que el `GlobalExceptionHandler` se encarga de transformarlas en respuestas HTTP consistentes.

---

# 2. Verificación funcional

Para verificar el funcionamiento del manejo global de excepciones se realizaron pruebas manuales utilizando **Postman** contra el backend desarrollado en **Spring Boot** ejecutándose en Docker.

Base URL utilizada:

```
http://localhost:8080
```

Las pruebas consistieron en provocar diferentes tipos de errores para confirmar que el sistema responde utilizando el nuevo mecanismo centralizado de manejo de excepciones.

---

# 3. Pruebas realizadas

## 3.1 Error de validación de datos

**Endpoint**

```
POST /api/auth/login
```

**Body enviado**

```json
{
  "email": "",
  "password": "Admin123!"
}
```

**Resultado esperado**

* El sistema detecta un error de validación debido al campo `email` vacío.
* El `GlobalExceptionHandler` captura la excepción `MethodArgumentNotValidException`.
* Se devuelve una respuesta HTTP `400 Bad Request` con detalles del error.


![Login exitoso](../../imagenes/sistema%20mejorado/P3/Manejo_error1.png)

**Figura X.** Manejo global de error de validación mediante `GlobalExceptionHandler`.

---

## 3.2 Recurso no encontrado

**Endpoint**

```
GET /api/church
```

**Condición**

No existe una iglesia registrada en la base de datos.

**Resultado esperado**

* El sistema lanza una excepción `ResponseStatusException` o `EntityNotFoundException`.
* El `GlobalExceptionHandler` captura la excepción.
* Se devuelve una respuesta `404 Not Found` con la estructura de error definida.


![Login exitoso](../../imagenes/sistema%20mejorado/P3/Manejo_error2.png)

**Figura X.** Manejo global de error cuando un recurso solicitado no existe.

---

# 4. Resultado

Las pruebas realizadas permiten verificar que:

* Las excepciones generadas en el sistema son capturadas por el `GlobalExceptionHandler`.
* Todas las respuestas de error siguen una estructura consistente.
* Se evita duplicar lógica de manejo de errores en los controladores.

Esto mejora la claridad de la API y facilita su consumo por parte de clientes externos.

---

# 5. Conclusión

La implementación del **manejo global de excepciones** permite centralizar el tratamiento de errores dentro del sistema.

Este enfoque mejora la mantenibilidad del código, evita duplicación de lógica en los controladores y garantiza que todas las respuestas de error devueltas por la API tengan un formato uniforme.

---

