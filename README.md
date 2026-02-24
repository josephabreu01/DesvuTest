# Sistema Bancario - Banco App

Aplicación bancaria Full-Stack con Java Spring Boot, Angular y PostgreSQL.

## Arquitectura y Patrones de Diseño

Este proyecto sigue los principios de **Clean Code** y **SOLID** para garantizar un código mantenible, escalable y testeable.

### Backend (Java Spring Boot)
- **Layered Architecture (Arquitectura por Capas)**: Organización clara entre controladores, servicios y persistencia.
- **MVC (Model-View-Controller)**: Separación de la lógica de presentación de los datos.
- **Dependency Injection (DI)**: Inyección de dependencias para desacoplar componentes y facilitar pruebas.
- **Data Transfer Object (DTO)**: Desacopla el modelo interno de la base de datos de la API pública, evitando fugas de datos y permitiendo validaciones personalizadas.
- **Repository Pattern**: Abstracción del acceso a datos mediante Spring Data JPA.
- **Global Exception Handling**: Centralización del manejo de errores para respuestas API consistentes.

### Frontend (Angular)
- **Component-Based Architecture**: UI organizada en componentes reutilizables y atómicos.
- **Feature-Driven Modules**: Organización por módulos funcionales (`Core`, `Shared`, `Features`).
- **Reactive Programming (RxJS)**: Manejo asíncrono y reactivo de datos y estados mediante Observables.
- **Observable Data Services**: Centralización del estado y la lógica compartida (e.g., `NotificationService`).
- **Reactive Forms**: Validación declarativa y desacoplada de la interfaz de usuario.
- **Interceptor Pattern**: Manejo centralizado de errores HTTP y cabeceras.

### ¿Por qué estos patrones?
- **Mantenibilidad**: Los cambios en una capa (ej. cambiar de DB) impactan mínimamente a las demás.
- **Testabilidad**: El desacoplamiento permite realizar pruebas unitarias robustas con mocks.
- **Escalabilidad**: La estructura modular permite añadir nuevas funcionalidades sin aumentar la complejidad técnica.
- **Robustez**: La centralización de validaciones y errores previene fallos inesperados y mejora la UX.

## Entidades

```
Persona (MappedSuperclass)
└── Cliente (Entity) → Cuenta (Entity) → Movimiento (Entity)
```

## Prerrequisitos

- Docker Desktop instalado y corriendo
- Java 17+ y Maven (para desarrollo local)
- Node.js 20+ (para desarrollo local frontend)

## Ejecución con Docker

```bash
# Levantar toda la aplicación
docker compose up --build

# Acceder:
# Frontend: http://localhost:4200
# Backend API: http://localhost:8080
# Base de datos: localhost:5432
```

## Desarrollo Local

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
ng serve
```

## Endpoints REST API

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /clientes | Lista todos los clientes |
| POST | /clientes | Crea un cliente |
| PUT | /clientes/{id} | Actualiza un cliente |
| PATCH | /clientes/{id} | Actualización parcial |
| DELETE | /clientes/{id} | Elimina un cliente |
| GET | /cuentas | Lista todas las cuentas |
| POST | /cuentas | Crea una cuenta |
| ... | ... | igual para movimientos |
| GET | /reportes?clienteId=&fechaInicio=&fechaFin= | Estado de cuenta |

## Pruebas

### Backend (JUnit 5 + MockMvc)
```bash
cd backend
./mvnw test
```

### Frontend (Jest)
```bash
cd frontend
npm run test
```
