from flask import Flask, request, jsonify
from soul_link_demo import speech_to_text, detect_emotion, generate_empathy_prompt, get_weighted_emotions
from werkzeug.utils import secure_filename
import os
from pydub import AudioSegment

from flask import send_from_directory

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = 'uploads'
app.config['MAX_CONTENT_LENGTH'] = 16 * 1024 * 1024  # 16 MB max upload size

if not os.path.exists(app.config['UPLOAD_FOLDER']):
    os.makedirs(app.config['UPLOAD_FOLDER'])

@app.route('/')
def index():
    try:
        return send_from_directory('.', 'index.html')
    except Exception:
        return "Welcome to the Audio Processing App!"

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

        # Log file info
        app.logger.info(f"Received file: filename={file.filename}, content_type={file.content_type}")

        # Accept WAV and WEBM files
        if not (file.filename.lower().endswith('.wav') or file.filename.lower().endswith('.webm')):
            app.logger.error("Unsupported file type uploaded: %s", file.filename)
            return jsonify({'error': 'Only WAV and WEBM audio files are supported'}), 400

        filename = secure_filename(file.filename)
        filepath = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(filepath)

        # Convert WEBM to WAV if needed
        if filepath.lower().endswith('.webm'):
            wav_filename = os.path.splitext(filename)[0] + '.wav'
            wav_filepath = os.path.join(app.config['UPLOAD_FOLDER'], wav_filename)
            try:
                audio = AudioSegment.from_file(filepath)
                audio.export(wav_filepath, format='wav')
                converted_path = wav_filepath
                # Remove original webm file after conversion
                os.remove(filepath)
            except Exception as e:
                app.logger.error(f"Error converting WEBM to WAV: {str(e)}")
                return jsonify({'error': 'Failed to convert WEBM to WAV'}), 500
        else:
            converted_path = filepath

        # Get language parameter from form data or default to 'en-IN'
        language = request.form.get('language', 'en-IN')

        # Process the WAV audio file directly
        try:
            transcription = speech_to_text(converted_path, language=language)
        except Exception as e:
            app.logger.error(f"Error in speech_to_text: {str(e)}")
            transcription = ""

        emotion = detect_emotion(transcription) if transcription else "No transcription"
        weighted_emotions = {}
        llm_response = ""
        if transcription:
            try:
                weighted_emotions = get_weighted_emotions(transcription)
            except Exception as e:
                app.logger.error(f"Error in get_weighted_emotions: {str(e)}")
                weighted_emotions = {}

            try:
                empathy_response = generate_empathy_prompt(transcription)
                llm_response = empathy_response.content if empathy_response else ""
            except Exception as e:
                app.logger.error(f"Error in generate_empathy_prompt: {str(e)}")
                llm_response = ""

        # Delete temporary file after processing
        os.remove(converted_path)

        return jsonify({
            'transcription': transcription,
            'emotion': emotion,
            'weighted_emotions': weighted_emotions,
            'llm_response': llm_response
        })
    except Exception as e:
        app.logger.error(f"Unexpected error in upload_audio: {str(e)}")
        return jsonify({'error': f'Unexpected error: {str(e)}'}), 500

if __name__ == '__main__':
    app.run(debug=True)
