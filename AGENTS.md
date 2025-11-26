# 사용 언어

- 사고 과정은 상관 없으나, 최종 답변은 **한국어**로 작성하세요.
- 주석의 경우 기존의 스타일을 따르거나, 사용자의 프롬프트에서 지정된 언어를 따르세요.

# 프로젝트 개요

- 단국대학교 총동아리연합회 회칙을 그대로 근거로 삼아 Discord 멘션으로 들어오는 질문에 대해 LLM이 답변하는 회칙봇 프로젝트입니다.
- Java 21과 Spring Boot 3.5.7 기반이며, Spring AI의 OpenAI 채팅 클라이언트와 net.dv8tion.JDA 6.1.2를 결합해 Discord에서 멘션 리스너를 운영합니다.
- `src/main/resources/federation-rulebook-20250602.txt`에 저장된 회칙 전문과 RulebookPrompts의 시스템 메시지를 통해 회칙 범위와 보안 규칙을 강제하고, 스레드 히스토리를 모아 문맥을 구성합니다.
- `application.yml`은 `LLM_OPENAI_BASE_URL`, `LLM_API_KEY`, `LLM_COMPLETION_PATH`, `LLM_MODEL`, `DISCORD_TOKEN`, `DEBUG` 와 같은 환경 변수를 참조하여 OpenAI 및 Discord 인증을 설정하고, Spring AI 채팅 옵션과 JDA 인스턴스를 빈으로 제공합니다.

# 질문 모드

- 질문 모드는 사용자가 `/ask` 로 시작하는 프롬프트를 입력하면 활성화합니다.
- 질문 모드에서는 사용자의 프롬프트를 보고 필요한 경우 프로젝트의 파일을 탐색하며 풍부한 컨텍스트를 얻고 이를 바탕으로 질문에 답변합니다.
- 현재 approvals 상태와 관련 없이 그 어떤 경우에도 **프로젝트 파일을 수정하지 않습니다**.
- 코드에 변경 사항을 반영하고 싶은 경우, 수정하는 대신 반영 예시를 텍스트로 제시합니다.
- 프로젝트 파일에 변경을 가하지 않는 명령어의 경우에 제한적으로 실행할 수 있습니다.

# 계획 모드

- 계획 모드는 사용자가 `/plan` 으로 시작하는 프롬프트를 입력하면 활성화합니다.
- 계획 모드에서는 사용자의 프롬프트를 보고 **반드시** 프로젝트의 파일을 탐색하며 풍부한 컨텍스트를 얻고 이를 바탕으로 계획을 작성합니다.
- 본 계획은 `update_plan` 과 같은 내장 도구 사용, Todo-List 작성이 아닌 일반적인 텍스트로 계획을 출력하면 됩니다.

# 웹 검색

- 질문, 계획, 그 외의 모든 모드에서 본인이 보유한 지식으로 답변할 수 없는 경우나, 사용자가 요청한 경우 웹 검색을 수행합니다.
- 웹 검색은 내장된 도구를 사용하여 수행합니다.

# 디렉토리 구조 탐색

- tree 명령어를 사용하여 빠르게 디렉토리 구조를 파악하세요.
- 일반 코드는 다음 명령어를 통해 파일 목록을 탐색할 수 있습니다.
  `tree src/main/java/com/dku/project -L 3` (파일이 많으므로 깊이 3까지만 탐색)
- 테스트 코드는 다음 명령어를 통해 파일 목록을 탐색할 수 있습니다.
  `tree src/test/java/com/dku/project` (파일이 적으므로 전체 탐색)

# 코드 탐색

- 내장된 도구를 사용해도 되지만 ripgrep, ast-grep 등의 도구를 활용하여 코드를 탐색할 수 있습니다.

# 코드 작성 지침

- /Volumes/personal/development/project/aegis/aegis-server 폴더에 있는 코드를 참고하세요.
- 해당 프로젝트는 본인이 직접 작성하고 유지보수하는 오픈소스 프로젝트입니다.

---

<ast_grep_vs_ripgrep>

# ast-grep 대 ripgrep (빠른 가이드)

**구조가 중요할 때는 `ast-grep`을 사용하세요.** 코드를 파싱하고 AST(추상 구문 트리) 노드를 매칭하므로, 결과에서 주석이나 문자열은 무시되며 문법을 이해하고 코드를 **안전하게 재작성(safely
rewrite)** 할 수 있습니다.

- **리팩토링/코드모드:** 함수 시그니처 변경, 패키지 마이그레이션(예: `ioutil` -\> `io`), 에러 처리 패턴 변경.
- **정책 검사:** 저장소 전반에 걸쳐 패턴 강제 (예: `panic` 사용 금지, 특정 구조체 필드 필수 확인).
- **에디터/자동화:** LSP 모드 지원; 도구 연동을 위한 `--json` 출력.

**텍스트로 충분할 때는 `ripgrep`을 사용하세요.** 여러 파일에 걸쳐 리터럴이나 정규표현식(regex)을 grep 하는 가장 빠른 방법입니다.

- **탐색(Recon):** 하드코딩된 문자열, TODO, 로그 라인, 설정 파일(YAML/JSON) 값 찾기.
- **사전 필터링:** 정밀한 작업을 수행하기 전 후보 파일 범위 좁히기.

## 경험 법칙 (Rule of thumb)

- 속도보다 정확성이 필요하거나 **변경 사항을 적용(apply changes)** 해야 한다면 → `ast-grep`으로 시작하세요.
- 단순히 빠른 속도가 필요하거나 그저 **텍스트를 검색(hunting text)** 하는 중이라면 → `rg`로 시작하세요.
- 종종 결합해서 사용합니다: `rg`로 파일 목록을 추린 다음, `ast-grep`으로 정밀하게 매칭하거나 수정합니다.

## 스니펫 (Snippets)

구조화된 코드 찾기 (주석/문자열 무시, 예: `error`를 반환하는 모든 함수 찾기):

```bash
ast-grep run -l Go -p 'func $NAME($ARGS) error { $$$ }'
```

코드모드 (예: `fmt.Println`을 `log.Println`으로 안전하게 변경):

```bash
ast-grep run -l Go -p 'fmt.Println($A)' -r 'log.Println($A)' -U
```

빠른 텍스트 검색 (예: `fmt.Println` 텍스트 찾기):

```bash
rg -n 'fmt\.Println\(' -t go
```

속도 + 정밀함 결합 (예: Deprecated된 `ioutil.ReadAll`을 `io.ReadAll`로 마이그레이션):

```bash
rg -l -t go 'ioutil\.ReadAll' | xargs ast-grep run -l Go -p 'ioutil.ReadAll($A)' -r 'io.ReadAll($A)' -U
```

## 멘탈 모델 (Mental model)

- **매칭 단위:** `ast-grep` = 노드(node); `rg` = 라인(line).
- **오탐(False positives):** `ast-grep`은 낮음 (문맥 인식); `rg`는 정규식에 의존.
- **재작성(Rewrites):** `ast-grep`은 기본 기능으로 지원; `rg`는 `sed` 등을 조합해야 하며 부수적인 수정(collateral edits) 위험이 있음.

</ast_grep_vs_ripgrep>
