import cv2
import time
import numpy as np
import mediapipe as mp
import math



def printCircles():
    circleColor = [(0,0,255),(0, 255, 255),(0, 255, 0)]
    #draw rectangles
    for i in range(amount):
        cv2.circle(image, center, (int(75*math.log(radius*(i+1),logBase))),circleColor[i],5)
    
    if(currentCircle == 0):
        cv2.putText(image, "ALARM! Stoppe Maschine", (10, 50), font, 0.7, (0,0,255),  2, cv2.LINE_AA)
    elif (currentCircle == 1):
        cv2.putText(image, "Warnung! Sie befinden sich nah einer Sperrzone", (10,50), font, 0.7, (0,255,255),  2, cv2.LINE_AA)

def printFPS():
    #FPS Anzeige
    fps = 'FPS: {:.2f}'.format(frame_count / (time.time() - start_time))
    position = (5, 25)
    
    font_scale = 1
    color = (0, 255, 0)  # Grün (BGR-Format)
    thickness = 1
    cv2.putText(image, fps, position, font, font_scale, color, thickness)

def printMenuButton(text):
    cv2.rectangle(image, (10, frame.shape[0] - 60), (100, frame.shape[0] - 10), (0, 0, 255), -1)
    cv2.putText(image, text, (15, frame.shape[0] - 20), font, 0.7, (255, 255, 255), 2, cv2.LINE_AA)

def printMenu():
    cv2.putText(image, "Selected radius: " + str(radius), (int(dimension[1]/3), 85), font, 0.7, (255, 255, 255),  2, cv2.LINE_AA)
    cv2.rectangle(image, (0, 100), (dimension[1], 150), (47, 12, 16), -1)
    cv2.rectangle(image, (int(dimension[1]/maxRadius * (radius-1)), 100), (int(dimension[1]/maxRadius*(radius-1)+50), 150), (255, 255, 255), -1)


font = cv2.FONT_HERSHEY_SIMPLEX
cap = cv2.VideoCapture(1)
frame_count = 0
start_time = time.time()
mp_drawing = mp.solutions.drawing_utils
mp_hand = mp.solutions.hands
prevState = False
currentState = False
menuOpened = False
radius= 10
maxRadius = 10
with mp_hand.Hands(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
    while cap.isOpened():
        isTrue, frame = cap.read()
        image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = holistic.process(image)
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
        dimension = (image.shape)

        center= (int(dimension[1]/2),int(dimension[0]/2))
        amount = 2
        alpha = 0.6
        logBase = math.e
        currentCircle = -1
        if results.multi_hand_landmarks and len(results.multi_hand_landmarks) == 1:
            mp_drawing.draw_landmarks(image, results.multi_hand_landmarks[0], mp_hand.HAND_CONNECTIONS)
            datasetHand = [i for i in results.multi_hand_landmarks[0].landmark]
            indexFingerCoords = datasetHand[8]
            if not menuOpened:
                currentCircle = int((logBase**(math.sqrt((indexFingerCoords.x*dimension[1]-center[0])**2 + (indexFingerCoords.y*dimension[0]-center[1])**2)/75))/radius)
            else:
                if(indexFingerCoords.y* dimension[0] > 100 and indexFingerCoords.y* dimension[0] < 150):
                    radius = (int(indexFingerCoords.x / (1.0/maxRadius))+1)
                pass
            if dimension[0] - 60 <= indexFingerCoords.y*dimension[0] <= dimension[0]-10 and 10 <= indexFingerCoords.x*dimension[1] <= 100:
                currentState = True
            else:
                currentState = False
            
            if currentState and not prevState and not menuOpened:
                menuOpened = True
            elif currentState and not prevState and menuOpened:
                menuOpened = False
            prevState = currentState

        if menuOpened:
            printMenu()
            printMenuButton("Back")
        else:
            printCircles()
            printMenuButton("Menu")
        printFPS()
        frame_count += 1

        #show frame
        print(image)
        cv2.imshow("Video", image)



        # Warten auf eine Taste zum Schließen des Fensters
        if cv2.waitKey(1) == ord('q'):
            break

cap.release()
cv2.destroyAllWindows()
