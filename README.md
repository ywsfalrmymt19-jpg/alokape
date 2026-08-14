# 📊 تطبيق العكابي المحاسبي (Al-Okabi Accounting App)

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20(Material%203)-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Database](https://img.shields.io/badge/Local%20DB-Room-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Build](https://github.com/ywsfalrmymt19-jpg/alokape/actions/workflows/android_build.yml/badge.svg)](https://github.com/ywsfalrmymt19-jpg/alokape/actions)

تطبيق محاسبي احترافي لإدارة الإيرادات، ساعات العمل، والمصروفات اليومية واستخراج التقارير المالية الدورية مع ميزة تصدير وطباعة تقارير PDF.

---

## 👨‍💻 بيانات التطوير والبرمجة
- **المطور:** د/ مالك الهيمة
- **رقم التواصل / واتساب:** `771134103`
- **المستخدم المعتمد:** يوسف

---

## 🌟 المميزات الرئيسية (Core Features)

### 1. 📈 قسم الدخل اليومي (Daily Income)
- تسجيل **إجمالي عدد الساعات** المنجزة مع دعم الكسور العشرية بدقة.
- تحديد **نوع العمل** (مقاولات، نقليات، أعمال حرة، إلخ) مع إمكانية إضافة تصنيفات مخصصة.
- إدخال **المبلغ الإجمالي (ريال)** والتاريخ مع إضافة الملاحظات والبيان.
- عرض فوري لإجمالي الدخل اليومي وسجل القيود المحفوظة.

### 2. 📉 قسم الخرجيات والمصاريف (Daily Expenses)
- تسجيل بنود المصروفات المتنوعة (ديزل/وقود، زيوت، صيانة، طعام ومصروف، أجور عمال، إلخ).
- تسجيل مبالغ المصروفات اليومية بدقة.
- عرض فوري لإجمالي المصروفات اليومية وسجل تفصيلي.

### 3. 📊 التقارير المالية وتصدير PDF (Financial Reports)
- **٣ فترات تقارير مخصصة:**
  - **اسبوعي:** تقرير لآخر 7 أيام.
  - **شهري:** تقرير الشهر الحالي بالتفصيل.
  - **سنوي:** تقرير السنة المالية الكاملة.
- ملخص الإحصائيات المالية المباشرة:
  - 🟢 **إجمالي الدخل**
  - ⏱️ **إجمالي ساعات العمل**
  - 🔴 **إجمالي المصاريف والخرجيات**
  - 💎 **الصافي (الراسب)** = إجمالي الدخل - إجمالي المصاريف
- **تصدير وطباعة PDF:** توليد مستند PDF جاهز للطباعة والمشاركة الفورية متضمناً جدول الحسابات بالكامل.

### 4. 🔒 التخزين المحلي الآمن والأداء
- قاعدة بيانات محلية مدمجة **(Room Database / SQLite)** بدون الحاجة لاتصال بالإنترنت.
- تصميم عصري يدعم اللغة العربية واتجاه اليمين إلى اليسار (**RTL**) بنظام ألوان Emerald Gold المميز.

---

## 🛠️ التقنيات المستخدمة (Tech Stack)

- **لغة البرمجة:** Kotlin
- **واجهة المستخدم:** Jetpack Compose (Material Design 3)
- **معمارية التطبيق:** MVVM (Model-View-ViewModel) + StateFlow & Coroutines
- **قاعدة البيانات:** Android Room Database (KSP)
- **مستندات وتقارير:** Android Native PDF Document Generator
- **التكامل المستمر:** GitHub Actions CI/CD للبناء والتوقيع التلقائي لملفات APK.

---

## 🚀 البناء والتشغيل التلقائي (GitHub Actions Build)

المشروع يحتوي على ملف سير عمل مؤتمت في `.github/workflows/android_build.yml`:
1. عند عمل **Push** لأي تحديث، يتم بناء المشروع آلياً.
2. تتوفر نسختان من التطبيق في قسم **Artifacts** في تبويب **Actions**:
   - `AlOkabi-Accounting-Release-APK` (النسخة الموقعة الجاهزة).
   - `AlOkabi-Accounting-Debug-APK` (النسخة التجريبية).

---

## 📱 صور وشاشات التطبيق

| الواجهة الرئيسية | الدخل اليومي | الخرجيات | التقارير المالية |
| :---: | :---: | :---: | :---: |
| ![Home](https://via.placeholder.com/200x400.png?text=لوحة+التحكم) | ![Income](https://via.placeholder.com/200x400.png?text=الدخل+اليومي) | ![Expenses](https://via.placeholder.com/200x400.png?text=الخرجيات) | ![Reports](https://via.placeholder.com/200x400.png?text=التقارير+وPDF) |

---

## 📄 الترخيص
تم تطوير هذا التطبيق خصيصاً لإدارة أعمال وحسابات العكابي. جميع الحقوق محفوظة © 2026.
