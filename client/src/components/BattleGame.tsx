import { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Textarea } from './ui/textarea';
import { Badge } from './ui/badge';
import { Progress } from './ui/progress';
import { Stage, STAGE_INFO, InterviewQuestion, BattleRound } from '../types';
import { battleApi } from '../lib/mockApi';
import { motion } from 'motion/react';
import { Swords, Heart, Zap, MessageSquare, Loader2, X } from 'lucide-react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';

interface BattleGameProps {
  opponentId: string;
  opponentName: string;
  stage: Stage;
  onBattleEnd: (result: { winner: string; rounds: BattleRound[] }) => void;
  onCancel: () => void;
}

export function BattleGame({ opponentId, opponentName, stage, onBattleEnd, onCancel }: BattleGameProps) {
  const [myHP, setMyHP] = useState(1000);
  const [opponentHP, setOpponentHP] = useState(1000);
  const [currentRound, setCurrentRound] = useState(0);
  const [isMyTurn, setIsMyTurn] = useState(true);
  const [questions, setQuestions] = useState<InterviewQuestion[]>([]);
  const [currentQuestion, setCurrentQuestion] = useState<InterviewQuestion | null>(null);
  const [myAnswer, setMyAnswer] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [roundHistory, setRoundHistory] = useState<BattleRound[]>([]);
  const [lastDamage, setLastDamage] = useState<{ attacker: string; damage: number } | null>(null);
  const [showRoundResult, setShowRoundResult] = useState(false);
  const [lastRoundResult, setLastRoundResult] = useState<{
    score: number;
    damage: number;
    feedback: string;
    attacker: string;
    defender: string;
  } | null>(null);

  const initBattle = useCallback(async () => {
    const qs = await battleApi.generateQuestions(stage);
    setQuestions(qs);
    setCurrentQuestion(qs[0]);
  }, [stage]);

  useEffect(() => {
    void initBattle();
  }, [initBattle]);

  const handleSubmitAnswer = async () => {
    if (!currentQuestion || !myAnswer.trim()) {
      alert('답변을 작성해주세요!');
      return;
    }

    setIsSubmitting(true);
    
    try {
      // Evaluate my answer
      const result = await battleApi.submitAnswer(currentQuestion, myAnswer);
      
      // Deal damage to opponent
      const newOpponentHP = Math.max(0, opponentHP - result.damage);
      setOpponentHP(newOpponentHP);
      
      // Show damage animation
      setLastDamage({ attacker: 'player', damage: result.damage });
      setTimeout(() => setLastDamage(null), 2000);

      const round: BattleRound = {
        roundNumber: currentRound + 1,
        attackerId: 'user-1',
        attackerName: '나',
        defenderId: opponentId,
        defenderName: opponentName,
        question: currentQuestion,
        answer: myAnswer,
        answerScore: result.score,
        damage: result.damage,
        feedback: result.feedback
      };

      const updatedHistory = [...roundHistory, round];
      setRoundHistory(updatedHistory);
      setLastRoundResult({
        ...result,
        attacker: '나',
        defender: opponentName
      });
      setShowRoundResult(true);

      // Check if opponent is defeated
      if (newOpponentHP <= 0) {
        setTimeout(() => {
          onBattleEnd({ winner: 'user-1', rounds: updatedHistory });
        }, 2000);
        return;
      }

      // Clear answer and switch turn after showing result
      setTimeout(() => {
        setMyAnswer('');
        setIsMyTurn(false);
        setShowRoundResult(false);
        void opponentTurn();
      }, 3000);

    } catch (error) {
      alert('답변 제출 실패');
    } finally {
      setIsSubmitting(false);
    }
  };

  const opponentTurn = async () => {
    if (!questions.length) {
      return;
    }
    // Wait a bit before opponent acts
    await new Promise(resolve => setTimeout(resolve, 1500));

    const questionIndex = Math.floor(Math.random() * questions.length);
    const question = questions[questionIndex];
    
    setCurrentQuestion(question);

    // Opponent answers
    const result = await battleApi.opponentAnswer(question);
    
    // Deal damage to player
    const newMyHP = Math.max(0, myHP - result.damage);
    setMyHP(newMyHP);
    
    // Show damage animation
    setLastDamage({ attacker: 'opponent', damage: result.damage });
    setTimeout(() => setLastDamage(null), 2000);

    const round: BattleRound = {
      roundNumber: currentRound + 1,
      attackerId: opponentId,
      attackerName: opponentName,
      defenderId: 'user-1',
      defenderName: '나',
      question,
      answerScore: result.score,
      damage: result.damage,
      feedback: result.feedback
    };

    const updatedHistory = [...roundHistory, round];
    setRoundHistory(updatedHistory);
    setLastRoundResult({
      ...result,
      attacker: opponentName,
      defender: '나'
    });
    setShowRoundResult(true);

    // Check if player is defeated
    if (newMyHP <= 0) {
      setTimeout(() => {
        onBattleEnd({ winner: opponentId, rounds: updatedHistory });
      }, 2000);
      return;
    }

    // Switch to player's turn
    const nextRound = currentRound + 1;

    setTimeout(() => {
      const nextQuestionIndex = nextRound % questions.length;
      setCurrentQuestion(questions[nextQuestionIndex]);
      setCurrentRound(nextRound);
      setIsMyTurn(true);
      setShowRoundResult(false);
    }, 3000);
  };

  return (
    <div className="space-y-6">
      {/* HP Bars */}
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 md:gap-6">
        {/* Player HP */}
        <Card
          className={`border border-[color:var(--border)]/60 bg-[color:var(--card)] transition-all ${
            isMyTurn ? 'ring-1 ring-[color:var(--primary)]/60' : ''
          }`}
        >
          <CardContent className="relative z-10 pt-6">
            <div className="mb-2 flex items-center justify-between text-sm">
              <div className="flex items-center gap-2 text-[color:var(--foreground)]">
                <span className="text-2xl">👊</span>
                <span className="text-base font-medium">나</span>
                {isMyTurn && (
                  <Badge className="border-[color:var(--primary)]/60 bg-[color:var(--primary)]/15 text-[color:var(--primary-foreground)]">
                    내 턴
                  </Badge>
                )}
              </div>
              <div className="flex items-center gap-2 text-[color:var(--foreground)]">
                <Heart className="h-5 w-5 text-destructive" />
                <span className="font-mono text-sm">{myHP} / 1000</span>
              </div>
            </div>
            <Progress value={(myHP / 1000) * 100} className="h-3" />
          </CardContent>
        </Card>

        {/* Opponent HP */}
        <Card
          className={`border border-[color:var(--border)]/60 bg-[color:var(--card)] transition-all ${
            !isMyTurn ? 'ring-1 ring-destructive/60' : ''
          }`}
        >
          <CardContent className="relative z-10 pt-6">
            <div className="mb-2 flex items-center justify-between text-sm">
              <div className="flex items-center gap-2 text-[color:var(--foreground)]">
                <span className="text-2xl">🛡️</span>
                <span className="text-base font-medium">{opponentName}</span>
                {!isMyTurn && (
                  <Badge className="border-destructive/60 bg-destructive/15 text-destructive">
                    상대 턴
                  </Badge>
                )}
              </div>
              <div className="flex items-center gap-2 text-[color:var(--foreground)]">
                <Heart className="h-5 w-5 text-destructive" />
                <span className="font-mono text-sm">{opponentHP} / 1000</span>
              </div>
            </div>
            <Progress value={(opponentHP / 1000) * 100} className="h-3" />
          </CardContent>
        </Card>
      </div>

      {/* Damage Animation */}
      {lastDamage && (
        <motion.div
          initial={{ scale: 0, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="fixed left-1/2 top-1/2 z-50 -translate-x-1/2 -translate-y-1/2"
        >
          <div
            className={`text-6xl font-semibold ${
              lastDamage.attacker === 'player'
                ? 'text-[color:var(--primary)]'
                : 'text-destructive'
            }`}
          >
            <Zap className="mx-auto mb-2 h-20 w-20" />
            <div className="text-center">-{lastDamage.damage}</div>
          </div>
        </motion.div>
      )}

      {/* Battle Stage */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center justify-between">
            <span className="flex items-center gap-2">
              <Swords className="h-5 w-5 text-[color:var(--primary)]" />
              <span className="text-[color:var(--foreground)]">
                {STAGE_INFO[stage].emoji} {STAGE_INFO[stage].name} 면접전
              </span>
            </span>
            <Button onClick={onCancel} variant="ghost" size="sm">
              <X className="w-4 h-4" />
            </Button>
          </CardTitle>
        </CardHeader>
      </Card>

      {/* Question & Answer */}
      {isMyTurn && currentQuestion && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MessageSquare className="h-5 w-5 text-[color:var(--primary)]" />
                면접 질문
              </CardTitle>
              <Badge className="w-fit border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 text-[color:var(--foreground)]">
                질문 맥락: {currentQuestion.context}
              </Badge>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="rounded-lg border border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 p-4">
                <p className="text-lg text-[color:var(--foreground)]">{currentQuestion.question}</p>
              </div>

              <div className="space-y-2">
                <label className="text-sm text-muted-foreground">답변 작성</label>
                <Textarea
                  value={myAnswer}
                  onChange={(e) => setMyAnswer(e.target.value)}
                  placeholder="구체적인 경험과 기술적 근거를 포함하여 답변해주세요..."
                  className="min-h-[150px]"
                  disabled={!isMyTurn || isSubmitting}
                />
                <div className="text-xs text-muted-foreground">
                  💡 팁: 구체적인 수치, 기술 스택, 문제 해결 과정을 포함하면 높은 점수를 받을 수 있습니다.
                </div>
              </div>

              <Button
                onClick={handleSubmitAnswer}
                disabled={!myAnswer.trim() || isSubmitting}
                className="w-full"
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    답변 평가 중...
                  </>
                ) : (
                  <>
                    <Zap className="mr-2 h-4 w-4" />
                    공격하기!
                  </>
                )}
              </Button>
            </CardContent>
          </Card>
        </motion.div>
      )}

      {/* Opponent Turn Indicator */}
      {!isMyTurn && (
        <motion.div initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
          <Card>
            <CardContent className="space-y-3 py-12 text-center">
              <Loader2 className="mx-auto h-12 w-12 animate-spin text-[color:var(--primary)]" />
              <p className="text-xl font-medium text-[color:var(--foreground)]">상대가 답변 중입니다...</p>
              <p className="text-sm text-muted-foreground">잠시만 기다려주세요</p>
            </CardContent>
          </Card>
        </motion.div>
      )}

      {/* Round Result Dialog */}
      <Dialog open={showRoundResult} onOpenChange={() => {}}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle className="text-center font-semibold text-[color:var(--foreground)]">
              라운드 결과
            </DialogTitle>
          </DialogHeader>
          {lastRoundResult && (
            <div className="space-y-4">
              <div className="text-center">
                <div className="mb-4 text-5xl">
                  {lastRoundResult.attacker === '나' ? '👊' : '🛡️'}
                </div>
                <p className="mb-2 text-xl font-medium text-[color:var(--foreground)]">
                  {lastRoundResult.attacker}의 공격!
                </p>
                <div className="text-3xl text-destructive">
                  -{lastRoundResult.damage} 데미지
                </div>
              </div>

              <Card>
                <CardContent className="pt-4">
                  <div className="mb-2 flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">답변 점수</span>
                    <Badge className="border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 text-[color:var(--foreground)]">
                      {lastRoundResult.score}점
                    </Badge>
                  </div>
                  <Progress value={lastRoundResult.score} className="mb-4" />
                  <p className="text-sm text-[color:var(--foreground)]">
                    {lastRoundResult.feedback}
                  </p>
                </CardContent>
              </Card>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Round History */}
      {roundHistory.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle>전투 기록</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="max-h-48 space-y-2 overflow-y-auto">
              {roundHistory.map((round, index) => (
                <div
                  key={index}
                  className="flex items-center justify-between rounded-lg border border-[color:var(--border)]/50 bg-[color:var(--accent)]/60 p-2 text-sm"
                >
                  <span className="text-[color:var(--foreground)]">
                    R{round.roundNumber}: {round.attackerName} → {round.defenderName}
                  </span>
                  <div className="flex items-center gap-2 text-sm">
                    <Badge variant="secondary">{round.answerScore}점</Badge>
                    <span className="text-destructive">-{round.damage}</span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
