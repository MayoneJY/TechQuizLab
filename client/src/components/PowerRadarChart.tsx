import { Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, ResponsiveContainer, Legend } from 'recharts';

interface PowerRadarChartProps {
  techDepth: number;
  serviceImpact: number;
  scalability: number;
  collaboration: number;
  originality: number;
  opponentData?: {
    techDepth: number;
    serviceImpact: number;
    scalability: number;
    collaboration: number;
    originality: number;
  };
}

export function PowerRadarChart({ 
  techDepth, 
  serviceImpact, 
  scalability, 
  collaboration, 
  originality,
  opponentData 
}: PowerRadarChartProps) {
  const data = [
    {
      category: '기술력',
      나: techDepth,
      상대: opponentData?.techDepth,
    },
    {
      category: '서비스 영향',
      나: serviceImpact,
      상대: opponentData?.serviceImpact,
    },
    {
      category: '확장성',
      나: scalability,
      상대: opponentData?.scalability,
    },
    {
      category: '협업',
      나: collaboration,
      상대: opponentData?.collaboration,
    },
    {
      category: '주도성',
      나: originality,
      상대: opponentData?.originality,
    },
  ];

  return (
    <ResponsiveContainer width="100%" height={300}>
      <RadarChart data={data}>
        <PolarGrid stroke="#374151" />
        <PolarAngleAxis 
          dataKey="category" 
          tick={{ fill: '#9CA3AF', fontSize: 12 }}
        />
        <PolarRadiusAxis 
          angle={90} 
          domain={[0, 100]} 
          tick={{ fill: '#9CA3AF', fontSize: 10 }}
        />
        <Radar 
          name="나" 
          dataKey="나" 
          stroke="#3B82F6" 
          fill="#3B82F6" 
          fillOpacity={0.6} 
        />
        {opponentData && (
          <Radar 
            name="상대" 
            dataKey="상대" 
            stroke="#EF4444" 
            fill="#EF4444" 
            fillOpacity={0.6} 
          />
        )}
        <Legend />
      </RadarChart>
    </ResponsiveContainer>
  );
}
