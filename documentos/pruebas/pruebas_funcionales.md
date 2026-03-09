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
  "email": "dm@gmail.com",
  "password": "danay123"
}
```

**Resultado obtenido**

* Respuesta `200 OK`
* Generación de token JWT
* Retorno de email y rol del usuario autenticado

📸 **Pantallazo aquí**

![Login exitoso](../../imagenes/sistema%20mejorado/P1/Autenticación_exitoza.png)

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

📸 **Pantallazo aquí**

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



