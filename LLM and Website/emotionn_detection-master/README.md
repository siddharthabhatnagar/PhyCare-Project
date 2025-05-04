# Voice Emotion Detector

This is a web application that allows users to upload or record voice audio to detect emotions and receive empathetic responses. The application supports multi-language speech-to-text transcription, including English, Hindi, Japanese, Korean, Chinese, and various Indian languages.

## Features

- Upload audio files (WAV, WEBM) or record live audio.
- Speech-to-text transcription using Google Speech Recognition API with multi-language support.
- Emotion detection from transcribed text using sentiment analysis.
- Empathetic response generation using LangChain Groq LLM.
- Language selection for transcription to support various languages.
- User-friendly web interface with transcription display, detected emotion, and empathetic response.

## Technologies Used

- Python Flask for backend API.
- SpeechRecognition library for speech-to-text.
- TextBlob for sentiment analysis.
- Deep Translator for text translation.
- LangChain Groq for empathetic response generation.
- HTML, CSS, and JavaScript for frontend UI.
- Web Audio API for live audio recording and conversion.

## Installation

1. Clone the repository:

```bash
git clone <repository-url>
cd hackathon_tessarx
```

2. Create and activate a Python virtual environment (optional but recommended):

```bash
python -m venv venv
venv\Scripts\activate  # Windows
source venv/bin/activate  # macOS/Linux
```

3. Install the required Python packages:

```bash
pip install -r requirements.txt
```

4. Set the Groq API key as an environment variable (optional, default key is used if not set):

```bash
set GROQ_API_KEY=your_groq_api_key_here  # Windows
export GROQ_API_KEY=your_groq_api_key_here  # macOS/Linux
```

## Usage

1. Run the Flask application:

```bash
python app.py
```

2. Open your web browser and navigate to:

```
http://localhost:5000/
```

3. Use the web interface to:

- Upload an audio file or record live audio.
- Select the language of the audio from the dropdown.
- View the transcribed text, detected emotion, and empathetic response.

## Supported Languages

- English (India)
- Hindi
- Japanese
- Korean
- Chinese (Mandarin)
- Tamil
- Telugu
- Bengali
- Marathi
- Gujarati
- Punjabi
- Malayalam
- Odia
- Assamese

## Notes

- Only WAV and WEBM audio file formats are supported for upload.
- The application uses Google Speech Recognition API, which requires internet connectivity.
- The empathetic response is generated using LangChain Groq LLM and requires a valid API key.
- The maximum upload size is 16 MB.

## License

This project is licensed under the MIT License.

## Acknowledgments

- [SpeechRecognition](https://pypi.org/project/SpeechRecognition/)
- [TextBlob](https://textblob.readthedocs.io/en/dev/)
- [Deep Translator](https://github.com/nidhaloff/deep-translator)
- [LangChain Groq](https://github.com/groq-ai/langchain-groq)
- Inspired by voice emotion detection and empathetic AI research.
