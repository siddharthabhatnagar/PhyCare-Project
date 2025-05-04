from flask import Flask, request, jsonify, send_from_directory
from werkzeug.utils import secure_filename
import os
import logging
import speech_recognition as sr
from textblob import TextBlob
from deep_translator import GoogleTranslator
from langchain_groq import ChatGroq

# ─── Flask App Setup ───────────────────────────────────────────────────────────
app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'uploads'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16MB

if not os.path.exists(app.config['UPLOAD_FOLDER']):
    os.makedirs(app.config['UPLOAD_FOLDER'])

# ─── Logging ────────────────────────────────────────────────────────────────────
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# ─── GROQ Setup ─────────────────────────────────────────────────────────────────
GROQ_API_KEY = os.getenv("GROQ_API_KEY", "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")
if not GROQ_API_KEY:
    logger.warning("GROQ_API_KEY not set. LangChain Groq may not work properly.")
os.environ["GROQ_API_KEY"] = GROQ_API_KEY

llm = ChatGroq(model="llama-3.1-8b-instant", temperature=0, groq_api_key=GROQ_API_KEY)

# ─── Utility Functions ─────────────────────────────────────────────────────────

def speech_to_text(audio_file, language='en-IN'):
    r = sr.Recognizer()
    with sr.AudioFile(audio_file) as source:
        audio_data = r.record(source)
        try:
            text = r.recognize_google(audio_data, language=language)
            logger.info("[Speech-to-Text] Transcribed Text: %s", text)
            return text
        except sr.UnknownValueError:
            logger.error("Could not understand audio")
            return ""
        except sr.RequestError:
            logger.error("API request failed")
            return ""

def detect_emotion(text):
    blob = TextBlob(text)
    sentiment = blob.sentiment.polarity
    emotion = "Positive" if sentiment > 0 else "Negative" if sentiment < 0 else "Neutral"
    logger.info("[Emotion Detection] Detected Emotion: %s", emotion)
    return emotion

def translate_text(text, dest_language='es'):
    try:
        translated = GoogleTranslator(source='auto', target=dest_language).translate(text)
        logger.info("[Translation] Translated Text (%s): %s", dest_language, translated)
        return translated
    except Exception as e:
        logger.error("Translation failed: %s", str(e))
        return ""

def generate_empathy_prompt(user_text):
    prompt = f"A person said: '{user_text}'. Write an empathetic response:"
    try:
        response = llm.invoke(prompt)
        logger.info("[Empathy Prompt] Empathy Response: %s", response.content)
        return response
    except Exception as e:
        logger.error("Empathy prompt generation failed: %s", str(e))
        return None

# ─── Routes ─────────────────────────────────────────────────────────────────────

@app.route('/')
def index():
    try:
        return send_from_directory('.', 'index.html')
    except Exception:
        return "Welcome to the Audio/Text Emotion App!"

@app.route('/upload-audio', methods=['POST'])
def upload_audio():
    try:
        if 'audio' not in request.files:
            app.logger.error("No audio file part in request")
            return jsonify({'error': 'No audio file part'}), 400
        file = request.files['audio']
        if file.filename == '':
            app.logger.error("No selected file in request")
            return jsonify({'error': 'No selected file'}), 400

        if not (file.filename.lower().endswith('.wav') or file.filename.lower().endswith('.webm')):
            app.logger.error("Unsupported file type uploaded: %s", file.filename)
            return jsonify({'error': 'Only WAV and WEBM audio files are supported'}), 400

        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

        language = request.form.get('language', 'en-IN')
        try:
            transcription = speech_to_text(filepath, language=language)
        except Exception as e:
            app.logger.error(f"Error in speech_to_text: {str(e)}")
            transcription = ""

        emotion = detect_emotion(transcription) if transcription else "No transcription"
        llm_response = ""
        if transcription:
            try:
                empathy_response = generate_empathy_prompt(transcription)
                llm_response = empathy_response.content if empathy_response else ""
            except Exception as e:
                app.logger.error(f"Error in generate_empathy_prompt: {str(e)}")
                llm_response = ""

        os.remove(filepath)

        return jsonify({
            'transcription': transcription,
            'emotion': emotion,
            'llm_response': llm_response
        })

    except Exception as e:
        app.logger.error(f"Unexpected error in upload_audio: {str(e)}")
        return jsonify({'error': f'Unexpected error: {str(e)}'}), 500

@app.route('/upload_text', methods=['POST'])
def upload_text():
    try:
        if 'text' not in request.files:
            app.logger.error("No text file part in request")
            return jsonify({'error': 'No text file part'}), 400

        file = request.files['text']
        if file.filename == '':
            app.logger.error("No selected text file")
            return jsonify({'error': 'No selected file'}), 400

        if not file.filename.lower().endswith('.txt'):
            app.logger.error("Unsupported file type uploaded: %s", file.filename)
            return jsonify({'error': 'Only TXT files are supported'}), 400

        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

        try:
            with open(filepath, 'r', encoding='utf-8') as f:
                text_content = f.read()
        except Exception as e:
            app.logger.error(f"Error reading file: {str(e)}")
            return jsonify({'error': 'Failed to read text file'}), 500

        emotion = detect_emotion(text_content) if text_content else "No content"
        llm_response = ""
        if text_content:
            try:
                empathy_response = generate_empathy_prompt(text_content)
                llm_response = empathy_response.content if empathy_response else ""
            except Exception as e:
                app.logger.error(f"Error in generate_empathy_prompt: {str(e)}")
                llm_response = ""

        os.remove(filepath)

        return jsonify({
            'text': text_content,
            'emotion': emotion,
            'llm_response': llm_response
        })

    except Exception as e:
        app.logger.error(f"Unexpected error in upload_text: {str(e)}")
        return jsonify({'error': f'Unexpected error: {str(e)}'}), 500

# ─── Run Server ─────────────────────────────────────────────────────────────────
if __name__ == '__main__':
    app.run(debug=True, port=8000)
