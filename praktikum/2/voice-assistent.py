import sys
import speech_recognition as sr
import pyaudio
import threading
import pyttsx3 as tts
from neuralintents import GenericAssistant
import wikipedia
import pyjokes
import python_weather
import asyncio
import pywhatkit

WAKE_WORD = "computer"

class Assistant:
    def __init__(self):
        self.recognizer = sr.Recognizer()
        self.speaker = tts.init()
        self.speaker.setProperty("rate", 150)
        self.speaker.setProperty("voice", "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Speech\Voices\Tokens\TTS_MS_EN-US_ZIRA_11.0")

        self.assistant = GenericAssistant("praktikum\\2\\intents.json", intent_methods={"wikipedia": self.wikipedia, "jokes": self.jokes, "weather": self.weather, "youtube": self.youtube, "stop": self.terminate})
        self.assistant.train_model()
        self.listen()

    def terminate(self):
        self.tts_output("Terminating voice assistant... Bye bye!")
        sys.exit(0)

    def tts_output(self, text_to_say):
        print(text_to_say)
        self.speaker.say(text_to_say)
        self.speaker.runAndWait()
        self.speaker.stop()

    def jokes(self):
        self.tts_output(pyjokes.get_joke(language="en"))

    def weather(self):
        asyncio.run(self.get_weather())

    async def get_weather(self):
        async with python_weather.Client(unit=python_weather.METRIC) as client:
            weather = await client.get('Rheda-Wiedenbrück')
            self.tts_output("The temperature in Rheda-Wiedenbrück is " + str(weather.current.temperature) + " °C.")

    def youtube(self):
        with sr.Microphone() as source:
            self.recognizer.adjust_for_ambient_noise(source)
            self.tts_output("What would you like to watch on YouTube?")
            audio = self.recognizer.listen(source, 5, 5)
            try:
                text_yt = self.recognizer.recognize_google(audio).lower()
                if text_yt:
                    print(text_yt)
                    pywhatkit.playonyt(text_yt)
                else:
                    self.tts_output("No results found.")
            except sr.UnknownValueError:
                # audio not recognised
                print("Could not understand audio")
            except sr.RequestError as e:
                # error in google Speech recognition
                self.tts_output("Could not request results from Google Speech Recognition service; {0}".format(e))
            except sr.WaitTimeoutError:
                self.tts_output("Nothing was said. Returning back...")

    def wikipedia(self):
        with sr.Microphone() as source:
            self.recognizer.adjust_for_ambient_noise(source)
            self.tts_output("What would you like to search for in Wikipedia?")
            audio = self.recognizer.listen(source, 5, 5)
            try:
                text_wiki = self.recognizer.recognize_google(audio).lower()
                print(text_wiki)
                results = wikipedia.search(text_wiki)
                #print(results)
                if len(results) > 0:
                    page = wikipedia.page(results[0])
                    self.tts_output("Wikipedia Title: " + page.title)
                    self.tts_output("Wikipedia Summary: " + page.summary)
                else:
                    self.tts_output("No Wikipedia results found.")
            except sr.UnknownValueError:
                # audio not recognised
                print("Could not understand audio")
            except sr.RequestError as e:
                # error in google Speech recognition
                self.tts_output("Could not request results from Google Speech Recognition service; {0}".format(e))
            except sr.WaitTimeoutError:
                self.tts_output("Nothing was said. Returning back...")

    def listen(self):
        # Use the default microphone as the audio source
        with sr.Microphone() as source:
            # Calibrate the microphone to remove noise
            self.recognizer.adjust_for_ambient_noise(source)
            print("Listening for wake word...")

            while True:
                audio = self.recognizer.listen(source, 5, 5)

                try:
                    text = self.recognizer.recognize_google(audio).lower()
                    print(text)
                    if WAKE_WORD in text:
                        #only process text after wake word
                        text = text.partition(WAKE_WORD)[2]
                        self.text = text
                        response = self.assistant.request(text)

                        if response is not None:
                            self.tts_output(response)

                except sr.UnknownValueError:
                    # audio not recognised
                    print("Could not understand audio")
                except sr.RequestError as e:
                    # error in google Speech recognition
                    self.tts_output("Could not request results from Google Speech Recognition service; {0}".format(e))

if __name__ == '__main__':
    speechrecognition = Assistant()
    speechrecognition.client.loop_forever()