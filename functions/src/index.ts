import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp()

// noinspection JSUnusedGlobalSymbols
export const addScore = functions
    .region('europe-west1')
    .https.onRequest((req, res) => {
        if (typeof req.query.raspberryPiId !== "string") return res.sendStatus(400)
        const raspberryPiId = req.query.raspberryPiId as string

        if (typeof req.body.score !== "number") return res.sendStatus(400)
        const score = req.body.score as number

        save(raspberryPiId, {score: score}).catch(err => console.error(err))

        return res.sendStatus(202);
    });

const firestore = admin.firestore

const save = async (raspberryPiId: string, data: { score: number }) => {
    const tournamentAssignmentDoc = await firestore().doc(`tournamentAssignments/${raspberryPiId}`).get()
    if (!tournamentAssignmentDoc.exists) return console.info(`Raspberry pi (id=${raspberryPiId}) is not assigned to any tournament.`)

    const tournamentId = tournamentAssignmentDoc.data()?.tournamentId
    if (tournamentId === undefined) return console.error(`Malformed document ${raspberryPiId}: no field "tournamentId"`)

    const tournamentRef = firestore().doc(`tournaments/${tournamentId}/rooms/${raspberryPiId}`)
    const tournamentDoc = await tournamentRef.get()
    if (tournamentDoc.exists) {
        await tournamentRef.update(data)
        console.info(`Added score ${data.score}`)
    } else {
        await tournamentRef.create(data)
        console.info(`Created room with initial score ${data.score}`)
    }
}
