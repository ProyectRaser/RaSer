## RaSer

**RaSer** es una aplicación móvil enfocada en la **gestión de tareas para profesionales DevOps**, diseñada para facilitar el seguimiento, organización y filtrado de tareas técnicas de forma rápida e intuitiva.

### 🧩 Funcionamiento

- **Registro sencillo**: Solo necesitas ingresar un correo electrónico y una contraseña para crear una cuenta.
- **Completar perfil**: Al registrarte, serás redirigido a una pantalla para completar tu información personal.
- **Gestión de tareas**: La interfaz principal permite añadir, editar y eliminar tareas fácilmente. Al crear una tarea, se despliega un formulario con campos personalizables.

### 📋 Campos de la tarea

Cada tarea cuenta con los siguientes campos:

- **Estado**: Por hacer, En proceso, Completada.
- **Prioridad**: Baja (🟢), Media (🟠), Alta (🔴).
- **Lenguaje**: Lenguaje principal relacionado con la tarea (por ejemplo, Python, Java...).
- **Tipo**: Tipo de tarea (bug, mejora, nueva funcionalidad...).
- **Entorno**: Entorno donde se desarrolla (producción, staging, local...).

---

### 📌 Detalle de los campos de la tarea

- **Estado**  
  Indica el progreso actual de la tarea:
  - **Por hacer**: La tarea está registrada pero aún no se ha comenzado.
  - **En proceso**: La tarea se encuentra actualmente en desarrollo o ejecución.
  - **Completada**: La tarea ya fue finalizada con éxito.

- **Prioridad**  
  Permite visualizar rápidamente la urgencia o importancia de la tarea. Se representa con colores:
  - **Baja (🟢)**: No es urgente; puede resolverse en cualquier momento.
  - **Media (🟠)**: Tiene cierta prioridad, conviene atenderla pronto.
  - **Alta (🔴)**: Requiere atención inmediata.

- **Lenguaje**  
  Lenguaje de programación principal asociado a la tarea. Ejemplos comunes incluyen:
  - Python, Java, JavaScript, Kotlin, etc.

- **Tipo**  
  Clasifica la naturaleza de la tarea:
  - **Bug**: Corrección de errores o fallos en el sistema.
  - **Mejora**: Optimización o refactorización de funcionalidad existente.
  - **Nueva funcionalidad**: Desarrollo de características nuevas.

- **Entorno**  
  Define el contexto en el que se ejecuta o se prueba la tarea:
  - **Producción**: Sistema en funcionamiento real, usado por los usuarios finales.
  - **Staging**: Entorno previo a producción, para pruebas finales.
  - **Local**: Entorno de desarrollo personal del programador.

---

### 🔎 Filtros inteligentes

Puedes **filtrar tareas por cualquiera de los campos anteriores**, lo que permite un control más eficiente según el estado, la prioridad, el entorno de ejecución, etc.

### 🎯 Características clave

- Interfaz intuitiva y optimizada para móviles.
- Autenticación con correo electrónico y Google.
- Posibilidad de editar y eliminar tareas.
- Filtros combinables para encontrar tareas fácilmente.
- Almacenamiento seguro con Firebase Firestore.
- Subida de imagen de perfil y gestión de cuenta.



