from qr.generator import generate_qr
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="QR mircroservice");

class requestQR(BaseModel):
    account_number: str;

@app.post("/generate")
def create_qr(request: requestQR):
    qr_img_b64 = generate_qr(request.account_number);
    return {"qr_image": qr_img_b64};