# TeacherApp 📚

Una aplicación Android moderna para profesores que facilita la gestión de cursos, asistencia y tareas. Desarrollada con Kotlin siguiendo la arquitectura Clean Architecture.

## Características 🌟

- **Gestión de Cursos**: Visualización y administración de cursos asignados
- **Control de Asistencia**: Registro y seguimiento de asistencia de estudiantes
- **Gestión de Tareas**: Crear, editar y eliminar tareas para cada curso
- **Lista de Estudiantes**: Acceso a información detallada de estudiantes por curso
- **Notificaciones**: Sistema de notificaciones para nuevos cursos asignados
- **Tema Oscuro**: Interfaz moderna con tema oscuro por defecto

## Tecnologías Utilizadas 🛠️

- **Kotlin**: Lenguaje principal de desarrollo
- **Clean Architecture**: Arquitectura modular y escalable
- **Dagger Hilt**: Inyección de dependencias
- **Firebase**: 
  - Authentication: Gestión de usuarios
  - Firestore: Base de datos en tiempo real
  - Cloud Messaging: Sistema de notificaciones
- **Coroutines & Flow**: Programación asíncrona y reactiva
- **Navigation Component**: Navegación entre fragmentos
- **Material Design 3**: Componentes de UI modernos
- **ViewBinding**: Vinculación de vistas segura

## Arquitectura 🏗️

El proyecto sigue los principios de Clean Architecture con tres capas principales:

### 1. Capa de Presentación (presentation)
- Activities y Fragments
- ViewModels
- Adaptadores
- Estados de UI

### 2. Capa de Dominio (domain)
- Casos de uso
- Modelos de dominio
- Interfaces de repositorios

### 3. Capa de Datos (data)
- Implementaciones de repositorios
- Fuentes de datos
- Modelos de datos

## Requisitos 📋

- Android Studio Arctic Fox o superior
- Android SDK nivel 21 o superior
- Gradle 7.0 o superior
- Cuenta de Firebase

## Configuración ⚙️

1. Clona el repositorio:
```bash
git clone https://github.com/yourusername/TeacherApp.git
```

2. Abre el proyecto en Android Studio

3. Configura Firebase:
   - Crea un proyecto en Firebase Console
   - Descarga el archivo `google-services.json`
   - Colócalo en la carpeta `app/`

4. Sincroniza el proyecto con Gradle

## Estructura del Proyecto 📁

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/teacherapp/
│   │   │   ├── data/
│   │   │   │   ├── datasource/
│   │   │   │   ├── repository/
│   │   │   │   └── service/
│   │   │   ├── di/
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   ├── repository/
│   │   │   │   └── usecase/
│   │   │   └── presentation/
│   │   │       ├── activity/
│   │   │       ├── adapter/
│   │   │       ├── dialog/
│   │   │       ├── fragment/
│   │   │       └── viewmodel/
│   │   └── res/
└── build.gradle
```


