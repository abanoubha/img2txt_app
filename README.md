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
  - feature: add reward ad (to collect points)
  - feature: add coins count
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
- [using Tesseract in Python with OpenCV pre-processing](https://github.com/NanoNets/ocr-with-tesseract/blob/master/tesseract-tutorial.ipynb)

## code snippets

```java
public Bitmap convertGray(Bitmap bitmap3) {
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);

        Paint paint = new Paint();
        paint.setColorFilter(filter);
        Bitmap result = Bitmap.createBitmap(bitmap3.getWidth(), bitmap3.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        canvas.drawBitmap(bitmap3, 0, 0, paint);
        return result;
    }

   /**
     * Binarization
     *
     * @param tmp Binarization threshold Default 100
     */
    private Bitmap binaryzation(Bitmap bitmap22, int tmp) {
        // Get the width and height of the image
        int width = bitmap22.getWidth();
        int height = bitmap22.getHeight();
        // Create a binary image
        Bitmap bitmap;
        bitmap = bitmap22.copy(Bitmap.Config.ARGB_8888, true);
        // Traverse the original image pixels and perform binarization processing
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Get the current pixel value
                int pixel = bitmap.getPixel(i, j);
                // Get the value of the alpha channel
                int alpha = pixel & 0xFF000000;
                // Get the value of Red
                int red = (pixel & 0x00FF0000) >> 16;
                // Get the value of Green
                int green = (pixel & 0x0000FF00) >> 8;
                // Get the value of Blue
                int blue = pixel & 0x000000FF;

                if (red > tmp) {
                    red = 255;
                } else {
                    red = 0;
                }
                if (blue > tmp) {
                    blue = 255;
                } else {
                    blue = 0;
                }
                if (green > tmp) {
                    green = 255;
                } else {
                    green = 0;
                }

                // The optimal pixel value is calculated by weighted average algorithm.
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                // Set the image to black and white
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // Get the new pixel value
                int newPiexl = alpha | (gray << 16) | (gray << 8) | gray;
                // Assign pixels to the new image
                bitmap.setPixel(i, j, newPiexl);
            }
        }
        return bitmap;
    }

private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            final Bitmap bitmap_1 = convertGray(BitmapFactory.decodeFile(path));

            baseApi.setImage(bitmap_1);
            result = baseApi.getUTF8Text();
            baseApi.recycle();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView2.setImageBitmap(bitmap_1);
                    textView.setText(result);
                    dialog.dismiss();
                }
            });
        }
    };
```
