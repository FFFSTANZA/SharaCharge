/**
 * Firebase Cloud Function to Update Charger Reliability Scores
 *
 * This function runs on a schedule (daily) and updates the reliability score
 * for all chargers based on community contributions.
 *
 * Deploy with:
 * firebase deploy --only functions:updateReliabilityScores
 */

const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Initialize Firebase Admin if not already initialized
if (admin.apps.length === 0) {
  admin.initializeApp();
}

const db = admin.firestore();

/**
 * Calculate reliability score for a single charger
 *
 * @param {string} chargerId - The charger ID
 * @param {Array} contributions - Array of contributions for this charger
 * @returns {Object} Reliability score object
 */
function calculateReliabilityScore(chargerId, contributions) {
  // 1. Count photos
  const photoContributions = contributions.filter(c => c.type === 'PHOTO');
  const photoCount = photoContributions.length;
  const photoScore = Math.min((photoCount / 5) * 20, 20);

  // 2. Count reviews and calculate average rating
  const reviewContributions = contributions.filter(c => c.type === 'REVIEW');
  const reviewCount = reviewContributions.length;
  const reviewScore = Math.min((reviewCount / 10) * 20, 20);

  // 3. Calculate average rating
  const ratings = reviewContributions
    .map(r => r.rating)
    .filter(rating => rating != null);
  const avgRating = ratings.length > 0
    ? ratings.reduce((sum, r) => sum + r, 0) / ratings.length
    : 0;
  const ratingScore = Math.min(avgRating * 4, 20);

  // 4. Count recent contributions (last 7 days)
  const sevenDaysAgo = Date.now() - (7 * 24 * 60 * 60 * 1000);
  const recentContributions = contributions.filter(c => c.timestamp >= sevenDaysAgo);
  const recentCount = recentContributions.length;
  const freshnessScore = Math.min((recentCount / 5) * 20, 20);

  // 5. Calculate average validation confidence (last 30 days)
  const thirtyDaysAgo = Date.now() - (30 * 24 * 60 * 60 * 1000);
  const recentValidatedContribs = contributions.filter(c => c.timestamp >= thirtyDaysAgo);

  const avgConfidence = recentValidatedContribs.length > 0
    ? recentValidatedContribs.reduce((sum, c) => sum + (c.confidenceScore || 0), 0) / recentValidatedContribs.length
    : 0;
  const validationScore = avgConfidence * 20;

  // Calculate total score
  const totalScore = photoScore + reviewScore + ratingScore + freshnessScore + validationScore;

  return {
    totalScore: parseFloat(totalScore.toFixed(2)),
    photoScore: parseFloat(photoScore.toFixed(2)),
    reviewScore: parseFloat(reviewScore.toFixed(2)),
    ratingScore: parseFloat(ratingScore.toFixed(2)),
    freshnessScore: parseFloat(freshnessScore.toFixed(2)),
    validationScore: parseFloat(validationScore.toFixed(2)),
    lastUpdated: admin.firestore.FieldValue.serverTimestamp()
  };
}

/**
 * Update reliability score for a single charger
 *
 * @param {string} chargerId - The charger ID
 */
async function updateChargerReliabilityScore(chargerId) {
  try {
    // Get all contributions for this charger
    const contributionsSnapshot = await db
      .collection('contributions')
      .where('chargerId', '==', chargerId)
      .get();

    const contributions = contributionsSnapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));

    // Calculate reliability score
    const reliabilityScore = calculateReliabilityScore(chargerId, contributions);

    // Update charger document
    const chargerRef = db.collection('power-sources').doc(chargerId);
    await chargerRef.update({
      reliability_score: reliabilityScore.totalScore,
      reliability_score_breakdown: {
        photo_score: reliabilityScore.photoScore,
        review_score: reliabilityScore.reviewScore,
        rating_score: reliabilityScore.ratingScore,
        freshness_score: reliabilityScore.freshnessScore,
        validation_score: reliabilityScore.validationScore
      },
      last_score_update: reliabilityScore.lastUpdated
    });

    console.log(`Updated reliability score for charger ${chargerId}: ${reliabilityScore.totalScore}`);
    return reliabilityScore;
  } catch (error) {
    console.error(`Error updating reliability score for charger ${chargerId}:`, error);
    throw error;
  }
}

/**
 * Scheduled function to update all charger reliability scores
 * Runs daily at 2:00 AM (cron: 0 2 * * *)
 */
exports.updateReliabilityScores = functions.pubsub
  .schedule('0 2 * * *')
  .timeZone('Asia/Kolkata') // Adjust to your timezone
  .onRun(async (context) => {
    try {
      console.log('Starting daily reliability score update...');

      // Get all chargers
      const chargersSnapshot = await db
        .collection('power-sources')
        .where('category', '==', 'EV_CHARGER')
        .get();

      console.log(`Found ${chargersSnapshot.size} chargers to update`);

      // Update each charger in batches to avoid timeout
      const batchSize = 50;
      const chargerIds = chargersSnapshot.docs.map(doc => doc.id);

      for (let i = 0; i < chargerIds.length; i += batchSize) {
        const batch = chargerIds.slice(i, i + batchSize);
        await Promise.all(
          batch.map(chargerId => updateChargerReliabilityScore(chargerId))
        );
        console.log(`Updated batch ${Math.floor(i / batchSize) + 1}/${Math.ceil(chargerIds.length / batchSize)}`);
      }

      console.log('Reliability score update completed successfully');
      return null;
    } catch (error) {
      console.error('Error in scheduled reliability score update:', error);
      return null;
    }
  });

/**
 * Triggered function to update reliability score when a contribution is created/updated
 * This provides real-time score updates
 */
exports.updateReliabilityScoreOnContribution = functions.firestore
  .document('contributions/{contributionId}')
  .onWrite(async (change, context) => {
    try {
      const contribution = change.after.exists ? change.after.data() : null;

      // If contribution was deleted or doesn't have a chargerId, skip
      if (!contribution || !contribution.chargerId) {
        console.log('Contribution deleted or missing chargerId, skipping score update');
        return null;
      }

      const chargerId = contribution.chargerId;
      console.log(`Updating reliability score for charger ${chargerId} due to contribution change`);

      await updateChargerReliabilityScore(chargerId);

      return null;
    } catch (error) {
      console.error('Error updating reliability score on contribution change:', error);
      return null;
    }
  });

/**
 * HTTP callable function to manually trigger reliability score update
 * Can be called from the app with: functions.httpsCallable('manualUpdateReliabilityScore')
 */
exports.manualUpdateReliabilityScore = functions.https.onCall(async (data, context) => {
  try {
    // Optionally require authentication
    if (!context.auth) {
      throw new functions.https.HttpsError(
        'unauthenticated',
        'The function must be called while authenticated.'
      );
    }

    const { chargerId } = data;

    if (!chargerId) {
      throw new functions.https.HttpsError(
        'invalid-argument',
        'The function requires a chargerId parameter.'
      );
    }

    console.log(`Manual reliability score update requested for charger ${chargerId}`);
    const reliabilityScore = await updateChargerReliabilityScore(chargerId);

    return {
      success: true,
      chargerId,
      reliabilityScore
    };
  } catch (error) {
    console.error('Error in manual reliability score update:', error);
    throw new functions.https.HttpsError('internal', error.message);
  }
});

/**
 * Helper function to get charger reliability score stats
 * Useful for analytics and monitoring
 */
exports.getReliabilityScoreStats = functions.https.onCall(async (data, context) => {
  try {
    // Get all chargers with reliability scores
    const chargersSnapshot = await db
      .collection('power-sources')
      .where('category', '==', 'EV_CHARGER')
      .get();

    const scores = chargersSnapshot.docs
      .map(doc => doc.data().reliability_score)
      .filter(score => score != null);

    if (scores.length === 0) {
      return {
        count: 0,
        average: 0,
        min: 0,
        max: 0,
        distribution: {}
      };
    }

    // Calculate statistics
    const sum = scores.reduce((a, b) => a + b, 0);
    const average = sum / scores.length;
    const min = Math.min(...scores);
    const max = Math.max(...scores);

    // Calculate distribution by rating (1-5 stars)
    const distribution = {
      '5_stars': scores.filter(s => s >= 90).length,
      '4_stars': scores.filter(s => s >= 75 && s < 90).length,
      '3_stars': scores.filter(s => s >= 50 && s < 75).length,
      '2_stars': scores.filter(s => s >= 25 && s < 50).length,
      '1_star': scores.filter(s => s > 0 && s < 25).length,
      '0_stars': scores.filter(s => s === 0).length
    };

    return {
      count: scores.length,
      average: parseFloat(average.toFixed(2)),
      min: parseFloat(min.toFixed(2)),
      max: parseFloat(max.toFixed(2)),
      distribution
    };
  } catch (error) {
    console.error('Error getting reliability score stats:', error);
    throw new functions.https.HttpsError('internal', error.message);
  }
});
