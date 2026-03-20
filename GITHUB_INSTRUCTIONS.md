# GitHub Yükleme Adımları

## 1. GitHub Repository Oluşturun
1. [github.com](https://github.com) adresine gidin
2. "New repository" tıklayın
3. Repository adı: "VirtualKasa"
4. Public seçin
5. "Create repository" tıklayın

## 2. Kodları Yükleyin
Repository oluşturduktan sonra GitHub size komutları gösterecek. Aşağıdaki komutları kullanın (repository URL'nizi ekleyin):

```powershell
git remote add origin https://github.com/KULLANICI_ADINIZ/VirtualKasa.git
git branch -M main
git push -u origin main
```

## 3. Otomatik Derleme
Kodlar yüklendikten sonra:
1. GitHub repository'nize gidin
2. "Actions" sekmesine tıklayın
3. "Build Plugin" workflow'unun çalıştığını görün
4. Derleme bittiğinde "Artifacts" bölümünden VirtualKasa.jar dosyasını indirin

## 4. Plugini Kullanın
İndirdiğiniz VirtualKasa.jar dosyasını sunucunuzun plugins klasörüne atın ve sunucuyu yeniden başlatın!

## Alternatif: Manuel Derleme
Eğer GitHub kullanmak istemiyorsanız:
1. IntelliJ IDEA Community Edition indirin
2. Projeyi açın
3. Sağ taraftaki Maven panelinden "clean package" tıklayın
