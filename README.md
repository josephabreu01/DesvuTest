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

## Guía de Despliegue con Docker

El proyecto está completamente contenedorizado y utiliza el archivo [BaseDatos.sql](file:///Users/josephabreu/Documents/Devsu/Proyect/BaseDatos.sql) para la inicialización automática de la estructura y datos de prueba.

### 1. Requisitos
- **Docker Desktop** instalado y en ejecución.
- Disponibilidad de los puertos `4200` (Frontend), `8080` (Backend) y `5432` (PostgreSQL).

### 2. Comandos de Ejecución

Desde la raíz del proyecto, utiliza los siguientes comandos:

```bash
# Construir y levantar todos los servicios en segundo plano
docker compose up --build -d

# Detener los servicios
docker compose stop

# Detener y eliminar contenedores
docker compose down

# Detener, eliminar contenedores y BORRAR volúmenes de datos (Resetea la DB)
docker compose down -v
```

### 3. Acceso a los Servicios

| Servicio | URL / Acceso | Nota |
|----------|--------------|------|
| **Frontend** | [http://localhost:4200](http://localhost:4200) | Aplicación Angular |
| **Backend API** | [http://localhost:8080](http://localhost:8080) | Documentación/API Base |
| **PostgreSQL** | `localhost:5432` | User: `banco_user`, Pass: `banco_pass`, DB: `bancodb` |

### 4. Monitoreo y Troubleshooting

Si experimentas problemas, puedes revisar los logs de los contenedores:

```bash
# Ver logs de todos los servicios
docker compose logs -f

# Ver logs específicos del backend
docker compose logs -f backend

# Ver logs específicos de la base de datos
docker compose logs -f postgres
```

### 5. Reinicio Limpio (Reseteo de Base de Datos)
Si deseas limpiar la base de datos y empezar de cero con los registros de `BaseDatos.sql`:
1. Ejecuta `docker compose down -v`
2. Ejecuta `docker compose up --build -d`

> [!NOTE]
> La base de datos es persistente mediante un volumen llamado `postgres_data`. No se perderán los datos al reiniciar los contenedores a menos que uses el comando `down -v`.

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

Para una documentación interactiva y pruebas rápidas, utiliza la colección de Postman incluida:
👉 [BancoAPI.postman_collection.json](file:///Users/josephabreu/Documents/Devsu/Proyect/BancoAPI.postman_collection.json)


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
