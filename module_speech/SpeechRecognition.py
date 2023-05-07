import speech_recognition as sr
import pyaudio
import threading
import paho.mqtt.client as mqtt

WAKE_WORD = "hello"

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
        self.client = mqtt.Client()
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.connect("localhost", 1883, 60)
        print("MQTT-Client initialised")
        self.listen()

    # callback function
    def wake_word_callback(self):
        print("Wake word detected!")

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
                audio = r.listen(source)
                # DEBUG -- play audio in separate thread
                t = threading.Thread(target=SpeechRecognition.play_audio, args=(self,audio,))
                t.start()

                try:
                    #  google speech recognitoin TODO funktionieren die andere besser?
                    text = r.recognize_google(audio)
                    print(text)
                    # Check if the wake word is in the transcribed text
                    if WAKE_WORD in text.lower():
                        self.wake_word_callback()

                except sr.UnknownValueError:
                    # audio not recognised
                    print("Could not understand audio")
                except sr.RequestError as e:
                    # error in google Speech recognition
                    print("Could not request results from Google Speech Recognition service; {0}".format(e))

speechrecognition = SpeechRecognition()