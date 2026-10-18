# App Android — João Barbosa (v0.1.0-teste)

App multilíngue (PT/FR/ES) com o conteúdo de joaobarbosa.pt e joaobarbosa.es.
A leitura de *Famílias de Dinheiro* só abre depois de comprar o **Kit Leitor**
(neste momento em **modo de teste**: a compra é simulada e não cobra nada).

## Gerar o APK

**Opção A — GitHub (sem instalar nada):** cria um repositório, faz push desta pasta.
O workflow `.github/workflows/build-apk.yml` compila sozinho. Em *Actions → Build APK →
Artifacts* descarregas `app-debug.apk`.

**Opção B — Android Studio:** *File → Open* nesta pasta. Se pedir Gradle wrapper,
aceita (ou corre `gradle wrapper --gradle-version 8.9`). Depois *Build → Build APK(s)*.

**Opção C — linha de comando:** JDK 17 + Android SDK (platform 34) + Gradle 8.9:
`gradle :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`

## Como funciona o bloqueio (modo teste)

- Separador **Ler** mostra o livro selado até haver um código válido.
- **Kit → Pagar (teste)** gera uma encomenda fictícia e um código `JB-XXXX-XXXX`,
  que desbloqueia de imediato.
- **Tenho um código** aceita qualquer código `JB-` com dígito de controlo válido,
  e também `TESTE-2026` (só enquanto `TEST_MODE = true`).
- Ligação "Repor app" no fundo do separador Kit apaga o desbloqueio para voltar a testar.

## Onde mexer

| O quê | Ficheiro |
|---|---|
| Textos da interface | `app/src/main/assets/i18n/<língua>.json` |
| Texto do livro | `app/src/main/assets/book/<língua>.json` |
| Lista de línguas | `index.html` → `LANGS` e `BOOK_LANGS` |
| Preço / conteúdo do kit | `index.html` → secção `v-kit` |
| Modo de teste | `app/build.gradle.kts` → `TEST_MODE` |
| Códigos | `KitCode.kt` |

## Línguas

Interface em PT, FR e ES; livro em PT e ES. A língua inicial é a do telemóvel,
se existir na lista, senão PT. A escolha manual fica guardada.

Para acrescentar uma língua (ex.: inglês):
1. Copiar `i18n/pt.json` para `i18n/en.json` e traduzir os valores (não as chaves).
2. Em `index.html`, juntar `{ code:"en", name:"English" }` a `LANGS`.
3. Opcional: criar `book/en.json` e juntar `"en"` a `BOOK_LANGS`.

Chaves em falta caem para PT, por isso uma tradução parcial não parte nada.
Sem livro traduzido, o leitor mostra a versão PT com um aviso na língua escolhida.
Com mais de 4 línguas, os botões passam automaticamente a lista suspensa.

## Antes de passar a produção (importante)

O bloqueio atual é 100% local, portanto **contornável**: o texto do livro vai dentro
do APK (`assets/`) e o estado de desbloqueio está em SharedPreferences. Serve para
validar o fluxo, não para proteger o livro.

1. **Pagamento real:** o kit é físico, por isso pode ser vendido pela loja WooCommerce
   já existente. A encomenda paga gera o código (plugin/webhook) que vai impresso no kit
   ou por email.
2. **Validação no servidor:** a app envia o código para um endpoint
   (ex.: `POST /wp-json/jb/v1/redeem`), que o marca como usado/associado a uma conta
   e devolve um token.
3. **Livro fora do APK:** o texto só é descarregado depois de o servidor validar o token
   (idealmente por capítulo). Opcional: Play Integrity API para dificultar clientes adulterados.
4. **Google Play:** vender acesso a conteúdo digital *dentro* da app obriga ao Google Play
   Billing. O modelo "compra-se um produto físico e o código vem junto" é a forma mais
   simples de ficar dentro das regras — confirmar com a política em vigor antes de publicar.
5. Gerar keystore própria e retirar `signingConfig = debug` do release.
