# Repository Guidelines

## Project Structure & Module Organization
This repository is a Maven multi-module microservice system with a separate Vue frontend.

- `unimed-auth`, `unimed-gateway`: auth and gateway services.
- `unimed-modules/*`: core business services (for example `unimed-system`, `unimed-resource`, `unimed-workflow`, `unimed-dh`).
- `unimed-visual/*`: monitoring and ops services.
- `unimed-common/*`: shared libraries (security, redis, mybatis, tenant, etc.).
- `unimed-api/*`: cross-service API contracts and DTOs.
- `unimed-example/*`: demo and integration examples.
- `web/plus-ui`: Vue 3 + TypeScript frontend.
- `script/sql/*`: database initialization and upgrade scripts.

Use standard Java layout: `src/main/java`, `src/main/resources`, `src/test/java`.

## Build, Test, and Development Commands
- Backend full build (default skips tests): `mvn clean install -DskipTests`
- Backend build with tests: `mvn clean package -DskipTests=false`
- Run backend tests only: `mvn test -DskipTests=false`
- Run one service locally: `mvn -pl unimed-gateway spring-boot:run`

Frontend (`web/plus-ui`):
- Install deps: `npm install`
- Start dev server: `npm run dev`
- Production build: `npm run build:prod`
- Lint and auto-fix: `npm run lint:eslint:fix`
- Format code: `npm run prettier`

## Coding Style & Naming Conventions
- Follow `.editorconfig`: UTF-8, LF, trim trailing whitespace.
- Indentation: 4 spaces by default; 2 spaces for `*.json` and `*.yml`.
- Java: packages in lowercase, classes in PascalCase, methods/fields in camelCase.
- Vue/TS: keep existing structure (`src/api/**/index.ts` + `types.ts`, page entries often `index.vue`).
- Use ESLint + Prettier in frontend before opening a PR.

## Testing Guidelines
- Backend tests use JUnit (via `spring-boot-starter-test`) and Maven Surefire.
- Name test classes `*Test` and place under `src/test/java`.
- Tag environment-specific tests with `@Tag` when needed; keep default tests runnable locally.
- For frontend changes, add/maintain Vitest tests (`npx vitest run`) when business logic is introduced.

## Commit & Pull Request Guidelines
Git history uses short imperative subjects in either English or Chinese (for example `Update .gitignore`, `重构 DH 模块...`).

- Keep subject concise and module-oriented, e.g. `unimed-auth: refine token validation`.
- One logical change per commit; include SQL/config updates in the same PR when required.
- PRs should include: purpose, affected modules, test evidence, and screenshots for UI changes.
- Link related issues/tasks and note any required environment or Nacos config changes.

## Security & Configuration Tips
- Never commit secrets or real credentials in `application*.yml`.
- Prefer externalized configuration (Nacos/environment variables).
- When schema changes are introduced, add matching scripts under `script/sql/update/*`.
