# Imagen base con Android SDK y Gradle preinstalado
FROM reactnativecommunity/react-native-android

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el proyecto al contenedor
COPY . .

# No se ejecuta gradlew porque el .apk se construye fuera
# RUN ./gradlew assembleDebug

CMD ["bash"]
