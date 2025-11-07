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
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl text-white mb-2 flex items-center gap-3">
            <Swords className="w-8 h-8" />
            전투 아레나
          </h1>
          <p className="text-gray-400">상대를 선택하고 면접 질문으로 전투하세요</p>
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
            <div className="mt-4 text-center text-sm text-gray-400">
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
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
                <Input
                  placeholder="닉네임 또는 이메일 검색..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="pl-10"
                />
              </div>

              <div className="space-y-2 max-h-96 overflow-y-auto">
                {filteredUsers.map((user) => (
                  <motion.div
                    key={user.id}
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    <div
                      className={`p-4 rounded-lg border cursor-pointer transition-colors ${
                        selectedOpponent?.id === user.id
                          ? 'bg-blue-950 border-blue-600'
                          : 'bg-gray-800 border-gray-700 hover:bg-gray-750'
                      }`}
                      onClick={() => setSelectedOpponent(user)}
                    >
                      <div className="flex items-center justify-between">
                        <div>
                          <div className="text-white">{user.nickname}</div>
                          <div className="text-sm text-gray-400">{user.email}</div>
                        </div>
                        {selectedOpponent?.id === user.id && (
                          <Badge className="bg-blue-600">선택됨</Badge>
                        )}
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>

              <Button
                onClick={handleStartBattle}
                disabled={!selectedOpponent}
                className="w-full bg-red-600 hover:bg-red-700"
              >
                <Swords className="w-4 h-4 mr-2" />
                전투 시작
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>

      {/* Battle Rules */}
      <Card className="bg-gradient-to-r from-purple-950 to-blue-950 border-purple-700">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            📖 전투 규칙
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-2 text-gray-300">
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
            <DialogTitle className="text-center text-2xl">
              {battleResult?.winner === 'user-1' ? (
                <span className="text-green-400">🎉 승리!</span>
              ) : (
                <span className="text-red-400">💔 패배</span>
              )}
            </DialogTitle>
          </DialogHeader>

          {battleResult && (
            <div className="space-y-6">
              {/* Battle Scene */}
              <div className="relative h-48 bg-gradient-to-r from-blue-900 via-purple-900 to-red-900 rounded-lg overflow-hidden">
                <ImageWithFallback
                  src="https://images.unsplash.com/photo-1613626318906-68be0aef3334?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHxmaWdodGluZyUyMGdhbWUlMjBiYXR0bGV8ZW58MXx8fHwxNzYyNDc5MDg3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                  alt="Battle"
                  className="w-full h-full object-cover opacity-30"
                />
                <div className="absolute inset-0 flex flex-col items-center justify-center">
                  <Trophy className="w-20 h-20 text-yellow-400 mb-4" />
                  <div className="text-2xl text-white">{battleResult.winnerName} 승리!</div>
                </div>
              </div>

              {/* Battle Summary */}
              <Card>
                <CardHeader>
                  <CardTitle>전투 요약</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400">무대</span>
                    <Badge className={STAGE_INFO[battleResult.stage].color}>
                      {STAGE_INFO[battleResult.stage].emoji} {STAGE_INFO[battleResult.stage].name}
                    </Badge>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400">총 라운드</span>
                    <span className="text-white">{battleResult.rounds?.length || 0}</span>
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
                    <div className="space-y-2 max-h-64 overflow-y-auto">
                      {battleResult.rounds.map((round: BattleRound, index: number) => (
                        <div key={index} className="p-3 bg-gray-800 rounded-lg">
                          <div className="flex items-center justify-between mb-2">
                            <span className="text-sm text-gray-400">라운드 {round.roundNumber}</span>
                            <div className="flex items-center gap-2">
                              <Badge variant="secondary">{round.answerScore}점</Badge>
                              <span className="text-red-400">-{round.damage} 데미지</span>
                            </div>
                          </div>
                          <p className="text-sm text-gray-300">{round.attackerName} → {round.defenderName}</p>
                          {round.feedback && (
                            <p className="text-xs text-gray-400 mt-2">{round.feedback}</p>
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
