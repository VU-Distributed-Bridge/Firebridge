import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp()
const firestore = admin.firestore

// noinspection JSUnusedGlobalSymbols
export const addScore = functions
    .region('europe-west1')
    .https.onRequest((req, res) => {
        if (typeof req.query.raspberryPiId !== "string") return res.sendStatus(400)
        const raspberryPiId = req.query.raspberryPiId as string

        if (typeof req.body.score !== "number") return res.sendStatus(400)
        const score = req.body.score as number

        firestore().collection("tournamentAssignments").doc(raspberryPiId).get()
            .then(doc => {
                if (!doc.exists) {
                    console.info(`Raspberry Pi with id ${raspberryPiId} is not assigned to any tournament.`);
                    return;
                }

                console.info("Document data:", doc.data());
                return doc.data()
            })
            .then(data => {
                if (data == undefined) {
                    console.error(`Malformed document ${raspberryPiId}: no field "tournamentId"`)
                    return;
                }

                const docRef = firestore().collection("tournaments").doc(data.tournamentId)
                    .collection("rooms").doc(raspberryPiId);
                docRef.get()
                    .then(doc => {
                        if (doc.exists) {
                            const data = {score: score}
                            docRef.update(data)
                                .then(() => console.info(`Added score ${data.score}`))
                        } else {
                            const data = {score: score}
                            docRef.create(data)
                                .then(() => console.info(`Created room with initial score ${data.score}`))
                        }
                    })
            })
            .catch(err => console.error(err));

        return res.sendStatus(202);
    });
