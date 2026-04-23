# Skill Registry

**Project**: DrSecurity (app display name: Dr Security)  
**Last Updated**: 2026-04-22  
**Location**: C:\icagps

## User Skills (Global)

| Name | Trigger | Location |
|------|---------|----------|
| brainstorming | Before any creative work, creating features, building components | `~/.agents/skills/brainstorming/` |
| code-quality | When writing code (general correctness, comments, avoiding over-engineering) | `~/.agents/skills/code-quality/` |
| caveman | "caveman mode", "less tokens", "be brief", /caveman | `~/.agents/skills/caveman/` |
| caveman-commit | "write a commit", "commit message", /commit | `~/.agents/skills/caveman-commit/` |
| caveman-compress | /caveman:compress \<filepath\>, "compress memory file" | `~/.agents/skills/caveman-compress/` |
| caveman-help | /caveman-help, "caveman help" | `~/.agents/skills/caveman-help/` |
| caveman-review | "review this PR", "code review", /review | `~/.agents/skills/caveman-review/` |
| filament-pro | Filament v4, Custom Data Sources, Nested Resources, AI Admin Panels | `~/.agents/skills/filament-pro/` |
| find-skills | "how do I do X", "find a skill for X" | `~/.agents/skills/find-skills/` |
| finishing-a-development-branch | Implementation complete, deciding merge/PR/cleanup | `~/.agents/skills/finishing-a-development-branch/` |
| frontend-design | Build web components, pages, landing pages, React components, styling | `~/.agents/skills/frontend-design/` |
| laravel-specialist | Laravel 10+, Eloquent, Sanctum, Horizon, Livewire, Pest tests | `~/.agents/skills/laravel-specialist/` |
| laravel-tdd | Laravel TDD with Pest PHP | `~/.agents/skills/laravel-tdd/` |
| receiving-code-review | Receiving code review feedback, before implementing suggestions | `~/.agents/skills/receiving-code-review/` |
| requesting-code-review | Completing tasks, before merging | `~/.agents/skills/requesting-code-review/` |
| subagent-driven-development | Executing implementation plans with independent tasks | `~/.agents/skills/subagent-driven-development/` |
| test-driven-development | Implementing any feature/bugfix, before writing code | `~/.agents/skills/test-driven-development/` |
| ui-ux-pro-max | UI/UX design, web/mobile interfaces, color palettes, styles | `~/.agents/skills/ui-ux-pro-max/` |
| using-git-worktrees | Starting feature work needing isolation | `~/.agents/skills/using-git-worktrees/` |
| web-design-guidelines | "review my UI", "check accessibility", "audit design" | `~/.agents/skills/web-design-guidelines/` |
| writing-plans | Have spec/requirements for multi-step task, before touching code | `~/.agents/skills/writing-plans/` |

## SDD Skills (Agent Teams Lite)

| Name | Trigger | Location |
|------|---------|----------|
| sdd-init | "sdd init", "iniciar sdd", "openspec init" | `~/.config/opencode/skills/sdd-init/` |
| sdd-explore | Orchestrator launches exploration phase | `~/.config/opencode/skills/sdd-explore/` |
| sdd-propose | Orchestrator launches proposal phase | `~/.config/opencode/skills/sdd-propose/` |
| sdd-spec | Orchestrator launches spec writing phase | `~/.config/opencode/skills/sdd-spec/` |
| sdd-design | Orchestrator launches design phase | `~/.config/opencode/skills/sdd-design/` |
| sdd-tasks | Orchestrator launches task breakdown phase | `~/.config/opencode/skills/sdd-tasks/` |
| sdd-apply | Orchestrator launches implementation phase | `~/.config/opencode/skills/sdd-apply/` |
| sdd-verify | Orchestrator launches verification phase | `~/.config/opencode/skills/sdd-verify/` |
| sdd-archive | Orchestrator launches archive phase | `~/.config/opencode/skills/sdd-archive/` |
| sdd-onboard | Orchestrator launches onboarding walkthrough | `~/.config/opencode/skills/sdd-onboard/` |

## Other Skills

| Name | Trigger | Location |
|------|---------|----------|
| branch-pr | Creating PR, opening PR, preparing for review | `~/.config/opencode/skills/branch-pr/` |
| go-testing | Go tests, Bubbletea TUI testing, teatest | `~/.config/opencode/skills/go-testing/` |
| issue-creation | Creating GitHub issue, reporting bug, requesting feature | `~/.config/opencode/skills/issue-creation/` |
| judgment-day | "judgment day", "review adversarial", "dual review" | `~/.config/opencode/skills/judgment-day/` |
| skill-creator | Create new AI agent skill | `~/.config/opencode/skills/skill-creator/` |
| skill-registry | "update skills", "skill registry", after installing/removing skills | `~/.config/opencode/skills/skill-registry/` |

## Project Conventions

No project-level convention files detected (agents.md, AGENTS.md, CLAUDE.md, .cursorrules, GEMINI.md, copilot-instructions.md).

## Tech Stack Context

- **Language**: Kotlin 2.0.21
- **Platform**: Kotlin Multiplatform (Android + iOS)
- **UI**: Jetpack Compose + Compose Multiplatform
- **Networking**: Ktor 2.3.12
- **Local DB**: SQLDelight 2.0.2
- **Testing**: kotlin.test, coroutines-test, Turbine
