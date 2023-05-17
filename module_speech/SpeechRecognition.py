import speech_recognition as sr
import pyaudio
import threading
import paho.mqtt.client as mqtt
from transformers import pipeline
import time
import json
from enum import Enum

WAKE_WORD = "roxy"
CONFIDENCE = 0.70
TIMEOUT = 30

class NextStep(Enum):
    FORWARD = 1
    BACKWARD = 2

class SpeechRecognition:
    #MQTT methods
    def on_connect(self,client, userdata, flags, rc):
        print("Connected with result code " + str(rc))
        client.subscribe("master/current_task")

    def on_message(self,client, userdata, msg):
        print("Message received: ")
        if msg.topic == "master/current_task":
            try:
                payload = json.loads(msg.payload)
                self.task = payload['task']
                self.gotTask = True
            except Exception as e:
                print(e)
        else:
            print(msg.topic+" "+str(msg.payload))

    def __init__(self):
        print("Initializing...")
        start = time.time()
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.connect("localhost", 1883, 60)

        self.gotTask = False        
        self.processor = pipeline(model="facebook/bart-large-mnli")
        end = time.time()
        print("MQTT-Client and Processing-Model initialised")
        print("Init took " + str(end-start) + " seconds")
        start = time.time()
        print("Waiting for task from master")
        while(not self.gotTask):
            if time.time() - start  >= TIMEOUT:
                raise TimeoutError("Did not receive a task from master in specified timeout period (" + str(TIMEOUT) + " seconds)")
            time.sleep(0.1)
        self.is_listening = True
        self.listenTask = threading.Thread(target=SpeechRecognition.listen, args=(self,))
        self.listenTask.start()

    # callback function
    def wake_word_callback(self, text):
        try:
            print("Wake word detected! Processing text: "+text)
            response = self.processor(
                text,
                candidate_labels=["forward", "backward", "other"],
            )
            #print(response)
            label = response["labels"][0]
            score = response["scores"][0]
            if(score > CONFIDENCE and not label =="other"):
                self.publishTask(NextStep.FORWARD if label == "forward" else NextStep.BACKWARD)
            else:
                #other
                pass
        except:
            print("Error processing text")

    def publishTask(self, nextStep: NextStep):
        new_task = self.task-1 if nextStep == NextStep.BACKWARD else self.task+1
        payload = json.dumps({"current_task": self.task, "new_task": new_task})
        print(payload)
        self.client.publish("speech_module/task", payload, qos=2)

    def play_audio(self,audio_data):
        try:
            print("Playing audio")
            p = pyaudio.PyAudio()
            stream = p.open(format=p.get_format_from_width(audio_data.sample_width),
                            channels=1,
                            rate=44100,
                            output=True)
            stream.write(audio_data.get_wav_data())
            stream.stop_stream()
            stream.close()
            p.terminate()
            print("Finished playing audio")
        except Exception as e:
            print("Error playing audio: ", e)

    def listen(self):
        # Initialize the recognizer
        r = sr.Recognizer()

        # Use the default microphone as the audio source
        with sr.Microphone() as source:
            # Calibrate the microphone to remove noise
            r.adjust_for_ambient_noise(source)
            r.energy_threshold = 1000  # TODO bringt das was?
            print("Listening for wake word...")

            while self.is_listening:
                # Listen for audio input
                #audio = r.listen(source)
                #TODO improve listening
                audio = r.listen(source,5,5)
                # DEBUG -- play audio in separate thread
                #t = threading.Thread(target=SpeechRecognition.play_audio, args=(self,audio,))
                #t.start()

                try:
                    #  google speech recognitoin TODO funktionieren die andere besser?
                    text = r.recognize_google(audio).lower()
                    print(text)
                    # Check if the wake word is in the transcribed text
                    if WAKE_WORD in text:
                        #only process text after wake word
                        text = text.partition(WAKE_WORD)[2]
                        t = threading.Thread(target=SpeechRecognition.wake_word_callback, args=(self,text,))
                        t.start()
                except sr.UnknownValueError:
                    # audio not recognised
                    print("Could not understand audio")
                except sr.RequestError as e:
                    # error in google Speech recognition
                    print("Could not request results from Google Speech Recognition service; {0}".format(e))

if __name__ == '__main__':
    speechrecognition = SpeechRecognition()
    speechrecognition.client.loop_forever()
    speechrecognition.is_listening = False #stops listen task