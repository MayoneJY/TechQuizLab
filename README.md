# 잡스페이스 (Job Space) 🚀

취업을 위한 다양한 테마의 퀴즈 게임

## 📖 프로젝트 소개

잡스페이스는 게이미피케이션을 활용한 취업 준비용 퀴즈 게임입니다. 픽셀 아트 스타일의 우주 게임 컨셉으로 재미있게 CS 지식을 학습할 수 있습니다.

## 🎮 주요 기능

- ✅ 회원가입/로그인 시스템
- ✅ 주제별 퀴즈 풀이
- ✅ 실시간 정답 채점
- ✅ 오답 노트 (북마크) 기능
- ✅ 업적 시스템
- ✅ 일일 미션 시스템
- ✅ 우주선 커서 효과
- ✅ 게임화된 UI/UX

## 🛠 기술 스택

### Frontend
- **Vue 3** (Composition API)
- **TypeScript**
- **Pinia** (State Management)
- **Vue Router**
- **Axios** (HTTP Client)
- **Vite** (Build Tool)

### Backend API
- **Base URL**: `https://battle.mayonedev.com`
- **API Documentation**: [Swagger UI](https://battle.mayonedev.com/swagger-ui/index.html)

## 📁 프로젝트 구조

```
passproject-5-final/
├── client/                 # Vue.js 프론트엔드
│   ├── src/
│   │   ├── components/    # 재사용 가능한 컴포넌트
│   │   │   ├── CustomCursor.vue      # 우주선 커서
│   │   │   ├── CursorTrail.vue       # 커서 궤적 효과
│   │   │   ├── ParticleBackground.vue # 파티클 배경
│   │   │   ├── ExplosionEffect.vue    # 폭발 효과
│   │   │   ├── PixelHeart.vue         # 하트 아이콘
│   │   │   ├── PixelSpaceship.vue     # 우주선 아이콘
│   │   │   └── PixelMonster.vue       # 몬스터 아이콘
│   │   ├── views/         # 페이지 컴포넌트
│   │   │   ├── Home.vue    # 홈 화면
│   │   │   ├── Game.vue    # 게임 화면
│   │   │   └── Login.vue   # 로그인 화면
│   │   ├── stores/        # Pinia 스토어
│   │   │   ├── auth.ts           # 인증 관리
│   │   │   ├── game.ts            # 게임 상태
│   │   │   ├── question.ts        # 문제 관리
│   │   │   ├── topic.ts           # 주제 관리
│   │   │   └── gamification.ts   # 게이미피케이션
│   │   ├── services/       # API 서비스
│   │   │   └── api.ts     # Axios 설정 및 API 메서드
│   │   ├── router/         # 라우터 설정
│   │   └── style.css       # 전역 스타일
│   └── package.json
├── server/                 # Spring Boot 백엔드
└── USER_FLOW.md           # 유저 플로우 문서
```

## 🚀 시작하기

### Prerequisites
- Node.js 18+
- npm 또는 yarn

### Installation

```bash
# 클라이언트 디렉토리로 이동
cd client

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

### 환경 변수

`.env` 파일을 생성하여 API URL을 설정할 수 있습니다:

```env
VITE_API_BASE_URL=https://battle.mayonedev.com
```

## 📚 주요 화면

### 1. 홈 화면
- 게임 타이틀
- 주제 선택
- 로그인/회원가입
- 업적 및 일일 미션 확인

### 2. 로그인 화면
- 이메일/비밀번호 로그인
- 회원가입
- 이메일/닉네임 중복 확인

### 3. 게임 화면
- 문제 표시
- 답안 입력
- 정답/오답 피드백
- 생명력 및 점수 표시
- 게임 오버/승리 화면

## 🎯 API 엔드포인트

### 인증
- `POST /api/users/login` - 로그인
- `POST /api/users` - 회원가입
- `GET /api/users/exists/email/{email}` - 이메일 중복 확인
- `GET /api/users/exists/nickname/{nickname}` - 닉네임 중복 확인

### 주제
- `GET /api/topics` - 주제 목록 조회
- `GET /api/topics/{topicId}/level` - 주제별 레벨 조회

### 문제
- `GET /api/questions?topicId={id}` - 주제별 문제 조회
- `GET /api/questions/{id}` - 문제 상세 조회
- `POST /api/questions/{id}/submit` - 정답 제출
- `POST /api/questions/{id}/bookmark` - 북마크 추가
- `GET /api/questions/bookmarks` - 북마크 목록 조회

### 게이미피케이션
- `GET /api/gamification/achievements` - 전체 업적 목록
- `GET /api/gamification/my-achievements?userId={id}` - 내 업적 조회
- `GET /api/gamification/missions/daily?userId={id}` - 일일 미션 조회
- `POST /api/gamification/missions/{id}/claim` - 미션 보상 수령

## 🎨 디자인 특징

- **픽셀 아트 스타일**: 레트로 게임 느낌
- **우주 테마**: 우주선, 별, 몬스터 등 우주 관련 요소
- **둥근 한글 폰트**: Pretendard, Noto Sans KR 사용
- **인터랙티브 효과**: 호버, 클릭, 애니메이션 효과
- **반응형 디자인**: 모바일 및 데스크톱 지원

## 📖 문서

- [유저 플로우 문서](./USER_FLOW.md) - 전체 사용자 흐름 및 기능 설명

## 🔧 개발

### 빌드

```bash
npm run build
```

### 프리뷰

```bash
npm run preview
```

## 📝 라이선스

이 프로젝트는 교육 목적으로 제작되었습니다.
