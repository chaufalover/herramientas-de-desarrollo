"""
src/face/validate.py
Sistema de validación de rostros
"""
import cv2
import numpy as np
import pickle
from pathlib import Path
from typing import Dict

class FaceValidator:
    def __init__(self, data_dir: str = "face_data"):
       
        self.data_dir = Path(data_dir)
        
        cascade_path = cv2.data.haarcascades + 'haarcascade_frontalface_default.xml'
        self.face_cascade = cv2.CascadeClassifier(cascade_path)
        
        self.recognizer = cv2.face.LBPHFaceRecognizer_create()
        self.person_map = {}
        
        self.model_file = self.data_dir / "face_model.yml"
        self.map_file = self.data_dir / "person_map.pkl"
        
        self._load_model()
    
    def _load_model(self):
       
        if self.model_file.exists() and self.map_file.exists():
            self.recognizer.read(str(self.model_file))
            
            with open(self.map_file, 'rb') as f:
                self.person_map = pickle.load(f)
    
    def detect_face(self, image: np.ndarray) -> tuple:
       
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        faces = self.face_cascade.detectMultiScale(
            gray,
            scaleFactor=1.1,
            minNeighbors=5,
            minSize=(100, 100)
        )
        
        if len(faces) == 0:
            return False, None
            
        largest_face = max(faces, key=lambda f: f[2] * f[3])
        return True, largest_face
    
    def extract_face_encoding(self, image: np.ndarray) -> tuple:
       
        detected, face_coords = self.detect_face(image)
        
        if not detected:
            return False, None
        
        x, y, w, h = face_coords
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
        face_roi = gray[y:y+h, x:x+w]
        
        face_roi = cv2.resize(face_roi, (200, 200))
        
        return True, face_roi
    
    def validate_face(self, image: np.ndarray, confidence_threshold: float = 70.0) -> Dict:
        
        if not self.model_file.exists() or not self.person_map:
            return {
                "success": False,
                "recognized": False,
                "message": "No hay personas registradas en el sistema",
                "person_id": None,
                "confidence": 0.0
            }
        
        success, face_encoding = self.extract_face_encoding(image)
        
        if not success:
            return {
                "success": False,
                "recognized": False,
                "message": "No se detectó rostro en la imagen",
                "person_id": None,
                "confidence": 0.0
            }
        
        label, confidence = self.recognizer.predict(face_encoding)
        

        recognized = confidence < confidence_threshold
        
        person_id = self.person_map.get(label, "unknown") if recognized else None
        
        confidence_percentage = max(0, min(100, 100 - confidence))
        
        return {
            "success": True,
            "recognized": recognized,
            "message": f"Rostro reconocido: {person_id}" if recognized else "Rostro no reconocido",
            "person_id": person_id,
            "confidence": float(confidence),
            "confidence_percentage": float(confidence_percentage)
        }