package com.github.frozensync.tournament

class ScorerService(
    private val tournamentService: TournamentService,
    private val directorId: String,
    private val tournamentId: String
) : ScorerGrpcKt.ScorerCoroutineImplBase() {

    override suspend fun sendScore(request: ScoreRequest): Empty {
        val result = Score(
            round = request.round,
            NS = request.ns,
            EW = request.ew,
            board = request.board,
            contr = request.contr,
            lead = request.lead,
            result = request.result
        )
        tournamentService.saveScore(result, directorId, tournamentId)
        return Empty.getDefaultInstance()
    }
}
