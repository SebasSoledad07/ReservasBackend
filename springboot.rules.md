# Reglas "springboot" (aplicables siempre en este chat y proyecto)

Propósito: definir reglas y buenas prácticas para desarrollar servicios Spring Boot con Java (versión moderna), que siempre seguiré al trabajar en este proyecto.

Resumen ejecutivo (qué hace este archivo):
- Establece convenciones de nombres, documentación, estructura de proyecto y prácticas recomendadas.
- Obliga: nombres en inglés, DTOs como `record`, cada método público con Javadoc completo.
- Describe un ejemplo de estructura del proyecto y checklist para PRs.

Contrato (cuando se aplican estas reglas):
- Inputs: cambios de código Java para este proyecto.
- Outputs esperados: código legible, consistente, documentado y testeable.
- Errores/Excepciones: si una regla no puede aplicarse (p. ej. dependencia de terceros), documentar la excepción en la PR.

Reglas generales
1. Idioma y nombres
   - Todos los nombres de paquetes, clases, interfaces, métodos y atributos deben estar en inglés.
   - Paquetes en minúsculas (ej.: `com.devsenior.soledad.reservas`).
   - Clases y interfaces en PascalCase: `BookingService`, `BookingRepository`.
   - Métodos y atributos en camelCase: `createBooking`, `bookingDate`.
   - Constantes en UPPER_SNAKE_CASE.

2. Documentación
   - Cada método público debe tener Javadoc actualizado y completo: resumen, `@param`, `@return` (si aplica), `@throws` (si lanza excepciones).
   - Las clases públicas principales (controllers, services, exceptions) deben tener una breve Javadoc.
   - Mantener Javadoc conciso y orientado al comportamiento público (qué hace, no cómo internamente).

3. DTOs
   - Usar `record` para DTOs inmutables.
   - No usar `record` para entidades JPA (las entidades deben ser clases regulares con constructor protegido/no-args requerido por JPA).
   - Los DTOs deben estar en paquete `.dto`.

4. Capas y responsabilidades
   - Controller: manejar HTTP (request/response), validación superficial, y delegar lógica a Services.
   - Service: contener la lógica de negocio, transacciones, orquestación.
   - Repository: acceso a datos (Spring Data JPA). Métodos simples y con contratos claros.
   - Mappers: mapear entre entidades y DTOs (preferir MapStruct o métodos dedicados en mappers).
   - No poner lógica de negocio en controllers ni lógica de acceso a datos en services.

5. Excepciones y manejo de errores
   - Definir excepciones de dominio (ej.: `BookingNotFoundException`) en paquete `.exception`.
   - Usar `@ControllerAdvice` para mapear excepciones a respuestas HTTP coherentes.
   - No usar `RuntimeException` genérico sin un motivo; crear excepciones con significado.

6. Validación
   - Usar `jakarta.validation` (anotaciones: `@NotNull`, `@Size`, etc.) en DTOs y `@Valid` en controladores.
   - Validar en la capa más cercana al borde (controller) y repetir validaciones esenciales en la capa de servicio cuando sea crítico.

7. Concurrencia y recursos
   - Preferir soluciones reactivas o manejo explícito de concurrencia cuando sea necesario.
   - Cuando use hilos, preferir virtual threads si la JVM/librerías lo permiten y aporta beneficio. Documentar la decisión.

8. Transacciones
   - Declarar límites transaccionales en métodos del Service (`@Transactional`) y documentar el comportamiento (readOnly, propagation si aplica).

9. Logging
   - Usar SLF4J con logger por clase (`private static final Logger logger = LoggerFactory.getLogger(Foo.class);`).
   - No loggear información sensible.
   - Usar niveles adecuados: DEBUG para desarrollo, INFO para hitos, WARN/ERROR para problemas.

10. Testeo
    - Unit tests: JUnit + Mockito para servicios y utilidades.
    - Integration tests: SpringBootTest para flujos que toquen la base de datos (usar Testcontainers cuando sea posible).
    - Cobertura: tests para happy path + al menos los casos de error más probables.

11. Calidad y herramientas
    - Usar Maven (ya presente). Mantener `source`/`target` en pom.xml alineados con la versión de Java.
    - Integrar linters/formatters (Checkstyle/Spotless) y análisis estático (SpotBugs).
    - Ejecutar `mvn -DskipTests=false test` en CI antes de merge.

12. Seguridad
    - No almacenar secretos en el código. Usar variables de entorno o herramientas de secret management.
    - Usar Spring Security para endpoints que lo requieran.

13. Dependencias
    - Mantener dependencias al mínimo necesario. Preferir dependencias estables y con mantenimiento activo.

14. Migración de idioma en código existente
    - Si hay nombres en español, migrarlos gradualmente; cada cambio debe estar en una PR con tests y refactor seguro.

15. Formato y estilo
    - Seguir un style guide (p. ej. Google Java Style o el style de la organización).
    - Configurar editor/IDE (IntelliJ) para aplicar el formato automáticamente.

16. Javadoc obligatorio - formato mínimo
    - Para métodos públicos:
      /**
       * Short description of what the method does.
       *
       * @param paramName description
       * @return description (omit if void)
       * @throws SomeException when ... (only if applicable)
       */

Reglas específicas (ejemplos y plantillas)
- DTO (record) ejemplo:
  package com.devsenior.soledad.reservas.dto;

  /**
   * Booking DTO usado para respuestas.
   *
   * @param id Identificador de la reserva
   * @param guestName Nombre del huésped
   * @param checkIn Fecha de entrada
   * @param checkOut Fecha de salida
   */
  public record BookingDto(Long id, String guestName, java.time.LocalDate checkIn, java.time.LocalDate checkOut) {}

- Plantilla Javadoc en un Controller:
  /**
   * Creates a new booking.
   *
   * @param dto Booking creation DTO
   * @return the created BookingDto
   * @throws ValidationException if input invalid
   */
  @PostMapping
  public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody BookingCreateDto dto) { ... }

Estructura recomendada del proyecto
- src/
  - main/
    - java/
      - com.devsenior.soledad.reservas
        - controller
          - BookingController.java
        - service
          - BookingService.java
        - repository
          - BookingRepository.java
        - dto
          - BookingDto.java (record)
          - BookingCreateDto.java (record)
        - entity
          - Booking.java
        - mapper
          - BookingMapper.java (MapStruct)
        - exception
          - BookingNotFoundException.java
        - config
          - WebConfig.java
    - resources/
      - application.properties
      - db/migration (si usa Flyway)
  - test/
    - java/
      - com.devsenior.soledad.reservas
        - controller
        - service
        - repository

Checklist para Pull Requests (PR)
- [ ] Nombres en inglés.
- [ ] Todos los métodos públicos con Javadoc.
- [ ] DTOs implementados como `record`.
- [ ] Tests unitarios para lógica nueva.
- [ ] No lógica de negocio en controllers.
- [ ] Validación en DTOs y `@Valid` en controllers.
- [ ] Cambios en dependencias justificados en la descripción de la PR.

Buenas prácticas adicionales
- Preferir inmutabilidad en DTOs y estructuras de datos transferidas.
- Evitar getters/setters públicos innecesarios en entidades; usar métodos con intención.
- Registrar decisiones arquitectónicas importantes en un ADR (Architecture Decision Record).

Notas sobre Java moderno
- Aprovechar `record`, `sealed` (cuando sea útil), pattern matching y otras mejoras de las últimas versiones cuando aporten claridad y seguridad.
- Documentar el uso de características experimentales y asegurarse de compatibilidad en el pipeline CI.

Aplicación de las reglas en este chat
- Seguiré estas reglas siempre al proponer cambios de código en este repositorio.
- Si alguna regla conflictúa con una restricción técnica del proyecto, mencionaré la excepción y propondré alternativas.

Contacto y actualización
- Este documento puede actualizarse si aparecen nuevas convenciones o herramientas en el proyecto.
- Para cambios mayores en las reglas abrir una PR y discutir en el repositorio.

---
Archivo generado automáticamente para uso en conversaciones y ediciones en este repositorio; nombre del archivo: `springboot.rules.md`.

