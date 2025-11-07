import { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Stage, STAGE_INFO, ScoreSnapshot, Battle } from '../types';
import { scoreApi, battleApi } from '../lib/mockApi';
import { PowerMeter } from './PowerMeter';
import { PowerRadarChart } from './PowerRadarChart';
import { Skeleton } from './ui/skeleton';
import { Trophy, Target, TrendingUp, AlertCircle } from 'lucide-react';
import { motion } from 'motion/react';

interface DashboardProps {
  onNavigate: (page: string) => void;
}

export function Dashboard({ onNavigate }: DashboardProps) {
  const [selectedStage, setSelectedStage] = useState<Stage>('STARTUP');
  const [scores, setScores] = useState<Record<Stage, ScoreSnapshot> | null>(null);
  const [recentBattles, setRecentBattles] = useState<Battle[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadData = useCallback(async () => {
    setIsLoading(true);
    try {
      const [allScores, battles] = await Promise.all([
        scoreApi.getAllMyScores(),
        battleApi.getHistory()
      ]);
      setScores(allScores);
      setRecentBattles(battles.slice(0, 3));
    } catch (error) {
      console.error('Failed to load data', error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadData();
  }, [loadData]);

  if (isLoading) {
    return (
      <div className="space-y-6">
        <Skeleton className="h-40 w-full" />
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Skeleton className="h-96" />
          <Skeleton className="h-96" />
        </div>
      </div>
    );
  }

  const currentScore = scores?.[selectedStage];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="mb-2 text-3xl font-semibold text-[color:var(--foreground)]">전투력 대시보드</h1>
          <p className="text-sm text-muted-foreground">무대를 선택하여 전투력을 확인하세요</p>
        </div>
        <div className="flex gap-2">
          <Button onClick={() => onNavigate('portfolio')} variant="outline">
            포트폴리오 수정
          </Button>
          <Button onClick={() => onNavigate('battle')}>
            전투 시작
          </Button>
        </div>
      </div>

      {/* Stage Selector */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
        {(['BANK', 'SME', 'MID', 'STARTUP'] as Stage[]).map((stage) => {
          const info = STAGE_INFO[stage];
          const isSelected = selectedStage === stage;
          
          return (
            <motion.div
              key={stage}
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
            >
              <Card
                className={`cursor-pointer border border-[color:var(--border)]/60 transition-all ${
                  isSelected
                    ? 'bg-[color:var(--primary)]/15 ring-1 ring-[color:var(--primary)]/70'
                    : 'bg-[color:var(--accent)]/60 hover:bg-[color:var(--accent)]/80'
                }`}
                onClick={() => setSelectedStage(stage)}
              >
                <CardContent className="p-4">
                  <div className="flex items-center gap-3">
                    <span className="text-3xl">{info.emoji}</span>
                    <div className="flex-1">
                      <div className="font-medium text-[color:var(--foreground)]">{info.name}</div>
                      <div className="text-sm text-muted-foreground">
                        {scores?.[stage]?.totalScore.toLocaleString()}
                      </div>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          );
        })}
      </div>

      {/* Main Stats */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Power Meter & Stats */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
              <Target className="h-5 w-5 text-[color:var(--primary)]" />
              {STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name} 전투력
            </CardTitle>
            <CardDescription>{STAGE_INFO[selectedStage].description}</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex flex-col items-center gap-6">
              <PowerMeter score={currentScore?.totalScore || 0} size="lg" />
              
              <div className="w-full space-y-3">
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">기술력</span>
                  <Badge variant="secondary">{currentScore?.techDepth}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">서비스 영향</span>
                  <Badge variant="secondary">{currentScore?.serviceImpact}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">확장성</span>
                  <Badge variant="secondary">{currentScore?.scalability}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">협업</span>
                  <Badge variant="secondary">{currentScore?.collaboration}</Badge>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-sm text-muted-foreground">주도성</span>
                  <Badge variant="secondary">{currentScore?.originality}</Badge>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Radar Chart */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
              <TrendingUp className="h-5 w-5 text-[color:var(--primary)]" />
              능력치 분석
            </CardTitle>
          </CardHeader>
          <CardContent>
            <PowerRadarChart
              techDepth={currentScore?.techDepth || 0}
              serviceImpact={currentScore?.serviceImpact || 0}
              scalability={currentScore?.scalability || 0}
              collaboration={currentScore?.collaboration || 0}
              originality={currentScore?.originality || 0}
            />
          </CardContent>
        </Card>
      </div>

      {/* Highlights & Risks */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card className="border-[color:var(--border)]/60 bg-[color:var(--accent)]/60">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
              <Trophy className="h-5 w-5 text-[color:var(--primary)]" />
              강점
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ul className="space-y-2">
              {currentScore?.highlights.map((highlight: string, index: number) => (
                <li key={index} className="flex items-start gap-2 text-sm text-[color:var(--foreground)]">
                  <span className="mt-1 text-[color:var(--primary)]">✓</span>
                  <span>{highlight}</span>
                </li>
              ))}
            </ul>
          </CardContent>
        </Card>

        <Card className="border-[color:var(--border)]/60 bg-[color:var(--accent)]/60">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
              <AlertCircle className="h-5 w-5 text-destructive" />
              보완점
            </CardTitle>
          </CardHeader>
          <CardContent>
            <ul className="space-y-2">
              {currentScore?.risks.map((risk: string, index: number) => (
                <li key={index} className="flex items-start gap-2 text-sm text-[color:var(--foreground)]">
                  <span className="mt-1 text-destructive">!</span>
                  <span>{risk}</span>
                </li>
              ))}
            </ul>
          </CardContent>
        </Card>
      </div>

      {/* Recent Battles */}
      <Card>
        <CardHeader>
          <CardTitle>최근 전투 기록</CardTitle>
        </CardHeader>
        <CardContent>
          {recentBattles.length === 0 ? (
            <p className="py-8 text-center text-sm text-muted-foreground">아직 전투 기록이 없습니다</p>
          ) : (
            <div className="space-y-3">
              {recentBattles.map((battle) => (
                <div
                  key={battle.id}
                  className="flex items-center justify-between gap-4 rounded-lg border border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 p-3"
                >
                  <div className="flex items-center gap-3">
                    <span className="text-2xl">{STAGE_INFO[battle.stage].emoji}</span>
                    <div>
                      <div className="text-sm text-[color:var(--foreground)]">
                        {battle.challengerName} vs {battle.opponentName}
                      </div>
                      <div className="text-xs text-muted-foreground">
                        {new Date(battle.createdAt).toLocaleDateString()}
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <div className="text-sm text-[color:var(--foreground)]">
                      {battle.challengerScore} : {battle.opponentScore}
                    </div>
                    <Badge variant={battle.winnerUserId === 'user-1' ? 'default' : 'secondary'}>
                      {battle.winnerUserId === 'user-1' ? '승리' : '패배'}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
