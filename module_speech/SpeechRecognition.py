import speech_recognition as sr
import pyaudio
import threading
import paho.mqtt.client as mqtt
from transformers import pipeline
import time

WAKE_WORD = "roxy"
CONFIDENCE = 0.70

class SpeechRecognition:
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
        else:
            print(msg.topic+" "+str(msg.payload))

    def __init__(self):
        print("Initializing...")
        start = time.time()
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.connect("localhost", 1883, 60)
        #TODO get task from server
        self.task = 0
        self.processor = pipeline(model="facebook/bart-large-mnli")
        end = time.time()
        print("MQTT-Client and Processing-Model initialised")
        print("Init took " + str(end-start) + " seconds")
        self.listen()

    # callback function
    def wake_word_callback(self, text):
        try:
            print("Wake word detected! Processing text: "+text)
            response = self.processor(
                text,
                candidate_labels=["forward", "backward", "other"],
            )
            print(response)
            label = response["labels"][0]
            score = response["scores"][0]
            if(score > CONFIDENCE and not label =="other"):
                self.publishTask(True if label == "forward" else False)
            else:
                #other
                pass
        except:
            print("Error processing text")

    def publishTask(self, increment):
        if increment:
            payload =  str(self.task+1)
        else:
            payload = str(self.task-1)
        print("publishing with payload: " + payload)
        self.client.publish("speech_module/task", increment)

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
        except e:
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

            while True:
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

speechrecognition = SpeechRecognition()