# Carrito de compras — Android nativo (Kotlin)

App Android nativa para consultar un catálogo, agregar productos al carrito, modificar cantidades y eliminar líneas, con subtotales y total actualizados después de cada operación.

**Stack:** Kotlin, Android Views (XML) con Fragments, MVVM + Clean Architecture (dominio DDD), Hilt, Room, Coroutines + Flow, Navigation Component y Material 3.

## Alcance acordado

Por indicación del evaluador **no se implementa backend**: los datos se simulan localmente.

- **Persistencia:** el catálogo se precarga en una base **Room** y el carrito se guarda ahí mismo, así que se conserva al navegar y al cerrar la app.
- **Reglas de negocio:** viven en la capa `domain`, en Kotlin puro.
- **Latencia:** los repositorios simulan **500 ms** de latencia de red, para que se vean los estados de carga y el bloqueo de envíos duplicados.

## Notas de entorno

- **Versiones de herramientas:** se usan **AGP 8.5.1** y **compileSdk 34** (minSdk 24, JDK 17) porque la versión de Android Studio disponible en el entorno de trabajo del autor es antigua. Las librerías se eligieron compatibles con esa configuración.
- **Interfaz en XML:** la UI usa **XML y Fragments** en lugar de Jetpack Compose, por la familiaridad y experiencia con este enfoque.

El proyecto Android está en **`mobile/`**. No requiere claves ni servicios externos.

## Datos iniciales (semilla)

Los 8 productos están en `mobile/app/src/main/assets/products.json`. `ProductSeedCallback` los inserta **solo la primera vez que se crea la base**.

Para volver al estado inicial (catálogo original y carrito vacío), o para aplicar cambios en el JSON, borra los datos de la app:

```bash
adb shell pm clear com.example.shoppingcart
```

También sirve **Ajustes → Apps → ShoppingCart → Almacenamiento → Borrar datos**, o reinstalar la app.

## Pruebas

```bash
cd mobile
./gradlew testDebugUnitTest
```

Son pruebas unitarias en la JVM, sin emulador ni base de datos. El reporte queda en `mobile/app/build/reports/tests/testDebugUnitTest/index.html`.

- **`CartTest`, `QuantityTest`, `MoneyTest`:** pruebas de dominio. Cubren subtotales y total, acumulación sin duplicar líneas, rechazo por stock sin modificar el estado, actualizar, eliminar, carrito vacío y validación de los objetos de valor.
- **`CatalogViewModelTest`, `CartViewModelTest`:** pruebas de ViewModel. Usan los use cases reales con repositorios falsos en memoria y cubren estados (contenido, vacío, error con reintento), acciones, botones habilitados según el stock y bloqueo durante una operación pendiente.

## Arquitectura

```text
ui (View + ViewModel) ──► domain (modelo, use cases, contratos) ◄── data (Room, repositorios)
                                        ▲
                              di (Hilt) une contratos e implementaciones
```

```text
mobile/app/src/main/java/com/example/shoppingcart/
├── domain/   model (Cart, CartItem, Product, Quantity, Money), error, repository (contratos), usecase
├── data/     local (Room, DAOs, semilla), mapper, repository (implementaciones)
├── di/       módulos de Hilt
└── ui/       catalog, cart, common
```

**Cómo se reparte MVVM:**
- **Model:** `domain` + `data`.
- **View:** Fragments, adapters y layouts. Solo pintan el estado y delegan las acciones.
- **ViewModel:** `CatalogViewModel` y `CartViewModel`. Exponen `uiState` (Loading, Empty, Error, Content) y eventos de error, y coordinan los use cases.

Las dos pantallas observan el mismo `Flow` del carrito, así que todo queda sincronizado: el catálogo, el badge y el carrito.

## Contexto delimitado y conceptos

El contexto es **el carrito de compras de un único cliente de demostración**. El catálogo es de apoyo y de solo lectura: aporta el precio y el stock confiables.

| Concepto | Significado |
|---|---|
| `Cart` | Agregado raíz e inmutable. Solo él modifica sus líneas con `add`, `updateQuantity` y `remove`. Garantiza una línea por producto, el tope de stock y el cálculo del total. |
| `CartItem` | Línea del carrito: producto, precio unitario y cantidad. Calcula su subtotal. |
| `Product` | Producto del catálogo, con precio y stock. |
| `Quantity` | Objeto de valor: un entero mayor que cero. |
| `Money` | Objeto de valor: importe en centavos de USD, nunca negativo. |

## Decisiones principales

- **Room en lugar de Firebase:** no hay credenciales en el repositorio, funciona sin conexión y la app se ejecuta solo con clonar el repo.
- **Dominio inmutable:** una operación rechazada nunca deja el carrito a medias.
- **El precio sale siempre del catálogo:** el carrito guarda solo `productId` y `quantity`.
- **Una operación a la vez por pantalla:** evita envíos duplicados y que dos guardados se pisen.
- **Errores en dos niveles:** el dominio indica *qué* falló y la UI lo traduce a un mensaje de `strings.xml`.
- **Cambiar a un backend real:** solo requiere nuevas implementaciones de los repositorios y cambiar los `@Binds` de Hilt. La UI no cambia.

## Limitaciones

- No hay backend: la lógica de negocio vive en la app.
- Las líneas del carrito se ordenan por id de producto.
- La semilla no se re-sincroniza con el JSON en cada arranque.

## Uso de IA

El proyecto se desarrolló con ayuda de **Claude** (Anthropic) como asistente de programación, bajo la dirección del autor.

- **Dirección del autor:**
  - la visión de la solución;
  - la arquitectura y el stack (MVVM, Clean Architecture, Hilt, Room, Flow, XML con Fragments);
  - el alcance y el orden de implementación de cada parte;
  - los ajustes sobre lo propuesto: estructura del repositorio, versiones de herramientas, textos en español, nivel de documentación del código, entre otros;
  - la revisión del código.
- **Generado con ayuda de la IA, siguiendo esas indicaciones:**
  - la infraestructura del proyecto;
  - la implementación de las capas y las pantallas;
  - las pruebas unitarias;
  - esta documentación.
- **Forma de trabajo:** cada cambio se propuso primero y solo se incorporó con la autorización del autor, que lo revisó, ajustó y validó.
