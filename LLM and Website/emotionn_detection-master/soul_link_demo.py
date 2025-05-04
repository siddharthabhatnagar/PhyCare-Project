import speech_recognition as sr
from textblob import TextBlob
from deep_translator import GoogleTranslator
from langchain_groq import ChatGroq
import os
import logging
from transformers import pipeline

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Set Groq API key from environment variable or fallback to provided key
GROQ_API_KEY = os.getenv("GROQ_API_KEY", "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")
if not GROQ_API_KEY:
    logger.warning("GROQ_API_KEY environment variable not set. LangChain Groq may not work properly.")
os.environ["GROQ_API_KEY"] = GROQ_API_KEY

# Initialize LangChain Groq with API key parameter
llm = ChatGroq(model="llama-3.1-8b-instant", temperature=0, groq_api_key=GROQ_API_KEY)

# Load multi-label emotion detection model
emotion_detector = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")

def speech_to_text(audio_file, language='en-IN'):
    r = sr.Recognizer()
    with sr.AudioFile(audio_file) as source:
        audio_data = r.record(source)
        try:
            text = r.recognize_google(audio_data, language=language)
            logger.info("[Speech-to-Text] Transcribed Text: %s", text)
            return text
        except sr.UnknownValueError:
            logger.error("Could not understand audio. Trying fallback language if applicable.")
            # Fallback for Japanese language codes
            if language.startswith('ja'):
                try:
                    text = r.recognize_google(audio_data, language='ja-JP')
                    logger.info("[Speech-to-Text] Fallback Transcribed Text: %s", text)
                    return text
                except Exception as e:
                    logger.error(f"Fallback recognition failed: {str(e)}")
            return "[Error] Could not understand the audio."
        except sr.RequestError:
            logger.error("API request failed. Try again later.")
            return "[Error] Speech recognition API failed."

def detect_emotion(text):
    blob = TextBlob(text)
    sentiment = blob.sentiment.polarity
    if sentiment > 0:
        emotion = "Positive"
    elif sentiment < 0:
        emotion = "Negative"
    else:
        emotion = "Neutral"
    logger.info("[Emotion Detection] Detected Emotion: %s", emotion)
    return emotion

def detect_multiple_emotions(text):
    emotions = ["joy", "sadness", "anger", "fear", "love", "surprise"]
    predictions = emotion_detector(text, candidate_labels=emotions)
    logger.info("[Multi-Label Emotion Detection] Predictions: %s", predictions)
    return predictions

def get_weighted_emotions(text):
    emotions = ["joy", "sadness", "anger", "fear", "love", "surprise"]
    predictions = emotion_detector(text, candidate_labels=emotions)
    
    # predictions is a dict with 'labels' and 'scores' lists
    emotion_scores = {label: round(score * 100, 2) for label, score in zip(predictions['labels'], predictions['scores'])}
    logger.info("[Emotion Detection] Weighted Emotion Scores: %s", emotion_scores)
    return emotion_scores

def translate_text(text, dest_language='es'):
    try:
        translated = GoogleTranslator(source='auto', target=dest_language).translate(text)
        logger.info("[Translation] Translated Text (%s): %s", dest_language, translated)
        return translated
    except Exception as e:
        logger.error("Translation failed: %s", str(e))
        return "[Error] Translation unavailable."

def generate_empathy_prompt(user_text):
    prompt = f"A person said: '{user_text}'. Write an empathetic response:"
    try:
        response = llm.invoke(prompt)
        logger.info("[Empathy Prompt] Empathy Response: %s", response.content)
        return response
    except Exception as e:
        logger.error("Empathy prompt generation failed: %s", str(e))
        return "[Error] Unable to generate empathy response."

if __name__ == "__main__":
    audio_file = "audio_input.wav"
    
    # Step 1: Speech-to-Text Processing
    text = speech_to_text(audio_file)
    
    if text and not text.startswith("[Error]"):
        # Step 2: Emotion Detection (Multi-Label + Weighted)
        emotion_scores = get_weighted_emotions(text)
        
        # Step 3: Translation (English → Spanish)
        translated_text = translate_text(text, dest_language='es')
        
        # Step 4: Empathy Prompt Generation
        empathy_response = generate_empathy_prompt(text)
        
        # Debug Outputs (Final Results)
        print(f"Transcribed Text: {text}")
        print(f"Emotion Scores: {emotion_scores}")
        print(f"Translated Text: {translated_text}")
        print(f"Empathy Response: {empathy_response}")
    else:
        logger.info("No valid text to process further.")
