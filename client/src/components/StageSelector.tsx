import { Stage, STAGE_INFO } from '../types';
import { cn } from './ui/utils';

interface StageSelectorProps {
  selectedStage: Stage;
  onSelectStage: (stage: Stage) => void;
}

export function StageSelector({ selectedStage, onSelectStage }: StageSelectorProps) {
  const stages: Stage[] = ['BANK', 'SME', 'MID', 'STARTUP'];

  return (
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
      {stages.map((stage) => {
        const info = STAGE_INFO[stage];
        const isSelected = selectedStage === stage;

        return (
          <button
            key={stage}
            type="button"
            className={cn(
              'relative flex h-full min-h-[112px] w-full flex-col items-start justify-between gap-2 rounded-xl border px-4 py-4 text-left transition-colors',
              isSelected
                ? 'border-[color:var(--primary)] text-[color:var(--primary-foreground)] ring-1 ring-[color:var(--primary)]/60'
                : 'border-[color:var(--border)]/60 bg-[color:var(--accent)]/60 text-[color:var(--foreground)] hover:border-[color:var(--border)] hover:bg-[color:var(--accent)]/75'
            )}
            onClick={() => onSelectStage(stage)}
            aria-pressed={isSelected}
            style={
              isSelected
                ? { backgroundColor: 'rgba(59, 130, 246, 0.22)' }
                : undefined
            }
          >
            {isSelected && (
              <span
                className="absolute right-3 top-3 block h-2 w-2 rounded-full"
                style={{ backgroundColor: 'var(--primary-foreground)', opacity: 0.85 }}
              />
            )}
            <span className="text-2xl">{info.emoji}</span>
            <div className="space-y-1">
              <div className="text-sm font-semibold tracking-tight">{info.name}</div>
              <div
                className={cn(
                  'text-xs leading-snug',
                  isSelected
                    ? 'text-[color:var(--primary-foreground)] opacity-80'
                    : 'text-muted-foreground'
                )}
              >
                {info.description}
              </div>
            </div>
          </button>
        );
      })}
    </div>
  );
}
