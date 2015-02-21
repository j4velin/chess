/*
 * Copyright 2015 Thomas Hoffmann
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
package de.j4velin.chess.game;

import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;

import java.util.ArrayList;

public class Match {

    TurnBasedMatch turnBasedMatch;

    public boolean isLocal;
    private int numPlayers;
    public int mode;
    public final String id;

    public Match(final TurnBasedMatch match, int mode) {
        this.turnBasedMatch = match;
        this.isLocal = false;
        this.mode = mode;
        this.id = match.getMatchId();
    }

    public Match(final String id, int mode, int numPlayers) {
        this.isLocal = true;
        this.mode = mode;
        this.id = id;
        this.numPlayers = numPlayers;
    }

    public int getStatus() {
        return turnBasedMatch.getStatus();
    }

    public ArrayList<Participant> getParticipants() {
        return turnBasedMatch.getParticipants();
    }

    public int getNumPlayers() {
        if (turnBasedMatch != null) {
            return turnBasedMatch.getParticipants().size() +
                    turnBasedMatch.getAvailableAutoMatchSlots();
        } else {
            return numPlayers;
        }
    }

    public String getParticipantId(final String playerId) {
        return turnBasedMatch.getParticipantId(playerId);
    }

}
