# IMG2TXT

A free opensource Android app to extract text from images using OCR technology.

- works offline (without Internet access)
- app size 42 MB
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

## documented fix

Google Play : "This App Bundle contains native code, and you've not uploaded debug symbols. We recommend you upload a symbol file to make your crashes and ANRs easier to analyze and debug. Learn More"

How to do that?

1 . in `app/build.gradle` add:

```
android {
   ...
    defaultConfig {
       ...
        ndk {
            debugSymbolLevel 'FULL'
        }
    }
}
```

2 . go to this path inside your project

```
cd app/build/intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib
```

3 . compress them all in one file

```
zip -r symbols.zip .
```

4 . upload the `symbols.zip` file into Google Play Console/release

## Tasks

- [x] use onActivityResult (modern code)
- [x] use general/universal openUrl() method/function
- [x] better way of cropping images
- [x] share image into IMG2TXT app to extract text from it
- [x] translations (English & Arabic), default is English
- [x] About Me
- [x] ~~rate us on Google Play~~ (removed)
- [x] link to Google Play
- [x] first opensource version release is v2.5.1 on April 21st, 2024
- [x] Android 6 (Marshmallow) (SDK 23)
- [x] Android 7
- [x] Android 8
- [x] Android 9
- [x] Android 10
- [x] Android 11
- [x] Android 12
- [x] Android 13
- [x] Android 14 (SDK 34) ([v2.6.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.6.0))
- [x] simplify app UI layout
- [x] new simpler theme
- [x] show alert/notice if the mean_confidence of result text is less than 60%
- [x] show thresholded/cleaned image (created by Tesseract)
- [x] show bounding rectangles/boxes around each recognized word
- [x] on-device Tesseract OCR (English & Arabic models)
- [x] on-device Google vision API (latin scripted languages)
- [x] on-device ML Kit (latin scripted languages)
- [x] in case of Arabic language or both (Arabic+English), use Tesseract OCR
- [x] __fallback strategy__ in case of English language is ML Kit -> Google Vision -> TesseractOCR
- [x] color-coded confidence/accuracy of the result text from ML Kit & Tesseract OCR
- [x] prompt the app user to choose the language of text on the image before processing it
- [x] add line breaks (newline) in extracted text (ML Kit & Tesseract OCR)
- [x] make result/extracted text editable
- [x] release [v2.7.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.7.0) on May 29th, 2024
- [x] customize crop-screen
- [x] image to text OCR app release [v2.8.0](https://github.com/abanoubha/img2txt_app/releases/tag/2.8.0) on May 31st, 2024 with enhanced user experience and bug fixes.

- [ ] save OCR history (aka : Detailed scanned images history) (WIP)

- [ ] batch processing (in bulk)
- [ ] PDF -> Images.foreach(ocr)
- [ ] expose more functions to Java : cpp files in `tesseract4android/src/main/cpp/tesseract` and java files in `tesseract4android/src/main/java/com/googlecode/tesseract/android` must be modified/added.
- [ ] support Hindi ( Indian language )
- [ ] support Farsi ( Persian language )
- [ ] save extracted text as PDF
- [ ] choose more than one image to OCR
- [ ] pre-process image with thresholding for more clarity and better results/accuracy/extracted text
- [ ] Ability to edit image before/after running OCR on it (manual)
