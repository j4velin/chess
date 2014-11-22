/*
 * Copyright 2013 Thomas Hoffmann
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.j4velin.chess.util;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.ParticipantResult;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.HashMap;

import de.j4velin.chess.BuildConfig;
import de.j4velin.chess.R;
import de.j4velin.chess.game.Game;

/**
 * Class to manage the Google Play achievements
 */
/*
 * Copyright 2014 Thomas Hoffmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Achievements {

    /**
     * Check the conditions for not-yet-unlocked achievements and unlock them if
     * the condition is met and updates the leaderboard
     *
     * @param gc      the GamesClient
     * @param context the Context
     */
    public static void checkAchievements(final GoogleApiClient gc, final Context context) {
        if (gc.isConnected()) {
            Games.TurnBasedMultiplayer
                    .loadMatchesByStatus(gc, new int[]{TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE})
                    .setResultCallback(
                            new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
                                @Override
                                public void onResult(final TurnBasedMultiplayer.LoadMatchesResult result) {
                                    HashMap<Integer, Integer> wins =
                                            new HashMap<Integer, Integer>(4);
                                    wins.put(Game.MODE_2_PLAYER_2_SIDES, 0);
                                    wins.put(Game.MODE_2_PLAYER_4_SIDES, 0);
                                    wins.put(Game.MODE_4_PLAYER_NO_TEAMS, 0);
                                    wins.put(Game.MODE_4_PLAYER_TEAMS, 0);

                                    ParticipantResult pResult;
                                    for (TurnBasedMatch m : result.getMatches()
                                            .getCompletedMatches()) {
                                        pResult = m.getParticipant(m.getParticipantId(
                                                Games.Players.getCurrentPlayerId(gc))).getResult();
                                        if (pResult != null && pResult.getResult() ==
                                                ParticipantResult.MATCH_RESULT_WIN) {
                                            wins.put(m.getVariant(), wins.get(m.getVariant()) + 1);
                                        }
                                    }

                                    if (BuildConfig.DEBUG) {
                                        Logger.log("-- Wins --");
                                        for (Integer type : wins.keySet())
                                            Logger.log(type + ": " + wins.get(type));
                                    }

                                    if (wins.get(Game.MODE_2_PLAYER_2_SIDES) +
                                            wins.get(Game.MODE_2_PLAYER_4_SIDES) > 1) {
                                        Games.Achievements.unlock(gc, context.getString(
                                                R.string.achievement_checkmate_i));
                                    }

                                    if (wins.get(Game.MODE_2_PLAYER_2_SIDES) +
                                            wins.get(Game.MODE_2_PLAYER_4_SIDES) > 5) {
                                        Games.Achievements.unlock(gc, context.getString(
                                                R.string.achievement_checkmate_ii));
                                    }

                                    if (wins.get(Game.MODE_2_PLAYER_2_SIDES) +
                                            wins.get(Game.MODE_2_PLAYER_4_SIDES) > 10) {
                                        Games.Achievements.unlock(gc, context.getString(
                                                R.string.achievement_checkmate_iii));
                                    }

                                    if (wins.get(Game.MODE_4_PLAYER_TEAMS) > 1) {
                                        Games.Achievements.unlock(gc,
                                                context.getString(R.string.achievement_teamplay_i));
                                    }

                                    if (wins.get(Game.MODE_4_PLAYER_TEAMS) > 5) {
                                        Games.Achievements.unlock(gc, context.getString(
                                                R.string.achievement_teamplay_ii));
                                    }

                                    if (wins.get(Game.MODE_4_PLAYER_TEAMS) > 10) {
                                        Games.Achievements.unlock(gc, context.getString(
                                                R.string.achievement_teamplay_iii));
                                    }

                                }
                            });
        }
    }
}
