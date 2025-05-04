# Installing FFmpeg on Windows

FFmpeg is required for audio format conversion in the backend.

## Steps to install FFmpeg on Windows:

1. Download FFmpeg:
   - Go to https://ffmpeg.org/download.html
   - Click on Windows builds by gyan.dev or BtbN
   - Download the latest release zip file (e.g., ffmpeg-release-essentials.zip)

2. Extract the zip file to a folder, e.g., `C:\ffmpeg`

3. Add FFmpeg to system PATH:
   - Open Start Menu, search "Environment Variables"
   - Click "Edit the system environment variables"
   - Click "Environment Variables" button
   - Under "System variables", find and select "Path", then click "Edit"
   - Click "New" and add the path to the `bin` folder inside FFmpeg folder, e.g., `C:\ffmpeg\bin`
   - Click OK to close all dialogs

4. Verify installation:
   - Open Command Prompt and run:
     ```
     ffmpeg -version
     ```
   - You should see FFmpeg version info if installed correctly.

## After installation

Restart your terminal or IDE to pick up the new PATH.

The backend will then be able to convert WEBM audio files to WAV for processing.

---

If you want, I can update the backend code to enable audio conversion using pydub and ffmpeg.
