# TeacherApp

TeacherApp es una aplicación Android diseñada para ayudar a los docentes a gestionar sus cursos, estudiantes, asistencias y asignaciones de manera eficiente.

## Características

- **Autenticación de Docentes**: Inicio de sesión seguro para docentes.
- **Gestión de Cursos**: Visualización de la lista de cursos asignados al docente.
- **Control de Asistencia**: Registro y visualización de la asistencia de los estudiantes por curso y fecha.
- **Gestión de Asignaciones**: Creación, edición y eliminación de asignaciones para cada curso.
- **Información de Estudiantes**: Acceso a la lista de estudiantes por curso con sus datos personales.

## Tecnologías Utilizadas

- Kotlin
- Android Jetpack (ViewModel, LiveData, Room, Navigation)
- MVVM Architecture
- SQLite para almacenamiento local

## Cómo Usar

1. **Inicio de Sesión**:
   - Use las credenciales proporcionadas (usuario: teacher1, contraseña: 123456).

2. **Lista de Cursos**:
   - Después de iniciar sesión, verá la lista de cursos asignados.
   - Toque en un curso para ver las opciones disponibles.

3. **Asistencia**:
   - Seleccione "Attendance" en un curso.
   - Elija una fecha y marque la asistencia de los estudiantes.
   - Guarde los cambios con el botón "Save Attendance".

4. **Asignaciones**:
   - Seleccione "Assignments" en un curso.
   - Vea la lista de asignaciones existentes.
   - Use el botón "+" para agregar una nueva asignación.
   - Toque en una asignación existente para editarla o eliminarla.

5. **Estudiantes**:
   - Seleccione "Students" en un curso para ver la lista de estudiantes y sus detalles.

## Configuración del Proyecto

1. Clone el repositorio:
   git clone https://github.com/Gramir/TeacherApp.git
   
3. Abra el proyecto en Android Studio.

4. Sincronice el proyecto con los archivos Gradle.

5. Ejecute la aplicación en un emulador o dispositivo Android.

## Datos de Prueba

La aplicación viene pre-cargada con datos de prueba para demostración. Estos incluyen un docente, varios cursos, estudiantes, asignaciones y registros de asistencia.


