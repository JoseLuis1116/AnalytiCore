from datetime import datetime

from pydantic import BaseModel, ConfigDict, Field, field_validator


class JobCreate(BaseModel):
    text: str = Field(
        min_length=3,
        max_length=5000
    )

    @field_validator("text")
    @classmethod
    def validate_text(cls, value: str) -> str:
        clean_text = value.strip()

        if len(clean_text) < 3:
            raise ValueError(
                "El texto debe tener al menos 3 caracteres"
            )

        return clean_text


class JobResponse(BaseModel):
    id: str
    text: str
    status: str
    sentiment: str | None
    keywords: list[str] | None
    created_at: datetime
    updated_at: datetime

    model_config = ConfigDict(
        from_attributes=True
    )