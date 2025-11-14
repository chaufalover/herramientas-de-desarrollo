from qr.generator import generate_qr
from qr.detector import detect_qr
from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.responses import JSONResponse
import json
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="QR mircroservice");

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
    

##CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:8080",
        "http://127.0.0.1:8001"
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
);