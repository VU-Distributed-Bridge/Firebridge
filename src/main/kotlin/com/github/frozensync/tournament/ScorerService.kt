package com.github.frozensync.tournament

class ScorerService(private val tournamentService: TournamentService) : ScorerGrpcKt.ScorerCoroutineImplBase() {

    override suspend fun sendScore(request: ScoreRequest): Empty {
        val result = Score(request.score)
        tournamentService.save(result)
        return Empty.getDefaultInstance()
    }
}
