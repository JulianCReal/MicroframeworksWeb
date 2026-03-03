# Web Framework — REST Services & Static File Management

A lightweight Java web framework built on raw TCP sockets for developing REST services and serving static files.

## Architecture

```
src/main/java/com/tdse/framework/
├── utilities/
│   ├── HttpServer.java   ← Core server: routing, static files, HTTP parsing
│   ├── WebMethod.java    ← Functional interface for lambda handlers
│   ├── Request.java      ← HTTP request with query param extraction
│   ├── Response.java     ← HTTP response (statusCode, contentType, body)
│   ├── EchoClient.java   ← TCP socket client example
│   ├── EchoServer.java   ← TCP socket server example
│   ├── URLParser.java    ← URL component parsing example
│   └── URLReader.java    ← URL content reading example
└── appexamples/
    └── MathServices.java ← Example app using the framework
```

## Features

- `get(path, lambda)` — register REST endpoints with lambda functions
- `req.getValues("param")` — extract query parameters from the URL
- `staticfiles("folder")` — serve static HTML/CSS/JS/images (binary-safe)
- `start(port)` — configurable port
- Returns 404 for unrecognized paths

## Build & Run

```bash
git clone https://github.com/<your-username>/web-framework.git
cd web-framework
mvn clean package
java -cp target/web-framework.jar com.tdse.framework.appexamples.MathServices
```

## Test Endpoints

| URL | Response |
|-----|----------|
| `http://localhost:35000/index.html` | Static HTML page |
| `http://localhost:35000/App/hello?name=Pedro` | `Hello Pedro!` |
| `http://localhost:35000/App/pi` | `PI = 3.141592653589793` |
| `http://localhost:35000/App/sqrt?value=144` | `sqrt(144.0) = 12.0` |
| `http://localhost:35000/notfound` | `404 Not Found` |

## Running Tests

```bash
mvn test
```

Key tests:
- `testRequestGetValuesNoTrailingSpace` — verifica que `getValues()` no retorna espacios extra
- `testGetEndpointExecutesCorrectly` — verifica que los lambdas ejecutan correctamente
- `testStaticfilesSetsFolder` — verifica la configuración de archivos estáticos
- `testLambdaWithMultipleParams` — verifica múltiples query params

## References

- Oracle Java Networking Tutorial: https://docs.oracle.com/javase/tutorial/networking/index.html
- Luis Daniel Benavides Navarro — *Introducción a esquemas de nombres, redes, clientes y servicios con Java*, Escuela Colombiana de Ingeniería, 2020
