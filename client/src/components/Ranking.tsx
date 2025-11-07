import { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Stage, STAGE_INFO, RankingEntry } from '../types';
import { rankingApi } from '../lib/mockApi';
import { StageSelector } from './StageSelector';
import { Trophy, Medal, Award, TrendingUp, Loader2 } from 'lucide-react';
import { motion } from 'motion/react';
import { ImageWithFallback } from './figma/ImageWithFallback';

interface RankingProps {
  onNavigate: (page: string) => void;
}

export function Ranking({ onNavigate }: RankingProps) {
  const [selectedStage, setSelectedStage] = useState<Stage>('STARTUP');
  const [rankings, setRankings] = useState<RankingEntry[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const loadRankings = useCallback(async () => {
    setIsLoading(true);
    try {
      const data = await rankingApi.get(selectedStage);
      setRankings(data);
    } catch (error) {
      console.error('Failed to load rankings', error);
    } finally {
      setIsLoading(false);
    }
  }, [selectedStage]);

  useEffect(() => {
    void loadRankings();
  }, [loadRankings]);

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Trophy className="w-6 h-6 text-yellow-400" />;
      case 2:
        return <Medal className="h-6 w-6 text-muted-foreground" />;
      case 3:
        return <Award className="w-6 h-6 text-orange-600" />;
      default:
        return (
          <div className="flex h-6 w-6 items-center justify-center text-muted-foreground">
            #{rank}
          </div>
        );
    }
  };

  const getRankBgColor = (rank: number) => {
    switch (rank) {
      case 1:
        return 'border-[color:var(--primary)]/60 bg-[color:var(--primary)]/12';
      case 2:
        return 'border-[color:var(--border)]/70 bg-[color:var(--accent)]/70';
      case 3:
        return 'border-destructive/50 bg-destructive/10';
      default:
        return 'border-[color:var(--border)]/60 bg-[color:var(--accent)]/50';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="mb-2 flex items-center gap-3 text-3xl font-semibold text-[color:var(--foreground)]">
            <Trophy className="h-8 w-8 text-[color:var(--primary)]" />
            랭킹
          </h1>
          <p className="text-sm text-muted-foreground">무대별 최강자를 확인하세요</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={() => onNavigate('battle')} variant="outline">
            전투하기
          </Button>
          <Button onClick={() => onNavigate('dashboard')}>
            대시보드
          </Button>
        </div>
      </div>

      {/* Stage Selection */}
      <Card>
        <CardHeader>
          <CardTitle>무대 선택</CardTitle>
          <CardDescription>무대별 랭킹을 확인하세요</CardDescription>
        </CardHeader>
        <CardContent>
          <StageSelector selectedStage={selectedStage} onSelectStage={setSelectedStage} />
        </CardContent>
      </Card>

      {/* Podium - Top 3 */}
      {!isLoading && rankings.length >= 3 && (
        <Card>
          <CardContent className="pt-6">
            <div className="relative h-64">
              <ImageWithFallback
                src="https://images.unsplash.com/photo-1659277319138-bc5ea0b3377f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cm9waHklMjB3aW5uZXIlMjBwb2RpdW18ZW58MXx8fHwxNzYyNDc5MDg3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                alt="Podium"
                className="absolute inset-0 h-full w-full rounded-lg object-cover opacity-10"
              />
              <div className="relative flex h-full items-end justify-center gap-4 pb-8">
                {/* 2nd Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.2 }}
                  className="flex flex-col items-center"
                >
                  <div className="mb-2 text-center">
                    <Medal className="mx-auto mb-2 h-10 w-10 text-muted-foreground" />
                    <div className="text-[color:var(--foreground)]">{rankings[1].nickname}</div>
                    <div className="text-sm text-muted-foreground">
                      {rankings[1].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="flex h-20 w-24 items-center justify-center rounded-t-lg border border-[color:var(--border)]/60 bg-[color:var(--accent)]/60">
                    <span className="text-2xl font-semibold text-[color:var(--foreground)]">2</span>
                  </div>
                </motion.div>

                {/* 1st Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.1 }}
                  className="flex flex-col items-center"
                >
                  <div className="mb-2 text-center">
                    <Trophy className="mx-auto mb-2 h-12 w-12 text-[color:var(--primary)]" />
                    <div className="text-lg font-semibold text-[color:var(--foreground)]">{rankings[0].nickname}</div>
                    <div className="text-[color:var(--primary)]">
                      {rankings[0].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="flex h-32 w-28 items-center justify-center rounded-t-lg border border-[color:var(--primary)]/60 bg-[color:var(--primary)]/15">
                    <span className="text-3xl font-semibold text-[color:var(--primary)]">1</span>
                  </div>
                </motion.div>

                {/* 3rd Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.3 }}
                  className="flex flex-col items-center"
                >
                  <div className="mb-2 text-center">
                    <Award className="mx-auto mb-2 h-10 w-10 text-destructive" />
                    <div className="text-[color:var(--foreground)]">{rankings[2].nickname}</div>
                    <div className="text-sm text-muted-foreground">
                      {rankings[2].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="flex h-16 w-24 items-center justify-center rounded-t-lg border border-destructive/40 bg-destructive/10">
                    <span className="text-2xl font-semibold text-destructive">3</span>
                  </div>
                </motion.div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Full Rankings */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
            <TrendingUp className="h-5 w-5 text-[color:var(--primary)]" />
            전체 랭킹
          </CardTitle>
          <CardDescription>
            {STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name} 무대
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-12 text-muted-foreground">
              <Loader2 className="mb-3 h-8 w-8 animate-spin text-[color:var(--primary)]" />
              랭킹 데이터를 불러오는 중입니다...
            </div>
          ) : (
            <div className="space-y-2">
              {rankings.map((entry, index) => (
                <motion.div
                  key={entry.userId}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                >
                  <div
                    className={`flex items-center justify-between rounded-lg border p-4 transition-colors ${getRankBgColor(
                      entry.rank
                    )} ${entry.userId === 'user-1' ? 'ring-2 ring-[color:var(--primary)]/60' : ''}`}
                  >
                    <div className="flex flex-1 items-center gap-4">
                      <div className="flex w-12 items-center justify-center">
                        {getRankIcon(entry.rank)}
                      </div>
                      
                      <div className="flex-1">
                        <div className="flex items-center gap-2 text-[color:var(--foreground)]">
                          <span className="font-medium">{entry.nickname}</span>
                          {entry.userId === 'user-1' && (
                            <Badge
                              variant="outline"
                              className="border-[color:var(--primary)]/60 text-[color:var(--primary)]"
                            >
                              나
                            </Badge>
                          )}
                        </div>
                        <div className="mt-1 text-sm text-muted-foreground">
                          총점 {entry.totalScore.toLocaleString()} · 승 {entry.wins} · 패 {entry.losses}
                        </div>
                      </div>
                    </div>
                    <Badge className="border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 text-[color:var(--foreground)]">
                      {entry.rank} 위
                    </Badge>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Stage Info Card */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
            {STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name} 무대 특징
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">{STAGE_INFO[selectedStage].description}</p>
          {!isLoading && rankings.length > 0 && (
            <div className="mt-4 flex flex-wrap gap-2 text-sm">
              <Badge variant="outline" className="border-[color:var(--border)]/60">
                총 {rankings.length}명 참여
              </Badge>
              <Badge variant="outline" className="border-[color:var(--border)]/60">
                최고 점수: {rankings[0]?.totalScore.toLocaleString()}
              </Badge>
              <Badge variant="outline" className="border-[color:var(--border)]/60">
                평균 점수:{' '}
                {Math.round(
                  rankings.reduce((sum, r) => sum + r.totalScore, 0) / rankings.length
                ).toLocaleString()}
              </Badge>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
