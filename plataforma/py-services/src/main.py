from ast import List
from typing import Optional
from qr.generator import generate_qr
from qr.detector import detect_qr
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from fastapi.responses import JSONResponse
import json
from fastapi.middleware.cors import CORSMiddleware
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

