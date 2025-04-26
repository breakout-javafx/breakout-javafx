# ARCHIVO PARA EL RASTREO DE TAREAS PENDIENTES

---

**Prioridad:**
- **URG (Urgente)**: Tareas críticas que deben ser resueltas lo más rápido posible.
- **MED (Media)**: Tareas importantes, pero no tan urgentes.
- **LOW (Baja)**: Tareas que no afectan significativamente el funcionamiento y pueden esperar.

---

## 🛑 Tareas urgentes (URG)

- [x] **Control de FPS**  
  *Añadir control para la tasa de frames por segundo para asegurar que el juego corra de manera fluida en todas las plataformas.*  
  **Done by:** _[javi]_

- [x] **Mapa, Paredes y Colisiones**  
  *Añadir un sistema de detección de paredes, para contemplar top/right/left/bottom y que sea lo suficientemente abstracto como para permitir en un futuro controlar las vidas*  
  **Done by:** _[alvaro]_

- [x] **Control vidas**  
  *Añadir algún item reutilizable que permita controlar cuántas vidas quedan y asegurar una interconexión con las clases wall*  
  **Done by:** _[alvaro]_

- [x] **Lógica de rebotes de la pelota**  
  *Asegurar la lógica básica de rebote.*  
  **Done by:** _[javi & alvaro]_


- [x] **Ajustar tamaño de la ventana del juego**  
  *La ventana se inicializa en pantalla completa. El panel de juego es más pequeño que la pantalla.*  
  **Done by:** _[javi]_


- [x] **Añadir delimitadores del panel de juego (bordes)**  
  *Dibujar los bordel del panel de juego para que se vean de forma clara los límites*  
  **Done by:** _[Javi]_


- [x] **Cambiar pos inical de paddle**  
  *Hacer que el paddle aparezca y se mantenta en la parte inferiror del cuadro de juego*  
  **Done by:** _[Javi]_


- [x] **Creación de bricks**  
  *Crear el objeto brick implementado patrones de uso*  
  **Done by:** _[Nombre del responsable]_


- [x] **Algoritmo spawn brick**  
  *Diseñar el algoritmo de spawn de los mismos*  
  **Done by:** _[javi]_

- [x] **Patron Strategy**  
  *Implementación patrón strategy a Ball*  
  **Done by:** _[javi]_


- [ ] **Ajuste tamaño ventan juego**  
  *Implementar el ajuste del tamaño de la ventana de juego (no ventana total) desde el .properties.*  
  **Done by:** _[Nombre del responsable]_


- [ ] **Score tracker**  
  *Investigar, crear e implentar la tabla de puntuación y su lógica*  
  **Done by:** _[Nombre del responsable]_

---

## 🔶 Tareas medianas (MED)
- [ ] **Colores bricks**  
  *Una vez implementados los bricks diseñar las texturas y colores de los mismos.*  
  **Done by:** _[Nombre del responsable]_

- [ ] **Añadir un numero de vidas al brick**  
  *Implementar núermo de vidas por brick y cambiar aspecto en función de las vidas. Implementar junto a tarea ""Añadir decoratos Brick"*  
  **Done by:** _[Nombre del responsable]_

- [ ] **Añadir decoratos Brick**  
  *Diseñar e implementar mas decoradores para brick. Añadirlos al algoritmo de generación. SI hay duda hablar ocn Javi.*  
  **Done by:** _[Nombre del responsable]_

- [ ] **Añadir strategies Ball**  
  *Diseñar e implementar estrategis de comportamiento para Ball. Implementarla con destrucción de bloques*    
  **Done by:** _[Nombre del responsable]_

---

## 🟢 Tareas de baja prioridad (LOW)
- [ ] **Menu**  
  *Añadir un menu de juego al presionar una tecla. Opciones: [Continuar, Reiniciar, Ajustes, Salir].*  
  **Done by:** _[Nombre del responsable]_


- [ ] **Color bode ventana de juego**  
  *Cambiar el color dele borde de la ventana de juego*  
  **Done by:** _[Nombre del responsable]_

---

## Resumen patrones implementados

| **Patrón**    | **Uso (Clase)**                  |
|---------------|----------------------------------|
| **Singleton** | ConfigLoader                     |
| **Decorator** | AbstractBrick                    |
| **Template**  | AbstractBrick                    |
| **Strategy**  | Ball / NormalMovementStrategy    |
| **Patrón 5**  | Descripción de dónde se ha usado |
| **Patrón 6**  | Descripción de dónde se ha usado |
