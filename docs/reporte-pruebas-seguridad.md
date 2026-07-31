# Reporte de pruebas y seguridad de AnalytiCore

**Fecha de verificaciÃ³n:** 31 de julio de 2026
**Proyecto:** AnalytiCore
**Entorno evaluado:** Desarrollo local con Docker Compose

## 1. Alcance

La verificaciÃ³n incluyÃ³ los componentes principales de la aplicaciÃ³n:

- Frontend desarrollado con React y Vite.
- Microservicio Python desarrollado con FastAPI.
- Microservicio Java desarrollado con Spring Boot.
- Base de datos PostgreSQL.
- ImÃ¡genes y configuraciones Docker.
- Dependencias administradas mediante npm, pip y Maven.

## 2. Pruebas automatizadas

| Componente | Herramienta | Resultado |
|---|---|---|
| Frontend | Vitest y Testing Library | 3 pruebas aprobadas |
| Python | Pytest | 3 pruebas aprobadas |
| Java | JUnit y Mockito | 4 pruebas aprobadas |

En total se ejecutaron **10 pruebas automatizadas sin fallos**.

## 3. Calidad del cÃ³digo

### Frontend

- `npm test`: aprobado.
- `npm run lint`: aprobado.
- `npm run build`: aprobado.
- `npm audit`: 0 vulnerabilidades.

### Python

- Ruff: `All checks passed!`.
- Pytest: 3 pruebas aprobadas.
- Las importaciones fueron normalizadas automÃ¡ticamente.
- La regla B008 fue configurada para aceptar el uso intencional de `fastapi.Depends`.

### Java

- Maven `clean verify`: aprobado.
- Pruebas ejecutadas: 4.
- Fallos: 0.
- Errores: 0.
- Se generÃ³ correctamente el archivo ejecutable JAR.

## 4. RevisiÃ³n de vulnerabilidades

El repositorio fue analizado mediante **Trivy 0.72.0**.

| Archivo analizado | Resultado HIGH/CRITICAL |
|---|---:|
| `frontend/package-lock.json` | 0 |
| `python-service/requirements.txt` | 0 |
| `java-service/pom.xml` | 0 |
| `frontend/Dockerfile` | 0 configuraciones incorrectas |
| `python-service/Dockerfile` | 0 configuraciones incorrectas |
| `java-service/Dockerfile` | 0 configuraciones incorrectas |

Durante el primer anÃ¡lisis se detectÃ³ una vulnerabilidad de nivel HIGH en el controlador PostgreSQL JDBC `42.7.11`.

La dependencia fue actualizada a:

`org.postgresql:postgresql:42.7.12`

El escaneo posterior confirmÃ³ **0 vulnerabilidades HIGH o CRITICAL**.

No se reportaron secretos expuestos durante el escaneo final del repositorio.

## 5. Endurecimiento de contenedores

Los servicios dejaron de ejecutarse con el usuario root.

| Servicio | Usuario del contenedor | UID |
|---|---|---:|
| Frontend | nginx | 101 |
| Python | appuser | 10001 |
| Java | appuser | 10001 |

El frontend utiliza la imagen:

`nginxinc/nginx-unprivileged:stable-alpine`

Nginx escucha en el puerto interno `8080` y Docker publica el servicio mediante:

`localhost:3000 â†’ contenedor:8080`

## 6. VerificaciÃ³n funcional

Los cuatro contenedores se encuentran en estado saludable:

- `analyticore-db`: healthy.
- `analyticore-frontend`: healthy.
- `analyticore-java`: healthy.
- `analyticore-python`: healthy.

Endpoints verificados:

| Endpoint | Resultado |
|---|---|
| `http://localhost:3000` | HTTP 200 |
| `http://localhost:3000/api/health` | Python service OK |
| `http://localhost:8000/health` | Python service OK |
| `http://localhost:8080/actuator/health` | Estado UP |

El healthcheck de Java confirmÃ³ tambiÃ©n:

- PostgreSQL: UP.
- Liveness: UP.
- Readiness: UP.

## 7. Resultado final

La aplicaciÃ³n AnalytiCore supera correctamente las pruebas unitarias, validaciones de calidad, compilaciones y anÃ¡lisis de seguridad local.

El repositorio queda preparado para la siguiente etapa: configuraciÃ³n de integraciÃ³n continua mediante GitHub Actions.
