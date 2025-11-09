import cv2

capture = cv2.VideoCapture(0);

qrDetector = cv2.QRCodeDetector();

while (capture.isOpened()) : 
    ret, frame = capture.read();
    data, bbox, rectifiedImg = qrDetector.detectAndDecode(frame)

    if (cv2.waitKey(1) == 27 or len(data)) :
        print("Dato: ", data);
        cv2.imshow("Webcam", rectifiedImg);
        break;
    else:
        cv2.imshow("Webcam", frame);

capture.release();
cv2.destroyAllWindows();

