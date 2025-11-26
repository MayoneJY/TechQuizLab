import { Quiz } from '../stores/game'

export const quizData: Quiz[] = [
  // 면접 관련
  {
    id: 1,
    category: '면접',
    question: '면접에서 "자기소개를 해주세요"라고 물었을 때 가장 적절한 답변 시간은?',
    options: ['30초', '1-2분', '5분 이상', '시간 제한 없음'],
    correctAnswer: 1,
    explanation: '면접에서 자기소개는 1-2분이 적절합니다. 간결하면서도 핵심을 전달하는 것이 중요합니다.'
  },
  {
    id: 2,
    category: '면접',
    question: 'STAR 기법에서 S는 무엇을 의미하나요?',
    options: ['Situation (상황)', 'Skill (기술)', 'Success (성공)', 'Strategy (전략)'],
    correctAnswer: 0,
    explanation: 'STAR 기법은 Situation(상황), Task(과제), Action(행동), Result(결과)의 약자입니다.'
  },
  {
    id: 3,
    category: '면접',
    question: '면접에서 질문을 잘 이해하지 못했을 때 가장 좋은 대응은?',
    options: ['대충 답변하기', '질문을 다시 확인하기', '무시하고 넘어가기', '다른 질문으로 대체하기'],
    correctAnswer: 1,
    explanation: '질문을 정확히 이해하지 못했다면 정중하게 다시 확인하는 것이 좋습니다.'
  },
  
  // 이력서 관련
  {
    id: 4,
    category: '이력서',
    question: '이력서 작성 시 가장 중요한 원칙은?',
    options: ['길게 작성하기', '정확성과 간결성', '과장하기', '사진 크게 넣기'],
    correctAnswer: 1,
    explanation: '이력서는 정확하고 간결하게 작성하는 것이 가장 중요합니다.'
  },
  {
    id: 5,
    category: '이력서',
    question: '이력서의 자기소개서는 보통 몇 자 정도가 적절한가요?',
    options: ['500자', '800-1000자', '2000자 이상', '자유롭게'],
    correctAnswer: 1,
    explanation: '자기소개서는 보통 800-1000자 정도가 적절하며, 회사 요구사항에 맞춰 작성합니다.'
  },
  
  // 기술 관련
  {
    id: 6,
    category: '기술',
    question: 'RESTful API에서 GET 메서드의 특징은?',
    options: ['데이터 수정', '데이터 조회', '데이터 삭제', '데이터 생성'],
    correctAnswer: 1,
    explanation: 'GET 메서드는 서버의 데이터를 조회하는 용도로 사용되며, 멱등성(idempotent)을 가집니다.'
  },
  {
    id: 7,
    category: '기술',
    question: 'Git에서 현재 변경사항을 스테이징 영역에 추가하는 명령어는?',
    options: ['git commit', 'git add', 'git push', 'git pull'],
    correctAnswer: 1,
    explanation: 'git add 명령어는 변경사항을 스테이징 영역에 추가합니다.'
  },
  
  // 직무 관련
  {
    id: 8,
    category: '직무',
    question: '프론트엔드 개발자의 주요 역할은?',
    options: ['서버 관리', '사용자 인터페이스 개발', '데이터베이스 설계', '네트워크 구성'],
    correctAnswer: 1,
    explanation: '프론트엔드 개발자는 사용자가 직접 상호작용하는 인터페이스를 개발합니다.'
  },
  {
    id: 9,
    category: '직무',
    question: '백엔드 개발자가 주로 다루는 것은?',
    options: ['UI/UX 디자인', '서버 로직 및 API 개발', '마케팅', '고객 서비스'],
    correctAnswer: 1,
    explanation: '백엔드 개발자는 서버 측 로직, 데이터베이스, API 등을 개발합니다.'
  },
  
  // 취업 전략
  {
    id: 10,
    category: '전략',
    question: '포트폴리오를 만들 때 가장 중요한 것은?',
    options: ['양이 많을수록 좋음', '질과 관련성', '디자인만 예쁘면 됨', '복사해서 사용'],
    correctAnswer: 1,
    explanation: '포트폴리오는 양보다 질과 지원하는 직무와의 관련성이 중요합니다.'
  },
  {
    id: 11,
    category: '전략',
    question: '네트워킹의 가장 큰 장점은?',
    options: ['무료 식사', '정보와 기회 획득', '시간 낭비', '스트레스'],
    correctAnswer: 1,
    explanation: '네트워킹을 통해 업계 정보와 취업 기회를 얻을 수 있습니다.'
  },
  
  // 추가 퀴즈들
  {
    id: 12,
    category: '면접',
    question: '면접에서 "왜 우리 회사를 선택했나요?" 질문에 답할 때 강조해야 할 것은?',
    options: ['급여', '회사의 비전과 나의 가치관 일치', '근무 시간', '위치'],
    correctAnswer: 1,
    explanation: '회사의 비전, 문화, 그리고 자신의 가치관과의 일치점을 강조하는 것이 좋습니다.'
  },
  {
    id: 13,
    category: '기술',
    question: 'JavaScript에서 비동기 처리를 위한 방법이 아닌 것은?',
    options: ['Promise', 'async/await', 'Callback', 'for loop'],
    correctAnswer: 3,
    explanation: 'for loop는 동기적으로 실행되며, 비동기 처리를 위한 방법이 아닙니다.'
  },
  {
    id: 14,
    category: '직무',
    question: '풀스택 개발자가 하는 일은?',
    options: ['프론트엔드만', '백엔드만', '프론트엔드와 백엔드 모두', '디자인만'],
    correctAnswer: 2,
    explanation: '풀스택 개발자는 프론트엔드와 백엔드 모두를 다룰 수 있는 개발자입니다.'
  },
  {
    id: 15,
    category: '전략',
    question: '취업 준비 시 가장 먼저 해야 할 것은?',
    options: ['이력서 작성', '자기 분석과 목표 설정', '면접 준비', '포트폴리오 제작'],
    correctAnswer: 1,
    explanation: '자신을 분석하고 목표를 명확히 설정한 후 구체적인 준비를 시작하는 것이 효과적입니다.'
  }
]

export const categories = ['전체', '면접', '이력서', '기술', '직무', '전략']

