import { Stage, STAGE_INFO } from '../types';
import { Button } from './ui/button';
import { motion } from 'motion/react';

interface StageSelectorProps {
  selectedStage: Stage;
  onSelectStage: (stage: Stage) => void;
}

export function StageSelector({ selectedStage, onSelectStage }: StageSelectorProps) {
  const stages: Stage[] = ['BANK', 'SME', 'MID', 'STARTUP'];
  
  return (
    <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
      {stages.map((stage) => {
        const info = STAGE_INFO[stage];
        const isSelected = selectedStage === stage;
        
        return (
          <motion.div
            key={stage}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            <Button
              variant={isSelected ? 'default' : 'outline'}
              className={`w-full h-auto p-4 flex flex-col items-center gap-2 ${
                isSelected ? info.color + ' hover:opacity-90' : ''
              }`}
              onClick={() => onSelectStage(stage)}
            >
              <span className="text-2xl">{info.emoji}</span>
              <div className="text-center">
                <div className={isSelected ? '' : ''}>{info.name}</div>
                <div className="text-xs opacity-70 mt-1">{info.description}</div>
              </div>
            </Button>
          </motion.div>
        );
      })}
    </div>
  );
}
