import { motion } from 'motion/react';

interface PowerMeterProps {
  score: number;
  maxScore?: number;
  label?: string;
  size?: 'sm' | 'md' | 'lg';
}

export function PowerMeter({ score, maxScore = 10000, label, size = 'md' }: PowerMeterProps) {
  const percentage = Math.min((score / maxScore) * 100, 100);
  
  const sizes = {
    sm: { outer: 80, strokeWidth: 8 },
    md: { outer: 120, strokeWidth: 12 },
    lg: { outer: 160, strokeWidth: 16 }
  } as const;
  
  const { outer, strokeWidth } = sizes[size];
  const radius = (outer - strokeWidth) / 2;
  const circumference = 2 * Math.PI * radius;
  const strokeDashoffset = circumference - (percentage / 100) * circumference;
  
  const getColor = (pct: number) => {
    if (pct >= 90) return '#10B981'; // green
    if (pct >= 75) return '#3B82F6'; // blue
    if (pct >= 50) return '#F59E0B'; // orange
    return '#EF4444'; // red
  };
  
  return (
    <div className="flex flex-col items-center gap-2">
      <div className="relative" style={{ width: outer, height: outer }}>
        {/* Background circle */}
        <svg width={outer} height={outer} className="transform -rotate-90">
          <circle
            cx={outer / 2}
            cy={outer / 2}
            r={radius}
            fill="none"
            stroke="rgba(148, 163, 184, 0.2)"
            strokeWidth={strokeWidth}
          />
          <motion.circle
            cx={outer / 2}
            cy={outer / 2}
            r={radius}
            fill="none"
            stroke={getColor(percentage)}
            strokeWidth={strokeWidth}
            strokeDasharray={circumference}
            strokeDashoffset={circumference}
            strokeLinecap="round"
            animate={{ strokeDashoffset }}
            transition={{ duration: 1, ease: "easeOut" }}
          />
        </svg>
        
        {/* Center text */}
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{ delay: 0.5, type: "spring" }}
            className="text-center"
          >
            <div className={`${size === 'lg' ? 'text-2xl' : size === 'md' ? 'text-xl' : ''}`} style={{ color: getColor(percentage) }}>
              {score.toLocaleString()}
            </div>
            {size === 'lg' && (
              <div className="text-xs text-muted-foreground">전투력</div>
            )}
          </motion.div>
        </div>
      </div>
      
      {label && (
        <div className="text-center text-sm text-muted-foreground">{label}</div>
      )}
    </div>
  );
}
