# Hotel Availability Search

## Descripción

Implementación del challenge utilizando Spring Boot.  
La aplicación expone dos endpoints REST para registrar búsquedas de disponibilidad de hoteles y consultar cuántas veces se repitió una búsqueda determinada.

La persistencia se realiza de forma asíncrona mediante Kafka, y los datos se almacenan en una base Oracle.

---

## Cómo levantar el proyecto

Con Docker corriendo:

```bash
docker compose up --build
```

Esto arranca Oracle, Kafka y la app. La primera vez tarda un rato porque Oracle se toma su tiempo en estar listo.

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
  "checkIn": "15/02/2027",
  "checkOut": "17/02/2027",
  "ages": [30, 29, 1, 3]
}
```

Con curl:
```bash
curl -X POST http://localhost:8080/search \
  -H "Content-Type: application/json" \
  -d '{"hotelId":"1234aBc","checkIn":"15/02/2027","checkOut":"17/02/2027","ages":[30,29,1,3]}'
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

Con curl (reemplazando `xxxxx` por el `searchId` que devolvió `/search`):
```bash
curl "http://localhost:8080/count?searchId=xxxxx"
```

**Response:**
```json
{
  "searchId": "xxxxx",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "15/02/2027",
    "checkOut": "17/02/2027",
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

- `hotelId` obligatorio, máximo 64 caracteres  
- `checkIn` no puede ser anterior a hoy ni posterior a un año desde hoy  
- `checkIn` < `checkOut`  
- rango entre `checkIn` y `checkOut` de como máximo 30 días  
- `ages` no vacío, máximo 20 elementos  
- cada edad entre 0 y 120  

En caso de error se devuelve `400 Bad Request` con el detalle de las reglas violadas.

> Las fechas del ejemplo son ilustrativas. Si al probar la API te aparece un `400`, asegurate de usar un `checkIn` entre hoy y un año desde hoy.

--------

## Arquitectura

Se utilizó una estructura basada en arquitectura hexagonal:

- `domain`: modelo y contratos  
- `application`: casos de uso  
- `infrastructure`: adapters (REST, Kafka, DB)  

-----------

## Decisiones

- Se utilizó Kafka porque estaba requerido en la consigna.  
- La arquitectura hexagonal también responde a un requisito explícito.  
- Para este problema, una solución más simple sería suficiente, pero se priorizó cumplir los requisitos solicitados.  

----------

## Swagger / OpenAPI

Disponible en:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)


--------

## Tests

```bash 
  mvn test
```

Los tests de integración usan Testcontainers, así que Docker también tiene que estar corriendo. Si ya tenés la app levantada con `docker compose up` no pasa nada, Testcontainers arranca sus propios contenedores aparte.

- Unitarios + integración  
- Oracle se levanta con Testcontainers  
- Coverage con JaCoCo - para generar el reporte correr 
```bash
  mvn verify 
```
  
  y abrir target/site/jacoco/index.html

---------

## Notas

- Se utilizan `LocalDate` para fechas (no `Date`)  
- Los objetos son inmutables  
- El orden de las edades influye en el cálculo del count  
- La solución está completamente dockerizada  
