import { User, Portfolio, ScoreSnapshot, Battle, RankingEntry, Stage } from '../types';

// Mock current user
export const mockCurrentUser: User = {
  id: 'user-1',
  email: 'developer@example.com',
  nickname: '코드마스터',
  createdAt: '2024-01-15T00:00:00Z'
};

// Mock portfolio
export const mockPortfolio: Portfolio = {
  id: 'portfolio-1',
  userId: 'user-1',
  content: `# 포트폴리오

## 주요 프로젝트

### 1. E-commerce 플랫폼 개발 (2023.03 - 2024.01)
- React, TypeScript, Next.js를 활용한 프론트엔드 개발
- 일 평균 10만 명의 사용자 트래픽 처리
- Redis 캐싱을 통한 응답속도 40% 개선
- MSA 아키텍처로 서비스 분리 및 확장성 확보
- GitHub Actions를 통한 CI/CD 파이프라인 구축

### 2. 실시간 채팅 서비스 (2022.06 - 2023.02)
- WebSocket 기반 실시간 통신 구현
- MongoDB 샤딩을 통한 데이터 분산 처리
- Docker, Kubernetes를 활용한 컨테이너 오케스트레이션
- 동시 접속자 5만명 처리 가능한 아키텍처 설계

## 기술 스택
- Frontend: React, TypeScript, Next.js, TailwindCSS
- Backend: Node.js, Spring Boot, Python
- Database: MySQL, MongoDB, Redis
- DevOps: Docker, Kubernetes, AWS, CI/CD

## 협업 경험
- 5명 규모의 개발팀 리드 경험
- 코드 리뷰 문화 정착 및 기술 문서화
- Jira를 활용한 애자일 스프린트 운영

## 오픈소스 기여
- React 관련 라이브러리 3개 배포
- Stack Overflow 상위 5% 기여자`,
  updatedAt: '2024-11-01T00:00:00Z'
};

// Mock scores for different stages
export const mockScores: Record<Stage, ScoreSnapshot> = {
  BANK: {
    id: 'score-bank-1',
    userId: 'user-1',
    portfolioId: 'portfolio-1',
    stage: 'BANK',
    totalScore: 8420,
    techDepth: 85,
    serviceImpact: 82,
    scalability: 78,
    collaboration: 90,
    originality: 75,
    highlights: [
      '체계적인 문서화 및 협업 프로세스',
      '안정적인 서비스 운영 경험',
      '대규모 트래픽 처리 역량'
    ],
    risks: [
      '최신 기술 트렌드 도입 경험 부족',
      '금융권 특화 경험 제한적'
    ],
    createdAt: '2024-11-05T10:00:00Z'
  },
  SME: {
    id: 'score-sme-1',
    userId: 'user-1',
    portfolioId: 'portfolio-1',
    stage: 'SME',
    totalScore: 8650,
    techDepth: 88,
    serviceImpact: 80,
    scalability: 75,
    collaboration: 85,
    originality: 82,
    highlights: [
      '다양한 기술 스택 활용 능력',
      '팀 리드 경험 보유',
      '빠른 문제 해결 능력'
    ],
    risks: [
      '대기업 프로세스 경험 부족',
      '레거시 시스템 개선 경험 제한적'
    ],
    createdAt: '2024-11-05T10:00:00Z'
  },
  MID: {
    id: 'score-mid-1',
    userId: 'user-1',
    portfolioId: 'portfolio-1',
    stage: 'MID',
    totalScore: 8580,
    techDepth: 87,
    serviceImpact: 84,
    scalability: 80,
    collaboration: 88,
    originality: 78,
    highlights: [
      '프로세스와 실무 균형감',
      '확장 가능한 아키텍처 설계',
      '효과적인 팀 협업'
    ],
    risks: [
      '대규모 조직 경험 제한적',
      '복잡한 레거시 마이그레이션 경험 부족'
    ],
    createdAt: '2024-11-05T10:00:00Z'
  },
  STARTUP: {
    id: 'score-startup-1',
    userId: 'user-1',
    portfolioId: 'portfolio-1',
    stage: 'STARTUP',
    totalScore: 8920,
    techDepth: 86,
    serviceImpact: 78,
    scalability: 72,
    collaboration: 80,
    originality: 95,
    highlights: [
      '높은 주도성과 오픈소스 기여',
      '0→1 프로젝트 구축 경험',
      '빠른 학습과 실행력'
    ],
    risks: [
      '대규모 조직 운영 경험 부족',
      '장기적인 유지보수 관점 보완 필요'
    ],
    createdAt: '2024-11-05T10:00:00Z'
  }
};

// Mock other users
export const mockUsers: User[] = [
  {
    id: 'user-2',
    email: 'user2@example.com',
    nickname: '풀스택개발자',
    createdAt: '2024-02-01T00:00:00Z'
  },
  {
    id: 'user-3',
    email: 'user3@example.com',
    nickname: '백엔드마스터',
    createdAt: '2024-01-20T00:00:00Z'
  },
  {
    id: 'user-4',
    email: 'user4@example.com',
    nickname: '프론트엔드닌자',
    createdAt: '2024-03-10T00:00:00Z'
  },
  {
    id: 'user-5',
    email: 'user5@example.com',
    nickname: 'DevOps전문가',
    createdAt: '2024-02-15T00:00:00Z'
  },
  {
    id: 'user-6',
    email: 'user6@example.com',
    nickname: '아키텍트',
    createdAt: '2024-01-05T00:00:00Z'
  }
];

// Mock battles
export const mockBattles: Battle[] = [
  {
    id: 'battle-1',
    challengerId: 'user-1',
    opponentId: 'user-2',
    stage: 'STARTUP',
    challengerScore: 8920,
    opponentScore: 8650,
    winnerUserId: 'user-1',
    challengerName: '코드마스터',
    opponentName: '풀스택개발자',
    createdAt: '2024-11-06T14:30:00Z'
  },
  {
    id: 'battle-2',
    challengerId: 'user-3',
    opponentId: 'user-1',
    stage: 'BANK',
    challengerScore: 8680,
    opponentScore: 8420,
    winnerUserId: 'user-3',
    challengerName: '백엔드마스터',
    opponentName: '코드마스터',
    createdAt: '2024-11-05T09:15:00Z'
  },
  {
    id: 'battle-3',
    challengerId: 'user-1',
    opponentId: 'user-4',
    stage: 'SME',
    challengerScore: 8650,
    opponentScore: 8200,
    winnerUserId: 'user-1',
    challengerName: '코드마스터',
    opponentName: '프론트엔드닌자',
    createdAt: '2024-11-04T16:45:00Z'
  }
];

// Mock rankings for different stages
export const mockRankings: Record<Stage, RankingEntry[]> = {
  STARTUP: [
    { userId: 'user-1', nickname: '코드마스터', totalScore: 8920, rank: 1, wins: 15, losses: 3 },
    { userId: 'user-6', nickname: '아키텍트', totalScore: 8850, rank: 2, wins: 12, losses: 4 },
    { userId: 'user-2', nickname: '풀스택개발자', totalScore: 8650, rank: 3, wins: 10, losses: 5 },
    { userId: 'user-5', nickname: 'DevOps전문가', totalScore: 8420, rank: 4, wins: 9, losses: 7 },
    { userId: 'user-4', nickname: '프론트엔드닌자', totalScore: 8200, rank: 5, wins: 7, losses: 8 },
    { userId: 'user-3', nickname: '백엔드마스터', totalScore: 8100, rank: 6, wins: 6, losses: 9 }
  ],
  BANK: [
    { userId: 'user-3', nickname: '백엔드마스터', totalScore: 8680, rank: 1, wins: 14, losses: 2 },
    { userId: 'user-6', nickname: '아키텍트', totalScore: 8550, rank: 2, wins: 11, losses: 5 },
    { userId: 'user-1', nickname: '코드마스터', totalScore: 8420, rank: 3, wins: 10, losses: 6 },
    { userId: 'user-5', nickname: 'DevOps전문가', totalScore: 8300, rank: 4, wins: 8, losses: 7 },
    { userId: 'user-2', nickname: '풀스택개발자', totalScore: 8150, rank: 5, wins: 7, losses: 9 },
    { userId: 'user-4', nickname: '프론트엔드닌자', totalScore: 7920, rank: 6, wins: 5, losses: 10 }
  ],
  SME: [
    { userId: 'user-2', nickname: '풀스택개발자', totalScore: 8780, rank: 1, wins: 16, losses: 2 },
    { userId: 'user-1', nickname: '코드마스터', totalScore: 8650, rank: 2, wins: 13, losses: 4 },
    { userId: 'user-5', nickname: 'DevOps전문가', totalScore: 8520, rank: 3, wins: 11, losses: 6 },
    { userId: 'user-6', nickname: '아키텍트', totalScore: 8400, rank: 4, wins: 9, losses: 7 },
    { userId: 'user-3', nickname: '백엔드마스터', totalScore: 8280, rank: 5, wins: 8, losses: 8 },
    { userId: 'user-4', nickname: '프론트엔드닌자', totalScore: 8050, rank: 6, wins: 6, losses: 10 }
  ],
  MID: [
    { userId: 'user-6', nickname: '아키텍트', totalScore: 8720, rank: 1, wins: 15, losses: 3 },
    { userId: 'user-1', nickname: '코드마스터', totalScore: 8580, rank: 2, wins: 12, losses: 5 },
    { userId: 'user-3', nickname: '백엔드마스터', totalScore: 8490, rank: 3, wins: 11, losses: 6 },
    { userId: 'user-2', nickname: '풀스택개발자', totalScore: 8420, rank: 4, wins: 10, losses: 7 },
    { userId: 'user-5', nickname: 'DevOps전문가', totalScore: 8310, rank: 5, wins: 8, losses: 8 },
    { userId: 'user-4', nickname: '프론트엔드닌자', totalScore: 8120, rank: 6, wins: 6, losses: 10 }
  ]
};
