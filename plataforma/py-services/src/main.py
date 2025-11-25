from ast import List
from typing import Optional
from qr.generator import generate_qr
from qr.detector import detect_qr
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from fastapi.responses import JSONResponse
import json
from fastapi.middleware.cors import CORSMiddleware
from src.face.register import FaceRegister
from src.face.validate import FaceValidator
import cv2
import numpy as np
import base64

app = FastAPI(title="QR/Face mircroservice");

##CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "*"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
);

@app.get("/")
def read_root():
    return {"message": "Welcome to the QR/Face microservice!"};

## QR endpoints

class requestQR(BaseModel):
    account_number: str;

@app.post("/generate")
def create_qr(request: requestQR):
    qr_img_b64 = generate_qr(request.account_number);
    return {"qr_image": qr_img_b64};

JAVA_API_URL = "http://localhost:8080/login/qr/validate";

@app.get("/detect")
def start_detection():
    result = detect_qr(JAVA_API_URL)
    if result is None:
        return JSONResponse(content=None, status_code=200)
    if isinstance(result, str):
        try:
            parsed = json.loads(result)
            return JSONResponse(content=parsed)
        except Exception:
            return JSONResponse(content={"raw": result})
    return JSONResponse(content=result)

## Face endpoints
face_register = FaceRegister(data_dir="face_data")
face_validator = FaceValidator(data_dir="face_data")

class RegisterRequest(BaseModel):
    person_id: str
    images_base64: list[str]  

class ValidateRequest(BaseModel):
    image_base64: str
    confidence_threshold: Optional[float] = 70.0


def decode_image(base64_string: str) -> np.ndarray:
   
    try:
        if ',' in base64_string:
            base64_string = base64_string.split(',')[1]
        
        img_bytes = base64.b64decode(base64_string)
        img_array = np.frombuffer(img_bytes, dtype=np.uint8)
        img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)
        
        if img is None:
            raise ValueError("No se pudo decodificar la imagen")
        
        return img
    except Exception as e:
        raise HTTPException(
            status_code=400, 
            detail=f"Error al decodificar imagen: {str(e)}"
        )

@app.post("/api/face/register")
async def register_face(request: RegisterRequest):
   
    try:
        
        if not request.person_id or request.person_id.strip() == "":
            return JSONResponse(
                status_code=400,
                content={
                    "success": False,
                    "message": "El person_id no puede estar vacío"
                }
            )
        
        if not request.image_base64:
            return JSONResponse(
                status_code=400,
                content={
                    "success": False,
                    "message": "No se recibió ninguna imagen"
                }
            )
        
        img = decode_image(request.image_base64)
        
        result = face_register.register_person(request.person_id, img)
        
        return JSONResponse(
            status_code=200 if result["success"] else 400,
            content=result
        )
    
    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(
            status_code=500, 
            detail=f"Error interno del servidor: {str(e)}"
        )

@app.post("/api/face/validate")
async def validate_face(request: ValidateRequest):
    try:
        if not request.image_base64:
            return JSONResponse(
                status_code=400,
                content={
                    "success": False,
                    "message": "No se recibió ninguna imagen"
                }
            )
        
        img = decode_image(request.image_base64)
        
        result = face_validator.validate_face(img, request.confidence_threshold)
        
        return JSONResponse(
            status_code=200,
            content=result
        )
    
    except HTTPException as he:
        raise he
    except Exception as e:
        raise HTTPException(
            status_code=500, 
            detail=f"Error interno del servidor: {str(e)}"
        )