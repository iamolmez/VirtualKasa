# VirtualKasa Plugini

Paper 1.20.4 için geliştirilmiş sanal kasa (ekonomi) plugini.

## Özellikler

- ✅ SQLite ve MySQL veritabanı desteği
- ✅ Temel ekonomi işlemleri (bakiye, yatır, çek, transfer)
- ✅ Admin komutları
- ✅ PlaceholderAPI entegrasyonu
- ✅ Türkçe dil desteği
- ✅ Tab completion
- ✅ İşlem kayıtları
- ✅ Konfigürasyon sistemi

## Kurulum

1. Plugin jar dosyasını sunucunuzun `plugins` klasörüne atın
2. Sunucuyu yeniden başlatın
3. `plugins/VirtualKasa/config.yml` dosyasından ayarları düzenleyin

## Komutlar

### Oyuncu Komutları

- `/kasa` - Bakiyenizi gösterir
- `/kasa para` - Bakiyenizi gösterir
- `/kasa yatır <miktar>` - Hesabınıza para yatırır
- `/kasa çek <miktar>` - Hesabınızdan para çeker
- `/kasa transfer <oyuncu> <miktar>` - Başka oyuncuya para transfer eder
- `/kasa bilgi` - Sistem bilgisi gösterir
- `/kasa yardım` - Yardım menüsünü gösterir

### Admin Komutları

- `/adminkasa ver <oyuncu> <miktar>` - Oyuncuya para verir
- `/adminkasa al <oyuncu> <miktar>` - Oyuncudan para alır
- `/adminkasa ayarla <oyuncu> <miktar>` - Oyuncunun bakiyesini ayarlar
- `/adminkasa sıfırla <oyuncu>` - Oyuncunun bakiyesini sıfırlar
- `/adminkasa bak <oyuncu>` - Oyuncunun bakiyesini gösterir
- `/adminkasa top <sayı>` - En zengin oyuncuları listeler

## İzinler

- `virtualkasa.admin` - Tüm admin komutlarını kullanma
- `virtualkasa.transfer` - Para transferi yapma
- `virtualkasa.withdraw` - Para çekme
- `virtualkasa.deposit` - Para yatırma

## PlaceholderAPI

Eğer PlaceholderAPI kurulu ise şu placeholder'ları kullanabilirsiniz:

- `%virtualkasa_balance%` - Oyuncunun bakiyesi (formatlı)
- `%virtualkasa_balance_raw%` - Oyuncunun bakiyesi (sayı olarak)
- `%virtualkasa_currency%` - Para birimi sembolü
- `%virtualkasa_max_balance%` - Maksimum bakiye
- `%virtualkasa_starting_balance%` - Başlangıç bakiyesi

## Veritabanı Ayarları

### SQLite (Varsayılan)
```yaml
database:
  type: sqlite
  sqlite:
    file: database.db
```

### MySQL
```yaml
database:
  type: mysql
  mysql:
    host: localhost
    port: 3306
    database: virtualkasa
    username: root
    password: "sifre"
```

## Derleme

Plugini derlemek için:

```bash
mvn clean package
```

Derlenmiş jar dosyası `target/` klasöründe bulunacaktır.

## Gereksinimler

- Minecraft Paper 1.20.4+
- Java 17+
- Maven (derleme için)

## Lisans

Bu plugin InfinityMC için özel olarak geliştirilmiştir.
