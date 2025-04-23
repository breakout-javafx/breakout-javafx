# 📘 GitHub Cheat Sheet para Principiantes

Este documento resume los comandos más comunes de Git y GitHub para comenzar a trabajar con repositorios.

---

## ⚙️ Configuración Inicial

```
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

---

## 🆕 Crear un Repositorio

```
git init
```

---

## 🔗 Clonar un Repositorio

```
git clone https://github.com/usuario/repositorio.git
```

---

## 📂 Estados de los Archivos

```
git status         # Verifica el estado actual del repositorio
git diff           # Muestra los cambios no agregados al staging
```

---

## ➕ Agregar Archivos al Staging

```
git add archivo.txt    # Agrega un archivo al staging
git add .              # Agrega todos los archivos al staging
```

---

## ✅ Confirmar Cambios (Commit)

```
git commit -m "Mensaje descriptivo del cambio"
```

---

## 📤 Subir Cambios a GitHub

```
git push origin main   # Sube los commits a la rama 'main'
```

---

## 📥 Descargar Cambios del Repositorio Remoto

```
git pull origin main   # Actualiza tu rama local con los últimos cambios
```

---

## 🌿 Ramas

```
git branch                 # Lista las ramas
git branch nueva-rama      # Crea una nueva rama
git checkout nueva-rama    # Cambia a la nueva rama
git checkout -b nueva-rama # Crea y cambia a la nueva rama
git merge nombre-rama      # Fusiona 'nombre-rama' a la rama actual
```

---

## ⚠️ Comandos Peligrosos

```
git reset --hard HEAD
```

> ⚠️ **Advertencia:** Este comando elimina todos los cambios no confirmados. ¡No se puede deshacer!

```
git push --force
```

> ⚠️ **Advertencia:** Forzar el push puede sobrescribir el historial remoto. Úsalo solo si estás completamente seguro.

```
git rebase
```

> ⚠️ **Advertencia:** Reescribe el historial. Asegúrate de no afectar a otros colaboradores.

---

## 🔄 Ver el Historial

```
git log                     # Historial detallado
git log --oneline --graph   # Historial resumido y visual
```

---

## 🧼 Revertir Cambios

```
git checkout -- archivo.txt   # Revierte el archivo al último commit
git restore archivo.txt       # Alternativa moderna
git revert <hash>             # Crea un nuevo commit que revierte uno anterior
```

---

## 🔍 Ignorar Archivos

Crea un archivo `.gitignore` con contenido como:

```
node_modules/
.env
*.log
```

---

## 🤝 GitHub: Trabajar con Remotos

```
git remote add origin https://github.com/usuario/repositorio.git
git remote -v    # Verifica los remotos existentes
```

---

## 🐙 Flujo Básico de Trabajo con GitHub

1. Clona el repositorio:
   ```
   git clone https://github.com/usuario/repositorio.git
   ```

2. Crea una rama:
   ```
   git checkout -b nombre-rama
   ```

3. Haz cambios, agrégalos y haz commit:
   ```
   git add .
   git commit -m "Descripción clara del cambio"
   ```

4. Sube la rama:
   ```
   git push origin nombre-rama
   ```

5. Abre un Pull Request en GitHub.

---

## 🧠 Consejos Finales

- Haz commits pequeños y frecuentes.
- Usa mensajes descriptivos.
- Trabaja en ramas para mantener organizado el flujo de trabajo.
- Antes de usar comandos peligrosos como `reset --hard` o `push --force`, asegúrate de que entiendes sus efectos.

---

📌 Guarda este archivo como referencia rápida mientras aprendes y trabajas con Git y GitHub.
