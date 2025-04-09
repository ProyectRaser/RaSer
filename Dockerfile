# Imagen base con Android SDK y Gradle
FROM reactnativecommunity/react-native-android

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar todos los archivos del proyecto
COPY . .

# Dar permisos de ejecución a gradlew
RUN chmod +x ./gradlew

# Ejecutar build para generar el APK
RUN ./gradlew assembleDebug