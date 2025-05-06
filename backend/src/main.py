from fastapi import FastAPI
from prometheus_client import Counter, Gauge, generate_latest
from fastapi.responses import PlainTextResponse
import firebase_admin
from firebase_admin import credentials, firestore
import uvicorn
import os
import psutil

# Inicializar FastAPI
app = FastAPI()

# Inicializar Firebase
cred = credentials.Certificate("firebase_key.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# Métricas generales
main_requests_total = Counter("main_requests_total", "Número de peticiones al endpoint raíz")
healthcheck_requests_total = Counter("healthcheck_requests_total", "Número de peticiones al healthcheck")
server_requests_total = Counter("server_requests_total", "Número total de peticiones")

# Métricas Prometheus
REQUEST_COUNT = Counter("http_requests_total", "Total HTTP Requests", ["method", "endpoint"])
TAREAS_COUNT = Counter("tareas_requests_total", "Total Tareas Requests", ["method", "endpoint"])
USUARIOS_COUNT = Counter("usuarios_requests_total", "Total Usuarios Requests", ["method", "endpoint"])

# Métricas Gauge
cpu_usage_total = Gauge("cpu_usage_seconds_total", "Total CPU time used by the process")
total_usuarios_gauge = Gauge("total_usuarios", "Cantidad total de usuarios en la base de datos")
total_tareas_gauge = Gauge("total_tareas", "Cantidad total de tareas en la base de datos")

# 🔧 Corregidas: funciones sin await
def actualizar_total_usuarios():
    try:
        usuarios_ref = db.collection("Usuarios")
        docs = list(usuarios_ref.stream())
        print(f"🔥 Documentos obtenidos (Usuarios): {docs}")
        total = len(docs)
        print(f"✅ Total usuarios: {total}")
        total_usuarios_gauge.set(total)
    except Exception as e:
        print(f"❌ Error al contar usuarios: {e}")

def actualizar_total_tareas():
    try:
        tareas_ref = db.collection("Tareas")
        docs = list(tareas_ref.stream())
        print(f"🔥 Documentos obtenidos (Tareas): {docs}")
        total = len(docs)
        print(f"✅ Total tareas: {total}")
        total_tareas_gauge.set(total)
    except Exception as e:
        print(f"❌ Error al contar tareas: {e}")

# Middleware para contar peticiones
@app.middleware("http")
async def prometheus_middleware(request, call_next):
    response = await call_next(request)

    if request.url.path == "/usuarios":
        USUARIOS_COUNT.labels(method=request.method, endpoint=request.url.path).inc()
    elif request.url.path == "/tareas":
        TAREAS_COUNT.labels(method=request.method, endpoint=request.url.path).inc()

    REQUEST_COUNT.labels(method=request.method, endpoint=request.url.path).inc()
    return response

# Endpoints
@app.get("/")
def root():
    main_requests_total.inc()
    server_requests_total.inc()
    return {"message": "Hello World"}

@app.get("/health", response_class=PlainTextResponse)
def health():
    healthcheck_requests_total.inc()
    server_requests_total.inc()
    return {"health": "ok"}

@app.get("/usuarios")
def get_users():
    actualizar_total_usuarios()
    try:
        usuarios_ref = db.collection("Usuarios").stream()
        usuarios = [doc.to_dict() for doc in usuarios_ref]
        print("Usuarios obtenidos:", usuarios)
        return usuarios
    except Exception as e:
        print("Error:", e)
        return {"error": str(e)}

@app.get("/tareas")
def get_tasks():
    actualizar_total_tareas()
    try:
        tareas_ref = db.collection("Tareas").stream()
        tareas = [doc.to_dict() for doc in tareas_ref]
        print("Tareas obtenidas:", tareas)
        return tareas
    except Exception as e:
        print("Error:", e)
        return {"error": str(e)}

@app.get("/metrics", response_class=PlainTextResponse)
def metrics():
    cpu_usage_total.set(psutil.cpu_percent() / 100.0)
    actualizar_total_usuarios()
    actualizar_total_tareas()
    return PlainTextResponse(generate_latest().decode("utf-8"))

# Ejecutar localmente si fuera necesario
if __name__ == "__main__":
    port = int(os.environ.get("PORT", 8080))
    uvicorn.run(app, host="0.0.0.0", port=port)