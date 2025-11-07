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
        return <Medal className="w-6 h-6 text-gray-400" />;
      case 3:
        return <Award className="w-6 h-6 text-orange-600" />;
      default:
        return <div className="w-6 h-6 flex items-center justify-center text-gray-400">#{rank}</div>;
    }
  };

  const getRankBgColor = (rank: number) => {
    switch (rank) {
      case 1:
        return 'bg-gradient-to-r from-yellow-900/50 to-yellow-800/50 border-yellow-600';
      case 2:
        return 'bg-gradient-to-r from-gray-800/50 to-gray-700/50 border-gray-500';
      case 3:
        return 'bg-gradient-to-r from-orange-900/50 to-orange-800/50 border-orange-600';
      default:
        return 'bg-gray-800 border-gray-700';
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div>
          <h1 className="text-3xl text-white mb-2 flex items-center gap-3">
            <Trophy className="w-8 h-8 text-yellow-400" />
            랭킹
          </h1>
          <p className="text-gray-400">무대별 최강자를 확인하세요</p>
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
        <Card className="bg-gradient-to-br from-gray-900 to-gray-800 border-gray-700">
          <CardContent className="pt-6">
            <div className="relative h-64">
              <ImageWithFallback
                src="https://images.unsplash.com/photo-1659277319138-bc5ea0b3377f?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w3Nzg4Nzd8MHwxfHNlYXJjaHwxfHx0cm9waHklMjB3aW5uZXIlMjBwb2RpdW18ZW58MXx8fHwxNzYyNDc5MDg3fDA&ixlib=rb-4.1.0&q=80&w=1080&utm_source=figma&utm_medium=referral"
                alt="Podium"
                className="absolute inset-0 w-full h-full object-cover opacity-20 rounded-lg"
              />
              <div className="relative flex items-end justify-center h-full gap-4 pb-8">
                {/* 2nd Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.2 }}
                  className="flex flex-col items-center"
                >
                  <div className="text-center mb-2">
                    <Medal className="w-10 h-10 text-gray-400 mx-auto mb-2" />
                    <div className="text-white">{rankings[1].nickname}</div>
                    <div className="text-sm text-gray-400">
                      {rankings[1].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="w-24 h-20 bg-gradient-to-t from-gray-600 to-gray-500 rounded-t-lg flex items-center justify-center">
                    <span className="text-2xl">2</span>
                  </div>
                </motion.div>

                {/* 1st Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.1 }}
                  className="flex flex-col items-center"
                >
                  <div className="text-center mb-2">
                    <Trophy className="w-12 h-12 text-yellow-400 mx-auto mb-2" />
                    <div className="text-white text-lg">{rankings[0].nickname}</div>
                    <div className="text-yellow-400">
                      {rankings[0].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="w-28 h-32 bg-gradient-to-t from-yellow-600 to-yellow-500 rounded-t-lg flex items-center justify-center">
                    <span className="text-3xl">1</span>
                  </div>
                </motion.div>

                {/* 3rd Place */}
                <motion.div
                  initial={{ y: 50, opacity: 0 }}
                  animate={{ y: 0, opacity: 1 }}
                  transition={{ delay: 0.3 }}
                  className="flex flex-col items-center"
                >
                  <div className="text-center mb-2">
                    <Award className="w-10 h-10 text-orange-600 mx-auto mb-2" />
                    <div className="text-white">{rankings[2].nickname}</div>
                    <div className="text-sm text-gray-400">
                      {rankings[2].totalScore.toLocaleString()}
                    </div>
                  </div>
                  <div className="w-24 h-16 bg-gradient-to-t from-orange-700 to-orange-600 rounded-t-lg flex items-center justify-center">
                    <span className="text-2xl">3</span>
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
          <CardTitle className="flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            전체 랭킹
          </CardTitle>
          <CardDescription>
            {STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name} 무대
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="flex flex-col items-center justify-center py-12 text-gray-400">
              <Loader2 className="w-8 h-8 animate-spin mb-3" />
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
                    className={`flex items-center justify-between p-4 rounded-lg border ${getRankBgColor(
                      entry.rank
                    )} ${entry.userId === 'user-1' ? 'ring-2 ring-blue-500' : ''}`}
                  >
                    <div className="flex items-center gap-4 flex-1">
                      <div className="flex items-center justify-center w-12">
                        {getRankIcon(entry.rank)}
                      </div>
                      
                      <div className="flex-1">
                        <div className="flex items-center gap-2">
                          <span className="text-white">{entry.nickname}</span>
                          {entry.userId === 'user-1' && (
                            <Badge variant="outline" className="border-blue-500 text-blue-500">
                              나
                            </Badge>
                          )}
                        </div>
                        <div className="text-sm text-gray-400 mt-1">
                          총점 {entry.totalScore.toLocaleString()} · 승 {entry.wins} · 패 {entry.losses}
                        </div>
                      </div>
                    </div>
                    <Badge className={STAGE_INFO[selectedStage].color}>{entry.rank} 위</Badge>
                  </div>
                </motion.div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Stage Info Card */}
      <Card className={`${STAGE_INFO[selectedStage].color} bg-opacity-20 border-opacity-50`}>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            {STAGE_INFO[selectedStage].emoji} {STAGE_INFO[selectedStage].name} 무대 특징
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-gray-300">{STAGE_INFO[selectedStage].description}</p>
          {!isLoading && rankings.length > 0 && (
            <div className="mt-4 flex flex-wrap gap-2">
              <Badge variant="outline">총 {rankings.length}명 참여</Badge>
              <Badge variant="outline">최고 점수: {rankings[0]?.totalScore.toLocaleString()}</Badge>
              <Badge variant="outline">
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
