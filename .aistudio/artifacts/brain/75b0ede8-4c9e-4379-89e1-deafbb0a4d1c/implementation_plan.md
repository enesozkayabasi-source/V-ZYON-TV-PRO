# VİZYON TV - v2.9.0 Güncellemesi & GitHub & Doğrudan APK İndirme Bağlantısı

## 1. Karnaval Grubu 10sn Donma/Durma Probleminin Kalıcı Çözümü
- **Kök Neden:** 
  StreamTheWorld MediaGateway CDN sunucuları masaüstü User-Agent tespit ettiğinde veya `Icy-MetaData: 1` gönderildiğinde akışı ~8-10 saniye (32 KB) sonra sonlandırmaktaydı.
- **Uygulanan Çözüm:**
  - `RadioPlaybackService.kt` içerisinde yerel mobil radyo istemcisi User-Agent (`Karnaval/6.0 (Android; Mobile; rv:125.0) ExoPlayerLib/2.19.1`), `Icy-MetaData: 0` ve `Connection: keep-alive` başlıkları uygulandı.
  - Canlı radyo oynatımı için düşük gecikmeli tamponlama (`minBufferMs: 4000`, `bufferForPlaybackMs: 500`) yapılandırıldı.
  - Süper FM, Metro FM, Joy FM, Joy Türk ve Virgin Radio için çoklu yedek CDN akış zincirleri entegre edildi.

## 2. GitHub Sayfası ve Doğrudan APK İndirme Bağlantıları (V-ZYON-TV-PRO)
- **Bağlantılar:**
  - GitHub Sayfası / Depo: `https://github.com/enesozkayabasi-source/V-ZYON-TV-PRO`
  - GitHub Sürümler (Releases): `https://github.com/enesozkayabasi-source/V-ZYON-TV-PRO/releases`
  - Doğrudan APK İndirme Bağlantısı: `https://github.com/enesozkayabasi-source/V-ZYON-TV-PRO/releases/latest/download/vizyontv.apk`
- **404 Hatası Koruması (Smart Safe URL Resolver):**
  - GitHub üzerinde henüz v2.9.0 Release'i yayınlanmadığında doğrudan indirme bağlantısına basıldığında tarayıcıda 404 "Not Found" sayfası çıkması engellendi.
  - Arka planda `HEAD` kontrolü yapılarak bağlantı 404 verdiğinde kullanıcıya bilgi verilir ve otomatik olarak çalışan GitHub deposu ana sayfasına (`HTTP 200`) yönlendirilir.
  - Proje ana dizinine `vizyontv.apk` dosyası yerleştirildi. Release yayınlandığında doğrudan indirme kesintisiz çalışır.
- **Ana Ekran Güncelleme Kartı (Banner):**
  - **"APK İndir"** butonu: Tıklandığında doğrudan APK dosyasının indirilmesini tetikler.
  - **"GitHub"** butonu: Tıklandığında kullanıcıyı projenin GitHub releases/proje sayfasına yönlendirir.
- **Üst Bar Güncelleme Butonu:**
  - Tıklandığında doğrudan son sürüm APK indirme linkini başlatır.
- **Ayarlar Menüsü (SettingsDialog):**
  - **Doğrudan APK İndir:** Tek tıkla APK dosyasını indirir.
  - **GitHub Sayfası:** GitHub sürümler sayfasını açar.
  - **Bağlantıları Özelleştirme:** Kullanıcının kendi GitHub deposunu veya özel APK bağlantısını girip kaydetmesine imkan tanıyan genişletilebilir ayar paneli. Girilen bağlantılar `UserPreferencesManager` üzerinden SharedPreferences'a kalıcı olarak kaydedilir.

## 3. Versiyon Bilgisi
- `versionCode = 290`
- `versionName = "2.9.0"`
- Temiz ve hatasız derleme sağlandı.
