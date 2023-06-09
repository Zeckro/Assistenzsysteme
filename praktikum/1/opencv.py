import cv2
import time
import numpy as np
import mediapipe as mp

cap = cv2.VideoCapture(1)
frame_count = 0
start_time = time.time()
mp_drawing = mp.solutions.drawing_utils
mp_hand = mp.solutions.hands
with mp_hand.Hands(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
    while cap.isOpened():
        isTrue, frame = cap.read()
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = holistic.process(image)
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        dimension = (image.shape)
        amount = 6
        height = 100
        y_start = 50
        alpha = 0.6
        currentBox = -1
        if results.multi_hand_landmarks and len(results.multi_hand_landmarks) == 1:
            mp_drawing.draw_landmarks(image, results.multi_hand_landmarks[0], mp_hand.HAND_CONNECTIONS)
            datasetHand = [i for i in results.multi_hand_landmarks[0].landmark]
            indexFingerCoords = datasetHand[8]
            if(indexFingerCoords.y* dimension[0] > y_start and indexFingerCoords.y* dimension[0] < y_start + height):
                currentBox = (int(indexFingerCoords.x / (1.0/amount))+1)

        overlay = image.copy()
        
        #FPS Anzeige
        fps = 'FPS: {:.2f}'.format(frame_count / (time.time() - start_time))
        position = (5, 25)
        font = cv2.FONT_HERSHEY_SIMPLEX
        font_scale = 1
        color = (0, 255, 0)  # Grün (BGR-Format)
        thickness = 1
        cv2.putText(image, fps, position, font, font_scale, color, thickness)
        frame_count += 1

        
        
        #draw rectangles
        for i in range(amount):
            cv2.rectangle(image, (int(i*dimension[1]/amount), y_start), (int(i*dimension[1]/amount+dimension[1]/amount), y_start+height), (0, int(i*255/amount), 255), -1)
            color = (255, 255, 255)
            if i+1 == currentBox:
                color = (0,255,0)
            cv2.putText(image, str(i+1), (int(i*dimension[1]/amount), y_start-5), font, 0.7, color,  2, cv2.LINE_AA)
        #make rectangles transparent
        image = cv2.addWeighted(overlay, alpha, image, 1 - alpha, 0)

        #show frame
        cv2.imshow("Video", image)



        # Warten auf eine Taste zum Schließen des Fensters
        if cv2.waitKey(1) == ord('q'):
            break

cap.release()
cv2.destroyAllWindows()
