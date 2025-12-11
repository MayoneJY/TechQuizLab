<template>
  <div class="about-screen">
    <ParticleBackground />
    <div class="stars-container"></div>
    
    <div class="about-container">
      <div class="header">
        <h1 class="pixel-text title">ABOUT</h1>
        <button class="pixel-button back-button" @click="goHome">
          ← 홈으로
        </button>
      </div>

      <div class="about-content">
        <div class="about-section">
          <h2 class="pixel-text section-title">잡스페이스란?</h2>
          <p class="about-text">
            잡스페이스는 게이미피케이션을 활용한 취업 준비용 퀴즈 게임입니다.<br/>
            픽셀 아트 스타일의 우주 게임 컨셉으로 재미있게 CS 지식을 학습할 수 있습니다.
          </p>
        </div>

        <div class="about-section">
          <h2 class="pixel-text section-title">게임 방법</h2>
          <div class="game-rules">
            <div class="rule-item">
              <span class="rule-icon">🎯</span>
              <div class="rule-content">
                <h3 class="pixel-text rule-title">주제 선택</h3>
                <p class="rule-desc">원하는 주제를 선택하고 게임을 시작하세요.</p>
              </div>
            </div>
            <div class="rule-item">
              <span class="rule-icon">✅</span>
              <div class="rule-content">
                <h3 class="pixel-text rule-title">정답 맞추기</h3>
                <p class="rule-desc">정답을 맞추면 점수 +100점을 얻습니다.</p>
              </div>
            </div>
            <div class="rule-item">
              <span class="rule-icon">❌</span>
              <div class="rule-content">
                <h3 class="pixel-text rule-title">오답 처리</h3>
                <p class="rule-desc">오답을 선택하면 생명력이 -1 감소합니다.</p>
              </div>
            </div>
            <div class="rule-item">
              <span class="rule-icon">🏆</span>
              <div class="rule-content">
                <h3 class="pixel-text rule-title">게임 완료</h3>
                <p class="rule-desc">모든 퀴즈를 완료하거나 생명력이 0이 되면 게임이 종료됩니다.</p>
              </div>
            </div>
          </div>
        </div>

        <div class="about-section">
          <h2 class="pixel-text section-title">주요 기능</h2>
          <div class="features-list">
            <div class="feature-item">
              <span class="feature-icon">📚</span>
              <span class="feature-text">다양한 CS 주제별 퀴즈</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📝</span>
              <span class="feature-text">오답 노트 기능</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🏅</span>
              <span class="feature-text">업적 시스템</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📋</span>
              <span class="feature-text">일일 미션</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">💬</span>
              <span class="feature-text">게시판 커뮤니티</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🎮</span>
              <span class="feature-text">게임화된 UI/UX</span>
            </div>
          </div>
        </div>

        <div class="about-section">
          <h2 class="pixel-text section-title">팁</h2>
          <ul class="tips-list">
            <li>정답을 모르겠다면 오답 노트에 추가해서 나중에 복습하세요!</li>
            <li>일일 미션을 완료하면 보상을 받을 수 있습니다.</li>
            <li>업적을 달성하면 경험치를 얻을 수 있습니다.</li>
            <li>게시판에서 다른 사용자들과 정보를 공유하세요.</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Spaceship decoration -->
    <div class="spaceship">
      <PixelSpaceship direction="up" />
    </div>
    
    <!-- Floating monsters -->
    <div class="monster monster-1">
      <PixelMonster type="alien" />
    </div>
    <div class="monster monster-2">
      <PixelMonster type="alien" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import ParticleBackground from '../components/ParticleBackground.vue'
import PixelSpaceship from '../components/PixelSpaceship.vue'
import PixelMonster from '../components/PixelMonster.vue'

const router = useRouter()

function goHome() {
  router.push('/')
}
</script>

<style scoped>
.about-screen {
  min-height: 100vh;
  position: relative;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  animation: screen-enter 0.8s ease-out;
}

.about-container {
  width: 100%;
  max-width: 900px;
  z-index: 10;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  flex-wrap: wrap;
  gap: 20px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #ffd43b;
  text-shadow: 
    4px 4px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
  margin: 0;
}

.back-button {
  font-size: 14px;
  padding: 12px 24px;
}

.about-content {
  background: rgba(0, 0, 0, 0.7);
  border: 4px solid #fff;
  border-radius: 8px;
  padding: 40px;
}

.about-section {
  margin-bottom: 40px;
}

.about-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #ffd43b;
  margin-bottom: 20px;
  text-shadow: 
    3px 3px 0 #000,
    -1px -1px 0 #000,
    1px -1px 0 #000,
    -1px 1px 0 #000;
}

.about-text {
  font-size: 16px;
  line-height: 1.8;
  color: #fff;
  word-break: keep-all;
}

.game-rules {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.rule-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border: 3px solid #666;
  border-radius: 8px;
  transition: all 0.3s;
}

.rule-item:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
}

.rule-icon {
  font-size: 40px;
  flex-shrink: 0;
  filter: drop-shadow(2px 2px 0 #000);
}

.rule-content {
  flex: 1;
}

.rule-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  margin-bottom: 8px;
}

.rule-desc {
  font-size: 14px;
  color: #ccc;
  line-height: 1.6;
}

.features-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.1);
  border: 2px solid #666;
  border-radius: 6px;
  transition: all 0.3s;
}

.feature-item:hover {
  border-color: #4a9eff;
  background: rgba(255, 255, 255, 0.15);
}

.feature-icon {
  font-size: 24px;
  filter: drop-shadow(2px 2px 0 #000);
}

.feature-text {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

.tips-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tips-list li {
  padding: 15px 20px;
  margin-bottom: 10px;
  background: rgba(255, 212, 59, 0.1);
  border: 2px solid #ffd43b;
  border-radius: 6px;
  color: #fff;
  font-size: 14px;
  line-height: 1.6;
  position: relative;
  padding-left: 50px;
}

.tips-list li::before {
  content: '💡';
  position: absolute;
  left: 15px;
  font-size: 20px;
  filter: drop-shadow(2px 2px 0 #000);
}

.tips-list li:last-child {
  margin-bottom: 0;
}

.stars-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
}

.spaceship {
  position: fixed;
  bottom: 50px;
  right: 50px;
  animation: float 3s ease-in-out infinite;
  z-index: 5;
}

.monster {
  position: fixed;
  z-index: 3;
  animation: float 2s ease-in-out infinite;
}

.monster-1 {
  top: 100px;
  left: 50px;
  animation-delay: 0s;
}

.monster-2 {
  top: 200px;
  right: 100px;
  animation-delay: 1s;
}

@keyframes screen-enter {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@media (max-width: 768px) {
  .title {
    font-size: 24px;
  }
  
  .about-content {
    padding: 25px;
  }
  
  .section-title {
    font-size: 20px;
  }
  
  .features-list {
    grid-template-columns: 1fr;
  }
  
  .rule-item {
    flex-direction: column;
    text-align: center;
  }
}
</style>

