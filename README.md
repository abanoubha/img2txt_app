# IMG2TXT

A free opensource Android app to extract text from images using OCR technology.

- works offline (without Internet access)
- works on all versions of Android; from Android 6 up to Android 14 (the latest version release)
- get image from __gallery__ or from __Camera__ for extracting text
- crop image before OCR processing
- share image into IMG2TXT app to extract text from it
- app UI supports Arabic & English
- app supports extracting Arabic & English languages
- app supports extracting Arabic+English text from the same image
- Use three OCR engines (ML Kit -> Google Vision -> Tesseract OCR)
- color-coded confidence/accuracy extracted text
  - +80% -> white/black
  - 50-79% -> purple
  - -50% -> red
- let the user choose the text language before processing
- show the newlines in the result text
- ability to edit extracted text
- ability to copy extracted text

Install the img2txt app from Google Play:
<https://play.google.com/store/apps/details?id=com.softwarepharaoh.img2txt>

## documented fixes

Google Play : "This App Bundle contains native code, and you've not uploaded debug symbols. We recommend you upload a symbol file to make your crashes and ANRs easier to analyze and debug. Learn More". I documented [the fix on my tech blog here](https://abanoubhanna.com/posts/upload-symbol-file-google-play-publisher/).

## Roadmap | timeline of release versions with tasks

- Old Codebase
  - use onActivityResult (modern code)
  - use general/universal openUrl() method/function
  - better way of cropping images
  - share image into IMG2TXT app to extract text from it
  - translations (English & Arabic), default is English
  - About Me
  - ~~rate us on Google Play~~ (removed)
  - link to Google Play
- v2.5.1
  - first opensource version release is v2.5.1 on April 21st, 2024
  - support Android 6 (Marshmallow) (SDK 23)
  - support Android 7
  - support Android 8
  - support Android 9
  - support Android 10
  - support Android 11
  - support Android 12
  - support Android 13
- [v2.6.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.6.0)
  - support Android 14 (SDK 34)
  - show thresholded/cleaned image (created by Tesseract)
- [v2.7.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.7.0)
  - release v2.7.0 on May 29th, 2024
  - show bounding rectangles/boxes around each recognized word
- [v2.8.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.8.0)
  - image to text OCR app release v2.8.0 on May 31st, 2024 with enhanced user experience and bug fixes.
  - simplify app UI layout
  - new simpler theme
  - show alert/notice if the mean_confidence of result text is less than 60%
  - on-device Tesseract OCR (English & Arabic models)
  - on-device Google vision API (latin scripted languages)
  - on-device ML Kit (latin scripted languages)
  - in case of Arabic language or both (Arabic+English), use Tesseract OCR
  - __fallback strategy__ in case of English language is ML Kit -> Google Vision -> TesseractOCR
  - color-coded confidence/accuracy of the result text from ML Kit & Tesseract OCR
  - prompt the app user to choose the language of text on the image before processing it
  - customize crop-screen
  - make result/extracted text editable
  - add line breaks (newline) in extracted text (ML Kit & Tesseract OCR)
- [v2.9.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.9.0)
  - support Android 15 (SDK 35)
  - save OCR history (aka : Detailed scanned images history)
- v2.10.0
  - fix: make sure OCR history is saved in local db
- Next Version
  - rewrite app in Jetpack Compose
  - batch processing (in bulk)
  - PDF -> Images.foreach(ocr)
  - expose more functions to Java : cpp files in `tesseract4android/src/main/cpp/tesseract` and java files in `tesseract4android/src/main/java/com/googlecode/tesseract/android` must be modified/added.
  - support Hindi ( Indian language )
  - support Farsi ( Persian language )
  - save extracted text as PDF
  - choose more than one image to OCR
  - pre-process image with thresholding for more clarity and better results/accuracy/extracted text
  - Ability to edit image before/after running OCR on it (manual)

##  Resources & references

- [Pre-Recognize Library](https://github.com/leha-bot/PRLib) - library with algorithms for improving OCR quality.
