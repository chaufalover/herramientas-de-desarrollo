import cv2
import numpy as np
import pickle
from pathlib import Path
from typing import Dict

class FaceRegister:
    def __init__(self, data_dir: str = "face_data"):
        
        self.data_dir = Path(data_dir)
        self.data_dir.mkdir(exist_ok=True)
        
        cascade_path = cv2.data.haarcascades + 'haarcascade_frontalface_default.xml'
        self.face_cascade = cv2.CascadeClassifier(cascade_path)
        
        self.recognizer = cv2.face.LBPHFaceRecognizer_create()
        
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
    
    def register_person(self, person_id: str, image: np.ndarray) -> Dict:
        
        success, face_encoding = self.extract_face_encoding(image)
        
        if not success:
            return {
                "success": False,
                "message": "No se detectó un rostro en la imagen capturada",
                "person_id": person_id
            }
        
        face_encodings = [face_encoding]
        
        for angle in [-5, 5]:
            rotated = self._rotate_image(face_encoding, angle)
            face_encodings.append(rotated)
        
        for brightness in [0.9, 1.1]:
            adjusted = (face_encoding * brightness).astype(np.uint8)
            face_encodings.append(adjusted)

        person_data = {
            "person_id": person_id,
            "encodings": face_encodings,
            "num_samples": len(face_encodings)
        }
        
        person_file = self.data_dir / f"{person_id}.pkl"
        with open(person_file, 'wb') as f:
            pickle.dump(person_data, f)
        
        self._retrain_model()
        
        return {
            "success": True,
            "message": f"Rostro registrado exitosamente",
            "person_id": person_id
        }
    
    def _rotate_image(self, image: np.ndarray, angle: float) -> np.ndarray:
       
        height, width = image.shape
        center = (width // 2, height // 2)
        matrix = cv2.getRotationMatrix2D(center, angle, 1.0)
        rotated = cv2.warpAffine(image, matrix, (width, height))
        return rotated
    
    def _retrain_model(self):
       
        faces = []
        labels = []
        person_map = {}
        
        for idx, person_file in enumerate(self.data_dir.glob("*.pkl")):
            with open(person_file, 'rb') as f:
                person_data = pickle.load(f)
            
            person_id = person_data["person_id"]
            person_map[idx] = person_id
            
            for encoding in person_data["encodings"]:
                faces.append(encoding)
                labels.append(idx)
        
        if len(faces) > 0:
            self.recognizer.train(faces, np.array(labels))
            
            model_file = self.data_dir / "face_model.yml"
            self.recognizer.save(str(model_file))
            
            map_file = self.data_dir / "person_map.pkl"
            with open(map_file, 'wb') as f:
                pickle.dump(person_map, f)