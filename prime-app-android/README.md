# PRIME+ — App Android

Casca WebView que abre o PRIME+ hospedado no domínio de produção.
Todo o backend continua no servidor; o app só exibe a interface.

## Antes do primeiro build

Edite `app/src/main/res/values/strings.xml` e troque `app_url` pelo domínio
real (precisa ser HTTPS). É o único arquivo que precisa mudar.

## Gerar o APK

O build roda sozinho a cada push (ou pelo botão *Run workflow* na aba **Actions**).
Quando ficar verde: abra o build → **Artifacts** → baixe **prime-apk**.
Dentro do zip está o `app-release.apk`.

## Instalar no celular

Envie o `.apk` por WhatsApp/Drive. No Android, autorize "instalar de fontes
desconhecidas" quando ele pedir. Pronto.

## Importante

Compile **sempre pelo GitHub Actions**. O release é assinado com a keystore de
debug, que é gerada por ambiente — se um build sair do Android Studio e outro
do Actions, a assinatura muda e o Android recusa a atualização (todos teriam
que desinstalar antes de reinstalar).

Mudanças no sistema **não** exigem APK novo: como é WebView, todo deploy no
domínio já chega aos usuários. Só recompile se mudar ícone, nome ou URL.
