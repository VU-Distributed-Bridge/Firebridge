package com.github.frozensync.tournament

class ScorerService(
    private val tournamentService: TournamentService,
    private val directorId: String,
    private val tournamentId: String
) : ScorerGrpcKt.ScorerCoroutineImplBase() {

    override suspend fun sendScore(request: ScoreRequest): Empty {
        val result = Score(request.score)
        tournamentService.saveScore(result, directorId, tournamentId)
        return Empty.getDefaultInstance()
    }
}
