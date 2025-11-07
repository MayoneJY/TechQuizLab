import { useState } from 'react';
import { AuthPage } from './components/AuthPage';
import { Dashboard } from './components/Dashboard';
import { PortfolioEditor } from './components/PortfolioEditor';
import { BattleArena } from './components/BattleArena';
import { Ranking } from './components/Ranking';
import { Button } from './components/ui/button';
import { Home, FileText, Swords, Trophy, LogOut } from 'lucide-react';
import { motion } from 'motion/react';

type Page = 'auth' | 'dashboard' | 'portfolio' | 'battle' | 'ranking';

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentPage, setCurrentPage] = useState<Page>('dashboard');

  const handleLogin = () => {
    setIsAuthenticated(true);
    setCurrentPage('dashboard');
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setCurrentPage('auth');
  };

  const navigate = (page: string) => {
    setCurrentPage(page as Page);
  };

  if (!isAuthenticated) {
    return <AuthPage onLogin={handleLogin} />;
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-950 via-gray-900 to-gray-950">
      {/* Navigation */}
      <nav className="bg-gray-900 border-b border-gray-800 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center gap-2">
              <span className="text-2xl">🥊</span>
              <span className="text-xl text-white">포트폴리오 전투력</span>
            </div>

            <div className="flex items-center gap-2">
              <Button
                variant={currentPage === 'dashboard' ? 'default' : 'ghost'}
                onClick={() => navigate('dashboard')}
                className="flex items-center gap-2"
              >
                <Home className="w-4 h-4" />
                <span className="hidden sm:inline">대시보드</span>
              </Button>
              <Button
                variant={currentPage === 'portfolio' ? 'default' : 'ghost'}
                onClick={() => navigate('portfolio')}
                className="flex items-center gap-2"
              >
                <FileText className="w-4 h-4" />
                <span className="hidden sm:inline">포트폴리오</span>
              </Button>
              <Button
                variant={currentPage === 'battle' ? 'default' : 'ghost'}
                onClick={() => navigate('battle')}
                className="flex items-center gap-2"
              >
                <Swords className="w-4 h-4" />
                <span className="hidden sm:inline">전투</span>
              </Button>
              <Button
                variant={currentPage === 'ranking' ? 'default' : 'ghost'}
                onClick={() => navigate('ranking')}
                className="flex items-center gap-2"
              >
                <Trophy className="w-4 h-4" />
                <span className="hidden sm:inline">랭킹</span>
              </Button>
              <Button
                variant="ghost"
                onClick={handleLogout}
                className="flex items-center gap-2 text-red-400 hover:text-red-300"
              >
                <LogOut className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <motion.div
          key={currentPage}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          {currentPage === 'dashboard' && <Dashboard onNavigate={navigate} />}
          {currentPage === 'portfolio' && <PortfolioEditor onNavigate={navigate} />}
          {currentPage === 'battle' && <BattleArena onNavigate={navigate} />}
          {currentPage === 'ranking' && <Ranking onNavigate={navigate} />}
        </motion.div>
      </main>

      {/* Footer */}
      <footer className="bg-gray-900 border-t border-gray-800 mt-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="text-center text-gray-400 text-sm">
            <p>포트폴리오 전투력 측정 프로젝트</p>
            <p className="mt-2">나의 개발 포트폴리오는 얼마나 강할까? 🥊</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
