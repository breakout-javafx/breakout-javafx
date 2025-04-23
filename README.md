# 🎮 Proyecto: Breakout con Patrones de Diseño en Java

## 📌 Descripción General

Este proyecto implementa una versión moderna del clásico juego **Breakout**, desarrollado en **Java con JavaFX** como motor gráfico, y estructurado completamente mediante **patrones de diseño orientados a objetos**.
Expandir
message.txt
7 KB
﻿
# 🎮 Proyecto: Breakout con Patrones de Diseño en Java

## 📌 Descripción General

Este proyecto implementa una versión moderna del clásico juego **Breakout**, desarrollado en **Java con JavaFX** como motor gráfico, y estructurado completamente mediante **patrones de diseño orientados a objetos**.

El jugador controla una paleta para mantener la bola en juego mientras intenta destruir todos los bloques del nivel. A medida que se destruyen bloques, pueden aparecer power-ups que modifican el comportamiento del juego.

El enfoque principal del proyecto es **la correcta aplicación de patrones de diseño clásicos**, según el catálogo GoF (Gang of Four), integrándolos dentro de un juego funcional, modular y fácilmente extensible.

---

## 🧩 Patrones de Diseño Aplicados

### 🔨 Creacionales

- **Factory Method**  
  Para la creación de distintos tipos de bloques (`NormalBlock`, `BonusBlock`, `UnbreakableBlock`) de forma desacoplada.

- **Abstract Factory**  
  Utilizado para generar objetos relacionados a un nivel completo: paleta, bloques, bola, colores de fondo, etc.

- **Singleton**  
  Utilizado en `GameManager`, el cual orquesta el estado general del juego, accesible globalmente sin múltiples instancias.

---

### 🏗️ Estructurales

- **Adapter**  
  Para unificar distintas entradas del jugador (teclado, mouse) bajo una interfaz común que la paleta puede interpretar.

- **Decorator**  
  Permite añadir funcionalidades adicionales a la bola o paleta sin modificar sus clases originales (como bola de fuego, bola multiplicadora, paleta elástica, etc).

- **Facade**  
  Proporciona una interfaz simplificada (`GameFacade`) para inicializar, pausar, reanudar o reiniciar el juego, ocultando la complejidad interna.

---

### 🔁 Comportamiento

- **Template Method**  
  Define una estructura base para construir niveles (`GameLevel`), permitiendo que los pasos comunes se compartan y los específicos se definan en subclases.

- **Observer**  
  Aplicado para que la UI reaccione automáticamente a cambios en el juego (como actualización de puntuación, vidas, o cambio de estado del juego).

- **State**  
  Gestiona el estado del juego: `PlayingState`, `PausedState`, `GameOverState`, `VictoryState`, permitiendo cambiar comportamientos globales según el contexto.

- **Strategy**  
  Diferentes estrategias de comportamiento de la bola (rebotar normalmente, aceleración curva, rebote caótico, etc) o IA de movimiento en caso de paletas automáticas.

---

## 🧱 Estructura de Carpetas del Proyecto

```
breakout-game/
│
├── app/                      # Punto de entrada (MainApp.java) + setup JavaFX
│
├── core/                     # Lógica central del juego
│   ├── GameManager.java      # Singleton, orquesta todo
│   ├── GameLoop.java         # Bucle principal del juego (extiende AnimationTimer)
│   └── GameFacade.java       # Interfaz simplificada del juego
│
├── entities/                 # Entidades del juego (bola, paleta, bloques)
│   ├── Ball.java             # Soporta Decorator y Strategy
│   ├── Paddle.java           # Recibe comandos adaptados
│   ├── Block.java            # Clase base para bloques
│   └── PowerUp.java          # Elementos bonus caídos desde bloques
│
├── entities/decorators/      # Decoradores para Ball o Paddle
│   └── FireBall.java         # Ejemplo: bola decorada con fuego
│
├── entities/strategies/      # Estrategias de rebote
│   ├── NormalBounce.java
│   └── ZigZagBounce.java
│
├── factory/                  # Fabricación de objetos
│   ├── BlockFactory.java     # Factory Method
│   └── LevelFactory.java     # Abstract Factory para niveles
│
├── input/                    # Adaptadores de entrada
│   └── KeyboardInputAdapter.java
│
├── observer/                 # Observadores para HUD y otros
│   ├── Observer.java
│   ├── Subject.java
│   ├── ScoreBoard.java       # Observa cambios de puntaje
│   └── LivesDisplay.java     # Observa cambios de vida
│
├── state/                    # Estados del juego
│   ├── GameState.java
│   ├── PlayingState.java
│   ├── PausedState.java
│   └── GameOverState.java
│
└── ui/                       # Interfaz gráfica con JavaFX
    ├── Renderer.java         # Dibuja objetos en Canvas
    ├── HUD.java              # Interfaz de puntuación, vidas, etc.
    └── LevelView.java        # Representación gráfica del nivel
```

---

## 🧠 Flujo General del Juego

1. El `MainApp` inicia JavaFX, carga el `GameFacade`.
2. El `GameFacade` instancia un `LevelFactory` y crea el nivel.
3. El `GameManager` carga todos los objetos del nivel (bloques, paleta, bola).
4. `GameLoop` comienza a ejecutarse con `AnimationTimer`.
5. La entrada del jugador se recibe mediante `KeyboardInputAdapter` → mueve la paleta.
6. La `Ball` se mueve y colisiona, con posibles decoradores activos.
7. Al romper un bloque, puede dispararse un `PowerUp`, y se notifica a los `Observer`s (HUD).
8. Si se pierde la bola o se ganan todos los puntos, se cambia de `GameState`.
9. El `GameManager` y `HUD` se actualizan en tiempo real.
10. El juego puede ser pausado, reiniciado o terminado desde el `GameFacade`.

---

## 🧑‍🏫 Objetivo Didáctico

Este proyecto permite demostrar la comprensión profunda de los siguientes aspectos:

- Aplicación práctica de patrones de diseño en un sistema real.
- Modularización, escalabilidad y bajo acoplamiento en una aplicación interactiva.
- Separación clara entre lógica y presentación.
- Integración de múltiples patrones de forma armoniosa en un juego funcional.

---

## ✅ Alcance del Proyecto

### Funcionalidades mínimas:
- Control de paleta mediante teclado
- Bola con rebote realista
- Bloques con distintos comportamientos
- Bonus y power-ups
- Puntuación y vidas en pantalla
- Cambios de estado (pausa, reinicio, game over)
- Uso explícito de al menos **10 patrones de diseño**

### Extensiones opcionales:
- Efectos gráficos adicionales
- Niveles personalizados
- Modo infinito o survival
- Dificultad progresiva
- Guardado/carga de progreso

---

## 🚀 Próximos Pasos

1. Implementar estructura base (`MainApp`, `GameLoop`, `GameManager`)
2. Agregar entidades básicas (`Ball`, `Paddle`, `Block`)
3. Integrar patrones uno a uno (Factory, Observer, Strategy, etc.)
4. Diseñar un nivel de prueba con un `LevelFactory`
5. Probar el flujo completo con HUD actualizado

---

