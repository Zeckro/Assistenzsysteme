import speech_recognition as sr
import pyaudio
import threading

WAKE_WORD = "hello"

# callback function
def wake_word_callback():
    print("Wake word detected!")

def play_audio(audio_data):
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
        t = threading.Thread(target=play_audio, args=(audio,))
        t.start()

        try:
            #  google speech recognitoin TODO funktionieren die andere besser?
            text = r.recognize_google(audio)
            print(text)
            # Check if the wake word is in the transcribed text
            if WAKE_WORD in text.lower():
                wake_word_callback()

        except sr.UnknownValueError:
            # audio not recognised
            print("Could not understand audio")
        except sr.RequestError as e:
            # error in google Speech recognition
            print("Could not request results from Google Speech Recognition service; {0}".format(e))
