import sys
import types

import pytest
from fastapi import FastAPI
from fastapi.testclient import TestClient
from pydantic import ValidationError

# Simula temporalmente las dependencias de PostgreSQL.
# Esto permite importar las rutas sin cargar psycopg ni conectarse a una BD.
fake_database = types.ModuleType("app.database")


def fake_get_db():
    yield None


fake_database.get_db = fake_get_db
sys.modules["app.database"] = fake_database


# Simula el modelo Job, que no se utiliza en la prueba de /health.
fake_models = types.ModuleType("app.models")


class FakeJob:
    pass


fake_models.Job = FakeJob
sys.modules["app.models"] = fake_models


from app.routes import router
from app.schemas import JobCreate

app_under_test = FastAPI()
app_under_test.include_router(router)

client = TestClient(app_under_test)


def test_health_returns_ok():
    response = client.get("/health")

    assert response.status_code == 200
    assert response.json() == {
        "service": "python-service",
        "status": "ok",
    }


def test_job_create_accepts_valid_text():
    payload = JobCreate(
        text="  Este texto es valido  "
    )

    assert payload.text == "Este texto es valido"


def test_job_create_rejects_text_shorter_than_three_characters():
    with pytest.raises(ValidationError):
        JobCreate(text="  ab  ")