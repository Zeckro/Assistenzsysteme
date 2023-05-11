import paho.mqtt.client as mqtt
import mediapipe as mp
import cv2
import keras
import tensorflow
import numpy as np
import os
import pickle
import time



class CameraControl:
    #MQTT methods
    def on_connect(self,client, userdata, flags, rc):

        print("Connected with result code " + str(rc))
        client.subscribe("test/topic")
        client.subscribe("test2/topic")


    def on_message(self,client, userdata, msg):
        if msg.topic == "test/topic":
            print(msg.topic+" "+str(msg.payload))
        elif msg.topic == "test2/topic":
            print("Test2")

    def __init__(self):
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message

        self.client.connect("localhost", 1883, 60)
        dir_path = os.path.dirname(os.path.realpath(__file__))  
        self.path = os.path.join(dir_path, 'model0905_relativeTolWrist_smallDropout.h5')
        self.model = keras.models.load_model(self.path)
        self.sequence_length = self.model.layers[0].input_shape[1]

        with open('module_camera\\mappingActivity.pkl', 'rb') as f:
            mappingActivity = pickle.load(f)
            self.actions = [mappingActivity[i] for i in sorted(mappingActivity.keys(), reverse=False)]
            print(self.actions)

    """
    def getRealTimeCamera(self,selectData):
        mp_drawing = mp.solutions.drawing_utils
        mp_holistic = mp.solutions.holistic
        cap = cv2.VideoCapture(0)
        # Initiate holistic model
        with mp_holistic.Holistic(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
            
            while cap.isOpened() and selectData:
                ret, frame = cap.read()
                
                # Recolor Feed
                image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                # Make Detections
                results = holistic.process(image)
                # print(results.face_landmarks)
                
                # face_landmarks, pose_landmarks, left_hand_landmarks, right_hand_landmarks
                
                # Recolor image back to BGR for rendering
                image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
                
                # Draw face landmarks
                #mp_drawing.draw_landmarks(image, results.face_landmarks, mp_holistic.FACE_CONNECTIONS)
                
                # Right hand
                mp_drawing.draw_landmarks(image, results.right_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

                # Left Hand
                mp_drawing.draw_landmarks(image, results.left_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

                # Pose Detections
                mp_drawing.draw_landmarks(image, results.pose_landmarks, mp_holistic.POSE_CONNECTIONS)

                #print(type(results.left_hand_landmarks))
                print(results.left_hand_landmarks)

                                
                cv2.imshow('Raw Webcam Feed', image)

                if cv2.waitKey(10) & 0xFF == ord('q'):
                    break

        cap.release()
        cv2.destroyAllWindows()
        """

    def get_Camera_Activity(self,relativeCoordinates=True,relativeTolwrist=True):
        mp_drawing = mp.solutions.drawing_utils
        mp_hand = mp.solutions.hands
        cap = cv2.VideoCapture(1)
        # Initiate holistic model
        data = []
        with mp_hand.Hands(min_detection_confidence=0.5, min_tracking_confidence=0.5) as holistic:
            
            while cap.isOpened():
                timeLoopBegin = time.time()
                ret, frame = cap.read()
            
                
                # Recolor Feed
                image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                # Make Detections
                results = holistic.process(image)
                # print(results.face_landmarks)
                
                # face_landmarks, pose_landmarks, left_hand_landmarks, right_hand_landmarks
                
                # Recolor image back to BGR for rendering
                image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)
                

                if results.multi_hand_landmarks and len(results.multi_hand_landmarks) == 2:
                    datasetLeftHand = [i for i in results.multi_hand_landmarks[0].landmark]
                    datasetRightHand = [i for i in results.multi_hand_landmarks[1].landmark]
                    mp_drawing.draw_landmarks(image, results.multi_hand_landmarks[0], mp_hand.HAND_CONNECTIONS)
                    mp_drawing.draw_landmarks(image, results.multi_hand_landmarks[1], mp_hand.HAND_CONNECTIONS)

                    dataset = [datasetLeftHand + datasetRightHand]
                    #print(dataset)
                    datasettmp = []
                    if relativeCoordinates:
                        if relativeTolwrist:
                            for i in dataset[0]:
                                datasettmp.append(i.x - dataset[0][0].x)
                                datasettmp.append(i.y - dataset[0][0].y)
                                datasettmp.append(i.z)
                            #tmpx = datasettmp[0]
                            #tmpy = datasettmp[1]
                            #datasettmp = [datasettmp[i] - tmpx for i in range(0,len(datasettmp),3)]
                            #datasettmp = [datasettmp[i+1] - tmpy for i in range(0,len(datasettmp),3)]
                        else:
                            tmpCnt = 0
                            for i in dataset[0]:
                                if tmpCnt > 0 and tmpCnt < 21:
                                    datasettmp.append(i.x-datasettmp[0])
                                    datasettmp.append(i.y-datasettmp[1])
                                    datasettmp.append(i.z)
                                elif tmpCnt > 21:
                                    datasettmp.append(i.x-datasettmp[63])
                                    datasettmp.append(i.y-datasettmp[64])
                                    datasettmp.append(i.z)
                                else:
                                    datasettmp.append(i.x)
                                    datasettmp.append(i.y)
                                    datasettmp.append(i.z)
                                tmpCnt+=1
                    else:
                        for i in dataset[0]:

                            #print(i)
                            datasettmp.append(i.x)
                            datasettmp.append(i.y)
                            datasettmp.append(i.z)
                    if len(data) == self.sequence_length:
                        data.pop(0)
                    data.append(datasettmp)

                    if len(data) == self.sequence_length:
                        prediction = self.model.predict(np.expand_dims(data,axis=0))[0]
                        print(self.actions[np.argmax(prediction)])
                    
                    
                #os.system('cls')
                
            
                cv2.imshow('camera feed', image)
                timeLoopDif = time.time() - timeLoopBegin
                print("FPS" + str(int(1/timeLoopDif)))

                if cv2.waitKey(10) & 0xFF == ord('q'):
                    break

        cap.release()
        cv2.destroyAllWindows()
        return data





camera1 = CameraControl()
#camera1.getRealTimeCamera(True)
camera1.get_Camera_Activity()


#camera1.client.loop_forever()



