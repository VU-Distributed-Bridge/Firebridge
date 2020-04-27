'use strict';

const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();
const store = admin.firestore()

exports.helloWorld = functions
  .region('europe-west1')
  .https.onRequest((request, response) => {
    console.log(request.body);

    response.send("Hello from Firebase!");
  });
