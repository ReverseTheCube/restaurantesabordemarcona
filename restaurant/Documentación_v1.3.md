# Documentación Técnica (BACKEND)
Fecha: 23/05/2025

## Índice
1. **Arquitectura del Proyecto**
2. **Controladores (Controllers)**
   - AuthController
   - HistorialController
   - PedidoController
3. **DTO (Data Transfer Objects)**
   - ItemDTO
4. **Modelos**
   - Item
   - PedidoSession
5. **Servicios**
   - PedidoService
6. **Repositorios (Repositories)**
7. **Entidades (Entities)**
8. **Flujo de Trabajo**
9. **Consideraciones Futuras**

---

## 1. Arquitectura del Proyecto
El sistema sigue una arquitectura MVC (Model-View-Controller) con Spring Boot y está estructurado en:

- **Controladores**: Manejan las solicitudes HTTP y enrutamiento.
- **Servicios**: Contienen la lógica de negocio principal.
- **Repositorios**: Interfaz con la base de datos (JPA/Hibernate).
- **Modelos/DTOs**: Objetos para transferencia de datos y estado temporal.
- **Vistas**: Archivos estáticos HTML/CSS alojados en `/static`.

**Tecnologías Clave**:
- **Spring Boot 3**: Para configuración automática e inyección de dependencias.
- **Spring Data JPA**: Para operaciones con la base de datos.
- **Session Scope**: Mantiene el estado del pedido por usuario (`PedidoSession`).
- **MySQL**: Base de datos relacional para persistencia.

---

## 2. Controladores

### AuthController (`Controlador/AuthController.java`)
**Responsabilidad**: Autenticación de usuarios (cajeros/meseros).

#### Métodos:
- **`login()`** (`POST /auth/login`):
  - Valida credenciales contra `UsuarioRepository`.
  - Almacena `usuarioId` en la sesión HTTP.
  - Redirige a `/main.html` si es exitoso, o a `/login.html?error` si falla.

**Dependencias**:
- `UsuarioRepository`: Consulta usuarios en la base de datos.

---

### HistorialController (`Controlador/HistorialController.java`)
**Responsabilidad**: Obtener el historial de pedidos del usuario.

#### Métodos:
- **`getHistorial()`** (`GET /api/historial`):
  - Recupera `usuarioId` de la sesión.
  - Devuelve lista de `Pedido` filtrada por usuario via `PedidoRepository`.

**Dependencias**:
- `PedidoRepository`: Consulta pedidos asociados a un usuario.

---

### PedidoController (`Controlador/PedidoController.java`)
**Responsabilidad**: Gestionar el ciclo de vida de los pedidos.

#### Endpoints:
- **`agregarItem()`** (`POST /api/pedido/agregar`):
  - Convierte `ItemDTO` a `Item`, normalizando la categoría a mayúsculas.
  - Delega a `PedidoService` para agregar el ítem al pedido temporal.
  
- **`verPedido()`** (`GET /api/pedido/ver`):
  - Devuelve la lista actual de ítems en el pedido temporal.
  
- **`limpiarPedido()`** (`DELETE /api/pedido/limpiar`):
  - Vacía el pedido temporal.
  
- **`confirmarPedido()`** (`POST /api/pedido/confirmar`):
  - Obtiene `usuarioId` de la sesión y `clienteId` (hardcodeado temporalmente).
  - Invoca `PedidoService` para persistir el pedido en la base de datos.

**Dependencias**:
- `PedidoService`: Coordina la lógica de negocio del pedido.

---

## 3. DTO (Data Transfer Objects)

### ItemDTO (`dto/ItemDTO.java`)
**Propósito**: Transferir datos del frontend al backend sin exponer las entidades JPA.

**Campos**:
- `nombre`: Nombre del ítem (ej: "Lomo Saltado").
- `categoria`: Tipo de ítem ("COMIDA", "BEBIDA", etc.).
- `cantidad`: Cantidad seleccionada.

**Uso**: Convertido a `Item` en `PedidoController` antes de procesar.

---

## 4. Modelos

### Item (`modelo/Item.java`)
**Propósito**: Representa un ítem en el pedido temporal (no persistido directamente).

**Campos**:
- `nombre`, `categoria`, `cantidad`, `precioUnitario`.

**Notas**:
- Se utiliza para cálculos temporales antes de guardar en la base de datos.

---

### PedidoSession (`modelo/PedidoSession.java`)
**Propósito**: Mantener el estado del pedido durante la sesión del usuario.

**Características**:
- **Scope de Sesión**: Instancia única por usuario (sesión HTTP).
- `@Scope(value = WebApplicationContext.SCOPE_SESSION)`: Garantiza aislamiento.

**Métodos**:
- `addItem()`, `getItems()`, `clear()`: Gestionan la lista temporal de ítems.

---

## 5. Servicios

### PedidoService (`servicio/PedidoService.java`)
**Responsabilidad**: Coordinar toda la lógica de pedidos y persistencia.

#### Métodos Clave:
1. **`agregarItem(Item item)`**:
   - Busca el precio en `ComidaRepository` o `BebidaRepository` según la categoría.
   - Agrega el ítem a `pedidoTemporal` (lista en memoria).

2. **`guardarPedidoEnBD(int usuarioId, int clienteId)`**:
   - **Transaccional**: Asegura consistencia en la base de datos.
   - Crea un `Pedido` vinculado al usuario y cliente.
   - Itera sobre `pedidoSession` para crear `DetallePedido`.
   - Calcula subtotales y total usando `BigDecimal`.
   - Limpia `pedidoSession` al finalizar.

3. **`obtenerPedido()`, `limpiarPedido()`**: Gestionan el pedido temporal.

**Dependencias**:
- Repositorios (`PedidoRepository`, `DetallePedidoRepository`, etc.): Para operaciones CRUD.
- `PedidoSession`: Estado temporal del pedido.

---

## 6. Repositorios
Interfaces JPA para operaciones con la base de datos:

| Repositorio               | Métodos Clave                          | Uso                              |
|---------------------------|----------------------------------------|----------------------------------|
| `UsuarioRepository`       | `findByNombreUsuarioAndContraseña()`   | Autenticación                    |
| `PedidoRepository`        | `findByUsuario_IdUsuario()`            | Historial de pedidos por usuario |
| `ComidaRepository`        | `findByNombreComida()`                 | Búsqueda de platos               |
| `BebidaRepository`        | `findByNombreBebida()`                 | Búsqueda de bebidas              |

---

## 7. Entidades (JPA)
Mapeo de tablas de la base de datos. Ejemplos:

### Pedido (`entidades/Pedido.java`)
```java
@Entity
@Table(name = "Pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer idPedido;
    @ManyToOne private Cliente cliente;
    @ManyToOne private Usuario usuario;
    private LocalDateTime fechaPedido;
    private Double total;
    @OneToMany(mappedBy = "pedido")
    private List<DetallePedido> detalles;
}
```

### DetallePedido (`entidades/DetallePedido.java`)
```java
@Entity
@Table(name = "DetallePedido")
public class DetallePedido {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Integer idDetallePedido;
    @ManyToOne private Pedido pedido;
    @ManyToOne private Comida comida;
    @ManyToOne private Bebida bebida;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    @Enumerated(STRING) private TipoItem tipoItem;
}
```

**Enums**:
- `TipoItem`: Clasifica ítems en `COMIDA`, `BEBIDA`, `ENTRADA`.
- Otros enums (`TipoCliente`, `TipoComida`, etc.): Mapeados a `VARCHAR` en la base de datos.

---

## 8. Flujo de Trabajo (según prototipo)
### Ejemplo: Creación de un Pedido
1. **Login**: Usuario válido inicia sesión (`AuthController`).
2. **Agregar Ítems**: 
   - Frontend envía `ItemDTO` → `PedidoController` → `PedidoService`.
   - `PedidoService` busca precios y almacena en `PedidoSession`.
3. **Confirmar Pedido**:
   - `PedidoController` invoca `guardarPedidoEnBD()`.
   - `PedidoService` persiste `Pedido` y `DetallePedido` en transacción.
4. **Historial**: 
   - `HistorialController` recupera pedidos desde `PedidoRepository`.

---

## 9. Consideraciones Futuras
- El `usuarioId` se almacena en la sesión HTTP.
- Se está utilizando `clienteId=1` en `PedidoController` que está hardcodeado de manera temporal, debe reemplazarse por una lógica dinámica.
- Implementar Spring Security (Bcrypt) para roles y cifrado de contraseñas.
- `PedidoService` convierte `BigDecimal` a `Double` para compatibilidad con la entidad `Pedido`, no debería ser un problema pero técnicamente existe pérdida de precisión de datos.
- `PedidoSession` Asegura que cada usuario tenga su propia funcionalidad (registro de pedidos) para mayor control.
- Se deben implementar los endpoints para obtener menús. De momento no existen APIs para listar platos, bebidas o entradas desde la base de datos.
