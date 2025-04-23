from fastapi import FastAPI
from prometheus_client import Counter, generate_latest
from fastapi.responses import PlainTextResponse
import firebase_admin
from firebase_admin import credentials, firestore

# Inicializar FastAPI
app = FastAPI()

# Inicializar Firebase
cred = credentials.Certificate("firebase_key.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

# Métricas
main_requests_total = Counter("main_requests_total", "Número de peticiones al endpoint raíz")
healthcheck_requests_total = Counter("healthcheck_requests_total", "Número de peticiones al healthcheck")
server_requests_total = Counter("server_requests_total", "Número total de peticiones")

# Metricas Prometheus
REQUEST_COUNT = Counter("http_requests_total", "Total HTTP Requests", ["method", "endpoint"])
TAREAS_COUNT = Counter("tareas_requests_total", "Total Tareas Requests", ["method", "endpoint"])
USUARIOS_COUNT = Counter("usuarios_requests_total", "Total Usuarios Requests", ["method", "endpoint"])

#Middleware para contar peticiones
@app.middleware("http")
async def prometheus_middleware(request, call_next):
    response = await call_next(request)
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
    return generate_latest()
