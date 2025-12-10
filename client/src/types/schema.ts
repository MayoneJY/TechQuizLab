export interface Achievement {
    id: number
    name: string
    description: string
    conditionType: string
    conditionValue: number
    rewardExp: number
    imgUrl?: string
}

export interface Battle {
    userId: number
    battleId: number
    stageId: number
    pfId: number
    totalDamage?: number
    status?: 'IN_PROGRESS' | 'COMPLETED' | 'GAVE_UP'
    createdAt?: string
}

export interface BattleBookmark {
    userId: number
    bookmarkId: number
    refBattleId: number
    refDetailId: number
    memo?: string
    createdAt?: string
}

export interface BattleDetail {
    userId: number
    battleId: number
    detailId: number
    questionText: string
    keywordTags?: string
    difficulty?: string
    userAnswer?: string
    aiFeedback?: string
    damage?: number
    createdAt?: string
}

export interface BookmarkPractice {
    userId: number
    bookmarkId: number
    practiceId: number
    userAnswer?: string
    aiFeedback?: string
    damage?: number
    createdAt?: string
}

export interface DailyLog {
    userId: number
    logDate: string
    isAttended?: boolean
    dailyExp?: number
}

export interface DailyMission {
    id: number
    content: string
    missionType: string
    targetValue: number
    rewardExp: number
}

export interface Friend {
    userId: number
    friendId: number
    createdAt?: string
}

export interface Portfolio {
    userId: number
    pfId: number
    title: string
    content?: string
    createdAt?: string
}

export interface RefreshToken {
    userId: number
    tokenId: number
    tokenValue: string
    expiresAt: string
    createdAt?: string
}

export interface Stage {
    stageId: number
    companyName: string
    title: string
    jobCategory?: string
    content?: string
    deadline?: string
}

export interface Topic {
    id: number
    name: string
}

export interface User {
    userId: number
    email: string
    password?: string
    nickname: string
    level?: number
    exp?: number
    role?: string
    enabled?: boolean
    createdAt?: string
    solvedCount?: number
    currentStreak?: number
    maxStreak?: number
    lastLoginAt?: string
    remainingLives?: number
    lastLivesResetAt?: string
}

export interface UserAchievement {
    id: number
    userId: number
    achievementId: number
    achievedAt: string
}

export interface UserDailyMission {
    id: number
    userId: number
    dailyMissionId: number
    progress: number
    isCompleted: boolean
    isClaimed: boolean
    assignedDate: string
}

export interface UserTopicLevel {
    id: number
    userId: number
    topicId: number
    level: number
    exp: number
}
