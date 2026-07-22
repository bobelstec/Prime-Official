# Como gerar o APK do PRIME+

Este projeto é uma **casca Android (WebView)** que abre o seu sistema hospedado
no seu domínio de produção. O backend continua no servidor — o app só exibe a
interface, igual ao navegador, mas com cara de aplicativo (ícone na tela inicial,
tela cheia, pull-to-refresh, botão voltar, tela de "sem conexão").

---

## PASSO 0 — Configurar a URL (obrigatório, 30 segundos)

Abra o arquivo:

    app/src/main/res/values/strings.xml

Troque **apenas** a linha do `app_url` pelo seu domínio real (precisa ser HTTPS):

    <string name="app_url">https://app.suaempresa.com.br</string>

É o único lugar que precisa mudar. Não mexa em mais nada.

---

## Como compilar — escolha UMA das opções

### ✅ Opção A — GitHub Actions (recomendado, não instala NADA no seu PC)

Você sobe o projeto num repositório do GitHub e o próprio GitHub compila o APK
de graça na nuvem. No fim, baixa o arquivo pronto.

1. Crie um repositório novo no GitHub (pode ser privado).
2. Suba todos os arquivos desta pasta para o repositório.
3. O arquivo `.github/workflows/build-apk.yml` (já incluído) faz o resto.
4. No GitHub, vá em **Actions** → aguarde o build ficar verde (~3-5 min).
5. Clique no build → em **Artifacts**, baixe **prime-apk**.
6. Dentro do zip está o `app-release.apk`. Pronto para enviar por WhatsApp/link.

> Toda vez que quiser gerar uma versão nova, é só subir a mudança — o APK sai
> automaticamente.

---

### Opção B — Android Studio (no seu computador)

1. Baixe e instale o **Android Studio** (gratuito): https://developer.android.com/studio
2. Abra o Android Studio → **Open** → selecione esta pasta.
3. Espere o Gradle sincronizar (baixa o SDK sozinho na primeira vez).
4. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. Ao terminar, clique em **locate** — o APK está em:

    app/build/outputs/apk/release/app-release.apk

---

### Opção C — Linha de comando (se você já tem o Android SDK)

    # dentro desta pasta
    ./gradlew assembleRelease

O APK sai em `app/build/outputs/apk/release/app-release.apk`.

---

## Instalar no celular (distribuição direta)

1. Envie o `app-release.apk` por WhatsApp, Drive, ou link de download.
2. No Android, ao abrir o arquivo, o celular pede para permitir
   **"instalar apps de fontes desconhecidas"** — aceite (é normal para apps
   fora da Play Store).
3. Pronto, o PRIME+ aparece na tela inicial com ícone próprio.

---

## Perguntas comuns

**Precisa pagar a Play Store?**
Não. Distribuindo o APK direto (link/WhatsApp), não há custo nenhum. A taxa de
US$ 25 da Play Store só existe se um dia você quiser publicar lá.

**O app funciona offline?**
Não — ele depende do servidor (Firestore, distribuição de leads). Sem internet,
mostra a tela de "sem conexão". Isso é esperado para um sistema com backend.

**Como atualizo o app quando mudo o sistema?**
Na maioria das vezes você NÃO precisa gerar APK novo. Como o app carrega o site,
qualquer deploy no seu domínio já aparece para todos automaticamente. Só é preciso
gerar APK novo se mudar o ícone, o nome, ou a URL.

**O login do Firebase se mantém?**
Sim. Cookies e localStorage persistem entre aberturas do app.

**iPhone (iOS)?**
Este projeto é só Android. iOS exige conta de desenvolvedor Apple (US$ 99/ano) e
um Mac para compilar — caminho bem mais caro. No iPhone, a alternativa gratuita é
"Adicionar à Tela de Início" pelo Safari (vira um atalho parecido com app).
