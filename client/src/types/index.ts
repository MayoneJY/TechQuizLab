export type Stage = 'BANK' | 'SME' | 'MID' | 'STARTUP';

export interface User {
  id: string;
  email: string;
  nickname: string;
  createdAt: string;
}

export interface Portfolio {
  id: string;
  userId: string;
  content: string;
  updatedAt: string;
}

export interface ScoreSnapshot {
  id: string;
  userId: string;
  portfolioId: string;
  stage: Stage;
  totalScore: number;
  techDepth: number;
  serviceImpact: number;
  scalability: number;
  collaboration: number;
  originality: number;
  highlights: string[];
  risks: string[];
  createdAt: string;
}

export interface Battle {
  id: string;
  challengerId: string;
  opponentId: string;
  stage: Stage;
  challengerScore: number;
  opponentScore: number;
  winnerUserId: string;
  challengerName: string;
  opponentName: string;
  createdAt: string;
  rounds?: BattleRound[];
}

export interface InterviewQuestion {
  id: string;
  question: string;
  context: string; // 포트폴리오에서 추출된 맥락
  expectedPoints: string[]; // 기대되는 답변 포인트들
}

export interface BattleRound {
  roundNumber: number;
  attackerId: string;
  attackerName: string;
  defenderId: string;
  defenderName: string;
  question: InterviewQuestion;
  answer?: string;
  answerScore?: number; // 0-100
  damage?: number;
  feedback?: string;
}

export interface RankingEntry {
  userId: string;
  nickname: string;
  totalScore: number;
  rank: number;
  wins: number;
  losses: number;
}

export const STAGE_INFO: Record<Stage, {
  name: string;
  description: string;
  emoji: string;
  color: string;
}> = {
  BANK: {
    name: '은행사',
    description: '안정성과 문서화 중심',
    emoji: '🏦',
    color: 'bg-blue-500'
  },
  SME: {
    name: '중소기업',
    description: '실무형·멀티태스킹 중심',
    emoji: '🏢',
    color: 'bg-green-500'
  },
  MID: {
    name: '중견기업',
    description: '프로세스 + 실무 균형형',
    emoji: '🏭',
    color: 'bg-purple-500'
  },
  STARTUP: {
    name: '스타트업',
    description: '속도, 주도성, 메이킹 중심',
    emoji: '🚀',
    color: 'bg-orange-500'
  }
};
