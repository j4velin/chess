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
package de.j4velin.chess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;

import java.util.ArrayList;

import de.j4velin.chess.game.Game;
import de.j4velin.chess.game.Match;
import de.j4velin.chess.util.Logger;

public class Main extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnTurnBasedMatchUpdateReceivedListener {

    public final static int RC_SELECT_PLAYERS = 2;
    public final static int RC_MATCH_HISTORY = 3;
    private final static int RC_RESOLVE = 4;

    public static StartFragment startFragment;
    public static GameFragment gameFragment;

    private GoogleApiClient mGoogleApiClient;

    /**
     * Called when the activity is starting. Restores the activity state.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Create new fragment and transaction
            if (BuildConfig.DEBUG) Logger.log("Main.onCreate create new startFramgnet");
            startFragment = new StartFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content, startFragment,
                    startFragment.getClass().getSimpleName()).commit();
        }

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this, this, this);
        builder.addApi(Games.API, Games.GamesOptions.builder().build());
        builder.addScope(Games.SCOPE_GAMES);

        mGoogleApiClient = builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Logger.log("Main::onStart");
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (BuildConfig.DEBUG) Logger.log("Main::onStop");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Asks the user to update the game app
     */
    void updateApp() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Your version of the game has to be updated first to join this match!");
        b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName()))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
        b.create().show();
    }

    public GoogleApiClient getGC() {
        return mGoogleApiClient;
    }

    public void startGame(final String matchID) {
        if (BuildConfig.DEBUG) Logger.log("Main.startGame");
        gameFragment = new GameFragment();
        Bundle b = new Bundle();
        b.putString("matchID", matchID);
        gameFragment.setArguments(b);
        getFragmentManager().beginTransaction().replace(android.R.id.content, gameFragment,
                gameFragment.getClass().getSimpleName()).addToBackStack(null)
                .commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int request, int response, final Intent data) {
        super.onActivityResult(request, response, data);
        if (BuildConfig.DEBUG) Logger.log("Main onActivityResult");
        if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED &&
                !mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        } else if (response == Activity.RESULT_CANCELED) {
            // User cancelled.
            mGoogleApiClient.disconnect();
        } else if (response == Activity.RESULT_OK) {
            if (request == RC_SELECT_PLAYERS) {
                // get the invitee list
                final ArrayList<String> invitees =
                        data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // get auto-match criteria
                Bundle autoMatchCriteria;
                int minAutoMatchPlayers =
                        data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers =
                        data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig
                            .createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                TurnBasedMatchConfig tbmc =
                        TurnBasedMatchConfig.builder().addInvitedPlayers(invitees)
                                .setAutoMatchCriteria(autoMatchCriteria)
                                .setVariant(StartFragment.LAST_SELECTED_MATCH_MODE).build();

                // kick the match off
                Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                        new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                            @Override
                            public void onResult(final TurnBasedMultiplayer.InitiateMatchResult result) {
                                if (BuildConfig.DEBUG) Logger.log(
                                        "InitiateMatchResult onResult " + result.getStatus());
                                // Check if the status code is not success;
                                if (result.getStatus().getStatusCode() !=
                                        GamesStatusCodes.STATUS_OK) {
                                    return;
                                }
                                TurnBasedMatch match = result.getMatch();
                                if (match.getData() == null) {
                                    Game.newGame(new Match(match, match.getVariant()),
                                            mGoogleApiClient);
                                } else {
                                    if (!Game.load(match.getData(), match, mGoogleApiClient)) {
                                        updateApp();
                                        return;
                                    }
                                }
                                startGame(match.getMatchId());
                            }
                        });
            } else if (request == RC_RESOLVE) {
                // We're coming back from an activity that was launched to resolve a
                // connection problem. For example, the sign-in UI.
                if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
                    // Ready to try to connect again.
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onTurnBasedMatchReceived(final TurnBasedMatch match) {
        if (BuildConfig.DEBUG) Logger.log("Main onTurnBasedMatchReceived: " + match.getMatchId());
        if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN &&
                match.getStatus() == TurnBasedMatch.MATCH_STATUS_ACTIVE) {
            final Ringtone tone = RingtoneManager.getRingtone(this, RingtoneManager
                    .getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
            tone.setStreamType(AudioManager.STREAM_NOTIFICATION);
            tone.play();
        }
        if (startFragment != null && startFragment.isVisible()) {
            startFragment.loadMatches();
        }
        if (gameFragment != null && gameFragment.isVisible() &&
                match.getMatchId().equals(gameFragment.currentMatch)) {
            if (Game.load(match.getData(), match, mGoogleApiClient)) {
                gameFragment.update(match.getStatus() != TurnBasedMatch.MATCH_STATUS_ACTIVE &&
                        match.getStatus() != TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING);
            } else {
                updateApp();
            }
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(final String s) {
        if (BuildConfig.DEBUG) Logger.log("Main onTurnBasedMatchRemoved: " + s);
        if (startFragment != null && startFragment.isVisible()) {
            startFragment.loadMatches();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        } else {
            finish();
        }
    }

    public boolean optionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                break;
        }
        return true;
    }

    @Override
    public void onConnected(final Bundle bundle) {
        if (BuildConfig.DEBUG) Logger.log("Main.onConnected");
        TurnBasedMatch match;
        if (bundle != null &&
                (match = bundle.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH)) != null) {
            if (gameFragment == null || !gameFragment.isVisible()) {
                if (Game.load(match.getData(), match, mGoogleApiClient)) {
                    startGame(match.getMatchId());
                } else {
                    updateApp();
                }
            }
        } else {
            if (startFragment != null && startFragment.isVisible()) startFragment.loadMatches();
        }
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            // This problem can be fixed. So let's try to fix it.
            try {
                // launch appropriate UI flow (which might, for example, be the
                // sign-in flow)
                connectionResult.startResolutionForResult(this, RC_RESOLVE);
            } catch (IntentSender.SendIntentException e) {
                // Try connecting again
                mGoogleApiClient.connect();
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }
}
