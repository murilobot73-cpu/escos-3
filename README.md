# Ecos do Vazio — mod de terror psicológico (Fabric, Minecraft 26.3)

Nada aqui quer te matar. O medo vem de **sanidade**, **paranoia** e de coisas que
não deveriam estar ali. Todos os sons e mensagens são enviados **só para o jogador-alvo**
(em multiplayer, os outros não percebem nada).

## Sistema de sanidade (0–100)
- **Cai:** no escuro (luz ≤ 4, inclui a noite ao ar livre), no subsolo, no Nether/End.
- **Sobe:** com luz forte, sol, dormindo, perto de outro jogador.
- Quanto menor, mais frequentes e mais pesados os eventos.
- Os primeiros 3 minutos de cada sessão são "de graça". Tudo ajustável em `Config.java`.

## Criaturas (todas novas, invulneráveis, sem dano)
| Criatura | Comportamento |
|---|---|
| **O Observador** | Figura alta e imóvel, ao longe, virada para você. Some se você o encara ou chega perto. A cada aparição surge mais perto. |
| **A Sombra** | Só anda quando você NÃO olha. Se olha, congela e fica te encarando. Ao tocar, apaga sua visão e arranca sanidade. |
| **A Criança** | De costas no escuro. Gira devagar quando você desvia o olhar. Se você a flagra virada para você, ela some rindo. |
| **O Eco** | Invisível por 8 s, depois refaz exatamente o seu caminho. Se você o encara, ele olha de volta. Se te alcança, se funde em você. |
| **O Hospedeiro** | O final. Gigante, lento, NÃO para quando você olha. Coração cada vez mais alto. Ao te alcançar: "ACORDE". |

## Eventos (14)
`passos` `sons` `batidas` `porta` `chat` `sino` `tochas` `observador` `crianca` `apagao` `eco` `sombra` `saiu` `visita`

Destaques: mensagens no chat com **o seu nome**; o jogo diz que você *saiu* e *entrou*,
e "você" responde *"não era eu."*; portas abrindo sozinhas; tochas sumindo; passos no
mesmo tipo de chão em que você pisa; sino distante.

## Comandos (afetam só quem digita)
- `/horror sanidade` / `/horror sanidade 20`
- `/horror evento <nome>` — dispara na hora (ótimo para testar; autocompleta)
- `/horror limpar`

## Como compilar
1. Instale **JDK 25**.
2. Em https://fabricmc.net/develop gere o template para **26.3** e anote `loader_version`,
   `loom_version` e `fabric_api_version`; coloque em `gradle.properties`
   (ou copie `build.gradle`/`gradle.properties` do template por cima destes).
3. `./gradlew build` → o `.jar` sai em `build/libs/`.
4. Instale Fabric Loader + Fabric API e coloque o jar em `mods/`.

## Se der erro de compilação (a API muda a cada versão)
- `Identifier` não existe → o nome da classe de ids mudou; troque pelo nome atual (`ResourceLocation`).
- `scale(...)` / `createRenderState()` no `HorrorRenderer` → ajuste à assinatura atual de `MobRenderer`.
- `EntityType.Builder.build(chave)` → verifique se ainda recebe `ResourceKey`.

## Jeito mais fácil: deixar o GitHub compilar
1. Crie uma conta e um repositório novo em github.com.
2. Envie o CONTEÚDO desta pasta (arrastar para a página do repositório funciona).
   A pasta oculta `.github` precisa ir junto.
3. Aba **Actions** > "Compilar mod" > espere ficar verde.
4. Abra a execução, baixe o arquivo **ecos_do_vazio-jar** (vem num .zip), extraia e copie o `.jar` para `mods/`.
