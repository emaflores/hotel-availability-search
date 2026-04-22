# Hotel Availability Search

## Descripción

Implementación del challenge utilizando Spring Boot.  
La aplicación expone dos endpoints REST para registrar búsquedas de disponibilidad de hoteles y consultar cuántas veces se repitió una búsqueda determinada.

La persistencia se realiza de forma asíncrona mediante Kafka, y los datos se almacenan en una base Oracle.

---

## Stack tecnológico

- Java 21  
- Spring Boot 3.5.13  
- Spring Web / Validation / Data JPA  
- Apache Kafka  
- Oracle Database (Docker)  
- Testcontainers (para tests de integración)  
- JaCoCo (coverage)  
- springdoc-openapi 2.8.17  

---

## Endpoints

### POST /search

Registra una búsqueda y devuelve un identificador único.

**Request:**
```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}
```

**Response:**
```json
{
  "searchId": "xxxxx"
}
```

---

### GET /count?searchId=xxxxx

Devuelve la búsqueda original y cuántas veces se repitió.

**Response:**
```json
{
  "searchId": "xxxxx",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2023",
    "checkOut": "31/12/2023",
    "ages": [3, 29, 30, 1]
  },
  "count": 100
}
```

---

## Flujo de la aplicación

1. `POST /search`
   - valida el payload
   - genera un `searchId`
   - publica el mensaje en Kafka

2. Kafka Consumer
   - consume el mensaje
   - persiste la búsqueda en Oracle (usando virtual threads)

3. `GET /count`
   - recupera la búsqueda
   - calcula cuántas veces se repitió

> Nota: al usar Kafka, la persistencia es asíncrona. Puede existir una pequeña ventana en la que `/count` todavía no refleje una búsqueda recién creada.

---

## Validaciones

- `hotelId` obligatorio  
- `checkIn` < `checkOut`  
- `ages` no vacío  
- todas las edades ≥ 0  

En caso de error se devuelve `400 Bad Request`.

---

## Arquitectura

Se utilizó una estructura basada en arquitectura hexagonal:

- `domain`: modelo y contratos  
- `application`: casos de uso  
- `infrastructure`: adapters (REST, Kafka, DB)  

---

## Decisiones

- Se utilizó Kafka porque estaba requerido en la consigna.  
- La arquitectura hexagonal también responde a un requisito explícito.  
- Para este problema, una solución más simple sería suficiente, pero se priorizó cumplir los requisitos solicitados.  

---

## Cómo levantar el proyecto

Requisito: tener Docker instalado y corriendo.

```bash
docker compose up --build
```

Esto levanta:

- Oracle Database  
- Kafka  
- la aplicación  

---

## Swagger / OpenAPI

Disponible en:

```text
http://localhost:8080/swagger-ui.html
```

---

## Tests

Ejecutar:

```bash
mvn test
```

- Incluye tests unitarios e integración  
- Se utiliza Testcontainers para levantar Oracle en tests  
- Coverage configurado con JaCoCo  

---

## Notas

- Se utilizan `LocalDate` para fechas (no `Date`)  
- Los objetos son inmutables  
- El orden de las edades influye en el cálculo del count  
- La solución está completamente dockerizada  
