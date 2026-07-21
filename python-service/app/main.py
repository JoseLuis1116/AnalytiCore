from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import app.models
from app.database import Base, engine
from app.routes import router


Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="AnalytiCore Submission Service",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:5173",
        "http://localhost:3000"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

app.include_router(router)


@app.get("/")
def root():
    return {
        "message": "Servicio de submisión de AnalytiCore"
    }