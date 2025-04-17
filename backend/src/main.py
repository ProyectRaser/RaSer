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
usuarios_requests_total = Counter("usuarios_requests_total", "Peticiones al endpoint /usuarios")
server_requests_total = Counter("server_requests_total", "Número total de peticiones")

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



@app.get("/metrics", response_class=PlainTextResponse)
def metrics():
    return generate_latest()
