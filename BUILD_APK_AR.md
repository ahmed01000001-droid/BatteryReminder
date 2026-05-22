# طريقة استخراج ملف APK

## الخيار 1: من Android Studio
1. افتح مجلد المشروع BatteryReminder في Android Studio.
2. انتظر اكتمال Gradle Sync.
3. من القائمة اختر: Build > Build Bundle(s) / APK(s) > Build APK(s).
4. بعد انتهاء البناء ستجد الملف هنا:
   app/build/outputs/apk/debug/app-debug.apk
5. انسخ الملف للجوال وثبته.

## الخيار 2: من GitHub Actions
1. ارفع مجلد المشروع إلى GitHub Repository.
2. افتح تبويب Actions.
3. شغل Workflow باسم: Build Android APK.
4. بعد انتهاء التشغيل، حمل Artifact باسم BatteryReminder-debug-apk.

## ملاحظة
نسخة debug مناسبة للتجربة الشخصية. للنشر في Google Play تحتاج نسخة release موقعة رسميًا.
