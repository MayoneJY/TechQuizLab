import { Stage, ScoreSnapshot, Battle, RankingEntry, InterviewQuestion, User } from '../types';
import { mockCurrentUser, mockPortfolio, mockScores, mockUsers, mockBattles, mockRankings } from './mockData';

// Simulate API delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

// Auth API
export const authApi = {
  login: async (email: string, password: string) => {
    await delay(500);
    if (email && password) {
      return { success: true, user: mockCurrentUser, token: 'mock-jwt-token' };
    }
    throw new Error('Invalid credentials');
  },
  
  signup: async (email: string, password: string, nickname: string) => {
    await delay(500);
    return { 
      success: true, 
      user: { ...mockCurrentUser, email, nickname },
      token: 'mock-jwt-token'
    };
  }
};

// Portfolio API
export const portfolioApi = {
  get: async () => {
    await delay(300);
    return mockPortfolio;
  },
  
  save: async (content: string) => {
    await delay(500);
    return {
      ...mockPortfolio,
      content,
      updatedAt: new Date().toISOString()
    };
  }
};

// Score API
export const scoreApi = {
  measure: async (stage: Stage): Promise<ScoreSnapshot> => {
    await delay(2000); // Simulate AI processing time
    
    // Simulate some variation in scores
    const baseScore = mockScores[stage];
    const variation = Math.floor(Math.random() * 100) - 50;
    
    return {
      ...baseScore,
      totalScore: baseScore.totalScore + variation,
      createdAt: new Date().toISOString()
    };
  },
  
  getMyScore: async (stage: Stage): Promise<ScoreSnapshot> => {
    await delay(300);
    return mockScores[stage];
  },
  
  getAllMyScores: async (): Promise<Record<Stage, ScoreSnapshot>> => {
    await delay(300);
    return mockScores;
  }
};

// Interview Question Generator (Mock AI)
const generateInterviewQuestions = (portfolioContent: string, stage: Stage): InterviewQuestion[] => {
  // Mock questions based on stage
  const questionsByStage: Record<Stage, InterviewQuestion[]> = {
    BANK: [
      {
        id: 'q1',
        question: '대규모 트래픽 처리 시 안정성을 어떻게 보장하셨나요?',
        context: '일 평균 10만 명의 사용자 트래픽 처리',
        expectedPoints: ['로드 밸런싱', '캐싱 전략', '모니터링', '장애 대응']
      },
      {
        id: 'q2',
        question: '코드 리뷰 문화를 정착시킨 구체적인 방법은 무엇인가요?',
        context: '코드 리뷰 문화 정착 및 기술 문서화',
        expectedPoints: ['리뷰 프로세스', '팀 합의', '문서화', '지속성']
      },
      {
        id: 'q3',
        question: 'MSA 아키텍처로 전환하면서 겪은 주요 과제는 무엇이었나요?',
        context: 'MSA 아키텍처로 서비스 분리 및 확장성 확보',
        expectedPoints: ['서비스 분리 기준', '통신 방식', '데이터 일관성', '배포 전략']
      }
    ],
    SME: [
      {
        id: 'q1',
        question: '프론트엔드와 백엔드를 모두 다뤄본 경험에서 가장 어려웠던 점은?',
        context: '다양한 기술 스택 활용',
        expectedPoints: ['기술 스택 선택', '학습 곡선', '트레이드오프', '문제 해결']
      },
      {
        id: 'q2',
        question: '5명 규모의 팀을 리드하면서 가장 중요하게 생각한 것은?',
        context: '5명 규모의 개발팀 리드 경험',
        expectedPoints: ['커뮤니케이션', '역할 분담', '동기 부여', '기술 지원']
      },
      {
        id: 'q3',
        question: '응답속도를 40% 개선한 구체적인 방법을 설명해주세요.',
        context: 'Redis 캐싱을 통한 응답속도 40% 개선',
        expectedPoints: ['병목 지점 분석', '캐싱 전략', '측정 방법', '결과 검증']
      }
    ],
    MID: [
      {
        id: 'q1',
        question: 'CI/CD 파이프라인 구축 시 고려한 핵심 요소는 무엇인가요?',
        context: 'GitHub Actions를 통한 CI/CD 파이프라인 구축',
        expectedPoints: ['자동화 범위', '테스트 전략', '배포 안정성', '롤백 계획']
      },
      {
        id: 'q2',
        question: 'Kubernetes를 선택한 이유와 운영상의 장단점은?',
        context: 'Docker, Kubernetes를 활용한 컨테이너 오케스트레이션',
        expectedPoints: ['선택 근거', '학습 비용', '운영 효율', '실제 효과']
      },
      {
        id: 'q3',
        question: '애자일 스프린트 운영 중 어떻게 일정을 조율하셨나요?',
        context: 'Jira를 활용한 애자일 스프린트 운영',
        expectedPoints: ['스프린트 계획', '우선순위', '리스크 관리', '회고']
      }
    ],
    STARTUP: [
      {
        id: 'q1',
        question: '오픈소스 라이브러리를 직접 배포한 동기와 과정을 설명해주세요.',
        context: 'React 관련 라이브러리 3개 배포',
        expectedPoints: ['문제 인식', '솔루션 설계', '커뮤니티 기여', '유지보수']
      },
      {
        id: 'q2',
        question: '실시간 채팅 서비스를 처음부터 구축하면서 배운 점은?',
        context: 'WebSocket 기반 실시간 통신 구현',
        expectedPoints: ['기술 선택', '시행착오', '성능 최적화', '학습 내용']
      },
      {
        id: 'q3',
        question: 'MongoDB 샤딩을 도입한 구체적인 이유와 효과는?',
        context: 'MongoDB 샤딩을 통한 데이터 분산 처리',
        expectedPoints: ['도입 배경', '샤딩 전략', '마이그레이션', '성능 개선']
      }
    ]
  };

  return questionsByStage[stage];
};

// Evaluate answer quality (Mock AI)
const evaluateAnswer = async (question: InterviewQuestion, answer: string): Promise<{ score: number; feedback: string }> => {
  await delay(1500); // Simulate AI processing
  
  if (!answer || answer.trim().length < 20) {
    return {
      score: 20,
      feedback: '답변이 너무 짧습니다. 구체적인 경험과 근거를 추가해주세요.'
    };
  }

  // Mock scoring based on answer length and keywords
  let score = 50; // Base score
  const answerLower = answer.toLowerCase();
  
  // Check for expected points
  let matchedPoints = 0;
  question.expectedPoints.forEach(point => {
    if (answerLower.includes(point.toLowerCase())) {
      matchedPoints++;
    }
  });
  
  score += (matchedPoints / question.expectedPoints.length) * 30;
  
  // Bonus for length (thoughtful answer)
  if (answer.length > 100) score += 10;
  if (answer.length > 200) score += 10;
  
  // Random variation
  score += Math.floor(Math.random() * 10) - 5;
  score = Math.min(100, Math.max(0, score));
  
  const feedbacks = [
    score >= 90 ? '훌륭한 답변입니다! 구체적인 경험과 기술적 깊이가 돋보입니다.' : '',
    score >= 75 ? '좋은 답변입니다. 몇 가지 핵심 포인트를 잘 짚었습니다.' : '',
    score >= 60 ? '괜찮은 답변이지만, 좀 더 구체적인 사례가 있으면 좋겠습니다.' : '',
    score >= 40 ? '기본적인 내용은 포함되어 있으나 깊이가 부족합니다.' : '',
    '답변이 질문의 핵심을 충분히 다루지 못했습니다.'
  ];
  
  const feedback = feedbacks.find(f => f !== '') || feedbacks[feedbacks.length - 1];
  
  return { score, feedback };
};

// Battle API
export const battleApi = {
  start: async (opponentId: string, stage: Stage): Promise<Battle> => {
    await delay(1000);
    
    const opponent = mockUsers.find(u => u.id === opponentId) || mockUsers[0];
    
    const battle: Battle = {
      id: `battle-${Date.now()}`,
      challengerId: mockCurrentUser.id,
      opponentId,
      stage,
      challengerScore: 0,
      opponentScore: 0,
      winnerUserId: '',
      challengerName: mockCurrentUser.nickname,
      opponentName: opponent.nickname,
      createdAt: new Date().toISOString(),
      rounds: []
    };
    
    return battle;
  },
  
  generateQuestions: async (stage: Stage): Promise<InterviewQuestion[]> => {
    await delay(800);
    const portfolio = await portfolioApi.get();
    return generateInterviewQuestions(portfolio.content, stage);
  },
  
  submitAnswer: async (question: InterviewQuestion, answer: string): Promise<{ score: number; damage: number; feedback: string }> => {
    const evaluation = await evaluateAnswer(question, answer);
    const damage = Math.floor((evaluation.score / 100) * 300); // Max 300 damage
    
    return {
      score: evaluation.score,
      damage,
      feedback: evaluation.feedback
    };
  },
  
  opponentAnswer: async (question: InterviewQuestion): Promise<{ score: number; damage: number; feedback: string }> => {
    await delay(1000);
    
    // AI opponent generates random quality answer
    const score = 60 + Math.floor(Math.random() * 30); // 60-90
    const damage = Math.floor((score / 100) * 300);
    
    return {
      score,
      damage,
      feedback: '상대가 답변을 완료했습니다.'
    };
  },
  
  getHistory: async (): Promise<Battle[]> => {
    await delay(300);
    return mockBattles;
  }
};

// Ranking API
export const rankingApi = {
  get: async (stage: Stage): Promise<RankingEntry[]> => {
    await delay(300);
    return mockRankings[stage];
  }
};

// Users API
export const usersApi = {
  getAll: async (): Promise<User[]> => {
    await delay(300);
    return mockUsers;
  },
  
  search: async (query: string): Promise<User[]> => {
    await delay(300);
    return mockUsers.filter(u => 
      u.nickname.toLowerCase().includes(query.toLowerCase()) ||
      u.email.toLowerCase().includes(query.toLowerCase())
    );
  }
};
