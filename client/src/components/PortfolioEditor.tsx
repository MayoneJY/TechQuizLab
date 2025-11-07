import { useState, useEffect, useCallback } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Textarea } from './ui/textarea';
import { Label } from './ui/label';
import { Badge } from './ui/badge';
import { Stage, STAGE_INFO, ScoreSnapshot } from '../types';
import { portfolioApi, scoreApi } from '../lib/mockApi';
import { StageSelector } from './StageSelector';
import { PowerMeter } from './PowerMeter';
import { Loader2, Save, Sparkles } from 'lucide-react';
import { motion } from 'motion/react';

interface PortfolioEditorProps {
  onNavigate: (page: string) => void;
}

export function PortfolioEditor({ onNavigate }: PortfolioEditorProps) {
  const [content, setContent] = useState('');
  const [selectedStage, setSelectedStage] = useState<Stage>('STARTUP');
  const [isSaving, setIsSaving] = useState(false);
  const [isMeasuring, setIsMeasuring] = useState(false);
  const [lastScore, setLastScore] = useState<ScoreSnapshot | null>(null);
  const [showResult, setShowResult] = useState(false);

  const loadPortfolio = useCallback(async () => {
    try {
      const portfolio = await portfolioApi.get();
      setContent(portfolio.content);
    } catch (error) {
      console.error('Failed to load portfolio', error);
    }
  }, []);

  useEffect(() => {
    void loadPortfolio();
  }, [loadPortfolio]);

  const handleSave = async () => {
    setIsSaving(true);
    try {
      await portfolioApi.save(content);
      alert('포트폴리오가 저장되었습니다!');
    } catch (error) {
      alert('저장 실패');
    } finally {
      setIsSaving(false);
    }
  };

  const handleMeasure = async () => {
    if (!content.trim()) {
      alert('포트폴리오를 작성해주세요!');
      return;
    }

    setIsMeasuring(true);
    setShowResult(false);
    
    try {
      // First save the portfolio
      await portfolioApi.save(content);
      
      // Then measure the score
      const score = await scoreApi.measure(selectedStage);
      setLastScore(score);
      setShowResult(true);
    } catch (error) {
      alert('전투력 측정 실패');
    } finally {
      setIsMeasuring(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="mb-2 text-3xl font-semibold text-[color:var(--foreground)]">포트폴리오 작성</h1>
          <p className="text-sm text-muted-foreground">포트폴리오를 작성하고 AI로 전투력을 측정하세요</p>
        </div>
        <Button onClick={() => onNavigate('dashboard')} variant="outline">
          대시보드로
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Editor */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>포트폴리오 내용</CardTitle>
              <CardDescription>
                프로젝트 경험, 기술 스택, 성과 등을 자유롭게 작성하세요. Markdown 형식을 지원합니다.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Textarea
                value={content}
                onChange={(e) => setContent(e.target.value)}
                placeholder="# 포트폴리오

## 주요 프로젝트

### 1. 프로젝트명
- 기술 스택: React, Node.js, MongoDB
- 성과: 사용자 10만명 돌파
- 역할: 프론트엔드 개발 리드

..."
                className="min-h-[500px] font-mono text-sm"
              />
              <div className="flex gap-2 mt-4">
                <Button onClick={handleSave} disabled={isSaving} variant="outline" className="flex-1">
                  {isSaving ? (
                    <>
                      <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                      저장 중...
                    </>
                  ) : (
                    <>
                      <Save className="w-4 h-4 mr-2" />
                      저장
                    </>
                  )}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Measurement Panel */}
        <div className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-[color:var(--foreground)]">
                <Sparkles className="h-5 w-5 text-[color:var(--primary)]" />
                전투력 측정
              </CardTitle>
              <CardDescription>
                무대를 선택하고 AI로 전투력을 측정하세요
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label>무대 선택</Label>
                <StageSelector
                  selectedStage={selectedStage}
                  onSelectStage={setSelectedStage}
                />
              </div>

              <Button onClick={handleMeasure} disabled={isMeasuring} className="w-full">
                {isMeasuring ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    AI 분석 중...
                  </>
                ) : (
                  <>
                    <Sparkles className="mr-2 h-4 w-4" />
                    전투력 측정하기
                  </>
                )}
              </Button>

              {isMeasuring && (
                <div className="space-y-2 py-4 text-center">
                  <div className="text-sm text-muted-foreground">
                    ChatGPT가 포트폴리오를 분석하고 있습니다...
                  </div>
                  <div className="flex justify-center gap-2">
                    <motion.div
                      className="h-2 w-2 rounded-full bg-[color:var(--primary)]"
                      animate={{ scale: [1, 1.5, 1] }}
                      transition={{ repeat: Infinity, duration: 1, delay: 0 }}
                    />
                    <motion.div
                      className="h-2 w-2 rounded-full bg-[color:var(--primary)]"
                      animate={{ scale: [1, 1.5, 1] }}
                      transition={{ repeat: Infinity, duration: 1, delay: 0.2 }}
                    />
                    <motion.div
                      className="h-2 w-2 rounded-full bg-[color:var(--primary)]"
                      animate={{ scale: [1, 1.5, 1] }}
                      transition={{ repeat: Infinity, duration: 1, delay: 0.4 }}
                    />
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Result */}
          {showResult && lastScore && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5 }}
            >
              <Card>
                <CardHeader>
                  <CardTitle className="text-center text-[color:var(--foreground)]">측정 완료!</CardTitle>
                  <CardDescription className="text-center text-muted-foreground">
                    {STAGE_INFO[lastScore.stage].emoji} {STAGE_INFO[lastScore.stage].name} 무대
                  </CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="flex justify-center">
                    <PowerMeter score={lastScore.totalScore} size="lg" />
                  </div>

                  <div className="space-y-2 text-sm">
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">기술력</span>
                      <Badge variant="secondary">{lastScore.techDepth}</Badge>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">서비스 영향</span>
                      <Badge variant="secondary">{lastScore.serviceImpact}</Badge>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">확장성</span>
                      <Badge variant="secondary">{lastScore.scalability}</Badge>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">협업</span>
                      <Badge variant="secondary">{lastScore.collaboration}</Badge>
                    </div>
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground">주도성</span>
                      <Badge variant="secondary">{lastScore.originality}</Badge>
                    </div>
                  </div>

                  <Button onClick={() => onNavigate('dashboard')} variant="outline" className="w-full">
                    대시보드에서 확인
                  </Button>
                </CardContent>
              </Card>
            </motion.div>
          )}
        </div>
      </div>
    </div>
  );
}
