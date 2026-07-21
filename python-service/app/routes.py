import os

import requests
from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.database import get_db
from app.models import Job
from app.schemas import JobCreate, JobResponse


router = APIRouter()

JAVA_SERVICE_URL = os.getenv(
    "JAVA_SERVICE_URL",
    "http://localhost:8080"
)


@router.get("/health")
def health():
    return {
        "service": "python-service",
        "status": "ok"
    }


@router.post(
    "/jobs",
    response_model=JobResponse,
    status_code=status.HTTP_201_CREATED
)
def create_job(
    payload: JobCreate,
    database: Session = Depends(get_db)
):
    job = Job(
        text=payload.text,
        status="PENDIENTE"
    )

    database.add(job)
    database.commit()
    database.refresh(job)

    try:
        response = requests.post(
            f"{JAVA_SERVICE_URL}/analysis/{job.id}",
            timeout=20
        )

        response.raise_for_status()

    except requests.RequestException as exception:
        job.status = "ERROR"
        database.commit()
        database.refresh(job)

        raise HTTPException(
            status_code=status.HTTP_502_BAD_GATEWAY,
            detail="No se pudo iniciar el análisis en el servicio Java"
        ) from exception

    database.refresh(job)

    return job


@router.get(
    "/jobs/{job_id}",
    response_model=JobResponse
)
def get_job(
    job_id: str,
    database: Session = Depends(get_db)
):
    job = database.get(Job, job_id)

    if job is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Trabajo no encontrado"
        )

    return job