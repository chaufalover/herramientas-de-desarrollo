import cv2
import requests

def detect_qr(api_url):
    capture = cv2.VideoCapture(0)
    qrDetector = cv2.QRCodeDetector()

    try:
        while capture.isOpened():
            ret, frame = capture.read()
            if not ret:
                print("detect_qr: frame no disponible")
                break

            data, bbox, rectifiedImg = qrDetector.detectAndDecode(frame)

            if len(data):
                print(f"QR detectado: {data}")
                try:
                    response = requests.post(api_url, json={"numeroTarjeta": data}, timeout=10)
                    try:
                        resp_json = response.json()
                        print("Respuesta del servidor (json):", resp_json)
                        return resp_json
                    except ValueError:
                        print("Respuesta del servidor (texto):", response.text)
                        return {"raw": response.text}
                except Exception as e:
                    print("Error al enviar el número al backend:", e)
                    return {"error": str(e)}
            else:
                cv2.imshow("Escanea tu QR", frame)

            if cv2.waitKey(1) == 27:  
                break
    finally:
        capture.release()
        cv2.destroyAllWindows()
