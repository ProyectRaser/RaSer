# Imagen base con Android SDK y Gradle
FROM ghcr.io/cirruslabs/android-sdk:30

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar todos los archivos del proyecto
COPY . .

# Dar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Ejecutar build para generar el APK
RUN ./gradlew assembleDebug