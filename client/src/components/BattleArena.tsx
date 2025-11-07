import { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Badge } from './ui/badge';
import { Stage, STAGE_INFO, BattleRound, User, ScoreSnapshot } from '../types';
import { usersApi, scoreApi } from '../lib/mockApi';
import { StageSelector } from './StageSelector';
import { PowerMeter } from './PowerMeter';
import { BattleGame } from './BattleGame';
import { Swords, Search, Trophy } from 'lucide-react';
import { motion } from 'motion/react';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from './ui/dialog';
import { ImageWithFallback } from './figma/ImageWithFallback';

interface BattleArenaProps {
  onNavigate: (page: string) => void;
}

interface BattleResult {
  winner: string;
  winnerName: string;
  rounds: BattleRound[];
  stage: Stage;
}

export function BattleArena({ onNavigate }: BattleArenaProps) {
  const [selectedStage, setSelectedStage] = useState<Stage>('STARTUP');
  const [users, setUsers] = useState<User[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedOpponent, setSelectedOpponent] = useState<User | null>(null);
  const [battleResult, setBattleResult] = useState<BattleResult | null>(null);
  const [isBattling, setIsBattling] = useState(false);
  const [myScore, setMyScore] = useState<ScoreSnapshot | null>(null);

  const loadUsers = useCallback(async () => {
    try {
      const allUsers = await usersApi.getAll();
      setUsers(allUsers);
    } catch (error) {
      console.error('Failed to load users', error);
    }
  }, []);

  const loadMyScore = useCallback(async () => {
    try {
      const score = await scoreApi.getMyScore(selectedStage);
      setMyScore(score);
    } catch (error) {
      console.error('Failed to load my score', error);
    }
  }, [selectedStage]);

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  useEffect(() => {
    loadMyScore();
  }, [loadMyScore]);

  const handleStartBattle = () => {
    if (!selectedOpponent) return;
    setIsBattling(true);
  };

  const handleBattleEnd = (result: { winner: string; rounds: BattleRound[] }) => {
    setIsBattling(false);
    setBattleResult({
      winner: result.winner,
      winnerName: result.winner === 'user-1' ? '나' : selectedOpponent?.nickname ?? '상대',
      rounds: result.rounds,
      stage: selectedStage
    });
  };

  const handleCancelBattle = () => {
    setIsBattling(false);
    setSelectedOpponent(null);
  };

  const filteredUsers = users.filter(
    (user) =>
      user.nickname.toLowerCase().includes(searchQuery.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const resultStageInfo = battleResult ? STAGE_INFO[battleResult.stage] : null;

  if (isBattling && selectedOpponent) {
    return (
      <BattleGame
        opponentId={selectedOpponent.id}
        opponentName={selectedOpponent.nickname}
        stage={selectedStage}
        onBattleEnd={handleBattleEnd}
        onCancel={handleCancelBattle}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="mb-2 flex items-center gap-3 text-3xl font-semibold text-[color:var(--foreground)]">
            <Swords className="h-8 w-8 text-[color:var(--primary)]" />
            전투 아레나
          </h1>
          <p className="text-sm text-muted-foreground">상대를 선택하고 면접 질문으로 전투하세요</p>
        </div>
        <Button onClick={() => onNavigate('dashboard')} variant="outline">
          대시보드로
        </Button>
      </div>

      {/* Stage Selection */}
      <Card>
        <CardHeader>
          <CardTitle>전투 무대 선택</CardTitle>
          <CardDescription>무대에 따라 다른 면접 질문이 출제됩니다</CardDescription>
        </CardHeader>
        <CardContent>
          <StageSelector selectedStage={selectedStage} onSelectStage={setSelectedStage} />
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* My Power */}
        <Card>
          <CardHeader>
            <CardTitle>내 전투력</CardTitle>
            <CardDescription>{STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name}</CardDescription>
          </CardHeader>
          <CardContent className="flex flex-col items-center">
            <PowerMeter score={myScore?.totalScore || 0} size="md" />
            <div className="mt-4 text-center text-sm text-muted-foreground">
              <p>💡 면접 질문에 잘 답변할수록</p>
              <p>더 큰 데미지를 줄 수 있습니다!</p>
            </div>
          </CardContent>
        </Card>

        {/* Opponent Selection */}
        <div className="lg:col-span-2">
          <Card>
            <CardHeader>
              <CardTitle>상대 선택</CardTitle>
              <CardDescription>도전할 상대를 선택하세요</CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 transform text-muted-foreground" />
                <Input
                  placeholder="닉네임 또는 이메일 검색..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>

              <div className="max-h-96 space-y-2 overflow-y-auto overflow-x-hidden">
                {filteredUsers.map((user) => (
                  <motion.div
                    key={user.id}
                    className="w-full"
                    whileHover={{ scale: 1.01 }}
                    whileTap={{ scale: 0.99 }}
                  >
                    <div
                      className={`cursor-pointer rounded-lg border p-4 transition-colors ${
                        selectedOpponent?.id === user.id
                          ? 'border-[color:var(--primary)]/60 bg-[color:var(--primary)]/10'
                          : 'border-[color:var(--border)]/60 bg-[color:var(--accent)]/70 hover:border-[color:var(--border)]'
                      }`}
                      onClick={() => setSelectedOpponent(user)}
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <div className="font-medium text-[color:var(--foreground)]">{user.nickname}</div>
                          <div className="text-sm text-muted-foreground">{user.email}</div>
                        </div>
                        {selectedOpponent?.id === user.id && (
                          <Badge className="border-[color:var(--primary)]/60 bg-[color:var(--primary)]/20 text-[color:var(--primary-foreground)]">
                            선택됨
                          </Badge>
                        )}
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>

              <Button
                onClick={handleStartBattle}
                disabled={!selectedOpponent}
                className="w-full"
              >
                <Swords className="w-4 h-4 mr-2" />
                전투 시작
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Battle Rules */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            📖 전투 규칙
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2 text-sm text-muted-foreground">
          <p>• 턴제 전투: 플레이어와 상대가 번갈아가며 공격합니다</p>
          <p>• 각 턴마다 포트폴리오 기반 면접 질문이 출제됩니다</p>
          <p>• 답변의 품질(0-100점)에 따라 공격력이 결정됩니다</p>
          <p>• 상대의 HP를 0으로 만들면 승리합니다!</p>
          <p>• 구체적인 경험, 기술 스택, 수치를 포함할수록 높은 점수를 받습니다</p>
        </CardContent>
      </Card>

      {/* Battle Result Dialog */}
      <Dialog open={!!battleResult} onOpenChange={(open) => !open && setBattleResult(null)}>
        <DialogContent className="max-w-3xl">
          <DialogHeader>
            <DialogTitle className="text-center text-2xl font-semibold text-[color:var(--foreground)]">
              {battleResult?.winner === 'user-1' ? (
                <span className="text-[color:var(--primary)]">🎉 승리!</span>
              ) : (
                <span className="text-destructive">💔 패배</span>
              )}
            </DialogTitle>
          </DialogHeader>

          {battleResult && (
            <div className="space-y-6">
              {/* Battle Scene */}
              <div className="relative h-48 overflow-hidden rounded-lg border border-[color:var(--border)]/60 bg-[color:var(--accent)]/60">
                <ImageWithFallback
                  src="https://images.unsplash.com/photo-1613626318906-68be0aef3334?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmaWdodGluZyUyMGdhbWUlMjBiYXR0bGV8ZW58MXx8fHwxNzYyNDc5MDg3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                  alt="Battle"
                  className="h-full w-full object-cover opacity-20"
                />
                <div className="absolute inset-0 flex flex-col items-center justify-center gap-3">
                  <Trophy className="h-16 w-16 text-[color:var(--primary)]" />
                  <div className="text-2xl font-medium text-[color:var(--foreground)]">{battleResult.winnerName} 승리!</div>
                </div>
              </div>

              {/* Battle Summary */}
              <Card>
                <CardHeader>
                  <CardTitle>전투 요약</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">무대</span>
                    <Badge className="border-[color:var(--border)]/60 bg-[color:var(--accent)]/70 text-[color:var(--foreground)]">
                      {resultStageInfo?.emoji} {resultStageInfo?.name}
                    </Badge>
                  </div>
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-muted-foreground">총 라운드</span>
                    <span className="text-[color:var(--foreground)]">{battleResult.rounds?.length || 0}</span>
                  </div>
                </CardContent>
              </Card>

              {/* Round Details */}
              {battleResult.rounds && battleResult.rounds.length > 0 && (
                <Card>
                  <CardHeader>
                    <CardTitle>라운드별 기록</CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="max-h-64 space-y-2 overflow-y-auto">
                      {battleResult.rounds.map((round: BattleRound, index: number) => (
                        <div
                          key={index}
                          className="rounded-lg border border-[color:var(--border)]/50 bg-[color:var(--accent)]/60 p-3"
                        >
                          <div className="mb-2 flex items-center justify-between">
                            <span className="text-xs text-muted-foreground">라운드 {round.roundNumber}</span>
                            <div className="flex items-center gap-2">
                              <Badge variant="secondary">{round.answerScore}점</Badge>
                              <span className="text-sm text-destructive">-{round.damage} 데미지</span>
                            </div>
                          </div>
                          <p className="text-sm text-[color:var(--foreground)]">
                            {round.attackerName} → {round.defenderName}
                          </p>
                          {round.feedback && (
                            <p className="mt-2 text-xs text-muted-foreground">{round.feedback}</p>
                          )}
                        </div>
                      ))}
                    </div>
                  </CardContent>
                </Card>
              )}

              <div className="flex gap-2">
                <Button onClick={() => setBattleResult(null)} variant="outline" className="flex-1">
                  닫기
                </Button>
                <Button onClick={() => onNavigate('ranking')} className="flex-1">
                  랭킹 보기
                </Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}
