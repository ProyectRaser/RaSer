# Usar la imagen base de Python
FROM python:3.11-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo requirements.txt
COPY requirements.txt .

# Instalar las dependencias
RUN pip install --no-cache-dir -r requirements.txt

# Copiar todo el código del directorio src al contenedor
COPY src/ ./src/

# Copiar el archivo de credenciales de Firebase al contenedor
COPY firebase_key.json /app/firebase_key.json

# Exponer el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación con Uvicorn
CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "8080"]
